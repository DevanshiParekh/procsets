package com.semaifour.facesix.data.elasticsearch.device;

import org.json.simple.JSONArray;
import org.springframework.data.annotation.Id;
import com.semaifour.facesix.domain.FSObject;

public class DeviceBssid extends FSObject {

	@Id
	private String 		id;
	private String 		cid;
	private String 		bssid;
	private String 		radio_type;
	private String 		mesh_id;
	private int 		channel;
	private JSONArray 	mesh_links;

	public DeviceBssid() {
		super();
	}

	public String getId() {
		return id;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getRadio_type() {
		return radio_type;
	}

	public void setRadio_type(String radio_type) {
		this.radio_type = radio_type;
	}

	public String getMesh_id() {
		return mesh_id;
	}

	public void setMesh_id(String mesh_id) {
		this.mesh_id = mesh_id;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public JSONArray getMesh_links() {
		return mesh_links;
	}

	public void setMesh_links(JSONArray mesh_links) {
		this.mesh_links = mesh_links;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "DeviceBssid [id=" + id + ", cid=" + cid + ", bssid=" + bssid + ", radio_type=" + radio_type
				+ ", mesh_id=" + mesh_id + ", channel=" + channel + ", mesh_links=" + mesh_links + "]";
	}

}
