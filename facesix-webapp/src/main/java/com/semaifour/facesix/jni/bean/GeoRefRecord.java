package com.semaifour.facesix.jni.bean;

import org.springframework.data.annotation.Id;

public class GeoRefRecord {

    @Id
    private String id;
    private String cid;
    private String sid;
    private String spid;
    private String message;
    private String geoTiffFilePath;
    private String mapboxUrl;
    
    public GeoRefRecord() {
    }

	public GeoRefRecord(String spid, String filePath) {
		this.spid = spid;
		geoTiffFilePath = filePath;
	}

	public String getId() {
		return id;
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

	public String getMessage() {
		return message;
	}

	public String getGeoTiffFilePath() {
		return geoTiffFilePath;
	}

	public String getMapboxUrl() {
		return mapboxUrl;
	}

	public void setId(String id) {
		this.id = id;
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

	public void setMessage(String message) {
		this.message = message;
	}

	public void setGeoTiffFilePath(String geoTiffFilePath) {
		this.geoTiffFilePath = geoTiffFilePath;
	}

	public void setMapboxUrl(String mapboxUrl) {
		this.mapboxUrl = mapboxUrl;
	}

	@Override
	public String toString() {
		return "GeoRefRecord [id=" + id + ", cid=" + cid + ", sid=" + sid + ", spid=" + spid + ", message=" + message
				+ ", geoTiffFilePath=" + geoTiffFilePath + ", mapboxUrl=" + mapboxUrl + "]";
	}    
}
