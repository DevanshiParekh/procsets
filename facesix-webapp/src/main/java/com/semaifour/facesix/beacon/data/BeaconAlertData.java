package com.semaifour.facesix.beacon.data;

import org.springframework.data.annotation.Id;

import net.sf.json.JSONArray;

public class BeaconAlertData {

	@Id
	private String id;
	private String cid;
	private String tagtype;
	private JSONArray tagnames;
	private JSONArray tagids;
	private String place;
	private JSONArray placenames;
	private JSONArray placeIds;
	private long duration;
	
	public String getId() {
		return id;
	}
	
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getTagtype() {
		return tagtype;
	}
	public void setTagtype(String tagtype) {
		this.tagtype = tagtype;
	}
	public JSONArray getTagnames() {
		return tagnames;
	}
	public void setTagnames(JSONArray tagnames) {
		this.tagnames = tagnames;
	}
	public JSONArray getTagids() {
		return tagids;
	}
	public void setTagids(JSONArray tagids) {
		this.tagids = tagids;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public JSONArray getPlacenames() {
		return placenames;
	}
	public void setPlacenames(JSONArray placenames) {
		this.placenames = placenames;
	}
	public JSONArray getPlaceIds() {
		return placeIds;
	}
	public void setPlaceIds(JSONArray placeIds) {
		this.placeIds = placeIds;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "BeaconAlertData [id=" + id + ", cid=" + cid + ", tagtype=" + tagtype + ", tagnames=" + tagnames
				+ ", tagids=" + tagids + ", place=" + place + ", placenames=" + placenames + ", placeIds=" + placeIds
				+ ", duration=" + duration + "]";
	}
}
