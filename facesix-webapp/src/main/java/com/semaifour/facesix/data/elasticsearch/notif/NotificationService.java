
package com.semaifour.facesix.data.elasticsearch.notif;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
	
	static Logger LOG = LoggerFactory.getLogger(NotificationService.class.getName());

	@Autowired
	private NotificationRepository repository;
	
	public NotificationService() {
	}
	
	public Page<Notification> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public Notification findById(String id) {
		return repository.findOne(QueryParser.escape(id));
	}
	
	public boolean exists(String id) {
		return repository.exists(QueryParser.escape(id));
	}
	
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public void delete(String id) {
		repository.delete(QueryParser.escape(id));
	}
	
	public long count() {
		return repository.count();
	}
	
	public Notification save(Notification notif) {
		notif = repository.save(notif);
		return notif;
	}

	public Iterable<Notification> findAll() {
		return repository.findAll();
	}
	
}
