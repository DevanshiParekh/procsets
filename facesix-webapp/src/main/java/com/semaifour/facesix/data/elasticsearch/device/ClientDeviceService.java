
package com.semaifour.facesix.data.elasticsearch.device;

import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClientDeviceService {
	
	static Logger LOG = LoggerFactory.getLogger(ClientDeviceService.class.getName());

	
	@Autowired
	private ClientDeviceRepository repository;
	
	
	public ClientDeviceService() {
	}
	
	public Page<ClientDevice> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	
	public List<ClientDevice> findByName(String name) {
		return repository.findByName(QueryParser.escape(name));
	}
	
	public List<ClientDevice> findByUid(String uid) {
		return repository.findByUid(uid);
	}
	
	public List<ClientDevice> findByUuid(String uuid) {
		return repository.findByUuid(uuid);
	}
	
	public ClientDevice findOneByUid(String uid) {
		List<ClientDevice> list = findByUid(uid);
		if (list != null & list.size() > 0 ) {
			ClientDevice bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public ClientDevice findOneByUuid(String uuid) {
		List<ClientDevice> list = findByUuid(uuid);
		if (list != null & list.size() > 0 ) {
			ClientDevice bdev = list.get(0);
			if (uuid.equalsIgnoreCase(bdev.getUuid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public List<ClientDevice> findByPid(String pid) {
		return repository.findByPid(pid);
	}	
	
	public ClientDevice findOneByName(String name) {
		List<ClientDevice> list = findByName(name);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public ClientDevice findOneByPid(String pid) {
		List<ClientDevice> list = findByPid(pid);
		if (list != null & list.size() > 0 ) {
			ClientDevice bdev = list.get(0);
			if (pid.equalsIgnoreCase(bdev.getPid())){
				return bdev;
			}	
		}
		return null;
	}	
		
	public ClientDevice findById(String id) {
		return repository.findOne(QueryParser.escape(id));
	}
	
	public boolean exists(String id) {
		return repository.exists(QueryParser.escape(id));
	}
	
	public boolean exists(String uid, String name) {
		if (findOneByUid(uid) != null) return true;
		if (findOneByName(name) != null) return true;
		return false;
	}
	
	public List<ClientDevice> findBySid(String sid){
		return repository.findBySid(sid);
	}
	
	public List<ClientDevice> findBySpid(String spid){
		return repository.findBySpid(spid);
	}
	
	public List<ClientDevice> findByCid(String cid){
		return repository.findByCid(cid);
	}
	
	public void deleteAll() {
		LOG.info("delete all ");
		repository.deleteAll();
	}
	
	public void delete(String id) {
		repository.delete(QueryParser.escape(id));
	}
	
	public void delete(ClientDevice device) {
		repository.delete(device);
	}
	
	public long count() {
		return repository.count();
	}
	
	/**
	 * Save device and notify 
	 * 
	 * @param device
	 * @return
	 */
	public ClientDevice save(ClientDevice device) {
		return save(device, true);
	}
	
	/**
	 * 
	 * Save device and notify=true or false
	 * 
	 * @param device
	 * @param notify
	 * @return
	 */
	public ClientDevice save(ClientDevice device, boolean notify) {
		device = repository.save(device);
		if (device.getPkid()== null) {
			device.setPkid(device.getId());
			device = repository.save(device);
		}
		//LOG.info("saved sucess " +device.getMac());
		return device;
	}

	public Iterable<ClientDevice> findAll() {
		return repository.findAll();
	}
	
	/**
	 * Query for ClientDevice
	 * 
	 * @param query
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	public Iterable<ClientDevice> findByQuery(String uid, String type, String status, String sort, int page, int size) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		
		if (uid != null)
		qb = qb.must(QueryBuilders.termQuery("uid", uid));
		
		if (type != null)
			qb = qb.must(QueryBuilders.termQuery("typefs", type));

		if (status != null)
			qb = qb.must(QueryBuilders.termQuery("status", status));

    	//List<Sort> sorts = FSql.parseSorts(sort);
    	
		return null;
	}
	
	
	public ClientDevice findByMac(String macid) {
		return repository.findByMac(macid);
	}

	public ClientDevice findOneByPeermac(String peer_mac) {
		List<ClientDevice> dev  = findByPeermac(peer_mac);
		if (dev !=null && dev.size() >0) {
			ClientDevice device = dev.get(0);
			if (device.getPeermac().equals(peer_mac)) {
				return device;
			} else {
				return null;
			}
		}
		return null;
	}
	
	public List<ClientDevice> findByPeermac(String peer_mac) {
		return  repository.findByPeermac(peer_mac);
	}

	public ClientDevice findByPeermacAndStatus(String peer_mac, String peerStatus) {
		return  repository.findByPeermacAndStatus(peer_mac,peerStatus);
	}

	public Iterable<ClientDevice> findByUidAndStatus(String uid, String status) {
		return  repository.findByUidAndStatus(uid,status);
	}

	public List<ClientDevice> findByUuidAndStatus(String uuid, String peerStatus) {
		return  repository.findByUuidAndStatus(uuid,peerStatus);
	}
	
	public void save(List<ClientDevice> clientDevice) { 
		 repository.save(clientDevice);
	}

	public List<ClientDevice> findBySidAndStatus(String sid, String status) {
		return  repository.findBySidAndStatus(sid,status);
	}

	public List<ClientDevice> findBySpidAndStatus(String spid, String status) {
		return  repository.findBySpidAndStatus(spid,status);
	}

	public List<ClientDevice> findByCidAndStatus(String cid, String status) {
		return  repository.findByCidAndStatus(cid,status);
	}

	public List<ClientDevice> findByUuidStatusAndLastactiveBefore(String uuid, String peerstatus,long time) {
		return repository.findByUuidStatusAndLastactiveBefore(uuid,peerstatus,time);
	}

	public void delete(List<ClientDevice> clientDevList) {
		repository.delete(clientDevList);
		
	}

	public List<ClientDevice> getSavedPeerList(List<String> uidList, String status) {
		return  repository.getSavedPeerList(uidList,status);
	}
	
}
