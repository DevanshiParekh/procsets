package com.semaifour.facesix.beacon.rest;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation.SingleValue;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.ElasticService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.entity.elasticsearch.EntityService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.rest.EntityRestController;
import com.semaifour.facesix.rest.FSqlRestController;
import com.semaifour.facesix.rest.NetworkConfRestController;
import com.semaifour.facesix.session.SessionCache;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;

/**
 * 
 * Rest BLE Device Controller handles all rest calls for network configuration
 * 
 * @author mjs
 *
 */
@Controller
@RestController
@RequestMapping("/rest/beacon/ble/networkdevice")
public class BLENetworkDeviceRestController extends WebController {
	
	static Logger LOG = LoggerFactory.getLogger(BLENetworkDeviceRestController.class.getName());
	static Font smallBold 	= new Font(Font.FontFamily.TIMES_ROMAN, 12, 	Font.BOLD);
    static Font catFont 	= new Font(Font.FontFamily.TIMES_ROMAN, 18, 	Font.BOLD);
    static Font redFont 	= new Font(Font.FontFamily.TIMES_ROMAN, 12, 	Font.NORMAL);
    static Font subFont 	= new Font(Font.FontFamily.TIMES_ROMAN, 16,     Font.BOLD);
    static boolean ThreadTobeStarted = false;
    DateFormat parse 			   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    DateFormat format 			   = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    
	
	@Autowired
	NetworkDeviceService 	networkDeviceService;
	
	@Autowired
	BeaconService 	beaconService;
	
	@Autowired
	SiteService siteService;
	
	@Autowired
	PortionService portionService;	
	   
	@Autowired
	FSqlRestController 		fsqlRestController;
	
	@Autowired
	EntityRestController 	entity;
	
	@Autowired
	BeaconDeviceService beacondeviceService;
	
	@Autowired
	SessionCache sessionCache;

	@Autowired
	ClientDeviceService clientService;
	
	@Autowired
	EntityRestController  entityRestController;
	
	@Autowired
	EntityService entityService;
	
	@Autowired
	GeoFinderRestController  geoFinderRestController;
	
	@Autowired
	NetworkConfRestController netRestController;
	
	@Autowired
	NetworkDeviceService 	_networkDeviceService;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	ElasticService elasticService;
		
	String 	device_history_event = "device-history-event";
	
	private String indexname = "facesix*";

	private int num_snr 	= 0;
	private int num_svi 	= 0;
	private int num_swi 	= 0;
	private int num_flr 	= 0;
	private int num_ap 		= 0;	

	
	List<Map<String, Object>> peer_txrxlist = null;
	Map<String, Object> vap_map  			= null;
	Map<String, Object> vap5g_map  			= null;

	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	TimeZone timezone     		   = null;
	
	@PostConstruct
	public void init() {
		peer_txrxlist 	= new ArrayList<Map<String, Object>>();
		vap_map  		= new HashMap<String, Object>();
		vap5g_map		= new HashMap<String, Object>();

		indexname 		= _CCC.properties.getProperty("elasticsearch.indexnamepattern", "facesix*");
		device_history_event = _CCC.properties.getProperty("facesix.device.event.history.table",device_history_event);

	}
	
	
	/*
	 * 
	 * Builds an array condition condition + vap_mac:[ list[0].getUid(), list[i].getUid() ]
	 * 
	 */
	public static String buildArrayCondition(List<NetworkDevice> list, String fieldname) {
		if (list.size() > 0) {
    		StringBuilder sb = new StringBuilder(fieldname).append(":(");
    		boolean isFirst = true;
    		for (NetworkDevice nd : list) {
    			if (nd.getTypefs().equals("sensor") || nd.getParent().equals("ble")) {
	    			if (isFirst) {
	    				isFirst = false;
	    			} else {
	    				sb.append(" OR ");
	    			}
	    			sb.append("\"").append(nd.getUid()).append("\"");
    			}
    		}
    		sb.append(")");
    		return sb.toString();
		} else {
			return "";
		}
	}
	
	
	public static String buildBeaconDeviceArrayCondition(List<BeaconDevice> list, String fieldname) {
		if (list.size() > 0) {
    		StringBuilder sb = new StringBuilder(fieldname).append(":(");
    		boolean isFirst = true;
    		for (BeaconDevice beacon : list) {
	    			if (isFirst) {
	    				isFirst = false;
	    			} else {
	    				sb.append(" OR ");
	    			}
	    			sb.append("\"").append(beacon.getUid()).append("\"");
    		}
    		sb.append(")");
    		return sb.toString();
		} else {
			return "";
		}
	}
	
    @RequestMapping(value = "/peercount", method = RequestMethod.GET)
    public  int peercount(@RequestParam(value="sid",  required=false)String sid,
    					  @RequestParam(value="spid", required=false)String spid, 
    					  @RequestParam(value="swid", required=false)String swid,
    					  @RequestParam(value="cid", required=false)String cid) {
    
    	int device_count = 0;
	   	
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}
    	
		if (customerUtils.trilateration(cid)) {
			
			String state  		= "active";
			String status 		= "checkedout";			
			List<Beacon> beacon = beaconService.getSavedBeaconByCidSpidStateAndStatus(cid,spid,state,status);
			if (beacon != null) {
				device_count 	= beacon.size();
			}
		} else {
		   	List<NetworkDevice> devices = networkDeviceService.findBy (spid, sid, swid);
	    	
	    	if (devices == null) {
	    		return  0;
	    	}
			
			if (devices.size() > 0) {
	    		for (NetworkDevice nd : devices) { 
	    			device_count = device_count + nd.getActivetag();
	    		}
			}			
		}
    	
 
		//LOG.info("peercount" +device_count);
    	return device_count;
    }
    
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/climap", method = RequestMethod.GET)
    public  JSONObject climap(@RequestParam(value="sid", required=true)String sid) {
		
		JSONArray  dev_array = null;
		JSONObject devlist 	 = new JSONObject();
		
		dev_array = new JSONArray();
		JSONArray  dev_array1 = new JSONArray();
		JSONArray  dev_array2 = new JSONArray();
		
		dev_array1.add (0, "2G");
		//dev_array1.add (1, cnt24);
		dev_array2.add (0, "5G");
		//dev_array2.add (1, cnt5);
		
		dev_array.add  (0, dev_array1);
		dev_array.add  (1, dev_array2);
		
		devlist.put("typeOfDevices", dev_array);	
		
		//LOG.info("typeOfDevices" +devlist.toString());		
    	
    	return devlist;
    }
    
    @RequestMapping(value = "/rxtx", method = RequestMethod.GET)
    public  List<Map<String, Object>> rxtx(@RequestParam(value="sid",  required=false)  String sid, 
    								 	   @RequestParam(value="spid", required=false)  String spid,
    								 	   @RequestParam(value="swid", required=false)  String swid,
    								 	   @RequestParam(value="uid",  required=false)  String uid,
    								 	   @RequestParam(value="time", required=false) String time,
    								 	   @RequestParam(value="place", required=false) String place,
    								 	   HttpServletRequest request, HttpServletResponse response) {
    	String fsql   = null;
    	
    	//LOG.info("***uid  " +uid);
		try {
			if (time == null || time.isEmpty() || time.equals("undefined")) {
				time = "12h";
			}
			
			int size = 10;
			if (place != null && place.equals("report")) {
				size = 2000;
			}
			if (((Boolean) sessionCache.getAttribute(request.getSession(), "demo")) == true) {
				fsql = "index=qubercomm_*,sort=timestamp desc,size="+size+",query=";
			} else {
				fsql = "index="+indexname +",sort=timestamp desc,size="+size+",query=timestamp:>now-"+time + " AND ";
			}
		} catch (Exception e) {
			fsql = "index=" + indexname + ",sort=timestamp desc,size=10,query=timestamp:>now-" + time + " AND ";
		}
		
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}
        	
     	List<NetworkDevice> devices = networkDeviceService.findBy(spid, sid, swid);
     	
    	List<Map<String, Object>>  logs   = EMPTY_LIST_MAP;
    	
    	if (devices != null && uid == null) {
    		String uidbuilder = buildArrayCondition (devices, "uid");
    		if (uidbuilder.length() > 0) {
	        	fsql = fsql + uidbuilder;
	        	fsql = fsql + " AND opcode:\"system_stats\"|value(downlink,Rx,NA);value(uplink,Tx,NA);value(timestamp,time,NA)|table";    	        	
	        	logs   =  fsqlRestController.query(fsql);	        	
    		} else {
    			//LOG.info("Oops No infrastructure available ");
    		}
        	
    	} else if (uid != null) {
    		uid = uid.toUpperCase();
    		fsql = fsql + "uid:\"" + uid + "\"";
    		fsql = fsql + " AND opcode:\"system_stats\"|value(downlink,Rx,NA);value(uplink,Tx,NA);value(timestamp,time,NA)|table";
        	logs   =  fsqlRestController.query(fsql);		
    	}
    	    	    	
    	//LOG.info("BLE RXTX ==> " + logs);
    		//LOG.info("BLE RXTX fsql ==> " + fsql);
    	
    	return logs;

    }
    
    @RequestMapping(value = "/venueagg", method = RequestMethod.GET)
    public List<Map<String, Object>> venueagg(
    		@RequestParam(value="sid", required=true) String sid, 
    		@RequestParam(value="cid", required=false) String cid,
    		HttpServletRequest request, HttpServletResponse response) {
    
    	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
    	
    	Map<String, Object> map  = new HashMap<String, Object>();
    	Map<String, Object> tmap = new HashMap<String, Object>();
    	Map<String, Object> omap = new HashMap<String, Object>();
    	Map<String, Object> imap = new HashMap<String, Object>();
    	Map<String, Object> idmap= new HashMap<String, Object>();
    	
		List<NetworkDevice> ulist  = networkDeviceService.findBySid(sid);
		List<NetworkDevice> mlist  = Collections.unmodifiableList(ulist);
		List<NetworkDevice> list   = new ArrayList<NetworkDevice>(mlist);
		
		Collections.sort(list);    	

    	int ap  = 0;
    	int cli = 0;
    	int bt  = 0;
    	
    	int activetag 		= 0;
    	int checkedoutTag  	= 0;
    	int checkedinTag  	= 0;
    	int inactiveTag  	= 0;
    	int idleTag			= 0;
    	
    	try {
    		
			if (customerUtils.trilateration(cid)) {
				
				String status ="checkedout";
				Collection<Beacon> checkedout = beaconService.getSavedBeaconByCidAndStatus(cid, "checkedout");
				Collection<Beacon> checkedin  = beaconService.getSavedBeaconByCidAndStatus(cid, "checkedin");
				List<Beacon> inactive 		  = beaconService.getSavedBeaconByCidSidStateAndStatus(cid, sid, "inactive", status);
				List<Beacon> idle 			  = beaconService.getSavedBeaconByCidSidStateAndStatus(cid, sid, "idle", status);
				List<Beacon> active 		  = beaconService.getSavedBeaconByCidSidStateAndStatus(cid, sid, "active", status);

				checkedoutTag = checkedout==null?0:checkedout.size();
				checkedinTag  = checkedin==null?0:checkedin.size();
				
				inactiveTag = inactive == null ? 0 : inactive.size();
				idleTag 	= idle == null ?0 : idle.size();
				activetag   = active == null ? 0 : active.size();

			} else {
    			
    			for (NetworkDevice nd : list) {
    	    		
    	    		if (nd.getTypefs().equals("ap") && nd.getStatus().equals("active")) {
    	    			ap++;
    	    			cli = cli + nd.getActiveClient();
    	    		}
    	    		
    	    		if (nd.getTypefs().equals("sensor") || nd.getParent().equals("ble")) {
    	    			bt++;
    	    			activetag = activetag + nd.getActivetag();
    					checkedoutTag = checkedoutTag + nd.getCheckedoutTag();
    					checkedinTag  =  checkedinTag + nd.getCheckedintag();
    	    		}	    		
    	    	}
    		}
    		
	    
	    	omap.put("Status", "ChkOut");
	    	omap.put("Tags", checkedoutTag);
	    	ret.add(omap);	
	    	
	    	imap.put("Status", "ChkIn");
	    	imap.put("Tags", checkedinTag);
	    	ret.add(imap);
	    	
	    	tmap.put("Status", "Active");
	    	tmap.put("Tags", activetag);
	    	ret.add(tmap);
	    	
	    	if(!customerUtils.entryexit(cid)){
	    		idmap.put("Status", "Idle");
	    		idmap.put("Tags", 	idleTag);
		    	ret.add(idmap);
	    	}
	    	
    		map.put("Status", "Inactive");
    		map.put("Tags", 	inactiveTag);
	    	ret.add(map);
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return ret;
    }
    
    @RequestMapping(value = "/flraggr", method = RequestMethod.GET)
    public List<Map<String, Object>> flraggr(@RequestParam(value="spid", required=true) String spid, 
    										 @RequestParam(value="time", required=false, defaultValue="120") String time,
    										 HttpServletRequest request, HttpServletResponse response) {
    
    	Map<String, Object> map = null;
    	List<Map<String, Object>> rxtx = null;
    	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
    	
    	rxtx = rxtxagg(null, spid, null, null, time, "20", "BLE", request, response);
		if (rxtx.size() > 0 ) {
			map = rxtx.get(0);
			map.put("Radio", "BLE");
			ret.add(map);
		}		
    	    	
   // 	LOG.info("FLOOR MAP STR" + ret.toString());
    	return ret;
    }    
    
    @RequestMapping(value = "/peeraggr", method = RequestMethod.GET)
    public List<Map<String, Object>> peeraggr(@RequestParam(value="uid", required=true) String uid, 
    										  @RequestParam(value="time", required=false, defaultValue="120") String time) {
    
    	Map<String, Object> map = null;
    	if (peer_txrxlist.size() > 0) {
	    	map = peer_txrxlist.get(0);
	    	String id = (String) map.get("uid");
	    	if (uid.equals(id)) {
	    		return peer_txrxlist;
	    	}
    	}
    	
    	return EMPTY_LIST_MAP;
    }    
    
    // Exit Tag Count 
    
    @RequestMapping(value = "/exittag", method = RequestMethod.GET)
    public  int checkedout(@RequestParam(value="sid",  required=false)String sid,
    					  @RequestParam(value="spid", required=false)String spid, 
    					  @RequestParam(value="swid", required=false)String swid) {
    
    	int device_count = 0;
	   	
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}
    	
    	List<NetworkDevice> devices = networkDeviceService.findBy (spid, sid, swid);
    	
    	if (devices == null) {
    		return  0;
    	}
		
		if (devices.size() > 0) {
    		for (NetworkDevice nd : devices) { 
				device_count = device_count + nd.getExitTag();
    		}
		}
//		LOG.info("Exit Tag Count <><><><><><><> " +device_count);
		
    	return device_count;
    }
     
    // Active Receiver Count
    
    @RequestMapping(value = "/activedevices", method = RequestMethod.GET)
    public  int activedevices(@RequestParam(value="sid",  required=false)String sid,
    					  	@RequestParam(value="spid", required=false)String spid, 
    					  	@RequestParam(value="swid", required=false)String swid) {
    
    	int activeRecv = 0;
	   	
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}
    	
    	List<NetworkDevice> devices = networkDeviceService.findBy (spid, sid, swid);
    	
    	if (devices == null) {
    		return  0;
    	}
		
    	try {
    		if (devices.size() > 0) {
        		for (NetworkDevice nd : devices) {     			
        			//LOG.info( "type FS  " + nd.getTypefs());
        			if (nd.getTypefs().equals("sensor") || nd.getParent().equals("ble")) {
        				//LOG.info("BLE Type " + nd.bleType);
    					if (nd.getStatus() != null && nd.getStatus().equals("active") && nd.bleType.equalsIgnoreCase("receiver")) {
        						activeRecv++;
        				}
        			}
    					
        		}
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}
		
    		
	//	LOG.info("active reciver count <><><><><><><> " +activeRecv);
		
    	return activeRecv;
    }
    
    
    @SuppressWarnings("unused")
	@RequestMapping(value = "/rxtxagg", method = RequestMethod.GET)
    public   List<Map<String, Object>> rxtxagg(@RequestParam(value="sid", required=false) String sid, 
    								 	      @RequestParam(value="spid", required=false) String spid,
    								 	      @RequestParam(value="uid", required=false) String uid,
    								 	      @RequestParam(value="swid", required=false) String swid,
    								 	      @RequestParam(value="time", required=false, defaultValue="12") String time,
    								 	      @RequestParam(value="interval", required=false, defaultValue="2") String interval,
    								 	      @RequestParam(value="radio", required=false, defaultValue="2G5G") String radio,
    								 	      HttpServletRequest request, HttpServletResponse response) {
    	String esql = "";
    	int count   = 0;
    	time = time+"h";
    	interval = interval+"h";
    	if (time.equals("12")) {
    		interval = "12h";
    	} else if (time.equals("18")) {
    		interval = "18h";
    	}else if (time.equals("24")) {
    		interval = "24h";
    	} else {
    		interval = "12h";
    	}
    	
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}    	
    	
    	List<NetworkDevice> devices = networkDeviceService.findBy(spid, sid, swid);
    	
    	if (devices != null && uid == null) {
    		String uidBuidler = buildArrayCondition(devices, "uid");
    		if (uidBuidler.length() > 0) {
    			esql = esql + uidBuidler;
    		} else {
    			return EMPTY_LIST_MAP;
    		}
        	
    	} else if (uid != null) {
    		esql = esql + "uid:\"" + uid + "\"";
    	}
    	
    	if (radio.equals("BLE")) {
        	esql = esql + "AND opcode:\"system_stats\"";       	
    	}	
    	
		try {
			if (((Boolean) sessionCache.getAttribute(request.getSession(), "demo")) == false) {
				esql = esql + " AND timestamp:>now-" + time;
			}
		} catch (Exception e) {
				esql = esql + " AND timestamp:>now-" + time;
		}
       
    	//LOG.info("ESQL" + esql);
    	
    	if (esql != null) {
    		QueryBuilder builder = QueryBuilders.queryStringQuery(esql);
	    	SearchQuery sq = new NativeSearchQueryBuilder()
	    					.withQuery(builder)
	    					.withSort(new FieldSortBuilder("timestamp").order(SortOrder.DESC))
	    					.addAggregation(AggregationBuilders.dateHistogram("bucket")
	    						.field("timestamp")
	    						.interval(new DateHistogramInterval(interval))
	    						.minDocCount(1)
	    						.subAggregation(AggregationBuilders.min("min_vap_rx_bytes").field("_vap_rx_bytes"))
	    						.subAggregation(AggregationBuilders.max("max_vap_rx_bytes").field("_vap_rx_bytes"))
	    						.subAggregation(AggregationBuilders.avg("avg_vap_rx_bytes").field("_vap_rx_bytes"))
	    						.subAggregation(AggregationBuilders.min("min_vap_tx_bytes").field("_vap_tx_bytes"))
	    						.subAggregation(AggregationBuilders.max("max_vap_tx_bytes").field("_vap_tx_bytes"))
	    						.subAggregation(AggregationBuilders.avg("avg_vap_tx_bytes").field("_vap_tx_bytes"))
	    						.subAggregation(AggregationBuilders.avg("avg_ble_rx_bytes").field("downlink"))
	    						.subAggregation(AggregationBuilders.avg("avg_ble_tx_bytes").field("uplink"))
	    						).build();
	    	sq.addIndices(indexname);

	    	sq.setPageable(new PageRequest(0,1));
	    	
	    	Histogram histogram = _CCC.elasticsearchTemplate.query(sq, new ResultsExtractor<Histogram>() {
				@Override
				public Histogram extract(SearchResponse response) {
					return response.getAggregations().get("bucket");
				}
			});
	    	
	    	List<Map<String, Object>> rxtx = new ArrayList<Map<String, Object>>();
	    	Map<String, Object> map = null;
	    	for (Histogram.Bucket entry : histogram.getBuckets()) {
	    		count++;
	    		map = new HashMap();
	    		map.put("time", entry.getKey().toString());
	    		List<Aggregation> aggs = entry.getAggregations().asList();
	    		for(Aggregation agg : aggs) {
	    			String name = agg.getName();
	    			map.put(agg.getName(), ((SingleValue)agg).value());
	    			
	    		}
	    		
	    		rxtx.add(map);
	    		
	    		if (count >= 10) {
	    			break;
	    		}
	    		
	    	}
	    	
	    	//LOG.info("RXTX AFFR " + rxtx);
	    	return rxtx;
    	} else {
    		return EMPTY_LIST_MAP;
    	}
    }
    
    @RequestMapping(value = "/netflow", method = RequestMethod.GET)
    public  List<Map<String, Object>> netflow(@RequestParam(value="sid",  required=false) String sid, 
    								 	      @RequestParam(value="spid", required=false) String spid,
    								 	      @RequestParam(value="swid", required=false) String swid,
    								 	      @RequestParam(value="uid",  required=false) String uid,
    								 	      @RequestParam(value="time", required=false, defaultValue="5") String time,
    								 	      HttpServletRequest request, HttpServletResponse response) {
    	String fsql   = null;
    	
		try {
			if (((Boolean) sessionCache.getAttribute(request.getSession(), "demo")) == true) {
				fsql = "index=qubercomm_*,sort=timestamp desc,size=10,query=";
			} else {
				fsql = "index="+indexname +",sort=timestamp desc,size=10,query=timestamp:>now-" + time + "m" + " AND ";
			}
		} catch (Exception e) {
				fsql = "index="+indexname +",sort=timestamp desc,size=10,query=timestamp:>now-" + time + "m" + " AND ";
		}

    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}

    	List<NetworkDevice> devices = networkDeviceService.findBy(spid, sid, swid);
    	    	
    	if (devices != null && uid == null) {
    		String uidbuilder = buildArrayCondition(devices, "uid");
    		if (uidbuilder.length() > 0) {
	        	fsql = fsql + uidbuilder;
	        	fsql = fsql + " AND web_stats:\"Qubercloud Manager\"|value(num_social,social,NA);value(num_chat,chat,NA);value(num_ecomm,ecom,NA);value(num_http,web,NA);value(timestamp,time,NA)|table";
	        	return fsqlRestController.query(fsql);
    		}
    	} else if (uid != null) {
    		fsql = fsql + "uid:\"" + uid + "\"";
    		fsql = fsql + " AND web_stats:\"Qubercloud Manager\"|value(num_social,social,NA);value(num_chat,chat,NA);value(num_ecomm,ecom,NA);value(num_http,web,NA);value(timestamp,time,NA)|table";
        	return fsqlRestController.query(fsql);    		
    	}
    	
    	return EMPTY_LIST_MAP;
    }
    
    
    @RequestMapping(value = "/netagg", method = RequestMethod.GET)
    public   List<Map<String, Object>> netagg(@RequestParam(value="sid", required=false) String sid, 
    								 	      @RequestParam(value="spid", required=false) String spid,
    								 	      @RequestParam(value="uid", required=false) String uid,
    								 	      @RequestParam(value="swid", required=false) String swid,
    								 	      @RequestParam(value="time", required=false, defaultValue="30") String time,
    								 	      @RequestParam(value="interval", required=false, defaultValue="5") String interval,
    								 	     HttpServletRequest request, HttpServletResponse response) {
    	String esql = "";
    	int count   = 0;
    	time = time+"m";
    	interval = interval+"m";
    	if (time.equals("5")) {
    		interval = "1m";
    	} else if (time.equals("15")) {
    		interval = "2m";
    	} else if (time.equals("30")) {
    		interval = "5m";
    	} else if (time.equals("60")) {
    		interval = "10m";
    	}else if (time.equals("120")) {
    		interval = "20m";
    	}
    	
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}    	
    	
    	List<NetworkDevice> devices = networkDeviceService.findBy(spid, sid, swid);
    	
    	if (devices != null && uid == null) {
        	esql = esql + buildArrayCondition(devices, "uid");
        	esql = esql + "AND web_stats:\"Qubercloud Manager\"";
    	} else if (uid != null) {
    		esql = esql + "uid:\"" + uid + "\"";
    		esql = esql + "AND web_stats:\"Qubercloud Manager\"";
    	}
    	
		try {
			if (((Boolean) sessionCache.getAttribute(request.getSession(), "demo")) == false) {
				esql = esql + " AND timestamp:>now-" + time;
			}
		} catch (Exception e) {
				esql = esql + " AND timestamp:>now-" + time;
		}
        
        
    	if (esql != null) {
    		QueryBuilder builder = QueryBuilders.queryStringQuery(esql);
	    	SearchQuery sq = new NativeSearchQueryBuilder()
	    					.withQuery(builder)
	    					.withSort(new FieldSortBuilder("timestamp").order(SortOrder.DESC))
	    					.addAggregation(AggregationBuilders.dateHistogram("bucket")
	    						.field("timestamp")
	    						.interval(new DateHistogramInterval(interval))
	    						.minDocCount(1)
	    						.subAggregation(AggregationBuilders.min("min_num_social").field("web_social_count"))
	    						.subAggregation(AggregationBuilders.max("max_num_social").field("web_social_count"))
	    						.subAggregation(AggregationBuilders.avg("avg_num_social").field("web_social_count"))
	    						.subAggregation(AggregationBuilders.min("min_num_chat").field("web_chat_count"))
	    						.subAggregation(AggregationBuilders.max("max_num_chat").field("web_chat_count"))	    						
	    						.subAggregation(AggregationBuilders.avg("avg_num_chat").field("web_chat_count"))
	    						.subAggregation(AggregationBuilders.min("min_num_ecomm").field("web_ecomm_count"))
	    						.subAggregation(AggregationBuilders.max("max_num_ecomm").field("web_ecomm_count"))		    						
	    						.subAggregation(AggregationBuilders.avg("avg_num_ecomm").field("web_ecomm_count"))
	    						.subAggregation(AggregationBuilders.min("min_num_http").field("web_http_count"))
	    						.subAggregation(AggregationBuilders.max("max_num_http").field("web_http_count"))	    						
	    						.subAggregation(AggregationBuilders.avg("avg_num_http").field("web_http_count"))
	    						).build();
	    	sq.addIndices(indexname);
	    	//sq.addTypes("message");
	    	sq.setPageable(new PageRequest(0,1));
	    	
	    	Histogram histogram = _CCC.elasticsearchTemplate.query(sq, new ResultsExtractor<Histogram>() {
				@Override
				public Histogram extract(SearchResponse response) {
					return response.getAggregations().get("bucket");
				}
			});
	    	
	    	List<Map<String, Object>> net = new ArrayList<Map<String, Object>>();
	    	Map<String, Object> map = null;
	    	for (Histogram.Bucket entry : histogram.getBuckets()) {
	    		count++;
	    		map = new HashMap();
	    		map.put("time", entry.getKey());
	    		List<Aggregation> aggs = entry.getAggregations().asList();
	    		for(Aggregation agg : aggs) {
	    			String name = agg.getName();
	    			map.put(agg.getName(), ((SingleValue)agg).value());
	    			
	    		}
	    		net.add(map);
	    		if (count >= 10) {
	    			break;
	    		}
	    		
	    	}
	    	return net;
    	} else {
    		return EMPTY_LIST_MAP;
    	}
    }    
    
    @RequestMapping(value = {"status", "status/all"}, method = RequestMethod.GET)
    public  List<NetworkDevice> status(@RequestParam(value="sid",  required=false) String sid, 
								 	   @RequestParam(value="spid", required=false) String spid,
								 	   @RequestParam(value="swid", required=false) String swid,
								 	   @RequestParam(value="uid",  required=false) String uid,
								 	   @RequestParam(value="duration", required=false, defaultValue="5m") String duration,
								 	  HttpServletRequest request, HttpServletResponse response) {
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}
    	return getstatus(sid, spid, swid, uid, duration, "all", request, response);
    }
    
    @RequestMapping(value = "status/active", method = RequestMethod.GET)
    public  List<NetworkDevice> statusactive(@RequestParam(value="sid",  required=false) String sid, 
    								   		 @RequestParam(value="spid", required=false) String spid,
    								   		 @RequestParam(value="swid", required=false) String swid,
    								   		 @RequestParam(value="duration", required=false, defaultValue="5m") String duration,
    								   		 HttpServletRequest request, HttpServletResponse response) {
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}
    	return getstatus(sid, spid, swid, null, duration, "active", request, response);
    }
    
    @RequestMapping(value = "status/inactive", method = RequestMethod.GET)
    public  List<NetworkDevice> statusinactive(@RequestParam(value="sid",  required=false) String sid, 
    								   		   @RequestParam(value="spid", required=false) String spid,
    								   		   @RequestParam(value="swid", required=false) String swid,
    								   		   @RequestParam(value="duration", required=false, defaultValue="5m") String duration,
    								   		   HttpServletRequest request, HttpServletResponse response) {
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}
    	return getstatus(sid, spid, swid, null, duration, "inactive", request, response);
    }
    	
    public  List<NetworkDevice> getstatus (	 @RequestParam(value="sid",  required=false) String sid, 
    								 	     @RequestParam(value="spid", required=false) String spid,
    								 	     @RequestParam(value="swid", required=false) String swid,
    								 	     @RequestParam(value="uid",  required=false) String uid,
    								 	     @RequestParam(value="duration", required=false, defaultValue="5m") String duration,
    								 	     @PathVariable("status") String status,
    								 	     HttpServletRequest request, HttpServletResponse response) {
    	
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}

    	List<NetworkDevice> devies = networkDeviceService.findBy(spid, sid, swid);

    	if (devies != null && devies.size() > 0 && uid == null) {
    		String fsql = null; // "index=" + indexname + ",query=timestamp:>now-" + duration + " AND ";

			try {
				if (((Boolean) sessionCache.getAttribute(request.getSession(), "demo")) == true) {
					fsql = "index=qubercomm_*,sort=timestamp desc,size=10,query=";
				} else {
					fsql = "index=" + indexname + ",query=timestamp:>now-" + duration + " AND ";
				}
			} catch (Exception e) {
					fsql = "index=" + indexname + ",query=timestamp:>now-" + duration + " AND ";
			}
    		
    	
    		fsql = fsql + buildArrayCondition(devies, "vap_mac") + "|bucket(vap_mac,uid,NA);value(timestamp,time,vindex=0);|table";

    		Map<String, Map<String, Object>>  logs = fsqlRestController.queryMap(fsql, "uid");
    		List<NetworkDevice> actives = new ArrayList<NetworkDevice>();
    		List<NetworkDevice> inactives = new ArrayList<NetworkDevice>();

    		for(NetworkDevice dv : devies) {
    			Map<String, Object> m = logs.get(dv.getUid());
    			if (m != null) {
    				dv.setStatus("ACTIVE");
    				actives.add(dv);
    			} else {
    				dv.setStatus("INACTIVE");
    				inactives.add(dv);
    			}
    		}
    		
    		if ("ACTIVE".equalsIgnoreCase(status)) {
    			devies = actives;
    		} else if ("INACTIVE".equalsIgnoreCase(status)) {
    			devies = inactives;
    		}

    	}
    	return devies;
    }

    @RequestMapping(value = "/alerts", method = RequestMethod.GET)
    public  List<String> alerts(@RequestParam(value="sid",  required=false) String sid, 
    								 	     @RequestParam(value="spid", required=false) String spid,
    								 	     @RequestParam(value="swid", required=false) String swid,
    								 	     @RequestParam(value="uid",  required=false) String uid,
    								 	     @RequestParam(value="duration", required=false, defaultValue="30m") String duration,
    								 	    HttpServletRequest request, HttpServletResponse response) {
    	
    	String val;
    	String str = null;
    	ArrayList<String> alert = new ArrayList<String>();
    	
    	List<NetworkDevice> devices = null;
    	
    	if (sid != null){
    		devices = networkDeviceService.findBySid(sid);
    	} else  if (spid != null) {
    		devices = networkDeviceService.findBySpid(spid);
    	}
    	    	    	
    	if (devices != null) {

    		for (NetworkDevice nd : devices) {
    			
    			if (nd.getTypefs().equals("sensor") || nd.getParent().equals("ble")) {
	    			if (nd.getStatus().equals("inactive")) {
	        			val = nd.getUid();
	    				str = "Device MAC "+val+" Status " +  nd.getStatus();
	    				alert.add(str); 
	    			}				
    			}

    		}
    	}
    	
    	
    	return alert;
    }
    
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public  List<String> summary(@RequestParam(value="sid",  required=false) String sid, 
    								 	      @RequestParam(value="spid", required=false) String spid,
    								 	      @RequestParam(value="swid", required=false) String swid,
    								 	      @RequestParam(value="uid",  required=false) String uid,
    								 	      @RequestParam(value="duration", required=false, defaultValue="30m") String duration,
    								 	     HttpServletRequest request, HttpServletResponse response) {
   	
    	ArrayList<String> summary = new ArrayList<String>();
    	String str = "Number of Floors = " + num_flr;
    			
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}

    	List<NetworkDevice> devices = networkDeviceService.findBy (spid, sid, swid);

    	if (devices != null && devices.size() > 0) { 
    		
    		str = "Number of Floors =====================>  "  + num_flr;    		
    		summary.add(str);
    		
    		str = "Number of Servers ====================>  " + num_svi;
    		summary.add(str);

    		str = "Number of Switches ===================>  " + num_swi;
    		summary.add(str);

    		str = "Number of Access Points ===============>  " + num_ap;
    		summary.add(str);

    		str = "Number of Sensors ====================> " + num_snr;
    		summary.add(str);
    	} 
    	
    	return summary;
    }    
       
    @RequestMapping(value = "/getcpu", method = RequestMethod.GET)
    public  List<Map<String, Object>> getcpu(@RequestParam(value="sid",  required=false) String sid, 
    								 	     @RequestParam(value="spid", required=false) String spid,
    								 	     @RequestParam(value="swid", required=false) String swid,
    								 	     @RequestParam(value="uid",  required=false) String uid,
    								 	     @RequestParam(value="place",  required=false) String place,
    								 	     @RequestParam(value="duration", required=false, defaultValue="30m") String duration) {
    	
    	List<Map<String, Object>>  res  = EMPTY_LIST_MAP;
    	
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}
   	
    	int size = 1;
    	if(place != null && place.equals("report")){
    		size = 2000;
    	}
    	
    	List<NetworkDevice> devices = networkDeviceService.findBy(spid, sid, swid);
		//String fsql = "index=" + indexname + ",sort=timestamp desc,size=1,query=opcode:\"system_stats\" AND timestamp:>now-30m AND ";
		String fsql = "index=" + indexname + ",sort=timestamp desc,size="+size+",query=opcode:\"system_stats\" AND timestamp:>now-" + duration + " AND ";


    	if (devices != null && devices.size() > 0 && uid == null) {
    	
    		fsql = fsql + buildArrayCondition(devices, "uid") + "|value(uid,uid,NA);value(cpu_percentage,cpu,NA);value(timestamp,time,NA);|table";
    		//LOG.info("FSQL CPU =" + fsql);
    		res =  fsqlRestController.query(fsql);
    		//LOG.info( "RESULT QUERY" + res);
    		return res;
    		
    	} else if (uid != null){
    		uid =uid.toUpperCase();
    		fsql = fsql + "uid:\"" + uid + "\"";
    		fsql = fsql + "|value(uid,uid,NA);value(cpu_percentage,cpu,NA);value(timestamp,time,NA);|table";
    		//LOG.info("FSQL CPU1 =" + fsql);
    		res =   fsqlRestController.query(fsql);	
    		//LOG.info( "RESULT QUERY" + res);
    		return res;
    		
    	} else {
    		return EMPTY_LIST_MAP;
    	}
    }
    
    
    @RequestMapping(value = "/deviceupstate", method = RequestMethod.GET)
    public  List<Map<String, Object>>  DeviceUpTime(@RequestParam(value="uid",required=true) String uid,
    												@RequestParam(value="duration", required=false, defaultValue="30m") String duration) {
    	
    	List<Map<String, Object>>  res  = EMPTY_LIST_MAP;

    	uid = uid.toUpperCase();
    	
		String fsql =    " index=" + indexname + ",sort=timestamp desc,size=1,query=opcode:\"system_stats\" "
						+" AND timestamp:>now-"+duration+" AND uid:\"" + uid +"\" |value(uid,uid,NA);"
						+" value(cpu_percentage,cpu,NA);value(ram_percentage,ram_value,NA);" 
						+" value(cpu_days,cpuDays,NA);value(cpu_hours,cpuHours,NA);value(cpu_minutes,cpuMinutes,NA);" 
						+" value(app_days,appDays,NA);value(app_hours,appHours,NA);value(app_minutes,appMinutes,NA);" 
						+" value(uplink,bletx,NA);value(downlink,blerx,NA);value(timestamp,time,NA);|table ";
		
		res = fsqlRestController.query(fsql);
		
		return res;
    }
    
    @RequestMapping(value = "/getmem", method = RequestMethod.GET)
    public  List<Map<String, Object>> getmem(@RequestParam(value="sid",  required=false) String sid, 
    								 	     @RequestParam(value="spid", required=false) String spid,
    								 	     @RequestParam(value="swid", required=false) String swid,
    								 	     @RequestParam(value="uid",  required=false) String uid,
    								 	     @RequestParam(value="place",  required=false) String place,
    								 	     @RequestParam(value="duration", required=false, defaultValue="30m") String duration) {
    	
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}
    	

    	int size = 1;
    	if(place != null && place.equals("report")){
    		size = 2000;
    	}
    	
    	List<NetworkDevice> devices = networkDeviceService.findBy(spid, sid, swid);

    	//String fsql = "index=" + indexname + ",sort=timestamp desc,size=1,query=opcode:\"system_stats\" AND timestamp:>now-30m AND ";
		String fsql = "index=" + indexname + ",sort=timestamp desc,size="+size+",query=opcode:\"system_stats\" AND timestamp:>now-" + duration + " AND ";
    	
    	if (devices != null && devices.size() > 0 && uid == null) {
    	
    		fsql = fsql + buildArrayCondition(devices, "uid") + "|value(uid,uid,NA);value(ram_percentage,mem,NA);value(timestamp,time,NA);|table";
    		return fsqlRestController.query(fsql);
    	} else if (uid != null){
    		uid  = uid.toUpperCase();
    		fsql = fsql + "uid:\"" + uid + "\"";
    		fsql = fsql + "|value(uid,uid,NA);value(ram_percentage,mem,NA);value(timestamp,time,NA);|table";

    		return fsqlRestController.query(fsql);	
    	} else {
    		return EMPTY_LIST_MAP;
    	}
    } 
    
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/gettags", method = RequestMethod.GET)
    public JSONObject gettags(@RequestParam(value="sid", 	 	required=false) String sid, 
    						   @RequestParam(value="spid", 	 	required=false) String spid,
    						   @RequestParam(value="swid", 	 	required=false) String swid,
    						   @RequestParam(value="uid", 	 	required=false) String uid,
    						   @RequestParam(value="duration", 	required=false, defaultValue="5m") String duration) throws IOException {

    	JSONArray  dev_array = new JSONArray();
    	
    	JSONObject devlist 	 = new JSONObject();
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}
		
		int tagcount   = 0;
		int activeRecv = 0;
		int scanner	   = 0;
		int server	   = 0;
    	
    	List<NetworkDevice> devices = networkDeviceService.findBy(spid, sid, swid);
    	
    	if (devices != null && uid == null) {
    		
	    	for (NetworkDevice nd : devices) { 
					
				if (nd.getTypefs() == null) {
					continue;
				}
				
				if (nd.getTypefs().equals("sensor") || nd.getParent().equals("ble")) {
					
					tagcount = tagcount + nd.getActivetag();
					
					if (nd.bleType != null && nd.bleType.equalsIgnoreCase("receiver")) {
						activeRecv++;
					}
					if (nd.bleType != null && nd.bleType.equalsIgnoreCase("scanner")) {
						scanner++;
					}
					if (nd.bleType != null && nd.bleType.equalsIgnoreCase("server")) {
						server++;
					}	
					
				}
			}
    	}
    	
    	
		JSONArray  tag_client = new JSONArray();
		tag_client.add(0,"Tags");
		tag_client.add(1, tagcount);
		
		JSONArray  ble_client = new JSONArray();
		ble_client.add(0,"Receiver");
		ble_client.add(1, activeRecv);
		
		JSONArray  scan_client = new JSONArray();
		scan_client.add(0,"Scanner");
		scan_client.add(1, scanner);
		
		JSONArray  srv_client = new JSONArray();
		srv_client.add(0,"Server");
		srv_client.add(1, server);
		
		dev_array.add(dev_array.size(), tag_client);

		dev_array.add(dev_array.size(), ble_client);
		
		dev_array.add(dev_array.size(), scan_client);
		
		dev_array.add(dev_array.size(), srv_client);
		
		devlist.put("devicesConnected", dev_array);		
		
    	
		return devlist;		
    }
    
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/getpeers", method = RequestMethod.GET)
    public JSONObject getpeers(@RequestParam(value="spid", required=false) String spid,
    						   @RequestParam(value= "cid", required=false) String  cid,
    						   @RequestParam(value="uid",  required=false) String uid) throws IOException {

    	JSONObject devlist 	 = new JSONObject();
    	List<Beacon> beacon 	= null ;
    	
		if (uid != null) {
			uid = uid.toUpperCase();
		}
		
		//LOG.info("spid " + spid + "cid " + cid + "uid " + uid);
		
		if (customerUtils.trilateration(cid)) {

			//LOG.info("==========locatum getPeers =============");
			
			BeaconDevice device = beacondeviceService.findOneByUid(uid);
			String bleType = "receiver";

			if (device != null) {
				bleType = device.getType();
			}
			if (bleType.equalsIgnoreCase("server")) {
				beacon = beaconService.getSavedBeaconByServerid(uid);
			} else {
				beacon = beaconService.findByReciverinfo(uid);
			}

			if (beacon != null) {
				devlist = processingTagDetails(beacon);
			}

	
			//LOG.info("devlist>>>>>>>>>>>>>>>>.. " +devlist);
			return devlist;
		}
		return devlist;		
    }
    
	@SuppressWarnings("unchecked")
	private JSONObject processingTagDetails(List<Beacon> beacon) {
		try {
			
			JSONObject taglist 	 	= null;
	    	JSONArray  tagarray 	= new JSONArray();
	    	JSONArray  dev_array 	= new JSONArray();
	    	JSONObject devlist 	 	= new JSONObject();
	    	String state 			= "";
	    	String 	loc_str			= "";
	    	int 	keyFound		= 0;
	    	int location_cnt		= 0;
	    	int activetag			= 0;
	    	String status           = "";
	    	String color 			= "";
			String fafa 			= "";
			
	    	Map<String, Integer> loc_map = new HashMap<String, Integer>();
	    	JSONArray  active_tag_array  = new JSONArray();
	    	JSONArray  locate_tag_array  = new JSONArray();
	    	
				for (Beacon b : beacon) {
					
					status = b.getStatus();
					state  = b.getState();
					
					if ((state.equals("active") || state.equals("idle")) && status.equals("checkedout")) {
						
						taglist = new JSONObject();

						loc_str = b.getLocation().toUpperCase();
						taglist.put("taguid",   	b.getMacaddr());
						taglist.put("location", 	loc_str);
						taglist.put("serverid", 	b.getServerid());
						taglist.put("range", 		b.getRange());
						taglist.put("accuracy", 	b.getAccuracy());
						taglist.put("assignedto", 	b.getAssignedTo().toUpperCase());
						taglist.put("tagtype", 		b.getTag_type().toUpperCase());
						taglist.put("client_type",  "tag");
						taglist.put("distance", 	b.getDistance());

						if (b.getBattery_level() != 0) {
							int battery 		= b.getBattery_level();
							String batteryinfo  = beaconService.batteryStatus(battery);
							taglist.put("fafa",     batteryinfo.split("&")[0]);
							taglist.put("color",    batteryinfo.split("&")[1]);
							taglist.put("battery", battery); //battery percentage
						} else { //battery is  null 
							color = "black";
							fafa  = "fa fa-battery-empty fa-2x";
							taglist.put("fafa",     fafa);
							taglist.put("color",    color);
						}
						tagarray.add (taglist);
						activetag++;

						Set<String> keys = loc_map.keySet();
						keyFound = 0;
						for (String key : keys) {
							if (key.equals(loc_str)) {
								int i = loc_map.get(key);

								//LOG.info("KEY " + key + "value " + i);
								loc_map.put(key, i + 1);
								keyFound = 1;
								//LOG.info("KEY1 " + key + "value1 " + i);
							}
						}

						if (keyFound == 0) {
							loc_map.put(loc_str, 1);
							//LOG.info("KEY2 " + loc_str + "value2" + 1);
						}
					}

				}

			active_tag_array.add (0, "Active Tags");
	 		active_tag_array.add (1, activetag);
	 		
	 		Set<String> keys = loc_map.keySet();
	 		for (String key : keys) {
	 			location_cnt++;
	 		}
	 		
	 		locate_tag_array.add (0, "Active Locations");
	 		locate_tag_array.add (1, location_cnt);
	 		
	 		//LOG.info("Location Map ==>" + loc_map);
	 		

							
			dev_array.add(0, tagarray);
			dev_array.add(1, active_tag_array);
			dev_array.add(2, locate_tag_array);
			int dev_count = 2;
			int i = 1;
	 		for (String key : keys) {
	 			JSONArray  locatum_array = new JSONArray();
	 			locatum_array.add(0, key);
	 			locatum_array.add(1, loc_map.get(key));
	 			dev_array.add (dev_count+i, locatum_array);
	 			i = i + 1;
	 		}
					
			devlist.put("devicesConnected", dev_array);	
			return devlist;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * Given stat value is set or summed up at the src vector at index 1
	 * 
	 * @param src
	 * @param stat
	 */
	void addStat(JSONArray src, String type, int stat) {
		if (src.isEmpty()) {
			src.add(0, type);
			src.add(1, stat);
		} else {
			stat = (Integer) src.get(1) + stat;
			src.set(1, stat);
		}
	}
    
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/getvaps", method = RequestMethod.GET)
    public JSONObject getvaps(@RequestParam(value="sid", 	  required=false) String sid, 
    						  @RequestParam(value="spid", 	  required=false) String spid,
    						  @RequestParam(value="uid", 	  required=false) String uid,
    						  @RequestParam(value="duration", required=false, defaultValue="5m") String duration) throws IOException {
    	
    	JSONArray  dev_array = new JSONArray();
    	JSONObject dev 	     = null;    	
    	JSONObject devlist 	 = new JSONObject();
    	
    	int num_intf  = 3;
    	String vap_2g = "1";
    	String vap_5g = "1";
    	int count_2g  = 0;
    	int count_5g  = 0;
    	List<NetworkDevice> devices = networkDeviceService.findBy(spid, sid, null);
    	
    	if (devices != null && uid == null) {
	    	for (NetworkDevice nd : devices) { 
	    		
	    		BeaconDevice dv = getBeaconDeviceService().findOneByUid(nd.getUid());
	    		if (dv != null) {
		    		//vap_2g = dv.getVap2gcount();
		    		if (vap_2g == null) {
		    			vap_2g = "1";
		    		}	    		
		    		//vap_5g = dv.getVap5gcount();
		    		if (vap_5g == null) {
		    			vap_5g = "1";
		    		}
	    		}
	    		
	    		count_2g += Integer.parseInt(vap_2g);
	    		count_5g += Integer.parseInt(vap_5g);
	    	}
    	}
    	
		JSONArray  dev_array1 = new JSONArray();
		JSONArray  dev_array2 = new JSONArray();
		
		dev_array1.add (0, "2GVAP");
		dev_array1.add (1, count_2g);
		dev_array2.add (0, "5GVAP");
		dev_array2.add (1, count_5g);
		
		dev_array.add  (0, dev_array1);
		dev_array.add  (1, dev_array2);
		
		devlist.put("ActiveVaps", dev_array);	
		
		//LOG.info("ActiveVaps" +devlist.toString());	
    	
    	return devlist;
    	
    }    
    
    
	/*connectedInterfaces": [
	                		{"device":"WLAN","status":"enabled"},
	                		{"device":"XBEE","status":"disabled"},
	                		{"device":"PLC","status":"enabled"},
	                		{"device":"BLE","status":"disabled"}
	                	],    
    */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/getintf", method = RequestMethod.GET)
    public JSONObject getintf(@RequestParam(value="sid", 	  required=false) String sid, 
    						  @RequestParam(value="spid", 	  required=false) String spid,
    						  @RequestParam(value="uid", 	  required=false) String uid,
    						  @RequestParam(value="duration", required=false, defaultValue="5m") String duration) throws IOException {
    	
    	JSONArray  dev_array = new JSONArray();
    	JSONObject dev 	     = null;    	
    	JSONObject devlist 	 = new JSONObject();
    	int num_intf 		 = 4;
    	
    	BeaconDevice device = getBeaconDeviceService().findOneByUid(uid);
    	
    	
    	for (int i = 0;  i < num_intf; i++) {
    		dev = new JSONObject();
    		switch (i) {
    			case 0:
    					dev.put("device", "wlan2g");
    	    			dev.put("status", "disabled");
    	    			dev.put("vapcount", "1");
    				break;
    			case 1:
    	    			dev.put("device", "wlan5g");
    	    			dev.put("status", "disabled");
    	    			dev.put("vapcount", "1");
    				break;
    			case 2:
    	    		dev.put("device", "ble");
	    			dev.put("status", "enabled");
	    			dev.put("vapcount", "1");   				
    				break;
    			case 3:
    	    		dev.put("device", "xbee");
	    			dev.put("status", "disabled");
	    			dev.put("vapcount", "1");   				
    				break;    				
    				
    			default:
    				break;
    		}

    		dev_array.add(dev);
    	}
    	devlist.put("connectedInterfaces", dev_array);
    	
    	//LOG.info("connectedInterfaces" +devlist.toString());
    	
    	return devlist;
    	
    }    
    
    @SuppressWarnings("unchecked")
   	@RequestMapping(value = "/battery", method = RequestMethod.GET)
       public JSONObject getbattery(@RequestParam(value="sid", 	  required=false) String sid, 
       						  @RequestParam(value="spid", 	  required=false) String spid,
       						  @RequestParam(value="uid", 	  required=false) String uid,
       						  @RequestParam(value="duration", required=false, defaultValue="5m") String duration) throws IOException {
       	
       	JSONArray  dev_array = new JSONArray();
       	JSONObject dev 	     = null;    	
       	JSONObject devlist 	 = new JSONObject();
       	int i  = 0;
       	
       	List<Beacon> beacon =  null ;
		if (uid != null) {
			uid = uid.toUpperCase();
		}
		
    	beacon = beaconService.findByReciverId(uid);
    	
		if (beacon != null) {
			for (Beacon b : beacon) {
			if (b.getUpdatedstatus().equalsIgnoreCase("entry")) {
				dev = new JSONObject();
				dev.put("device", b.getMacaddr());
				dev.put("status", i++);
				dev.put("batterylevel", b.getBattery_level());
				dev_array.add(dev);
			}
		  }
		}

       	devlist.put("batteryinfo", dev_array);
       	
       	return devlist;
       	
       }  
	
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/getdevcon", method = RequestMethod.GET)
    public JSONObject getdevcon(@RequestParam(value="sid", 		required=false) String sid, 
    							@RequestParam(value="spid", 	required=false) String spid,
    							@RequestParam(value="swid", 	required=false) String swid,
    							@RequestParam(value="uid", 	  	required=false) String uid,
    							@RequestParam(value="duration", required=false, defaultValue="5m") String duration) throws IOException {
    	
    	JSONArray  dev_array = null;   	
    	JSONObject devlist 	 = new JSONObject();
		dev_array = new JSONArray();
		
		BeaconDevice dv = getBeaconDeviceService().findOneByUid(uid);
		
		if (dv != null) {
			
			JSONArray  dev_array1 = new JSONArray();
			JSONArray  dev_array2 = new JSONArray();
			JSONArray  dev_array3 = new JSONArray();
			JSONArray  dev_array4 = new JSONArray();
	    	
			dev_array1.add (0, "Mac");
			//dev_array1.add (1, dv.getIos());
			//dev_array2.add (0, "Android");
			//dev_array2.add (1, dv.getAndroid());
			//dev_array3.add (0, "Win");
			//dev_array3.add (1, dv.getWindows());
			dev_array4.add (0, "Others");
			dev_array4.add (1, dv.getOthers());		
			
			dev_array.add  (0, dev_array1);
			dev_array.add  (1, dev_array2);
			dev_array.add  (2, dev_array3);
			dev_array.add  (3, dev_array4);			
			
		}
    					
		devlist.put("devicesConnected", dev_array);	
		
    	//LOG.info("Connected Clients" +devlist.toString());
    	
    	return devlist;
    	
    }
    
/*"devicesConnected": [
	["2G", 7],
	["5G", 12]
],*/
@SuppressWarnings("unchecked")
@RequestMapping(value = "/getdevtype", method = RequestMethod.GET)
public JSONObject getdevtype(@RequestParam(value="sid", 		required=false) String sid, 
				 	      	 @RequestParam(value="spid", 		required=false) String spid,
				 	      	 @RequestParam(value="uid", 		required=false) String uid,
				 	      	 @RequestParam(value="duration", 	required=false, defaultValue="5m") String duration) throws IOException {

		JSONArray  dev_array = null;
		JSONObject devlist 	 = new JSONObject();
		dev_array = new JSONArray();
		
		BeaconDevice dv = getBeaconDeviceService().findOneByUid(uid);
		
		if (dv != null) {

			JSONArray  dev_array1 = new JSONArray();
			JSONArray  dev_array2 = new JSONArray();
	    	
			dev_array2.add (0, "2G");
			//dev_array2.add (1, dv.getPeer2gcount());
			dev_array1.add (0, "5G");
			//dev_array1.add (1, dv.getPeer5gcount());
	
			dev_array.add  (0, dev_array2);
			dev_array.add  (1, dev_array1);
		}
		
		devlist.put("typeOfDevices", dev_array);	
		
    	//LOG.info("typeOfDevices" +devlist.toString());			
				
		return devlist;
	}  

/*"devicesConnected": [
["2G", 7],
["5G", 12]
],*/
@SuppressWarnings("unchecked")
@RequestMapping(value = "/getstacount", method = RequestMethod.GET)
public JSONObject getstacount(@RequestParam(value="sid", 	required=false) String sid, 
			 	      	 @RequestParam(value="spid", 		required=false) String spid,
			 	      	 @RequestParam(value="uid", 		required=false) String uid,
			 	      	 @RequestParam(value="duration", 	required=false, defaultValue="5m") String duration) throws IOException {

		JSONArray  dev_array = null;
		JSONObject devlist 	 = new JSONObject();
		dev_array = new JSONArray();
		
		BeaconDevice dv = getBeaconDeviceService().findOneByUid(uid);
		
		if (dv != null) {
	
			JSONArray  dev_array1 = new JSONArray();
			JSONArray  dev_array2 = new JSONArray();
	    	
			dev_array2.add (0, "Active");
			dev_array2.add (1, 5);
			dev_array1.add (0, "Block");
		//	dev_array1.add (1, blk_count);
	
			dev_array.add  (0, dev_array2);
			dev_array.add  (1, dev_array1);
		}
		
		devlist.put("typeOfDevices", dev_array);	
		
		//LOG.info("typeOfDevices" +devlist.toString());			
				
		return devlist;
	} 
    
	
	
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/tcpudpconn", method = RequestMethod.GET)
    public  JSONObject tcpudpconn(@RequestParam(value="uid", required=true)String uid) {
    	
    	int tcp = 0;
    	int udp = 0;
    	
    	List<Map<String, Object>>  conn  = EMPTY_LIST_MAP;
    	
    	String fsql  = "index="+indexname +",sort=timestamp desc,size=1,query=timestamp:>now-" + "1m" + " AND ";

		fsql = fsql + "uid:\"" + uid + "\"";
		fsql = fsql + "AND web_stats:\"Qubercloud Manager\"|value(num_tcp,tcp,NA);value(num_udp,udp,NA);value(timestamp,time,NA)|table";
    	conn =  fsqlRestController.query(fsql);
    	   	
		Iterator<Map<String, Object>> iterator = conn.iterator();
		
		while (iterator.hasNext()) {
			
			TreeMap<String , Object> me = new TreeMap<String, Object> (iterator.next());
			
			String JStr = (String) me.values().toArray()[0];

			String tcp_str = (String)me.values().toArray()[0];
			String udp_str = (String)me.values().toArray()[2]; 
			tcp=Integer.parseInt(tcp_str);
			udp=Integer.parseInt(udp_str);
		}
 
		JSONArray  dev_array = null;
		JSONObject devlist 	 = new JSONObject();
		dev_array = new JSONArray();
		JSONArray  dev_array1 = new JSONArray();
		JSONArray  dev_array2 = new JSONArray();
		JSONArray  dev_array3 = new JSONArray();
		JSONArray  dev_array4 = new JSONArray();		
		JSONArray  dev_array5 = new JSONArray();
		
		dev_array1.add (0, "TCP");
		dev_array1.add (1, tcp);
		dev_array2.add (0, "UDP");
		dev_array2.add (1, udp);		
		
		dev_array.add  (0, dev_array1);
		dev_array.add  (1, dev_array2);
		
		devlist.put("tcpudpconnections", dev_array);	
		
		//LOG.info("tcpudpconnections" +devlist.toString());		
    	
    	return devlist;
    }
    
    
 
    
    
    
	@RequestMapping(value = "/pdf", method = RequestMethod.GET)
	public String pdf(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {
		String pdfFileName  = "./uploads/qubercloud.pdf";//"c:\\temp\\qubexport.pdf";
		String logoFileName = "./uploads/logo-home.png";//"c:\\temp\\logo-home.png";
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			Document document = new Document();
			try {
				FileOutputStream os = new FileOutputStream(pdfFileName);
				PdfWriter.getInstance(document, os);
				document.open();
				Paragraph paragraph = new Paragraph();
				Image image2 = Image.getInstance(logoFileName);
				image2.scaleAbsoluteHeight(25f);//scaleAbsolute(50f, 50f);
				image2.scaleAbsoluteWidth(100f);
				paragraph.add(image2);
				paragraph.setAlignment(Element.ALIGN_LEFT);
				paragraph.add("Qubercloud Venue Report Summary");
				paragraph.setAlignment(Element.ALIGN_CENTER);
				
			    addEmptyLine(paragraph, 1);
			    
			    // Will create: Report generated by: _name, _date
			    paragraph.add(new Paragraph("Report generated by: " + System.getProperty("user.name") + ", " + new Date(), smallBold));
			    addEmptyLine(paragraph, 3);	
			    document.add(paragraph);
			    			    
			    document.newPage();
			    
			    addContent (document);			    
				
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
	

	
	/*@RequestMapping(value = "/export", method = RequestMethod.GET)
	public String export(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {
		String pdfFileName  = "./uploads/qubexport.pdf";
		String logoFileName = "./uploads/logo-home.png";
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			Document document = new Document();
			try {
				FileOutputStream os = new FileOutputStream(pdfFileName);
				PdfWriter.getInstance(document, os);
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
			    
			    addlogContent (document);			    
				
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
				fileInputStream.close();
				responseOutputStream.close();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//return pdfFileName;
		}
		return pdfFileName;
	}*/
	
    private void addContent(Document document) throws DocumentException, IOException, ParseException {
        Anchor anchor = new Anchor("Qubercloud Venue Summary", catFont);
        anchor.setName("Qubercloud Venue Summary");

        // Second parameter is the number of the chapter
        Chapter catPart = new Chapter(new Paragraph(anchor), 2);

        Paragraph subPara = new Paragraph("Venue Details", subFont);
        addEmptyLine(subPara, 2);

        Section subCatPart = catPart.addSection(subPara);

        // add a table
        createTable(subCatPart, document);

        // now add all this to the document
        document.add(catPart);

    }
    
    /*private void addlogContent(Document document) throws DocumentException, IOException, ParseException {
        Anchor anchor = new Anchor("Qubercloud LOG Summary", catFont);
        anchor.setName("Qubercloud LOG Summary");

        // Second parameter is the number of the chapter
        Chapter catPart = new Chapter(new Paragraph(anchor), 1);

        Paragraph subPara = new Paragraph("Qubercloud LOGS", subFont);
        addEmptyLine(subPara, 1);

        Section subCatPart = catPart.addSection(subPara);

        // add a table
        createlogTable(subCatPart, document);

        // now add all this to the document
        document.add(catPart);

    }*/
    
	/*private  void createlogTable(Section subCatPart, Document document) throws IOException, ParseException, DocumentException {
        PdfPTable table 	= new PdfPTable(3);

        PdfPCell c1 = new PdfPCell(new Phrase("UID"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("LEVEL"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);	 
        
        c1 = new PdfPCell(new Phrase("LOG"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);	        
        
        table.setHeaderRows(1);
    	
		JSONObject newJObject = null;
		JSONParser parser = new JSONParser();
		
		List<Map<String, Object>>  logs  = activity (null, null, null, null, null);
    	
    	if (logs != null) {
    		
    		Iterator<Map<String, Object>> iterator = logs.iterator();
    		
    		while (iterator.hasNext()) {
    			
    			TreeMap<String , Object> me = new TreeMap<String, Object> (iterator.next());
    			
    			String JStr = (String) me.values().toArray()[0];
    						
    			try {
    				newJObject = (JSONObject) parser.parse(JStr);
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
				JSONArray slideContent = (JSONArray) newJObject.get("log_list");
				
				if (slideContent != null) {
					Iterator i = slideContent.iterator();
					while (i.hasNext()) { 
						
						String uid		= (String) newJObject.get("uid");  
		    			table.addCell( uid);
						
						JSONObject slide = (JSONObject) i.next();
	    				String debug_level	= (String) slide.get("debug_level");  
	    				table.addCell( debug_level);
	    				
	    				String debug_log	= (String) slide.get("debug_log");  
	    				table.addCell( debug_log);    				
					}
				}
				
    		}
    	} 	

        subCatPart.add(table);
	}    */

	private  void createTable(Section subCatPart, Document document) throws IOException, ParseException, DocumentException {
	        PdfPTable table 	= new PdfPTable(5);
	        PdfPTable ap_table 	= new PdfPTable(5);
	
	        PdfPCell c1 = new PdfPCell(new Phrase("Venue#"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);
	
	        c1 = new PdfPCell(new Phrase("Server#"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);
	
	        c1 = new PdfPCell(new Phrase("Switch#"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);
	        
	        c1 = new PdfPCell(new Phrase("AP#"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);	       
	        
	        c1 = new PdfPCell(new Phrase("Status#"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);	 	        
	        
	        table.setHeaderRows(1);
	        
	        c1 = new PdfPCell(new Phrase("Client"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ap_table.addCell(c1);
	
	        c1 = new PdfPCell(new Phrase("VAP"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ap_table.addCell(c1);
	
	        c1 = new PdfPCell(new Phrase("OEM"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ap_table.addCell(c1);
	        
	        c1 = new PdfPCell(new Phrase("Device"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ap_table.addCell(c1);	       
	        
	        c1 = new PdfPCell(new Phrase("Venue#"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ap_table.addCell(c1);
	        
	        ap_table.setHeaderRows(1);	        
	            
	    	Iterable<NetworkDevice> devices = networkDeviceService.findAll();
	    	List<NetworkDevice> list   = new ArrayList<NetworkDevice>();
	    	
	    	for (NetworkDevice id : devices) {
	    		list.add(id);
	    	}
	    	Collections.sort(list);	    	
	    	
	    	Site site = null;
	    		    	
	    	if (list != null) {

	    		for (NetworkDevice nd : list) {
	    			site = siteService.findById(nd.sid);
	    			
	    			Portion port = portionService.findById(nd.spid);
	    			
	    			if (nd.getTypefs().equals("ap")) {
	    				
		    			table.addCell( port.getUid());
		    			table.addCell(nd.svid);
		    			table.addCell(nd.swid);
		    			table.addCell(nd.getUid());
		    			table.addCell(nd.getStatus());	    				
	    				
	    				String uid = nd.getUid();
	    				JSONObject JStr = getpeers (null,null,uid);
	    				
	    				JSONArray slideContent = (JSONArray) JStr.get("devicesConnected");
						JSONArray devicesConnected =  null;
						if (slideContent.get(0) != null) {
							devicesConnected = (JSONArray)slideContent.get(0);
						}
	    				Iterator i = devicesConnected.iterator();

	    				while (i.hasNext()) {

		    				JSONObject slide = (JSONObject) i.next();
		    				String mac_address	= (String) slide.get("mac_address");  
		    				String ip_address	= (String) slide.get("ap");
		    				String dev_type 	= (String) slide.get("devtype");	
		    				String client_type	= (String) slide.get("client_type");
		    				//String ttl			= (String) slide.get("time");
		    				ap_table.addCell( mac_address);
		    				ap_table.addCell( ip_address);
		    				ap_table.addCell( dev_type);
		    				ap_table.addCell( client_type);
		    				ap_table.addCell( port.getUid());
	    				}
	    				
	    			}
	    		}
			}
	    	
	    	Paragraph Para;
	    	String venue = "Venue ";
	    	if (site != null) {
	    		venue = venue + site.getUid();
	    		Para = new Paragraph(venue);
	    	} else {
	    		Para = new Paragraph("Venue Report");
	    	}
	    	
	        subCatPart.add(Para);
	        addEmptyLine(Para, 2);    	
	
	        subCatPart.add(table);
	        
	        document.newPage();
	        
	        Anchor anchor = new Anchor("Qubercloud Client Summary", catFont);
	        anchor.setName("Qubercloud Connected Device(s) Summary");

	        // Second parameter is the number of the chapter
	        Chapter catPart = new Chapter(new Paragraph(anchor), 1);

	        Paragraph subPara = new Paragraph("Client Details", subFont);
	        addEmptyLine(subPara, 1);

	        Section subCatPart1 = catPart.addSection(subPara);	        
	        
	        Paragraph ap_Para = new Paragraph("Client Report Summary");
	        subCatPart1.add(ap_Para);
	        addEmptyLine(ap_Para, 1);
	        subCatPart1.add(ap_table);
	        document.add(catPart);
	}	
	
	
	@RequestMapping(value = "/imgcapture", method = RequestMethod.GET)
	public String imgcapture(HttpServletRequest request, HttpServletResponse response) throws IOException {	
		
		String imgFileName = "./uploads/screenshot.jpg";
		
		if (SessionUtil.isAuthorized(request.getSession())) {
		    try {

		    	System.setProperty("java.awt.headless", "false"); 
		        Toolkit tool 	  = Toolkit.getDefaultToolkit();
		        Dimension d 	  = tool.getScreenSize();
		        Rectangle rect 	  = new Rectangle(d);
		        Robot robot 	  = new Robot();
		        File f 			  = new File(imgFileName);
		        BufferedImage img = robot.createScreenCapture(rect);
		        
		        ImageIO.write(img,"jpeg",f);
		        
		      } catch(Exception e){
		    	  e.printStackTrace();
		      }
		    
			File jpegFile = new File(imgFileName);
			response.setContentType("application/jpeg");
			response.setHeader("Content-Disposition", "attachment; filename=" + imgFileName);
			response.setContentLength((int) jpegFile.length());
			
			FileInputStream fileInputStream 	= new FileInputStream(jpegFile);
			OutputStream responseOutputStream 	= response.getOutputStream();
			int bytes;
			
			while ((bytes = fileInputStream.read()) != -1) {
				responseOutputStream.write(bytes);
			}	
			
			fileInputStream.close();		    
			responseOutputStream.close();
			return imgFileName;
		}
			
		return imgFileName;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tagactivity", method = RequestMethod.GET)
    public  JSONObject bottleneck(@RequestParam(value="macaddr", required=true) String macaddr,
    							  @RequestParam(value="time", required=false) String days,
    							  HttpServletRequest request, HttpServletResponse response) {
    	
    	String fsql   = null;
    	List<Map<String, Object>>  logs   = EMPTY_LIST_MAP;
    	
		String size = null;
		if (days.equals("12h"))
			size = "100";
		else if (days.equals("1d"))
			size = "300";

		JSONObject json 	= null;
    	JSONObject jsonList = new JSONObject();
    	JSONArray jsonArray = new JSONArray();
    	
    	Map<String,String> portionMap = new HashMap<String,String>();
    	Map<String,String> deviceMap = new HashMap<String,String>();

		try {
			
			fsql = "index=facesix-int-beacon-event, ";
			
			if (size != null) {
				fsql = fsql.concat("size=" + size + ", ");
			}
			
			fsql = fsql.concat("type=trilateration,query=timestamp:>now-" + days+" AND opcode:\"reports\" AND tagid:\""+macaddr+"\""
						+ " ,sort=timestamp DESC|value(timestamp,Date,typecast=date);value(tagid,tagid,null);"
						+ " value(cid,cid,null);value(sid,sid,null);value(location,location,null);value(entry_floor,entry_floor,null);"
						+ " value(entry_loc,entry,null);value(exit_floor,exit_floor,null);value(exit_loc,exit,null);"
						+ " value(elapsed_floor,elapsed_floor,null);value(elapsed_loc,elapsed,null);value(spid,Spid,null);value(assingedto,assingedto,null);|table,sort=Date:desc;");

		   // LOG.info("tagactivity fsql========== >   " +fsql);
		   // LOG.info("days" +days +"size " +size);
			
			logs   =  fsqlRestController.query(fsql);
			
			String elapsed 	= "";
			String exit 	= null;
			String entry 	= null;
			
			Beacon beacon 	=  beaconService.findOneByMacaddr(macaddr);
			
			if(beacon == null){
				return null;
			}
			
			String tagType  = beacon.getTag_type();
			
			String floorname = beacon.getLocation()== null ? "NA" : beacon.getLocation().toUpperCase();
			String location  = beacon.getReciveralias() == null ? "NA" : beacon.getReciveralias().toUpperCase();
			String def_entry = beacon.getState().equals("inactive")? " INACTIVE " : beacon.getEntry_loc();
			String exitTime  = beacon.getExitTime() == null? " INACTIVE ": beacon.getExitTime();
			String def_exit  = beacon.getState().equals("inactive")? exitTime : "Did not Exit";
			
			json = new JSONObject();
			json.put("tagid", 		macaddr);
			json.put("assignedTo", 	beacon.getAssignedTo());
			json.put("tagType", 	tagType);
			json.put("floorname", 	floorname);
			json.put("location", 	location);
			json.put("entry", 		def_entry);
			json.put("exit", 		def_exit);
			json.put("elapsed", 	"0");
			jsonArray.add(json);
			
			for(Map<String,Object> map : logs ) {
				json = new JSONObject();
						
				if (map.containsKey("exit") && map.containsKey("entry")) {
					entry 	= map.get("entry").toString();
					elapsed = map.get("elapsed").toString();
					exit 	= map.get("exit").toString();
					int elap = Integer.parseInt(elapsed);
					if (elap != 0) {
						int hours = elap / 3600;
						int minutes = (elap % 3600) / 60;
						int seconds = (elap % 3600) % 60;
						elapsed = String.format("%02d:%02d:%02d", hours, minutes, seconds);
					}
				}else{
					continue;
				}
				
				String tagid 		= (String) map.get("tagid");
				String assingedto 	= (String) map.get("assingedto");
				String spid 		= (String) map.get("Spid");
				String locationuid  = (String) map.get("location");

				if(locationuid == null || locationuid.isEmpty()){
					continue;
				}
				
				if (deviceMap.containsKey(locationuid)) {
					location = deviceMap.get(locationuid);
				} else {
					BeaconDevice device = beacondeviceService.findOneByUid(locationuid);
					if (device != null) {
						location = device.getAlias();
					}
					deviceMap.put(locationuid, location);
				}

				if(entry == null || entry.isEmpty()){
					entry = "INACTIVE";
				}
				
				if (portionMap.containsKey(spid)) {
					floorname = portionMap.get(spid);
				} else {
					Portion p = portionService.findById(spid);
					if (p != null) {
						floorname = p.getUid().toUpperCase();
					} else {
						floorname = "NA";
					}
					portionMap.put(spid, floorname);
				}

				json.put("tagid", 		tagid);
				json.put("assignedTo", 	assingedto);
				json.put("tagType", 	tagType);
				json.put("floorname", 	floorname);
				json.put("location", 	location);
				json.put("entry", 		entry);
				json.put("exit", 		exit);
				json.put("elapsed", 	elapsed);
				jsonArray.add(json);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		jsonList.put("bottleneck", jsonArray);
//		LOG.info("jsonList "+jsonList);
    	return jsonList;

    }
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/PatientVisitPathBySession", method = RequestMethod.GET)
    public  JSONObject PatientVisitPathBySession(@RequestParam(value="macaddr", required=true) String macaddr,
				    							@RequestParam(value="time", required=false) String days,
				    							HttpServletRequest request, HttpServletResponse response) {
    	
    
		String fsql   = null;
    	List<Map<String, Object>>  logs   = EMPTY_LIST_MAP;
    	String visitId = "";
    	
    	JSONObject json =null;
    	JSONObject jsonList = new JSONObject();
    	JSONArray jsonArray = new JSONArray();
    	
		try {
			
			//LOG.info("PatientVisitPathBySession Tag macaddr <<<<<<<< " +macaddr);
			//LOG.info("PatientVisitPathBySession  days <<<<<<<< " +days);
			
			Beacon beacon = null;
			beacon = beaconService.findOneByMacaddr(macaddr);
			
			if (beacon  != null) {
				visitId = beacon.getId();
			}
			
			
			fsql = " fsql=index=fsi-beacon-event-agarwal,size=100,query=opcode:\"entry-exit\" AND timestamp:>now-"+days+ " "
					+ " AND visitId:\""+visitId+"\" | value(timestamp,Date,typecast=date);value(name,Name,null);"
					+ " value(location,Location,null);value(timeElapsed,Time Elapsed,null);value(opcode,Action,null);"
					+ " value(visitId,Session,null)|table,sort=Date:desc ";

			//LOG.info(" PatientVisitPathBySession fsql " +fsql);
			
			logs   =  fsqlRestController.query(fsql);
	
			for(Map<String,Object> map : logs ) {
				json = new JSONObject();
				if (map.get("Date") != null) {
					json.put("date", df.format(map.get("Date")));
				}
				json.put("location", 	map.get("Location"));
				json.put("timeElapsed", map.get("Time Elapsed"));
				json.put("visit", 		map.get("Action"));
				jsonArray.add(json);
			}
			
			//LOG.info(" PatientVisitPathBySession JSON Array  ======== >  " +jsonArray);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		jsonList.put("bottleneck", jsonArray);
    	return jsonList;

    }
	

	@RequestMapping(value = "/finder_log", method = RequestMethod.GET)
    public  List<Map<String, Object>> finderlog(@RequestParam(value="duration",required=false) String days,
												@RequestParam(value = "cid", required = false) String cid,
												@RequestParam(value = "uid", required = false) String uid) {

		List<Map<String, Object>> logs = EMPTY_LIST_MAP;
		try {

			List<BeaconDevice> devices = null;
			
			if (uid != null && !uid.isEmpty()) {
				devices = getBeaconDeviceService().findByUid(uid);
			} else {
				devices = getBeaconDeviceService().findByCid(cid);
			}
			
			String fsql = "index="+indexname+",sort=timestamp desc,size=1000,query=finder_log:\"user-level\" AND timestamp:>now-"+days+" AND ";
			
			if (devices != null && devices.size() >0) {
				
				String uidbuilder = buildBeaconDeviceArrayCondition(devices, "lmac");
				fsql = fsql + uidbuilder.toLowerCase();
				fsql+= " |value(message,snapshot,NA);value(lmac,lmac,NA);value(timestamp,time,NA);|table";
				
				//LOG.info("fsql " +fsql);
				
				logs = fsqlRestController.query(fsql);
			}
			//LOG.info("logs " +logs);
		} catch(Exception e) {
			LOG.error("while Finder Log getting error ",e);
		}
    	return logs;
    }
	
	 @RequestMapping(value = "/venue/peercount", method = RequestMethod.GET)
	 public  int venuePeercount(@RequestParam(value="sid", required=true)String sid,@RequestParam(value="cid", required=true)String cid) {
	    
			int device_count = 0;
			try {

			if (customerUtils.trilateration(cid)) {
				
				String state 		= "active";
				String status 		= "checkedout";
				List<Beacon> beacon = beaconService.getSavedBeaconByCidSidStateAndStatus(cid,sid, state, status);
				
				if (beacon != null) {
					device_count = beacon.size();
				}
			} else {
					
					List<NetworkDevice> devices = null;
			    	devices = networkDeviceService.findBySid(sid);
			    	
			    	if (devices == null) {
			    		return  0;
			    	}
					
					if (devices.size() > 0) {
			    		for (NetworkDevice nd : devices) {
			    			device_count = device_count + nd.getActivetag();
			    		}
					}
				}
			} catch (Exception e) {
				LOG.info("While getting venue peercount error " ,e);
			}
	    	
	    	return device_count;
	    }
				
		 @RequestMapping(value = "/venue/agg", method = RequestMethod.GET)
		    	public List<Map<String, Object>> venueaggNew(@RequestParam(value="sid", required=true) String sid,
		    												 @RequestParam(value="cid", required=false) String cid,
		    												 HttpServletRequest request, HttpServletResponse response) {
		    
		    	Map<String, Object> map = null;
		    	List<Map<String, Object>> rxtx = null;
		    	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		    	
				List<NetworkDevice> ulist  = networkDeviceService.findBySid(sid);
				List<NetworkDevice> mlist  = Collections.unmodifiableList(ulist);
				List<NetworkDevice> list   = new ArrayList<NetworkDevice>(mlist);
				
				Collections.sort(list);    	
		    	String flrName;
		    	String prev_spid	= "";
		    	
		    	try {
			    	for (NetworkDevice nd : list) { 
			    		
			    		if (prev_spid.equals(nd.spid) == false) {
				    		rxtx = venue_agg(nd.spid,cid);
							Portion port = portionService.findById(nd.spid);
							
							if (port!= null) {
								flrName = port.getUid();
							} else {
								flrName = "Floor";
							}
				    		
				    		if (rxtx.size() > 0 ) {
				    			map = rxtx.get(0);
				    			map.put("Status", flrName);
				    			if (ret.contains(map)) {
				    				// nothing to do
				    			} else {
				    				ret.add(map);
				    			}
				    			
				    		}
				    		
				    		prev_spid = nd.spid;
			    		}
			    	}
		    	} catch (Exception e) {
		    		LOG.info("While getting venue Aggr error " ,e);
		    	}
		    	
		    	return ret;
		    }
		 
		public List<Map<String, Object>> venue_agg(String spid,String cid) {
		
			List<NetworkDevice> devices = networkDeviceService.findBySpid(spid);
			List<Map<String, Object>> venueMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> tmap = new HashMap<String, Object>();
	    	int activetag = 0;
	    	int idletag   = 0;
	    	int inacttag  = 0;
	    	String state  = "";
	    	String status = "checkedout";
	    	boolean entryExit = customerUtils.entryexit(cid);
	    	
		if (customerUtils.trilateration(cid)) {
			List<Beacon> beacon = beaconService.getSavedBeaconBySpidAndStatus(spid, status);
			if (beacon != null) {
				for (Beacon b : beacon) {
					state 				= b.getState();
					String beacon_cid 	= b.getCid();
					
					if (cid.equals(beacon_cid)) {
						
						if (state.equals("active")) {
							activetag++;
						}
						if (state.equals("inactive")) {
							inacttag++;
						}
						if (state.equals("idle")) {
							idletag++;
						}
					}
				}
			}
			//LOG.info(" trilateration Floor vs Traffic Active tags " + activetag);
		} else {
			for (NetworkDevice entry : devices) {
				activetag = activetag + entry.getActivetag();
			}
		}
			
	    	tmap.put("Status", 	   "Active");
	    	tmap.put("activeTags", activetag);
	    	tmap.put("inactTags",  inacttag);
	    	if(!entryExit){
	    		tmap.put("idleTags",   idletag);
	    	}
	    	venueMap.add(tmap);
	    	
			return venueMap;
	
		}
	
	@SuppressWarnings("unchecked")
	public JSONArray processTagType(JSONObject maplist) {

		JSONArray dev_array = new JSONArray();
		JSONObject dev 		= null;

		for (Iterator iterator = maplist.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			dev = new JSONObject();
			dev.put("tagType", key); // Tag type
			dev.put("tagCount", maplist.get(key)); // Tag count
			dev_array.add(dev);
		}

		return dev_array;

	}
	
	 	@SuppressWarnings("unchecked")
		@RequestMapping(value = "/venue/connectedTagType", method = RequestMethod.GET)
	    public JSONArray connectedTagType(@RequestParam(value="sid", required=false) String sid,
	    								   @RequestParam(value="spid",required=false) String spid,
	    								   @RequestParam(value="uid", required=false) String uid,
	    								   @RequestParam(value="cid", required=false) String cid) throws IOException {
	    	
	    	JSONArray  dev_array = new JSONArray();
	    	JSONObject dev 	     = null;    	
	    	JSONObject devlist 	 = new JSONObject();
	    	String status = "";
	    	String state  = "";
	    	
	    	try {
	    		
	    		if (customerUtils.trilateration(cid)) {
		
					//LOG.info(" cid " + cid + "sid " + sid + "spid " + spid);
	    				
	    			List<Beacon> beacon = null;
	    			JSONObject map 	 	= new JSONObject();
	    			status = "checkedout";
					if (sid != null) {
						beacon = beaconService.getSavedBeaconBySidAndStatus(sid, status);
					} else if (spid != null) {
						beacon = beaconService.getSavedBeaconBySpidAndStatus(spid, status);
					} else { // device dash tag type process
						
						BeaconDevice device =  beacondeviceService.findOneByUid(uid);
						spid 				=  device.getSpid();
						String bleType = "";
						if (device != null) {
							bleType = device.getType();
						}
						
						uid = uid.toUpperCase();
						
						if (bleType.equals("server")) { // server based tag type
							beacon = beaconService.getSavedBeaconByServerid(uid);
						} else { // Receiver based tag type  							
							beacon = beaconService.findByReciverinfo(uid); // Receiver based tag type
						}
					}
	    			
					
					// floor and venue based tag type  process
					if (beacon != null) {
						for (Beacon b : beacon) {
							String tag_type 	= b.getTagType();
							status 				= b.getStatus();
							state  				= b.getState();
							String beacon_cid   = b.getCid();
							
							if (status.equalsIgnoreCase("checkedout") && !state.equals("inactive") && cid.equals(beacon_cid)) {
								if (map.containsKey(tag_type)) {
									int count = Integer.parseInt(String.valueOf(map.get(tag_type)));
									map.put(tag_type, count+1);
								}  else {
									map.put(tag_type, 1);
								}
							}
						}
						dev_array = processTagType(map);				
					}
					if (dev_array == null || dev_array.isEmpty()) {
						dev = new JSONObject();
						dev.put("tagType", "Tag"); // Tag type
						dev.put("tagCount", 0); // Tag count
						dev_array.add(dev);
					}
				
					//devlist.put("connectedTagTypes", dev_array);
					//LOG.info(" Connected Tag Type " + devlist);	
					
					return dev_array;
			} else {
			   	
				List<NetworkDevice> networkDevice  = null;
			    networkDevice = networkDeviceService.findBy(spid, sid, null);
			    	
				if (uid != null && sid == null && spid == null) {
					String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
					networkDevice = networkDeviceService.findByUuid(uuid);
				}
			    	
				if (networkDevice != null) {
					for (NetworkDevice nd : networkDevice) {
						if (nd.persontype != null && nd.persontype != "") {
							String jsonMap = nd.persontype;
							net.sf.json.JSONObject map = net.sf.json.JSONObject.fromObject(jsonMap);
							for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
								String key = (String) iterator.next();
								dev = new JSONObject();
								dev.put("tagType", key); // Tag type
								dev.put("tagCount", map.get(key)); // Tag count
								dev_array.add(dev);
							}
						}
					}
				}		
			}
		} catch (Exception e) {
			LOG.info("While getting connected TagType error ", e);
		} 	
	    	
    	if (dev == null || dev.size() <= 0) {
			dev = new JSONObject();
			dev.put("tagType", "Tag"); // Tag type
			dev.put("tagCount", 0); // Tag count
			dev_array.add(dev);
		}
		
    	//devlist.put("connectedTagTypes", dev_array);
		return dev_array;
	} 
		 
	@RequestMapping(value = "/venue/checkoutTag", method = RequestMethod.GET)
	public int venueCheckedout(@RequestParam(value = "sid", required = false) String sid,
							   @RequestParam(value = "cid", required = false) String cid) {

		int device_count = 0;
			
		if (customerUtils.trilateration(cid)) {
			String status 		= "checkedout";
			List<Beacon> beacon = beaconService.getSavedBeaconBySidAndStatus(sid, status);
			if (beacon == null) {
				return device_count;
			}
			device_count = beacon.size();
			return device_count;
		} else {

			List<NetworkDevice> devices = networkDeviceService.findBySid(sid);
			if (devices == null) {
				return 0;
			}

			if (devices.size() > 0) {
				for (NetworkDevice nd : devices) {
					device_count = device_count + nd.getCheckedoutTag();
				}
			}
		}
		return device_count;
	}
	
		
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/venue/gettags", method = RequestMethod.GET)
    public JSONObject venueTags(@RequestParam(value="sid",required=false) String sid) throws IOException {

    	JSONArray  dev_array = new JSONArray();
    	
    	JSONObject devlist 	 = new JSONObject();
    	int tagcount   = 0;
		int activeRecv = 0;
		int scanner	   = 0;
		int server	   = 0;
    	
    	List<NetworkDevice> devices = networkDeviceService.findBySid(sid);
    	
    	if (devices != null) {
    		
	    	for (NetworkDevice nd : devices) { 
					
				if (nd.getTypefs() == null) {
					continue;
				}
				
				if (nd.getTypefs().equals("sensor") || nd.getParent().equals("ble")) {
					
					tagcount = tagcount + nd.getActivetag();
					
					if (nd.bleType != null && nd.bleType.equalsIgnoreCase("receiver")) {
						activeRecv++;
					}
					if (nd.bleType != null && nd.bleType.equalsIgnoreCase("scanner")) {
						scanner++;
					}
					if (nd.bleType != null && nd.bleType.equalsIgnoreCase("server")) {
						server++;
					}	
					
				}
			}
    	}
    	
    	//LOG.info(" activeRecv " +activeRecv + " scanner " +scanner + " server " +server + " Active Tag " +tagcount);
    	
		JSONArray  tag_client = new JSONArray();
		tag_client.add(0,"Tags");
		tag_client.add(1, tagcount);
		
		JSONArray  ble_client = new JSONArray();
		ble_client.add(0,"Receiver");
		ble_client.add(1, activeRecv);
		
		JSONArray  scan_client = new JSONArray();
		scan_client.add(0,"Scanner");
		scan_client.add(1, scanner);
		
		JSONArray  srv_client = new JSONArray();
		srv_client.add(0,"Server");
		srv_client.add(1, server);
		
		dev_array.add(dev_array.size(), tag_client);

		dev_array.add(dev_array.size(), ble_client);
		
		dev_array.add(dev_array.size(), scan_client);
		
		dev_array.add(dev_array.size(), srv_client);
		
		devlist.put("devicesConnected", dev_array);		
		
		return devlist;		
    }

	@RequestMapping(value = "/beacon/alerts", method = RequestMethod.GET)
	public ArrayList<String> alert(
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "sid", required = false) String sid) {
		
		Collection<Beacon> beacons 	= null;
		ArrayList<String> alert 	= new ArrayList<String>();
		
		if (sid == null || sid.isEmpty()) {
			return null;
		}
		
		String status = "checkedout";
		beacons = beaconService.getSavedBeaconByCidSidAndStatus(cid, sid, status);
		
   		 if (customerUtils.trilateration(cid)) {
    		 for(Beacon b : beacons){
    			 String state      = b.getState();
    			 String macAddr    = b.getMacaddr();
    			 String assignedTo = b.getAssignedTo().toUpperCase();
    			 
				 if(state.equalsIgnoreCase("inactive")){
    				 alert.add(assignedTo+" ( Tag id :"+macAddr+" ) Status : "+state);
    			 }
				 
				if (b.getBattery_level() != 0) {
					int battery = b.getBattery_level();
					if (battery <= 40) {
						alert.add(assignedTo + " ( Tag id :" + macAddr + " ) Battery Level <= " + battery + "%");
					}
				}
    		 }
			 
		 } else {
			 // entry-exit
		 }
   		 return alert;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/inactivetags", method = RequestMethod.GET)
	public JSONObject inactiveTags(@RequestParam(value = "cid", required = true) String cid,
			@RequestParam(value = "pdfgenration", required = false) Boolean pdfgenration) {
		
		if (cid == null || cid.isEmpty()) {
			return null;
		}

		JSONObject devlist = new JSONObject();
		JSONArray dev_array = new JSONArray();
		
		try {
			
			JSONObject dev = null;
			String state   = "inactive";
			String status  = "checkedout";
			
			Collection<Beacon> beacons 	= null;
			beacons 				    = beaconService.getSavedBeaconByCidStateAndStatus(cid,state,status);
			
			if (customerUtils.trilateration(cid)) {
				for (Beacon dv : beacons) {
					
					String floorname = dv.getLocation() != null? dv.getLocation():"NA";
					String alias 	 = dv.getReciveralias() != null? dv.getReciveralias():"NA";
					String lastSeen  = dv.getLastReportingTime() != null? dv.getLastReportingTime():"NOT SEEN";
					
					dev = new JSONObject();

					dev.put("macaddr", 		dv.getMacaddr());
					dev.put("minor", 		dv.getMinor());
					dev.put("major",		dv.getMajor());
					dev.put("assignedTo", 	dv.getAssignedTo().toUpperCase());
					dev.put("tagtype", 		dv.getTagType().toUpperCase());
					dev.put("floorname", 	floorname);
					dev.put("state", 	    state.toUpperCase());
					dev.put("alias", 	    alias.toUpperCase());
					dev.put("lastSeen", 	lastSeen);
					dev_array.add(dev);
				}
			} else {
				// entry-exit
				//LOG.info("entry-exit");
			}
		} catch (Exception e) {
			LOG.info("tagalert error " +e);
			e.printStackTrace();
		}
		
		if (dev_array == null || dev_array.size() == 0) {
			if(pdfgenration != null && pdfgenration){
				return null;
			}
			dev_array = defaultDatas(dev_array);
		}
	
		devlist.put("inactivetags", dev_array);
		//LOG.info("inactive tags>>>>>>>>>>>>>>" + devlist.toString());
		return devlist;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray defaultDatas(JSONArray dev_array) {
		JSONObject dev = new JSONObject();
		dev.put("macaddr",  	"-");
		dev.put("minor", 		"0");
		dev.put("major", 		"0");
		dev.put("assignedTo", 	"-");
		dev.put("tagtype", 		"-");
		dev.put("floorname", 	"NA");
		dev.put("alias", 		"NA");
		dev.put("batterylevel", "NA");
		dev.put("uid", 			"-");
		dev.put("type", 		"-");
		dev.put("portionname", 	"NA");
	    dev.put("sitename",    	"NA");
	    dev.put("state",    	"-");
	    dev.put("status",       "Unknown");
		dev_array.add(dev);
		return dev_array;
	}


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/beaconbattery", method = RequestMethod.GET)
	public JSONObject beaconBatteryAlert(
			@RequestParam(value = "cid", required = true) String cid,
			@RequestParam(value = "level", required = true) String level,
			@RequestParam(value = "pdfgenration", required = false) Boolean pdfgenration) {
		
		if (cid == null || cid.isEmpty()) {
			return null;
		}

		JSONObject devlist = new JSONObject();
		JSONArray dev_array = new JSONArray();
		
		try {
			
			int batterylevel = Integer.parseInt(level);
			JSONObject dev 					    = null;
			int batteryLevel  					= 0;
			String status 						= "checkedout";
			
			Collection<Beacon> beacons 	= null;
			beacons = beaconService.getSavedBeaconByCidAndStatus(cid, status);
			
			if (customerUtils.trilateration(cid)) {
				for (Beacon dv : beacons) {
					if (dv.getBattery_level() != 0) {
						
						batteryLevel 		   = dv.getBattery_level();
						String reciverLocation = dv.getReciveralias()==null ? "NA" : dv.getReciveralias().toUpperCase();
						String floorname 	   = dv.getLocation()==null ? "NA" : dv.getLocation().toUpperCase();
						
						if (batteryLevel < batterylevel) {
							dev =	new JSONObject();
							dev.put("macaddr",  	dv.getMacaddr());
							dev.put("minor", 		dv.getMinor());
							dev.put("major", 		dv.getMajor());
							dev.put("assignedTo", 	dv.getAssignedTo().toUpperCase());
							dev.put("tagtype", 		dv.getTagType().toUpperCase());
							dev.put("batterylevel", batteryLevel+"%");
							dev.put("floorname", 	floorname);
							dev.put("alias", 	    reciverLocation);
							dev_array.add(dev);
						}
					}
				}
			} else {
				// entry-exit
				//LOG.info("entry-exit");
			}
		} catch (Exception e) {
			LOG.info("beaconbattery error " +e);
			e.printStackTrace();
		}
		
		if (dev_array == null || dev_array.size() == 0) {
			if(pdfgenration != null && pdfgenration){
				return null;
			}
			dev_array = defaultDatas(dev_array);
		}
		
		devlist.put("beaconbattery", dev_array);
		
		return devlist;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/beacondevicealert", method = RequestMethod.GET)
	public JSONObject beaconDeviceAlert(@RequestParam(value = "cid", required = true) String cid,
			@RequestParam(value = "pdfgenration", required = false) Boolean pdfgenration) {
		
		if (cid == null || cid.isEmpty()) {
			return null;
		}

		JSONObject devlist  = new JSONObject();
		JSONArray dev_array = new JSONArray();
		String lastSeen = "NA";
		
		try {

			JSONObject dev 					= null;
			List<BeaconDevice> 	devices     = null;
			Portion portion 				= null;
			String portionName 				= "NA";

			final String state = "inactive";
			devices = beacondeviceService.findByCidAndState(cid, state);
			
			if (devices !=null) {
				for (BeaconDevice devi : devices) {
					dev = new JSONObject();
					
					String location  = devi.getAlias() == null ? "NA" : devi.getAlias().toUpperCase();
					
					if (devi.getSpid() !=null) {
						portion = portionService.findById(devi.getSpid());
						if (portion != null) {
							portionName = portion.getUid() == null ? "NA" : portion.getUid().toUpperCase();
						}	
					}
					if (devi.getLastseen() !=null) {
						lastSeen = devi.getLastseen();
					}
					String fileStatus   = devi.getDevCrashDumpUploadStatus() == null ? "NA" : devi.getDevCrashDumpUploadStatus();
					String fileName	    = devi.getDevCrashdumpFileName()== null ? "NA" : devi.getDevCrashdumpFileName();
					
					String crashState = "enabled";
					
					if (fileStatus.equals("NA") || fileStatus.isEmpty() || !fileStatus.equals("0")) {
						crashState = "disabled";
					}
					
					dev.put("portionname", 	portionName);
					dev.put("uid", 			devi.getUid());
					dev.put("type",  	    devi.getType().toUpperCase());
					dev.put("status", 		"INACTIVE");
					dev.put("alias", 		location);
					dev.put("timestamp", 	lastSeen);
					dev.put("filestatus", 	fileStatus);
					dev.put("fileName",     fileName);
					dev.put("crashState", 	crashState);
					dev_array.add(dev);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if ((dev_array == null || dev_array.size() == 0)) {
			if(pdfgenration != null && pdfgenration){
				return null;
			}
			dev_array = defaultDatas(dev_array);
		}
		
		devlist.put("beacondevicealert", dev_array);
		
		return devlist;
	}
	
	@RequestMapping(value = "/inactiveTagsCount", method = RequestMethod.GET)
    public  int inactiveTags(@RequestParam(value="cid", required=true)String cid,
    						 @RequestParam(value="sid", required=false)String sid,
    					  	 @RequestParam(value="spid",required=false)String spid) {
    
    	int inactiveTags = 0;
	   	
    	if (customerUtils.trilateration(cid)) {
    		
    		List<Beacon> beacon = null;

    		String status 		= "checkedout";
    		String state        = "inactive";
    		
			if (spid != null) {
				beacon = beaconService.getSavedBeaconByCidSpidStateAndStatus(cid,spid,state,status);
			} else {
				beacon = beaconService.getSavedBeaconByCidSidStateAndStatus(cid,sid,state,status);
			}
        	
			inactiveTags = beacon == null ? 0 : beacon.size();

		} else {
			LOG.info("Entry- exit");
		}
	   	//	LOG.info("inactive Tags  count " +inactiveTags);
    	return inactiveTags;
    }
	
	@RequestMapping(value = "/idleTagsCount", method = RequestMethod.GET)
	public int idleTags(@RequestParam(value = "cid", required = true) String cid,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid) {

		int idleTags = 0;

		if (customerUtils.trilateration(cid)) {

			List<Beacon> beacon = null;

			String status 		= "checkedout";
			String state        = "idle";
    		
			if (spid != null) {
				beacon = beaconService.getSavedBeaconByCidSpidStateAndStatus(cid,spid,state,status);
			} else {
				beacon = beaconService.getSavedBeaconByCidSidStateAndStatus(cid,sid,state,status);
			}

			idleTags = beacon == null ? 0 : beacon.size();

		} else {
			LOG.info("Entry- exit");
		}
		//LOG.info("idleTags Tags  count " + idleTags);
		return idleTags;
	}
	
	@RequestMapping(value = "/alltagstatus", method = RequestMethod.GET)
    public JSONObject heatMap(@RequestParam(value="cid",required = true) String cid,
    						  @RequestParam(value="sid",required =  false) String sid,
    						  @RequestParam(value="spid",required =  false) String spid) 
    						 throws IOException {
		
		
		
		
		int active 			= 0;
		int idle 			= 0;
		int inactive		= 0;
		int total  			= 0;
		
		String state  		 =  "inactive";
		final String status  =  "checkedout";
		
		if (customerUtils.trilateration(cid)) {
			
			Collection<Beacon> beacon = null;
			
			if (sid != null) {
				beacon = beaconService.getSavedBeaconBySidAndStatus(sid, status);
			} else if (spid != null) {
				beacon = beaconService.getSavedBeaconBySpidAndStatus(spid, status);
			} else {
				beacon = beaconService.getSavedBeaconByCidAndStatus(cid, status);
			}
			
			if (beacon != null) {
				for (Beacon b : beacon) {
					
					state 				= b.getState();
					String beacon_cid 	= b.getCid();
					
					if (cid.equals(beacon_cid)) {
						
						if (state.equals("active")) {
							active++;
						}
						if (state.equals("inactive")) {
							inactive++;
						}
						if (state.equals("idle")) {
							idle++;
						}
					}
				}
			}
		} else {
			//LOG.info("other solution");
		}
		
		JSONObject devlist      = new JSONObject();
		JSONArray  data_array 	= new JSONArray();
		
		JSONArray  active_array 	=  new JSONArray();
		JSONArray  idle_array 		=  new JSONArray();
		JSONArray  inactive_array 	=  new JSONArray() ;
		JSONArray  total_array 		=  new JSONArray() ;
		
		
		active_array.add(0, "Active");
		active_array.add(1, active);
		
		idle_array.add(0,"Idle");
		idle_array.add(1,idle);
		
		inactive_array.add(0,"Inactive");
		inactive_array.add(1,inactive);
		
		
		total = active + idle +inactive;
		
		total_array.add(0,"Total");
		total_array.add(1,total);
		
		
		data_array.add(0,active_array);
		data_array.add(1,idle_array);
		data_array.add(2,inactive_array);
		data_array.add(3,total_array);
		
		
		devlist.put("tagstatus", data_array);	
		
		
		return devlist;
	}
	
	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}    
    
	private BeaconDeviceService getBeaconDeviceService() {
		if (beacondeviceService == null) {
			beacondeviceService = Application.context.getBean(BeaconDeviceService.class);
		}
		return beacondeviceService;
	}	
	
	@RequestMapping(value = "/finderScatterChart", method = RequestMethod.GET)
    public JSONObject finderScatterChart(@RequestParam(value="cid",required = false) String cid,
    						  @RequestParam(value="sid",required =  false) String sid,
    						  @RequestParam(value="spid",required =  false) String spid) 
    						 throws IOException {
		Collection<Beacon> beaconlist = null;
		String status 		   = "checkedout";
		
		if(spid != null){
			beaconlist = beaconService.getSavedBeaconBySpidAndStatus(spid, status);
		}else if(sid != null){
			beaconlist = beaconService.getSavedBeaconBySidAndStatus(sid, status);
		}else{
			beaconlist = beaconService.getSavedBeaconByCidAndStatus(cid, status);
		}
		
		JSONObject stateJson =  null;
		JSONObject stateDetails =  null;
		JSONArray jsonArray =  null;
		
		JSONObject tagjson =  null;
		JSONArray taglist =  null;
		JSONObject json =  null;
		
		HashSet<String> floornames =  new HashSet<String>();
		if(beaconlist != null){
			 stateJson = new JSONObject();
			for (Beacon beacon : beaconlist) {
				int count = 0;
				String b_state = beacon.getState();
				String tagid = beacon.getMacaddr();
				String b_sid = beacon.getSid();
				String b_spid = beacon.getSpid();
				String b_floorname = beacon.getLocation();
				floornames.add(b_floorname);
				
				if (b_floorname == null) {
					b_floorname = "unknown";
				} else {
					b_floorname = b_floorname.toLowerCase();
				}

				if (stateJson.containsKey(b_state)) {

					stateDetails = (JSONObject) stateJson.get(b_state);
					jsonArray = (JSONArray) stateDetails.get("floors");
					count = Integer.parseInt(stateDetails.get("count").toString()) + 1;

					int floorAvailable = 0;

					for (int i = 0; i < jsonArray.size(); i++) {
						json = (JSONObject) jsonArray.get(i);
						if (json.containsValue(b_floorname)) {
							taglist = (JSONArray) json.get("taglist");

							tagjson = new JSONObject();

							tagjson.put("tagid", tagid);
							tagjson.put("sid", b_sid);
							tagjson.put("spid", b_spid);

							taglist.add(tagjson);

							json.replace("taglist", taglist);

							jsonArray.remove(i);
							jsonArray.add(json);
							floorAvailable = 1;
							break;
						}
					}
					if (floorAvailable == 0) {

						json = new JSONObject();
						taglist = new JSONArray();
						tagjson = new JSONObject();

						tagjson.put("tagid", tagid);
						tagjson.put("sid", b_sid);
						tagjson.put("spid", b_spid);

						taglist.add(tagjson);

						json.put("floorname", b_floorname);
						json.put("taglist", taglist);

						jsonArray.add(json);
					}
				} else {
					stateDetails = new JSONObject();
					jsonArray = new JSONArray();
					json = new JSONObject();
					tagjson = new JSONObject();
					taglist = new JSONArray();
					count = 1;

					tagjson.put("tagid", tagid);
					tagjson.put("sid", b_sid);
					tagjson.put("spid", b_spid);

					taglist.add(tagjson);

					json.put("floorname", b_floorname);
					json.put("taglist", taglist);
					jsonArray.add(json);
				}
				stateDetails.put("count", count);
				stateDetails.put("floors", jsonArray);
				
				stateJson.put(b_state, stateDetails);
			}
		}
		if (!stateJson.containsKey("active")
			|| !stateJson.containsKey("inactive")
			|| !stateJson.containsKey("idle")) {
				
				json = new JSONObject();
				json.put("count", 0);
				if (!stateJson.containsKey("active")) {
					stateJson.put("active", json);
				}
				if (!stateJson.containsKey("inactive")) {
					stateJson.put("inactive", json);
				}
				if (!stateJson.containsKey("idle")) {
					stateJson.put("idle", json);
				}
			}
		stateJson.put("floornames", floornames);
		return stateJson;
	}
	
	@RequestMapping(value = "inactivityNotify", method = RequestMethod.GET)
	public int inactivitNotify(@RequestParam(value = "cid", required = true) String cid,
			HttpServletRequest req,HttpServletResponse res) {
		
		int count = 0;
		if (SessionUtil.isAuthorized(req.getSession())) {
			Customer customer = customerService.findById(cid);
			if (customer !=null) {
				int inacDeviceCount 	= customer.getDeviceAlertCount();
				int inacTagsCount   	= customer.getTagAlertCount();
				int lowBatteryCount 	= customer.getBatteryAlertCount();
				count = inacDeviceCount + inacTagsCount + lowBatteryCount;
			}
		}
		
		return count;
	}
	
	   @GetMapping("/CrashDumpFileDownload")
	   public ResponseEntity<InputStreamResource> downloadFile(@RequestParam(value = "fileName",required = true) String name) throws IOException {

		  final   String FILE_EXTENSION = "core";
		  String fileName = name+"."+FILE_EXTENSION;
		  
		  LOG.info("FILE NAME " +fileName);
		  
		  //Path path = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_"),(fileName));
		   Path path  = Paths.get(_CCC.properties.getProperty("facesix.fileio.binary.root", "/var/www/html"),(fileName));

		if (!Files.exists(path)) {
			LOG.info("FILE OR DIRECTORY NOT FOUND " + path);
		} else {
			LOG.info(" FILE AVILABLE  " + path);
		}
		  
		File file = new File(path.toString());
		if (file.exists()) {
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
					.contentType(MediaType.APPLICATION_PDF)
					.contentLength(file.length())
					.body(resource);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	public boolean  deviceDupmDetails(Map<String, Object> map) {
		try {
			
			String cid 				=  (String)map.get("cid");
			String uid 				=  (String)map.get("uid");
			int crash_timestamp 	=  (int)map.get("timestamp");
			String  daemon_info		=  (String)map.get("victim");
			String  version			=  (String)map.get("version");
			String  fileName		=  (String)map.get("filename");
			int  upload_status		=  (int)map.get("upload_status");
			String strUploadStatus 	= String.valueOf(upload_status);
			
			final String opcode 	= "device_crash_info";
			final String type   	= "device_crash";
			
			HashMap<String,Object> jsonMap = new HashMap<String,Object>();
			
			jsonMap.put("opcode",		 	opcode);
			jsonMap.put("uid",		     	uid);
			jsonMap.put("cid",  			cid);
			jsonMap.put("filename",  		fileName); 
			jsonMap.put("version",  		version); 
			jsonMap.put("daemon_info",  	daemon_info); 
			jsonMap.put("crash_timestamp",  crash_timestamp);
			jsonMap.put("upload_state",    strUploadStatus); 
			
			elasticService.post(device_history_event, type, jsonMap);  		
			
			jsonMap.clear();

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("while device crash details posting error " +e);
		}
		return true;

	}

	
	@RequestMapping(value = "/finder_Device_crash_info", method = RequestMethod.GET)
	public JSONArray  getDevice_crash_info(@RequestParam(value = "cid", required = true) String cid,
											@RequestParam(value = "time", required = true) String time) {
		try {
			
			final String opcode = "device_crash_info";
			final String type   = "device_crash";
			
			List<Map<String, Object>>  logs = EMPTY_LIST_MAP;
			
			if (time == null || time.isEmpty()) {
				time = "10d";
			}
			String size      = "500";
			
			String fsql = "index=" + device_history_event+",size=" + size + ",type="+type+",query=timestamp:>now-"+time+" AND opcode:"
					+ opcode + " AND ";
					
			List<BeaconDevice> devices = getBeaconDeviceService().findByCid(cid);
			if (devices != null) {
				String uidbuilder = buildBeaconDeviceArrayCondition(devices, "uid");
				fsql = fsql +uidbuilder;
			}
			fsql += " |value(uid,uid, NA);"
					+ " value(crash_timestamp,crash_timestamp,NA);"
					+ " value(cid,cid,NA);value(daemon_info,daemon_info,NA);"
					+ " value(version,version,NA);value(filename,filename,NA);value(upload_state,upload_state,NA);"
					+ " value(timestamp,time,NA);|table";
			
			logs   =  fsqlRestController.query(fsql);
			
			JSONObject object = null;
			JSONArray   array = new JSONArray();
			
			BeaconDevice beaconDevice =  null;
			
			if (logs !=null) {
				Iterator<Map<String, Object>> iterator = logs.iterator();
	    		while (iterator.hasNext()) {
	    			Map<String, Object> map = iterator.next();
	    			
	    			object = new JSONObject();
	    			
	    			String devUid 			= (String)map.get("uid");
	    			int crashTime 			= (int)map.get("crash_timestamp");
	    			String filename 		= (String)map.getOrDefault("filename","NA");
	    			String upload_status 	= (String)map.get("upload_state");
	    			
	    			String alias = "NA";
	    			beaconDevice = getBeaconDeviceService().findOneByUid(devUid);
					if (beaconDevice != null && beaconDevice.getUid().equalsIgnoreCase(devUid)) {
						alias = beaconDevice.getAlias();
					}
					
	    			object.put("uid", 		 devUid);
	    			object.put("crashTime",  crashTime);
	    			object.put("filename",   filename);
	    			object.put("alias",  	 alias);
	    			object.put("status_code",upload_status);
	    			array.add(object);
	    			
	    		}
				
			}
			
			
			return array;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	@RequestMapping(value = "/venue/taginfo", method = RequestMethod.GET)
	public JSONObject venue(@RequestParam(value = "cid", required = true) String cid,
									  @RequestParam(value = "sid", required = true) String sid,
									  HttpServletRequest request, HttpServletResponse response) {
		JSONObject venueDetails = new JSONObject();
		try {
			int activeTags = venuePeercount(sid, cid);
			int inactiveTags = inactiveTags(cid, sid, null);
			int idleTags = idleTags(cid, sid, null);
			int checkedoutTags = venueCheckedout(sid, cid);
			List<Map<String, Object>> floorVsTraffic = venueaggNew(sid, cid, request, response);
			JSONArray connectedTagType = connectedTagType(sid, null, null, cid);

			venueDetails.put("activeTags", activeTags);
			venueDetails.put("inactiveTags", inactiveTags);
			venueDetails.put("idleTags", idleTags);
			venueDetails.put("totalCheckedoutTags", checkedoutTags);
			venueDetails.put("floorVsTraffic", floorVsTraffic);
			venueDetails.put("connectedTagType", connectedTagType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return venueDetails;
	}

	@RequestMapping(value = "/floor/taginfo", method = RequestMethod.GET)
	public JSONObject floor(@RequestParam(value = "cid", required = true) String cid,
									  @RequestParam(value = "sid", required = true) String sid,
									  @RequestParam(value = "spid", required = true) String spid,
									  HttpServletRequest request, HttpServletResponse response) {
		JSONObject floorDetails = new JSONObject();
		try {
			int activeTags = peercount(sid,spid,null,cid);
			int inactiveTags = inactiveTags(cid,sid,spid);
			int idleTags = idleTags(cid,sid,spid);
			JSONArray connectedTagType = connectedTagType(sid,spid,null,cid);
			floorDetails.put("activeTags", activeTags);
			floorDetails.put("inactiveTags", inactiveTags);
			floorDetails.put("idleTags", idleTags);
			floorDetails.put("connectedTagType", connectedTagType);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return floorDetails;
	}
}