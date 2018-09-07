package com.semaifour.facesix.beacon;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.beacon.rest.BLENetworkDeviceRestController;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.ElasticService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.impl.qubercloud.DeviceUpdateEventHandler;
import com.semaifour.facesix.mqtt.DeviceEventPublisher;
import com.semaifour.facesix.mqtt.Payload;

public class ScannerMqttMessageHandler extends DeviceUpdateEventHandler {
	
	Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BeaconDeviceService _beaconDeviceService;
	
	private BeaconService _beaconService;
	
	@Autowired
	private CustomerService customerService;
		 
	NetworkDeviceService 	_networkDeviceService;
	
	@Autowired
	DeviceService deviceService;

	@Autowired
	private BLENetworkDeviceRestController bleRestController;

	@Override
	public boolean messageArrived(String topic, MqttMessage message) {
		return messageArrived(topic, message.toString());
	}
	
	@Override
	public boolean messageArrived(String topic, String message) {
		//LOG.debug("handling msqtt message at " + topic + " : " + message);
		BeaconDeviceService beaconDev = getBeaconDeviceService();
		BeaconService beaconSvc = beaconService();
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(message, new TypeReference<HashMap<String, Object>>(){});
			String op = String.valueOf(map.get("opcode"));
			
			LOG.info("Opcode===> " +op);
			LOG.info("MAP ===> " +map);
			
			if (StringUtils.equalsIgnoreCase(op, "add-beacon-tags")) {
				List<Object> items = (List<Object>) map.get("beacons");
				for(Object item : items) {
					Beacon beacon = mapper.convertValue(item, Beacon.class);
					beacon.setScannerUid(String.valueOf(map.get("scannerUid")));
					beacon.setCid(String.valueOf(map.get("cid")));
					if (beaconSvc != null) 
						beaconSvc.addScannedBeacon(beacon, (String) map.get("reqid"));
				}
			} else if (StringUtils.equalsIgnoreCase(op, "entry") || StringUtils.equalsIgnoreCase(op, "exit")) {
				if (beaconSvc != null) {
					beaconSvc.entryExit(op, map);
				}
			} else if (StringUtils.equalsIgnoreCase(op, "device_register")) {
				map.put("status", Device.STATUS.REGISTERED.name());
				return createBeaconDevice(map, false);
			}else if (StringUtils.equalsIgnoreCase(op, "device_update")) {
				return updateBeaconDevice(map);
			}else if (StringUtils.equalsIgnoreCase(op, "device_create")) {
				map.put("status", Device.STATUS.AUTOCONFIGURED.name());
				return createBeaconDevice(map, true);
			}else if (StringUtils.equals(op, "device_heartbeat")) {
				
				if (beaconDev != null) {
					beaconDev.updateDeviceHealth(map);
				}
				return true;
			} else if (StringUtils.equals(op, "ping_request")) {
				return pingBeaconDevice(map);
			} else if (StringUtils.equalsIgnoreCase(op, "peer_update")) {
				return peer_update_BeaconDevice(map);
			} else if (StringUtils.equalsIgnoreCase(op, "device-started")) {
				return update_ip(map);
			} else if (StringUtils.equalsIgnoreCase(op, "UPGRADE")) {
				return binarySetting_upgrade(map);
			} else if (StringUtils.equalsIgnoreCase(op, "crash_dump_alert")) {
				return updateCrashDump(map);
			}
		} catch (Exception e) {
			LOG.error("Failed to process messageArrvied at " + topic + " : " + message, e);
			return false;
		}
		return false;
	}

	private boolean updateCrashDump(Map<String, Object> map) {
		
		try {
			
			String uid 				=  (String)map.get("uid");
			int crash_timestamp 	=  (int)map.get("timestamp");
			String  daemon_info		=  (String)map.get("victim");
			String  version			=  (String)map.get("version");
			int  upload_status	    =  (int)map.get("upload_status");
			String strUploadStatus 	= String.valueOf(upload_status);
			
			if (uid != null && !uid.isEmpty()) {
				
				LOG.info("opcode " 			+map.get("opcode") );
				LOG.info("UID " 			+uid );
				LOG.info("crash_timestamp " +crash_timestamp );
				LOG.info("daemon_info " 	+daemon_info );
				LOG.info("version " 		+version );
				LOG.info("upload_status " 	+upload_status);
				
				BeaconDevice beaconDevice =  null;
				Device device  			  = null;
				String cid   			  = null;
				
				String uuid 	= uid.replaceAll("[^a-zA-Z0-9]", "");			
				String fileName = daemon_info+"_"+uuid+"_"+crash_timestamp+"_"+version;
				
				LOG.info("Crash Dump FileName " +fileName);
				
				beaconDevice = getBeaconDeviceService().findOneByUid(uid);
				if (beaconDevice != null && beaconDevice.getUid().equalsIgnoreCase(uid)) {
					beaconDevice.setDevCrashTimestamp(String.valueOf(crash_timestamp));
					beaconDevice.setDevCrashdumpFileName(fileName);
					beaconDevice.setDevCrashDumpUploadStatus(strUploadStatus);
					beaconDevice.setModifiedOn(new Date());
					beaconDevice.setModifiedBy("cloud");
					getBeaconDeviceService().save(beaconDevice,false);
					cid = beaconDevice.getCid() == null ? "" : beaconDevice.getCid();
				}

				device  = getDeviceService().findOneByUid(uid);
				if (device != null  && device.getUid().equalsIgnoreCase(uid)) {
					device.setDevCrashTimestamp(String.valueOf(crash_timestamp));
					device.setDevCrashdumpFileName(fileName);
					device.setDevCrashDumpUploadStatus(strUploadStatus);
					device.setModifiedOn(new Date());
					device.setModifiedBy("cloud");
					getDeviceService().save(device,false);
					cid = device.getCid() == null ? "" : device.getCid();
				}
				map.put("filename", fileName);
				map.put("cid", cid);
				getBLERestController().deviceDupmDetails(map);
				
				return true;
			}
			
			
		} catch(Exception e) {
			LOG.error("while CrashDump update error " +e);
		}
		return false;
	}

	private boolean binarySetting_upgrade(Map<String, Object> map) throws Exception {
		try {
			
			String uid 		=   (String)map.get("uid");
			String reson 	=  (String)map.get("reason");
			
			LOG.info("binarySetting_upgrade  uid " +uid);
			LOG.info("binarySetting_upgrade  reson " +reson);
			if (uid != null) {
				String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
				NetworkDevice nd = getNetworkDeviceService().findOneByUuid(uuid);
				if (nd != null) {
					nd.setModifiedOn(new Date(System.currentTimeMillis()));
					nd.setBinaryreason(reson);
					getNetworkDeviceService().save(nd, false);
				}
			}
			return true;
			
		} catch (Exception e) {
			LOG.warn("Error "+e.getStackTrace());
		}
		return false;
	}
	private boolean update_ip(Map<String, Object> map) {
		try {
			String uid = (String) map.get("uid");
			String ver = (String) map.get("version");
			String buildtime = (String) map.get("buildtime");
			String ip = "0.0.0.0";
			String tunnelip = "0.0.0.0";
			
			if (map.containsKey("ip")) {
				ip = (String) map.get("ip");
			}
			if (map.containsKey("tunnelip")) {
				tunnelip = (String) map.get("tunnelip");
			}

			//LOG.info("uid  " + uid);
			//LOG.info("ip  " + ip);
			String devid = uid.replaceAll("[^a-zA-Z0-9]", "");
			BeaconDevice beaconDevice = getBeaconDeviceService().findOneByUid(uid);
			
			if (beaconDevice != null) {
				String type = beaconDevice.getType();
				if (type !=null && type.equalsIgnoreCase("server")) {
					
					String cid = beaconDevice.getCid();
					Customer acc = getCustomerService().findById(cid);
					
					if (acc !=null && (ip != null && !ip.isEmpty())) {
							acc.setBleserverip(ip);
							acc.setModifiedBy("cloud");
							acc.setModifiedOn(new Date(System.currentTimeMillis()));
							getCustomerService().save(acc);
							
						String id = "";
						
						if (beaconDevice.getSid() != null) {
							id = beaconDevice.getSid();
						} else if (beaconDevice.getCid() != null) {
							id = beaconDevice.getCid();
						}
						getBeaconDeviceService().universal_ServerIP_MQTT(id, ip);
					}
					
				}
				
				if (tunnelip != null && !tunnelip.isEmpty()) {
					beaconDevice.setTunnelIp(tunnelip);
				}
				if (ip != null && !ip.isEmpty()) {
					beaconDevice.setIp(ip);
				}
				if (ver != null) {
					beaconDevice.setVersion(ver);
				} else {
					beaconDevice.setVersion("unknown");
				}

				if (buildtime != null) {
					beaconDevice.setBuild(buildtime);
				} else {
					beaconDevice.setBuild("unknown");
				}

				if (beaconDevice != null && !beaconDevice.getState().equals("active")) {
					beaconDevice.setState("active");
				}					
				beaconDevice.setModifiedBy("cloud");
				beaconDevice.setModifiedOn(new Date(System.currentTimeMillis()));
				beaconDevice = getBeaconDeviceService().save(beaconDevice, false);

				NetworkDevice rcvDev = getNetworkDeviceService().findOneByUuid(devid);
				if (rcvDev != null) {
					if (!rcvDev.getStatus().equals("active")) {
						rcvDev.setStatus("active");
						getNetworkDeviceService().save(rcvDev);
					}
				}			
			}
		} catch (Exception e) {
			LOG.info("while beacon device ip update error ", e);
		}
		return true;
	}

	public boolean createBeaconDevice(Map<String, Object> map, boolean notify){
		
		try {

			String uid 		= (String)map.get("uid");
			String by 		= (String)map.get("by");
			String fstype 	= (String) map.get("fstype");
			String status   = (String)map.get("status");
			String alias 	= (String)map.get("alias");
			String template = (String)map.get("template");
			String bleType  = (String)map.get("bleType");
			
			//LOG.info("Becon Device Creating/Registering Uid :" + uid);
	
			Date now = new Date();
			BeaconDevice beaconDevice = getBeaconDeviceService().findOneByUid(uid);
			if (beaconDevice == null) {
				beaconDevice = new BeaconDevice();
				beaconDevice.setUid(uid);
				beaconDevice.setName(uid);
				beaconDevice.setFstype(fstype);
				beaconDevice.setCreatedBy(by);
				beaconDevice.setCreatedOn(now);
				beaconDevice.setStatus(status);
				beaconDevice.setIp("0.0.0.0");
			}
			
			if (alias != null) {
				beaconDevice.setName(alias);
			}
			if(bleType !=null) {
				beaconDevice.setType(bleType);
			}
			beaconDevice.setModifiedBy(by);
			beaconDevice.setModifiedOn(now);
			beaconDevice.setState("inactive");
			
			if (by.equals("Web") && template !=null && !template.isEmpty()) {
				//LOG.info("=============BY Customer configured device========");
				beaconDevice.setTemplate(template);
				beaconDevice.setConf(template);
			} else {
				// by = "device";
				//LOG.info("==========BY Device Registerd to cloud============");
			}
			
			getBeaconDeviceService().save(beaconDevice,notify);

		} catch (Exception e) {
			LOG.info("while beacon device save error ",e);
		}
		return true;
	}

	private boolean updateBeaconDevice(Map<String, Object> map) throws Exception {
		
		String uid 		= (String)map.get("uid");
		String en 		= (String)map.get("_entity");
		String by		=(String)map.get("by");
		
		BeaconDevice dv = getBeaconDeviceService().findOneByUid(uid);
		if (dv != null) {
			if (en != null) {
			}
			dv.setModifiedBy(by);
			dv.setModifiedOn(new Date(System.currentTimeMillis()));
			getBeaconDeviceService().save(dv, false);
		}	
		return true;
	}
	
	
	private boolean pingBeaconDevice(Map<String, Object> map) { //publish
		String uid = (String)map.get("uid");
		try {
			String msg = (String)map.get("message");
			Payload payload = new Payload("ping_response", uid, uid, msg);
			getDeviceEventPublisher().publish(payload.toJSONString(), uid);
		} catch (Exception e) {
			LOG.warn("ping_response for ping_reqquest from [{}] failed", uid, e);
		}
		return true;
	}
	
	private boolean peer_update_BeaconDevice(Map<String, Object> map) throws Exception {
		
		String uid  	= (String)map.get("uid");
		String peers   	= (String)map.get("peer_count");
		String by   	= (String)map.get("peer_count");
		String state	= "";
		//LOG.info("Peer :" + peers);
		
		BeaconDevice dv = getBeaconDeviceService().findOneByUid(uid);
		 		
		if (dv != null) {
			int peer_count = Integer.parseInt(peers);
			if (peer_count != 0) {
				state = "active";
			} else {
				state = "idle";
			}
			
			//LOG.info ("PEER UID" + uid);
			//LOG.info ("PeerCount" + peers);
			
			try {
				dv.setState(state);
				dv.setModifiedBy(by);
				dv.setModifiedOn(new Date(System.currentTimeMillis()));
				getBeaconDeviceService().save(dv, false);
				
			}catch (NumberFormatException nfe) {
				LOG.info("Error in Format" + System.currentTimeMillis());
			}
						
			return true;
		}
						
		return false;
	}

	@Override
	public String getName() {
		return "ScannerMqttMessageHandler";
	}
	
	
	
	public BeaconService beaconService() {
		
		try {
			if (_beaconService == null) {
				_beaconService = Application.context.getBean(BeaconService.class);
			}			
		} catch (Exception e) {
			
		}

		try {
			if (_beaconService == null) {
				LOG.warn("Unable to load BeaconService, please check");
			}			
		} catch (Exception e) {
			
		}

		return _beaconService;
		
	}
	
	private BeaconDeviceService getBeaconDeviceService() {
		
		try {
			if (_beaconDeviceService == null) {
				_beaconDeviceService = Application.context.getBean(BeaconDeviceService.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if (_beaconDeviceService == null) {
				LOG.info("Unable to load BeaconDeviceService, please check");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _beaconDeviceService;
	}
	
	private DeviceEventPublisher _mqttPublisher;
	private DeviceEventPublisher getDeviceEventPublisher() {
		
		try {
			if (_mqttPublisher == null) {
				_mqttPublisher = Application.context.getBean(DeviceEventPublisher.class);
			}			
		} catch (Exception e) {
			
		}

		return _mqttPublisher;
	}
	
	private CustomerService getCustomerService() {
		
		try {
			if (customerService == null) {
				customerService = Application.context.getBean(CustomerService.class);
			}			
		} catch (Exception e) {
			
		}

		return customerService;
	}
	
	private NetworkDeviceService getNetworkDeviceService() {
		
		try {
			if (_networkDeviceService == null) {
				_networkDeviceService = Application.context.getBean(NetworkDeviceService.class);
			}
		} catch (Exception e) {
			
		}
		return _networkDeviceService;
	}
	
	private DeviceService getDeviceService() {
		
		try {
			if (deviceService == null) {
				deviceService = Application.context.getBean(DeviceService.class);
			}			
		} catch (Exception e) {
			
		}

		return deviceService;
	}
	
	private BLENetworkDeviceRestController getBLERestController() {
		
		try {
			if (bleRestController == null) {
				bleRestController = Application.context.getBean(BLENetworkDeviceRestController.class);
			}			
		} catch (Exception e) {
			
		}

		return bleRestController;
	}

}
