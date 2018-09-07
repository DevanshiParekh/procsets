package com.semaifour.facesix.data.captive.portal;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CaptivePortalRepository extends MongoRepository<CaptivePortal, String> {


	public List<CaptivePortal> findByUid(String uid);

	public List<CaptivePortal> findById(String uid);

	public Iterable<CaptivePortal> findOneById(String id);

	public List<CaptivePortal> findByCid(String cid);

	public CaptivePortal findByPreferedUrl(String preferedUrl);

	public CaptivePortal findByBackgroundImg(String imgPath);

	public List<CaptivePortal> findByPortalTypeAndCid(String portalType,String cid);
	
	@Query("{associationIds:{$eq:?0}}")
	public List<CaptivePortal> getCaptivePortalContainingAssociatedId(String associatedId);

}
