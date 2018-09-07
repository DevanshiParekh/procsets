package com.semaifour.facesix.data.elasticsearch.device;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DeviceRepository extends ElasticsearchRepository<Device, String> {

	public List<Device> findByName(String name);

	public List<Device> findByUid(String uid);

	public List<Device> findByStatus(String status);
	
	public List<Device> findBySpid(String spid);

	public List<Device> findBySid(String sid);

	public List<Device> findByCid(String cid);

	public List<Device> findByUidAndCid(String uid, String cid);

	public Iterable<Device> findByCidAndState(String cid, String state);

	public List<Device> findByCidAndAlias(String cid, String alias);

	public List<Device> findBySidAndState(String sid, String state);

	public List<Device> findBySpidAndState(String spid, String state);
	
}