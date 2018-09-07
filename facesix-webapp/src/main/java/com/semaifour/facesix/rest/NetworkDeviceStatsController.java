package com.semaifour.facesix.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.JSONMap;
import com.semaifour.facesix.web.WebController;

@RestController
@RequestMapping("/rest/site/networkdevice")
public class NetworkDeviceStatsController extends WebController {
	static Logger LOG = LoggerFactory.getLogger(NetworkDeviceStatsController.class.getName());

	@Autowired
	NetworkDeviceService 	networkDeviceService;
	
	@Autowired
	ClientDeviceService 	clientDeviceService;
	
	@Autowired
	SiteService siteService;
	
	@Autowired
	PortionService portionService;	
	   
	@Autowired
	FSqlRestController 		fsqlRestController;
	
	@Autowired
	EntityRestController 	entity;
	
	@Autowired
	DeviceService devService;
	
	private String indexname = "facesix*";
	
	@PostConstruct
	public void init() {
		indexname 		= _CCC.properties.getProperty("elasticsearch.indexnamepattern", "facesix*");
	}
	
	
	/**
	 * 
	 * 
	 * @param sid
	 * @param spid
	 * @param uid
	 * @param swid
	 * @param query - base query is 
	 * @param to
	 * @param radio
	 * @param keepalive
	 * @return
	 */
    @SuppressWarnings("unused")
	@RequestMapping(value = "/summarytree", method = RequestMethod.GET)
    public   JSONMap summarytree(@RequestParam(value="sid", required=false) String sid, 
    								 	      @RequestParam(value="spid", required=false) String spid,
    								 	      @RequestParam(value="uid", required=false) String uid,
    								 	      @RequestParam(value="swid", required=false) String swid,
    								 	      @RequestParam(value="query", required=false, defaultValue="timestamp:>now-15m") String query,
    								 	      @RequestParam(value="to", required=false) String to,
    								 	      @RequestParam(value="radio", required=false, defaultValue="2G5G") String radio,
    								 	      @RequestParam(value="keepalive", required=false, defaultValue="60000")int keepalive) {
    	String esql = "_exists_:client_count AND " + query + " AND ";
    	int count   = 0;
    	if (swid != null) {
    		swid = swid.replaceAll("[^a-zA-Z0-9]", "");
    	}    	
    	
    	List<NetworkDevice> devices = networkDeviceService.findBy(spid, sid, swid);
    	
    	//Init Response Tree
    	JSONMap result = null;
    	Map<String, JSONMap> device2portionMap = new HashMap<String, JSONMap>();
    	Map<String, JSONMap> sid2portionMap = new HashMap<String, JSONMap>();
    	Map<String, JSONMap> device2devicestats = new HashMap<String, JSONMap>();
    	Map<String, String> device2device = new HashMap<String, String>();

    	for(NetworkDevice device : devices) {
    		if (result == null) {
    			result = new JSONMap();
    			result.put("title", "Site Usage Summary");    			
    			Site s =  siteService.findById(device.getSid());
    			//result.put("siteObj", s);
    			result.put("sid", device.getSid());
    		}
    		JSONMap pmap = sid2portionMap.get(device.getSpid());
    		if (pmap == null) { 
    			pmap = new JSONMap();
    			pmap.put("spid", device.getSpid());
    			Portion p = portionService.findById(device.getSpid());
    			//pmap.put("portionObj", p);
            	sid2portionMap.put(device.getSpid(), pmap);
    		}
    		device2portionMap.put(device.getUid(), pmap);
    		if (device.getTypefs().equals("ap")) {
    			device2device.put(device.getUid(), device.getUid());
    		}
    	}
    	result.put("portions", sid2portionMap.values());
    	
    	//Build Query to fetch stats
    	if (devices != null && uid == null) {
        	esql = esql + NetworkDeviceRestController.buildArrayCondition(devices, "uid");
    	} else if (uid != null) {
    		esql = esql + "uid:\"" + uid + "\"";
    	}
    	
    	if (radio.equals("2G")) {
        	esql = esql + "AND radio_type:\"2.4Ghz\"";
    	} else if (radio.equals("5G")) {
        	esql = esql + "AND radio_type:\"5Ghz\"";
    	} /*else {
        	esql = esql + "AND radio_type:(\"5Ghz\" OR \"2.4Ghz\")";
    	}  */ 	
    	    	
    	LOG.info("summarytree() ESQL " + esql);
    	//Fetch stats and aggregate missing stats
    	if (esql != null) {
    		QueryBuilder builder = QueryBuilders.queryStringQuery(esql);
	    	SearchQuery sq = new NativeSearchQueryBuilder()
	    					.withQuery(builder)
	    					.withSort(new FieldSortBuilder("timestamp").order(SortOrder.DESC))
	    					.withPageable(new PageRequest(0,100))
	    					.build();
	    	sq.addIndices(indexname);
	    	//List<Map<String, Object>> page = _CCC.elasticsearchTemplate.query(sq, new ElasticResultsExtractor()); 	


	    	Client esClient = _CCC.elasticsearchTemplate.getClient();
	    	SearchResponse scrollResp = esClient.prepareSearch("summarybuilder")
	    	        .addSort("timestamp", SortOrder.DESC)
	    	        .setScroll(new TimeValue(keepalive))
	    	        .setQuery(builder)
	    	        .setIndices(indexname)
	    	        .setSize(1000).get(); //max of 100 hits will be returned for each scroll
	    	//Scroll until no hits are returned
	    	boolean done = false;
    		LOG.info("grand total # docs found ..wait for aggregation :"+ scrollResp.getHits().getTotalHits());
    		int deviceCount = device2device.size();
	    	//do {
    	    	LOG.info("# docs in batch :" + scrollResp.getHits().getHits().length);
	    	    for (SearchHit hit : scrollResp.getHits().getHits()) {
	    	    	Map<String, Object> map = hit.getSource();
	    	    	String duid = String.valueOf(map.get("uid"));
					JSONMap portionMap = device2portionMap.get(duid);
					try {
						List<JSONMap> list = (List<JSONMap>) portionMap.get("networkdevices");
						if (list == null) {
							list = new ArrayList<JSONMap>();
							portionMap.put("networkdevices", list);
						}
						JSONMap nd = device2devicestats.get(duid);
						if (nd == null) {
							nd = new JSONMap();
							nd.put("uid", duid);
							nd.put("log", JSONMap.toJSONMap(String.valueOf(map.get("message"))));
							device2devicestats.put(duid, nd);
							list.add(nd);
							LOG.info("done stats for device id :" + duid + " | count " + deviceCount--);
							long cv = toLong(map.get("client_count")); //current value
							long sv = portionMap.getLong("clientCount", cv); //stored value
							
							sv = (cv + sv ) / 2; //MEAN
							portionMap.put("clientCount", sv);
							sv = portionMap.getLong("clientCountMax", cv);
							sv = sv >= cv ? sv : cv;
							portionMap.put("clientCountMax", sv);
							sv = portionMap.getLong("clientCountMin", cv);
							sv = sv <= cv ? sv : cv;
							portionMap.put("clientCountMin", sv);
							
							cv = toLong(map.get("_vap_tx_bytes"));
							sv = portionMap.getLong("vapTx", 0);
							portionMap.put("vapTx", cv + sv);
							
							cv = toLong(map.get("_vap_rx_bytes"));
							sv = portionMap.getLong("vapRx", 0);
							portionMap.put("vapRx", cv + sv);
						}
						//remove device from device2device mark it is done
					/*	if (device2device.remove(duid) == null) {
							if(device2device.size() < 1) {
								done = true;
								LOG.info("existing loop and finish reading - read all devices");
								break;
							}
						}
						*/
					} catch (Exception e) {
						LOG.info("Exception in building summary stats", e);
					} finally {
						esClient.close();
					}
	    	    }
	    	    //scrollResp = esClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(keepalive)).execute().actionGet();
	    	//}while(scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
	    	
	    	
	    	/*for(Map<String, Object> map : page) {
				String duid = String.valueOf(map.get("uid"));
				JSONMap portionMap = device2portionMap.get(duid);
				try {
					List<JSONMap> list = (List<JSONMap>) portionMap.get("networkdevices");
					if (list == null) {
						list = new ArrayList<JSONMap>();
						portionMap.put("networkdevices", list);
					}
					JSONMap nd = device2devicestats.get(duid);
					if (nd == null) {
						nd = new JSONMap();
						nd.put("uid", duid);
						//nd.put("log", JSONMap.toJSONMap(String.valueOf(map.get("message"))));
						//device2devicestats.put(duid, nd);
						list.add(nd);
					}
					long cv = toLong(map.get("client_count")); //current value
					long sv = portionMap.getLong("clientCount", cv); //stored value
					sv = (cv + sv ) / 2; //MEAN
					portionMap.put("clientCount", sv);
					sv = portionMap.getLong("clientCountMax", cv);
					sv = sv >= cv ? sv : cv;
					portionMap.put("clientCountMax", sv);
					sv = portionMap.getLong("clientCountMin", cv);
					sv = sv <= cv ? sv : cv;
					portionMap.put("clientCountMin", sv);
					
					cv = toLong(map.get("_vap_tx_bytes"));
					sv = portionMap.getLong("vapTx", 0);
					portionMap.put("vapTx", cv + sv);
					
					cv = toLong(map.get("_vap_rx_bytes"));
					sv = portionMap.getLong("vapRx", 0);
					portionMap.put("vapRx", cv + sv);
					
				} catch (Exception e) {
					LOG.info("Exception in formatting network device stats", e);
				}
			}*/
	    	long clientCount = 0;
	    	long vapTx = 0;
	    	long vapRx = 0;
	    	for(JSONMap p : sid2portionMap.values()) {
	    		LOG.info(p.getString("spid") + " " + p.getLong("clientCount", 0) + " " + p.getLong("vapTx", 0) + " " + + p.getLong("vapTx", 0));
	    		clientCount += p.getLong("clientCount", 0);
	    		vapTx += p.getLong("vapTx", 0);
	    		vapRx += p.getLong("vapRx", 0);
	    	}
	    	
	    	result.put("clientCount", clientCount);
	    	result.put("vapRx", vapRx);
	    	result.put("vapTx", vapTx);
	    	
	    	return result;
    	} else {
    		return new JSONMap();
    	}
    }

	private long toLong(Object object) {
		try {
			return object != null ? Long.parseLong(String.valueOf(object).split(" ")[0]) : 0;
		} catch (Exception e) {
			return 0;
		}
	}

}
