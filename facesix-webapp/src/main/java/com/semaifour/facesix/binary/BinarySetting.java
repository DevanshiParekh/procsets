package com.semaifour.facesix.binary;

import org.springframework.data.annotation.Id;
import com.semaifour.facesix.domain.FSObject;

public class BinarySetting extends FSObject{
	
	@Id
	private String id;
	private String planFilepath;
	private String mqttfilePath;
	private String md5Checksum;
	private String configVersion;
	private String cid;
	private String sid;
	private String spid;
	private String reason;
	private String u_id;
	private String upgradeType;
	private String binaryType;
	
	public BinarySetting() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlanFilepath() {
		return planFilepath;
	}

	public void setPlanFilepath(String planFilepath) {
		this.planFilepath = planFilepath;
	}

	public String getMqttfilePath() {
		return mqttfilePath;
	}

	public void setMqttfilePath(String mqttfilePath) {
		this.mqttfilePath = mqttfilePath;
	}

	public String getMd5Checksum() {
		return md5Checksum;
	}

	public void setMd5Checksum(String md5Checksum) {
		this.md5Checksum = md5Checksum;
	}

	public String getConfigVersion() {
		return configVersion;
	}

	public void setConfigVersion(String configVersion) {
		this.configVersion = configVersion;
	}

	public String getSpid() {
		return spid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getU_id() {
		return u_id;
	}

	public void setU_id(String u_id) {
		this.u_id = u_id;
	}

	
	public String getUpgradeType() {
		return upgradeType;
	}

	public void setUpgradeType(String upgradeType) {
		this.upgradeType = upgradeType;
	}

	public String getBinaryType() {
		return binaryType;
	}

	public void setBinaryType(String binaryType) {
		this.binaryType = binaryType;
	}

	@Override
	public String toString() {
		return "BinarySetting [id=" + id + ", planFilepath=" + planFilepath + ", mqttfilePath=" + mqttfilePath
				+ ", md5Checksum=" + md5Checksum + ", configVersion=" + configVersion + ", cid=" + cid + ", sid=" + sid
				+ ", spid=" + spid + ", reason=" + reason + ", u_id=" + u_id + ", upgradeType=" + upgradeType
				+ ", binaryType=" + binaryType + "]";
	}

	
	
}
