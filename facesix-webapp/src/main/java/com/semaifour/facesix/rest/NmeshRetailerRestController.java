package com.semaifour.facesix.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.device.ClientDevice;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceBssid;
import com.semaifour.facesix.data.elasticsearch.device.DeviceBssidService;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.probe.oui.ProbeOUI;
import com.semaifour.facesix.probe.oui.ProbeOUIService;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.web.WebController;

/**
 * @author Qubercomm Inc
 * 
 */

@RestController
@RequestMapping("/rest/nmesh")
public class NmeshRetailerRestController extends WebController {
	
	static Logger LOG = LoggerFactory.getLogger(NmeshRetailerRestController.class.getName());
	
    DateFormat parse 			   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    DateFormat format 			   = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	
	@Autowired
	private ClientDeviceService 	clientDeviceService;
	
	@Autowired
	private FSqlRestController 		fsqlRestController;
	
	@Autowired
	private DeviceService devService;
	
	@Autowired
	private CustomerUtils customerUtils;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	DeviceBssidService deviceBssidService;
	
	@Autowired
	ProbeOUIService probeOUIService;
	
	private String indexname = "facesix*";

	private String 	device_history_event = "device-history-event";
	
	@PostConstruct
	public void init() {
		indexname 		= _CCC.properties.getProperty("elasticsearch.indexnamepattern", "facesix*");
		device_history_event = _CCC.properties.getProperty("facesix.device.event.history.table",device_history_event);
	}
	

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/dcs_chan_switch", method = RequestMethod.GET)
    public JSONArray dcs_chan_switch(
		    @RequestParam(value = "uid", required = true) String uid,
			@RequestParam(value = "time", required = false, defaultValue = "30m") String time) {

	try {

		Device device = devService.findOneByUid(uid);
		
		if (device == null) return null;

		String cid = device.getCid();
		
		DateFormat outputFormat = new SimpleDateFormat("MM-yyyy-dd HH:mm");
		DateFormat inputFormat  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

		Customer customer = customerService.findById(cid);
		if (customer != null) {
			TimeZone totimezone = customerUtils.FetchTimeZone(customer.getTimezone());
			inputFormat.setTimeZone(totimezone);
		}
	
		JSONObject devlist 	 = null;
		JSONArray  dev_array = new JSONArray();

	String fsql = "index="+device_history_event+",type=device_history_dcs_chan_switch,sort=timestamp asc,"
			+ " query=timestamp:>now-"+time+" AND opcode:dcs_chan_switch AND uid:\"" + uid + "\" "
			+ " |value(uid,uid,NA); value(radio_type,radio_type,NA);value(bandwidth,bandwidth,NA);"
			+ " value(channel,channel,NA);value(timestamp,timestamp,NA)|table,sort=Date:asc;";

	List<Map<String, Object>> log = fsqlRestController.query(fsql);

	Iterator<Map<String, Object>> iterator = log.iterator();

		while (iterator.hasNext()) {

			devlist = new JSONObject();

			HashMap<String, Object> channelMap = (HashMap<String, Object>) iterator.next();
				
			String radio_type = (String) channelMap.get("radio_type");
			String timestamp  = (String) channelMap.get("timestamp");

			Date date = inputFormat.parse(timestamp);
			timestamp = outputFormat.format(date);

			devlist.put("uid", 		 uid);
			devlist.put("bandwidth", channelMap.get("bandwidth"));
			devlist.put("channel", 	 channelMap.get("channel"));
			devlist.put("radio_type",radio_type);
			devlist.put("time", 	 timestamp);
			
			dev_array.add(devlist);

			}

			return dev_array;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(@RequestParam(value = "cid", required = true) String cid,
							@RequestParam(value = "time", required = false, defaultValue = "3m") String time,
							HttpServletRequest request) {
		
		JSONObject devlist     = new JSONObject();
		JSONArray dev_array    = new JSONArray();

		try {

			List<Device> device = getDeviceService().findByCid(cid);

			if (device != null) {

				device.forEach(dv -> {

					JSONObject dev = new JSONObject();

					String uid 		   = dv.getUid();
					int _2G_chennal_no = dv.get_2G_channel_number();
					int _5G_chennal_no = dv.get_5G_channel_number();
					
					String state = dv.getState().equalsIgnoreCase("active") ? "online" : "offline";
					String vap2G = dv.getVap2gcount() == null ? "0" : dv.getVap2gcount();
					String vap5G = dv.getVap5gcount() == null ? "0" : dv.getVap5gcount();
					
					JSONObject txrx           = device_tx_rx(uid,time,vap2G,vap5G);
					int device_tx 			  = (int)txrx.get("tx");
					int device_rx 			  = (int)txrx.get("rx");
					
					String conf               = dv.getConf();
					String security_type      = deviceConfigDetails(conf, "encryption");
					JSONObject client_metrics = deviceMetrics(uid,_2G_chennal_no,_5G_chennal_no);
					
					dev.put("uid",       	 uid);
					dev.put("location",      dv.getAlias());
					dev.put("state",         state);
					dev.put("cid", 		     dv.getCid());
					dev.put("tx_bytes",      device_tx);
					dev.put("rx_bytes", 	 device_rx);
					dev.put("security_type", security_type);
					dev.put("metrics",       client_metrics);

					dev_array.add(dev);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		devlist.put("device_details", dev_array);

		return devlist;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/mesh_link", method = RequestMethod.GET)
	public JSONObject mesh_link(@RequestParam(value = "cid", required = true) String cid,HttpServletRequest request) {

		JSONArray topology_array = new JSONArray();
		JSONObject data          = new JSONObject();
		
		String customerName = "My Home";
		
		try {

			JSONObject topology_json = null;
			
			Customer customer   = customerService.findById(cid);
			
			if (customer != null) {
				customerName = customer.getCustomerName();
			}

			List<Device> device = getDeviceService().findByCid(cid);
			
			if (device != null) {
				for (Device dev : device) {

					String uid         = dev.getUid();
					String location    = dev.getAlias();
					String root        = dev.getRoot() == null ? "NA" : dev.getRoot();
					boolean isRootNode = root.equals("no") ? false : true;
					
					String internet_status = "online";
					
					String ap_state   = dev.getState().equalsIgnoreCase("active") ? "online" : "offline";
					
					if (ap_state.equals("offline")) {
						internet_status = "offline";
					}
					
					topology_json 				  = new JSONObject();
					JSONArray topology_json_array = new JSONArray();
					
					topology_json.put("uid", 	 		  uid);
					topology_json.put("wan_backhaul",	  isRootNode);
					topology_json.put("location", 		  location);
					topology_json.put("internet_status",  internet_status);
					topology_json.put("ap_status", 	 	  ap_state);

					DeviceBssid devBss = deviceBssidService.findOneByUid(uid);
					
					if (devBss != null && ap_state.equals("online")) {
						
						JSONArray mesh_link   = devBss.getMesh_links();
						
						if (mesh_link != null) {
							
							mesh_link.forEach(meshmap -> {
								
								HashMap<String,Object> obj = (HashMap<String,Object>) meshmap;
								String peer_mesh_mac       = (String) obj.get("peer_mesh_mac");
								
								DeviceBssid bssid_assoc_uid = deviceBssidService.findByBssid(peer_mesh_mac);
								
								if (bssid_assoc_uid != null) {
									
									String assoc_mesh_uid = bssid_assoc_uid.getUid();
									JSONObject topology_data_json = new JSONObject();
									
									topology_data_json.put("mesh_mac", 			assoc_mesh_uid);
									topology_data_json.put("rssi", 				obj.get("rssi"));
									topology_data_json.put("band", 				obj.get("radio_type"));
									topology_data_json.put("signal_strength_percent", 	obj.get("signal_strength"));
									topology_data_json.put("_mesh_tx_bytes", 	obj.get("_mesh_tx_bytes"));
									topology_data_json.put("_mesh_rx_bytes", 	obj.get("_mesh_rx_bytes"));
									topology_data_json.put("_mesh_tx_pkts", 	obj.get("_mesh_tx_pkts"));
									topology_data_json.put("_mesh_rx_pkts", 	obj.get("_mesh_rx_pkts"));
									topology_data_json.put("conn_time_sec", 	obj.get("connection_time"));
									
									topology_json_array.add(topology_data_json);
								}
							});
						}
					}

					topology_json.put("mesh_links", topology_json_array);
					topology_array.add(topology_json);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		data.put("title",    customerName);
		data.put("topology", topology_array);

		return data;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/device_metrics_histogram", method = RequestMethod.GET)
    public  JSONObject device_metrics_histogram(
    					@RequestParam(value = "uid", required = true) String uid,
    					@RequestParam(value = "time", required = false, defaultValue = "5m") String time,
    					HttpServletRequest request) throws ParseException {

		List<Map<String, Object>> logs   = EMPTY_LIST_MAP;
		List<Map<String, Object>> logs5g = EMPTY_LIST_MAP;

		String fsql = "index=" + indexname + ",sort=timestamp desc,size=1,query=timestamp:>now-" + time + " AND uid:\"" + uid + "\"";
		String fsql2g = fsql +" AND radio_type:\"2.4Ghz\"|value(_vap_rx_bytes,rx_bytes,NA);value(_vap_tx_bytes,tx_bytes,NA);value(timestamp,time,NA)|table";
		String fsql5g = fsql +" AND radio_type:\"5Ghz\"|value(_vap_rx_bytes,rx_bytes,NA);value(_vap_tx_bytes,tx_bytes,NA);value(timestamp,time,NA)|table";
		
		logs = fsqlRestController.query(fsql2g);
		logs5g = fsqlRestController.query(fsql5g);
		logs.addAll(logs5g);

		JSONObject tx_rx 	 = new JSONObject();
		JSONArray txrx_array = new JSONArray();
		
		Device device = getDeviceService().findOneByUid(uid);

		DateFormat hhmmss 	= new SimpleDateFormat("HH:mm:ss");
		
		if (device != null) {
			
			String cid 			= device.getCid();
			Customer cx 		= customerService.findById(cid);
			
			TimeZone totimezone = customerUtils.FetchTimeZone(cx.getTimezone());
			hhmmss.setTimeZone(totimezone);
			parse.setTimeZone(TimeZone.getTimeZone("UTC"));
			
		}
		
		

		if (logs != null && logs.size() > 0) {

			Map<String, Object> txrx = logs.get(0);

			int rx_bytes = (int) txrx.get("rx_bytes");
			int tx_bytes = (int) txrx.get("tx_bytes");
			
			String timestamp = (String) txrx.get("time");
			Date date 		 = parse.parse(timestamp);
			timestamp 		 = hhmmss.format(date);
			
			tx_rx.put("uid", 	 uid);
			tx_rx.put("rx_bytes",rx_bytes);
			tx_rx.put("tx_bytes",tx_bytes);
			tx_rx.put("time", 	 timestamp);
			
			txrx_array.add(tx_rx);
			
		}
		
		String cpu_fsql = "index=" + indexname + ",sort=timestamp desc,size=1,query=cpu_stats:\"Qubercloud Manager\" AND timestamp:>now-"+time+" AND ";
		cpu_fsql = cpu_fsql + "uid:\"" + uid + "\"";
		cpu_fsql = cpu_fsql + "|value(uid,uid,NA);value(cpu_percentage,cpu_percentage,NA);value(timestamp,time,NA);|table";

	  List<Map<String, Object>> cpu = fsqlRestController.query(cpu_fsql);

	  String mem_fsql = "index=" + indexname + ",sort=timestamp desc,size=1,query=cpu_stats:\"Qubercloud Manager\" AND timestamp:>now-" + time + " AND ";
	  mem_fsql = mem_fsql + "uid:\"" + uid + "\"";
	  mem_fsql = mem_fsql + "|value(uid,uid,NA);value(ram_percentage,mem_percentage,NA);value(timestamp,time,NA);|table";
	 
	List<Map<String, Object>> memory = fsqlRestController.query(mem_fsql);

	JSONObject memory_json = new JSONObject();
	JSONObject cpu_json = new JSONObject();
	
	if (memory != null && memory.size() > 0) {

		Map<String, Object> map = memory.get(0);
		
		int mem_percentage = (int) map.get("mem_percentage");
		String timestamp = (String) map.get("time");
		Date date 		 = parse.parse(timestamp);
		timestamp 		 = hhmmss.format(date);
		
		memory_json.put("uid", 				uid);
		memory_json.put("mem_percentage",   mem_percentage);
		memory_json.put("time", 			timestamp);
	}
	
	if (cpu != null && cpu.size() > 0) {

		Map<String, Object> map = cpu.get(0);
		
		int cpu_percentage 	= (int) map.get("cpu_percentage");
		String timestamp = (String) map.get("time");
		Date date 		 = parse.parse(timestamp);
		timestamp 		 = hhmmss.format(date);
		
		cpu_json.put("uid", 			 uid);
		cpu_json.put("cpu_percentage",   cpu_percentage);
		cpu_json.put("time", 			timestamp);

	}

	JSONObject device_histogram = new JSONObject();
	
	device_histogram.put("memory",          memory_json);
	device_histogram.put("cpu",             cpu_json);
	device_histogram.put("tx_rx_histogram", txrx_array);

	JSONArray metrics_array = new JSONArray();
	metrics_array.add(device_histogram);

	JSONObject histogram = new JSONObject();
	histogram.put("device_metrics_histogram ", metrics_array);

	return histogram;

}

    
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/wireless_client_details", method = RequestMethod.GET)
	public JSONObject wireless_client_details(
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "uid", required = false) String uid) {

		JSONObject details = new JSONObject();
		JSONArray client = new JSONArray();

		try {

			JSONObject devlist 	 				= null;
			List<ClientDevice> clientDevices    = null;
			final String status 				= "active";

			if (uid != null) {
				String uuid   = uid.replaceAll("[^a-zA-Z0-9]", "");
				clientDevices = getClientDeviceService().findByUuidAndStatus(uuid, status);
			} else {
				clientDevices = getClientDeviceService().findByCidAndStatus(cid, status);
			}

			HashMap<String, Integer> dupsmap = new HashMap<String, Integer>();

			HashMap<String, String> devicesmap = new HashMap<String, String>();
			
			if (clientDevices != null) {

				for (ClientDevice clientDev : clientDevices) {

					devlist =  new JSONObject();
    				
    				boolean _11r = clientDev.is_11r();
    				boolean _11k = clientDev.is_11k();
    				boolean _11v = clientDev.is_11v();
    				
    				String client_mac   = clientDev.getMac();
    				String ap 			= clientDev.getUid();
    				String devtype   	= clientDev.getTypefs() == null ? "Others" : clientDev.getTypefs();
    				String radioType	= clientDev.getRadio_type() == null ? "2.4Ghz" : clientDev.getRadio_type();
    				String ssid      	= clientDev.getSsid() == null ? "UNKOWN" : clientDev.getSsid() ;
					String rssi         = clientDev.getPeer_rssi() == null ? "0"  : clientDev.getPeer_rssi();
					String ip           = clientDev.getPeer_ip() == null ? "NA"  : clientDev.getPeer_ip();
    				String clientType   = clientDev.getPeer_caps_client() == null ? "NA"  : clientDev.getPeer_caps_client();
    				
					if (dupsmap.containsKey(client_mac)) {
						dupsmap.put(client_mac, dupsmap.get(client_mac) + 1);
						continue;
					} else {
						
						String location = "NA";

						if (devicesmap.containsKey(ap)) {
							location = devicesmap.get(ap);
						} else {
							Device dev = getDeviceService().findOneByUid(ap);
							if (dev != null) {
								location = dev.getAlias();
							}
							devicesmap.put(ap, location);
						}
						
						String hostname = clientDev.getPeer_hostname();
						
						if (hostname == null || hostname.isEmpty()) {
							hostname = clientDev.getDevname();
						}

						devlist.put("mac_address",  	client_mac);
	    				devlist.put("uid", 				ap);
	    				devlist.put("location", 		location);
	    				devlist.put("bssid", 			clientDev.getVap_mac());
	    				devlist.put("ssid", 			ssid);
	    				devlist.put("rssi", 			rssi);
	    				devlist.put("signal_strength", 	clientDev.getPeer_signal_strength());
	    				devlist.put("_peer_tx_bytes", 	clientDev.getCur_peer_tx_bytes());
	    				devlist.put("_peer_rx_bytes", 	clientDev.getCur_peer_rx_bytes());
	    				devlist.put("_11r", 			_11r);
	    				devlist.put("_11v", 			_11v);
	    				devlist.put("_11k", 			_11k);
	    				devlist.put("client_type", 		clientType);
	    				devlist.put("conn_time_sec",    clientDev.getPeer_conn_time());
	    				devlist.put("no_of_streams",    clientDev.getNo_of_streams());
	    				devlist.put("ip",               ip);
	    				devlist.put("radio", 			radioType);
	    				devlist.put("os",             	devtype);
	    				devlist.put("host_name",      	hostname);
	    				
	    				client.add(devlist);

						dupsmap.put(client_mac, 0);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		details.put("wireless_client_details", client);

		return details;
	}

	@SuppressWarnings("unchecked")
	public JSONObject device_tx_rx(String uid,String time,String vap2g, String vap5g) {

		JSONObject txrx = new JSONObject();
		int tx 		= 0;
		int rx 		= 0;
		
		if (time == null) time = "3m";
		
		int vap2G = Integer.parseInt(vap2g);
		int vap5G = Integer.parseInt(vap5g);
		
		for (int i = 0; i < vap2G; i++) {
			
			String query = "index="+indexname +",sort=timestamp desc,size=1,query=timestamp:>now-"+ time+" AND "
					 +" uid:\""+uid+"\" AND vap_id:\""+i+"\" AND radio_type:\"2.4Ghz\"|"
					+ " value(_vap_tx_bytes,_vap_tx_bytes, NA); value(_vap_rx_bytes,_vap_rx_bytes, NA);"
					+ " value(timestamp,timestamp,NA);|table,sort=Date:desc;";

			List<Map<String, Object>> twoG_Details = fsqlRestController.query(query);

			if (twoG_Details != null && twoG_Details.size() > 0) {
				Map<String, Object> map = twoG_Details.get(0);
				tx += (int) map.get("_vap_tx_bytes");
				rx += (int) map.get("_vap_rx_bytes");
			}
		}

		for (int i = 0; i < vap5G; i++) {
			
			String query = "index="+indexname +",sort=timestamp desc,size=1,query=timestamp:>now-"+time+" AND "
						+ " uid:\""+uid+"\" AND vap_id:\""+i+"\" AND radio_type:\"5Ghz\"|"
						+ " value(_vap_tx_bytes,_vap_tx_bytes, NA); value(_vap_rx_bytes,_vap_rx_bytes, NA);"
						+ " value(timestamp,timestamp,NA);|table,sort=Date:desc;";

			List<Map<String, Object>> fiveG_Details = fsqlRestController.query(query);

			if (fiveG_Details != null && fiveG_Details.size() > 0) {
				Map<String, Object> map = fiveG_Details.get(0);
				tx += (int) map.get("_vap_tx_bytes");
				rx += (int) map.get("_vap_rx_bytes");
			}
		}

		txrx.put("tx", tx);
		txrx.put("rx", rx);

		return txrx;
	}

	@SuppressWarnings("unchecked")
	public String deviceConfigDetails(String conf, String type) {

		net.sf.json.JSONObject template = net.sf.json.JSONObject.fromObject(conf);
		String value = "open";

		if (template.containsKey("interfaces2g")) {

			net.sf.json.JSONArray jsonArray = template.getJSONArray("interfaces2g");
			Iterator<net.sf.json.JSONObject> it = jsonArray.iterator();

			while (it.hasNext()) {
				net.sf.json.JSONObject json = it.next();
				if (json.containsKey(type)) {
					value = json.getString(type);
					break;
				}
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	private JSONObject deviceMetrics(String uid,int _2G_chennal_no,int _5G_chennal_no) {

		String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
		
		final String status = "active";
		
		List<ClientDevice> clientDevices = getClientDeviceService().findByUuidAndStatus(uuid, status);

		final int GOOD = 50;
		final int FAIR = 35;

		 int _2G_GOOD = 0;
		 int _2G_FAIR = 0;
		 int _2G_POOR = 0;
		
		 int _5G_GOOD = 0;
		 int _5G_FAIR = 0;
		 int _5G_POOR = 0;
			
		int _2g_count = 0;
		int _5g_count = 0;
		
		if (clientDevices != null ) {
			for (ClientDevice dev : clientDevices) {
				
				String rssi      = dev.getPeer_rssi() == null ? "0" : dev.getPeer_rssi();
				String radioType = dev.getRadio_type() == null ? "2.4Ghz" : dev.getRadio_type();
				
				int intRSSI = Math.abs(Integer.parseInt(rssi));
				
				if (radioType.contains("2.4")) {
					_2g_count++;
					if (intRSSI >= GOOD) {
						_2G_GOOD++;
					} else if (intRSSI >= FAIR) {
						_2G_FAIR++;
					} else {
						_2G_POOR++;
					}
				} else {
					_5g_count++;
					if (intRSSI >= GOOD) {
						_5G_GOOD++;
					} else if (intRSSI >= FAIR) {
						_5G_FAIR++;
					} else {
						_5G_POOR++;
					}
				}
			}
		}
		
		JSONObject metrics        = new JSONObject();
		JSONObject client_metrics = new JSONObject();
	 
		 metrics.put("channel_number",     _2G_chennal_no);
		 metrics.put("_2g_station_count",  	_2g_count);
		 metrics.put("_2g_good_clients",   _2G_GOOD);
		 metrics.put("_2g_fair_clients",    _2G_FAIR);
		 metrics.put("_2g_poor_clients",    _2G_POOR);
		 
		 client_metrics.put("_2g_metrics", metrics);
		
		 metrics = new JSONObject();
		 
		 metrics.put("channel_number",      _5G_chennal_no);
		 metrics.put("_5g_station_count",   _5g_count);
		 metrics.put("_5g_good_clients",    _5G_GOOD);
		 metrics.put("_5g_fair_clients", 	_5G_FAIR);
		 metrics.put("_5g_poor_clients",    _5G_POOR);

		client_metrics.put("_5g_metrics", metrics);
	
		return client_metrics;
	}

	public boolean mesh_stats(@RequestBody Map<String, Object> map) {

		try {

			//LOG.info(" mesh_stats " + map);
			
			String uid 			= (String) map.get("uid");
			int channel 		= (int) map.get("CHANNEL");
			String radio_type 	= (String) map.get("radio_type");
			String bssid 		= (String) map.get("mesh_mac");
			String mesh_id 		= (String) map.get("mesh_id");
			
			JSONArray array_link_array = new JSONArray();
			
			if (map.containsKey("mesh_links")) {
				
				List<HashMap<String,Object>> mesh_links = (List<HashMap<String,Object>>)map.get("mesh_links");
				
					mesh_links.forEach(data -> {

					JSONObject object = new JSONObject();
					
					if (data.containsKey("peer_mesh_mac")) {
						
						String mesh_mac   = (String)data.get("peer_mesh_mac");
						
						object.put("peer_mesh_mac",   mesh_mac); 
						object.put("rssi",     		  data.get("rssi"));
						object.put("signal_strength", data.get("signal_strength"));
						object.put("radio_type", 	  radio_type);
						object.put("_mesh_tx_bytes",  data.get("_mesh_tx_bytes"));
						object.put("_mesh_rx_bytes",  data.get("_mesh_rx_bytes"));
						object.put("_mesh_tx_pkts",   data.get("_mesh_tx_pkts"));
						object.put("_mesh_rx_pkts",   data.get("_mesh_rx_pkts"));
						object.put("connection_time", data.get("connection_time"));
						
						array_link_array.add(object);
					}
				});
			}

			Device device = getDeviceService().findOneByUid(uid);
			if (device == null) {
				LOG.info(" DEVICE NOT FOUND " + uid);
				return false;
			}	
		
			String cid = device.getCid();
			
			DeviceBssid bssidmapping = deviceBssidService.findByBssid(bssid);
			if (bssidmapping == null) {
				bssidmapping = new DeviceBssid();
				bssidmapping.setBssid(bssid);
			}

			bssidmapping.setCid(cid);
			bssidmapping.setUid(uid);
			bssidmapping.setChannel(channel);
			bssidmapping.setRadio_type(radio_type);
			bssidmapping.setMesh_links(array_link_array);
			bssidmapping.setMesh_id(mesh_id);
			deviceBssidService.save(bssidmapping);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
  @SuppressWarnings("unchecked")
  public JSONObject deviceManufacture(String peer_mac) {
		
		JSONObject dev = new JSONObject();
	    
		String inputLine = "Unknown Vendor";
		String probeMac  = peer_mac;
		
		probeMac = probeMac.substring(0,8).toUpperCase();
		
		ProbeOUI oui = probeOUIService.findOneByUid(probeMac);
		
		if (oui != null) {
			inputLine = oui.getVendorName();
		}
		inputLine = inputLine.toLowerCase();

		String vendorType = inputLine;

		dev.put("manufactureName", vendorType);
	
		if (inputLine.contains("apple")) {
			dev.put("os", "mac");
		} else if (inputLine.contains("lenovo") || inputLine.contains("asustek") || inputLine.contains("oppo")
				|| inputLine.contains("vivo") || inputLine.contains("lgelectr") || inputLine.contains("sonymobi")
				|| inputLine.contains("motorola") || inputLine.contains("google") || inputLine.contains("xiaomi")
				|| inputLine.contains("oneplus") || inputLine.contains("samsung") || inputLine.contains("htc")
				|| inputLine.contains("gioneeco") || inputLine.contains("zte") || inputLine.contains("huawei")
				|| inputLine.contains("chiunmai")) {
			dev.put("os", "android");
		} else if (inputLine.contains("cisco") || inputLine.contains("ruckus") || inputLine.contains("juniper")
				|| inputLine.contains("d-link") || inputLine.contains("tp-link") || inputLine.contains("compex")
				|| inputLine.contains("ubiquiti") || inputLine.contains("netgear") || inputLine.contains("eero")
				|| inputLine.contains("merunetw") || inputLine.contains("plume") || inputLine.contains("buffalo")
				|| inputLine.contains("mojo") || inputLine.contains("compal") || inputLine.contains("aruba")) {
			dev.put("os", "router");
		} else if (  inputLine.contains("bose")
				   ||inputLine.contains("jbl")) {
			dev.put("os", "speaker");
		} else if (inputLine.contains("canon") || inputLine.contains("roku") || inputLine.contains("nintendo")
				|| inputLine.contains("hp") || inputLine.contains("hewlett")) {
			dev.put("os", "printer");
		} else if (inputLine.contains("microsof")) {
			dev.put("os", "windows");
		} else {
			dev.put("os", "laptop");
		}
		
		return dev;
	}

  @SuppressWarnings("unchecked")
	@RequestMapping(value = "/vap_list", method = RequestMethod.GET)
	public JSONObject vapList(@RequestParam(value = "cid", required = false) String cid,
							  @RequestParam(value = "uid", required = false) String uid,
							  HttpServletRequest request) {
		
		JSONObject devlist     = new JSONObject();
		JSONArray dev_array    = new JSONArray();

		try {

			List<Device> device = null;
			
			if (cid != null) {
				device = getDeviceService().findByCid(cid);
			} else {
				device = getDeviceService().findByUid(uid);
			}

			if (device != null) {

				device.forEach(dv -> {
					
					String devUid 		= dv.getUid();
					String devAlias 	= dv.getAlias();
					String conf  		= dv.getConf();

					net.sf.json.JSONObject template = net.sf.json.JSONObject.fromObject(conf);

					if (conf != null) {

						net.sf.json.JSONArray interfaces2g = null;
						net.sf.json.JSONArray interfaces5g = null;
						
						net.sf.json.JSONArray vap =  new net.sf.json.JSONArray();
						
						if (template.containsKey("interfaces2g")) {
							interfaces2g = template.getJSONArray("interfaces2g");
							vap.addAll(interfaces2g);
						}
						if (template.containsKey("interfaces5g")) {
							interfaces5g = template.getJSONArray("interfaces5g");
							vap.addAll(interfaces5g);
						}

						Iterator<net.sf.json.JSONObject> it = vap.iterator();
						JSONObject vap_object = null;
						
						while (it.hasNext()) {
							
							vap_object = new JSONObject();
							net.sf.json.JSONObject json = it.next();
							
							String ssid 		= (String)json.get("ssid");
							String vap_status	= (String)json.get("bcastssid");
							String vap_state 	= vap_status.equals("1") ? "online" : "offline";
									
							vap_object.put("uid",       	devUid);
							vap_object.put("location",  	devAlias);
							vap_object.put("vap_name",  	ssid);
							vap_object.put("vap_status", 	vap_state);
							dev_array.add(vap_object);
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		devlist.put("network_vap_details", dev_array);

		return devlist;
	}
  
	private ClientDeviceService getClientDeviceService() {
		if (clientDeviceService == null) {
			clientDeviceService = Application.context.getBean(ClientDeviceService.class);
		}
		return clientDeviceService;
	}	
	
	private DeviceService getDeviceService() {
		if (devService == null) {
			devService = Application.context.getBean(DeviceService.class);
		}
		return devService;
	}	
}
