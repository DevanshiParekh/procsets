
package com.semaifour.facesix.data.elasticsearch.datasource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.data.entity.elasticsearch.Entity;

@Service
public class DatasourceService {
	
	static Logger LOG = LoggerFactory.getLogger(DatasourceService.class.getName());

	@Autowired
	private DatasourceRepository repository;
	
	public DatasourceService() {
	}
	
	public Page<Datasource> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<Datasource> findByName(String name) {
		return repository.findByName(name);
	}
	
	public List<Datasource> findByUid(String uid) {
		return repository.findByUid(uid);
	}
	
	public Datasource findOneByName(String name) {
		List<Datasource> list = repository.findByName(name);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public Datasource findOneByUid(String uid) {
		List<Datasource> list = repository.findByUid(uid);
		if (list != null & list.size() > 0 ) {
			Datasource bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public Datasource findById(String id) {
		return repository.findOne(id);
	}
	
	public boolean exists(String id) {
		return repository.exists(id);
	}
	
	public boolean exists(String uid, String name) {
		if (findOneByUid(uid) != null) return true;
		if (findOneByName(name) != null) return true;
		return false;
	}
	
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public void delete(String id) {
		repository.delete(id);
	}
	
	public long count() {
		return repository.count();
	}
	
	public Datasource save(Datasource jdbc) {
		jdbc = repository.save(jdbc);
		LOG.info("Datasource saved successfully :" + jdbc.getId());
		return jdbc;
	}
	
}
