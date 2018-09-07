package com.semaifour.facesix.gustpass;

import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.semaifour.facesix.gustpass.Gustpass;

@Service
public class GustpassService {

	Logger LOG = LoggerFactory.getLogger(GustpassService.class.getName());

	@Autowired(required = false)
	private GustpassRepository repository;

	public GustpassService() {
	}

	public List<Gustpass> findByPassName(String name) {
		return repository.findByPassName(QueryParser.escape(name));
	}

	public List<Gustpass> findByUid(String uid) {
		return repository.findByUid(QueryParser.escape(uid));
	}

	public Gustpass findById(String id) {
		return repository.findOne(QueryParser.escape(id));
	}

	public boolean exists(String id) {
		return repository.exists(QueryParser.escape(id));
	}

	public void deleteAll() {
		repository.deleteAll();
	}

	public void delete(String id) {
		repository.delete(id);
	}

	public void delete(Gustpass gustpass) {
		repository.delete(gustpass);
	}

	public long count() {
		return repository.count();
	}

	/**
	 * Save Gustpass and notify
	 * 
	 * @param Gustpass
	 * @return
	 */
	public Gustpass save(Gustpass gustpass) {

		return save(gustpass, true);
	}

	/**
	 * 
	 * Save Gustpass and notify=true or false
	 * 
	 * @param Gustpass
	 * @param notify
	 * @return
	 */
	public Gustpass save(Gustpass gustpass, boolean notify) {
		gustpass = repository.save(gustpass);
		if (gustpass.getPkid()== null) {
			gustpass.setPkid(gustpass.getId());
			gustpass = repository.save(gustpass);
		}
		return gustpass;
	}

	public Iterable<Gustpass> findAll() {
		return repository.findAll();
	}

	public Iterable<Gustpass> findOneById(String id) {
		return repository.findOneById(id);
	}

	public Iterable<Gustpass> findByCustomerId(String id) {
		return repository.findByCustomerId(id);
	}
}
