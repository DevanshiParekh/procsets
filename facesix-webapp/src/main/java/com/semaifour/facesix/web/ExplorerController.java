package com.semaifour.facesix.web;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.semaifour.facesix.data.entity.elasticsearch.Entity;
import com.semaifour.facesix.data.entity.elasticsearch.EntityService;

/**
 * 
 * ExplorerController Controller for the webapp
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/web/explorer")
public class ExplorerController extends WebController{

	static Logger LOG = LoggerFactory.getLogger(ExplorerController.class.getName());
	
	@Autowired
	EntityService entityService;
	
	
	@RequestMapping(value={"","/"})
	public String x(Map<String, Object> model) {
		model.put("time", new Date());
		return "explorer";
	}
	
	@RequestMapping(value={"/{euid}", "/{euid}/"})
	public String x(Map<String, Object> model, @PathVariable("euid") String euid) {
		Entity en = entityService.findOneByUid(euid);
		
		if (en != null) {
			String fsql = en.getSettings().getProperty("fsql");
			List<Map<String, Object>> list = _CCC.fsqlRestController.query(fsql);
			model.put("fsobjects", list);
		}
		
		return "explorer";
	}	
}