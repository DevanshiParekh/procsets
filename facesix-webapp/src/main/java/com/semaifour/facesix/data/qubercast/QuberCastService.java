package com.semaifour.facesix.data.qubercast;

import java.util.List;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuberCastService {

	static Logger LOG = LoggerFactory.getLogger(QuberCastService.class.getName());

	@Autowired(required = false)
	private QuberCastRepository repository;

	public QuberCastService() {
	}

	public QuberCast findByMulticastPort(String name) {
		return repository.findByMulticastPort(QueryParser.escape(name));
	}

	public QuberCast save(QuberCast quberCast) {
		 LOG.info("QuberCast saved successfully :" + quberCast);
		return save(quberCast, true);
	}

	public QuberCast save(QuberCast quberCast, boolean notify) {
		quberCast = repository.save(quberCast);
		if (quberCast.getPkid()== null) {
			quberCast.setPkid(quberCast.getId());
			quberCast = repository.save(quberCast);
		}
		return quberCast;
	}

	public QuberCast findByReffId(String name) {
		return repository.findByReffId(QueryParser.escape(name));

	}

}
