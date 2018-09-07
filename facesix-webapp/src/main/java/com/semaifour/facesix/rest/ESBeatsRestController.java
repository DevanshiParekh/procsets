package com.semaifour.facesix.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.domain.Link;
import com.semaifour.facesix.domain.Node;
import com.semaifour.facesix.domain.Tree;
import com.semaifour.facesix.fsql.ElasticResultsExtractor;
import com.semaifour.facesix.web.WebController;

/**
 * 
 * Rest Device Controller handles all rest calls
 * 
 * @author mjs
 *
 */
@RestController
@RequestMapping("/rest/esbeats")
public class ESBeatsRestController extends WebController {
	
	static Logger LOG = LoggerFactory.getLogger(ESBeatsRestController.class.getName());
	
	
	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;

    @RequestMapping(value = "packetology", method = RequestMethod.GET)
    public  String packettopology(@RequestParam( value="q", defaultValue="+@timestamp:>now-12h") String query,
    							  @RequestParam(value ="s", defaultValue="500") int size,
    							  @RequestParam(value ="p", defaultValue="0") int page) {
    	ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Link> links = new ArrayList<Link>();
		try {
	    	QueryBuilder builder = QueryBuilders.queryStringQuery(query);
	    	
	    	SearchQuery sq = new NativeSearchQuery(builder);
	    	sq.addIndices("packetbeat*");
	    	sq.setPageable(new PageRequest(page,size));
	    	
	    	ElasticResultsExtractor rse = new ElasticResultsExtractor();
	    	
	    	List<Map<String, Object>> doclist = elasticsearchTemplate.query(sq, rse);
	    	
	    	Node cnode;
	    	Node snode;
	    	Link link;
	    	Map<String, Link> linkmap = new HashMap<String, Link>();
	    	Map<String, Node> nodemap = new HashMap<String, Node>();
			
	    	int nindex = 0;
	    	String clientip;
	    	String serverip;
	    	for(Map<String, Object> doc : doclist) {
	    		clientip = String.valueOf(doc.get("client_ip"));
	    		serverip = String.valueOf(((Map)doc.get("beat")).get("hostname"));
	    		cnode = nodemap.get(clientip);
	    		if (cnode == null) {
	    			cnode = new Node(clientip, "online", String.valueOf(doc.get("type")));
	    			cnode.put("position", nindex++);
	    			nodemap.put(clientip, cnode);
	    			nodes.add(cnode);
	    		}
	    		snode = nodemap.get(serverip);
	    		if (snode == null) {
	    			snode = new Node(serverip, "online", String.valueOf(doc.get("type")));
	    			snode.put("position", nindex++);
	    			nodemap.put(serverip, snode);
	    			nodes.add(snode);
	    		}
	    		link = linkmap.get(clientip + ">" + serverip);
	    		if (link == null) {
	    			link = new Link((Integer)cnode.get("position"),(Integer)snode.get("position"),String.valueOf(doc.get("type")));
	    			linkmap.put(clientip + ">" + serverip, link);
	        		links.add(link);
	    		} else {
	    			link.linkcrease();
	    		}
	    	}
		} catch (Exception e) {
			LOG.warn("Failed to execute query :" + query, e);
		}
    	
		Tree tree = new Tree("online", "success", links.toArray(new Link[links.size()]), nodes.toArray(new Node[nodes.size()]));

    	return tree.toJSONString();
    }
    
 /*   
    @RequestMapping(value = "packettopologycsv", method = RequestMethod.GET)
    public  String packettopologycsv(@RequestParam( value="q", defaultValue="*") String query,
    							  @RequestParam( value = "t", required=false, defaultValue="query") String type) {
    	
    	QueryBuilder builder = QueryBuilders.matchAllQuery();
    	
    	SearchQuery sq = new NativeSearchQuery(builder);
    	sq.addIndices("packetbeat*");
    	sq.setPageable(new PageRequest(0,10000));
    	
    	ElasticResultsExtractor rse = new ElasticResultsExtractor();
    	
    	List<Map<String, Object>> doclist = elasticsearchTemplate.query(sq, rse);
    	
    	Node cnode;
    	Node snode;
    	Link link;
    	Map<String, Link> linkmap = new HashMap<String, Link>();
    	Map<String, Node> nodemap = new HashMap<String, Node>();
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Link> links = new ArrayList<Link>();
    	int nindex = 0;
    	String clientip;
    	String serverip;
    	for(Map<String, Object> doc : doclist) {
    		clientip = String.valueOf(doc.get("client_ip"));
    		serverip = String.valueOf(doc.get("beat.hostname"));
    		cnode = nodemap.get(clientip);
    		if (cnode == null) {
    			cnode = new Node(clientip, "online", String.valueOf(doc.get("type")));
    			cnode.put("position", nindex++);
    			nodemap.put(clientip, cnode);
    			nodes.add(cnode);
    		}
    		snode = nodemap.get(serverip);
    		if (snode == null) {
    			snode = new Node(serverip, "online", String.valueOf(doc.get("type")));
    			snode.put("position", nindex++);
    			nodemap.put(serverip, snode);
    			nodes.add(snode);
    		}
    		link = linkmap.get(clientip + ">" + serverip);
    		if (link == null) {
    			link = new Link((Integer)cnode.get("position"),(Integer)snode.get("position"), String.valueOf(doc.get("type")));
    			linkmap.put(clientip + ">" + serverip, link);
        		links.add(link);
    		} else {
    			link.linkcrease();
    		}
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("source,target,value").append("\n\r");
    	for (Link l: links) {
    		sb.append(l.getSource()).append(",").append(l.getTarget()).append(",").append("1.2").append("\n\r");
    	}
		//Tree tree = new Tree("online", "success", links.toArray(new Link[links.size()]), nodes.toArray(new Node[nodes.size()]));

    	return sb.toString();
    }
 */
    
    
}