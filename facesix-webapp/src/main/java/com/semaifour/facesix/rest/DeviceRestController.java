package com.semaifour.facesix.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.account.rest.CaptivePortalRestController;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.captive.portal.CaptivePortalService;
import com.semaifour.facesix.data.elasticsearch.ElasticService;
import com.semaifour.facesix.data.elasticsearch.device.ClientDevice;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.qubercast.QuberCast;
import com.semaifour.facesix.data.qubercast.QuberCastService;
import com.semaifour.facesix.mqtt.DeviceEventPublisher;
import com.semaifour.facesix.spring.SpringComponentUtils;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.DeviceHelper;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.semaifour.facesix.probe.oui.ProbeOUI;
import com.semaifour.facesix.probe.oui.ProbeOUIService;

/**
 * 
 * Rest Device Controller handles all rest calls
 * 
 * @author mjs
 *
 */
@RestController
@RequestMapping("/rest/device")
public class DeviceRestController extends WebController {
	
	static Logger LOG = LoggerFactory.getLogger(DeviceRestController.class.getName());
	
	@Autowired
	DeviceService deviceManager;

	@Autowired
	DeviceEventPublisher deviceEventMqttPub;
	
	@Autowired
	ClientDeviceService 	clientDeviceService;	
	
	@Autowired
	QuberCastService qubercastService;
	
	@Autowired
	private DeviceEventPublisher mqttPublisher;
	
	@Autowired
	NetworkConfRestController networkConfRestController;
	
	@Autowired
	NetworkDeviceService  networkDeviceService;
	
	@Autowired
	FSqlRestController fsqlRestController;
	
	@Autowired
	private ElasticService elasticService;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	CustomerUtils customerUtils;
	
	private String indexname = "facesix*";
	
	String 	prop_event_table_index = "facesix-prop-client-event";
	
	 @Autowired
	QubercommScannerRestController qubercommScannerRestController;

	@Autowired
	ProbeOUIService probeOUIService;
	
	@Autowired
	NetworkDeviceRestController networkDeviceRestController;
	
	@Autowired
	CaptivePortalService captivePortalService;
	
	@Autowired
	CaptivePortalRestController  captivePortalRestController;
	
	
	@PostConstruct
	public void init() {
		indexname 		= _CCC.properties.getProperty("elasticsearch.indexnamepattern", "facesix*");
		prop_event_table_index = _CCC.properties.getProperty("facesix.data.prop.event.table",
				prop_event_table_index);

	}
	
	String mqttMsgTemplate = " \"opcode\":\"{0}\", \"uid\":\"{1}\",\"ap\":\"{2}\",\"mac\":\"{3}\", \"by\":\"{4}\"";
	
	String reffId="a5a5";

	static Font smallBold 	= new Font(Font.FontFamily.TIMES_ROMAN, 12, 	Font.BOLD);
    static Font catFont 	= new Font(Font.FontFamily.TIMES_ROMAN, 18, 	Font.BOLD);
    static Font redFont 	= new Font(Font.FontFamily.TIMES_ROMAN, 12, 	Font.NORMAL);
    static Font subFont 	= new Font(Font.FontFamily.TIMES_ROMAN, 16,     Font.BOLD);
    static boolean ThreadTobeStarted = false;
    
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody Iterable<Device> listAll(@RequestParam(value="size", defaultValue="100") String size, 
										@RequestParam(value="page", defaultValue="0") String page) {
		return deviceManager.findAll();
	}
	
	@RequestMapping(value = "/list/{status}", method = RequestMethod.GET)
	public @ResponseBody List<Device> list(@PathVariable("status") String status) {
		return deviceManager.findByStatus(status);
	}

	 
	 @RequestMapping(value = "/info", method = RequestMethod.GET)
	 public @ResponseBody Device info(@RequestParam("uid") String uid) {
			Device device = deviceManager.findOneByUid(uid);
			return device;
	}
	
    @RequestMapping(value = "conf", method = RequestMethod.GET)
    public  String confGet(@RequestParam("uid") String uid) {
		
    	Device device = deviceManager.findOneByUid(uid);
		
		//LOG.info("UID" + uid);
		
		if (device != null && !Device.STATUS.REGISTERED.name().equalsIgnoreCase(device.getStatus())) {
			
			JSONObject template =JSONObject.fromObject(device.getConf());
			
			String cid 		= null;
			String sid 		= null;
			String spid 	= null;
			
			if (device.getCid() != null) {
				cid = device.getCid();
				template.put("cid", cid);
			}
			if (device.getSid() != null) {
				sid = device.getSid();
				template.put("sid", sid);
			}
			if (device.getSpid() != null) {
				spid = device.getSpid();
				template.put("spid", spid);
			}
			
			String steering = device.getSteering() == null ? "false" : device.getSteering();
			template.put("steering", steering);
			
			String loadbalance = device.getLoadBalance() == null ? "false" : device.getLoadBalance();
			template.put("loadbalance", loadbalance);
			
			String keepalive = device.getKeepAliveInterval()== null ? "30": device.getKeepAliveInterval();
			template.put("keepalive", keepalive);

			String  root = device.getRoot() == null ? "no" : device.getRoot();
			template.put("root", root);
			
			String  workingMode = device.getWorkingMode() == null ? "normalmode" : device.getWorkingMode();
			template.put("workingmode", workingMode);
			
			/*
			 * Steer Band balancing
			 * 
			 */
			
			if (steering.equals("true")) {
				
				if (device.getBssStationRejectThresh() != null) {
					template.put("bss_station_reject_thresh", device.getBssStationRejectThresh());
				}if (device.getBssChanPbusyPercent() != null) {
					template.put("bss_chan_busy_percent", device.getBssChanPbusyPercent());
				}if (device.getBssRejectTimeout() != null) {
					template.put("bss_reject_timeout", device.getBssRejectTimeout());
				}if (device.getBand2GStaRatio() != null) {
					template.put("band_2g_sta_ratio", device.getBand2GStaRatio());
				}if (device.getBand5GStaRatio() != null) {
					template.put("band_5g_sta_ratio", device.getBand5GStaRatio());
				}if (device.getBandRcpiDiff() != null) {
					template.put("band_rcpi_diff", device.getBandRcpiDiff());
				}
				
			}
			
			/*
			 * Load balancing
			 * 
			 */
		
			if (loadbalance.equals("true")) {
				if (device.getMinStaCount() != null) {
					template.put("min_sta_count", device.getMinStaCount());
				}if (device.getRssiThreshold() != null) {
					template.put("rssi_threshold", device.getRssiThreshold());
				}if (device.getRcpiRange() != null) {
					template.put("rcpi_range", device.getRcpiRange());
				}if (device.getAvgStaCntLb() != null) {
					template.put("avg_sta_cnt_lb", device.getAvgStaCntLb());
				}
			}
			
			JSONArray hotspot = captivePortalRestController.getHotspotLink(cid, sid, spid);
			if (hotspot != null && hotspot.size() > 0) {
				template.put("hotspot", hotspot);
			}
			
			JSONObject ethernet_setting = new JSONObject();
			JSONObject lan_setting 		= new JSONObject();

			String lan_flag = device.getLan_Flag() == null ? "NA" : device.getLan_Flag();

			if (lan_flag.equals("lan_static")) {
				lan_setting.put("proto", "static");
				lan_setting.put("ipv4_addr", device.getLan_ipv4_Addr());
				lan_setting.put("ipv4_mask", device.getLan_ipv4_Mask());
			} else if (lan_flag.equals("lan_dhcp")) {
				lan_setting.put("proto", "dhcp");
			}

			JSONArray ethernet_setting_array = new JSONArray();
			ethernet_setting_array.add(lan_setting);
			
			ethernet_setting.put("lan", 	 ethernet_setting_array);
			
			template.put("network_setting", ethernet_setting);

			return template.toString();
		} else {
			return "{ \"uid\" :\"" + uid + "\" , \"status\":\"NOT_FOUND\" }";
		}			
    }
    
    @RequestMapping(value = "conf", method = RequestMethod.POST)
    public  String confPost(@RequestParam(value="uid", required=true) String uid, 
    						@RequestParam(value="name", required=false) String name, 
    						@RequestBody String conf) {
    	
		Device device = deviceManager.findOneByUid(uid);

		conf = StringUtils.trim(conf);

		String oldConf = conf;

		if (device != null) {
			oldConf = device.getConf();
			device.setConf(conf);
			if (!StringUtils.isEmpty(name)) {
				device.setName(name);
			}
			device.setModifiedOn(new Date());
			deviceManager.save(device);
		} else {
			uid = StringUtils.trim(uid);
			if (StringUtils.isEmpty(name)) {
				name = uid; 
			} else {
				name = StringUtils.trim(name);
			}
			
			device = new Device();
			device.setCreatedOn(new Date());
			device.setUid(uid);
			device.setName(name);
			device.setConf(conf);
			device.setModifiedOn(new Date());
			//device.setIp("0.0.0.0");
		}
		return oldConf;
    }

        
    @RequestMapping(value = "configure", method = RequestMethod.POST)
    public  String configure(@RequestParam(value="mac", required=false) String uid,
    						@RequestParam(value="key", required=false) String hashcode,
    						@RequestBody String config_data) {

    	String strValue = "Service not supported";
    	
	  	/*try {
   
	  		org.json.simple.JSONObject dev = new org.json.simple.JSONObject();
    		
    		if (config_data != null) {

    			JSONObject object 			= JSONObject.fromObject(config_data);
    	    	String node_mac 			= (String)object.get("node_mac");
    	    	
    	    	
    	    	if (node_mac != null && !node_mac.isEmpty()) {
    	    		
    	    		JSONObject devlist 		= new JSONObject();
    	    		JSONArray  dev_array 	= new JSONArray();
    	    		
    				String uuid = node_mac.replaceAll("[^a-zA-Z0-9]", "");
    				NetworkDevice nd = networkDeviceService.findOneByUuid(uuid);
    				if (nd == null) {
    					nd = networkDeviceService.findOneByUuid(uuid.toUpperCase());
    				}
    				JSONArray  tagArray = object.getJSONArray("probe_requests");
    				
    				Iterator it = tagArray.iterator();
    				
    				while(it.hasNext()) {
    					JSONObject slide =  (JSONObject)it.next();
    					
    					String peer_mac 		= (String) slide.get("mac");
        				Object count 		    = slide.get("count");
        				Object min_signal 		= slide.get("min_signal");
        				Object max_signal 		= slide.get("max_signal");
        				Object avg_signal 		= slide.get("avg_signal");
        				Object last_seen_signal = slide.get("last_seen_signal");
        				Object first_seen 		= slide.get("first_seen");
        				Object last_seen 		= slide.get("last_seen");
        				String associated 		= slide.getString("associated");
        				
        				networkDeviceRestController.probeCount(peer_mac,dev);
         				
        				devlist.put("node_mac",  		node_mac.toLowerCase());
        				devlist.put("mac_address",  	peer_mac);
        				devlist.put("count", 			count);
        				devlist.put("signal", 			max_signal);
        				devlist.put("channel", 			"NA");
        				devlist.put("max_signal", 		min_signal);
        				devlist.put("avg_signal", 		avg_signal);
        				devlist.put("last_seen_signal", last_seen_signal);
        				devlist.put("timestamp", 		first_seen);
        				devlist.put("last_seen", 		last_seen);
        				devlist.put("associated", 		associated);
        				devlist.put("devtype",          dev.getOrDefault("client_type", "0"));
        				dev_array.add(devlist);
    				}
    				
					if (dev_array != null && nd != null) {
						nd.setTagString(String.valueOf(dev_array));
						nd.setModifiedOn(new Date(System.currentTimeMillis()));
						nd.setModifiedBy("cloud");
						nd = networkDeviceService.save(nd);
					}
    				
    				
    				HashMap<String,Object> jsonMap = new HashMap<String,Object>();
        			jsonMap.put("opcode",		 	"prop_info");
        			jsonMap.put("uid",		     	node_mac.toUpperCase());
        			jsonMap.put("prop_list",     	object);        			
        			getElasticService().post(prop_event_table_index, "prop_client", jsonMap);  		
        			jsonMap.clear();        			
    			}
    	    
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}*/
    	
		return strValue;
	}
    
    @RequestMapping(value = "topology", method = RequestMethod.GET)
    public  String topology(@RequestParam("uid") String uid) {
		Device device = deviceManager.findOneByUid(uid);
		try {
			if (device != null) {
				return DeviceHelper.toJSON4D3Network(device);
			} 
		} catch (Exception e) {
			LOG.warn("Exception parsing device :" + uid, e);
		}
		return "{ \"uid\" :\"" + uid + "\" , \"status\":\"NOT_FOUND\" }";
    }
    
    
    @RequestMapping(value = "rpc", method = RequestMethod.POST)
    public String rpc(@RequestParam(value="uid", required=true) String uid,
    		          @RequestParam(value="ap", required=true) String ap,
    				  @RequestParam(value="mac", required=false) String mac,
    				  @RequestParam(value="cmd", required=true) String cmd, 
    				  @RequestParam(value="args",required=false) String[] args) {
    	
    	String ret = "SUCCESS: RPC Message Sent";
    	//LOG.info("RPC::UID" + uid);
    	//LOG.info("RPC::MAC" + mac);
    	//LOG.info("RPC::AP" + ap);

		try {
			Device device = deviceManager.findOneByUid(uid);
			
			if (device != null && !Device.STATUS.REGISTERED.name().equalsIgnoreCase(device.getStatus())) {
				
				//LOG.info("RPC Status" + device.getStatus());
				String message = MessageFormat.format(mqttMsgTemplate, new Object[]{cmd, 
																			   device.getUid(),
																			   ap,
																			   mac,
																			   "device_update"});
				if (cmd.equals("RESET")) {
					deviceManager.reset(device, true);
				} else {
					deviceEventMqttPub.publish("{" + message + "}", uid);
				}
				
				if (cmd.equals("UNBLOCK")) {
					//LOG.info("Deleted UNBLOCK MACCC" + mac);
					mac = mac.replaceAll("[^a-zA-Z0-9]", "");
					ClientDevice clientDevice = clientDeviceService.findOneByPeermac(mac);
					if (clientDevice != null) {
						clientDeviceService.delete(clientDevice.id);
					}
				}
				
				LOG.info("SUCCESS: RPC Message Sent |uid:" + uid + "|cmd:" + cmd );
				
			} else {
				ret = "FAILURE: Invalid Device";
				LOG.info("Invalid Device" );
			}
		} catch (Exception e) {
			ret = "Error: FATAL error occured";
			LOG.error("FAILURE: RPC Message Failed |uid :"+ uid + "|cmd:" + cmd , e);
			ret = "FAILURE: RPC Message Failed";
		}
    	
    	return ret;
    }
    
    
	@RequestMapping(value = "rpcQcast", method = RequestMethod.POST)
	public String rpcQcast(@RequestParam(value = "uid", required = true) String uid,
						   @RequestParam(value = "ap", required = true) String ap,
						   @RequestParam(value = "mac", required = false) String mac,
						   @RequestParam(value = "cmd", required = true) String cmd,
						   @RequestParam(value = "args", required = false) String[] args) {
		
		String ret = "SUCCESS: RPCQCAST Message Sent";
		//LOG.info("RPCQCAST::UID" + uid);
		//LOG.info("RPCQCAST::MAC" + mac);
		//LOG.info("RPCQCAST::AP" + ap);
		String qcastmqttMsgTemplate = " \"opcode\":\"{0}\", \"uid\":\"{1}\", \"by\":\"{2}\", \"newversion\":\"{3}\", \"value\":{4} ";
			
		JSONObject jsonObject = new JSONObject();
		QuberCast quber = qubercastService.findByReffId("a5a5");

		if (quber != null) {
			jsonObject.put("mediaPath", 		quber.getMediaPath());
			jsonObject.put("multicastPort", 	quber.getMulticastPort());
			jsonObject.put("mulicastAddress", 	quber.getMulicastAddress());
			jsonObject.put("totalFiles", 		quber.getLogFile());
			jsonObject.put("payLoad", 			quber.getLogLevel());
		}
					
		if (cmd.equals("KILL") || cmd.equals("REFRESH")) {
			String header = cmd.equals("KILL")?"QCAST_CLOSE":"QCAST_RESTART";
			
			Iterable<Device> devices = deviceManager.findAll();
			
			if (devices != null) {
				for (Device device : devices) {
						String msg = MessageFormat.format(qcastmqttMsgTemplate, new Object[] { header, device.getUid().toLowerCase(),
								"qubercloud", "0xFE", jsonObject.toString() });
						mqttPublisher.publish("{" + msg + "}", device.getUid().toLowerCase());
				}
			}
			
			return "OK";
		}
			

		try {
			
			Device device = deviceManager.findOneByUid(uid);

			if (device != null && !Device.STATUS.REGISTERED.name().equalsIgnoreCase(device.getStatus())) {
				
				if ( cmd.equals("QCAST")|| cmd.equals("QCLOS")|| cmd.equals("QCASTRESET")) {
					String header = cmd.equals("QCAST")?"AP_QCAST_START":"AP_QCAST_CLOSE";
					
					if (cmd.equals("QCASTRESET")) {
						header="AP_QCAST_RESET";
					}
					if (device != null) {
						String msg = MessageFormat.format(qcastmqttMsgTemplate,
								new Object[] { header, device.getUid().toLowerCase(), "qubercloud", "0xFE", jsonObject.toString() });
						mqttPublisher.publish("{" + msg + "}", device.getUid().toLowerCase());
					}
				}

				LOG.info("UID:" + uid + "|cmd:" + cmd +"jsonObject " +jsonObject);

			} else {
				ret = "FAILURE: Invalid Device";
				LOG.info("Invalid Device");
			}
		} catch (Exception e) {
			ret = "Error: FATAL error occured"+e;
		}		


		return ret;
	}
	
	@RequestMapping(value = "rpcACL", method = RequestMethod.POST)
	public String rpcACL  (@RequestParam(value = "uid",  required = true)  String uid,
						   @RequestParam(value = "ap",   required = true)  String ap,
						   @RequestParam(value = "mac",  required = false) String mac,
						   @RequestParam(value = "cmd",  required = true) String cmd,
						   @RequestParam(value = "sid",  required = false) String sid,
						   @RequestParam(value = "spid", required = false)  String spid,
						   @RequestParam(value = "cid",  required = false) String cid ) {
		
		String ret = "SUCCESS:RPCACL Message Sent";
		
		try {
			
			boolean enablelog 		= false;
			final String className  = this.getClass().getName();
			
			if (cid != null) {

				Customer customer = customerService.findById(cid);
				if (customer != null) {
					if (customer.getLogs() != null && customer.getLogs().equals("true")) {
						enablelog = true;
					}
				}
			}

			customerUtils.logs(enablelog, className, "RPCACL::UID	" + uid);
			customerUtils.logs(enablelog, className, "RPCACL::MAC	" + mac);
			customerUtils.logs(enablelog, className, "RPCACL::SID	" + sid);
			customerUtils.logs(enablelog, className, "RPCACL::SPID" + spid);
			customerUtils.logs(enablelog, className, "RPCACL::CID	" + cid);
			customerUtils.logs(enablelog, className, "RPCACL::CMD	" + cmd);
			
			String ACLMQTTMsgTemplate = " \"opcode\":\"{0}\", \"uid\":\"{1}\", \"ssid\":\"{2}\", \"peer_mac\":\"{3}\" ";
			
			String peer_mac = "";
			ClientDevice cdev = null;
			String universalId = null;
			
			if (mac != null) {
				peer_mac = mac.replaceAll("[^a-zA-Z0-9]", "");
				cdev = clientDeviceService.findOneByPeermac(peer_mac);
			}
			
			customerUtils.logs(enablelog, className,"RPCACL::peer_mac	" + peer_mac);
			
			if (cdev != null && cmd.equals("ACL")) { //UID BASED MQTT MESSAGE
				
				if (cdev.getPid().equals("Customer")) {
					universalId = cid;
				} else if (cdev.getPid().equals("Venue")) {
					universalId = sid;
				} else if (cdev.getPid().equals("Floor")) {
					universalId = spid;
				} else  {
					universalId = cdev.getUid().toLowerCase();
				}
				
				String message = MessageFormat.format(ACLMQTTMsgTemplate,new Object[] { "UNBLOCK", universalId, cdev.getSsid(), cdev.getMac()});
				mqttPublisher.publish("{" + message + "}", universalId);
				customerUtils.logs(enablelog, className," CMD " + cmd + " MQTT MESSAGE " + message);
			
				if (cmd.equals("ACL")) { // REMOVE THE UNBLOCK LIST
					clientDeviceService.delete(cdev.getId());
				}
				
				return "OK";

			}
			List<ClientDevice> clientDevList = new ArrayList<ClientDevice>();
			
			Iterable<ClientDevice> devices = clientDeviceService.findBySidAndStatus(sid, "blocked");
			
			if (devices != null) { // RESET
				for (ClientDevice device : devices) {
					if (cmd.equals("RACL") || cmd.equals("QACL")) {
						
						String policy = device.getPid();
						
						if (policy != null) {
							if (policy.equals("Customer")) {
								universalId = cid;
							} else if (policy.equals("Venue")) {
								universalId = sid;
							} else if (policy.equals("Floor")) {
								universalId = spid;
							} else {
								universalId = device.getUid().toLowerCase();
							}
						}
							
						
						String message = MessageFormat.format(ACLMQTTMsgTemplate, new Object[] { "UNBLOCK", universalId,
								device.getSsid(),  device.getMac() });
						mqttPublisher.publish("{" + message + "}", universalId);
						
						clientDevList.add(device);
						customerUtils.logs(enablelog, className,"ACL MQTT MESSAGE " + message);
					}

				}		
				if (cmd.equals("RACL") && clientDevList.size() >0) {
					clientDeviceService.delete(clientDevList);
				}
				customerUtils.logs(enablelog, className,"UID:" + uid + "|cmd:" + cmd);
			}
		} catch (Exception e) {
			LOG.info("While ACLrpc SEnd Error " ,e);
		}
		return ret;
	}
		
	@RequestMapping(value = "/cust/dev/list", method = RequestMethod.GET)
	public JSONObject list(
					@RequestParam(value = "cid", required = true) String cid,
					@RequestParam(value = "uid", required = false) String uid,
				    HttpServletRequest request) throws IOException{

		JSONObject devlist = new JSONObject();
		try {
			
			JSONObject dev 				= null;
			JSONArray dev_array 		= new JSONArray();
			List<Device> device 		= new ArrayList<Device>();
				
			if (uid == null || uid.isEmpty() || uid.equals("undefined")) {
				device = deviceManager.findByCid(cid);
			} else {
				Device dv = deviceManager.findByUidAndCid(uid, cid);
				if(dv  != null){
					device.add(dv);
				}else {
					device = deviceManager.findByCidAndAlias(cid, uid);
				}
			}
					
			if (device != null) {
				for (Device dv : device) {
						dev = new JSONObject();
						dev.put("id", 		   dv.getId());
						dev.put("mac_address", dv.getUid());
						dev.put("dev_name",    dv.getName());
						dev.put("status",      dv.getStatus());
						dev.put("state",       dv.getState().toUpperCase());
						dev.put("alias",       dv.getAlias());
						dev.put("cid",         dv.getCid());
						
						String state = dv.getState().toUpperCase();
						
						String ip  				= dv.getIp();
						String devIp = (ip == null || ip.isEmpty()) ? "0.0.0.0" : ip;
						dev.put("ip",           devIp);
						
						if (!state.equalsIgnoreCase("inactive") && !devIp.equals("0.0.0.0")) {
							dev.put("cmd_enable", "1");
						} else {
							dev.put("cmd_enable", "0");
						}
						
						if (dv.getRole() != null) {
							dev.put("ap_type", dv.getRole());
						} else {
							dev.put("ap_type", "ap");
						}
						
						dev_array.add(dev);
				}
				devlist.put("cust_dev_list", dev_array);
			}
		} catch (Exception e) {
			LOG.info("while getting customer device list error", e);
		}
		return devlist;
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public String export(@RequestParam(value = "cid", required = true) String cid,
			  			 HttpServletRequest request, HttpServletResponse response) 
			  		     throws IOException, ParseException {
		
		String pdfFileName  = "./uploads/qubexport.pdf";
		String logoFileName = "./uploads/logo-home.png";
		
		//String pdfFileName  = "C:/files/quber.pdf";
		//String logoFileName = "C:/files/2g-on.png";
		
			
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			Document document = new Document();
			try {
				FileOutputStream os = new FileOutputStream(pdfFileName);
				@SuppressWarnings("unused")
				PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
				document.open();
				Paragraph paragraph = new Paragraph();
				Image image2 = Image.getInstance(logoFileName);
				image2.scaleAbsoluteHeight(25f);//scaleAbsolute(50f, 50f);
				image2.scaleAbsoluteWidth(100f);
				paragraph.add(image2);
				paragraph.setAlignment(Element.ALIGN_LEFT);
				paragraph.add("Qubercloud Log Summary");
				paragraph.setAlignment(Element.ALIGN_CENTER);
				
				addEmptyLine(paragraph, 1);
			    
			    // Will create: Report generated by: _name, _date
			    paragraph.add(new Paragraph("Report generated by: " + System.getProperty("user.name") + ", " + new Date(), smallBold));
			    addEmptyLine(paragraph, 3);	
			    document.add(paragraph);
			    
			    document.newPage();
			    
			    addlogContent (document,cid);			    
				
				document.close();

				File pdfFile = new File(pdfFileName);
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=" + pdfFileName);
				response.setContentLength((int) pdfFile.length());
				FileInputStream fileInputStream = new FileInputStream(pdfFile);
				OutputStream responseOutputStream = response.getOutputStream();
				int bytes;
				while ((bytes = fileInputStream.read()) != -1) {
					responseOutputStream.write(bytes);
				}
				responseOutputStream.close();
				fileInputStream.close();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//return pdfFileName;
		}

		return pdfFileName;
	}
	
	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	private void addlogContent(Document document,String cid) throws DocumentException, IOException, ParseException {
		Anchor anchor = new Anchor("Qubercloud LOG Summary", catFont);
		anchor.setName("Qubercloud LOG Summary");

		// Second parameter is the number of the chapter
		Chapter catPart = new Chapter(new Paragraph(anchor), 1);

		Paragraph subPara = new Paragraph("Qubercloud LOGS", subFont);
		addEmptyLine(subPara, 1);

		Section subCatPart = catPart.addSection(subPara);

		// add a table
		createPropReqTable(subCatPart, document,cid);

		// now add all this to the document
		document.add(catPart);

	}
	
	@SuppressWarnings("unchecked")
	private void createPropReqTable(Section subCatPart, Document document,String cid)
			throws IOException, ParseException, DocumentException {
		
		PdfPTable table = new PdfPTable(4);

		PdfPCell c1 = new PdfPCell(new Phrase("PEER MAC"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("STATUS"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("STATE"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("ALIAS"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		table.setHeaderRows(1);
		
		table.setHeaderRows(1);

		JSONObject newJObject = null;
		newJObject = list(cid, null, null);

		if (newJObject != null) {
			JSONArray devicelist = (JSONArray) newJObject.get("cust_dev_list");
			//LOG.info("Export file " +devicelist.toString());
			if (devicelist != null) {
				Iterator<JSONObject> i = devicelist.iterator();
				while (i.hasNext()) {
					JSONObject slide =i.next();
					String peer_mac = (String) slide.get("mac_address");
					table.addCell(peer_mac);
					String status = (String) slide.get("status");
					table.addCell(status);
					String state = (String) slide.get("state");
					table.addCell(state);
					String alias = (String) slide.get("alias");
					table.addCell(alias);

				}
			}
		}

		subCatPart.add(table);
	}
	
	// GET DEFAULT CONFIG MESH
	@RequestMapping(value = "/meshdefaultconfig", method = RequestMethod.POST)
	public JSONObject custmeshconfig(
			@RequestParam(value = "band2g", required = false) String band2g,
			@RequestParam(value = "band5g", required = false) String band5g,
			HttpServletRequest request,	HttpServletResponse response) throws IOException {
		net.sf.json.JSONObject JSONCONF	=  null;
		try {
			String tconf = null;
			String template = "mesh_default";
			if (band2g.equals("2G") && band5g.equals("5G")) {
				template = "mesh_2G5G";
			} else if (band5g.equals("5G")) {
				template = "mesh_5G";
			} else if (band2g.equals("2G")) {
				template = "mesh_2G";
			} else {
				template = "mesh_default";
			}
			tconf = SpringComponentUtils.getApplicationMessages().getMessage("facesix.device.template."+template);
			JSONCONF= net.sf.json.JSONObject.fromObject(tconf);
			//LOG.info("MESH CONFIG  tconf " + JSONCONF);
		} catch (Exception e) {
			LOG.info("WHILE CUSTOMER MESH CONFIG ERROR {}", e);
		}
		return JSONCONF;
	}
	
		//GET DEFAULT CONFIG LEGACY
		@RequestMapping(value = "/legacydefaultconfig", method = RequestMethod.POST)
		public JSONObject custlegactest(
				@RequestParam(value = "band2g", required = false) String band2g,
				@RequestParam(value = "band5g", required = false) String band5g,
				HttpServletRequest request,	HttpServletResponse response) throws IOException {
			
			net.sf.json.JSONObject JSONCONF	=  null;
			try {
				String tconf = null;
				String template = "default";
				if (band2g.equals("2G") && band5g.equals("5G")) {
					template = "template_2G5G";
				} else if (band5g.equals("5G")) {
					template = "template_5G";
				} else if (band2g.equals("2G")) {
					template = "template_2G";
				}
				 tconf = SpringComponentUtils.getApplicationMessages().getMessage("facesix.device.template."+template);
				 JSONCONF	= net.sf.json.JSONObject.fromObject(tconf);
				 LOG.info("LEGACY CONFIG  template " + JSONCONF);
			} catch (Exception e) {
				LOG.info("WHILE CUSTOMER LEGACY CONFIG ERROR {}", e);
			}
			return JSONCONF;
		}
		
		@RequestMapping(value = "/uploadconfig", method = RequestMethod.POST)
		public JSONObject jsonFileRead(
						MultipartHttpServletRequest request,
						HttpServletRequest req,	
						HttpServletResponse res) throws IOException {
			
			net.sf.json.JSONObject JSONCONF	=  null;
			try {
				
				 Iterator<String> itrator = request.getFileNames();
			     MultipartFile multiFile = request.getFile(itrator.next());
		         
		         String content = new String(multiFile.getBytes(), "UTF-8");
		         JSONCONF	= net.sf.json.JSONObject.fromObject(content);
		        LOG.info("CONFIG  Template " + JSONCONF);
			} catch (Exception e) {
				LOG.info("WHILE FILE UPLOAD ERROR {}", e);
			}
			return JSONCONF;
		}
		
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public void delete(@RequestParam("uid") String uid,
					   @RequestParam("cid") String cid,
					   @RequestParam(value = "sid", 	required = false) String sid,
						@RequestParam(value = "spid",   required = false) String spid,
					   HttpServletRequest request,
					   HttpServletResponse response) throws Exception {

		//LOG.info("Quber SPOT UID " +uid);
		//LOG.info("Quber SPOT CID " +cid);
		
		String str = "/facesix/spots?cid="+cid+"&sid="+sid+"&spid="+spid;
		
		try {
			
			NetworkDevice nd = null;
			Device device = deviceManager.findOneByUid(uid);
			
			String[] stringArray = new String[] { "none" };
			if (device != null) {
				rpc(uid, null, null, "DELETE", stringArray);
				deviceManager.delete(device);
				device.setId(null);
			}

			String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
			nd = networkDeviceService.findOneByUuid(uuid);
			
			//LOG.info("ND " +nd);
			//LOG.info("ND UUID " +uuid);
			
			if (nd != null) {
				if (nd.getUid().equals(uid)) {
					networkDeviceService.delete(nd);
				}
			}
			response.sendRedirect(str);
		} catch (Exception e) {
			response.sendRedirect(str);
			LOG.info("While Device Delete Error " ,e);
		}
		
	}
	
	@RequestMapping(value = "/checkDuplicate", method = RequestMethod.GET)
	public  String checkDuplicate(@RequestParam("uid") String uid, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String Retresponse = "new";
		Device device = deviceManager.findOneByUid(uid);
		if (device != null && !Device.STATUS.REGISTERED.name().equalsIgnoreCase(device.getStatus()))  {
			Retresponse = "duplicate";
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
		
		String str = "/facesix/gwregdevices?cid="+cid;
	
		try {
			
			Iterable<Device> device= deviceManager.findByQuery("+status:REGISTERED", "modifiedOn", 0, 1000);	
			if (device != null) {
				for (Device dv : device) {
					if (Device.STATUS.REGISTERED.name().equalsIgnoreCase(dv.getStatus())) {
						deviceManager.delete(dv);
					}
				}
			}
			response.sendRedirect(str);
		} catch (Exception e) {
			response.sendRedirect(str);
		}
	}
	
	public ElasticService getElasticService() {
		if (elasticService == null) {
			elasticService = Application.context.getBean(ElasticService.class);
		}
		return elasticService;
	}
}