package com.semaifour.facesix.data.site;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import net.sf.json.JSONArray;

public interface SiteRepository extends MongoRepository<Site, String> {

	public List<Site> findByUid(String uid);

	public List<Site> findByCustomerId(String customerId);

	@Query("{id:{$in:?0}}")
	public List<Site> findByIds(List<String> id);

}