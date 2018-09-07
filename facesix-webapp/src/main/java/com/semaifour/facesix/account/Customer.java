package com.semaifour.facesix.account;

import java.util.Date;
import org.springframework.data.annotation.Id;
import com.semaifour.facesix.domain.FSObject;

public class Customer extends FSObject {
	@Id
	private String id;
	private String customerName;
	private String address;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String venueType;
	private String offerPackage;
	private String solution;
	private String noOfGateway;
	private String preferedUrlName;
	private Date   serviceStartDate;
	private String serviceDurationinMonths;
	private Date   serviceExpiryDate;
	private String qubercommAssist;
	private String contactPerson;
	private String contactPersonlname;
	private String designation;
	private String contactNumber;
	private String mobileNumber;
	private String email;
	private String password;
	private String bleserverip;
	private String alexacerfilepath;
	private String alexakeyfilepath;
	private String alexaendpoint;
	private String alexatopic;
	private String tagcount;
	private String threshold;
	private String timezone;
	private String logs;
	private String tagInact;
	private String logofile;
	private String background;
	private String discover_text;
	private String discover_link;
	private String facebook;
	private String twitter;
	private String linkedin;
	private String userAccId;
	private String oauth;
	private String restToken;
	private String mqttToken;
	private String jwtrestToken; // JWT Rest Token
	private String jwtmqttToken; // JWT MQTT Token
	private String inactivityMail;
	private String inactivitySMS;
	private String battery_threshold;
	private String customizeFinderDevInacTime;
	private String customizeGatewyDevInacTime;
	private int    tagAlertCount;
	private int    batteryAlertCount;
	private int    deviceAlertCount;
	private String vpn;
	
	/*
	 * Support EmailId details
	 */
	
	private String custSupportEmailEnable;
	private String custSupportEmailId;
	private String custSupportPassword;
	private String custSupportHost;
	private String custSupportPort;
	
	public Customer() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getVenueType() {
		return venueType;
	}

	public void setVenueType(String venueType) {
		this.venueType = venueType;
	}

	public String getOfferPackage() {
		return offerPackage;
	}

	public void setOfferPackage(String offerPackage) {
		this.offerPackage = offerPackage;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getNoOfGateway() {
		return noOfGateway;
	}

	public void setNoOfGateway(String noOfGateway) {
		this.noOfGateway = noOfGateway;
	}

	public String getPreferedUrlName() {
		return preferedUrlName;
	}

	public void setPreferedUrlName(String preferedUrlName) {
		this.preferedUrlName = preferedUrlName;
	}

	public Date getServiceStartDate() {
		return serviceStartDate;
	}

	public void setServiceStartDate(Date serviceStartDate) {
		this.serviceStartDate = serviceStartDate;
	}

	public String getServiceDurationinMonths() {
		return serviceDurationinMonths;
	}

	public void setServiceDurationinMonths(String serviceDurationinMonths) {
		this.serviceDurationinMonths = serviceDurationinMonths;
	}

	public Date getServiceExpiryDate() {
		return serviceExpiryDate;
	}

	public void setServiceExpiryDate(Date serviceExpiryDate) {
		this.serviceExpiryDate = serviceExpiryDate;
	}

	public String getQubercommAssist() {
		return qubercommAssist;
	}

	public void setQubercommAssist(String qubercommAssist) {
		this.qubercommAssist = qubercommAssist;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactPersonlname() {
		return contactPersonlname;
	}

	public void setContactPersonlname(String contactPersonlname) {
		this.contactPersonlname = contactPersonlname;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBleserverip() {
		return bleserverip;
	}

	public void setBleserverip(String bleserverip) {
		this.bleserverip = bleserverip;
	}

	public String getAlexacerfilepath() {
		return alexacerfilepath;
	}

	public void setAlexacerfilepath(String alexacerfilepath) {
		this.alexacerfilepath = alexacerfilepath;
	}

	public String getAlexakeyfilepath() {
		return alexakeyfilepath;
	}

	public void setAlexakeyfilepath(String alexakeyfilepath) {
		this.alexakeyfilepath = alexakeyfilepath;
	}

	public String getAlexaendpoint() {
		return alexaendpoint;
	}

	public void setAlexaendpoint(String alexaendpoint) {
		this.alexaendpoint = alexaendpoint;
	}

	public String getAlexatopic() {
		return alexatopic;
	}

	public void setAlexatopic(String alexatopic) {
		this.alexatopic = alexatopic;
	}

	public String getTagcount() {
		return tagcount;
	}

	public void setTagcount(String tagcount) {
		this.tagcount = tagcount;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getLogs() {
		return logs;
	}

	public void setLogs(String logs) {
		this.logs = logs;
	}

	public String getTagInact() {
		return tagInact;
	}

	public void setTagInact(String tagInact) {
		this.tagInact = tagInact;
	}
	
	public String getDiscover_text() {
		return discover_text;
	}

	public void setDiscover_text(String discover_text) {
		this.discover_text = discover_text;
	}

	public String getDiscover_link() {
		return discover_link;
	}

	public void setDiscover_link(String discover_link) {
		this.discover_link = discover_link;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}

	public String getLogofile() {
		return logofile;
	}

	public void setLogofile(String logofile) {
		this.logofile = logofile;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getUserAccId() {
		return userAccId;
	}

	public void setUserAccId(String userAccId) {
		this.userAccId = userAccId;
	}

	public String getRestToken() {
		return restToken;
	}

	public void setRestToken(String restToken) {
		this.restToken = restToken;
	}

	public String getMqttToken() {
		return mqttToken;
	}

	public void setMqttToken(String mqttToken) {
		this.mqttToken = mqttToken;
	}
	public String getOauth() {
		return oauth;
	}

	public void setOauth(String oauth) {
		this.oauth = oauth;
	}

	public String getJwtrestToken() {
		return jwtrestToken;
	}

	public void setJwtrestToken(String jwtrestToken) {
		this.jwtrestToken = jwtrestToken;
	}

	public String getJwtmqttToken() {
		return jwtmqttToken;
	}

	public void setJwtmqttToken(String jwtmqttToken) {
		this.jwtmqttToken = jwtmqttToken;
	}

	public String getInactivityMail() {
		return inactivityMail;
	}

	public void setInactivityMail(String inactivityMail) {
		this.inactivityMail = inactivityMail;
	}

	public String getInactivitySMS() {
		return inactivitySMS;
	}

	public void setInactivitySMS(String inactivitySMS) {
		this.inactivitySMS = inactivitySMS;
	}
	public String getBattery_threshold() {
		return battery_threshold;
	}

	public void setBattery_threshold(String battery_threshold) {
		this.battery_threshold = battery_threshold;
	}
	public String getCustomizeFinderDevInacTime() {
		return customizeFinderDevInacTime;
	}

	public void setCustomizeFinderDevInacTime(String customizeFinderDevInacTime) {
		this.customizeFinderDevInacTime = customizeFinderDevInacTime;
	}

	public String getCustomizeGatewyDevInacTime() {
		return customizeGatewyDevInacTime;
	}

	public void setCustomizeGatewyDevInacTime(String customizeGatewyDevInacTime) {
		this.customizeGatewyDevInacTime = customizeGatewyDevInacTime;
	}
	public int getTagAlertCount() {
		return tagAlertCount;
	}
	public void setTagAlertCount(int tagAlertCount) {
		this.tagAlertCount = tagAlertCount;
	}
	public int getBatteryAlertCount() {
		return batteryAlertCount;
	}

	public void setBatteryAlertCount(int batteryAlertCount) {
		this.batteryAlertCount = batteryAlertCount;
	}
	public int getDeviceAlertCount() {
		return deviceAlertCount;
	}

	public void setDeviceAlertCount(int deviceAlertCount) {
		this.deviceAlertCount = deviceAlertCount;
	}
	public String getCustSupportEmailId() {
		return custSupportEmailId;
	}

	public void setCustSupportEmailId(String custSupportEmailId) {
		this.custSupportEmailId = custSupportEmailId;
	}

	public String getCustSupportPassword() {
		return custSupportPassword;
	}

	public void setCustSupportPassword(String custSupportPassword) {
		this.custSupportPassword = custSupportPassword;
	}

	public String getCustSupportHost() {
		return custSupportHost;
	}

	public void setCustSupportHost(String custSupportHost) {
		this.custSupportHost = custSupportHost;
	}

	public String getCustSupportPort() {
		return custSupportPort;
	}

	public void setCustSupportPort(String custSupportPort) {
		this.custSupportPort = custSupportPort;
	}

	public String getCustSupportEmailEnable() {
		return custSupportEmailEnable;
	}

	public void setCustSupportEmailEnable(String custSupportEmailEnable) {
		this.custSupportEmailEnable = custSupportEmailEnable;
	}

	public String getVpn() {
		return vpn;
	}

	public void setVpn(String vpn) {
		this.vpn = vpn;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", customerName=" + customerName + ", address=" + address + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", postalCode=" + postalCode + ", venueType="
				+ venueType + ", offerPackage=" + offerPackage + ", solution=" + solution + ", noOfGateway="
				+ noOfGateway + ", preferedUrlName=" + preferedUrlName + ", serviceStartDate=" + serviceStartDate
				+ ", serviceDurationinMonths=" + serviceDurationinMonths + ", serviceExpiryDate=" + serviceExpiryDate
				+ ", qubercommAssist=" + qubercommAssist + ", contactPerson=" + contactPerson + ", contactPersonlname="
				+ contactPersonlname + ", designation=" + designation + ", contactNumber=" + contactNumber
				+ ", mobileNumber=" + mobileNumber + ", email=" + email + ", password=" + password + ", bleserverip="
				+ bleserverip + ", alexacerfilepath=" + alexacerfilepath + ", alexakeyfilepath=" + alexakeyfilepath
				+ ", alexaendpoint=" + alexaendpoint + ", alexatopic=" + alexatopic + ", tagcount=" + tagcount
				+ ", threshold=" + threshold + ", timezone=" + timezone + ", logs=" + logs + ", tagInact=" + tagInact
				+ ", logofile=" + logofile + ", background=" + background + ", discover_text=" + discover_text
				+ ", discover_link=" + discover_link + ", facebook=" + facebook + ", twitter=" + twitter + ", linkedin="
				+ linkedin + ", userAccId=" + userAccId + ", oauth=" + oauth + ", restToken=" + restToken
				+ ", mqttToken=" + mqttToken + ", jwtrestToken=" + jwtrestToken + ", jwtmqttToken=" + jwtmqttToken
				+ ", inactivityMail=" + inactivityMail + ", inactivitySMS=" + inactivitySMS + ", battery_threshold="
				+ battery_threshold + ", customizeFinderDevInacTime=" + customizeFinderDevInacTime
				+ ", customizeGatewyDevInacTime=" + customizeGatewyDevInacTime + ", tagAlertCount=" + tagAlertCount
				+ ", batteryAlertCount=" + batteryAlertCount + ", deviceAlertCount=" + deviceAlertCount + ", vpn="
				+ vpn + ", custSupportEmailEnable=" + custSupportEmailEnable + ", custSupportEmailId="
				+ custSupportEmailId + ", custSupportPassword=" + custSupportPassword + ", custSupportHost="
				+ custSupportHost + ", custSupportPort=" + custSupportPort + "]";
	}
}
