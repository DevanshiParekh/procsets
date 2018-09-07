package com.semaifour.facesix.data.site;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PortionRepository extends MongoRepository<Portion, String> {
	public List<Portion> findBySiteId(String siteId);
	public List<Portion> findByCid(String id);
	
	@Query("{id:{$in:?0}}")
	public List<Portion> findByIds(List<String> ids);
}