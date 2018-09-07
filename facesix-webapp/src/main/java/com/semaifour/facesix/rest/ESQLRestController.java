package com.semaifour.facesix.rest;

import static org.elasticsearch.action.search.SearchType.COUNT;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.fsql.ElasticResultsExtractor;
import com.semaifour.facesix.web.WebController;

/**
 * 
 * Rest Controller to query ES
 * 
 * @author mjs
 *
 */
@RestController
@RequestMapping("/rest/esql")
public class ESQLRestController extends WebController {
	
	static Logger LOG = LoggerFactory.getLogger(ESQLRestController.class.getName());
	
	
	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;
	
	/**
	 * Saves the given JSON document to given catalog (index) and schema (type)
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param item
	 * @param catalog
	 * @param schema
	 * @return
	 */
	@RequestMapping( value="/{catalog}/{schema}", method=RequestMethod.POST)
	public @ResponseBody IndexResponse postItem(HttpServletRequest request,
	    											HttpServletResponse response,
	    											@RequestBody Map<String, Object> item,
	    											@PathVariable("catalog") String catalog,
	    											@PathVariable("schema") String schema) {
	    	item.put("timestamp", new Date(System.currentTimeMillis()));
	    	item.put("clientip", request.getRemoteAddr());
	    	item.put("clientid", request.getRemoteUser());
	    	item.put("clientss", request.getRemoteHost());
	    	
	    	IndexResponse ir = elasticsearchTemplate
	    								.getClient()
	    								.prepareIndex(catalog, schema)
	    								.setRefresh(true)
	    								.setSource(item)
	    								.execute()
	    								.actionGet();
	    	return ir;
	}
	
	
	@RequestMapping( value="/{catalog}/{schema}", method=RequestMethod.GET)
	public @ResponseBody 
	List<Map<String, Object>> listItems(HttpServletRequest request,
	    										 HttpServletResponse response,
	    										 @RequestBody Map<String, Object> item,
	    										 @PathVariable("catalog") String catalog,
	    										 @PathVariable("schema") String schema) {
	    	item.put("timestamp", new Date(System.currentTimeMillis()));
			return search("*", catalog, schema, 1, 0);
	}
	
	/**
	 * Returns a doc for the given 'id' from given catalog (index) and schema (type)
	 * @param request
	 * @param response
	 * @param catalog
	 * @param schema
	 * @param id
	 * @return
	 */
	@RequestMapping( value="/{catalog}/{schema}/{id}", method=RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> getItem(HttpServletRequest request,
															HttpServletResponse response,
															@PathVariable("catalog") String catalog,
															@PathVariable("schema") String schema,
															@PathVariable("id") String id) {
		return search("id:" +  id, catalog, schema, 1, 0);
	}
	
	/**
	 * 	 Deletes a doc for the given 'id' from given catalog (index) and schema (type)
	 * 
	 * @param request
	 * @param response
	 * @param item
	 * @param catalog
	 * @param schema
	 * @param id
	 * @return
	 */
	@RequestMapping( value="/{catalog}/{schema}/{id}", method=RequestMethod.DELETE)
	public @ResponseBody List<Map<String, Object>> deleteItem(HttpServletRequest request,
															HttpServletResponse response,
															@RequestBody Map<String, Object> item,
															@PathVariable("catalog") String catalog,
															@PathVariable("schema") String schema,
															@PathVariable("id") String id) {
		List<Map<String, Object>> list = search("+id:" +  id, catalog, schema, 1, 0);
		String idx = elasticsearchTemplate.delete(catalog, schema, id);
		if (list.size() > 0) {
			list.get(0).put("_THIS_DOC_DELETED_WITH_ID_", idx);
		}
		return list;
	}
	    

	/**
	 * 
	 * Returns list of documents for the given 'query', 'index', 'type', 'size', 'page'
	 * 
	 * @param query 		(default: +@timestamp:>now-12h
	 * @param index 		(default: across all indices)
	 * @param type  		(default: across all types )
	 * @param size  		(default: 500 )
	 * @param page  		(default: 0 - first page)
	 * @return
	 */
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public  @ResponseBody List<Map<String, Object>> search(@RequestParam( value="q", defaultValue="+@timestamp:>now-12h") String query,
    							  @RequestParam( value="i", required=false) String index,
    							  @RequestParam( value="t", required=false) String type,
    							  @RequestParam(value ="s", defaultValue="500") int size,
    							  @RequestParam(value ="p", defaultValue="0") int page) {
    	List<Map<String, Object>> result = null;
    	try {
	    	QueryBuilder builder = QueryBuilders.queryStringQuery(query);
	    	
	    	SearchQuery sq = new NativeSearchQuery(builder);
	    	if (index != null) {
	    		sq.addIndices(index);
	    	}
	    	if (type != null) {
	    		sq.addTypes(type);
	    	}
	    	if (page >= 0 && size >= 0) {
	    		sq.setPageable(new PageRequest(page,size));
	    	}
	    	
	    	ElasticResultsExtractor rse = new ElasticResultsExtractor();
	    	
	    	result = elasticsearchTemplate.query(sq, rse);
	    	
		} catch (Exception e) {
			LOG.warn("Failed to execute query :" + query, e);
		}
    	return result;
    }
    
    /**
     * 
     * @param query
     * @param index
     * @param type
     * @param size
     * @param page
     * @return
     */
    @RequestMapping(value = "{catalog}/{schema}/search", method = RequestMethod.GET)
    public  @ResponseBody List<Map<String, Object>> search1(@RequestParam(value="q", defaultValue="+@timestamp:>now-12h") String query,
    							  @PathVariable( value="catalog", required=false) String index,
    							  @PathVariable( value="schema", required=false) String type,
    							  @RequestParam(value ="s", defaultValue="500") int size,
    							  @RequestParam(value ="p", defaultValue="0") int page) {
    	return search(query, index, type, size, page);
    }
    
    /**
     * 
     * @param query
     * @param index
     * @param type
     * @param size
     * @param page
     * @return
     */
    @RequestMapping(value = "{catalog}/{schema}/search", method = RequestMethod.POST)
    public  @ResponseBody List<Map<String, Object>> search2(@RequestBody String query) {
    	return search(query, null, null, -1, -1);
    }

    
    /**
     * Returns TERM aggregated values by give 'aggName' for the given parameters
     * 
     * @param aggName  				name for the aggregation bucket
     * @param aggField				name of the field to apply TERM aggregation
 	 * @param query 				(default: +@timestamp:>now-12h
	 * @param index 				(default: across all indices)
	 * @param type  				(default: across all types )
	 * @param size  				(default: 500 )
	 * @param page  				(default: 0 - first page)
     * @return
     */
    @RequestMapping(value = "aggterm", method = RequestMethod.GET)
    public  @ResponseBody Aggregations aggTerm(@RequestParam( value="an", required=true) String aggName,
			  					  @RequestParam( value="af", required=true) String aggField,
			  					  @RequestParam( value="q", defaultValue="+@timestamp:>now-12h") String query,
    							  @RequestParam( value="i", required=false) String index,
    							  @RequestParam( value="t", required=false) String type,
    							  @RequestParam( value ="s", defaultValue="500") int size,
    							  @RequestParam( value ="p", defaultValue="0") int page) {
    	Aggregations aggregations = null;
    	try {
    	
	    	QueryBuilder builder = QueryBuilders.queryStringQuery(query);
	    	
	    	SearchQuery sq = new NativeSearchQueryBuilder()
	            .withQuery(builder)
	            .withSearchType(COUNT)
	            .addAggregation(terms(aggName).field(aggField))
	            .build();	
	    	  
	    	if (index != null) {
	    		sq.addIndices(index);
	    	}
	    	if (type != null) {
	    		sq.addTypes(type);
	    	}
	    	
	    	sq.setPageable(new PageRequest(page,size));
	    	
	    	aggregations = elasticsearchTemplate.query(sq, new ResultsExtractor<Aggregations>() {
				@Override
				public Aggregations extract(SearchResponse response) {
					return response.getAggregations();
				}
			});
	    	
		} catch (Exception e) {
			LOG.warn("Failed to execute query :" + query, e);
		}
    	return aggregations;
    }
    
}