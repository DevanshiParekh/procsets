package com.semaifour.facesix.binary;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface BinarySettingRepository  extends MongoRepository<BinarySetting, String>  {

	public List<BinarySetting> findOneById(String id);
	
	public List<BinarySetting> findBySid(String sid);

	public List<BinarySetting> findBySpid(String spid);

	public BinarySetting findByCid(String cid);

	public BinarySetting findById(String id);
	
	public List<BinarySetting> findByUid(String uid);
	
}
