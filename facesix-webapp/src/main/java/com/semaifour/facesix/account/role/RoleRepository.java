package com.semaifour.facesix.account.role;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface RoleRepository extends MongoRepository<Role, String> {
	
	public List<Role> findByRoleName(String name);

	public List<Role> findByUid(String uid);

	public List<Role> findById(String uid);

}
