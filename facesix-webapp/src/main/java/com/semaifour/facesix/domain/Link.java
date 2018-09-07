package com.semaifour.facesix.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Link implements Serializable {
	
	private static final long serialVersionUID = -2213949415252466970L;
	
	private int source;
	private int target;
	private String type;
	private int value;
	
	public Link(int source, int target, String type) {
		this.source = source;
		this.target = target;
		this.type = type;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void linkcrease() {
		this.setValue(this.getValue() + 1);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
