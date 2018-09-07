package com.semaifour.facesix.data.captive.portal;

import java.util.List;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaptivePortalService {

	Logger LOG = LoggerFactory.getLogger(CaptivePortalService.class.getName());

	@Autowired(required = false)
	private CaptivePortalRepository repository;

	public CaptivePortalService() {
	}

	public List<CaptivePortal> findByUid(String uid) {
		return repository.findByUid(uid);
	}

	public CaptivePortal findById(String id) {
		return repository.findOne(QueryParser.escape(id));
	}

	public CaptivePortal findByPreferedUrl(String preferedUrl) {
		return repository.findByPreferedUrl(preferedUrl);
	}
	public List<CaptivePortal> findByPortalTypeAndCid(String portalType,String cid) {
		return repository.findByPortalTypeAndCid(portalType,cid);
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

	public void delete(CaptivePortal captivePortal) {
		repository.delete(captivePortal);
	}

	public long count() {
		return repository.count();
	}

	
	public CaptivePortal save(CaptivePortal captivePortal) {
		return save(captivePortal, true);
	}

	public CaptivePortal save(CaptivePortal captivePortal, boolean notify) {
		captivePortal = repository.save(captivePortal);
		if (captivePortal.getPkid()== null) {
			captivePortal.setPkid(captivePortal.getId());
			captivePortal = repository.save(captivePortal);
		}
		return captivePortal;
	}

	public Iterable<CaptivePortal> findAll() {
		return repository.findAll();
	}

	public Iterable<CaptivePortal> findOneById(String id) {
		return repository.findOneById(id);
	}

	public List<CaptivePortal> findByCid(String id) {
		return repository.findByCid(id);
	}

	public CaptivePortal findByBackgroundImg(String imgPath) {
		return repository.findByBackgroundImg(imgPath);
	}

	public List<CaptivePortal> getCaptivePortalContainingAssociatedId(String associatedId) {
		return repository.getCaptivePortalContainingAssociatedId(associatedId);
	}
	
	public boolean checkforcaptivePortal(String associatedId) {
		List<CaptivePortal> captivePortal = getCaptivePortalContainingAssociatedId(associatedId);
		if (captivePortal != null && captivePortal.size() > 0) {
			return true;
		}
		return false;
	}
}
