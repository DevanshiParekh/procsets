package com.semaifour.facesix.rest;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.data.entity.elasticsearch.Entity;
import com.semaifour.facesix.data.entity.elasticsearch.EntityService;
import com.semaifour.facesix.web.WebController;

@RestController
@RequestMapping("/rest/entity")
public class EntityRestController extends WebController{
	
	static Logger LOG = LoggerFactory.getLogger(EntityRestController.class.getName());

	@Autowired
	EntityService entityService;
	
	@Autowired
	FSqlRestController fsqlRestController;
	

	@RequestMapping("/data/{euid}")
	public @ResponseBody Map<String, Object> data(@PathVariable("euid")String euid) {
		Entity en = entityService.findOneByUid(euid);
		if (en != null) {
			return fsqlRestController.query4datatable(en.getSettings().getProperty("fsql"));
		}
		return null;
	}
	
	/**
	 * 
	 * Returns fsql query result for the given entity. Query string 'qs' is applied on to fsql.
	 * 
	 * @param euid UID of the entity
	 * @param querystring  Query string to be applied to fsql of the entity.
	 * 
	 * @return list of data objects for the given entity
	 */
	@RequestMapping("/query/{euid}")
	public @ResponseBody List<Map<String, Object>> query(@PathVariable("euid")String euid, 
														 @RequestParam(value="qs", required=false, defaultValue="*") String querystring) {
		Entity en = entityService.findOneByUid(euid);
		if (en != null) {
			if (StringUtils.isEmpty(querystring)) querystring = "*";
			querystring = (en.getSettings().getProperty("fsql")).replaceAll("_QUERY_PARAMS_", querystring);
			LOG.info("query.fsql :" + querystring);
			return fsqlRestController.query(querystring);
		}
		return null;
	}
	
}
