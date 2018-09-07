
package com.semaifour.facesix.data.entity.mongodb;

import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.data.entity.elasticsearch.Entity;

@Service 
public class EntityServiceMongodb {
	
	static Logger LOG = LoggerFactory.getLogger(EntityServiceMongodb.class.getName());
	

	@Autowired(required=false)
	private EntityRepositoryMongodb repository;
	
	public EntityServiceMongodb() {
	}
	
	public Page<MEntity> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<MEntity> findByName(String name) {
		return repository.findByName(QueryParser.escape(name));
	}
	
	public List<MEntity> findByUid(String uid) {
		return repository.findByUid(QueryParser.escape(uid));
	}
	
	public MEntity findOneByName(String name) {
		List<MEntity> list = findByName(name);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public MEntity findOneByUid(String uid) {
		List<MEntity> list = findByUid(uid);
		if (list != null & list.size() > 0 ) {
			MEntity bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public MEntity findById(String id) {
		return repository.findOne(QueryParser.escape(id));
	}
	
	public boolean exists(String id) {
		return repository.exists(QueryParser.escape(id));
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
		repository.delete(QueryParser.escape(id));
	}
	
	public void delete(MEntity device) {
		repository.delete(device);
	}
	
	public long count() {
		return repository.count();
	}
	
	/**
	 * Save device and notify 
	 * 
	 * @param device
	 * @return
	 */
	public MEntity save(MEntity device) {
		return save(device, true);
	}
	
	/**
	 * 
	 * Save device and notify=true or false
	 * 
	 * @param device
	 * @param notify
	 * @return
	 */
	public MEntity save(MEntity device, boolean notify) {
		device = repository.save(device);
		LOG.info("Device saved successfully :" + device.getId());
		return device;
	}

	public Iterable<MEntity> findAll() {
		return repository.findAll();
	}
	
}
