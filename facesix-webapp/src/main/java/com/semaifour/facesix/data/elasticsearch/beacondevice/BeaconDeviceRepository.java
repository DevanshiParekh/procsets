package com.semaifour.facesix.data.elasticsearch.beacondevice;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BeaconDeviceRepository extends ElasticsearchRepository<BeaconDevice, String> {

	public List<BeaconDevice> findByName(String name);

	public List<BeaconDevice> findByUid(String uid);

	public List<BeaconDevice> findByStatus(String status);
	
	public List<BeaconDevice> findBySpid(String spid);

	public List<BeaconDevice> findBySid(String sid);

	public List<BeaconDevice> findByCid(String cid);

	public BeaconDevice findByUuid(String uid);

	public List<BeaconDevice> findByUidAndCid(String uid, String cid);

	public List<BeaconDevice> findByCidAndType(String cid, String deviceType);

	public List<BeaconDevice> findBySidAndType(String sid, String deviceType);

	public List<BeaconDevice> findBySpidAndType(String spid, String deviceType);

	public List<BeaconDevice> findByCidAndAlias(String cid, String alias);
	
	public List<BeaconDevice> findByCidAndTypeAndAlias(String cid, String deviceType, String alias);

	public BeaconDevice findByUidAndCidAndType(String uid, String cid, String deviceType);

	public List<BeaconDevice> findBySidAndState(String sid, String state);
	
	public List<BeaconDevice> findBySpidAndState(String spid, String state);

	public List<BeaconDevice> findByCidAndState(String cid, String state);
	
}