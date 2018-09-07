package com.semaifour.facesix.beacon.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BeaconAlertDataRepository extends MongoRepository<BeaconAlertData, String>{
	
	public List<BeaconAlertData> findByCid(String cid);
	public BeaconAlertData findById(String id);

}
