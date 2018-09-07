package com.semaifour.facesix.geo.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Pois repository
 * 
 * @author jay
 *
 */
public interface PoiRepository extends MongoRepository<Poi, String> {
	public Poi findById(String id);
	public List<Poi> findByName(String name);
	public List<Poi> findByStatus(String status);	
	public List<Poi> findBySpid(String spid);
}
