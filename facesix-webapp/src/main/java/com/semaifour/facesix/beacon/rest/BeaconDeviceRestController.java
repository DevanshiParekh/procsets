package com.semaifour.facesix.beacon.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.beacon.finder.geo.GeoFinderLayoutData;
import com.semaifour.facesix.beacon.finder.geo.GeoFinderLayoutDataService;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.elasticsearch.ElasticService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.mqtt.DeviceEventPublisher;
import com.semaifour.facesix.rest.FSqlRestController;
import com.semaifour.facesix.spring.SpringComponentUtils;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/rest/beacon/device")
public class BeaconDeviceRestController extends WebController{

	@Autowired
	BeaconDeviceService beaconDeviceService;
	
	@Autowired
	FSqlRestController fsqlRestController;
	
	@Autowired
	NetworkDeviceService networkDeviceService;
	
	@Autowired
	DeviceEventPublisher deviceEventMqttPub;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	private GeoFinderLayoutDataService geoService;
	
	@Autowired
	PortionService portionService;
		
	@Autowired
	CustomerUtils CustomerUtils;
	
	@Autowired
	DeviceService deviceService;
	
	@Autowired
	UserAccountService userService;
	
	@Autowired
	ElasticService elasticService;
	
	String mqttMsgTemplate = " \"opcode\":\"{0}\", \"uid\":\"{1}\",\"ap\":\"{2}\",\"mac\":\"{3}\", \"by\":\"{4}\"";
	
	static Logger LOG = LoggerFactory.getLogger(BeaconDeviceRestController.class.getName());

	private String indexname = "facesix*";
	
	@PostConstruct
	public void init() {
		indexname 		= _CCC.properties.getProperty("elasticsearch.indexnamepattern", "facesix*");
	}
	
	@Value("${facesix.cloud.name}")
	private String cloudUrl;
	
	
	@RequestMapping(value = "/listAll", method = RequestMethod.GET)
	public @ResponseBody Iterable<BeaconDevice> listAll() {
		return beaconDeviceService.findAll();
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(@RequestParam("cid") String cid) {		

		JSONArray array = new JSONArray();
		
		try {
			
			List<BeaconDevice> device = null;
			device = beaconDeviceService.findByCid(cid);
		
			String serverip = "0.0.0.0";
			String uid = "";
			String solution = "";
			String type = "";
			String sid = "";
			String spid = "";
			String ip = "";
			String tunnelIp = "";
			JSONObject conf = null;
			String tagThreshold  = "20";
			String vpn = "disable";
			
			if (device != null) {

				Customer acc = customerService.findById(cid);
				if (acc != null) {
					serverip 		= acc.getBleserverip();
					solution 		= acc.getVenueType();
					tagThreshold 	= acc.getThreshold();
					vpn 			= acc.getVpn();
				}
				
				if (vpn != null && vpn.equals("true")) {
					vpn = "enable";
				} else {
					vpn = "disable";
				}

				for (BeaconDevice dev : device) {
					conf = new JSONObject();
					
					uid = dev.getUid();
					sid = dev.getSid();
					spid = dev.getSpid();
					type = dev.getType();
					ip = dev.getIp() == null? "0.0.0.0":dev.getIp();
					tunnelIp = dev.getTunnelIp() == null?"0.0.0.0":dev.getTunnelIp();

					conf.put("uid", uid);
					
					if (type != null) {
						conf.put("type", type);
					}
					if (cid != null) {
						conf.put("cid", cid);
					}
					if (sid != null) {
						conf.put("sid", sid);
					}
					if (spid != null) {
						conf.put("spid", spid);
					}
					
					conf.put("ip",ip);
					conf.put("tunnelip", tunnelIp);

					conf.put("serverip", serverip);
					if(solution != null){
						if (solution.equalsIgnoreCase("Locatum")){
							conf.put("solution", "trilateration");
						} else if(solution.equalsIgnoreCase("Patient-Tracker")){
							conf.put("solution", "entryexit");
						} else {
							conf.put("solution", "gateway");
						}
					}
					conf.put("tagthreshold", tagThreshold);
					
					String mqttDebugFlag = "enable";
			
					if (dev.getDebugflag() != null) {
						String debug = dev.getDebugflag().trim();
						if (debug.equalsIgnoreCase("unchecked")) {
							mqttDebugFlag = "disable";
						}
					} else {
						mqttDebugFlag = "disable";
					}
					
					conf.put("debug", mqttDebugFlag);
					
					if (dev.getPixelresult() != null) {
						conf.put("deviceinfo", dev.getPixelresult());
					}

					String temp = dev.getConf();
					net.sf.json.JSONObject template	 = net.sf.json.JSONObject.fromObject(temp);
					template = beaconDeviceService.addVpnToConf(vpn,template);
					conf.put("conf", template);
					array.add(conf);
				}
			}
		} catch(Exception e) {
			LOG.info("while configured device listing error ",e);
			return null;
		}
		return array.toString();
	}

	@RequestMapping(value = "/info", method = RequestMethod.GET)
    public  String info(@RequestParam("uid") String uid) {
		
		BeaconDevice device = beaconDeviceService.findOneByUid(uid);
		
		String cid = "";
		String serverip = null;
		String type = null;
		String tagThreshold  = "20";
		String sid = "";
		String spid = "";
		String ip = "";
		String tunnelIp = "";
		String vpn = "disable";
		
		//LOG.info("UID" + uid);
		
		if (device != null && !BeaconDevice.STATUS.REGISTERED.name().equalsIgnoreCase(device.getStatus())) {
			
			JSONObject conf = new JSONObject();
			cid = device.getCid();
			sid = device.getSid();
			spid = device.getSpid();
			ip = device.getIp() == null ? "0.0.0.0" : device.getIp();
			tunnelIp = device.getTunnelIp() == null ? "0.0.0.0" : device.getTunnelIp();

			conf.put("uid", 	device.getUid());
			conf.put("type",  	device.getType());
			
			if (cid != null) {
				conf.put("cid", cid);
			}
			if (sid != null) {
				conf.put("sid", sid);
			}
			if (spid != null) {
				conf.put("spid", spid);
			}

			conf.put("ip", ip);
			conf.put("tunnelip", tunnelIp);
			
			Customer cust = customerService.findById(cid);
			if (cust !=null) {
				type     = cust.getVenueType();
				if (cust.getBleserverip() != null) {
					serverip = cust.getBleserverip();
				}
				tagThreshold 	= cust.getThreshold();
				vpn = cust.getVpn();
			}
			
			if (serverip == null) {
				serverip = "0.0.0.0";
			}
			
			conf.put("serverip", serverip);
			
			if(type != null){
				if (type.equalsIgnoreCase("Locatum")){
					conf.put("solution", "trilateration");
				} else if(type.equalsIgnoreCase("Patient-Tracker")){
					conf.put("solution", "entryexit");
				} else {
					conf.put("solution", "gateway");
				}
			}
			
			conf.put("tagthreshold", tagThreshold);
			
			String mqttDebugFlag = "enable";
			
			if (device.getDebugflag() != null) {
				String debug = device.getDebugflag().trim();
				if (debug.equalsIgnoreCase("unchecked")) {
					mqttDebugFlag = "disable";
				}
			} else {
				mqttDebugFlag = "disable";
			}
			
			conf.put("debug", mqttDebugFlag);
			
			if (device.getPixelresult() != null) {
				conf.put("deviceinfo", device.getPixelresult());
			}
			
			String temp = device.getConf();
			net.sf.json.JSONObject template	 = net.sf.json.JSONObject.fromObject(temp);
			net.sf.json.JSONObject diag_json = beaconDeviceService.makeDiagJson(template); //diag_details merge key and value
			
			if(vpn != null && vpn.equals("true")){
				vpn = "enable";
			} else {
				vpn = "disable";
			}
			
			template = beaconDeviceService.addVpnToConf(vpn,template);

			if (diag_json != null && diag_json.size() > 0) {
				template.put("diag_details",diag_json);
			}
			
			conf.put("conf", template);

			return conf.toString();
		} else {
			return "{ \"uid\" :\"" + uid + "\" , \"status\":\"NOT_FOUND\" }";
		}			
    }
 
	@RequestMapping(value = "/floor_info", method = RequestMethod.GET)
	public String floor_info(@RequestParam("sid") String sid) {

		JSONObject conf = new JSONObject();

		if (sid != null && !sid.isEmpty()) {

			List<Portion> floors = portionService.findBySiteId(sid);

			int countFloor = 0;
			JSONObject flr = null;
			JSONArray flr_array = new JSONArray();

			if (floors != null) {
				
				Iterator<Portion> iter = floors.iterator();
				while (iter.hasNext()) {
					Portion floor = iter.next();
					String spid = floor.getId();
					flr = new JSONObject();
					flr.put(String.valueOf(countFloor), spid);
					GeoFinderLayoutData device = geoService.getSavedGeoLayoutDataBySpid(spid);
					if (device != null) {
						flr.put("geoboundary", device.getGeoPointslist());
						flr.put("geoinfo", 	   device.getGeoresult());
						//flr.put("deviceinfo",  device.getPixelresult());//device location info
					}
					flr_array.add(flr);
					countFloor += 1;
				}
				conf.put("floorcount", String.valueOf(countFloor));
				conf.put("floorDetails", flr_array);

			}
			//LOG.info("conf>>>>>>>> " +conf);
			return conf.toString();
		} else {
			return null;
		}

	}
	
	 	@RequestMapping(value = "/scanner", method = RequestMethod.GET)
		@SuppressWarnings("unchecked")
		public JSONObject bleScanner(@RequestParam(value = "cid", required = false) String cid,
							   HttpServletRequest request) throws IOException{

		 	
			 //LOG.info("CID ID " + cid);
			JSONObject devlist = new JSONObject();
			try {
				
				JSONObject dev 				= null;
				JSONArray dev_array 		= new JSONArray();
				List<BeaconDevice> device 	= null;
				String state 				= "inactive";
				String deviceType			= "scanner";
				device 						= beaconDeviceService.findByCidAndType(cid, deviceType);
			
				if (device != null) {
					for (BeaconDevice dv : device) {
						dev = new JSONObject();
							state = dv.getState();
							dev.put("state", 		state.toUpperCase());
		                    dev.put("id", 			dv.getId());
							dev.put("mac_address",  dv.getUid());
							dev.put("dev_name", 	dv.getName());
							dev.put("status",		dv.getStatus());
							dev.put("cid", 			dv.getCid());
							dev.put("bleType", 		dv.getType());
							dev.put("debugflag",    dv.getDebugflag());
							
							String ip  				= dv.getIp();
							String tunnelIp			= dv.getTunnelIp();
							String devIp = (ip == null || ip.isEmpty()) ? "0.0.0.0" : ip;
							tunnelIp = (tunnelIp == null || tunnelIp.isEmpty()) ? "0.0.0.0" : tunnelIp;
							dev.put("ip",           devIp);
							dev.put("tunnelip",     tunnelIp);
							
							if (!state.equalsIgnoreCase("inactive") && !devIp.equals("0.0.0.0")) {
								dev.put("cmd_enable", "1");
							} else {
								dev.put("cmd_enable", "0");
							}
							dev_array.add(dev);
					}
					devlist.put("blescanner", dev_array);
				}
			} catch (Exception e) {
				LOG.info("while getting customer device list error", e);
			}
			return devlist;
		}
		
		@RequestMapping(value = "/receiver", method = RequestMethod.GET)
		@SuppressWarnings("unchecked")
		public JSONObject bleReceiver(@RequestParam(value = "cid", required = false) String cid,
									  @RequestParam(value = "uid", required = false) String uid,
								      HttpServletRequest request) throws IOException{

			JSONObject devlist = new JSONObject();
			try {
				
				JSONObject dev 				= null;
				JSONArray dev_array 		= new JSONArray();
				List<BeaconDevice> device 	= new  ArrayList<BeaconDevice>();
				String state 				= "inactive";
				String deviceType           = "receiver";
				
				if (uid == null || uid.isEmpty() || uid.equals("undefined")) {
					device 	= beaconDeviceService.findByCidAndType(cid, deviceType);
				} else {
					BeaconDevice dv = beaconDeviceService.findByUidAndCidAndType(uid, cid,deviceType);
					if (dv == null) {
						device = beaconDeviceService.findByCidAndTypeAndAlias(cid,deviceType, uid);
					}else {
						device.add(dv);
					}
				}
							
				if (device != null) {
					for (BeaconDevice dv : device) {
						dev 	= new JSONObject();
						state 	= dv.getState();

						dev.put("state", 		state.toUpperCase());
	                    dev.put("id", 			dv.getId());
						dev.put("mac_address",  dv.getUid());
						dev.put("dev_name", 	dv.getName());
						dev.put("status",		dv.getStatus());
						dev.put("cid", 			dv.getCid());
						dev.put("bleType", 		dv.getType());
						dev.put("debugflag",    dv.getDebugflag());
						
						String ip  				= dv.getIp();
						String tunnelIp			= dv.getTunnelIp();
						String devIp = (ip == null || ip.isEmpty()) ? "0.0.0.0" : ip;
						tunnelIp = (tunnelIp == null || tunnelIp.isEmpty()) ? "0.0.0.0" : tunnelIp;

						dev.put("ip",           devIp);
						dev.put("tunnelip",     tunnelIp);
						
						if (!state.equalsIgnoreCase("inactive") && !devIp.equals("0.0.0.0")) {
							dev.put("cmd_enable", "1");
						} else {
							dev.put("cmd_enable", "0");
						}
						dev_array.add(dev);
					}
				}
					devlist.put("blereceiver", dev_array);
			} catch (Exception e) {
				LOG.info("while getting customer device list error", e);
			}
			return devlist;
		}
		
		@RequestMapping(value = "/server", method = RequestMethod.GET)
		@SuppressWarnings("unchecked")
		public JSONObject list(@RequestParam(value = "cid", required = false) String cid,
							   HttpServletRequest request) throws IOException{

			 //LOG.info("CID ID " + cid);
			JSONObject devlist = new JSONObject();
			try {
				
				JSONObject dev 				= null;
				JSONArray dev_array 		= new JSONArray();
				List<BeaconDevice> device 	= null;
				String state 				= "inactive";
				String deviceType			= "server";
				device 						= beaconDeviceService.findByCidAndType(cid, deviceType);
				
				if (device != null) {
					for (BeaconDevice dv : device) {
						dev = new JSONObject();
						state = dv.getState();
						dev.put("state", 		state.toUpperCase());
	                    dev.put("id", 			dv.getId());
						dev.put("mac_address",  dv.getUid());
						dev.put("dev_name", 	dv.getName());
						dev.put("status",		dv.getStatus());
						dev.put("cid", 			dv.getCid());
						dev.put("bleType", 		dv.getType());
						dev.put("debugflag",    dv.getDebugflag());

						String ip  				= dv.getIp();
						String tunnelIp			= dv.getTunnelIp();
						String devIp = (ip == null || ip.isEmpty()) ? "0.0.0.0" : ip;
						tunnelIp = (tunnelIp == null || tunnelIp.isEmpty()) ? "0.0.0.0" : tunnelIp;

						dev.put("ip",          devIp);
						dev.put("tunnelip",     tunnelIp);
						
						if (!state.equalsIgnoreCase("inactive") && !devIp.equals("0.0.0.0")) {
							dev.put("cmd_enable", "1");
						} else {
							dev.put("cmd_enable", "0");
						}
						
						dev_array.add(dev);
					}
					devlist.put("bleserver", dev_array);
				}
			} catch (Exception e) {
				LOG.info("while getting customer device list error", e);
			}
			return devlist;
		}
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public void delete(@RequestParam("uid") String uid,
					   @RequestParam("cid") String cid,
					   @RequestParam(value = "sid", required = false) String sid,
					   @RequestParam(value = "spid", required = false) String spid,
					   HttpServletRequest request,
					   HttpServletResponse response) throws Exception {

		//LOG.info("BeaconDevice UID " +uid);
		//LOG.info("BeaconDevice  CID " +cid);
		
		String str = "/facesix/web/finder/device/list?cid="+cid+"&sid="+sid+"&spid="+spid;
		
		try {
			
			String type = null;
			
			NetworkDevice nd = null;
			BeaconDevice device = beaconDeviceService.findOneByUid(uid);
			if (device != null) {
				type = device.getType();
				beaconDeviceService.delete(device);
				device.setId(null);
			}
			String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
			nd = networkDeviceService.findOneByUuid(uuid);
			if (nd != null) {
				networkDeviceService.delete(nd);
				//LOG.info("ND Delete UUID " +uuid);
			}

			beaconDeviceService.resetServerIP(type,cid);
			
			response.sendRedirect(str);
		} catch (Exception e) {
			response.sendRedirect(str);
			LOG.info("While Device Delete Error " ,e);
		}
		
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(
			@RequestParam(value = "uuid", required = true) String uid,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "conf", required = true) String conf,
			@RequestParam(value = "name", required = false) String name,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		//LOG.info("BEACON SAVE UID  " + uid);
		//LOG.info("BEACON SAVE NAME " + name);
		//LOG.info("BEACON SAVE JSON  " + conf);
		//LOG.info("BEACON SAVE CID  " + cid);
		
		String str = "/facesix/web/finder/device/list?cid="+cid;
		
		try {
			
			net.sf.json.JSONObject template = net.sf.json.JSONObject.fromObject(conf);
			BeaconDevice device = null;
			
			device =beaconDeviceService.findOneByUid(uid);
				if (device == null) {
					//LOG.info("BEACON SAVE >>>>");
					device = new BeaconDevice();
					device.setCreatedBy(SessionUtil.currentUser(request.getSession()));
					device.setUid(uid);
					device.setName(name);
					device.setFstype("scanner");
					device.setStatus(Device.STATUS.AUTOCONFIGURED.name());
					device.setState("inactive");
					device.setCid(cid);
					device.setTemplate(template.toString());
					device.setConf(template.toString());
					device.setModifiedBy("Cloud");
					device = beaconDeviceService.save(device, true);
				} else {
					//LOG.info("BEACON UPDATE>>>>>>");
					device.setStatus(Device.STATUS.CONFIGURED.name());
					device.setName(name);
					device.setTemplate(template.toString());
					device.setConf(template.toString());
					device.setModifiedBy("Cloud");
					device.setModifiedOn(new Date(System.currentTimeMillis()));
					device.setCid(cid);
					device =beaconDeviceService.save(device, true);
				}
				response.sendRedirect(str);

		} catch (Exception e) {
			response.sendRedirect(str);
			LOG.info("While beacon save error ",e);
		}
	}
	
	@RequestMapping(value = "/registered", method = RequestMethod.GET)
	public JSONObject register(@RequestParam(value = "cid", required = false) String cid,
							   HttpServletRequest request) throws IOException{

		if (cid == null || cid.isEmpty()) {
			//LOG.info(" REGISTEREDBEACON DEVICE CID  " + cid);
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		//LOG.info(" AFTER REGISTERED BEACONDEVICE CID  " + cid);
		 
		JSONObject devlist = new JSONObject();
		try {
			
			JSONObject dev = null;
			JSONArray dev_array = new JSONArray();	
			
			Iterable<BeaconDevice> device= beaconDeviceService.findByQuery("+status:REGISTERED", "modifiedOn", 0, 1000);

			if (device != null) {
				for (BeaconDevice dv : device) {
					dev = new JSONObject();
						dev.put("state", 		"UNKNOWN");
	                    dev.put("id", 			dv.getId());
						dev.put("mac_address",  dv.getUid());
						dev.put("dev_name", 	dv.getName());
						dev.put("status",		dv.getStatus().toUpperCase());
						dev.put("cid", 			cid);
						dev.put("bleType", 		dv.getFstype());
						dev.put("ip",           dv.getIp());
						dev.put("cmd_enable", 	"0");
						dev_array.add(dev);
					}
				devlist.put("registered", dev_array);
				//LOG.info("BLE REGISTERED LIST JSON " + devlist.toString());
			}
		} catch (Exception e) {
			LOG.info("while getting registered device list error", e);
		}
		return devlist;
	}
	
	@RequestMapping(value = "/beacondefaultconfig", method = RequestMethod.POST)
	public JSONObject custlegactest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		net.sf.json.JSONObject JSONCONF = null;
		try {
			String tconf = null;
			tconf = SpringComponentUtils.getApplicationMessages().getMessage("facesix.beacon.device.template.default");
			JSONCONF = net.sf.json.JSONObject.fromObject(tconf);
			//LOG.info("BEACON DEFAULT CONFIG " + JSONCONF);
		} catch (Exception e) {
			LOG.info("WHILE GETTING BEACON DEFAULT CONFIG ERROR {}", e);
		}
		return JSONCONF;
	}
	@RequestMapping(value = "/rpc", method = RequestMethod.POST)
	public String rpc(@RequestParam(value = "uid", required = true) String uid,
			@RequestParam(value = "ap", required = true) String ap,
			@RequestParam(value = "mac", required = false) String mac,
			@RequestParam(value = "cmd", required = true) String cmd) {

		String ret = "SUCCESS: RPC Message Sent";
		//LOG.info("RPC::UID " + uid);
		//LOG.info("RPC::MAC " + mac);
		//LOG.info("RPC::AP " + ap);
		//LOG.info("RPC::CMD " + cmd);
		try {

			BeaconDevice device = beaconDeviceService.findOneByUid(uid);

			if (device != null && !BeaconDevice.STATUS.REGISTERED.name().equalsIgnoreCase(device.getStatus())) {

				//LOG.info("RPC Status" + device.getStatus());
				String message = MessageFormat.format(mqttMsgTemplate,new Object[] { cmd, device.getUid().toUpperCase(), ap, mac, "device_update" });
				
				if (cmd.equals("RESET")) {
					beaconDeviceService.reset(device, true);
				} else {
					deviceEventMqttPub.publish("{" + message + "}", uid.toUpperCase());
				}

				//LOG.info("SUCCESS: RPC Message Sent |uid:" + uid + "|cmd:" + cmd);

			} else {
				ret = "FAILURE: Invalid Device";
				//LOG.info("Invalid Device");
			}
		} catch (Exception e) {
			ret = "Error: FATAL error occured";
			LOG.error("FAILURE: RPC Message Failed |uid :" + uid + "|cmd:" + cmd, e);
			ret = "FAILURE: RPC Message Failed";
		}

		return ret;
	}
	@RequestMapping(value = "/checkDuplicate", method = RequestMethod.GET)
	public String checkDuplicate(@RequestParam("uid") String uid, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String Retresponse  = "new";
		BeaconDevice device = beaconDeviceService.findOneByUid(uid);
		if (device != null && !BeaconDevice.STATUS.REGISTERED.name().equalsIgnoreCase(device.getStatus())) {
			Retresponse="duplicate";
		}
		
		return Retresponse;
	}
	@RequestMapping(value = "/deleteall", method = RequestMethod.GET)
	public void deleteall(@RequestParam(value = "cid", required = false) String cid,
						 HttpServletRequest request,
					     HttpServletResponse response) throws IOException{

		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		//LOG.info("  CID  " + cid);
		String str = "/facesix/web/finder/device/reglist?cid="+cid;
		try {
			
			Iterable<BeaconDevice> device= beaconDeviceService.findByQuery("+status:REGISTERED", "modifiedOn", 0, 1000);	
			if (device != null) {
				for (BeaconDevice dv : device) {
					if (BeaconDevice.STATUS.REGISTERED.name().equalsIgnoreCase(dv.getStatus())) {
						beaconDeviceService.delete(dv);
					}
				}
			}
	
			//LOG.info("delete sucess===> " +device.toString());
			
			response.sendRedirect(str);
		} catch (Exception e) {
			response.sendRedirect(str);
		}
	}
	
	@RequestMapping(value = "/binary/save",method = RequestMethod.POST)
	public JSONObject save(@RequestParam(value = "file" ,required = true) MultipartFile file,
			@RequestParam(value = "upgradeType" ,required = true) String upgradeType,
			@RequestParam(value="binaryType",	required = true)  String binaryType,
			@RequestParam(value = "sid", 		required = false) String sid,
			@RequestParam(value = "spid", 		required = false) String spid,
			@RequestParam(value = "location", 	required = false) String location,
			@RequestParam(value = "cid", 		required = false) String cid,
			HttpServletRequest request,			HttpServletResponse response) {
		
		
		String universalId  = null;
		int deviceCount		= 0;
		JSONObject retJsonObject = new JSONObject();
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			try {
				
				LOG.info(" upgradeType " + upgradeType +" binaryType " + binaryType);
				LOG.info(" CId " + cid+" SId " + sid+ " SPId " + spid+ " location " + location);
				
				Path path 		   		   = null;
				String fileName    		   = null;
				String md5CheckSum 		   = null;
				boolean isUID 		 	   = false;
				boolean IsgwSolution 	   = false;
				List<NetworkDevice> device = null;
				List<NetworkDevice> list   = new ArrayList<NetworkDevice>(); 
	
				upgradeType = upgradeType.trim();
				
				switch (upgradeType) {
		
				case "venue":
		
					if (sid != null && sid.equals("all")) {
						device = networkDeviceService.findByCid(cid);
						universalId = cid;
					} else if (sid !=null){
						device = networkDeviceService.findBySid(sid);
						universalId = sid;
					}
					break;
					
				case "floor":
					
					if (sid.equals("all") && cid !=null) {
						device = networkDeviceService.findByCid(cid);
						universalId = cid;
					} else if (spid != null && spid.equals("all") && sid != null) {
						device = networkDeviceService.findBySid(sid);
						universalId = sid;
					} else  if (spid != null){
						device = networkDeviceService.findBySpid(spid);
						universalId = spid;
					}
					break;
					
				case "location":
					
					if (location != null && location.contains(":") && (location.trim().length() == 17)) {
						String uuid   = location.replaceAll("[^a-zA-Z0-9]", "");
						device 		  = networkDeviceService.findByUuid(uuid);
						universalId = location;
					} else if (sid != null && sid.equals("all") && cid !=null) {
						device = networkDeviceService.findByCid(cid);
						universalId = cid;
					} else if (spid != null && spid.equals("all") && sid != null)  {
						device  = networkDeviceService.findBySid(sid);
						universalId = sid;
					}  else if (location != null && location.equals("all") && spid != null)  {
						device  = networkDeviceService.findBySpid(spid);
						universalId = spid;
					} 
						break;
				}
				
				if (device != null) {
					list.addAll(device);
					deviceCount = list.size();
					for (NetworkDevice dv : list) {
						dv.setBinaryType(binaryType);
						dv.setUpgradeType(upgradeType);
						dv.setBinaryreason("FAILURE");
						networkDeviceService.save(dv, false);
					}
				} else {
					return null;
				}
			
				if (universalId != null && universalId.contains(":")) {
					isUID = true;
				} else {
					isUID = false;
				}

				if (CustomerUtils.Gateway(cid) || CustomerUtils.isRetail(cid)) {
					IsgwSolution = true;
				}

				if (isUID == true && IsgwSolution == false) {
					universalId   = universalId.toUpperCase();
				} else if (IsgwSolution == true && isUID==true) {
					universalId  = universalId.toLowerCase();
				}	
				
				LOG.info(" MQTT Uiversal Id   " + universalId + " deviceCount " +deviceCount);
				
				if (file != null && !file.isEmpty() && file.getSize() > 1) {
					fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
					path = Paths.get(_CCC.properties.getProperty("facesix.fileio.binary.root", "/var/www/html"),(fileName));
					//path 	 = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_"),(fileName)); // test path
					Files.createDirectories(path.getParent());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					md5CheckSum = checkSumMD5(path.toString());
				} else {
					LOG.info("File is " +file);
					return null;
				}
				
			
				JSONObject json  = new JSONObject();
				String cloudName = cloudUrl;
				
				json.put("filename", 	fileName);
				json.put("filepath", 	cloudName);
				json.put("md5sum", 		md5CheckSum);
				String opcode 			= binaryType;
				
				BINARY_BOOT(json, opcode,universalId);
				
			} catch (IOException e) {
				e.printStackTrace();
				LOG.warn("Failed save binary files", e);
			}

			retJsonObject.put("universalId", universalId);
			retJsonObject.put("upgradeType", upgradeType);
			retJsonObject.put("deviceCount", deviceCount);
			
			//LOG.info(" binary retJsonObject " +retJsonObject);
			
			return retJsonObject;
			
		}

		return retJsonObject;

	}
	
	
	public void BINARY_BOOT(JSONObject json,String opcode, String id) {
		
		try {
			
			String mqttMsgTemplate = " \"opcode\":\"{0}\",\"by\":\"{1}\", \"type\":\"{2}\", \"value\":{3} ";
			String message = MessageFormat.format(mqttMsgTemplate, new Object[] { opcode, "qubercloud", "DFU", json });
			deviceEventMqttPub.publish("{" + message + "}", id);
			
			LOG.info("BINARY BOOT MQTT MESSAGE " + message +" " + id);
		} catch (Exception e) {
			LOG.warn("Failed to notify update", e);
		}
		
	}

	public static String checkSumMD5(String file) throws IOException {
		String checksum = "";
		FileInputStream inputStream = new FileInputStream(file);
		try {
			checksum = DigestUtils.md5Hex(inputStream);
			 LOG.info("Calulating MD5 checksum  "+checksum);
		} catch (IOException ex) {
			LOG.info("While Calulating MD5 checksum error ",ex);
		}finally{
			inputStream.close();
		}
		return checksum;
	}

	
	@RequestMapping(value = "/upgrade", method = RequestMethod.GET)
	public JSONArray upgrade(@RequestParam(value = "upgradeType", required = true) String upgradeType,
						     @RequestParam(value = "cid", 		required = true) String cid,
							 @RequestParam(value = "uid",required = true) String universalId,
						     HttpServletRequest request) throws IOException {

		JSONArray  array = new JSONArray ();
		
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			try {
				
				if (upgradeType == null || upgradeType.isEmpty()) {
					return array;
				}
				
				LOG.info(" upgradeType " + upgradeType);
				LOG.info(" upgrade cid " + cid);
				LOG.info(" upgrade universalId " + universalId);
							
				String status 		= "FAILURE";
				String ip           = "0.0.0.0";
				String version 		= null;
				String buildtime 	= null;
				
				JSONObject json  				= new JSONObject();
				BeaconDevice beaconDevice 		= null;
				Device       device 	  	    = null;
				List<NetworkDevice> ndDevice   	= null;
				List<NetworkDevice> list 		= new ArrayList<NetworkDevice>();
				
				boolean foundDev = true;
				
				if (universalId != null && !universalId.isEmpty()) {
					
					if (universalId.contains(":")) {
						String uuid       = universalId.replaceAll("[^a-zA-Z0-9]", "");
						ndDevice 		  = networkDeviceService.findByUuid(uuid);
					} else {

						ndDevice = networkDeviceService.findBySid(universalId);
						
						if (ndDevice != null && !ndDevice.isEmpty() && ndDevice.size() > 0) {
							foundDev = false;
						}

						if ((ndDevice == null || ndDevice.isEmpty()) && foundDev) {
							ndDevice = networkDeviceService.findBySpid(universalId);
						}
						if ((ndDevice == null || ndDevice.isEmpty()) && foundDev) {
							ndDevice = networkDeviceService.findByCid(universalId);
						}

					}
				} else {
					return array;
				}
				
				if (ndDevice != null && ndDevice.size() >0) {
					list.addAll(ndDevice);
				} else {
					return null;
				}

				for (NetworkDevice nd : list) {
		
					String  id		=  nd.getUid();
					String  c_id	=  nd.getCid();
					
					if (CustomerUtils.GeoFinder(c_id)) {
						beaconDevice = beaconDeviceService.findByUidAndCid(id, cid);;
						if (beaconDevice != null) {
							ip 		  = beaconDevice.getIp();
							version   = beaconDevice.getVersion();
							buildtime = beaconDevice.getBuild();
						}
					} else if (CustomerUtils.Gateway(c_id)) {
						device = deviceService.findOneByUid(id);
						if (device != null) {
							ip = device.getIp();
							version   = device.getBuildVersion();
							buildtime = device.getBuildTime();
						}
					} else {
						
						beaconDevice = beaconDeviceService.findOneByUid(id);
						if (beaconDevice != null) {
							ip 		  = beaconDevice.getIp();
							version   = beaconDevice.getVersion();
							buildtime = beaconDevice.getBuild();
						}
						
						device = deviceService.findOneByUid(id);
						if (device != null) {
							ip = device.getIp();
						}
						
					}
					
					if (nd.getBinaryreason() != null) {
						status = nd.getBinaryreason().toUpperCase();
					}
					version   = (version == null || version.isEmpty()) ? "UNKOWN" : version;
					buildtime = (buildtime == null || buildtime.isEmpty()) ? "UNKOWN" : buildtime;
					
					json.put("uid", 		id);
					json.put("binaryreason",status);
					json.put("binaryType",  nd.getBinaryType());
					json.put("upgradeType", nd.getUpgradeType());
					json.put("ip",			ip);
					json.put("version",     version);
					json.put("buildtime",   buildtime);
					array.add(json);
				}
				addArrayToElasticSearch(cid,array,upgradeType,request);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return array;	
			
		}
		return array;
	}
	
	private void addArrayToElasticSearch(String cid, JSONArray array,String upgradeType, HttpServletRequest request) {
				
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			Map<String, Object> postData = new HashMap<String, Object>();
			String current_user 		 = "Unknown";
			Customer  cx 			     = customerService.findById(cid);
			TimeZone timezone 			 = CustomerUtils.FetchTimeZone(cx.getTimezone());
			
			DateFormat format 			 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			format.setTimeZone(timezone);
			
			String current_time 		= format.format(new Date());
			String updgradeEventTable   = "device-history-event";
		
			
			updgradeEventTable = _CCC.properties.getProperty("device.history.event.table", updgradeEventTable);
			current_user 	   = SessionUtil.currentUser(request.getSession());
			
			UserAccount user = userService.findOneByEmail(current_user);
			if(user != null){
				current_user = user.getFname()+" "+user.getLname();
			}
			
			postData.put("opcode",     "upgradeHistory");
			postData.put("cid", 	    cid);
			postData.put("userName",    current_user.toUpperCase());
			postData.put("upgradeType", upgradeType.toUpperCase());
			postData.put("time",	    current_time);
			postData.put("status", 		array);
			
			elasticService.post(updgradeEventTable, "device-upgrade", postData);
		}
	}

	@RequestMapping(value = "/upgradeHistory", method = RequestMethod.GET)
	public List<Map<String, Object>> upgradeHistory(@RequestParam(value = "cid", required = true) String cid,
													@RequestParam(value = "time", required = false) String timeInterval,
													HttpServletRequest request, HttpServletResponse response) {
		
		List<Map<String, Object>> upgradeHistory  = null;
		
		if (SessionUtil.isAuthorized(request.getSession())) {

			if (timeInterval == null) {
				timeInterval = "365d";
			}
			
			String indexname     = _CCC.properties.getProperty("device.history.event.table","device-history-event");
			String deviceUpgrade = "device-upgrade";
			
			String fsql = "index="+indexname+",type ="+deviceUpgrade+",sort=timestamp desc,"
					+ "query=timestamp:>now-" + timeInterval +" AND cid:" + cid + " AND opcode:\"upgradeHistory\" "
					+ "|value(userName,userName, NA);value(upgradeType,upgradeType,NA);value(time,time,NA);value(status,status,NA)|table ;";	

			upgradeHistory = fsqlRestController.query(fsql);
		}
		return upgradeHistory;
	}

	@RequestMapping(value = "/scannerList", method = RequestMethod.GET)
	public JSONObject locationlist(@RequestParam(value = "cid", required  = true) String cid,
								   @RequestParam(value = "type", required = true) String type,
								   HttpServletRequest request,HttpServletResponse response) {
		
		if (cid == null) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		JSONObject json 	= null;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonList = new JSONObject();
	
		try {
			
			List<BeaconDevice> deviceType = beaconDeviceService.findByCidAndType(cid, type);
			for (BeaconDevice device : deviceType) {
				json = new JSONObject();
				json.put("uid",	 		 device.getAlias());
				String scanduration 	 = scanDuration(device.getConf());
				json.put("scanduration", scanduration);
				jsonArray.add(json);
			}
			
			
		} catch (Exception e) {
			LOG.error("While Scanner Duration getting error " + e);
		}
		jsonList.put("scanner", jsonArray);
		return jsonList;

	}
	
	public  String scanDuration(String conf) {

		String scanduration = "10";

		net.sf.json.JSONObject template   = net.sf.json.JSONObject.fromObject(conf);
		JSONArray jsonArray 		      = new JSONArray();
		net.sf.json.JSONObject jsonObject = new JSONObject();

		if (template.get("attributes") != null) {
			jsonArray = template.getJSONArray("attributes");
			if (jsonArray != null && jsonArray.size() > 0) {
				jsonObject = jsonArray.getJSONObject(0);
			}
		}

		if (jsonObject.get("scanduration") != null) {
			scanduration = jsonObject.getString("scanduration");
			scanduration = scanduration.split("\\.")[0];
		}
		LOG.info("scanduration " +scanduration);
		return scanduration;
	}
	
	@RequestMapping(value = "/debugByDevices", method = RequestMethod.POST)
	public void  debugByDevices(
		   @RequestParam(value = "sid",   		required = false) String  sid,
		   @RequestParam(value = "spid", 		required = false) String spid,
		   @RequestParam(value = "uid", 		required = false) String uid,
		   @RequestParam(value = "cid", 		required = false) String cid,
		   @RequestParam(value = "debugflag", 	required = true) String flag,
		   @RequestParam(value = "type",        required = false) String type,
		   HttpServletRequest request, 			HttpServletResponse response) throws IOException{

		try {

			String debug 			  			= "disable";
			String opcode 			 		 	= "device_logging";
			String  debugflag         			= "unchecked";
			ArrayList<BeaconDevice> deviceList  = new ArrayList<BeaconDevice>();
			
			if (flag.equals("true")) {
				debug 	  = "enable";
				debugflag = "checked";
			}

			String template = " \"opcode\":\"{0}\", \"device_uid\":\"{1}\",\"debug\":\"{2}\"";
			String message  =  "";
			
			if (uid != null && !uid.isEmpty()) { // select by uid
				BeaconDevice device = beaconDeviceService.findOneByUid(uid);
				deviceList.add(device);
				LOG.info("find by uid " +uid);
			} else { // select all
				List<BeaconDevice> beaconDevice = beaconDeviceService.findByCidAndType(cid,type);
				deviceList.addAll(beaconDevice);
				LOG.info("find by cid " +cid + " type " +type);
			}

			for (BeaconDevice dv : deviceList) {
				dv.setDebugflag(debugflag);
				dv.setModifiedOn(new Date());
				dv.setModifiedBy(whoami(request, response));
				beaconDeviceService.save(dv, false);
				uid 		= dv.getUid().toUpperCase();
				message 	= MessageFormat.format(template, new Object[] { opcode, uid, debug });
				deviceEventMqttPub.publish("{" + message + "}", uid);
			}

		} catch (Exception e) {
			LOG.info("While debug enabling error {}",e);
		}
	}
	
	@RequestMapping(value = "/buildversion", method = RequestMethod.GET)
	public JSONObject buildversion(
			@RequestParam(value = "cid", required = true) String cid,
			@RequestParam(value = "uid", required = true) String uid,
			HttpServletRequest request,HttpServletResponse response) {

		JSONObject json = new JSONObject();
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			if ((uid != null && uid.equalsIgnoreCase("all"))) {
				return json;
			}

			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			String version 			  = "UNKNOWN";
			String buildtime   		  = "UNKNOWN";
			
			Device device 			  = null;
			BeaconDevice beaconDevice = null;
			
			device 		 = deviceService.findOneByUid(uid);
			beaconDevice = beaconDeviceService.findOneByUid(uid);
			
			if (CustomerUtils.Gateway(cid) || CustomerUtils.Heatmap(cid)) {
					json.put("version",   version);
					json.put("buildtime", buildtime);
				return json;
				
			} else if (CustomerUtils.GeoFinder(cid)) {
				
				if (beaconDevice != null && uid.equalsIgnoreCase(beaconDevice.getUid())) {
					
					version = beaconDevice.getVersion();
					buildtime = beaconDevice.getBuild();
					
					version   = (version == null || version.isEmpty()) ? "UNKOWN" : version;
					buildtime = (buildtime == null || buildtime.isEmpty()) ? "UNKOWN" : buildtime;
					
					json.put("version", version);
					json.put("buildtime", buildtime);
				}
				
				return json;

			} else if (CustomerUtils.GatewayFinder(cid)){
				
				if (device != null && uid.equalsIgnoreCase(device.getUid())) {
					json.put("version", version);
					json.put("buildtime", buildtime);
					return json;
				}
				if (beaconDevice != null && uid.equalsIgnoreCase(beaconDevice.getUid())) {
					
					version = beaconDevice.getVersion();
					buildtime = beaconDevice.getBuild();
					
					version   = (version == null || version.isEmpty()) ? "UNKOWN" : version;
					buildtime = (buildtime == null || buildtime.isEmpty()) ? "UNKOWN" : buildtime;
					
					json.put("version", version);
					json.put("buildtime", buildtime);
					return json;
				}
			}
		}

		return json;

	}
	
	@RequestMapping(value = "/binaryDeviceUid", method = RequestMethod.GET)
	public JSONObject binaryDeviceUid(
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid, HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject jsonList = new JSONObject();
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			JSONObject json 		= null;
			JSONArray jsonArray 	= new JSONArray();
			
			if ((sid != null && sid.equalsIgnoreCase("all")) || (spid != null && spid.equalsIgnoreCase("all"))) {
				return jsonList;
			}

			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			List<Device> device 			= null;
			List<BeaconDevice> beaconDevice = null;
			
			device 		 = deviceService.findBySpid(spid);
			beaconDevice = beaconDeviceService.findBySpid(spid);
			
			if (CustomerUtils.Gateway(cid) || CustomerUtils.Heatmap(cid)) {
				
				for(Device dev : device) {
					json = new JSONObject();
					json.put("uid", dev.getUid());
					json.put("name", dev.getAlias());
					jsonArray.add(json);
				}
				jsonList.put("location", jsonArray);
				
				return jsonList;
			} else if (CustomerUtils.GeoFinder(cid)) {
				
				for (BeaconDevice dev : beaconDevice) {
					json = new JSONObject();
					json.put("uid", dev.getUid());
					json.put("name", dev.getAlias());
					jsonArray.add(json);
				}
				
				jsonList.put("location", jsonArray);
				return jsonList;

			} else if (CustomerUtils.GatewayFinder(cid)){
				
				for(Device dev : device) {
					json = new JSONObject();
					json.put("uid", dev.getUid());
					json.put("name", dev.getAlias());
					jsonArray.add(json);
				}
				
				for (BeaconDevice dev : beaconDevice) {
					json = new JSONObject();
					json.put("uid",  dev.getUid());
					json.put("name", dev.getAlias());
					jsonArray.add(json);
				}
				jsonList.put("location", jsonArray);
				
				return jsonList;
			}

		}

		return jsonList;

	}
}
