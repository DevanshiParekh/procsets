package com.semaifour.facesix.geo.data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeoLayoutDataService {
	Logger LOG = LoggerFactory.getLogger(GeoLayoutDataService.class.getName());
	
	@Autowired(required = false)
	private GeoLayoutDataRepository repository;
	
	public List<GeoLayoutData> getSavedGeoLayoutDataBySpid(String spid){
		return repository.findBySpid(spid);
	}
	
	public List<GeoLayoutData> getSavedGeoLayoutDataById(String id){
		return repository.findByType(id);
	}
	
	public List<GeoLayoutData> getSavedGeoLayoutDataByType(String type){
		return repository.findByType(type);
	}
	
	public GeoLayoutData findBySpidAndType(String spid, String type){
		return repository.findBySpidAndType(spid, type);
	}
	
	public GeoLayoutData save(GeoLayoutData fsobject) {
		return save(fsobject,true);
	}

	private GeoLayoutData save(GeoLayoutData fsobject, boolean notify) {
		fsobject = repository.save(fsobject);
		if (fsobject.getPkid()== null) {
			fsobject.setPkid(fsobject.getId());
			fsobject = repository.save(fsobject);
		}
		return fsobject;
	}
}
