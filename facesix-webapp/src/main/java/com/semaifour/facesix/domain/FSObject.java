package com.semaifour.facesix.domain;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.semaifour.facesix.data.site.Portion;

@JsonInclude(Include.NON_NULL)
public class FSObject {
	
	//standard fields
	private String pkid;
	private String uid;
	private String name;
	private String description;
	private String typefs;
	private Properties settings;
	private String jedittings;
	private String status;
	
	//audit fields
	private String version;
	private String createdBy;
	private String modifiedBy;
	private Date createdOn;
	private Date modifiedOn;
	
	public FSObject() {
		createdOn = new Date(System.currentTimeMillis());
		modifiedOn = createdOn;
	}
	
	public FSObject(String uid, String name, String typefs) {
		this.uid = uid;
		this.name = name;
		this.typefs = typefs;
		createdOn = new Date(System.currentTimeMillis());
		modifiedOn = createdOn;
	}

	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
	public Properties getSettings() {
		return settings;
	}

	public void setSettings(Properties settings) {
		this.settings = settings;
	}
	
	public void update(FSObject newfso) {
		this.uid = newfso.uid;
		this.name = newfso.name;
		this.description = newfso.description;
		this.typefs = newfso.typefs;
		this.settings = newfso.settings;
		this.jedittings = newfso.jedittings;
	}
	
	public String getSettingsAsString() {
		StringBuilder sb = new StringBuilder();
		if (getSettings() != null) {
			for(Entry<Object, Object> entry : getSettings().entrySet()) {
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append(System.getProperty("line.separator"));
			}
		}
		return sb.toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTypefs() {
		return typefs;
	}

	public void setTypefs(String typefs) {
		this.typefs = typefs;
	}

	public String getJedittings() {
		return jedittings;
	}

	public void setJedittings(String jedittings) {
		this.jedittings = jedittings;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPkid() {
		return pkid;
	}

	public void setPkid(String pkid) {
		this.pkid = pkid;
	}

	@Override
	public String toString() {
		return "FSObject [uid=" + uid + ", name=" + name + ", description=" + description + ", typefs=" + typefs
				+ ", settings=" + settings + ", jedittings=" + jedittings + ", status=" + status + ", version="
				+ version + ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy + ", createdOn=" + createdOn
				+ ", modifiedOn=" + modifiedOn + ",pkid" + pkid + "]";
	}

	public int compareTo(Portion arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
