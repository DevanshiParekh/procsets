package com.semaifour.facesix.data.captive.portal;
import org.springframework.data.annotation.Id;
import com.semaifour.facesix.domain.FSObject;

public class Casting extends FSObject {
	
	@Id
	private String id;
	private String cid;
	private String customerName;
	private String lastModified;
	private String size;
	private String screenshot;
	private String file;
	private String type;
	private String path;
	private String fileType;
	
	
	public Casting() {}

	public String getId() {
		return id;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(String screenshot) {
		this.screenshot = screenshot;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@Override
	public String toString() {
		return "Casting [id=" + id + ", cid=" + cid + ", customerName=" + customerName + ", lastModified="
				+ lastModified + ", size=" + size + ", screenshot=" + screenshot + ", file=" + file + ", type=" + type
				+ ", path=" + path + ", fileType=" + fileType + "]";
	}


}
