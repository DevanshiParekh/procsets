package com.semaifour.facesix.session;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.semaifour.facesix.util.SimpleCacheManager;

@Service
public class SessionCache {
	
	private static final long SessionTTL = 4 * 60 * 60 * 1000;

	public SessionCache() {
	}
	
	public Object setAttribute(HttpSession session, String attr, Object value) {
		String id = session.getId();
		Map<String, Object> map = (Map<String, Object>) SimpleCacheManager.getInstance().get(id);
		if (map == null) {
			map = new HashMap<String, Object>();
			SimpleCacheManager.getInstance().put(id, map, SessionTTL);
		}
		return map.put(attr, value);
	}
	
	public Object getAttribute(HttpSession session, String attr) {
		String id = session.getId();
		Map<String, Object> map = (Map<String, Object>) SimpleCacheManager.getInstance().get(id);
		if (map != null) {
			return map.get(attr);
		} else {
			return null;
		}
	}
	
	public String getStringAttribute(HttpSession session, String attr) {
		return String.valueOf(getAttribute(session, attr));
	}
	
	public boolean exists(HttpSession session, String attr) {
		return getAttribute(session, attr) != null;
	}
	
	
	public boolean equals(HttpSession session, String attr, Object value) {
		Object v = getAttribute(session, attr); 
		return  v != null && v.equals(value);
	}
	
	
	public Object clearAttribute(HttpSession session, String attr) {
		String id = session.getId();
		Map<String, Object> map = (Map<String, Object>) SimpleCacheManager.getInstance().get(id);
		if (map != null) {
			return map.remove(attr);
		} else {
			return null;
		}
	}
	
	public void clearAll(HttpSession session) {
		String id = session.getId();
		SimpleCacheManager.getInstance().clear(id);
	}

	public void clearAttribute(HttpSession session, String... attrs) {
		String id = session.getId();
		Map<String, Object> map = (Map<String, Object>) SimpleCacheManager.getInstance().get(id);
		if (map != null) {
			for (String attr : attrs) {
				map.remove(attr);
			}
		}
	}

}
