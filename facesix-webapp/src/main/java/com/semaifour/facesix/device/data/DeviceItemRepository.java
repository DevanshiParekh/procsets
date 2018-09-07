package com.semaifour.facesix.device.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceItemRepository extends MongoRepository<DeviceItem, String> {

	public List<DeviceItem> findByName(String name);

	public List<DeviceItem> findByUid(String uid);
	
	public List<DeviceItem> findByStatus(String status);
	
	public List<DeviceItem> findByTypefs(String typefs);

}