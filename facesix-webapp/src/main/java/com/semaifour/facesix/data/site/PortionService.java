
package com.semaifour.facesix.data.site;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;

@Service 
public class PortionService {
	
	static Logger LOG = LoggerFactory.getLogger(PortionService.class.getName());
	

	@Autowired(required=false)
	private PortionRepository repository;
	
	public PortionService() {
	}
	
	public Page<Portion> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<Portion> findBySiteId(String siteId) {
		return repository.findBySiteId(siteId);
	}
	
	public Portion findById(String id) {
		return repository.findOne(id);
	}
	
	public boolean exists(String id) {
		return repository.exists(id);
	}
	
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public void delete(String id) {
		repository.delete(id);
	}
	
	public void delete(Portion fsobject) {
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
	public Portion save(Portion fsobject) {
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
	public Portion save(Portion fsobject, boolean notify) {
		fsobject = repository.save(fsobject);
		if (fsobject.getPkid()== null) {
			fsobject.setPkid(fsobject.getId());
			fsobject = repository.save(fsobject);
		}
		LOG.info("Saved successfully :" + fsobject.getUid());
		return fsobject;
	}

	public Iterable<Portion> findAll() {
		return repository.findAll();
	}

	public List<Portion> findByCid(String id) {
		return repository.findByCid(id);
	}

	public List<Portion> findByIds(JSONArray placenames) {
		List<String> list = convertJSONArrayToList(placenames);
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
