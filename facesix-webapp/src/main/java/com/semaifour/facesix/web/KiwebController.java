package com.semaifour.facesix.web;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.semaifour.facesix.kiweb.KiwebConfiguration;

/**
 * 
 * Configuration Controller for the webapp
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/web/kiweb")
public class KiwebController extends WebController{

	static Logger LOG = LoggerFactory.getLogger(KiwebController.class.getName());
	
	@Autowired
	KiwebConfiguration kiwebConfiguration;
	
	
	@RequestMapping("/page/{path}")
	public String page(Map<String, Object> model, @PathVariable("path")String path, 
					   HttpServletRequest request, 
					   HttpServletResponse response) {
		model.put("time", new Date());
		String url = kiwebConfiguration.getWebUrl();
		url = url +"#/" + path + "?" + request.getQueryString();
		model.put("url", url);
		model.put(path, TAB_HIGHLIGHTER);
		return "kiweb";
	}
	
	@RequestMapping("/page/{path}/{dashboard}")
	public String dashboard(Map<String, Object> model,
							@PathVariable("path")String path,
							@PathVariable("dashboard")String dashboard, 
							HttpServletRequest request, 
							HttpServletResponse response) {
		model.put("time", new Date());
		String url = kiwebConfiguration.getWebUrl();
		url = url +"#/" + path + "/" + dashboard + "?" + request.getQueryString();
		model.put("url", url);
		model.put(path, TAB_HIGHLIGHTER);
		return "kiweb";
	}
	
	
}