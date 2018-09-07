package com.semaifour.facesix.beacon.finder.geo;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;
import com.semaifour.facesix.jni.bean.GeoPoint;

public class GeoFinderLayoutData extends FSObject{

	@Id
	private String id;
	private String cid;
	private String sid;
	private String spid;
	private String type;
	private String fgJson;
	private String gmapMarkers;
	private String geoPoints;
	private String pixels;
	private String rotationangel;
    private List<GeoPoint> geoPointslist;
    private String georesult;
    private String pixelresult;
    private String coordinateresult;
    private String outputFilePath;

	
	public GeoFinderLayoutData(){
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpid() {
		return spid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFgJson() {
		return fgJson;
	}

	public void setFgJson(String fgJson) {
		this.fgJson = fgJson;
	}

	public String getGmapMarkers() {
		return gmapMarkers;
	}

	public void setGmapMarkers(String gmapMarkers) {
		this.gmapMarkers = gmapMarkers;
	}

	public String getGeoPoints() {
		return geoPoints;
	}

	public void setGeoPoints(String geoPoints) {
		this.geoPoints = geoPoints;
	}

	public String getPixels() {
		return pixels;
	}

	public void setPixels(String pixels) {
		this.pixels = pixels;
	}

	public List<GeoPoint> getGeoPointslist() {
		return geoPointslist;
	}

	public void setGeoPointslist(List<GeoPoint> geoPointslist) {
		this.geoPointslist = geoPointslist;
	}

	public String getRotationangel() {
		return rotationangel;
	}

	public void setRotationangel(String rotationangel) {
		this.rotationangel = rotationangel;
	}
	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public String getGeoresult() {
		return georesult;
	}

	public void setGeoresult(String georesult) {
		this.georesult = georesult;
	}

	public String getPixelresult() {
		return pixelresult;
	}

	public void setPixelresult(String pixelresult) {
		this.pixelresult = pixelresult;
	}

	public String getCoordinateresult() {
		return coordinateresult;
	}

	public void setCoordinateresult(String coordinateresult) {
		this.coordinateresult = coordinateresult;
	}

	@Override
	public String toString() {
		return "GeoFinderLayoutData [id=" + id + ", cid=" + cid + ", sid=" + sid + ", spid=" + spid + ", type=" + type
				+ ", fgJson=" + fgJson + ", gmapMarkers=" + gmapMarkers + ", geoPoints=" + geoPoints + ", pixels="
				+ pixels + ", rotationangel=" + rotationangel + ", geoPointslist=" + geoPointslist + ", georesult="
				+ georesult + ", pixelresult=" + pixelresult + ", coordinateresult=" + coordinateresult
				+ ", outputFilePath=" + outputFilePath + "]";
	}


	
}
