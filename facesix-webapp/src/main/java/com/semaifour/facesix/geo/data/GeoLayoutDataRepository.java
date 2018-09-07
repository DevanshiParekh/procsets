package com.semaifour.facesix.geo.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface GeoLayoutDataRepository extends MongoRepository<GeoLayoutData, String> {
	public List<GeoLayoutData> findById(String id);
	public List<GeoLayoutData> findByType(String type);
	public List<GeoLayoutData> findBySpid(String spid);
	
	@Query("{spid:?0, type:?1}")
	public GeoLayoutData findBySpidAndType(String spid, String type);
}
