package com.semaifour.facesix.data.entity.mongodb;

import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;

public class MEntity extends FSObject {

	@Id
	private String id;
	
	public MEntity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MEntity [id=" + id + ", super =" + super.toString() + "]";
	}
}
