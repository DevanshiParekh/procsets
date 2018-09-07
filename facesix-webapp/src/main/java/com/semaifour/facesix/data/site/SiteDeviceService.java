
package com.semaifour.facesix.data.site;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service 
public class SiteDeviceService {
	
	static Logger LOG = LoggerFactory.getLogger(SiteDeviceService.class.getName());
	

	@Autowired(required=false)
	private SiteDeviceService repository;
	
	public SiteDeviceService() {
	}
	
	public Page<SiteDevice> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public SiteDevice findByDeviceUid(String deviceUid) {
		return repository.findByDeviceUid(deviceUid);
	}
	
	public SiteDevice findById(String id) {
		return repository.findById(id);
	}
	
	public boolean exists(String id) {
		return repository.exists(id);
	}
	
	public boolean existsByDevieUid(String deviceUid) {
		if (findByDeviceUid(deviceUid) != null) return true;
		return false;
	}
	
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public void delete(String id) {
		repository.delete(id);
	}
	
	public void delete(SiteDevice fsobject) {
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
	public SiteDevice save(SiteDevice fsobject) {
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
	public SiteDevice save(SiteDevice fsobject, boolean notify) {
		fsobject = repository.save(fsobject);
		if (fsobject.getPkid()== null) {
			fsobject.setPkid(fsobject.getId());
			fsobject = repository.save(fsobject);
		}
		LOG.info("Saved successfully :" + fsobject.getUid());
		return fsobject;
	}

	public Iterable<SiteDevice> findAll() {
		return repository.findAll();
	}
	
}
