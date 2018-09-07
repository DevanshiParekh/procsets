package com.semaifour.facesix.data.captive.portal;
import org.springframework.data.annotation.Id;
import com.semaifour.facesix.domain.FSObject;
import net.sf.json.JSONArray;

public class CaptivePortal extends FSObject {
	
	@Id
	private String id;
	private String portalName;
	private String location;
	private String backgroundImg;
	private String logoImg;
	private String portalType;
	private String preferedUrl;
	private String portalComponents;
	private String activeSkin;
	private String portalTheme;
	private String bgScreenShot;
	private String cid;
	private String customerName;
	private String supportComponents;
	private String associationWith;
	private JSONArray associationIds;
	
	public CaptivePortal() {}

	public String getId() {
		return id;
	}

	public String getPortalName() {
		return portalName;
	}

	public String getLocation() {
		return location;
	}

	public String getCid() {
		return cid;
	}

	public String getBackgroundImg() {
		return backgroundImg;
	}

	public String getPortalType() {
		return portalType;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPortalName(String portalName) {
		this.portalName = portalName;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public void setBackgroundImg(String background) {
		this.backgroundImg = background;
	}

	public void setPortalType(String portalType) {
		this.portalType = portalType;
	}

	public String getPreferedUrl() {
		return preferedUrl;
	}

	public void setPreferedUrl(String preferedUrl) {
		this.preferedUrl = preferedUrl;
	}

	public String getPortalComponents() {
		return portalComponents;
	}

	public void setPortalComponents(String portalComponents) {
		this.portalComponents = portalComponents;
	}

	public String getActiveSkin() {
		return activeSkin;
	}

	public void setActiveSkin(String activeSkin) {
		this.activeSkin = activeSkin;
	}

	public String getPortalTheme() {
		return portalTheme;
	}

	public void setPortalTheme(String portalTheme) {
		this.portalTheme = portalTheme;
	}

	public String getBgScreenShot() {
		return bgScreenShot;
	}

	public void setBgScreenShot(String bgScreenShot) {
		this.bgScreenShot = bgScreenShot;
	}

	public String getLogoImg() {
		return logoImg;
	}

	public void setLogoImg(String logoImg) {
		this.logoImg = logoImg;
	}
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getSupportComponents() {
		return supportComponents;
	}

	public void setSupportComponents(String supportComponents) {
		this.supportComponents = supportComponents;
	}

	public String getAssociationWith() {
		return associationWith;
	}

	public void setAssociationWith(String associationWith) {
		this.associationWith = associationWith;
	}

	public JSONArray getAssociationIds() {
		return associationIds;
	}

	public void setAssociationIds(JSONArray associationIds) {
		this.associationIds = associationIds;
	}

	@Override
	public String toString() {
		return "CaptivePortal [id=" + id + ", portalName=" + portalName + ", location=" + location + ", backgroundImg="
				+ backgroundImg + ", logoImg=" + logoImg + ", portalType=" + portalType + ", preferedUrl=" + preferedUrl
				+ ", portalComponents=" + portalComponents + ", activeSkin=" + activeSkin + ", portalTheme="
				+ portalTheme + ", bgScreenShot=" + bgScreenShot + ", cid=" + cid + ", customerName=" + customerName
				+ ", supportComponents=" + supportComponents + ", associationWith=" + associationWith
				+ ", associationIds=" + associationIds + "]";
	}
}
