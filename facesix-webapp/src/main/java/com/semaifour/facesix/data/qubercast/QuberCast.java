package com.semaifour.facesix.data.qubercast;
import java.util.Date;

import org.springframework.data.annotation.Id;
import com.semaifour.facesix.domain.FSObject;

public class QuberCast extends FSObject {

	@Id
	private String id;
	private String reffId;
	private String mediaPath;
	private String multicastPort;
	private String mulicastAddress;
	private String logFile; //totalFile
	private String logLevel; //payLoad

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReffId() {
		return reffId;
	}

	public void setReffId(String reffId) {
		this.reffId = reffId;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	public String getMulticastPort() {
		return multicastPort;
	}

	public void setMulticastPort(String multicastPort) {
		this.multicastPort = multicastPort;
	}

	public String getMulicastAddress() {
		return mulicastAddress;
	}

	public void setMulicastAddress(String mulicastAddress) {
		this.mulicastAddress = mulicastAddress;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public void update(QuberCast newfso) {
		super.update(newfso);
		this.reffId 			= newfso.getReffId();
		this.mediaPath 			= newfso.getMediaPath();
		this.multicastPort 		= newfso.getMulticastPort();
		this.mulicastAddress 	= newfso.getMulicastAddress();
		this.logFile 			= newfso.getLogFile();
		this.logLevel 			= newfso.getLogLevel();
	}

	@Override
	public String toString() {
		return " QuberCast [id=" + id + ", mediaPath=" + mediaPath + ", multicastPort=" + multicastPort
				+ ", mulicastAddress=" + mulicastAddress + ", logFile=" + logFile + ", logLevel=" + logLevel
				+ " reffId=" + reffId + ",getReffId()=" + getReffId() + "]";
	}

}
