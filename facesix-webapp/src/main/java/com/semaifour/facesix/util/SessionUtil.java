package com.semaifour.facesix.util;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Http Session Util methods
 * 
 * @author mjs
 *
 */
public class SessionUtil {

	public static boolean isAuthorized(HttpSession session) {
		return session.getAttribute("user") != null;
	}

	public static void authorizeSession(HttpSession session, String user) {
		session.setAttribute("user", user);
	}
	
	public static String currentUser(HttpSession session) {
		return String.valueOf(session.getAttribute("user"));
	}
	
	public static void setCurrentCustomer(HttpSession session, String cid) {
		session.setAttribute("cid", cid);
	}
	
	public static void clearCurrentCustomer(HttpSession session) {
		session.removeAttribute("cid");
	}
	
	public static String getCurrentCustomer(HttpSession session) {
		return String.valueOf(session.getAttribute("cid"));
	}
	
	public static boolean isCurrentCustomer(HttpSession session, String cid) {
		return StringUtils.equalsIgnoreCase(getCurrentCustomer(session), cid);
	}
	
	public static void setCurrentSite(HttpSession session, String sid) {
		session.setAttribute("sid", sid);
	}
	
	public static void clearCurrentSite(HttpSession session) {
		session.removeAttribute("sid");
	}
	
	public static String getCurrentSite(HttpSession session) {
		return String.valueOf(session.getAttribute("sid"));
	}
	
	public static boolean isCurrentSite(HttpSession session, String sid) {
		return StringUtils.equalsIgnoreCase(getCurrentSite(session), sid);
	}
	
	public static void setCurrentSitePortion(HttpSession session, String spid) {
		session.setAttribute("spid", spid);
	}
	
	public static String getCurrentSitePortion(HttpSession session) {
		return String.valueOf(session.getAttribute("spid"));
	}
	
	public static boolean isCurrentSitePortion(HttpSession session, String spid) {
		return StringUtils.equalsIgnoreCase(getCurrentSitePortion(session), spid);
	}
	
	
	public static void setCurrentSiteCustomerId(HttpSession session, String cid) {
		session.setAttribute("cid", cid);
	}
	
	public static void clearCurrentSiteCustomerId(HttpSession session) {
		session.removeAttribute("cid");
	}

	public static String getCurrentSiteCustomerId(HttpSession session) {
		return String.valueOf(session.getAttribute("cid"));
	}

	public static boolean isCurrentSiteCustomerId(HttpSession session, String cid) {
		return StringUtils.equalsIgnoreCase(getCurrentSiteCustomerId(session), cid);
	}
	
	public static void setCurrentPortionCustomerId(HttpSession session, String cid) {
		session.setAttribute("cid", cid);
	}
	
	public static String getCurrentPortionCustomerId(HttpSession session) {
		return String.valueOf(session.getAttribute("cid"));
	}
}

class SessionFSO implements Serializable {
	
	private String id;
	private String name;
	
	public SessionFSO(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
}

class SSite extends SessionFSO {
	public SSite(String id, String name) {
		super(id, name);
	}
}

class SSitePortion extends SessionFSO {
	public SSitePortion(String id, String name) {
		super(id, name);
	}
}
