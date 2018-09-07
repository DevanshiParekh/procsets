
package com.semaifour.facesix.data.entity.elasticsearch;

import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;

@Service
public class EntityService {
	
	static Logger LOG = LoggerFactory.getLogger(EntityService.class.getName());

	@Autowired
	private EntityRepository repository;
	
	public EntityService() {
	}
	
	public Page<Entity> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<Entity> findByName(String name) {
		return repository.findByName(QueryParser.escape(name));
	}
	
	public List<Entity> findByUid(String uid) {
		return repository.findByUid(QueryParser.escape(uid));
	}
	
	public Entity findOneByName(String name) {
		List<Entity> list = findByName(name);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public Entity findOneByUid(String uid) {
		List<Entity> list = findByUid(uid);
		if (list != null & list.size() > 0 ) {
			Entity bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public Entity findById(String id) {
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
	
	public void delete(Entity entity) {
		repository.delete(entity);
	}
	
	public long count() {
		return repository.count();
	}
	
	/**
	 * Save entity and notify 
	 * 
	 * @param entity
	 * @return
	 */
	public Entity save(Entity entity) {
		return save(entity, true);
	}
	
	/**
	 * 
	 * Save entity and notify=true or false
	 * 
	 * @param entity
	 * @param notify
	 * @return
	 */
	public Entity save(Entity entity, boolean notify) {
		entity = repository.save(entity);
		if (entity.getPkid()== null) {
			entity.setPkid(entity.getId());
			entity = repository.save(entity);
		}
		LOG.info("Device saved successfully :" + entity.getId());
		return entity;
	}

	public Iterable<Entity> findAll() {
		return repository.findAll();
	}
	
	
	public Iterable<Entity> findByQuery(String query, String sort, int page, int size) {
		
		QueryBuilder qb = QueryBuilders.queryStringQuery(query);

		SearchQuery sq = new NativeSearchQueryBuilder()
	    .withQuery(qb)
	    .withPageable(new PageRequest(page,size))
	    .build();
		
		//for(Sort s : sorts) {
		//	sq.addSort(s);
		//}
		
		return repository.search(sq);
	}
}
