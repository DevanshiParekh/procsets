package com.semaifour.facesix.data.entity.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.semaifour.facesix.domain.FSObject;

@Document(indexName = "fsi-entity-#{systemProperties['fs.app'] ?: 'default'}", type = "entity")
public class Entity extends FSObject {
	
	@Id
	private String id;
	private String cid;
	
	public Entity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	@Override
	public String toString() {
		return "Entity [id=" + id + ", cid=" + cid + "]";
	}

	
}
