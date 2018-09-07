
package com.semaifour.facesix.data.site;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;

@Service 
public class SiteService {
	
	static Logger LOG = LoggerFactory.getLogger(SiteService.class.getName());
	

	@Autowired(required=false)
	private SiteRepository repository;
	
	public SiteService() {
	}
	
	public Page<Site> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<Site> findByUid(String uid) {
		return repository.findByUid(uid);
	}
		
	public Site findOneByUid(String uid) {
		List<Site> list = findByUid(uid);
		if (list != null & list.size() > 0 ) {
			Site bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public Site findById(String id) {
		return repository.findOne(id);
	}
	
	public boolean exists(String id) {
		return repository.exists(id);
	}
	
	public boolean exists(String uid, String name) {
		if (findOneByUid(uid) != null) return true;
		return false;
	}
	
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public void delete(String id) {
		repository.delete(id);
	}
	
	public void delete(Site fsobject) {
		repository.delete(fsobject);
	}
	
	public long count() {
		return repository.count();
	}
	
	/**
	 * Save fsobject and notify 
	 * 
	 * @param fsobject
	 * @return
	 */
	public Site save(Site fsobject) {
		return save(fsobject, true);
	}
	
	/**
	 * 
	 * Save fsobject and notify=true or false
	 * 
	 * @param fsobject
	 * @param notify
	 * @return
	 */
	public Site save(Site fsobject, boolean notify) {
		fsobject = repository.save(fsobject);
		if (fsobject.getPkid()== null) {
			fsobject.setPkid(fsobject.getId());
			fsobject = repository.save(fsobject);
		}
		LOG.info("Saved successfully :" + fsobject.getUid());
		return fsobject;
	}

	public Iterable<Site> findAll() {
		return repository.findAll();
	}
	
	public List<Site> findByCustomerId(String customerId){
		return repository.findByCustomerId(customerId);
	}
	
	
	public List<Site> findAllSite() {
		return repository.findAll();
	}

	public List<Site> findByIds(JSONArray placeIds) {
		List<String> list = convertJSONArrayToList(placeIds);
		return repository.findByIds(list);
	}
	
	private List<String> convertJSONArrayToList(JSONArray placenames) {
		List<String> list = new ArrayList<String>();
		Iterator<String> iter = placenames.iterator();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
	}
		
}
