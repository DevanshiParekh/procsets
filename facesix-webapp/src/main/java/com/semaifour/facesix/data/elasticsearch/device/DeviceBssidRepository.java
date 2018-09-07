package com.semaifour.facesix.data.elasticsearch.device;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceBssidRepository extends MongoRepository<DeviceBssid, String> {
	
	public DeviceBssid findById(String id);

	public List<DeviceBssid> findByUid(String uid);

	public List<DeviceBssid> findByName(String name);

	public List<DeviceBssid> findByStatus(String status);

	public List<DeviceBssid> findByCid(String cid);
	
	public DeviceBssid findByBssid(String bssid);
}

