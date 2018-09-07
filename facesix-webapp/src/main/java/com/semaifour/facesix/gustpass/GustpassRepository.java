package com.semaifour.facesix.gustpass;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.semaifour.facesix.gustpass.Gustpass;

public interface GustpassRepository extends MongoRepository<Gustpass, String> {

	public List<Gustpass> findByPassName(String name);

	public List<Gustpass> findByUid(String uid);

	public List<Gustpass> findById(String uid);

	public Iterable<Gustpass> findOneById(String id);

	public Iterable<Gustpass> findByCustomerId(String id);

}
