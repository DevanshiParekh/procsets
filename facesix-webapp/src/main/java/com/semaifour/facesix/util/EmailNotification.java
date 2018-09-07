package com.semaifour.facesix.util;



import org.springframework.mail.SimpleMailMessage;


public class EmailNotification {
	
	
	public static SimpleMailMessage constructResetTokenEmail(String emailId, String name, String message) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(emailId);
		email.setSubject("Qubercomm Notification");
		email.setText("Hi" + "," + name + " " + message);
		return email;
	}

}
