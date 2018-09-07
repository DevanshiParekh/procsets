package com.semaifour.facesix.geo.data;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Service to manage Pois
 * 
 * @author jay
 *
 */
@Service
public class PoiService {
	Logger LOG = LoggerFactory.getLogger(PoiService.class.getName());
	
	@Autowired(required = false)
	private PoiRepository repository;
	
	public PoiService(){	
		LOG.info("service created");
	}

	public Poi getSavedPoiById(String id){
		return repository.findById(id);
	}
	
	public List<Poi> getSavedPoiByStatus(String status){
		return repository.findByStatus(status);	
	}
	
	public List<Poi> findSavedPoiBySpid(String spid){
		return repository.findBySpid(spid);
	}
	
	public List<Poi> findSavedPoiByName(String name){
		return repository.findByName(name);
	}
	
	public Poi save(Poi poi){
		return save(poi, true);
	}
	
	
	public Poi save(Poi poi, boolean notify) {
		poi = repository.save(poi);
		if (poi.getPkid()== null) {
			poi.setPkid(poi.getId());
			poi = repository.save(poi);
		}
		return poi;
	}

	@PostConstruct
	public void init() {		
		LOG.info("service started...");
	}
}
