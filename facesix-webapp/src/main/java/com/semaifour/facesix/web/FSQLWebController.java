package com.semaifour.facesix.web;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.semaifour.facesix.data.elasticsearch.datasource.DatasourceService;
import com.semaifour.facesix.rest.FSqlRestController;

/**
 * 
 * Configuration Controller for the webapp
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/web/fsql")
public class FSQLWebController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(FSQLWebController.class.getName());
			
	@Autowired
	DatasourceService service;
	
	@Autowired
	FSqlRestController fsqlRestController;
	
	@RequestMapping("/")
	public String exe(Map<String, Object> model) {
		return exe(model, null);
	}
	
	
	/**
	 * 
	 * Lists all datasources
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/exe")
	public String exe(Map<String, Object> model, @RequestParam(value = "fsql", required=false) String fsql) {
		model.put("time", new Date());
		model.put("fsql", fsql);
		List<Map<String, Object>> result = null;
		try {
			if (fsql != null) {
				long start = System.currentTimeMillis();
				result = fsqlRestController.query(fsql);
				long end = System.currentTimeMillis();
				LOG.info("Query Executed :[" + (end-start) + "] ms, FSQL :" + fsql);
			}
		} catch(Exception e) {
			LOG.warn("FSQL failed :" + fsql, e);
		}
		
		if (result != null && result.size() > 0) {
			model.put("columns", result.get(0).keySet());
			model.put("result", result);
		}

		return "fsql-aeditor";
	}
	
}