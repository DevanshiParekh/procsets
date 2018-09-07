package com.semaifour.facesix.fsql;

import java.util.LinkedList;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.ResultsMapper;

public class ESResultsMapper implements ResultsMapper {

	@Override
	public <T> Page<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T mapResult(GetResponse response, Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> LinkedList<T> mapResults(MultiGetResponse responses,
			Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityMapper getEntityMapper() {
		// TODO Auto-generated method stub
		return null;
	}

}
