package com.semaifour.facesix.data.elasticsearch;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.fsql.ElasticResultsExtractor;
import com.semaifour.facesix.util.DateUtils;

/**
 * 
 * Generic Service to Elasticsearch to post/query/delete/etc
 * 
 * @author mjs
 *
 */
@Service
public class ElasticService {
	
	private static Logger LOG = LoggerFactory.getLogger(ElasticService.class.getName());
	
	
	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;
		
	/**
	 * 
	 * Post a doc to an index
	 * 
	 * @param index
	 * @param type
	 * @param doc
	 * @return
	 */
	public IndexResponse post(String index, String type, Map<String, Object> doc) {
		Object timestamp = doc.get("timestamp");
		if (timestamp == null) { 
			timestamp = new Date();
		} else if (!(timestamp instanceof Date)) {
			try {
				timestamp = DateUtils.parse2Timestamp(String.valueOf(timestamp));
			} catch (Exception e) {
				LOG.error("UTC[yyyy-MM-dd'T'HH:mm:ssZ] is expected attr [_timestamp] :" + timestamp, e);
				return new IndexResponse(index, type, "Parse failed for _timestamp [yyyy-MM-dd'T'HH:mm:ssZ]=" + timestamp ,-1,false);
			}
		}
		doc.put("timestamp", timestamp);
		
    	IndexResponse ir = elasticsearchTemplate
									.getClient()
									.prepareIndex(index, type)
									.setSource(doc)
									.execute()
									.actionGet();
    	return ir;
	}
	
	/**
	 * Query data from an index
	 * 
	 * @param query
	 * @param index
	 * @param type
	 * @param page
	 * @param size
	 * @return
	 */
	public List<Map<String, Object>> query(String query, String index, String type, int page, int size) {
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
	    	
	    	sq.setPageable(new PageRequest(page,size));
	    	
	    	ElasticResultsExtractor rse = new ElasticResultsExtractor();
	    	
	    	result = elasticsearchTemplate.query(sq, rse);
	    	
		} catch (Exception e) {
			LOG.warn("Failed to execute query :" + query, e);
		}
    	return result;
	}
	
	/**
	 * Delete a doc with given Id
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> delete(String index, String type, String id) {
		List<Map<String, Object>> list = query("+id:" +  id, index, type, 0,1);
		String idx = elasticsearchTemplate.delete(index, type, id);
		if (list.size() > 0) {
			list.get(0).put("_THIS_DOC_DELETED_WITH_ID_", idx);
		}
		return list;
	}
	
}
