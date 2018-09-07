package com.semaifour.facesix.probe.oui;

import org.springframework.data.annotation.Id;
import com.semaifour.facesix.domain.FSObject;

public class ProbeOUI extends FSObject{
	
	@Id
	private String id;
	private String vendorName;
	
	public ProbeOUI() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	@Override
	public String toString() {
		return "ProbeOUI [id=" + id + ", vendorName=" + vendorName + "]";
	}
	
}
