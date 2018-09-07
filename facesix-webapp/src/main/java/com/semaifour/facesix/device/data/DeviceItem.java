package com.semaifour.facesix.device.data;

import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;

public class DeviceItem extends FSObject {

	@Id
	private String id;
	

	public DeviceItem(String uid, String mac, String type) {
		super(uid, mac, type);
	}
		
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getMac() {
		return super.getName();
	}
	
	public void setMac(String mac) {
		super.setName(mac);
	}

	@Override
	public String toString() {
		return "DeviceItem [id=" + id + ", FSObject ()=" + super.toString() + "]";
	}
 
	
}
