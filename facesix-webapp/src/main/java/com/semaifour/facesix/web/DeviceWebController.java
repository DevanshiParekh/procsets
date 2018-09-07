package com.semaifour.facesix.web;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.rest.DeviceRestController;
import com.semaifour.facesix.rest.NetworkConfRestController;
import com.semaifour.facesix.spring.SpringComponentUtils;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.DeviceHelper;
import com.semaifour.facesix.util.SessionUtil;

/**
 * 
 * Configuration Controller for the webapp
 * 
 * @author mjs
 *
 */


@Controller
@RequestMapping("/web/device")
public class DeviceWebController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(DeviceWebController.class.getName());
			
	@Autowired
	DeviceService service;
	
	@Autowired
	DeviceRestController deviceRestController;
	
	@Autowired
	NetworkDeviceService networkDeviceService;
	
	@Autowired
	NetworkConfRestController networkConfRestController;

	@Autowired
	CustomerUtils CustomerUtils;
	
	
	@RequestMapping("/list")
	public String list(Map<String, Object> model) {
		try {
			model.put("time", new Date());
			Iterable<Device> devices = service.findByQuery("-status:REGISTERED", "modifiedOn", 0, 100);
			model.put("devices", devices);
			model.put("configuration", TAB_HIGHLIGHTER);
			
	/*		GraylogRestClient graylogRestClient = new GraylogRestClient(_CCC.graylog.getRestUrl(),
																		_CCC.graylog.getPrincipal(),
																		_CCC.graylog.getSecret());
			ResponseEntity<SourcesList> response = graylogRestClient.invoke("/sources", null, SourcesList.class);
			List<Source> sources = new ArrayList<Source>();
			if (response != null) {
				for(Map.Entry<String, Long> item: response.getBody().sources().entrySet()) {
					if(service.findOneByUid(item.getKey()) == null) {
						sources.add(new Source(item.getKey(), item.getValue()));
					}
				}
			}
			model.put("sources", new PageImpl<Source>(sources)); */
			
		
	//		
	//		Aggregations aggs = esqlController.aggTerm("deviceuid", 
	//												   "deviceuid", 
	//												   "+@timestamp:>now-30d", 
	//												   "qubercomm_*", null, 100, 1);
	//		
	//		Terms terms = aggs.get("deviceuid");
	//		
	//		for (Terms.Bucket bucket : terms.getBuckets()) {
	//            LOG.info("Key: " + bucket.getKey() + " Value: "+ bucket.getDocCount());
	//        }
			
			
	//		List<Map<String, Object>> result = esqlRestController.search("+doctype:newdevice", "qubercomm_*", null, 100, 0);
	
			//String query = "index=qubercomm_*,query=doctype:newdevice ,size=100,page=0,sort=deviceuid,timestamp|bucket(deviceuid,deviceuid,count=true&none);value(devicetype,template,none);count(deviceid,count,none)|table";
			//List<Map<String, Object>> result = fsqlRestController.query(query);
			//for(Map<String, Object> map : result) {
			//	LOG.info(map.toString());
			//}
			
			devices = service.findByQuery("+status:REGISTERED", "modifiedOn", 0, 100);		
		
			
			model.put("newdevices", devices);
		} catch (Exception e) {
			LOG.warn("Error fetching devices", e);
		}

		return "device-list";
	}
	
	@RequestMapping("/copy")
	public String open(Map<String, Object> model,
					   @RequestParam(value = "id") String id) {
		model.put("time", new Date());
		Device device = null;
		if (id != null) {
			device = service.findById(id);
			if (device == null) {
				model.put("message", Message.newError("Device not found for copy, please enter new device details"));
			} else {
				Device tmp = device;
				device = new Device();
				device.setConf(tmp.getConf());
				device.setUid("Copy of " + tmp.getUid());
				device.setName("Copy of " + tmp.getName());
				model.put("message", Message.newInfo("Please correct copied details and save to persist."));
			}
		} else {
			model.put("message", Message.newError("No device to copy, please enter new device details"));
		}
		
		model.put("device", device);
		model.put("configuration", TAB_HIGHLIGHTER);

		return "device-edit";
	}
	
	@RequestMapping("/configure")
	public String configure(Map<String, Object> model, 
							@RequestParam(value = "uid", required = false) String uid, 
							@RequestParam(value = "template", required = false) String template) {
		model.put("time", new Date());
		Device device = null;
		if (uid != null) {
			device = service.findOneByUid(uid);
		}
		
		if (device != null) {
			//model.put("disabled", "disabled");
			model.put("message", Message.newInfo("Please update existing device config and press submit button to save"));
		}  else {
			device = new Device();
			device.setUid(uid);
			device.setName(uid);
			String tconf = _CCC.messages.getMessage("facesix.device.template." + template);
			if (tconf == null) {
				tconf = _CCC.messages.getMessage("facesix.device.template.default");
			}
			device.setConf(tconf);
			model.put("message", Message.newInfo("Please configure this new device and press submit button to save"));
		}
		
		model.put("device", device);
		model.put("configuration", TAB_HIGHLIGHTER);

		return "device-edit";
	}
	
	
	@RequestMapping("/open")
	public String open(Map<String, Object> model,
					   @RequestParam(value = "id", required = false) String id,
					   @RequestParam(value = "uid", required = false) String uid,
					   @RequestParam(value = "fstype", required = false, defaultValue="default") String fstype,
					   @RequestParam(value = "name", required = false) String name) {
		model.put("time", new Date());
		Device device = null;
		if (id != null) {
			device = service.findById(id);
		} else if (uid != null) {
			device = service.findOneByUid(uid);
			//fetch config by id
		} else if (name != null) {
			//fetch config by namne
			device = service.findOneByName(name);
		}
		
		if (device != null) {
			//model.put("disabled", "disabled");
			model.put("message", Message.newInfo("Please update existing device config correctly"));
		} else {
			device = new Device(); //dummy
			device.setFstype(fstype);
			String template = SpringComponentUtils.getApplicationMessages().getMessage("facesix.device.template.default");
			if (template != null) device.setConf(template);
			model.put("message", Message.newInfo("Please enter new device details correctly"));
		}
		
		model.put("device", device);
		model.put("configuration", TAB_HIGHLIGHTER);

		return "device-edit";
	}
	
	
	@RequestMapping("/delete")
	public String delete(Map<String, Object> model,
					  @RequestParam(value = "id", required = true) String id, 
					  @RequestParam(value = "uid", required = false) String uid) {
		Device device = service.findById(id);
		if (device == null && uid != null) {
			device = service.findOneByUid(uid);
			device.setId(id);
			device = service.save(device, false);
		}
		service.delete(device);
		device.setId(null);
		model.put("device", device);
		model.put("message", Message.newInfo("Device deleted successfully :" + device.getUid()));
		model.put("configuration", TAB_HIGHLIGHTER);

		return list(model);
	}
					  
	@RequestMapping("/save")
	public String save(Map<String, Object> model,
					  @RequestParam(value = "id", required = false) String id,
					  @RequestParam(value = "uid", required = false) String uid,
					  @RequestParam(value = "name", required = false) String name,
					  @RequestParam(value = "fstype", required = false) String fstype,
					  @RequestParam(value = "conf", required = false) String conf, HttpServletRequest request, HttpServletResponse response) {
		model.put("time", new Date());
		Device device = null;
		Date dt = new Date();

		boolean shouldSave = true;
		if (id == null) {
			uid = StringUtils.trimWhitespace(uid);
			name = StringUtils.trimWhitespace(name);
			conf = StringUtils.trimWhitespace(conf);
		
			device = new Device();
			device.setFstype(fstype);
			device.setUid(uid);
			device.setName(name);
			device.setConf(conf);
			device.setIp("0.0.0.0");
			device.setCreatedOn(dt);
			device.setModifiedOn(dt);
			device.setVer(1);
			device.setCreatedBy(getCurrentUser(request, response));
			device.setModifiedBy(getCurrentUser(request, response));
			

			if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(name)) {
				model.put("message", Message.newError("UID or Name can not be blank."));
				shouldSave = false;
			} else if (service.exists(uid, name)) {
				model.put("message", Message.newError("Device with UID or Name already exists."));
				shouldSave = false;
			}
		} else {
			//it's existing
			device = service.findById(id);
			if (device == null && uid != null) {
				device = service.findOneByUid(uid);
				if (device != null) {
					device.setId(id);	
				}
			}
			
			if (device == null) {
				model.put("message", Message.newFailure("Device not found with ID :" + id));
				shouldSave = false;
			} else {
				//check the MAC/device id not overwritten
				
				conf = StringUtils.trimWhitespace(conf);
				device.setConf(conf);
				device.setModifiedBy(getCurrentUser(request, response));
				device.setModifiedOn(dt);
				device.setVer(device.getVer() + 1);
				if (!StringUtils.isEmpty(name)) device.setName(name);
			}
		}
		
		
		if (shouldSave) {
			device.setStatus(Device.STATUS.CONFIGURED.name());
			device = service.save(device);
			model.put("disabled", "disabled");
			model.put("message", Message.newSuccess("Device saved successfully."));
		}
		model.put("device", device);
		model.put("configuration", TAB_HIGHLIGHTER);
		return "device-edit";
	}
	
	@RequestMapping("/topology")
	public String topology(Map<String, Object> model,
					  @RequestParam(value = "id", required = true) String id) {
		
		Device device = service.findById(id);
		try {
			model.put("device", device);
			model.put("configuration", TAB_HIGHLIGHTER);
			model.put("tree", DeviceHelper.toJSON4D3Network(device));

		} catch (Exception e) {
			model.put("message", Message.newError("Failed to open device topology. Check internal error."));
			LOG.warn("Exception parsing device :" + id, e);
		}
		return "device-topology";
	}

	@RequestMapping("/custconfig")
	public String custconfig(Map<String, Object> model,
							@RequestParam(value = "uid", required = false) String uid,
							@RequestParam(value = "sid", required = false) String sid,
							@RequestParam(value = "spid", required = false) String spid,
							@RequestParam(value = "cid", required = true) String cid,
							@RequestParam(value = "policy", required = false) String policy,
							@RequestParam(value = "param", required = false) String param,
							HttpServletRequest request, HttpServletResponse response) {

		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			try {
				//LOG.info("custconfign GET UID " + uid);
				//LOG.info("CUST CONFIG GET CID " + cid);
				//LOG.info("CUST CONFIG  GET SID " + sid);
				//LOG.info("CUST CONFIG GET SPID " + spid);
				//LOG.info("custconfign GET POLICY " + policy);
				
				super.prepare(model, request, response);
				Device device = null;
				
				if (uid != null) {
					if (uid.trim().equals("?")) {
						uid = null;
					} else {
						model.put("uid", uid);
						device = service.findOneByUid(uid);
					}
				}
				if (device != null) {
					model.put("message", Message.newInfo("Please update existing device config and press submit button to save"));
					model.put("hidden_value", false);
				} else {
					device = new Device();
					device.setUid(uid);
					device.setName(uid);
					device.setKeepAliveInterval("30");
					model.put("message", Message.newInfo("Please configure this new device and press submit button to save"));
					model.put("hidden_value", true);
				}
				if (policy != null) {
					if (policy.equals("1")) {
						model.put("policy", policy);
					}
				}
				
				if (cid == null || cid.isEmpty()) {
					cid = SessionUtil.getCurrentCustomer(request.getSession());
				}

				if (!CustomerUtils.isRetail(cid)) {
					model.put("sid", sid);
					model.put("spid", spid);
				}

				model.put("cid", cid);
				model.put("device", device);
				model.put("configuration", TAB_HIGHLIGHTER);
				
				param = param == null ? "FloorConfig" : "DeviceConfig";
				model.put("param", param);
				
				model.put("GatewayFinder", 	CustomerUtils.GatewayFinder(cid));
				model.put("GeoFinder", 		CustomerUtils.GeoFinder(cid));
				model.put("Gateway", 		CustomerUtils.Gateway(cid));
				model.put("GeoLocation", 	CustomerUtils.GeoLocation(cid));
				model.put("Heatmap", 		CustomerUtils.Heatmap(cid));
				model.put("Retail", 	    CustomerUtils.isRetail(cid));

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			page = _CCC.pages.getPage("facesix.custconfig", "custconfig");
		}
		
		return page;
	}
	
}