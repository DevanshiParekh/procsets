package com.semaifour.facesix.data.elasticsearch.datasource;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DatasourceRepository extends ElasticsearchRepository<Datasource, String> {

	public List<Datasource> findByName(String name);

	public List<Datasource> findByUid(String uid);

}