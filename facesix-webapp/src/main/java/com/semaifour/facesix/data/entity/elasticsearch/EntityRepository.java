package com.semaifour.facesix.data.entity.elasticsearch;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EntityRepository extends ElasticsearchRepository<Entity, String> {

	public List<Entity> findByName(String name);

	public List<Entity> findByUid(String uid);

}