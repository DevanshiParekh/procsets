package com.semaifour.facesix.util;

import java.net.InetAddress;

public class HostUtil {

	private static String hostname = null;

	public static String hostname() {
		if (hostname == null) {
			try {
				InetAddress addr =  InetAddress.getLocalHost();
		    	hostname = addr.getHostName();
		    	hostname = addr.getHostAddress();
			} catch (Exception e) {
				hostname = "hostunresolved";
			}
		}
		return hostname;
	}
}
