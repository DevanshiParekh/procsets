package com.semaifour.facesix.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Node extends JSONMap {

	private static final long serialVersionUID = 7507096655029088599L;
	
	public static final String SID = "_sid";
	public static final String STATUS = "_status";
	public static final String TYPE = "_type";
	
	public Node(String sid, String status, String type) {
		super();
		put(SID, sid);
		put(STATUS, status);
		put(TYPE, type);
	}
	
	public String getSid() {
		return String.valueOf(get(SID));
	}
	
	public String getStatus() {
		return String.valueOf(get(STATUS));
	}
	
	public String getType() {
		return String.valueOf(get(TYPE));
	}
}
