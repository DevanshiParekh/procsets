package com.semaifour.facesix.data.captive.portal;

import java.util.List;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CastingService {

	Logger LOG = LoggerFactory.getLogger(CastingService.class.getName());

	@Autowired(required = false)
	private CastingRepository repository;

	public CastingService() {
	}

	public List<Casting> findByUid(String uid) {
		return repository.findByUid(uid);
	}

	public Casting findById(String id) {
		return repository.findOne(QueryParser.escape(id));
	}

	public boolean exists(String id) {
		return repository.exists(QueryParser.escape(id));
	}

	public void deleteAll() {
		repository.deleteAll();
	}

	public void delete(String id) {
		repository.delete(id);
	}

	public void delete(Casting captivePortal) {
		repository.delete(captivePortal);
	}

	public long count() {
		return repository.count();
	}

	
	public Casting save(Casting captivePortal) {
		return save(captivePortal, true);
	}

	public Casting save(Casting captivePortal, boolean notify) {
		captivePortal = repository.save(captivePortal);
		if (captivePortal.getPkid()== null) {
			captivePortal.setPkid(captivePortal.getId());
			captivePortal = repository.save(captivePortal);
		}
		return captivePortal;
	}


	public Casting findByType(String type) {
		return repository.findByType(type);
	}
	
	public Iterable<Casting> findAll() {
		return repository.findAll();
	}

	public Iterable<Casting> findOneById(String id) {
		return repository.findOneById(id);
	}

	public List<Casting> findByCid(String id) {
		return repository.findByCid(id);
	}

	public Iterable<Casting> findByCidAndFileType(String cid, String fileType) {
		return repository.findByCidAndFileType(cid,fileType);
	}
	
}
