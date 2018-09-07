package com.semaifour.facesix.data.site;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;

public class Portion extends FSObject implements Serializable, Comparable<Portion>{
	private static final long serialVersionUID = -2960789727260329448L;

	@Id
	private String 	id;
	private String 	siteId;
	private String	planFilepath;
	private int 	width;
	private int 	height;
	private int 	length;
	private int 	breadth;
	private double 	latitude;
	private double 	longitude;
	private String 	networkConfigJson;
	private String 	cid;
	private String 	mapUrl;
	private String 	plotOperationStatus;
	private String 	JNIFilepath;
	public String 	activeTagType;
	public int 		activeTagCount;
	public String 	activeTagList;
	public String   imageJson;
	
	public Portion() {
	}

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

	public String getPlanFilepath() {
		return planFilepath;
	}

	public void setPlanFilepath(String planFilepath) {
		this.planFilepath = planFilepath;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getBreadth() {
		return breadth;
	}

	public void setBreadth(int breadth) {
		this.breadth = breadth;
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
		
	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getNetworkConfigJson() {
		return networkConfigJson;
	}

	public void setNetworkConfigJson(String networkConfigJson) {
		this.networkConfigJson = networkConfigJson;
	}
	
	public String getMapUrl() {
		return mapUrl;
	}

	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}	
	
	public String getJNIFilepath() {
		return JNIFilepath;
	}

	public void setJNIFilepath(String JNIFilepath) {
		this.JNIFilepath = JNIFilepath;
	}

	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int compareTo(Portion arg0) {
		int str = id.compareTo(arg0.id);
		return str;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object p) {
		if (p == null) return false;
		if (this == p) return true;
		if ((p instanceof Portion)) {
			return this.compareTo((Portion)p) == 0;
		}
		return false;
	}

	public String getPlotOperationStatus() {
		return plotOperationStatus;
	}

	public void setPlotOperationStatus(String plotOperationStatus) {
		this.plotOperationStatus = plotOperationStatus;
	}

	public String getActiveTagType() {
		return activeTagType;
	}

	public void setActiveTagType(String activeTagType) {
		this.activeTagType = activeTagType;
	}

	public int getActiveTagCount() {
		return activeTagCount;
	}

	public void setActiveTagCount(int activeTagCount) {
		this.activeTagCount = activeTagCount;
	}

	public String getActiveTagList() {
		return activeTagList;
	}

	public void setActiveTagList(String activeTagList) {
		this.activeTagList = activeTagList;
	}

	public String getImageJson() {
		return imageJson;
	}

	public void setImageJson(String imageJson) {
		this.imageJson = imageJson;
	}

	@Override
	public String toString() {
		return "Portion [id=" + id + ", siteId=" + siteId + ", planFilepath=" + planFilepath + ", width=" + width
				+ ", height=" + height + ", length=" + length + ", breadth=" + breadth + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", networkConfigJson=" + networkConfigJson + ", cid=" + cid + ", mapUrl="
				+ mapUrl + ", plotOperationStatus=" + plotOperationStatus + ", JNIFilepath=" + JNIFilepath
				+ ", activeTagType=" + activeTagType + ", activeTagCount=" + activeTagCount + ", activeTagList="
				+ activeTagList + "]";
	}


}
