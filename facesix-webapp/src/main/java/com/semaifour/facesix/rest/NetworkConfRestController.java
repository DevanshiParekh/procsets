package com.semaifour.facesix.rest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.beacon.ScannerMqttMessageHandler;
import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.beacon.rest.BLENetworkDeviceRestController;
import com.semaifour.facesix.beacon.rest.BeaconDeviceRestController;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.ClientDevice;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.impl.qubercloud.DeviceUpdateEventHandler;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.semaifour.facesix.beacon.rest.GeoFinderRestController;

/**
 *
 * Rest Device Controller handles all rest calls for network configuration
 *
 * @author mjs
 *
 */
@RestController
@RequestMapping("/rest/site/portion/networkdevice")
public class NetworkConfRestController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(NetworkConfRestController.class.getName());

	@Autowired
	NetworkDeviceService networkDeviceService;

	@Autowired
	SiteService siteService;

	@Autowired
	PortionService portionService;

	@Autowired
	DeviceService devService;

	@Autowired
	DeviceRestController deviceRestCtrl;

	@Autowired
	NetworkDeviceRestController devcontroller;

	DeviceUpdateEventHandler devupdate;

	@Autowired
	BeaconDeviceService beaconDeviceService;

	@Autowired
	BeaconDeviceRestController beaconDeviceRestController;

	ScannerMqttMessageHandler scannerMqttMessageHandler;
	
	@Autowired
	GeoFinderRestController geoFinderRestController;
	
	@Autowired
	BLENetworkDeviceRestController bleRestController;	
	
	@Autowired
	BeaconService beaconService;	
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	QubercommScannerRestController quberScannerRestCtr;
	
	@Autowired
	CustomerUtils CustomerUtils;
	
	@Autowired
	FSqlRestController 		fsqlRestController;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	ClientDeviceService 	clientDeviceService;
	
	private String indexname = "facesix*";
	
	@PostConstruct
	public void init() {
		indexname 		= _CCC.properties.getProperty("elasticsearch.indexnamepattern", "facesix*");
	}

	@RequestMapping(value = "/byuid", method = RequestMethod.GET)
	public List<NetworkDevice> getuid(@RequestParam("uid") String uid) {
		List<NetworkDevice> list = networkDeviceService.findByUid(uid);
		return list;
	}

	@RequestMapping(value = "/byspid", method = RequestMethod.GET)
	public List<NetworkDevice> byspid(@RequestParam("spid") String spid) {
		List<NetworkDevice> list = networkDeviceService.findBySpid(spid);
		return list;
	}

	@RequestMapping(value = "/bysid", method = RequestMethod.GET)
	public List<NetworkDevice> bysid(@RequestParam("sid") String sid) {
		List<NetworkDevice> list = networkDeviceService.findBySid(sid);
		return list;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Iterable<NetworkDevice> list(@RequestParam("spid") String spid) {
		List<NetworkDevice> list = networkDeviceService.findBySpid(spid);
		return list;
	}

	@RequestMapping(value = "/finder/list", method = RequestMethod.GET)
	public JSONObject finderList(@RequestParam(value="cid",		required=false) String cid,
								 @RequestParam(value="spid",	required=false) String spid,
								 @RequestParam(value="macaddr", required=false) String macaddr,
								 @RequestParam(value="param",	required=false) String param,
								 @RequestParam(value= "time", required= false) Long time) throws ParseException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String classname = this.getClass().getName();
		long st = System.currentTimeMillis();
		List<NetworkDevice> list = networkDeviceService.findBySpid(spid);
		boolean getlogs = false;
		boolean enableLogs = false;
		JSONObject  info 		= new JSONObject();
		JSONObject  jsonObj 	= null;
		JSONArray   array 		= new JSONArray();
		Object      tagCount 	= "0";
		JSONArray   tagList  	= new JSONArray();
		
		try {

			if (param != null && param.equals("1")) {
				tagList  = taginfo(null, macaddr);
				tagCount = tagList.size();
			}
						
			for (NetworkDevice nd : list) {
				cid = nd.cid;
				if(!getlogs){
					Customer cx = customerService.findById(cid);
					enableLogs = cx.getLogs().equals("true");
					String zone = cx.getTimezone();
					TimeZone timezone = customerUtils.FetchTimeZone(zone);
					format.setTimeZone(timezone);
				}
				getlogs = true;
				jsonObj= new  JSONObject ();
				jsonObj.put("parent", 	nd.parent);
				jsonObj.put("typefs", 	nd.getTypefs());
				jsonObj.put("status", 	nd.getStatus());
				jsonObj.put("uid",    	nd.getUid());
				jsonObj.put("xposition",nd.xposition);
				jsonObj.put("yposition",nd.yposition);
				jsonObj.put("sid", 		nd.sid);
				jsonObj.put("spid", 	nd.spid);
				jsonObj.put("cid", 		nd.cid);
				jsonObj.put("uuid", 	nd.uuid);
				jsonObj.put("bleType",  nd.bleType.toUpperCase());
				jsonObj.put("alias",    nd.alias.toUpperCase());
				array.add(jsonObj);
			}
		
			info.put("list", array);
			info.put("taglist", tagList);
			info.put("tagcount", tagCount);

		}catch (Exception e) {
			LOG.info("while device list getting error ",e);
		}
		if(time != null){
			long receiveTime = st - time;
			customerUtils.logs(enableLogs, classname," Time taken to receive request = "+receiveTime+" milliseconds");
		}
		long endTime =System.currentTimeMillis();
		long el = endTime - st;
		String compTime = format.format(new Date());
		long endtime = format.parse(compTime).getTime();
		customerUtils.logs(enableLogs, classname," Time taken to give response = "+el+" milliseconds");
		info.put("endTime", endtime);
		return info;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/heatMapDeviceList", method = RequestMethod.GET)
	public JSONObject heatMap(
			 @RequestParam(value="cid",	required=false) String cid,
			 @RequestParam(value="sid",	required=false) String sid,
			 @RequestParam(value="spid",required=false) String spid,
			 @RequestParam(value="uid",	required=false) String uid) {
		
		List<NetworkDevice> list 				= null;
		JSONObject  info 						= null;
		JSONObject  jsonObj 					= null;
		JSONArray   array 						= null;
		org.json.simple.JSONObject probObj 		= null;
		org.json.simple.JSONArray probArray 	= null;
		Hashtable<String, Integer> map 			= null;
		JSONArray  devType_array 				= new JSONArray();
		
		info 	= new JSONObject();
		array 	= new JSONArray();
		map 	= new Hashtable<String, Integer>();
		
		String customerId 		= "";
		int devCount   			= 0;
		long associateCount  	= 0;
		int dups 				= 0;	
		int android 			= 0;
		int windows 			= 0;
		int ios 				= 0;
		int	speaker 			= 0;
		int	printer 			= 0;
		int others				= 0;
		long totCount			= 0;
		
		
		int _2G = 0;
		int _5G = 0;
		
		
		try {
			
			if(spid != null && !spid.isEmpty()){
				list = networkDeviceService.findBySpid(spid);
			}else if( sid != null && !sid.isEmpty()){
				list = networkDeviceService.findBySid(sid);
			} else if (uid != null && !uid.isEmpty()) {
				String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
				list = networkDeviceService.findByUuid(uuid);
			} else {
				list = networkDeviceService.findByCid(cid);
			}
			
			for (NetworkDevice nd : list) {
				jsonObj= new  JSONObject ();
				
				customerId = nd.getCid();
				
				jsonObj.put("parent", 	nd.parent);
				jsonObj.put("typefs", 	nd.getTypefs());
				jsonObj.put("status", 	nd.getStatus());
				jsonObj.put("uid",    	nd.getUid());
				jsonObj.put("xposition",nd.xposition);
				jsonObj.put("yposition",nd.yposition);
				jsonObj.put("sid", 		nd.sid);
				jsonObj.put("spid", 	nd.spid);
				jsonObj.put("cid", 		customerId);
				jsonObj.put("uuid", 	nd.uuid);
				jsonObj.put("bletype",  nd.bleType);
				
				
				probArray = new org.json.simple.JSONArray();
				
				if (nd.getTypefs() != null && nd.getTypefs().equalsIgnoreCase("ap")) {
					
					devCount ++;
					String devUid = nd.getUid();
					
					String time = "5m";
					org.json.simple.JSONObject object  = quberScannerRestCtr.probe_req_stats(devUid, time, null, null, null);
					
					if (object != null && object.containsKey("probe_req_stats")) {
					
					org.json.simple.JSONArray valObject  = (org.json.simple.JSONArray)object.get("probe_req_stats");
						
					Iterator<org.json.simple.JSONObject> iter = valObject.iterator();
					// Heatmap prob Count
					while(iter.hasNext()) {
						
						org.json.simple.JSONObject prob = iter.next();
						
						String probMac  = (String) prob.get("mac_address");
						
						
						if (map.containsKey(probMac)) {
							dups ++;
							int dupsCount = Integer.parseInt(String.valueOf(map.get(probMac))) + 1;
							map.put(probMac, dupsCount);
							continue;
						} else {

							map.put(probMac, 0);
							
							probObj = new org.json.simple.JSONObject();
							
							String channel			= (String)prob.getOrDefault("channel", "NA");
							String assoc		    = (String)prob.getOrDefault("associated", "NA");
							Object signal		    = (Object)prob.getOrDefault("signal", 0);
							String devtype		    = (String)prob.getOrDefault("devtype","0");
							
							if (devtype.equals("mac")) {
								ios++;
							} else if (devtype.equals("android")) {
								android++;
							} else if (devtype.equals("speaker")) {
								speaker++;
							} else if (devtype.equals("printer")) {
								printer++;
							} else if (devtype.equals("windows")) {
								windows++;
							} else if (devtype.equals("laptop")) {
								others++;
							}
							
							probObj.put("node_mac",		devUid);
							probObj.put("mac_address", 	probMac);
							probObj.put("channel", 		channel);
							probObj.put("associated", 	assoc);
							probObj.put("signal", 		signal);
							probObj.put("devtype", 		devtype);
							
							probArray.add(probObj);

							if (assoc != null && assoc.equals("true")) {
								associateCount++;
							}
						}
					}
				}
						
					String clientstatus = "active";
					String uuid         = nd.getUuid();
					
					List<ClientDevice> clientDev = getClientDeviceService().findByUuidAndStatus(uuid, clientstatus);
					
					if (clientDev != null) {
						for (ClientDevice dev : clientDev) {
		
							String devType 		= dev.getTypefs();
							String radioType 	= dev.getRadio_type() == null ? "2.4Ghz" : dev.getRadio_type();
		    				String ssid      	= dev.getSsid() == null ? "UNKOWN" : dev.getSsid() ;
		    				
							probObj = new org.json.simple.JSONObject();

							probObj.put("node_mac", 		devUid);
							probObj.put("mac_address", 		dev.getMac());
							probObj.put("radio", 			radioType);
							probObj.put("channel", 			"NA");
							probObj.put("associated", 		"Yes");
							probObj.put("signal", 			"NA");
							probObj.put("devtype", 			devType);
							probObj.put("ssid",      		ssid);

							switch (devType) {
							case "android":
								android++;
								break;
							case "mac":
								ios++;
								break;
							case "speaker":
								speaker++;
								break;
							case "printer":
								printer++;
								break;
							case "windows":
								windows++;
								break;
							default:
								others++;
							}

							if (radioType.equals("2.4Ghz")) {
								_2G++;
							} else {
								_5G++;
							}

							probArray.add(probObj);
						}
					}
				}
				jsonObj.put("heatmap", probArray);
				array.add(jsonObj);
			}
			
			
			DateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
			
			final Date currentTime  = new Date();
			Customer customer 		= customerService.findById(customerId);
			
			if (customer != null) {
				String zone = customer.getTimezone();
				if (zone != null && !zone.isEmpty()) {
					sdf.setTimeZone(TimeZone.getTimeZone(zone));
				}
			}
			
			String cur_date =  sdf.format(currentTime);
			
			JSONArray  dev_array1 =  new JSONArray();
			JSONArray  dev_array2 =  new JSONArray();
			JSONArray  dev_array3 =  new JSONArray() ;
			JSONArray  dev_array4 =  new JSONArray();		
			JSONArray  dev_array5 =  new JSONArray();
			
			JSONArray  speaker_array =  new JSONArray();		
			JSONArray  printer_array =  new JSONArray();
			
			dev_array1.add(0, "Mac");
			dev_array1.add(1, ios);
			
			dev_array2.add(0,"Android");
			dev_array2.add(1,android);
			
			dev_array3.add(0,"Win");
			dev_array3.add(1,windows);
	
			speaker_array.add(0,"Speaker");
			speaker_array.add(1,speaker);
			
			printer_array.add(0,"Printer");
			printer_array.add(1, printer);
			
			dev_array4.add(0,"Others");
			dev_array4.add(1,others);
			
			int total = ios + android + windows + speaker +printer + others;
					
			dev_array5.add(0,"Total");
			dev_array5.add(1,total);
			
			totCount = _2G + _5G;
			
			if (associateCount == 0) {
				associateCount = totCount;
			}
			
			devType_array.add(0, dev_array1);
			devType_array.add(1, dev_array2);
			devType_array.add(2, dev_array3);
			devType_array.add(3, speaker_array);
			devType_array.add(4, printer_array);
			devType_array.add(5, dev_array4);
			devType_array.add(6, dev_array5);
			
			JSONArray  chartsArray      =  new JSONArray();
			
			chartsArray.add(0, "Chart Details");
			chartsArray.add(1, total);
			chartsArray.add(2, cur_date);
			
			info.put("list", 		array);
			info.put("probCount", 	total);
			info.put("dupsCount",	dups);
			info.put("assCount", 	associateCount);
			info.put("devCount", 	devCount);
			info.put("devType",     devType_array);
			info.put("chartDetails",chartsArray);	
		
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("while heatMapDeviceList getting error ", e);
		}

		return info;
	}
	
	
	@RequestMapping(value = "taginfo", method = RequestMethod.GET)
	public JSONArray taginfo(
							@RequestParam(value="spid" ,   required=false) String spid,
							@RequestParam(value="macaddr" ,required=false) String macaddr) {
		
		JSONArray   array 		= new JSONArray();
		
		try {
			
			String status 	= "checkedout";
		
			List<Beacon> beacon = null;
			if (null == spid) {
				beacon = beaconService.getSavedBeaconByMacaddrAndStatus(macaddr, status);
			} else {
				beacon = beaconService.getSavedBeaconBySpidAndStatus(spid, status);
			}
						
			JSONObject jsonObj = null;
			
			String x 		= "";
			String y 		= "";
			String state    = "";
			
			if (beacon != null) {
				for (Beacon b : beacon) {
					//status = b.getStatus();
					x      = b.getX();
					y      = b.getY();
					state  = b.getState();
					
					jsonObj      = new  JSONObject ();
					String color = 	beaconDeviceService.updateTagType(b.getTag_type());
						
					jsonObj.put("macaddr",      b.getMacaddr());
					jsonObj.put("floor",    	b.getLocation());
					jsonObj.put("x", 			x);
					jsonObj.put("y", 			y);
					jsonObj.put("state",        state);
					jsonObj.put("assignedto", 	b.getAssignedTo());
					jsonObj.put("tagType", 		b.getTagType()); //doctor or male or female						
					jsonObj.put("tagtype", 		color);
					jsonObj.put("client_type",  "tag");
					jsonObj.put("distance",		b.getDistance());
					jsonObj.put("reciverId",    b.getReciverinfo());
					jsonObj.put("location",     b.getReciveralias());
					jsonObj.put("spid", 		b.getSpid());
					jsonObj.put("height",  		b.getHeight());
					jsonObj.put("width",  		b.getWidth());
					jsonObj.put("lastSeen",  	b.getLastSeen());
					jsonObj.put("lastReportingTime", b.getLastReportingTime());
					array.add(jsonObj);	
				}
			}
			
			//LOG.info("tag array " +array);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return array;
	}
	
	@RequestMapping(value = "/personinfo", method = RequestMethod.GET)
	public List<Beacon>  personinfo(
							@RequestParam(value="spid" ,   required=false) String spid,
							@RequestParam(value="macaddr" ,required=false) String macaddr,
							@RequestParam(value = "time", required = false) Long time) {
		long stTime = System.currentTimeMillis();
		if(time != null){
			long curTime = System.currentTimeMillis() - time;
		}
		List<Beacon> beacon = null;
		String status = "checkedout";

		if (null == spid) {
			beacon = beaconService.getSavedBeaconByMacaddrAndStatus(macaddr, status);
		} else {
			beacon = beaconService.getSavedBeaconBySpidAndStatus(spid, status);
		}
		// LOG.info("TAG INFO ---------- " +beacon);
		long elapsedTime = System.currentTimeMillis() - stTime;
		LOG.info(" Person Info elapsed time "+elapsedTime);
		return beacon;
	}
	
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public JSONObject view(@RequestParam("sid") String sid) throws IOException {

		JSONObject venue 	 = new JSONObject();

		try {
			List<NetworkDevice> ulist  = networkDeviceService.findBySid(sid);
			List<NetworkDevice> mlist  = Collections.unmodifiableList(ulist);
			List<NetworkDevice> list   = new ArrayList<NetworkDevice>(mlist);

			Collections.sort(list);

			Iterator<NetworkDevice> iterator = list.iterator();


			JSONArray  flr_array = new JSONArray();
			JSONObject floor	 = null;
			JSONObject flrdev	 = null;
			JSONArray  dev_array = null;
			JSONObject dev 		 = null;

			String prev_spid	= "";
			String prev_sid		= "";

			while (iterator.hasNext()) {

				NetworkDevice nd = iterator.next();

				if (prev_sid.equals(nd.sid) == false) {
					Site site = siteService.findById(nd.sid);
					if (site != null) {
						venue.put("name", site.getUid());
					} else {
						venue.put("name", "venue");
					}
					venue.put("sid", nd.sid);
					prev_sid = sid;
				}

				if (prev_spid.equals(nd.spid) == false) {

					if (prev_spid.length() > 0) {
						flr_array.add(floor.toString());
					}
					Portion port = portionService.findById(nd.spid);
					floor  = new JSONObject();
					flrdev = new JSONObject();

					if (port!= null) {
						flrdev.put("name", port.getUid());
					} else {
						flrdev.put("name", "floor");
					}
					flrdev.put("spid", nd.spid);
					prev_spid = nd.spid;

					dev_array = new JSONArray();
					dev 	  = new JSONObject();
				}

				dev.put("id", 			nd.id);
				dev.put("uid", 			nd.getUid());
				dev.put("typefs", 		nd.getTypefs());
				dev.put("band2g", 		nd.band2g);
				dev.put("band5g", 		nd.band5g);
				dev.put("guest", 		nd.guest);
				dev.put("vap", 			nd.vapcount);
				dev.put("alias", 		nd.alias);
				dev.put("pssid", 		nd.pssid);
				dev.put("parent", 		nd.parent);
				dev.put("xposition", 	nd.xposition);
				dev.put("yposition", 	nd.yposition);
				dev.put("spid", 		nd.spid);
				dev.put("sid", 			nd.sid);

				dev.put("ch2g", 		nd.ch2g);
				dev.put("ch5g", 		nd.ch5g);
				dev.put("tag", 			nd.activetag);

				dev.put("status", nd.getStatus());

				dev_array.add(dev.toString());
				flrdev.put("devices", dev_array);
				floor.put("floor", flrdev);
			}

			if (floor != null) {
				flr_array.add(floor.toString());
				venue.put("floors", flr_array);
			}

		} catch (Exception e) {
			e.printStackTrace();
			//LOG.info("NetworkDevice Venue Dashboard Error :" + e.printStackTrace());
		}


		return venue;
	}

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public Iterable<NetworkDevice> query(@RequestParam("spid") String spid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		Iterable<NetworkDevice> list = networkDeviceService.findByQuery(spid, type, status, sort, page, size);
		return list;
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public NetworkDevice confGet(@RequestParam("spid") String spid, @RequestParam("uid") String uid) {
		NetworkDevice nd = networkDeviceService.findOneByUid(uid);
		return nd;
	}

	@RequestMapping(value = "/getid", method = RequestMethod.GET)
	public NetworkDevice getid(@RequestParam("id") String id) {
		NetworkDevice nd = networkDeviceService.findById(id);
		return nd;
	}

	public void deletesid (String sid) {
		NetworkDevice nd = null;
		List<NetworkDevice> list = networkDeviceService.findBySid(sid);
		Iterator<NetworkDevice> iterator = list.iterator();

		while (iterator.hasNext()) {
			nd = iterator.next();
			if (nd != null) {
				networkDeviceService.delete (nd);
			}
		}
		networkDeviceService.venue_device_count = 0;
	}

	public void deletespid (String spid) {
		NetworkDevice nd = null;
		List<NetworkDevice> list = networkDeviceService.findBySpid(spid);
		Iterator<NetworkDevice> iterator = list.iterator();

		while (iterator.hasNext()) {
			nd = iterator.next();
			if (nd != null) {
				networkDeviceService.delete (nd);
				if (networkDeviceService.venue_device_count > 0) {
					networkDeviceService.venue_device_count--;
				} else {
					networkDeviceService.venue_device_count = 0;
				}

			}
		}
	}

	/**
	 * Delete NetworkDevice by its 'id'.
	 *
	 * @param id
	 * @return
	 */

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(@RequestParam("spid") String spid, @RequestParam("uid") String uid ,@RequestParam("type") String type ) throws Exception {

		NetworkDevice nd = null;
		String cid = "";
		String bleType = null;
		
		//LOG.info( " type " + type + " uid " + uid + " sid " +spid);

		Device device = getDeviceService().findOneByUid(uid);
		String[] stringArray = new String[]{"none"};
		if (device != null) {
			deviceRestCtrl.rpc(uid,null,null, "DELETE", stringArray);
			devService.delete (device);
			device.setId (null);
		}
		if (type.equals("sensor") || type.equals("server")) {

			String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
			nd = networkDeviceService.findOneByUuid(uuid);
			if (nd != null) {
				if (nd.getUid().equals(uid)) {
					networkDeviceService.delete(nd);
				}
			}
			BeaconDevice beacon = getBeaconDeviceService().findOneByUid(uid);
			if (beacon != null) {
				cid 	= beacon.getCid();
				bleType = beacon.getType();
				beaconDeviceService.resetServerIP(bleType, cid);				
				getBeaconDeviceService().delete(beacon);
				beacon.setId(null);
			}
			
		}

		//LOG.info("cid " +cid +" bletype" +bleType);
		
	List<NetworkDevice> ndList = networkDeviceService.findBySpid(spid);
	
	if (type.equals("ap")) {
		
		for (NetworkDevice networkDevice : ndList) { // AP Child BLE Device delete 
			String parent = networkDevice.parent;
				if (parent.equals(uid)) { //
					
					String u_uid = networkDevice.getUid();
					networkDeviceService.delete(networkDevice);
					
					BeaconDevice beacon = getBeaconDeviceService().findOneByUid(u_uid);
					getBeaconDeviceService().delete(beacon);
					beacon.setId(null);
			}
		}
	}
	
		List<NetworkDevice> list = networkDeviceService.findBySpid(spid);
		Iterator<NetworkDevice> iterator = list.iterator();
		String id = uid;
		if (!type.equals("ap")) {
			id  = uid.replaceAll("[^a-zA-Z0-9]", "");
		}

		while (iterator.hasNext()) {
			nd = iterator.next();
			String nd_uid  = nd.getUid();
			String nd_svid = nd.svid;
			String nd_swid = nd.swid;
			if (type.equals("ap")) {
				if (nd_uid.equals(uid)) {
					networkDeviceService.delete (nd);
					return;
				}

			}
			{
				if (nd_svid.equals(id) || nd_swid.equals(id)) {
					device = getDeviceService().findOneByUid(nd_uid);
					if (device != null) {
						deviceRestCtrl.rpc(nd_uid,null,null, "DELETE", stringArray);
						devService.delete (device);
						device.setId (null);
					}
					
					
					BeaconDevice beacon = getBeaconDeviceService().findOneByUid(nd_uid);
					if (beacon != null) {
						//LOG.info( " finder beacon device  " +beacon);
						getBeaconDeviceService().delete(beacon);
						beacon.setId(null);
						//LOG.info( " finder u_uid " +nd_uid);
					}			
					networkDeviceService.delete (nd);
				}
			}
		}

	}

	@RequestMapping(value = "/delall", method = RequestMethod.GET)
	public void delall() {
		networkDeviceService.deleteAll();
		networkDeviceService.venue_device_count = 0;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public NetworkDevice update(@RequestBody String newfso) throws Exception {
		JSONObject json 	= JSONObject.fromObject(newfso);
		String spid 		= (String) json.get("spid");
		String xposition 	= (String) json.get("xposition");
		String yposition 	= (String) json.get("yposition");
		String uid 			= (String) json.get("uid");
		String type         = (String) json.get("type");
		String parent       = (String) json.get("parent");
		
		//LOG.info("json " +json.toString());
		
		//LOG.info(" uid " + uid + " type " + type + " parent " + parent);
		
		if (type == null || type.isEmpty() || type.contains("undefinded"))
			type = "NA";
		if (parent == null || parent.isEmpty() || parent.contains("undefinded"))
			parent = "NA";
		
		try {

			if ( parent.equalsIgnoreCase("ble") || type.equalsIgnoreCase("sensor")) {
				BeaconDevice beacondevice = null;
				beacondevice = getBeaconDeviceService().findOneByUid(uid);
				if (beacondevice != null) {
					geoFinderRestController.Pixel2Coordinate(spid, uid, xposition, yposition);
				}
			} else {
				//LOG.info("Reposition type " + type);
			}

		} catch (Exception e) {
			LOG.info("while reposition error ", e);
		}
		
		Portion portion = portionService.findById(spid);
		
		NetworkDevice nd = null;
		
		if (portion != null) {
			String cid = portion.getCid();
			String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");

			 nd = networkDeviceService.findOneByUuid(uuid);
			if (nd != null) {
				nd.setXposition(xposition);
				nd.setYposition(yposition);
				nd.setModifiedOn(new Date(System.currentTimeMillis()));
				nd.setModifiedBy("cloud");
				nd.setSid(portion.getSiteId());
				nd.setSpid(portion.getId());
				nd.setCid(cid);
				nd = networkDeviceService.save(nd);
			}
		}

		return nd;
	}


	public boolean updatestate(Device dv, NetworkDeviceService netdv) throws Exception {

		String uid = dv.getUid();

		String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");

		if (netdv != null) {
			NetworkDevice nd = netdv.findOneByUuid(uuid);

			if (nd != null) {
				nd.setStatus(dv.getState());
				nd.setRole (dv.getRole());
				nd.setModifiedOn(new Date(System.currentTimeMillis()));
				nd.setModifiedBy("cloud");
				nd = netdv.save(nd);

				// update the status of AP's parent
				nd = netdv.findOneByUuid(nd.swid);
				if (nd != null && nd.getStatus().equals("inactive")) {
					nd.setStatus("active");
					nd.setModifiedOn(new Date(System.currentTimeMillis()));
					nd.setModifiedBy("cloud");
					nd = netdv.save(nd);

					// update the status of AP's G-Parent
					nd = netdv.findOneByUuid(nd.svid);
					if (nd != null && nd.getStatus().equals("inactive")) {
						nd.setStatus("active");
						nd.setModifiedOn(new Date(System.currentTimeMillis()));
						nd.setModifiedBy("cloud");
						nd = netdv.save(nd);
					}
				}

				return true;
			}
		}

		return false;
	}

	public boolean updateblestate(BeaconDevice dv, NetworkDeviceService netdv) throws Exception {

		String uid 	= dv.getUid();

		String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");

		if (netdv != null) {
			NetworkDevice nd = netdv.findOneByUuid(uuid);

			if (nd != null) {
				nd.setStatus(dv.getState());
				nd.setModifiedOn(new Date(System.currentTimeMillis()));
				nd.setModifiedBy("cloud");
				nd = netdv.save(nd);
			}

			if (nd != null && nd.parent != null) {

				String pid = nd.parent.replaceAll("[^a-zA-Z0-9]", "");

				if (pid != null) {
					nd = netdv.findOneByUuid(pid);
				}

				if (nd != null) {
					nd.setStatus("active");
					nd.setModifiedOn(new Date(System.currentTimeMillis()));
					nd.setModifiedBy("cloud");
					nd = netdv.save(nd);
				}

				// update the status of AP's parent
				nd = netdv.findOneByUuid(nd.swid);
				if (nd != null && nd.getStatus().equals("inactive")) {
					nd.setStatus("active");
					nd.setModifiedOn(new Date(System.currentTimeMillis()));
					nd.setModifiedBy("cloud");
					nd = netdv.save(nd);

					// update the status of AP's G-Parent
					nd = netdv.findOneByUuid(nd.svid);
					if (nd != null && nd.getStatus().equals("inactive")) {
						nd.setStatus("active");
						nd.setModifiedOn(new Date(System.currentTimeMillis()));
						nd.setModifiedBy("cloud");
						nd = netdv.save(nd);
					}
				}

				return true;
			}
		}

		return false;
	}

	public boolean updatebletag(BeaconDevice dv, NetworkDeviceService netdv) throws Exception {

		String uid 	= dv.getUid();

		String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");

		if (netdv != null) {
			NetworkDevice nd = netdv.findOneByUuid(uuid);

			if (nd != null) {

				nd.setStatus(dv.getState());
				nd.setActivetag (dv.getActivetag());
				nd.setTagString (dv.getTagjsonstring());

				nd.setCheckedintag(dv.getCheckedintag());
				nd.setCheckedoutTag(dv.getCheckedoutTag());
				nd.setExitTag(dv.getExitTag());
				nd.setPersontype(dv.getPersonType());

				nd.setModifiedOn(new Date(System.currentTimeMillis()));
				nd.setModifiedBy("cloud");
				nd = netdv.save(nd);

				nd.setModifiedOn(new Date(System.currentTimeMillis()));
				nd.setModifiedBy("cloud");
				nd = netdv.save(nd);

				return true;
			}
		}

		return false;
	}


	// FORM ACTION VENUE/FLOOR/CUSTOMER CONFIG
	@RequestMapping(value = "/venueAndFloorDeviceConfig", method = RequestMethod.POST)
	public void  venueAndFloorDeviceConfig(@RequestParam(value = "sid", required = false) String  sid,
										   @RequestParam(value = "spid", required = false) String spid,
										   @RequestParam(value = "cid", required = false) String cid,
										   @RequestParam(value = "flag", required = true) String flag,
										   @RequestParam(value = "conf", required =true) String conf,
										   HttpServletRequest request, HttpServletResponse response) throws IOException{

		String redirectionUrl="/facesix/qubercloud/cloud/home";
		try {

			//LOG.info("VENUE&FLOOR CONFIG FLAG " + flag);
			//LOG.info("VENUE&FLOOR CONFIG JSON " + conf);
			//LOG.info("VENUE&FLOOR CONFIG SID " + sid);
			//LOG.info("VENUE&FLOOR CONFIG SPID " + spid);
			//LOG.info("VENUE&FLOOR CONFIG CID " + cid);

			String i_id=null;
			String nameFlag = null;

			List<NetworkDevice> networkDevice = null;

			if (sid != null && !sid.isEmpty() && flag.equals("Venue")) {
				networkDevice = networkDeviceService.findBySid(sid);
				i_id = sid;
				nameFlag="sid";
			}
			if (spid != null && !spid.isEmpty() && flag.equals("Floor")) {
				networkDevice = networkDeviceService.findBySpid(spid);
				i_id = spid;
				nameFlag="spid";
			}
			if (cid != null && !cid.isEmpty() && flag.equals("Customer")) {
				networkDevice = networkDeviceService.findByCid(cid);
				i_id = cid;
				nameFlag="cid";
			}

			//LOG.info("Configuration Flag " + nameFlag);

			// Universal MQTT Message (Venue,Floor,Customer)
			getDeviceService().universalMQTT(conf,i_id,nameFlag,true);

			if (networkDevice != null) {
				for (NetworkDevice nd : networkDevice) {
					if (nd != null) {
						this.updateVenuefloorDevice(conf, nd.getUid());
					}
				}
			}

			if (flag.equals("Customer")) {
				redirectionUrl = "/facesix/qubercloud/cloud/home";
			}
			if (flag.equals("Venue")) {
				redirectionUrl = "/facesix/web/site/sitelist?sid=" + sid;
			}
			if (flag.equals("Floor")) {
				redirectionUrl = "/facesix/web/site/portion/nwcfg?sid=" + sid + "&spid=" + spid + "&uid=?";
			}

			response.sendRedirect(redirectionUrl);

		} catch (Exception e) {
			response.sendRedirect(redirectionUrl);
			LOG.info("While venueAndFloorDeviceConfig error {}",e);
		}
	}

	public NetworkDevice updateVenuefloorDevice(String deviceJson,String uid) throws Exception {

		String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
		NetworkDevice nd  = networkDeviceService.findOneByUuid(uuid);

		try {
			net.sf.json.JSONObject template	= net.sf.json.JSONObject.fromObject(deviceJson);
			Device device = null;
			device = getDeviceService().findOneByUid(uid);
			if (null != nd && device != null) {
					device.setStatus(Device.STATUS.CONFIGURED.name());
					device.setTemplate(template.toString()); // JSON Template
					device.setConf(template.toString());
					device.setModifiedBy("Cloud");
					device.setModifiedOn(new Date(System.currentTimeMillis()));
					device.setVer(device.getVer() + 1);

					getDeviceService().saveAndSendMqtt(device,false);

					nd.setModifiedBy("Cloud");
					nd.setModifiedOn(new Date(System.currentTimeMillis()));
					nd = networkDeviceService.save(nd);
					//LOG.info("Venue Floor Customer config " + device.getUid());
				}
		} catch (Exception e) {
			LOG.info("networkconfig updation error ,", e);
			e.printStackTrace();
		}

		return nd;
	}


	@RequestMapping(value = "/blesave", method = RequestMethod.POST)
	public void ble(
			@RequestParam(value = "uuid", required = true) String uid,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = true) String cid,
			@RequestParam(value = "conf", required = true) String conf,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "param", required = false) String param,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		String str ="/facesix/web/finder/device/list?cid="+cid+"&sid="+sid+"&spid="+spid;
		
		if (SessionUtil.isAuthorized(request.getSession())){
			
			if (param.equals("DeviceConfig")) {
				str ="/facesix/web/finder/device/list?cid="+cid+"&sid="+sid+"&spid="+spid;
			} else {
				str = "/facesix/web/site/portion/nwcfg?sid="+sid+"&spid="+spid+"&uid=?&cid=" + cid;
			}
			
			String cur_user = SessionUtil.currentUser(request.getSession());
			LOG.info("Param " +param );
			LOG.info("URL  " +str );
			try{

				String bleType 			 = bleType(conf,"type");
				String keepAliveInterval = bleType(conf,"keepaliveinterval");
				String tlu 				 = bleType(conf,"tluinterval");
				double tluinterval		 = Double.parseDouble(tlu);
				
				net.sf.json.JSONObject template = net.sf.json.JSONObject.fromObject(conf);

				BeaconDevice beacondevice = null;
				NetworkDevice nd = null;
				beacondevice = getBeaconDeviceService().findOneByUid(uid);

				if (param.equals("DeviceConfig") || (spid == null || spid.trim().isEmpty())) { 
					if (beacondevice == null) {
						LOG.info("beacondevice save >>>>");
						beacondevice = new BeaconDevice();
						beacondevice.setCreatedBy(cur_user);
						beacondevice.setUid(uid);
						beacondevice.setCid(cid);
						beacondevice.setAlias(name);
						beacondevice.setName(name);
						beacondevice.setFstype("sensor");
						beacondevice.setStatus(BeaconDevice.STATUS.AUTOCONFIGURED.name());
						beacondevice.setState("inactive");
						beacondevice.setTemplate(template.toString());
						beacondevice.setConf(template.toString());
						beacondevice.setModifiedBy(cur_user);
						beacondevice.setType(bleType);
						beacondevice.setIp("0.0.0.0");
						beacondevice.setKeepAliveInterval(keepAliveInterval);
						beacondevice.setTlu(tluinterval);
						beacondevice = getBeaconDeviceService().save(beacondevice, true);
					} else {
						
						LOG.info("beacondevice update>>>>>>");
						
						String isConfigured = beacondevice.getStatus();
						if (!isConfigured.equalsIgnoreCase("CONFIGURED")) {
								beacondevice.setCid(cid);
						}
						
						beacondevice.setStatus(BeaconDevice.STATUS.CONFIGURED.name());
						beacondevice.setAlias(name);
						beacondevice.setName(name);
						beacondevice.setTemplate(template.toString());
						beacondevice.setConf(template.toString());
						beacondevice.setType(bleType); //BLE type scanner or receiver or server
						beacondevice.setKeepAliveInterval(keepAliveInterval);
						beacondevice.setTlu(tluinterval);
						beacondevice.setModifiedBy(cur_user);
						beacondevice.setModifiedOn(new Date(System.currentTimeMillis()));
						beacondevice = getBeaconDeviceService().save(beacondevice, true);
						
						String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
						nd 			= networkDeviceService.findOneByUuid(uuid);	
						if (nd != null) {
							nd.setBleType(bleType);
							nd.setAlias(name);
							nd.setModifiedBy(cur_user);
							nd.setModifiedOn(new Date(System.currentTimeMillis()));
							nd = networkDeviceService.save(nd);
						}
						
					}
				} else  if (sid != null && !sid.trim().isEmpty()
						&& spid != null && !spid.trim().isEmpty()
						&& !uid.trim().isEmpty() && uid != null
						&& cid != null && !cid.trim().isEmpty()) {

					String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
					nd 			= networkDeviceService.findOneByUuid(uuid);				
					
					if (beacondevice != null && nd != null) {  // FLOORT Sesnor UPDATE BeaconDevice & NETWORKDEVICE
						//LOG.info("Update beacondevice And NetworkDevice>>>>>>");
						beacondevice.setStatus(BeaconDevice.STATUS.CONFIGURED.name());
						beacondevice.setAlias(name);
						beacondevice.setName(name);
						beacondevice.setTemplate(template.toString());
						beacondevice.setConf(template.toString());
						beacondevice.setType(bleType); //BLE type scanner or receiver or server
						beacondevice.setKeepAliveInterval(keepAliveInterval);
						beacondevice.setTlu(tluinterval);
						
						if (beacondevice.getSid() == null || beacondevice.getSid().isEmpty()) {
							beacondevice.setSid(sid);
						}
						if (beacondevice.getSpid() == null || beacondevice.getSpid().isEmpty()) {
							beacondevice.setSpid(spid);
						}
						
						beacondevice.setModifiedBy(cur_user);
						beacondevice.setModifiedOn(new Date(System.currentTimeMillis()));
						getBeaconDeviceService().save(beacondevice,true);

						if (nd != null) {
							nd.setBleType(bleType);
							nd.setAlias(name);
							
							if (nd.getSid() == null || nd.getSid().isEmpty()) {
								nd.setSid(sid);
							}
							if (nd.getSpid() == null || nd.getSpid().isEmpty()) {
								nd.setSpid(spid);
							}
							
							nd.setModifiedBy(cur_user);
							nd.setModifiedOn(new Date(System.currentTimeMillis()));
							nd = networkDeviceService.save(nd);
						}
					} else { // FLOOR SENSOR SAVE BEACONDEVICE & NETWORKDEVICE
						//LOG.info("Save BeaconDevice And NetworkDevice>>>>>>");
						String json = request.getSession().getAttribute("json").toString();
						JSONObject beacondetails = JSONObject.fromObject(json);
						String type = (String)beacondetails.get("type");
						
						if (type.equals("ble")) {
							type = "sensor";
						} else if (type.equals("bleserver")) {
							type = "server";
						}
						
						//LOG.info("JSON tYPE>>>>>>>>>>>>>>>>>>.. " +beacondetails.get("type"));
						//LOG.info("beacondetails  TYPE >>>>>>>>>>>>>>>>>>. " +type);
						beacondetails.put("tluinterval",       tluinterval);
						beacondetails.put("keepAliveInterval", keepAliveInterval);
						beacondetails.put("type", 			   type);
						beacondetails.put("sensorFlag", 	  "1");
						beacondetails.put("alias", 			  name);
						beacondetails.put("json", 			  conf);
						this.save(beacondetails.toString(), request, response);
					}
				}

				response.sendRedirect(str);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public  String bleType(String conf,String type) {

		String value = "";

		net.sf.json.JSONObject template = net.sf.json.JSONObject.fromObject(conf);
		JSONArray jsonArray = new JSONArray();
		net.sf.json.JSONObject jsonObject = new JSONObject();

		if (template.get("attributes") != null) {
			jsonArray = template.getJSONArray("attributes");
			if (jsonArray != null && jsonArray.size() > 0) {
				jsonObject = jsonArray.getJSONObject(0);
			}
		}

		if (jsonObject.get(type) != null) {
			value = jsonObject.getString(type);
		}
		//LOG.info("value " +value);
		return value;
	}

	
	@RequestMapping(value = "/apsave", method = RequestMethod.POST)
	public void deviceSave(
			@RequestParam(value = "uuid", required = true) String uid,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = true) String cid,
			@RequestParam(value = "conf", required = true) String conf,
			@RequestParam(value = "alias", required = false) String alias,
			@RequestParam(value = "steering", required = false) String steering,
			@RequestParam(value = "param", required = false) String param,
			@RequestParam(value = "statusInterval", required = false) String statusInterval,
			@RequestParam(value = "root", required = false) String root,
			@RequestParam(value = "loadBalance", required = false) String loadBalance,
			@RequestParam(value = "workingMode", required = false) String workingMode,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		String str ="/facesix/spots?cid="+cid+"&sid="+sid+"&spid="+spid;
		
		if (SessionUtil.isAuthorized(request.getSession())){
			
			String cur_user = SessionUtil.currentUser(request.getSession());
			
			if (param.equals("DeviceConfig")) {
				str ="/facesix/spots?cid="+cid+"&sid="+sid+"&spid="+spid;
			}else {
				str = "/facesix/web/site/portion/nwcfg?sid="+sid+"&spid="+spid+"&uid=?&cid="+cid;
			}
			
			try {

				net.sf.json.JSONObject template = net.sf.json.JSONObject.fromObject(conf);
				
				uid 		= uid.toLowerCase();
				String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
				
				Device device 		= null;
				NetworkDevice nd 	= null;
				
				device = getDeviceService().findOneByUid(uid);

				if (steering == null || steering.isEmpty()) {
					steering = "false";
				}
				if (loadBalance == null || loadBalance.isEmpty()) {
					loadBalance = "false";
				}
				
				/*
				 * Steer Band Balance
				 * 
				 */
				
				String bssStationRejectThresh = null;
				String bssChanPbusyPercent 	  = null;
				String bssRejectTimeout 	  = null;
				String band2GStaRatio	      = null;
				String band5GStaRatio	 	  = null;
				String bandRcpiDiff 		  = null;
				
				if (steering.equals("true")) {
					bssStationRejectThresh = request.getParameter("bssStationRejectThresh");
					 bssChanPbusyPercent   = request.getParameter("bssChanPbusyPercent");
					 bssRejectTimeout 	  = request.getParameter("bssRejectTimeout");
					 band2GStaRatio	      = request.getParameter("band2GStaRatio");
					 band5GStaRatio	 	  = request.getParameter("band5GStaRatio");
					 bandRcpiDiff 		  = request.getParameter("bandRcpiDiff");
				}
				/*
				 * Load Balance
				 * 
				 */
				String minStaCount 		    = null;
				String rssiThreshold 	    = null;
				String rcpiRange 	        = null;
				String avgStaCntLb	        = null;
				
				
				if (loadBalance.equals("true")) {
					 minStaCount 	  = request.getParameter("minStaCount");
					 rssiThreshold 	  = request.getParameter("rssiThreshold");
					 rcpiRange 	      = request.getParameter("rcpiRange");
					 avgStaCntLb	  = request.getParameter("avgStaCntLb");
				}
				
				/* 
				 * Static ,DHCP LAN Configuration
				 * 
				 */
				
				String lan_ipv4_addr = null;
				String lan_ipv4_mask = null;
				String lan_flag 	 = request.getParameter("lan");
				
				if (lan_flag != null) {
					if (lan_flag.equals("lan_static")) {
						lan_ipv4_addr = request.getParameter("lan_ipv4_addr");
						lan_ipv4_mask = request.getParameter("lan_ipv4_mask");
					}
				}

				LOG.info(" LAN FLAG " + lan_flag + " IPV4 ADDR " 
				      + lan_ipv4_addr + " IPV4 MASK " + lan_ipv4_mask);

				if (param.equals("DeviceConfig") || (spid == null || spid.trim().isEmpty())) {
					
						boolean isnewDevice = false;
					
						if (device == null) {
							isnewDevice = true;
							device = new Device();
							device.setCreatedBy(cur_user);
							device.setUid(uid);
							device.setFstype("ap");
							device.setState("inactive");
							device.setCid(cid);
							device.setIp("0.0.0.0");
						} 
					
						if (isnewDevice) {
							device.setStatus(Device.STATUS.AUTOCONFIGURED.name());
						} else {
							device.setStatus(Device.STATUS.CONFIGURED.name());
							device.setModifiedBy(cur_user);
							device.setModifiedOn(new Date(System.currentTimeMillis()));
						}
						
						String devCid = device.getCid();
						
						if (devCid == null || devCid.isEmpty()) {
							device.setCid(cid);
						}
						
						device.setAlias(alias);
						device.setName(alias);
						device.setTemplate(template.toString());
						device.setConf(template.toString());
						
						device.setSteering(steering);
						device.setKeepAliveInterval(statusInterval);
						device.setRoot(root);
						
						device.setBssStationRejectThresh(bssStationRejectThresh);
						device.setBssChanPbusyPercent(bssChanPbusyPercent);
						device.setBssRejectTimeout(bssRejectTimeout);
						device.setBand2GStaRatio(band2GStaRatio);
						device.setBand5GStaRatio(band5GStaRatio);
						device.setBandRcpiDiff(bandRcpiDiff);
						
						device.setMinStaCount(minStaCount);
						device.setRssiThreshold(rssiThreshold);
						device.setRcpiRange(rcpiRange);
						device.setAvgStaCntLb(avgStaCntLb);
						device.setLoadBalance(loadBalance);
						device.setWorkingMode(workingMode);
						
						device.setLan_Flag(lan_flag);
						device.setLan_ipv4_Addr(lan_ipv4_addr);
						device.setLan_ipv4_Mask(lan_ipv4_mask);
						
						device = getDeviceService().saveAndSendMqtt(device, true);
						
						nd = networkDeviceService.findOneByUuid(uuid);
						
						if (nd != null) {
							nd.setAlias(alias);
							nd.setModifiedBy(cur_user);
							nd.setModifiedOn(new Date(System.currentTimeMillis()));
							nd = networkDeviceService.save(nd);
						}
					
				} else	if (sid != null && !sid.trim().isEmpty()
						&& spid != null && !spid.trim().isEmpty()
						&& uid != null && !uid.trim().isEmpty()
						&& cid != null && !cid.trim().isEmpty()) {

					nd = networkDeviceService.findOneByUuid(uuid);
					
					if (device != null && nd != null) {
						
						device.setStatus(Device.STATUS.CONFIGURED.name());
						device.setAlias(alias);
						device.setName(alias);
						device.setKeepAliveInterval(statusInterval);
						device.setTemplate(template.toString());
						device.setConf(template.toString());
						device.setModifiedBy(cur_user);
						device.setModifiedOn(new Date(System.currentTimeMillis()));
						device.setSteering(steering);
						device.setRoot(root);
						
						if (device.getSid() == null || device.getSid().isEmpty()) {
							device.setSid(sid);
						}
						if (device.getSpid() == null || device.getSpid().isEmpty()) {
							device.setSpid(spid);
						}
						device.setBssStationRejectThresh(bssStationRejectThresh);
						device.setBssChanPbusyPercent(bssChanPbusyPercent);
						device.setBssRejectTimeout(bssRejectTimeout);
						device.setBand2GStaRatio(band2GStaRatio);
						device.setBand5GStaRatio(band5GStaRatio);
						device.setBandRcpiDiff(bandRcpiDiff);
						
						device.setMinStaCount(minStaCount);
						device.setRssiThreshold(rssiThreshold);
						device.setRcpiRange(rcpiRange);
						device.setAvgStaCntLb(avgStaCntLb);
						device.setLoadBalance(loadBalance);
						device.setWorkingMode(workingMode);
						
						device.setLan_Flag(lan_flag);
						device.setLan_ipv4_Addr(lan_ipv4_addr);
						device.setLan_ipv4_Mask(lan_ipv4_mask);
						
						getDeviceService().saveAndSendMqtt(device,true);

						if (nd != null) {
							
							if (nd.getSid() == null || nd.getSid().isEmpty()) {
								nd.setSid(sid);
							}
							if (nd.getSpid() == null || nd.getSpid().isEmpty()) {
								nd.setSpid(spid);
							}
							
							nd.setAlias(alias);
							nd.setModifiedBy(cur_user);
							nd.setModifiedOn(new Date(System.currentTimeMillis()));
							nd = networkDeviceService.save(nd);
						}

					} else {
						
						String json 		 = request.getSession().getAttribute("json").toString();
						JSONObject apdetails = JSONObject.fromObject(json);
						
						apdetails.put("type", 				"ap");
						apdetails.put("apFlag", 			"1");
						apdetails.put("alias", 				alias);
						apdetails.put("json", 				conf);
						apdetails.put("steering", 			steering);
						apdetails.put("keepAliveInterval",  statusInterval);
						apdetails.put("root", 				root);
						
						apdetails.put("bssStationRejectThresh", bssStationRejectThresh);
						apdetails.put("bssChanPbusyPercent", 	bssChanPbusyPercent);
						apdetails.put("bssRejectTimeout",	 	bssRejectTimeout);
						apdetails.put("band2GStaRatio", 		band2GStaRatio);
						apdetails.put("band5GStaRatio", 		band5GStaRatio);
						apdetails.put("bandRcpiDiff", 			bandRcpiDiff);
						
						apdetails.put("minStaCount", 		minStaCount);
						apdetails.put("rssiThreshold", 		rssiThreshold);
						apdetails.put("rcpiRange", 			rcpiRange);
						apdetails.put("avgStaCntLb", 		avgStaCntLb);
						apdetails.put("loadBalance",        loadBalance);
						apdetails.put("workingMode",        workingMode);
						
						apdetails.put("lan_flag", 		    lan_flag);
						apdetails.put("lan_ipv4_addr",      lan_ipv4_addr);
						apdetails.put("lan_ipv4_mask",      lan_ipv4_mask);
						
						this.save(apdetails.toString(), request, response);
					}
				}

				response.sendRedirect(str);

			} catch (Exception e) {
				response.sendRedirect(str);
				LOG.info("While AP Config Error ", e);
			}
		}

	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public NetworkDevice save(@RequestBody String newfso, HttpServletRequest request, HttpServletResponse response) throws Exception {

	if (SessionUtil.isAuthorized(request.getSession())) {

		String cur_user = SessionUtil.currentUser(request.getSession());
		
		JSONObject json 	= JSONObject.fromObject(newfso);

		String parent 		= (String) json.get("parent");
		String sid 			= (String) json.get("sid");
		String spid 		= (String) json.get("spid");
		String xposition 	= (String) json.get("xposition");
		String yposition 	= (String) json.get("yposition");
		String uid 			= (String) json.get("uid");
		String status 		= "inactive";
		String type 		= (String) json.get("type");
		String gparent 		= (String) json.get("gparent");
		String svid 		= "";
		String swid 		= "";
		
		String keepAliveInterval = (String) json.get("keepAliveInterval");
		
		Portion portion = portionService.findById(spid);
		String cid      = portion.getCid();

		int dup = 0;

		request.getSession().setAttribute("json", json);
		
		String apFlag 			= json.containsKey("apFlag") ? (String) json.get("apFlag") : "0";
		String alias 			= json.containsKey("alias") ? (String) json.get("alias") :    "Device";
		String steering 		= json.containsKey("steering") ? (String) json.get("steering") : "false";
		String loadBalance 	    = json.containsKey("loadBalance") ? (String) json.get("loadBalance") : "false";
		String sensorFlag 		= json.containsKey("sensorFlag") ? (String) json.get("sensorFlag") : "0";
		double tlu 				= json.containsKey("tluinterval") ? Double.parseDouble(json.getString("tluinterval")) : 3;
		String workingMode 		= json.containsKey("workingMode") ? (String) json.get("workingMode") : "normalmode";
		String root 			= json.containsKey("root") ? (String) json.get("root") : "no";
		
		String lan_flag 		= json.containsKey("lan_flag") ? (String) json.get("lan_flag") : "NA";
		String lan_ipv4_addr	= null;
		String lan_ipv4_mask	= null;

		if (lan_flag.equals("lan_static")) {
			lan_ipv4_addr = json.containsKey("lan_ipv4_addr") ? (String) json.get("lan_ipv4_addr") :null;
			lan_ipv4_mask = json.containsKey("lan_ipv4_mask") ? (String) json.get("lan_ipv4_mask") :null;
		}	
	
		if (customerUtils.Gateway(cid)) {
			uid = uid.toLowerCase();
		}
		
		
		/*
		 * Steer Balance
		 * 
		 */
		
		String bssStationRejectThresh = null;
		String bssChanPbusyPercent 	  = null;
		String bssRejectTimeout 	  = null;
		String band2GStaRatio	      = null;
		String band5GStaRatio	 	  = null;
		String bandRcpiDiff 		  = null;
		
		/*
		 * Load Balance
		 * 
		 */
		String minStaCount 		    = null;
		String rssiThreshold 	    = null;
		String rcpiRange 	        = null;
		String avgStaCntLb	        = null;
		
		if ("ap".equalsIgnoreCase(type)) {
			
			if ("true".equals(steering)) {
				
				bssStationRejectThresh 	 = (String)json.get("bssStationRejectThresh");
				bssChanPbusyPercent		 = (String)json.get("bssChanPbusyPercent");
				bssRejectTimeout 		 = (String)json.get("bssRejectTimeout");
				band2GStaRatio 			 = (String)json.get("band2GStaRatio");
				band5GStaRatio 			 = (String)json.get("band5GStaRatio");
				bandRcpiDiff 			 = (String)json.get("bandRcpiDiff");
			}
			
			if ("true".equals(loadBalance)) {
				minStaCount 			= (String)json.get("minStaCount");
				rssiThreshold 			= (String)json.get("rssiThreshold");
				rcpiRange 				= (String)json.get("rcpiRange");
				avgStaCntLb 			= (String)json.get("avgStaCntLb");
			}
			
			
		}
		
		
		Iterable<NetworkDevice> list = networkDeviceService.findAll();
		Iterator<NetworkDevice> iterator = list.iterator();
		NetworkDevice nd_dup = null;

		try {
			
			Device dev = getDeviceService().findOneByUid(uid);

			if (dev != null) {

				if (Device.STATUS.REGISTERED.name().equalsIgnoreCase(dev.getStatus()) && type.equals("ap")  && apFlag.equals("1")) {

					while (iterator.hasNext()) {
						nd_dup = iterator.next();
						String nd_uid = nd_dup.getUid();

						if ((type.equals("ap") && parent.equals(nd_uid))) {
							swid = parent.replaceAll("[^a-zA-Z0-9]", "");
							svid = gparent.replaceAll("[^a-zA-Z0-9]", "");
							break;
						}

						if ((type.equals("sensor") && parent.equals(nd_uid))) {
							swid = gparent.replaceAll("[^a-zA-Z0-9]", "");
							break;
						}
					}

					String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
					NetworkDevice nd = new NetworkDevice();
					nd.setCreatedOn(new Date());
					nd.setCreatedBy(cur_user);
					nd.setModifiedOn(new Date());
					nd.setModifiedBy(cur_user);
					nd.setParent(parent);
					nd.setGParent(gparent);
					nd.setCid(cid);
					nd.setSid(sid);
					nd.setSpid(spid);
					nd.setSvid(svid);
					nd.setSwid(swid);
					nd.setXposition(xposition);
					nd.setYposition(yposition);
					nd.setUid(uid);
					nd.setStatus(status);
					nd.setUuid(uuid);
					nd.setTypefs(type);
					nd.setAlias(alias);
					nd = networkDeviceService.save(nd);

					net.sf.json.JSONObject conf = net.sf.json.JSONObject.fromObject(json.get("json"));
					
					dev.setAlias(alias);
					dev.setName(alias);
					dev.setCid(cid);
					dev.setSid(sid);
					dev.setSpid(spid);
					dev.setStatus(Device.STATUS.CONFIGURED.name());
					dev.setState("inactive");
					dev.setFstype(type);
					dev.setConf(String.valueOf(conf));
					dev.setTemplate(String.valueOf(conf));
					dev.setKeepAliveInterval(keepAliveInterval);
					dev.setRoot(root);
					dev.setSteering(steering);
					
					dev.setBssStationRejectThresh(bssStationRejectThresh);
					dev.setBssChanPbusyPercent(bssChanPbusyPercent);
					dev.setBssRejectTimeout(bssRejectTimeout);
					dev.setBand2GStaRatio(band2GStaRatio);
					dev.setBand5GStaRatio(band5GStaRatio);
					dev.setBandRcpiDiff(bandRcpiDiff);
					
					dev.setMinStaCount(minStaCount);
					dev.setRssiThreshold(rssiThreshold);
					dev.setRcpiRange(rcpiRange);
					dev.setAvgStaCntLb(avgStaCntLb);
					dev.setLoadBalance(loadBalance);
					dev.setWorkingMode(workingMode);
					
					dev.setLan_Flag(lan_flag);
					dev.setLan_ipv4_Addr(lan_ipv4_addr);
					dev.setLan_ipv4_Mask(lan_ipv4_mask);
					
					dev.setModifiedBy(cur_user);
					
					getDeviceService().saveAndSendMqtt(dev,true);
					
					return nd;
				}

			}

		} catch (Exception e) {
			LOG.info("While saving REGISTERED AP error",e);
		}

		while (iterator.hasNext()) {
			nd_dup = iterator.next();
			String nd_uid = nd_dup.getUid().toLowerCase();

			if (nd_uid.equals(uid.toLowerCase())) {

				Device device 				 = getDeviceService().findOneByUid(nd_uid);
				BeaconDevice beacondevice    = getBeaconDeviceService().findOneByUid(nd_uid);
				dup = 1;
				
				if (customerUtils.GatewayFinder(cid)) {
					if (beacondevice == null || device == null) {
						networkDeviceService.delete(nd_dup.getId());
						dup = 0;
					}
				}  else if(customerUtils.Gateway(cid)) {
					if (device == null) {
						networkDeviceService.delete(nd_dup.getId());
						dup = 0;
					}
				} else if (customerUtils.GeoFinder(cid)) {
					if (beacondevice == null) {
						networkDeviceService.delete(nd_dup.getId());
						dup = 0;
					}
				}
			}
		}

		if (dup == 1) {
			LOG.info("Duplicate MAC Found + UID:" + uid);
			return null;
		}

		if (type.equals("server")) {
			svid = uid.replaceAll("[^a-zA-Z0-9]", "");
		}

		String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");

		list 	 = networkDeviceService.findBySpid(spid);
		iterator = list.iterator();
		nd_dup 	 = null;

		while (iterator.hasNext()) {
			nd_dup = iterator.next();
			String nd_uid = nd_dup.getUid();

			if (type.equals("switch") && parent.equals(nd_uid)) {
				swid = uid.replaceAll("[^a-zA-Z0-9]", "");
				svid = parent.replaceAll("[^a-zA-Z0-9]", "");
				break;
			}

			if (type.equals("ap") && parent.equals(nd_uid)) {
				swid = parent.replaceAll("[^a-zA-Z0-9]", "");
				svid = gparent.replaceAll("[^a-zA-Z0-9]", "");
				break;
			}

			if (type.equals("sensor") && parent.equals(nd_uid)) {
				swid = gparent.replaceAll("[^a-zA-Z0-9]", "");
				break;
			}
		}

		NetworkDevice nd = new NetworkDevice();
		nd.setCreatedOn(new Date());
		nd.setCreatedBy(SessionUtil.currentUser(request.getSession()));
		nd.setModifiedOn(new Date());
		nd.setModifiedBy(nd.getCreatedBy());
		nd.setParent(parent);
		nd.setGParent(gparent);
		nd.setCid(cid);
		nd.setSid(sid);
		nd.setSpid(spid);
		nd.setSvid(svid);
		nd.setSwid(swid);
		nd.setXposition(xposition);
		nd.setYposition(yposition);
		nd.setUid(uid);
		nd.setStatus(status);
		nd.setTypefs(type);
		nd.setUuid(uuid);
		nd.setAlias(alias);

		try {

			//LOG.info("beacon Uid save " +uid);
			if (null != nd && type.equals("server") && sensorFlag.equals("1")) {
				
				BeaconDevice beacondevice = getBeaconDeviceService().findOneByUid(uid);
				net.sf.json.JSONObject template = net.sf.json.JSONObject.fromObject(json.get("json"));
				String bleType = bleType(template.toString(),"type");
				
				if (beacondevice == null) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put ("uid",  	uid);
					map.put ("name", 	alias);
					map.put ("state",   "inactive");
					map.put ("cid", 	cid);
					map.put ("sid", 	sid);
					map.put ("spid", 	spid);
					map.put ("fstype", 	type);
					map.put ("alias",	alias);
					map.put("bleType", bleType);

					map.put ("by", "Web");
					map.put("status", BeaconDevice.STATUS.AUTOCONFIGURED.name());
					if (json.get("json") != null) {
						map.put("template", template.toString());
					}else{
						map.put("template", "default");
					}
					scannerMqttMessageHandler = new ScannerMqttMessageHandler();
					scannerMqttMessageHandler.createBeaconDevice(map, false);
					beacondevice = getBeaconDeviceService().findOneByUid(uid);
				}
				
				geoFinderRestController.Pixel2Coordinate(spid, uid, xposition, yposition);
				nd.id = beacondevice.getId();
				beacondevice = getBeaconDeviceService().findOneByUid(uid);
				beacondevice.setTemplate(template.toString());
				beacondevice.setConf(template.toString());
				beacondevice.setCid(cid);
				beacondevice.setSid(sid);
				beacondevice.setSpid(spid);
				beacondevice.setAlias(alias);
				beacondevice.setName(alias);
				beacondevice.setStatus(BeaconDevice.STATUS.CONFIGURED.name());
				beacondevice.setState("active");
				beacondevice.setType(bleType);
				beacondevice.setModifiedBy(cur_user);
				beacondevice.setKeepAliveInterval(keepAliveInterval);
				beacondevice.setTlu(tlu);
				getBeaconDeviceService().save(beacondevice, true);
				
				nd.setStatus("active");
				nd.setBleType(bleType);
				nd.setModifiedBy(cur_user);
				nd.setModifiedOn(new Date(System.currentTimeMillis()));
				nd.update(nd);
				nd = networkDeviceService.save(nd);
				//LOG.info("senor save after" +nd);
			}
			
			if (null != nd && type.equals("sensor") && sensorFlag.equals("1")) {

				BeaconDevice beacondevice = getBeaconDeviceService().findOneByUid(uid);
				net.sf.json.JSONObject template = net.sf.json.JSONObject.fromObject(json.get("json"));
				String bleType = bleType(template.toString(),"type");
				
				if (beacondevice == null) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put ("uid",  	uid);
					map.put ("name", 	alias);
					map.put ("state",   "inactive");
					map.put ("cid", 	cid);
					map.put ("sid", 	sid);
					map.put ("spid", 	spid);
					map.put ("fstype", 	type);
					map.put ("alias",	alias);
					map.put("bleType", bleType);

					map.put ("by", "Web");
					map.put("status", BeaconDevice.STATUS.AUTOCONFIGURED.name());
					if (json.get("json") != null) {
						map.put("template", template.toString());
					}else{
						map.put("template", "default");
					}
					scannerMqttMessageHandler = new ScannerMqttMessageHandler();
					scannerMqttMessageHandler.createBeaconDevice(map, false);
					beacondevice = getBeaconDeviceService().findOneByUid(uid);
				}
				
				geoFinderRestController.Pixel2Coordinate(spid, uid, xposition, yposition);
				
				nd.id = beacondevice.getId();
				beacondevice = getBeaconDeviceService().findOneByUid(uid);
				beacondevice.setTemplate(template.toString());
				beacondevice.setConf(template.toString());
				beacondevice.setCid(cid);
				beacondevice.setSid(sid);
				beacondevice.setSpid(spid);
				beacondevice.setAlias(alias);
				beacondevice.setName(alias);
				beacondevice.setStatus(BeaconDevice.STATUS.CONFIGURED.name());
				beacondevice.setType(bleType);
				beacondevice.setState("active");
				beacondevice.setModifiedBy(cur_user);
				beacondevice.setKeepAliveInterval(keepAliveInterval);
				beacondevice.setTlu(tlu);
				getBeaconDeviceService().save(beacondevice, true);

				nd.setStatus("active");
				nd.setBleType(bleType);
				nd.setModifiedBy(cur_user);
				nd.setModifiedOn(new Date(System.currentTimeMillis()));
				nd.update(nd);
				nd = networkDeviceService.save(nd);
				//LOG.info("senor save after" +nd);
			}

			if (null != nd && type.equals("ap") && apFlag.equals("1")) {

				Device device = getDeviceService().findOneByUid(uid);

				if (device == null) {
					device = new Device();
					device.setUid(uid);
					device.setCreatedBy(cur_user);
					device.setCreatedOn(new Date(System.currentTimeMillis()));
					device.setIp("0.0.0.0");
					device.setStatus(Device.STATUS.AUTOCONFIGURED.name());
					device.setState("inactive");
				}
				
				net.sf.json.JSONObject conf = net.sf.json.JSONObject.fromObject(json.get("json"));
				
				device.setFstype(type);
				device.setName(alias);
				device.setAlias(alias);
				device.setCid(cid);
				device.setSid(sid);
				device.setSpid(spid);
				device.setConf(String.valueOf(conf));
				device.setTemplate(String.valueOf(conf));
				device.setKeepAliveInterval(keepAliveInterval);
				device.setRoot(root);
				device.setSteering(steering);
				
				device.setBssStationRejectThresh(bssStationRejectThresh);
				device.setBssChanPbusyPercent(bssChanPbusyPercent);
				device.setBssRejectTimeout(bssRejectTimeout);
				device.setBand2GStaRatio(band2GStaRatio);
				device.setBand5GStaRatio(band5GStaRatio);
				device.setBandRcpiDiff(bandRcpiDiff);
				
				device.setMinStaCount(minStaCount);
				device.setRssiThreshold(rssiThreshold);
				device.setRcpiRange(rcpiRange);
				device.setAvgStaCntLb(avgStaCntLb);
				device.setLoadBalance(loadBalance);
				device.setWorkingMode(workingMode);
				device.setStatus(Device.STATUS.CONFIGURED.name());
				
				device.setLan_Flag(lan_flag);
				device.setLan_ipv4_Addr(lan_ipv4_addr);
				device.setLan_ipv4_Mask(lan_ipv4_mask);
				
				device.setModifiedBy(cur_user);
				
				device = getDeviceService().saveAndSendMqtt(device, true);
				nd.id  = device.getId();

				nd.setModifiedBy(cur_user);
				nd.setModifiedOn(new Date(System.currentTimeMillis()));
				nd.update(nd);
				nd = networkDeviceService.save(nd);
				//LOG.info("nd save after" +nd);
			} if(type.equals("server") || type.equals("switch")) {
				// save other than AP in the network device list
				nd = networkDeviceService.save(nd);
			}

			networkDeviceService.venue_device_count++;
		} catch (Exception e) {
			e.printStackTrace();
		}

			return nd;
		}

		return null;
	}

	@RequestMapping(value = "/exportJSONConfig", method = RequestMethod.GET)
	public String exportJSONConfig(@RequestParam(value = "uid", required = true) String uid,
								   @RequestParam(value = "conf", required = true) String conf,
								   HttpServletRequest request,HttpServletResponse response) throws IOException{

		String fileName  = "./uploads/devconfig.txt";

		//String fileName="E:\\files\\jsonconfig.txt";
		FileWriter file          		  = null;
		FileInputStream fileInputStream   = null;
		OutputStream responseOutputStream = null;
		
		try {

			if (conf != null) {

				file = new FileWriter(fileName);
				file.write(conf);
				file.flush();

				File pdfFile = new File(fileName);
				response.setContentType("text/plain");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
				response.setContentLength((int) pdfFile.length());

				fileInputStream = new FileInputStream(pdfFile);
				responseOutputStream = response.getOutputStream();

				int bytes;
				while ((bytes = fileInputStream.read()) != -1) {
					responseOutputStream.write(bytes);
				}

				

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fileInputStream.close();
			responseOutputStream.close();
			file.close();
		}

		return fileName;
	}

	@RequestMapping(value = "/isDuplicateDevice", method = RequestMethod.GET)
	public String isDuplicateDevice(@RequestParam(value = "uid", required = true) String uid) {

		Iterable<Device> list = getDeviceService().findAll();
		Iterator<Device> iterator = list.iterator();
		Device device = null;
		int dup = 0;
		while (iterator.hasNext()) {
			device = iterator.next();
			String device_uid = device.getUid();
			if (device_uid.toLowerCase().equals(uid.toLowerCase())) {
				dup = 1;
			}
		}
		if (dup == 1) {
			LOG.info(" Device Duplicate MAC Found + UID:" + uid);
			return "found";
		}
		return "notfound";
	}

	private DeviceService getDeviceService() {
		if (devService == null) {
			devService = Application.context.getBean(DeviceService.class);
		}
		return devService;
	}

	private BeaconDeviceService getBeaconDeviceService() {
		if (beaconDeviceService == null) {
			beaconDeviceService = Application.context.getBean(BeaconDeviceService.class);
		}
		return beaconDeviceService;
	}public ClientDeviceService getClientDeviceService() {
		if (clientDeviceService == null) {
			clientDeviceService = Application.context.getBean(ClientDeviceService.class);
		}
		return clientDeviceService;
	}
}

