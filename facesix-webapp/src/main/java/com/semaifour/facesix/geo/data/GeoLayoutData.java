package com.semaifour.facesix.geo.data;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.semaifour.facesix.domain.FSObject;

public class GeoLayoutData extends FSObject{

	@Id
	private String id;
	private String spid;
	private String type;
	@JsonProperty("fg_json")
	private String fgJson;
	@JsonProperty("gmap_markers")
	private String gmapMarkers;
	@JsonProperty("geo_points")
	private String geoPoints;
	private String pixels;
	
	public GeoLayoutData(){
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

	@Override
	public String toString() {
		return "GeoLayoutData [id=" + id + ", spid=" + spid + ", type=" + type + ", fgJson=" + fgJson + ", gmapMarkers="
				+ gmapMarkers + ", geoPoints=" + geoPoints + ", pixels=" + pixels + "]";
	}
}
