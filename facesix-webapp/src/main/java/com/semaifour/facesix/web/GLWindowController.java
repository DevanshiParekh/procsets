package com.semaifour.facesix.web;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * Configuration Controller for the webapp
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/web/glwindow")
public class GLWindowController extends WebController{

	static Logger LOG = LoggerFactory.getLogger(GLWindowController.class.getName());
		
	@RequestMapping("/page/**")
	public String page(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		int i = request.getRequestURI().indexOf("/page/");
		String path = request.getRequestURI().substring(i + 6);
		path = path + "?" + request.getQueryString();
		model.put("time", new Date());
		String url = _CCC.graylog.getWebUrl();
		url = url +"/" + path;
		model.put("url", url);
		model.put(path, TAB_HIGHLIGHTER);
		return "glwindow";
	}
	
}