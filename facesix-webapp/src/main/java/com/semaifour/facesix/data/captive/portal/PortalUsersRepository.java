package com.semaifour.facesix.data.captive.portal;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PortalUsersRepository extends MongoRepository<PortalUsers, String> {


	public List<PortalUsers> findByUid(String uid);

	public List<PortalUsers> findById(String uid);

	public Iterable<PortalUsers> findOneById(String id);

	public List<PortalUsers> findByCid(String cid);
	
	public PortalUsers findByEmail(String email);
	
	public PortalUsers findByPhone(String phone);

	
}
