package com.semaifour.facesix.account.rest;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.session.SessionCache;
import com.semaifour.facesix.web.WebController;

@RequestMapping("/rest/privilege")
@RestController
public class PrivilegeRestController extends WebController {
	
	Logger LOG = LoggerFactory.getLogger(PrivilegeRestController.class.getName());

	@Autowired
	SessionCache sessionCache;

	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	CustomerService customerService;
	
	@RequestMapping(value = "/fetch", method = RequestMethod.GET)
	public Restponse<Map<String, Object>> fetch(HttpServletRequest request, HttpServletResponse reponse) {
		return new Restponse((Map<String, Object>)sessionCache.getAttribute(request.getSession(), "privs"));
	}
	
	@RequestMapping(value = "/has/{priv}", method = RequestMethod.GET)
	public Restponse<String> has(@PathVariable String priv, HttpServletRequest request, HttpServletResponse reponse) {
		Map<String, Object> privs = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
		if (privs != null) {
			Object privo = privs.get(priv);
			return new Restponse<String>(privo != null ? (Boolean) privo : false, 200);
		} else {
			return new Restponse<String>(false, 200, "PRIVS NOT FOUND");
		}
	}
		
}
