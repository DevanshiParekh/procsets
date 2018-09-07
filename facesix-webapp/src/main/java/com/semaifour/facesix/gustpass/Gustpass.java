package com.semaifour.facesix.gustpass;
import java.util.Date;
import org.springframework.data.annotation.Id;
import com.semaifour.facesix.domain.FSObject;

public class Gustpass extends FSObject {
	
	@Id
	private String id;
	private String passName;
	private String mobileNumber;
	private String allowedNetwork;
	private String expireson;	
	private String passStatus;
	private String email;
	private String notes;
	private String noOfdevices;
	private String sendByMobile;
	private String sendByEmail;
	private String printGuestPass;
	private String customerId;
	private Date guestpassexpireson;
	private Date guestpassstartingon;
	private String token;
	private String portalType;
	private String captivehours;
	private String ssid;
	
	public Gustpass() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassName() {
		return passName;
	}

	public void setPassName(String passName) {
		this.passName = passName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getAllowedNetwork() {
		return allowedNetwork;
	}

	public void setAllowedNetwork(String allowedNetwork) {
		this.allowedNetwork = allowedNetwork;
	}

	public String getExpireson() {
		return expireson;
	}

	public void setExpireson(String expireson) {
		this.expireson = expireson;
	}

	public String getPassStatus() {
		return passStatus;
	}

	public void setPassStatus(String passStatus) {
		this.passStatus = passStatus;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNoOfdevices() {
		return noOfdevices;
	}

	public void setNoOfdevices(String noOfdevices) {
		this.noOfdevices = noOfdevices;
	}

	public String getSendByMobile() {
		return sendByMobile;
	}

	public void setSendByMobile(String sendByMobile) {
		this.sendByMobile = sendByMobile;
	}

	public String getSendByEmail() {
		return sendByEmail;
	}

	public void setSendByEmail(String sendByEmail) {
		this.sendByEmail = sendByEmail;
	}

	public String getPrintGuestPass() {
		return printGuestPass;
	}

	public void setPrintGuestPass(String printGuestPass) {
		this.printGuestPass = printGuestPass;
	}

	
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Date getGuestpassexpireson() {
		return guestpassexpireson;
	}

	public void setGuestpassexpireson(Date guestpassexpireson) {
		this.guestpassexpireson = guestpassexpireson;
	}
	
	

	public Date getGuestpassstartingon() {
		return guestpassstartingon;
	}

	public void setGuestpassstartingon(Date guestpassstartingon) {
		this.guestpassstartingon = guestpassstartingon;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	public String getCaptivehours() {
		return captivehours;
	}

	public void setCaptivehours(String captivehours) {
		this.captivehours = captivehours;
	}
	public String getPortalType() {
		return portalType;
	}

	public void setPortalType(String portalType) {
		this.portalType = portalType;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	@Override
	public String toString() {
		return "Gustpass [id=" + id + ", passName=" + passName + ", mobileNumber=" + mobileNumber + ", allowedNetwork="
				+ allowedNetwork + ", expireson=" + expireson + ", passStatus=" + passStatus + ", email=" + email
				+ ", notes=" + notes + ", noOfdevices=" + noOfdevices + ", sendByMobile=" + sendByMobile
				+ ", sendByEmail=" + sendByEmail + ", printGuestPass=" + printGuestPass + ", customerId=" + customerId
				+ ", guestpassexpireson=" + guestpassexpireson + ", guestpassstartingon=" + guestpassstartingon
				+ ", token=" + token + ", portalType=" + portalType + ", captivehours=" + captivehours + "]";
	}
}
