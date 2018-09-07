package com.semaifour.facesix.geo.data;

import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;

/**
 * 
 * Poi represents an instance of "point of interest" 
 * 
 * @author jay
 *
 */
public class Poi extends FSObject {
	@Id
	private String id;	
	private String spid;
	private String iconUrl;
	private String filepath;
	private double latitude;
	private double longitude;
	
	public Poi(){		
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

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "Poi [id=" + id + ", spid=" + spid + ", iconUrl=" + iconUrl + ", filepath=" + filepath + ", latitude="
				+ latitude + ", longitude=" + longitude + "]";
	}
}
