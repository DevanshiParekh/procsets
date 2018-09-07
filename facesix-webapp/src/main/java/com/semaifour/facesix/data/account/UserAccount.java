package com.semaifour.facesix.data.account;


import org.springframework.data.annotation.Id;
import com.semaifour.facesix.domain.FSObject;
public class UserAccount extends FSObject {

	@Id
	private String  id;
	private String  fname;
	private String  lname;
	private String  group;
	private String  email;
	private String  phone;
	private String  password;
	private String  path;
	private String  token;
	private String  designation;
	private String  role;
	private String  isMailalert;
	private String  isSmsalert;
	private String  customerId;//company id
	private boolean reset_request;
	private String  alexassid;
	private String  alexapage;
	private long    count;
	private long 	resetTime;
	private String customizeEmailSms;
	
    public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getIsMailalert() {
		return isMailalert;
	}

	public void setIsMailalert(String isMailalert) {
		this.isMailalert = isMailalert;
	}

	public String getIsSmsalert() {
		return isSmsalert;
	}

	public void setIsSmsalert(String isSmsalert) {
		this.isSmsalert = isSmsalert;
	}

	public UserAccount() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}


	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String isMailalert() {
		return isMailalert;
	}

	public void setMailalert(String isMailalert) {
		this.isMailalert = isMailalert;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public boolean getResetStatus(){
		return reset_request;
	}
	
	public void setResetStatus(boolean reset_req){
		this.reset_request=reset_req;
	}
	
	public boolean isReset_request() {
		return reset_request;
	}

	public String getAlexassid() {
		return alexassid;
	}

	public String getAlexapage() {
		return alexapage;
	}

	public void setReset_request(boolean reset_request) {
		this.reset_request = reset_request;
	}

	public void setAlexassid(String alexassid) {
		this.alexassid = alexassid;
	}

	public void setAlexapage(String alexapage) {
		this.alexapage = alexapage;
	}

	public long getResetTime() {
		return resetTime;
	}

	public void setResetTime(long resetTime) {
		this.resetTime = resetTime;
	}
	public String getCustomizeEmailSms() {
		return customizeEmailSms;
	}
	public void setCustomizeEmailSms(String customizeEmailSms) {
		this.customizeEmailSms = customizeEmailSms;
	}

	@Override
	public String toString() {
		return "UserAccount [id=" + id + ", fname=" + fname + ", lname=" + lname + ", group=" + group + ", email="
				+ email + ", phone=" + phone + ", password=" + password + ", path=" + path + ", token=" + token
				+ ", designation=" + designation + ", role=" + role + ", isMailalert=" + isMailalert + ", isSmsalert="
				+ isSmsalert + ", customerId=" + customerId + ", reset_request=" + reset_request + ", alexassid="
				+ alexassid + ", alexapage=" + alexapage + ", count=" + count + ", resetTime=" + resetTime
				+ ", customizeEmailSms=" + customizeEmailSms + "]";
	}
}
