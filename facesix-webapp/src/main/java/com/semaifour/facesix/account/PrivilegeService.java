package com.semaifour.facesix.account;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.session.SessionCache;
import com.semaifour.facesix.util.SimpleCacheManager;

@Service
public class PrivilegeService {

	@Autowired
	SessionCache sessionCache;
	
		
	public boolean hasPrivilege(String sessionid, String privilege) {
		Map<String, Object> map = (Map<String, Object>) SimpleCacheManager.getInstance().get(sessionid);
		if (map == null) {
			return false;
		}
		map = (Map<String, Object>) map.get("privs");
		Boolean priv = null;
		if (map != null) {
			priv = (Boolean) map.get(privilege);
		}
		return priv != null ? priv : false;
	}

}
