package com.semaifour.facesix.domain;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class JSONMap extends HashMap<Object, Object> {
	
	private static final long serialVersionUID = -3193546077268389041L;

	public JSONMap() {
		super();
	}
	
	public static JSONMap toJSONMap(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, JSONMap.class);
	}
	
	public String toJSONString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
		    mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
	public String json() {
		return toJSONString();
	}
	
	public String getString(String key) {
		return String.valueOf(super.get(key));
	}
	
	public long getLong(String key, long defaultValue) {
		try {
			return Long.parseLong(getString(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
}
