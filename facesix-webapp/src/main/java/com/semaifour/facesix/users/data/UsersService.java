
package com.semaifour.facesix.users.data;

import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service 
public class UsersService {
	
	static Logger LOG = LoggerFactory.getLogger(UsersService.class.getName());
	

	@Autowired(required=false)
	private UserRepository repository;
	
	public UsersService() {
	}
	
	public Page<Users> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<Users> findByEmail(String name) {
		return repository.findByEmail(QueryParser.escape(name));
	}	
	
	public List<Users> findByName(String name) {
		return repository.findByName(QueryParser.escape(name));
	}
	
	public List<Users> findByUid(String uid) {
		return repository.findByUid(QueryParser.escape(uid));
	}
	
	public Users findOneByEmail(String name) {
		List<Users> list = findByEmail(name);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public Users findOneByName(String name) {
		List<Users> list = findByName(name);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public Users findOneByUid(String uid) {
		List<Users> list = findByUid(uid);
		if (list != null & list.size() > 0 ) {
			Users bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public Users findById(String id) {
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
		LOG.info("deleteById " +id);
		repository.delete(id);
	}
	
	public void delete(Users users) {
		repository.delete(users);
	}
	
	public long count() {
		return repository.count();
	}
	
	/**
	 * Save users and notify 
	 * 
	 * @param users
	 * @return
	 */
	public Users save(Users users) {
		
		return save(users, true);
	}
	
	/**
	 * 
	 * Save users and notify=true or false
	 * 
	 * @param users
	 * @param notify
	 * @return
	 */
	public Users save(Users users, boolean notify) {
		users = repository.save(users);
		if (users.getPkid()== null) {
			users.setPkid(users.getId());
			users = repository.save(users);
		}
		return users;
	}

	public Iterable<Users> findAll() {
		return repository.findAll();
	}

	public Iterable<Users> findAllByCustomerId(String cid) {
		return repository.findAllByCustomerId(cid);
	}
	
}
