package com.semaifour.facesix.geo.data;

public class PoiRequest{
	
	private String opcode;
	private String uuid;
	private String major;
	private String minor;
	private String sid;
	private String spid;
	
	public PoiRequest(){
		
	}
	
	public PoiRequest(String opcode, String uuid, String major, String minor, String sid, String spid) {
		super();
		this.opcode = opcode;
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
		this.sid = sid;
		this.spid = spid;
	}

	public String getOpcode() {
		return opcode;
	}
	public String getUuid() {
		return uuid;
	}
	public String getMajor() {
		return major;
	}
	public String getMinor() {
		return minor;
	}
	public String getSid() {
		return sid;
	}
	public String getSpid() {
		return spid;
	}
	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public void setMinor(String minor) {
		this.minor = minor;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
}
