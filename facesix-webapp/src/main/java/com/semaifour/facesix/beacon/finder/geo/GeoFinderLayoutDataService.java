package com.semaifour.facesix.beacon.finder.geo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeoFinderLayoutDataService {
	Logger LOG = LoggerFactory.getLogger(GeoFinderLayoutDataService.class.getName());
	
	@Autowired(required = false)
	private GeoFinderLayoutDataRepository repository;
	
	public GeoFinderLayoutData getSavedGeoLayoutDataBySpid(String spid){
		return repository.findBySpid(spid);
	}
	
	public GeoFinderLayoutData findByUid(String uid) {
		return repository.findByUid(uid);
	}
	
	public List<GeoFinderLayoutData> findAll(){
		return repository.findAll();
	}
	
	public List<GeoFinderLayoutData> getSavedGeoLayoutDataById(String id){
		return repository.findByType(id);
	}
	
	public List<GeoFinderLayoutData> getSavedGeoLayoutDataByType(String type){
		return repository.findByType(type);
	}
	
	public GeoFinderLayoutData findBySpidAndType(String spid, String type){
		return repository.findBySpidAndType(spid, type);
	}
	
	public GeoFinderLayoutData save(GeoFinderLayoutData fsobject) {
		return save(fsobject,true);
	}

	private GeoFinderLayoutData save(GeoFinderLayoutData fsobject, boolean notify) {
		fsobject = repository.save(fsobject);
		if (fsobject.getPkid() == null) {
			fsobject.setPkid(fsobject.getId());
			fsobject = repository.save(fsobject);
		}
		return fsobject;
	}

	public List<GeoFinderLayoutData> findByCid(String cid) {
		return repository.findByCid(cid);
	}
	
	public List<GeoFinderLayoutData> findBySid(String sid) {
		return repository.findBySid(sid);
	}

	public void delete(GeoFinderLayoutData data) {
		repository.delete(data);
	}

}
