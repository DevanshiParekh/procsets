package com.semaifour.facesix.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.web.WelcomeController;

@Service
public class Contactus {
	
	static Logger LOG = LoggerFactory.getLogger(WelcomeController.class.getName());
	public static final String to_addr  	= "support@qubercomm.com";
	public static final String user_name	= "support@qubercomm.com";
	public static final String passwd		= "Support@quber!";
	public static final String smtp			= "smtp.gmail.com";
	public static final String port			= "587";

	@Id
	private String name;
	private String email;
	private String pno;
	private String desc;
	
	public Contactus() {
		// TODO Auto-generated constructor stub
	}
	
	public String getName() {
		return name;
	}
		
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public String getPno() {
		return pno;
	}
	
	public void setPno(String pno) {
		this.pno = pno;
	}
	
	public String getDesc() {
		return desc;
	}	
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getTO() {
		return to_addr;
	}
	
	public String getuser() {
		return user_name;
	}
	
	public String getpwd() {
		return passwd;
	}	
	
}
