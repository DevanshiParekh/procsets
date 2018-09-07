package com.semaifour.facesix.fsql;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.data.elasticsearch.core.ResultsExtractor;

public class ElasticResultsExtractor implements ResultsExtractor<List<Map<String, Object>>> {

	@Override
	public List<Map<String, Object>> extract(SearchResponse response) {
		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		for (SearchHit hit : response.getHits()) {
			result.add(hit.getSource());
		}
		return result;
	}

}
