package com.semaifour.facesix.fsql;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ResultsExtractor;

public class FSqlResultsMapExtractor implements ResultsExtractor<Map<String, Map<String, Object>>> {

	static Logger LOG = LoggerFactory.getLogger(FSqlResultsMapExtractor.class.getName());

	protected FSql fsql;
	
	protected String keyfield;
	
	/**
	 * 
	 * @param fsql FSQL to be executed
	 * @param keyfield name of the field by which a result-item is identified.
	 */
	public FSqlResultsMapExtractor(FSql fsql, String keyfield) {
		this.fsql = fsql;
		this.keyfield = keyfield;
	}
	
	@Override
	public Map<String, Map<String, Object>> extract(SearchResponse response) {
		Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
		Map<String, Object> idoc = null;
		Map<String, Object> odoc = new HashMap<String, Object>();
		for (SearchHit hit : response.getHits()) {
			idoc = hit.getSource();
			for (FSField f : fsql.fields) {
				odoc = f.getFunc().exe(idoc.get(f.getIcolumn()), odoc,idoc);
				if (!fsql.flags.isAggregate) f.getFunc().flush(odoc);
			}
			
			//if at least one aggregate function found, we need to aggregate
			if (fsql.flags.isAggregate) {
				//if there was any flushnote raised, then flush 
				//current odoc as row, and setup a new row
				if (fsql.flags.hasFlushnote) {
					if (!odoc.isEmpty()) result.put(String.valueOf(odoc.get(keyfield)), odoc);
					odoc = new HashMap<String, Object>();
					fsql.flags.hasFlushnote = false;
				}
			} else {
				//no aggregate function found, so just add every doc as row
				if (!odoc.isEmpty()) result.put(String.valueOf(odoc.get(keyfield)), odoc);
				odoc = new HashMap<String, Object>();
			}
		}
		//issue a final flush note to all
		if (odoc != null) {
			for (FSField f : fsql.fields) {
				f.getFunc().flush(odoc);
			}
			if (!odoc.isEmpty()) result.put(String.valueOf(odoc.get(keyfield)), odoc);
		}
		return result;
	}
}
