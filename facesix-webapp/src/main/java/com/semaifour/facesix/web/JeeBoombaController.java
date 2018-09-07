package com.semaifour.facesix.web;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.semaifour.facesix.util.SessionUtil;

/**
 * 
 * Welcome Controller for the webapp
 * 
 * @author mjs
 *
 */
@Controller
public class JeeBoombaController extends WebController {
	

	static Logger LOG = LoggerFactory.getLogger(JeeBoombaController.class.getName());
			
	@RequestMapping(value = {"/thiranthidu"})
	public String thirathidu(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		if (!SessionUtil.isAuthorized(request.getSession()) && _CCC.properties.getProperty("facesix.devonly.autoauth") != null) {
			SessionUtil.authorizeSession(request.getSession(), _CCC.properties.getProperty("facesix.devonly.autoauth"));
		}
		return _CCC.pages.getPage("facesix.home", "home");
	}
}