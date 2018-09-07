package com.semaifour.facesix.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Restponse<T> {
	private boolean success;
	private int    code;
	private Date timestamp;
	private String message;
	private String id;
	
	private T body;
	
	public Restponse(boolean success, int code, String message, String id) {
		super();
		this.success = 	success;
		this.code = code;
		this.id = id;
		this.timestamp = new Date();
		
	}
	
	public Restponse(T body) {
		super();
		this.success = true;
		this.code = 200;
		this.body = body;
		this.timestamp = new Date();
		this.message = "Requested operation completed successfully.";
	}
	
	public Restponse(boolean success, int code) {
		super();
		this.success = 	success;
		this.code = code;
		this.timestamp = new Date();
	}
	
	public Restponse(boolean success, int code, T body) {
		super();
		this.success = 	success;
		this.code = code;
		this.body = body;
		this.timestamp = new Date();
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Restponse [success=" + success + ", code=" + code + ", timestamp=" + timestamp + ", message=" + message
				+ ", body=" + body + "]";
	}
	
}
