package com.semaifour.facesix.web;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * Kibana Controller for the webapp
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/kibana")
public class KibanaWebController extends WebController {

	Logger LOG = LoggerFactory.getLogger(KibanaWebController.class.getName());

	
	@GetMapping(value ={"", "/", "index"})
	public String index(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		return "kibana";
	}

	
	@RequestMapping("/config")
	public String config(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		model.put("kibanaIndexName", "fsi-kibana-int");
		return "kibana-config-js";
	}
		
}