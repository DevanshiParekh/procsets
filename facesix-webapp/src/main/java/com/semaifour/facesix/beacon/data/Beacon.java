package com.semaifour.facesix.beacon.data;

import java.util.Date;

import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;


/**
 * 
 * Beacon represents an instance of beacon device
 * 
 * @author mjs
 *
 */
public class Beacon extends FSObject {

	@Id
	private String id;
	
	private String	macaddr;
	private String 	scannerUid;
	private String 	assignedTo;
	private int 	minor;
	private int 	major;
	private String 	cid;
	private String 	sid;
	private String 	spid;
	private String 	uuid;
	private int 	battery_level;
	private String 	device_name;
	private String 	reciverId;
	private String 	location;
	private int 	rssi;
	private double 	distance;
	private int 	rxbytes;
	private String 	updatedstatus; //beacon entry or exit status
	private String 	visit_id;
	private String 	tag_type;
	private String 	template;
	private int 	txpower;
	private int 	interval;
	private double  accuracy;
	private double  range;
	private double 	lat;
	private double 	lon;
	private String 	x;
	private String 	y;
	private String  reciverinfo;
	private long 	battery_timestamp;
	private String  entry_floor;
	private String  entry_loc;
	private String	serverid;
	private String  state;
	private long    lastSeen;
	private String  reciveralias; // Receiver  Alias
	private String  addrtype;
	private int 	width;
	private int 	height;
	private long    lastactive;
	private String  mailsent;
	private String  debug;
	private String  lastReportingTime;
	private String  tagmodel;
	private String  reftxpwr;
	private String  localInactivityMailSent;
	private String  assginedLocationLastSeen;
	private String  exitTime;
	
	private Date recordSent;
	private Date recordSeen;
	private Date recordUpdate;
	private double avgUpdateTime;
	private double avgProcessTime;
	private double avgReceiveTime;
	
	public Beacon() {
		super();
	}

	public String getId() {
		return id;
	}

	public String getMacaddr() {
		return macaddr;
	}

	public String getScannerUid() {
		return scannerUid;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public int getMinor() {
		return minor;
	}

	public int getMajor() {
		return major;
	}

	public String getCid() {
		return cid;
	}

	public String getSid() {
		return sid;
	}

	public String getSpid() {
		return spid;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMacaddr(String macaddr) {
		this.macaddr = macaddr;
	}

	public void setScannerUid(String scannerUid) {
		this.scannerUid = scannerUid;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getBattery_level() {
		return battery_level;
	}

	public void setBattery_level(int battery_level) {
		this.battery_level = battery_level;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getReciverId() {
		return reciverId;
	}

	public void setReciverId(String reciverId) {
		this.reciverId = reciverId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getRxbytes() {
		return rxbytes;
	}

	public void setRxbytes(int rxbytes) {
		this.rxbytes = rxbytes;
	}

	public String getUpdatedstatus() {
		return updatedstatus;
	}

	public void setUpdatedstatus(String updatedstatus) {
		this.updatedstatus = updatedstatus;
	}
	
	
	public String getVisitID() {
		return visit_id;
	}

	public void setVisitID(String visit_id) {
		this.visit_id = visit_id;
	}
	
	public String getTagType () {
		return tag_type;
	}
	
	public void setTagType(String tag_type) {
		this.tag_type = tag_type;
	}	

	public String getTemplate() {
		return template;
	}
	
	public void setInterval(int interval){
		this.interval=interval;
	}
	
	public int getInterval(){
		return interval;
	}
	
	public void setTxPower(int txpower){
		this.txpower=txpower;
	}
	
	public int getTxPower(){
		return txpower;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getVisit_id() {
		return visit_id;
	}

	public void setVisit_id(String visit_id) {
		this.visit_id = visit_id;
	}

	public String getTag_type() {
		return tag_type;
	}

	public void setTag_type(String tag_type) {
		this.tag_type = tag_type;
	}

	public int getTxpower() {
		return txpower;
	}

	public void setTxpower(int txpower) {
		this.txpower = txpower;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getReciverinfo() {
		return reciverinfo;
	}

	public void setReciverinfo(String reciverinfo) {
		this.reciverinfo = reciverinfo;
	}

	public long getBattery_timestamp() {
		return battery_timestamp;
	}

	public void setBattery_timestamp(long battery_timestamp) {
		this.battery_timestamp = battery_timestamp;
	}

	public String getEntryFloor() {
		return entry_floor;
	}

	public void setEntryFloor(String entry) {
		this.entry_floor = entry;
	}

	public String getServerid() {
		return serverid;
	}

	public void setServerid(String serverid) {
		this.serverid = serverid;
	}

	public String getState() {  		
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(long lastSeen) {
		this.lastSeen = lastSeen;
	}
	
	public String getReciveralias() {
		return reciveralias;
	}

	public void setReciveralias(String reciveralias) {
		this.reciveralias = reciveralias;
	}

	public String getAddrtype() {
		return addrtype;
	}

	public void setAddrtype(String addrtype) {
		this.addrtype = addrtype;
	}

	public String getEntry_loc() {
		return entry_loc;
	}

	public void setEntry_loc(String entry_loc) {
		this.entry_loc = entry_loc;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public long getLastactive() {
		return lastactive;
	}

	public void setLastactive(long lastactive) {
		this.lastactive = lastactive;
	}
	
	
	public String getMailsent() {
		return mailsent;
	}

	public void setMailsent(String mailsent) {
		this.mailsent = mailsent;
	}

	public String getEntry_floor() {
		return entry_floor;
	}
	public void setEntry_floor(String entry_floor) {
		this.entry_floor = entry_floor;
	}

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
	}

	public String getTagmodel() {
		return tagmodel;
	}

	public void setTagmodel(String tagmodel) {
		this.tagmodel = tagmodel;
	}

	public String getReftxpwr() {
		return reftxpwr;
	}

	public void setReftxpwr(String reftxpwr) {
		this.reftxpwr = reftxpwr;
	}

	public String getLastReportingTime() {
		return lastReportingTime;
	}

	public void setLastReportingTime(String lastReportingTime) {
		this.lastReportingTime = lastReportingTime;
	}
	
	public String getTagModel() {
		return tagmodel;
	}	
	public void setTagModel(String tagmodel) {
		this.tagmodel = tagmodel;
	}
	
	public String getRefTxPwr() {
		return reftxpwr;
	}	
	
	public void setRefTxPwr(String reftxpwr) {
		this.reftxpwr = reftxpwr;
	}
	
	public String getLocalInactivityMailSent() {
		return localInactivityMailSent;
	}

	public void setLocalInactivityMailSent(String localInactivityMailSent) {
		this.localInactivityMailSent = localInactivityMailSent;
	}

	public String getAssginedLocationLastSeen() {
		return assginedLocationLastSeen;
	}

	public void setAssginedLocationLastSeen(String assginedLocationLastSeen) {
		this.assginedLocationLastSeen = assginedLocationLastSeen;
	}

	
	public String getExitTime() {
		return exitTime;
	}

	public void setExitTime(String exitTime) {
		this.exitTime = exitTime;
	}

	public Date getRecordSent() {
		return recordSent;
	}

	public void setRecordSent(Date recordSent) {
		this.recordSent = recordSent;
	}

	public Date getRecordSeen() {
		return recordSeen;
	}

	public void setRecordSeen(Date recordSeen) {
		this.recordSeen = recordSeen;
	}

	public Date getRecordUpdate() {
		return recordUpdate;
	}

	public void setRecordUpdate(Date recordUpdate) {
		this.recordUpdate = recordUpdate;
	}

	public double getAvgUpdateTime() {
		return avgUpdateTime;
	}

	public void setAvgUpdateTime(double avgUpdateTime) {
		this.avgUpdateTime = avgUpdateTime;
	}

	public double getAvgProcessTime() {
		return avgProcessTime;
	}

	public void setAvgProcessTime(double avgProcessTime) {
		this.avgProcessTime = avgProcessTime;
	}

	public double getAvgReceiveTime() {
		return avgReceiveTime;
	}

	public void setAvgReceiveTime(double avgReceiveTime) {
		this.avgReceiveTime = avgReceiveTime;
	}

	@Override
	public String toString() {
		return "Beacon [id=" + id + ", macaddr=" + macaddr + ", scannerUid=" + scannerUid + ", assignedTo=" + assignedTo
				+ ", minor=" + minor + ", major=" + major + ", cid=" + cid + ", sid=" + sid + ", spid=" + spid
				+ ", uuid=" + uuid + ", battery_level=" + battery_level + ", device_name=" + device_name
				+ ", reciverId=" + reciverId + ", location=" + location + ", rssi=" + rssi + ", distance=" + distance
				+ ", rxbytes=" + rxbytes + ", updatedstatus=" + updatedstatus + ", visit_id=" + visit_id + ", tag_type="
				+ tag_type + ", template=" + template + ", txpower=" + txpower + ", interval=" + interval
				+ ", accuracy=" + accuracy + ", range=" + range + ", lat=" + lat + ", lon=" + lon + ", x=" + x + ", y="
				+ y + ", reciverinfo=" + reciverinfo + ", battery_timestamp=" + battery_timestamp + ", entry_floor="
				+ entry_floor + ", entry_loc=" + entry_loc + ", serverid=" + serverid + ", state=" + state
				+ ", lastSeen=" + lastSeen + ", reciveralias=" + reciveralias + ", addrtype=" + addrtype + ", width="
				+ width + ", height=" + height + ", lastactive=" + lastactive + ", mailsent=" + mailsent + ", debug="
				+ debug + ", lastReportingTime=" + lastReportingTime + ", tagmodel=" + tagmodel + ", reftxpwr="
				+ reftxpwr + ", localInactivityMailSent=" + localInactivityMailSent + ", assginedLocationLastSeen="
				+ assginedLocationLastSeen + ", exitTime=" + exitTime + ", recordSent=" + recordSent + ", recordSeen="
				+ recordSeen + ", recordUpdate=" + recordUpdate + ", avgUpdateTime=" + avgUpdateTime
				+ ", avgProcessTime=" + avgProcessTime + ", avgReceiveTime=" + avgReceiveTime + "]";
	}
}
