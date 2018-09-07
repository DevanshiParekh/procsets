package com.semaifour.facesix.users.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<Users, String> {

	public List<Users> findByName(String name);

	public List<Users> findByUid(String uid);
	
	public List<Users> findByEmail(String email);

	public Iterable<Users> findAllByCustomerId(String cid);

}