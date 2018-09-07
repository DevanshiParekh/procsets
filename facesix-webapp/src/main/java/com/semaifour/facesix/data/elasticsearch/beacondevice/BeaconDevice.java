package com.semaifour.facesix.data.elasticsearch.beacondevice;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "fsi-beacondevice-#{systemProperties['fs.app'] ?: 'default'}", type = "beacondevice")
public class BeaconDevice {
	
	public enum STATUS { REGISTERED, CONFIGURED, AUTOCONFIGURED }
	public enum STATE  { inactive, active, idle}
	
	@Id
	private String  id;
	private String  pkid;
	private String  uid;
	private String  uuid;
	private String  name;
	private String  fstype;
	private String  conf;
	private String  template;
	private String  createdBy;
	private String  modifiedBy;
	private Date    createdOn;
	private Date    modifiedOn;
	private String  status;
	private String  state;
	private String  others;
	public  String  sid;
	public  String  spid;
	private String  cid;
	private String  alias;
	private int	    activetag;
	private String  tagjsonstring;
	private String 	type;
	private int		checkedintag;
	private int		checkedoutTag;
	private int		exitTag;
	private String	ip;
	private String	personType;
	private String  geopoints;
	private String  georesult;
	private String  pixelresult;
	private String  coordinateresult;
	private String  version;
	private String  build;
	private String debugflag;
	private String keepAliveInterval;
	private double    tlu;
	private String lastseen;
	private String customizeInactivityMailSent;
	private String devCrashTimestamp;
	private String devCrashdumpFileName;
	private String devCrashDumpUploadStatus;
	private String tunnelIp;
	
	@Field(type= FieldType.Nested)
	private Collection<String> tags;
	
	public BeaconDevice() {
		super();
	}
	public BeaconDevice(String UID, String name, String conf,
			String version, String createdBy, String modifiedBy,
			Date createdOn, Date modifiedOn) {
		super();
		this.uid = UID;
		this.name = name;
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
	public String getPkid() {
		return pkid;
	}
	public void setPkid(String pkid) {
		this.pkid = pkid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFstype() {
		return fstype;
	}
	public void setFstype(String fstype) {
		this.fstype = fstype;
	}
	public String getConf() {
		return conf;
	}
	public void setConf(String conf) {
		this.conf = conf;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getOthers() {
		return others;
	}
	public void setOthers(String others) {
		this.others = others;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getSpid() {
		return spid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Collection<String> getTags() {
		return tags;
	}
	public void setTags(Collection<String> tags) {
		this.tags = tags;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public int getActivetag() {
		return activetag;
	}
	public void setActivetag(int tag) {
		this.activetag = tag;
	}
	
	public String getTagjsonstring() {
		return tagjsonstring;
	}
	public void setTagjsonstring(String tagjsonstring) {
		this.tagjsonstring = tagjsonstring;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public int getCheckedintag() {
		return checkedintag;
	}

	public void setCheckedintag(int checkedintag) {
		this.checkedintag = checkedintag;
	}

	public int getCheckedoutTag() {
		return checkedoutTag;
	}

	public void setCheckedoutTag(int checkedoutTag) {
		this.checkedoutTag = checkedoutTag;
	}

	public int getExitTag() {
		return exitTag;
	}

	public void setExitTag(int exitTag) {
		this.exitTag = exitTag;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPersonType() {
		return personType;
	}
	public void setPersonType(String personType) {
		this.personType = personType;
	}
	public String getGeopoints() {
		return geopoints;
	}
	public void setGeopoints(String geopoints) {
		this.geopoints = geopoints;
	}
	public String getGeoresult() {
		return georesult;
	}
	public void setGeoresult(String georesult) {
		this.georesult = georesult;
	}
	public String getPixelresult() {
		return pixelresult;
	}
	public void setPixelresult(String pixelresult) {
		this.pixelresult = pixelresult;
	}
	public String getCoordinateresult() {
		return coordinateresult;
	}
	public void setCoordinateresult(String coordinateresult) {
		this.coordinateresult = coordinateresult;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getBuild() {
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	
	public String getDebugflag() {
		return debugflag;
	}
	public void setDebugflag(String debugflag) {
		this.debugflag = debugflag;
	}
	
	public String getKeepAliveInterval() {
		return keepAliveInterval;
	}
	public void setKeepAliveInterval(String keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}
	
	public double getTlu() {
		return tlu;
	}
	public void setTlu(double tlu) {
		this.tlu = tlu;
	}
	
	public String getLastseen() {
		return lastseen;
	}
	public void setLastseen(String lastseen) {
		this.lastseen = lastseen;
	}
	public String getCustomizeInactivityMailSent() {
		return customizeInactivityMailSent;
	}
	public void setCustomizeInactivityMailSent(String customizeInactivityMailSent) {
		this.customizeInactivityMailSent = customizeInactivityMailSent;
	}
	public String getDevCrashTimestamp() {
		return devCrashTimestamp;
	}
	public void setDevCrashTimestamp(String devCrashTimestamp) {
		this.devCrashTimestamp = devCrashTimestamp;
	}
	public String getDevCrashdumpFileName() {
		return devCrashdumpFileName;
	}
	public void setDevCrashdumpFileName(String devCrashdumpFileName) {
		this.devCrashdumpFileName = devCrashdumpFileName;
	}
	public String getDevCrashDumpUploadStatus() {
		return devCrashDumpUploadStatus;
	}
	public void setDevCrashDumpUploadStatus(String devCrashDumpUploadStatus) {
		this.devCrashDumpUploadStatus = devCrashDumpUploadStatus;
	}

	public String getTunnelIp() {
		return tunnelIp;
	}
	public void setTunnelIp(String tunnelip) {
		this.tunnelIp = tunnelip;
	}

	@Override
	public String toString() {
		return "BeaconDevice [id=" + id + ", pkid=" + pkid + ", uid=" + uid + ", uuid=" + uuid + ", name=" + name
				+ ", fstype=" + fstype + ", conf=" + conf + ", template=" + template + ", createdBy=" + createdBy
				+ ", modifiedBy=" + modifiedBy + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", status="
				+ status + ", state=" + state + ", others=" + others + ", sid=" + sid + ", spid=" + spid + ", cid="
				+ cid + ", alias=" + alias + ", activetag=" + activetag + ", tagjsonstring=" + tagjsonstring + ", type="
				+ type + ", checkedintag=" + checkedintag + ", checkedoutTag=" + checkedoutTag + ", exitTag=" + exitTag
				+ ", ip=" + ip + ", personType=" + personType + ", geopoints=" + geopoints + ", georesult=" + georesult
				+ ", pixelresult=" + pixelresult + ", coordinateresult=" + coordinateresult + ", version=" + version
				+ ", build=" + build + ", debugflag=" + debugflag + ", keepAliveInterval=" + keepAliveInterval
				+ ", tlu=" + tlu + ", lastseen=" + lastseen + ", customizeInactivityMailSent="
				+ customizeInactivityMailSent + ", devCrashTimestamp=" + devCrashTimestamp + ", devCrashdumpFileName="
				+ devCrashdumpFileName + ", devCrashDumpUploadStatus=" + devCrashDumpUploadStatus + ", tunnelIp="
				+ tunnelIp + ", tags=" + tags + "]";
	}
}
