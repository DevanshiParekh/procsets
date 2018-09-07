package com.semaifour.facesix.data.elasticsearch.device;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ClientDeviceRepository extends MongoRepository<ClientDevice, String> {
	
	public List<ClientDevice> findByName(String name);

	public List<ClientDevice> findByUid(String uid);

	public List<ClientDevice> findByStatus(String status);
	
	public List<ClientDevice> findByUidAndCid(String uid, String cid);

	public Iterable<ClientDevice> findByCidAndState(String cid, String state);
	
	public List<ClientDevice> findByPid(String uid);
	
	public List<ClientDevice> findBySid(String sid);
	
	public List<ClientDevice> findBySpid(String spid);
	
	public List<ClientDevice> findByCid(String cid);
	
	public ClientDevice findByMac(String peer_mac);
	
	public List<ClientDevice> findByPeermac(String peer_mac);
	
	public ClientDevice findByPeermacAndStatus(String peer_mac, String peerStatus);

	public Iterable<ClientDevice> findByUidAndStatus(String uid, String status);

	public List<ClientDevice> findByUuid(String uuid);

	public List<ClientDevice> findByUuidAndStatus(String uuid, String peerStatus);
	
	public List<ClientDevice> findBySidAndStatus(String sid, String peerStatus);
	
	public List<ClientDevice> findBySpidAndStatus(String spid, String peerStatus);

	public ClientDevice findOneByPeermac(String peer_mac);

	public List<ClientDevice> findByCidAndStatus(String cid, String status);

	@Query("{uuid:?0,status:?1,lastactive : { $lt: ?2 }}")
	public List<ClientDevice> findByUuidStatusAndLastactiveBefore(String uuid, String peerstatus, long time);

	@Query("{uuid:{$in:?0},status:?1}")
	public List<ClientDevice> getSavedPeerList(List<String> uidList, String status);

}
