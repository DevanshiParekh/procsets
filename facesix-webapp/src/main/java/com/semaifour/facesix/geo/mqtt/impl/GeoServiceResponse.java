package com.semaifour.facesix.geo.mqtt.impl;

public class GeoServiceResponse {

    // gen-geotiff-request
    private String opcode;
    //file:///path/to/the/file.png
    private String srcimagepath;
    private String mapboximagepath;
    private String cid;
    private String sid;
    private String spid;
    private String status;
	
    public String getOpcode() {
		return opcode;
	}
	public String getSrcimagepath() {
		return srcimagepath;
	}
	public String getMapboximagepath() {
		return mapboximagepath;
	}
	public String getCid() {
		return cid;
	}
	public String getSid() {
		return sid;
	}
	public String getSpid() {
		return spid;
	}
	public String getStatus() {
		return status;
	}
	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}
	public void setSrcimagepath(String srcimagepath) {
		this.srcimagepath = srcimagepath;
	}
	public void setMapboximagepath(String mapboximagepath) {
		this.mapboximagepath = mapboximagepath;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "UploadResponseMessage [opcode=" + opcode + ", srcimagepath=" + srcimagepath + ", mapboximagepath="
				+ mapboximagepath + ", cid=" + cid + ", sid=" + sid + ", spid=" + spid + ", status=" + status + "]";
	}
}
