package com.semaifour.facesix.data.site;

import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;

public class Site extends FSObject {

	@Id
	private String id;
	private double latitude;
	private double longitude;
	private boolean supportFlag;
	private String customerId;
	
	public Site() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public boolean isSupportFlag() {
		return supportFlag;
	}

	public void setSupportFlag(boolean supportFlag) {
		this.supportFlag = supportFlag;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@Override
	public String toString() {
		return "Site [id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + ", supportFlag=" + supportFlag
				+ ", customerId=" + customerId + ", toString()=" + super.toString() + "]";
	}
	
	
	
}
