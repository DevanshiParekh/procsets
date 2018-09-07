package com.semaifour.facesix.data.elasticsearch.device;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NetworkDeviceRepository extends ElasticsearchRepository<NetworkDevice, String> {
	
	public List<NetworkDevice> findByName(String name);
	public List<NetworkDevice> findByUid(String uid);
	public List<NetworkDevice> findBySvid(String svid);
	public List<NetworkDevice> findBySwid(String swid);
	public List<NetworkDevice> findByUuid(String uuid);
	public List<NetworkDevice> findBySid(String sid);
	public List<NetworkDevice> findBySpid(String spid);
	public List<NetworkDevice> findByCid(String cid);
	public List<NetworkDevice> findByCidAndStatus(String cid, String status);
}