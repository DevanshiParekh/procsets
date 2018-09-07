package com.semaifour.facesix.probe.oui;

import java.util.List;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProbeOUIService {
	static Logger LOG = LoggerFactory.getLogger(ProbeOUI.class.getName());

	@Autowired
	private ProbeOUIRepository repository;
	
	public ProbeOUIService() {
	}

	public List<ProbeOUI> findOneById(String id) {
		return repository.findOneById(QueryParser.escape(id));
	}

	public List<ProbeOUI> findByUid(String uid) {
		return repository.findByUid(uid);
	}
	

	public ProbeOUI findOneByUid(String uid) {
		List<ProbeOUI> list = findByUid(uid);
		
		if (list != null & list.size() > 0 ) {
			ProbeOUI bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		
		return null;
	}
	
	public ProbeOUI findById(String id) {
		return repository.findById(id);
	}

	public Iterable<ProbeOUI> findAll() {
		return repository.findAll();
	}

	public ProbeOUI save(ProbeOUI newfso) {
		newfso = repository.save(newfso);
		if (newfso.getPkid()== null) {
			newfso.setPkid(newfso.getId());
			newfso = repository.save(newfso);
		}
		LOG.info("ProbeOUI Saved successfully :" + newfso.getId());
		return newfso;
	}
	
	public void delete(String id) {
		repository.delete(id);
	}

	public void delete() {
		repository.deleteAll();
	}

	
}
