package com.semaifour.facesix.data.captive.portal;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CastingRepository extends MongoRepository<Casting, String> {


	public List<Casting> findByUid(String uid);

	public List<Casting> findById(String uid);

	public Iterable<Casting> findOneById(String id);

	public List<Casting> findByCid(String cid);

	public Casting findByType(String type);

	@Query("{cid:?0,fileType:?1}")
	public Iterable<Casting> findByCidAndFileType(String cid, String fileType);

}
