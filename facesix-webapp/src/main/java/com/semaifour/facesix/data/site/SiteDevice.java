package com.semaifour.facesix.data.site;

import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;

public class SiteDevice extends FSObject {
	@Id
	private String id;
	private String siteId;
	private String portionId;
	private String deviceUid;
	private String deviceType;
	private String deviceState;
	private String deviceLocation;
	private String deviceGeoloc;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public String getPortionId() {
		return portionId;
	}
	public void setPortionId(String portionId) {
		this.portionId = portionId;
	}
	public String getDeviceUid() {
		return deviceUid;
	}
	public void setDeviceUid(String deviceUid) {
		this.deviceUid = deviceUid;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getDeviceState() {
		return deviceState;
	}
	public void setDeviceState(String deviceState) {
		this.deviceState = deviceState;
	}
	public String getDeviceLocation() {
		return deviceLocation;
	}
	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}
	public String getDeviceGeoloc() {
		return deviceGeoloc;
	}
	public void setDeviceGeoloc(String deviceGeoloc) {
		this.deviceGeoloc = deviceGeoloc;
	}
	
	@Override
	public String toString() {
		return "SiteDevice [id=" + id + ", siteId=" + siteId + ", portionId=" + portionId + ", deviceUid=" + deviceUid
				+ ", deviceType=" + deviceType + ", deviceState=" + deviceState + ", deviceLocation=" + deviceLocation
				+ ", deviceGeoloc=" + deviceGeoloc + ", toString()=" + super.toString() + "]";
	}
	
	
}
