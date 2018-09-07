package com.semaifour.facesix.data.entity.mongodb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntityRepositoryMongodb extends MongoRepository<MEntity, String> {

	public List<MEntity> findByName(String name);

	public List<MEntity> findByUid(String uid);

}