package com.semaifour.facesix.data.captive.portal;
import java.util.Date;
import org.springframework.data.annotation.Id;
import com.semaifour.facesix.domain.FSObject;

public class PortalUsers extends FSObject {
	
	@Id
	private String id;
	private String username;
	private String email;
	private String phone;
	private boolean terms;
	private String portalId;
	private String cid;
	private String customerName;
	private String customField;
	private String token;
	private String tokenStatus;
	private Date   approvedDate;
	
	public PortalUsers() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPortalId() {
		return portalId;
	}

	public void setPortalId(String portalId) {
		this.portalId = portalId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean getTerms() {
		return terms;
	}

	public void setTerms(boolean terms) {
		this.terms = terms;
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

	public String getCustomField() {
		return customField;
	}

	public void setCustomField(String customField) {
		this.customField = customField;
	}
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenStatus() {
		return tokenStatus;
	}

	public void setTokenStatus(String tokenStatus) {
		this.tokenStatus = tokenStatus;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	@Override
	public String toString() {
		return "PortalUsers [id=" + id + ", username=" + username + ", email=" + email + ", phone=" + phone + ", terms="
				+ terms + ", portalId=" + portalId + ", cid=" + cid + ", customerName=" + customerName
				+ ", customField=" + customField + ", token=" + token + ", tokenStatus=" + tokenStatus
				+ ", approvedDate=" + approvedDate + "]";
	}
	
}
