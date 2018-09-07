package com.semaifour.facesix.fsql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ResultsExtractor;

public class FSqlResultsExtractor implements ResultsExtractor<List<Map<String, Object>>> {

	static Logger LOG = LoggerFactory.getLogger(FSqlResultsExtractor.class.getName());

	protected FSql fsql;
	
	public FSqlResultsExtractor(FSql fsql) {
		this.fsql = fsql;
	}
	
	@Override
	public List<Map<String, Object>> extract(SearchResponse response) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
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
					if (!odoc.isEmpty()) result.add(odoc);
					odoc = new HashMap<String, Object>();
					fsql.flags.hasFlushnote = false;
				}
			} else if (fsql.flags.isAggregateXL) {
				//if any XL function found, don't worry about finialyzing result
				//beacuse XL functions self manage aggregation, flushing and data
				//invoke f.flushXL() at the end to get result.
			} else {
				//no aggregate function found, so just add every doc as row
				if (!odoc.isEmpty()) result.add(odoc);
				odoc = new HashMap<String, Object>();
			}
		}
		//issue a final flush note to all
		if (odoc != null) {
			//if aggregateXL functions found, it will hold final result in it
			if (fsql.flags.isAggregateXL) {
				Collection<Map<String, Object>> list = null;
				for (FSField f : fsql.fields) {
					list = f.getFunc().flushXL();
					if (list != null) {
						result.addAll(list);
						break;
					}
				}
			} else {
				for (FSField f : fsql.fields) {
					f.getFunc().flush(odoc);
				}
				if (!odoc.isEmpty()) result.add(odoc);
			}
		}
		
		
		return result;
	}
}
