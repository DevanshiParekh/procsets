package com.semaifour.facesix.data.elasticsearch.device;

import java.io.Serializable;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.semaifour.facesix.domain.FSObject;

@Document(indexName = "facesix-int", type = "networkdevice", shards = 2, replicas = 1, refreshInterval = "-1")
public class NetworkDevice extends FSObject implements Serializable, Comparable<NetworkDevice> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger LOG = LoggerFactory.getLogger(NetworkDeviceService.class.getName());
	
	@Id
	public String 	id;
	
	public String 	parent;
	public String 	sid;
	public String 	spid;
	public String 	cid;
	public String 	svid;
	public String 	swid;	
	public String 	xposition;
	public String 	yposition;
	public String 	gparent;
	public String 	uuid;
	public String	band2g;
	public String	band5g;
	public String	guest;
	public String	vapcount;
	public String	alias;
	public String	pssid;
	public String   flrTx;
	public String   flrRx;
	public String   ch2g;
	public String   ch5g;
	public String   tagstring;	
	public int		activetag;
	public int		activecli;
	public String	bleType;
	public int		checkedintag;
	public int		checkedoutTag;
	public int		exitTag;
	public String	role;
	public String	persontype;
	private String  binaryreason;
	private String  upgradeType;
	private String  binaryType;
	public int 		probeAndroid;
	public int 		probeIos;
	public int 		probeWindows;
	public int 		proberouter;
	public int 		probeprinter;
	public int 		probespeaker;
	public int 		probeOthers;
	public int 		probePeercount;
	public int 		probeAssocate;
	public long		assocate2G5GCount;
	public long		_2GCount;
	public long		_5GCount;
	public String   connectedClientsList;
	public JSONObject steerClientList;
	public String  statconnectedClientsList;
				
	public NetworkDevice() {		
		super();
		tagstring = "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return super.getUid();
	}
	
	public void setUid(String uid) {
		super.setUid(uid);
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}	
	

	public void setStatus(String status) {
		super.setStatus(status);
	}
	
	public String getStatus() {
		return super.getStatus();
	}

	public void setTypefs(String type) {
		super.setTypefs(type);
	}	
	
	public String getTypefs() {
		return super.getTypefs();
	}		

	public String getGParent() {
		return gparent;
	}

	public void setGParent(String gparent) {
		this.gparent = gparent;
	}
	
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
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
		
	public String getSvid() {
		return svid;
	}

	public void setSvid(String svid) {
		this.svid = svid;
	}
	
	public String getSwid() {
		return swid;
	}

	public void setSwid(String swid) {
		this.swid = swid;
	}

	public String getXposition() {
		return xposition;
	}

	public void setXposition(String xposition) {
		this.xposition = xposition;
	}

	public String getYposition() {
		return yposition;
	}

	public void setYposition(String yposition) {
		this.yposition = yposition;
	}
	
	public String getBand2g() {
		return band2g;
	}

	public void setBand2g(String band2g) {
		this.band2g = band2g;
	}
	
	public String getBand5g() {
		return band5g;
	}

	public void setBand5g(String band5g) {
		this.band5g = band5g;
	}
	
	public String getGuest() {
		return guest;
	}

	public void setGuest(String guest) {
		this.guest = guest;
	}	
	
	public String getVapcount() {
		return vapcount;
	}

	public void setVapcount(String vapcount) {
		this.vapcount = vapcount;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getPssid() {
		return pssid;
	}

	public void setPssid(String pssid) {
		this.pssid = pssid;
	}	

	public String getFloorTx() {
		return flrTx;
	}

	public void setFloorTx(String flrTx) {
		this.flrTx = flrTx;
	}	
	
	public String getFloorRx() {
		return flrRx;
	}

	public void setFloorRx(String flrRx) {
		this.flrRx = flrRx;
	}
	
	public String getChan2G() {
		return ch2g;
	}

	public void setChan2G(String ch2g) {
		this.ch2g = ch2g;
	}	
	
	public String getChan5G() {
		return ch5g;
	}

	public void setChan5G(String ch5g) {
		this.ch5g = ch5g;
	}
	
	
	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getFlrTx() {
		return flrTx;
	}

	public void setFlrTx(String flrTx) {
		this.flrTx = flrTx;
	}

	public String getFlrRx() {
		return flrRx;
	}

	public void setFlrRx(String flrRx) {
		this.flrRx = flrRx;
	}

	public String getCh2g() {
		return ch2g;
	}

	public void setCh2g(String ch2g) {
		this.ch2g = ch2g;
	}

	public String getCh5g() {
		return ch5g;
	}

	public void setCh5g(String ch5g) {
		this.ch5g = ch5g;
	}
	
	public int getActivetag() {
		return activetag;
	}
	
	public void setActivetag(int tag) {
		this.activetag = tag;
	}
	
	public int getActiveClient() {
		return activecli;
	}
	
	public void setActiveClient(int cli) {
		this.activecli = cli;
	}

	public String getTagString() {
		return tagstring;
	}

	public void setTagString(String tagstring) {
		this.tagstring = tagstring;
	}

	public String getBleType() {
		return bleType;
	}

	public void setBleType(String bleType) {
		this.bleType = bleType;
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
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}		

	public String getPersontype() {
		return persontype;
	}

	public void setPersontype(String persontype) {
		this.persontype = persontype;
	}

	public String getBinaryreason() {
		return binaryreason;
	}

	public void setBinaryreason(String binaryreason) {
		this.binaryreason = binaryreason;
	}

	public String getUpgradeType() {
		return upgradeType;
	}

	public void setUpgradeType(String upgradeType) {
		this.upgradeType = upgradeType;
	}

	public String getBinaryType() {
		return binaryType;
	}

	public void setBinaryType(String binaryType) {
		this.binaryType = binaryType;
	}
	public int getProbeAndroid() {
		return probeAndroid;
	}

	public void setProbeAndroid(int probeAndroid) {
		this.probeAndroid = probeAndroid;
	}

	public int getProbeIos() {
		return probeIos;
	}

	public void setProbeIos(int probeIos) {
		this.probeIos = probeIos;
	}

	public int getProbeWindows() {
		return probeWindows;
	}

	public void setProbeWindows(int probeWindows) {
		this.probeWindows = probeWindows;
	}

	public int getProbeOthers() {
		return probeOthers;
	}

	public void setProbeOthers(int probeOthers) {
		this.probeOthers = probeOthers;
	}

	public int getProbePeercount() {
		return probePeercount;
	}

	public void setProbePeercount(int probePeercount) {
		this.probePeercount = probePeercount;
	}

	public int getProbeAssocate() {
		return probeAssocate;
	}
	public void setProbeAssocate(int probeAssocate) {
		this.probeAssocate = probeAssocate;
	}
	
	public int getProberouter() {
		return proberouter;
	}

	public void setProberouter(int proberouter) {
		this.proberouter = proberouter;
	}

	public int getProbeprinter() {
		return probeprinter;
	}

	public void setProbeprinter(int probeprinter) {
		this.probeprinter = probeprinter;
	}

	public int getProbespeaker() {
		return probespeaker;
	}

	public void setProbespeaker(int probespeaker) {
		this.probespeaker = probespeaker;
	}

	public String getConnectedClientsList() {
		return connectedClientsList;
	}

	public void setConnectedClientsList(String connectedClientsList) {
		this.connectedClientsList = connectedClientsList;
	}

	public long getAssocate2G5GCount() {
		return assocate2G5GCount;
	}

	public void setAssocate2G5GCount(long assocate2g5gCount) {
		assocate2G5GCount = assocate2g5gCount;
	}

	public long get_2GCount() {
		return _2GCount;
	}

	public void set_2GCount(long _2gCount) {
		_2GCount = _2gCount;
	}

	public long get_5GCount() {
		return _5GCount;
	}

	public void set_5GCount(long _5gCount) {
		_5GCount = _5gCount;
	}

	
	public JSONObject getSteerClientList() {
		return steerClientList;
	}

	public void setSteerClientList(JSONObject steerClientList) {
		this.steerClientList = steerClientList;
	}

	public String getStatconnectedClientsList() {
		return statconnectedClientsList;
	}

	public void setStatconnectedClientsList(String statconnectedClientsList) {
		this.statconnectedClientsList = statconnectedClientsList;
	}

	public void update(NetworkDevice newfso) {
		super.update(newfso);
		this.parent 	= newfso.getParent();
		this.gparent 	= newfso.getGParent();
		this.sid 		= newfso.getSid();
		this.spid 		= newfso.getSpid();
		this.svid 		= newfso.getSvid();
		this.swid 		= newfso.getSwid();
		this.uuid 		= newfso.getUuid();
		this.xposition 	= newfso.getXposition();
		this.yposition 	= newfso.getYposition();
		
		this.band2g 	= newfso.getBand2g();
		this.band5g 	= newfso.getBand5g();
		this.guest 		= newfso.getGuest();
		this.vapcount 	= newfso.getVapcount();
		this.alias 		= newfso.getAlias();
		this.pssid 		= newfso.getPssid();
		this.ch2g		= newfso.getChan2G();
		this.ch5g		= newfso.getChan5G();
		this.cid		= newfso.getCid();
		this.bleType	= newfso.getBleType();
	}


	@Override
	public String toString() {
		return "NetworkDevice [id=" + id + ", parent=" + parent + ", sid=" + sid + ", spid=" + spid + ", cid=" + cid
				+ ", svid=" + svid + ", swid=" + swid + ", xposition=" + xposition + ", yposition=" + yposition
				+ ", gparent=" + gparent + ", uuid=" + uuid + ", band2g=" + band2g + ", band5g=" + band5g + ", guest="
				+ guest + ", vapcount=" + vapcount + ", alias=" + alias + ", pssid=" + pssid + ", flrTx=" + flrTx
				+ ", flrRx=" + flrRx + ", ch2g=" + ch2g + ", ch5g=" + ch5g + ", tagstring=" + tagstring + ", activetag="
				+ activetag + ", activecli=" + activecli + ", bleType=" + bleType + ", checkedintag=" + checkedintag
				+ ", checkedoutTag=" + checkedoutTag + ", exitTag=" + exitTag + ", role=" + role + ", persontype="
				+ persontype + ", binaryreason=" + binaryreason + ", upgradeType=" + upgradeType + ", binaryType="
				+ binaryType + ", probeAndroid=" + probeAndroid + ", probeIos=" + probeIos + ", probeWindows="
				+ probeWindows + ", proberouter=" + proberouter + ", probeprinter=" + probeprinter + ", probespeaker="
				+ probespeaker + ", probeOthers=" + probeOthers + ", probePeercount=" + probePeercount
				+ ", probeAssocate=" + probeAssocate + ", assocate2G5GCount=" + assocate2G5GCount + ", _2GCount="
				+ _2GCount + ", _5GCount=" + _5GCount + ", connectedClientsList=" + connectedClientsList
				+ ", steerClientList=" + steerClientList + ", statconnectedClientsList=" + statconnectedClientsList
				+ "]";
	}

	@Override
	public int compareTo(NetworkDevice arg0) {
		int str = spid.compareTo(arg0.spid);
		return str;
	}

	
}
