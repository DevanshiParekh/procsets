package com.semaifour.facesix.data.elasticsearch.device;

import java.io.Serializable;
import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;

public class ClientDevice extends FSObject implements Serializable, Comparable<ClientDevice> {
	
	@Id
	public String 	id;
	public String	peermac;
	private String   mac;
	public String 	devname;
	public String uuid;

	/*
	 *  VAP details
	 * 
	 */
	
	public String	ssid;
	public String 	radio_type;
	public String 	vap_mac;
	
	/*
	 *  Client Details
	 */
	
	
	private long		prev_peer_tx_bytes;
	private long		prev_peer_rx_bytes;
	
	private long		cur_peer_tx_bytes;
	private long		cur_peer_rx_bytes;

	private String   peer_rssi;
	private String   peer_retry;
	private String   peer_tx_fail;
	
	private long   peer_conn_time;
	private long    lastactive;
	private String  entryTime;
	private String  exitTime;
	
	private boolean _11v;
	private boolean _11k;
	private boolean _11r;
	
	/*
	 *  ACL Properties
	 * 
	 */
	public String 	pid;
	public String 	acl;
	public String   conn;
	
	/*
	 *  Common Properties
	 */
	
	public String 	sid;
	public String 	spid;
	public String	cid;
	
	private String   state;
	private String   graylogtime;
	private String    peer_ip;
	private String    peer_hostname;
	private String    wlan_type;
	private String   peer_bw;
	private int      no_of_streams;
	private String   peer_caps_client;
	private int      peer_signal_strength;
	
	public ClientDevice() {
		super();
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPeermac() {
		return peermac;
	}

	public void setPeermac(String peermac) {
		this.peermac = peermac;
	}

	public String getDevname() {
		return devname;
	}

	public void setDevname(String devname) {
		this.devname = devname;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getRadio_type() {
		return radio_type;
	}

	public void setRadio_type(String radio_type) {
		this.radio_type = radio_type;
	}

	public String getVap_mac() {
		return vap_mac;
	}

	public void setVap_mac(String vap_mac) {
		this.vap_mac = vap_mac;
	}


	public String getPeer_rssi() {
		return peer_rssi;
	}

	public void setPeer_rssi(String peer_rssi) {
		this.peer_rssi = peer_rssi;
	}

	public String getPeer_retry() {
		return peer_retry;
	}

	public void setPeer_retry(String peer_retry) {
		this.peer_retry = peer_retry;
	}

	public String getPeer_tx_fail() {
		return peer_tx_fail;
	}

	public void setPeer_tx_fail(String peer_tx_fail) {
		this.peer_tx_fail = peer_tx_fail;
	}

	public long getPeer_conn_time() {
		return peer_conn_time;
	}

	public void setPeer_conn_time(long peer_conn_time) {
		this.peer_conn_time = peer_conn_time;
	}

	public boolean is_11v() {
		return _11v;
	}

	public void set_11v(boolean _11v) {
		this._11v = _11v;
	}

	public boolean is_11k() {
		return _11k;
	}

	public void set_11k(boolean _11k) {
		this._11k = _11k;
	}

	public boolean is_11r() {
		return _11r;
	}

	public void set_11r(boolean _11r) {
		this._11r = _11r;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}	

	public String getAcl() {
		return acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
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

	public String getConn() {
		return conn;
	}

	public void setConn(String conn) {
		this.conn = conn;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public long getPrev_peer_tx_bytes() {
		return prev_peer_tx_bytes;
	}
	public void setPrev_peer_tx_bytes(long prev_peer_tx_bytes) {
		this.prev_peer_tx_bytes = prev_peer_tx_bytes;
	}
	public long getPrev_peer_rx_bytes() {
		return prev_peer_rx_bytes;
	}
	public void setPrev_peer_rx_bytes(long prev_peer_rx_bytes) {
		this.prev_peer_rx_bytes = prev_peer_rx_bytes;
	}
	public long	 getCur_peer_tx_bytes() {
		return cur_peer_tx_bytes;
	}
	public void setCur_peer_tx_bytes(long	 cur_peer_tx_bytes) {
		this.cur_peer_tx_bytes = cur_peer_tx_bytes;
	}
	public long	 getCur_peer_rx_bytes() {
		return cur_peer_rx_bytes;
	}
	public void setCur_peer_rx_bytes(long	 cur_peer_rx_bytes) {
		this.cur_peer_rx_bytes = cur_peer_rx_bytes;
	}
	public long getLastactive() {
		return lastactive;
	}
	public void setLastactive(long lastactive) {
		this.lastactive = lastactive;
	}
	public String getExitTime() {
		return exitTime;
	}
	public void setExitTime(String exitTime) {
		this.exitTime = exitTime;
	}
	public String getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}
	
	public String getGraylogtime() {
		return graylogtime;
	}
	public void setGraylogtime(String graylogtime) {
		this.graylogtime = graylogtime;
	}
	
	public String getPeer_ip() {
		return peer_ip;
	}
	public void setPeer_ip(String peer_ip) {
		this.peer_ip = peer_ip;
	}
	public String getPeer_hostname() {
		return peer_hostname;
	}
	public void setPeer_hostname(String peer_hostname) {
		this.peer_hostname = peer_hostname;
	}
	public String getWlan_type() {
		return wlan_type;
	}
	public void setWlan_type(String wlan_type) {
		this.wlan_type = wlan_type;
	}
	public String getPeer_bw() {
		return peer_bw;
	}
	public void setPeer_bw(String peer_bw) {
		this.peer_bw = peer_bw;
	}
	public int getNo_of_streams() {
		return no_of_streams;
	}
	public void setNo_of_streams(int no_of_streams) {
		this.no_of_streams = no_of_streams;
	}
	public String getPeer_caps_client() {
		return peer_caps_client;
	}
	public void setPeer_caps_client(String peer_caps_client) {
		this.peer_caps_client = peer_caps_client;
	}
	public int getPeer_signal_strength() {
		return peer_signal_strength;
	}
	public void setPeer_signal_strength(int peer_signal_strength) {
		this.peer_signal_strength = peer_signal_strength;
	}
	
	@Override
	public String toString() {
		return "ClientDevice [id=" + id + ", peermac=" + peermac + ", mac=" + mac + ", devname=" + devname + 
				" uuid=" + uuid + ", ssid=" + ssid + ", radio_type=" + radio_type + ", vap_mac=" + vap_mac
				+ ", prev_peer_tx_bytes=" + prev_peer_tx_bytes + ", prev_peer_rx_bytes=" + prev_peer_rx_bytes
				+ ", cur_peer_tx_bytes=" + cur_peer_tx_bytes + ", cur_peer_rx_bytes=" + cur_peer_rx_bytes
				+ ", peer_rssi=" + peer_rssi + ", peer_retry=" + peer_retry + ", peer_tx_fail=" + peer_tx_fail
				+ ", peer_conn_time=" + peer_conn_time + ", lastactive=" + lastactive + ", entryTime=" + entryTime
				+ ", exitTime=" + exitTime + ", _11v=" + _11v + ", _11k=" + _11k + ", _11r=" + _11r + ", pid=" + pid
				+ ", acl=" + acl + ", conn=" + conn + ", sid=" + sid + ", spid=" + spid + ", cid=" + cid + ", state="
				+ state + ", graylogtime=" + graylogtime + ", peer_ip=" + peer_ip
				+ ", peer_hostname=" + peer_hostname + ", wlan_type=" + wlan_type + ", peer_bw=" + peer_bw
				+ ", no_of_streams=" + no_of_streams + ", peer_caps_client=" + peer_caps_client
				+ ", peer_signal_strength=" + peer_signal_strength + "]";
	}
	@Override
	public int compareTo(ClientDevice arg0) {
		int str = pid.compareTo(arg0.pid);
		return str;
	}
	
}
