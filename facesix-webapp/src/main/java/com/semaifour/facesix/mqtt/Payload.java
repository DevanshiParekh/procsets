package com.semaifour.facesix.mqtt;

import java.util.Date;
import java.util.HashMap;

import com.semaifour.facesix.domain.JSONMap;

public class Payload extends JSONMap {
	
	private static final long serialVersionUID = -1605119386647296074L;
	
	public static final String OPCODE = "opcode";
	public static final String SOURCE = "source";
	public static final String TARGET = "target";
	public static final String TIMESTAMP = "timestamp";
	public static final String MESSAGE = "message";
	public static final String STATUS = "status";
	
	public Payload(String opcode) {
		super();
		put(OPCODE, opcode);
	}
	public Payload(String opcode, String source, String target, String message) {
		super();
		put(OPCODE, opcode);
		put(SOURCE, source);
		put(TARGET, target);
		put(MESSAGE, message);
		put(TIMESTAMP, new Date());
	}
	
	public String opcode() {
		return super.getString(OPCODE);
	}
	
	public String source() {
		return super.getString(SOURCE);
	}
	
	public String target() {
		return super.getString(TARGET);
	}
	
	public String message() {
		return super.getString(MESSAGE);
	}
	public Date timestamp() {
		return (Date) super.get(TIMESTAMP);
	}
	public void status(String status) {
		put(STATUS, status);
	}
}
