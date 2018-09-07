
package com.semaifour.facesix.data.elasticsearch.device;

import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

@Service
public class NetworkDeviceService {
	
	static Logger LOG = LoggerFactory.getLogger(NetworkDeviceService.class.getName());

	
	@Autowired
	private NetworkDeviceRepository repository;
	
	public int venue_device_count;
	public int flr_svi_count;
	public int flr_swi_count;
	public int flr_ap_count;
	
	public NetworkDeviceService() {
	}
	
	public Page<NetworkDevice> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public  List<NetworkDevice> findBy(String spid, String sid, String swid) {
		//if (uid != null) return findByUid(uid);
		if (spid != null) return findBySpid(spid);
		if (sid  != null) return findBySid(sid);
		if (swid != null) return findBySwid(swid);
		return null;
	}
	
	public List<NetworkDevice> findByName(String name) {
		return repository.findByName(QueryParser.escape(name));
	}
	
	public List<NetworkDevice> findByUid(String uid) {
		return repository.findByUid(uid);
	}
	
	public List<NetworkDevice> findBySid(String sid) {
		return repository.findBySid(sid);
	}
	
	public List<NetworkDevice> findBySpid(String spid) {
		return repository.findBySpid(spid);
	}
	
	public List<NetworkDevice> findByCid(String cid) {
		return repository.findByCid(cid);
	}
	
	public List<NetworkDevice> findBySvid(String svid) {
		return repository.findBySvid(svid);
	}
	
	public List<NetworkDevice> findBySwid(String swid) {
		return repository.findBySwid(swid);
	}	
	
	public List<NetworkDevice> findByUuid(String uuid) {
		return repository.findByUuid(uuid);
	}	
	
	public NetworkDevice findOneByName(String name) {
		List<NetworkDevice> list = findByName(name);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public NetworkDevice findOneByUid(String uid) {
		List<NetworkDevice> list = findByUid(uid);
		if (list != null & list.size() > 0 ) {
			NetworkDevice bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public NetworkDevice findOneByUuid(String uuid) {
		List<NetworkDevice> list = findByUuid(uuid);
		if (list != null & list.size() > 0 ) {
			NetworkDevice bdev = list.get(0);
			if (uuid.equalsIgnoreCase(bdev.getUuid())){
				return bdev;
			}			
		}
		return null;
	}	
	
	public NetworkDevice findOneBySvid(String svid) {
		List<NetworkDevice> list = findByUuid(svid);
		if (list != null & list.size() > 0 ) {
			NetworkDevice bdev = list.get(0);
			if (svid.equalsIgnoreCase(bdev.getUuid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public NetworkDevice findOneBySwid(String swid) {
		List<NetworkDevice> list = findByUuid(swid);
		if (list != null & list.size() > 0 ) {
			NetworkDevice bdev = list.get(0);
			if (swid.equalsIgnoreCase(bdev.getUuid())){
				return bdev;
			}			
		}
		return null;
	}	
	
	public NetworkDevice findById(String id) {
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
	
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public void delete(String id) {
		repository.delete(QueryParser.escape(id));
	}
	
	public void delete(NetworkDevice device) {
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
	public NetworkDevice save(NetworkDevice device) {
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
	public NetworkDevice save(NetworkDevice device, boolean notify) {
		device = repository.save(device);
		if (device.getPkid()== null) {
			device.setPkid(device.getId());
			device = repository.save(device);
		}
		//LOG.info("NetworkDevice saved successfully :" + device.getId());
		return device;
	}

	public Iterable<NetworkDevice> findAll() {
		return repository.findAll();
	}
	
	/**
	 * Query for NetworkDevice
	 * 
	 * @param query
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	public Iterable<NetworkDevice> findByQuery(String sid, String type, String status, String sort, int page, int size) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		
		if (sid != null)
		qb = qb.must(QueryBuilders.termQuery("sid", sid));
		
		if (type != null)
			qb = qb.must(QueryBuilders.termQuery("typefs", type));

		if (status != null)
			qb = qb.must(QueryBuilders.termQuery("status", status));

    	//List<Sort> sorts = FSql.parseSorts(sort);
    	
		SearchQuery sq = new NativeSearchQueryBuilder()
	    .withQuery(qb)
	    .withPageable(new PageRequest(page,size))
	    .build();
		
		//for(Sort s : sorts) {
		//	sq.addSort(s);
		//}
		
		return repository.search(sq);
	}
	
	
	/**
	 * Query for NetworkDevice
	 * 
	 * @param query
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	public Iterable<NetworkDevice> findByQuery(String query, String sort, int page, int size) {
		
		QueryBuilder qb = QueryBuilders.queryStringQuery(query);
		
    	//List<Sort> sorts = FSql.parseSorts(sort);
    	
		SearchQuery sq = new NativeSearchQueryBuilder()
	    .withQuery(qb)
	    .withPageable(new PageRequest(page,size))
	    .build();
		
		//for(Sort s : sorts) {
		//	sq.addSort(s);
		//}
		
		return repository.search(sq);
	}

	public List<NetworkDevice> findByCidAndStatus(String cid, String status) {
		return repository.findByCidAndStatus(cid,status);
	}
}
