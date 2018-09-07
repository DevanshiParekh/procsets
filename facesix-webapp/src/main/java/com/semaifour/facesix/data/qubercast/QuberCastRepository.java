package com.semaifour.facesix.data.qubercast;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuberCastRepository extends MongoRepository<QuberCast, String> {

	public QuberCast findByMulticastPort(String uid);

	public QuberCast findByReffId(String name);
}
