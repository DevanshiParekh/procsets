package com.semaifour.facesix.probe.oui;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProbeOUIRepository  extends MongoRepository<ProbeOUI, String>  {

	public List<ProbeOUI> findOneById(String id);
	
	public ProbeOUI findById(String id);
	
	public List<ProbeOUI> findByUid(String uid);
	
}
