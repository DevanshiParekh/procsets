package com.semaifour.facesix.data.elasticsearch.notif;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "fsi-notification-#{systemProperties['fs.app'] ?: 'default'}", type = "notification")
public class Notification {
	
	@Id
	private String id;
	
	private String message;
	private String status;
	private String source;
	
	private String version;
	private String createdBy;
	private String modifiedBy;
	@Field(type= FieldType.Date)
	private Date createdOn;
	@Field(type= FieldType.Date)
	private Date modifiedOn;
	private int score;
	
	@Field(type= FieldType.Nested)
	private Collection<String> tags;
	
	public Notification() {
		super();
	}
	
	public Notification(String message, String status, String source, String target) {
		super();
		this.setMessage(message);
		this.setStatus(status);
		this.source = source;
		this.version = "1.0";
		this.createdBy = target;
		this.modifiedBy = target;
		this.createdOn = new Date(System.currentTimeMillis());
		this.modifiedOn = this.createdOn;
	}
	
	public Notification(String message, String status,
			String version, String createdBy, String modifiedBy,
			Date createdOn, Date modifiedOn) {
		super();
		this.setMessage(message);
		this.setStatus(status);
		this.version = version;
		this.createdBy = createdBy;
		this.modifiedBy = modifiedBy;
		this.createdOn = createdOn;
		this.modifiedOn = modifiedOn;
	}
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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
	
	public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }
    
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Notification [id=" + id + ", message=" + message + ", status="
				+ status + ", version=" + version + ", createdBy=" + createdBy
				+ ", modifiedBy=" + modifiedBy + ", createdOn=" + createdOn
				+ ", modifiedOn=" + modifiedOn + ", score=" + score + ", tags="
				+ tags + "]";
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	
	

}
