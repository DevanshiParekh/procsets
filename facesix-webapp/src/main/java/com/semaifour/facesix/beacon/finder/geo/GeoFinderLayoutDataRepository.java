package com.semaifour.facesix.beacon.finder.geo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface GeoFinderLayoutDataRepository extends MongoRepository<GeoFinderLayoutData, String> {
	public List<GeoFinderLayoutData> findById(String id);
	public List<GeoFinderLayoutData> findByType(String type);
	public GeoFinderLayoutData 		 findBySpid(String spid);
	public GeoFinderLayoutData findByUid(String uid);
	@Query("{spid:?0, type:?1}")
	public GeoFinderLayoutData findBySpidAndType(String spid, String type);
	public List<GeoFinderLayoutData> findByCid(String cid);
	public List<GeoFinderLayoutData> findBySid(String sid);
}
