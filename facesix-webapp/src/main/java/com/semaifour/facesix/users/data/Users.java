package com.semaifour.facesix.users.data;

import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;

public class Users extends FSObject {

	@Id
	private String id;
	private String fname;
	private String lname;
	private String email;
	private String phone;
	private String password;
	private String path;
	private String token;
	private String designation;
	private String role;
	private String isMailalert;
	private String isSmsalert;
	private String customerId;
	
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	@Override
	public String toString() {
		return "Users [id=" + id + ", fname=" + fname + ", lname=" + lname + ", email=" + email + ", phone=" + phone
				+ ", password=" + password + ", path=" + path + ", token=" + token
				+ ", designation=" + designation + ", role=" + role + ", isMailalert=" + isMailalert + ", isSmsalert="
				+ isSmsalert + ", customerId=" + customerId + "]";
	}
	
	
	
}
