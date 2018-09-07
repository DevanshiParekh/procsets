/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.semaifour.facesix.aws.pubsub;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * This class extends {@link AWSIotTopic} to receive messages from a subscribed
 * topic.
 */


public class TestTopicListener extends AWSIotTopic {

	Logger LOG=LoggerFactory.getLogger(TestTopicListener.class.getName());
	
    UserAccountService userAccount;

    DeviceService 	deviceService;
    
	private String userId;
	
    public TestTopicListener(String topic, AWSIotQos qos,String id) {
        super(topic, qos);
        userId = id;
    }

    @SuppressWarnings("unchecked")
	@Override
    public void onMessage(AWSIotMessage message) {
    	
    	try {

    		LOG.info("***** AWS CALBACK ***** ");
        	LOG.info("***** AWS CALBACK ***** " +message.getStringPayload());
        	LOG.info("***** Current  user id ***** " +userId);
        	
        	UserAccount acc = getUserAccountService().findById(userId);
    		
        	LOG.info("***** acc ***** " +acc);
    		
			if (acc != null) {
				
				LOG.info("***** user id ***** " + userId + " name  " + acc.getName());
				
				net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(message.getStringPayload());
				
				LOG.info("***** json ***** " +json);
				
				JSONObject state 		= (JSONObject)json.get("state");
				JSONObject desired  	= (JSONObject)state.get("desired");
				
				LOG.info("***** state***** " + state);
				LOG.info("***** desired***** " + desired);	
				
				alexa_update (desired, acc);
			}

		} catch (Exception e) {
			LOG.info("Alexa Message callback error " + e);
		}

	}
    
	private UserAccountService getUserAccountService() {
		try {
			if (userAccount == null) {
				userAccount = Application.context.getBean(UserAccountService.class);
			}
		} catch (Exception e) {}

		return userAccount;
	}
	
	private DeviceService getDeviceService() {
		try {
			if (deviceService == null) {
				deviceService = Application.context.getBean(DeviceService.class);
			}
		} catch (Exception e) {}
		return deviceService;
	}
	
	private void alexa_update (JSONObject desired, UserAccount acc) {
		
		String 		navigate 	= (String)desired.get("navigate");
		String 		ssid    	= (String)desired.get("ssid");
		String 		scan    	= (String)desired.get("scan");
		String 		upgrade    	= (String)desired.get("upgrade");
		
		if (ssid != null)
			acc.setAlexassid(ssid);
		
		if (navigate != null) 
			acc.setAlexapage(navigate);	
				
		acc.setModifiedBy("cloud");
		acc.setModifiedOn(new Date(System.currentTimeMillis()));
		acc = userAccount.saveContact(acc);
		
		String cid = acc.getCustomerId();
		
		if (ssid != null) {
			alexa_ssid_notify (ssid, cid);
		}
		
	}
	
	private void alexa_ssid_notify (String ssid, String cid) {
		
		if (ssid != null) {
			
			LOG.info("***** CONFIG SSIS ***** " + ssid);
			
			List<Device> device = null;
			device = getDeviceService().findByCid(cid);
			
			if (device != null && device.size() > 0) {
				
				Device dv = device.get(0);
				String conf = dv.getTemplate();
				
				JSONObject template	= JSONObject.fromObject(conf);
				JSONObject tmp = new JSONObject();
				
				LOG.info(" uid " + dv.getUid());
				LOG.info(" template " + template.toString());
				
				JSONArray interfaces2g = null;
				JSONArray interfaces5g = null;
				
				JSONArray radio2g = null;
				JSONArray radio5g = null;
				
				if (template.get("interfaces2g") != null) {
					interfaces2g = template.getJSONArray("interfaces2g");
					//interfaces2g.getJSONObject(0).remove("ssid");
				}
				if (template.get("interfaces5g") != null) {
					interfaces5g = template.getJSONArray("interfaces5g");
					//interfaces5g.getJSONObject(0).remove("ssid");
				}
				
				for (int i = 0; i < interfaces2g.size(); i++) {
					interfaces2g.getJSONObject(i).remove("ssid");
				}
				
				if (interfaces5g != null) {
					for (int i = 0; i < interfaces5g.size(); i++) {
						interfaces5g.getJSONObject(i).remove("ssid");
					}
				}
				
				LOG.info(" interfaces2g size " +interfaces2g.size());
				LOG.info(" interfaces2g " +interfaces2g.toString());
				LOG.info(" interfaces2g Size " +interfaces2g.size());

				String appendStr = "mesh";
				
				if (interfaces2g != null) {
					for (int i = 0; i < interfaces2g.size(); i++) {
						if (i == 0) {
							interfaces2g.getJSONObject(i).put("ssid", ssid);
						} else {
							interfaces2g.getJSONObject(i).put("ssid", ssid + "-" + appendStr);
						}
						
						LOG.info(" interfaces2G  new SSID  " + ssid + " interface count " +i);
					}
				}
				
				if (interfaces5g != null) {
					for (int i = 0; i < interfaces5g.size(); i++) {
						if (i == 0) {
							interfaces5g.getJSONObject(i).put("ssid", ssid);
						}else {
							interfaces5g.getJSONObject(i).put("ssid", ssid + "-" + appendStr);
						}
						
						LOG.info(" interfaces5G  new SSID  " + ssid + " interface count " +i);
					}
				}
				
				if (template.get("radio2g") != null) {
					radio2g = template.getJSONArray("radio2g");
				}
				if (template.get("radio5g") != null) {
					radio5g = template.getJSONArray("radio5g");
				}

				LOG.info(" after interfaces2g " + interfaces2g);
				LOG.info(" after interfaces5g " + interfaces5g);

				if (interfaces2g != null) {
					tmp.put("radio2g", radio2g);
					tmp.put("interfaces2g", interfaces2g);
				}
				if (interfaces5g != null) {
					tmp.put("radio5g", radio5g);
					tmp.put("interfaces5g", interfaces5g);
				}
				
				String config = tmp.toString();
				dv.setConf(config);
				dv.setTemplate(config);
				dv.setModifiedOn(new Date(System.currentTimeMillis()));
				dv.setModifiedBy("cloud");
				getDeviceService().saveAndSendMqtt(dv, true);
				
				LOG.info(" DONE config  " +config.toString());
			} else {
				LOG.info(" Device NOT FOUND  ");
			}

		}		
	}
}