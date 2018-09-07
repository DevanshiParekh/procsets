package com.semaifour.facesix.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RestResponse<T extends Object> {
	
	private String status;
	private int    code;
	private Date timestamp;
	private T body;
	
	public RestResponse() {	
	}
	
	public RestResponse(String status, int code, T body) {
		super();
		this.status = status;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "RestResponse [status=" + status + ", code=" + code
				+ ", timestamp=" + timestamp + ", body=" + body + "]";
	}
	
	
}
