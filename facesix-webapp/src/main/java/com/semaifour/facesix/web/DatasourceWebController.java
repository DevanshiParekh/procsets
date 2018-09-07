package com.semaifour.facesix.web;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.semaifour.facesix.data.elasticsearch.datasource.Datasource;
import com.semaifour.facesix.data.elasticsearch.datasource.DatasourceService;
import com.semaifour.facesix.data.jdbc.ConsumableQueueRowMapper;
import com.semaifour.facesix.data.jdbc.GelfNetcatRowProcessor;
import com.semaifour.facesix.data.jdbc.NetcatRowProcessor;
import com.semaifour.facesix.data.jdbc.Reader;
import com.semaifour.facesix.data.jdbc.RowConsumer;
import com.semaifour.facesix.data.jdbc.RowProcessor;
import com.semaifour.facesix.domain.JSONMap;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.util.SimpleUtil;

/**
 * 
 * Configuration Controller for the webapp
 * 
 * @author mjs
 *
 */
@Controller
@RequestMapping("/web/datasource")
public class DatasourceWebController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(DatasourceWebController.class.getName());
			
	@Autowired
	DatasourceService service;
	
	/**
	 * 
	 * Lists all datasources
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/list")
	public String list(Map<String, Object> model) {
		model.put("time", new Date());
		Page<Datasource> datasources = service.findAll(new PageRequest(0,100));
		model.put("fsobjects", datasources);
		model.put("datasource", TAB_HIGHLIGHTER);

		return "datasource-list";
	}
	
	/**
	 * 
	 * Copies given datasource to another
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/copy")
	public String open(Map<String, Object> model,
					   @RequestParam(value = "id") String id) {
		model.put("time", new Date());
		Datasource datasource = null;
		if (id != null) {
			datasource = service.findById(id);
			if (datasource == null) {
				model.put("message", Message.newError("Datasource not found for copy, please enter new datasource details"));
			} else {
				//datasource = new Datasource();
				datasource.setId(null);
				datasource.setUid("Copy of " + datasource.getUid());
				datasource.setName("Copy of " + datasource.getName());
				model.put("message", Message.newInfo("Please correct copied details and save to persist."));
			}
		} else {
			model.put("message", Message.newError("No Datasource to copy, please enter new Datasource details"));
		}
		
		model.put("fsobject", datasource);
		model.put("datasource", TAB_HIGHLIGHTER);

		return "datasource-edit";
	}
	
	/**
	 * 
	 * Open a given datasource
	 * 
	 * @param model
	 * @param id
	 * @param uid
	 * @param name
	 * @return
	 */
	@RequestMapping("/open")
	public String open(Map<String, Object> model,
					   @RequestParam(value = "id", required = false) String id,
					   @RequestParam(value = "uid", required = false) String uid, 
					   @RequestParam(value = "name", required = false) String name) {
		model.put("time", new Date());
		Datasource datasource = null;
		if (id != null) {
			datasource = service.findById(id);
		} else if (name != null) {
			//fetch config by namne
			datasource = service.findOneByName(name);
		} else {
			model.put("message", Message.newInfo("Please enter new Datasource details correctly"));
		}
		
		if (datasource != null) {
			model.put("disabled", "disabled");
			model.put("message", Message.newInfo("Please update existing Datasource config correctly"));
		}
		
		model.put("fsobject", datasource);
		model.put("datasource", TAB_HIGHLIGHTER);

		return "datasource-edit";
	}
	
	/**
	 * Deletes the Id
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	public String delete(Map<String, Object> model,
					  @RequestParam(value = "id", required = true) String id) {
		Datasource datasource = service.findById(id);
		if (datasource == null) datasource = new Datasource();
		service.delete(id);
		datasource.setId(null);
		model.put("fsobject", datasource);
		model.put("message", Message.newError("Datasource deleted successfully"));
		model.put("datasource", TAB_HIGHLIGHTER);

		return "datasource-edit";
	}
	
	/**
	 * Execute query
	 * 
	 * @param model
	 * @param id
	 * @param query
	 * @return
	 */
	@RequestMapping("/execute")
	public String execute(Map<String, Object> model,
					  @RequestParam(value = "id", required = true) String id,
					  @RequestParam(value = "query", required = false) String query,
					  @RequestParam(value = "wty", required = false) String writeType,
					  @RequestParam(value = "wto", required = false) String writeTo,
					  @RequestParam(value = "addattrs", required = false) String addattrs) {
		
		Datasource datasource = service.findById(id);
		model.put("query", query);
		model.put("fsobject", datasource);
		model.put("wty", writeType);
		model.put("wto", writeTo);
		model.put("addattrs", addattrs);
		if (query != null) {
			//execute query
			LOG.info("Executing Datasource :" + id);
			LOG.info("Executing Query :" + query);
			try {
				boolean isAggregation = false;
				RowProcessor processor = null;
				if (writeTo.indexOf("gelf:") >= 0) {
					processor = new GelfNetcatRowProcessor(writeTo.split(":")[1], Integer.parseInt(writeTo.split(":")[2]));
					isAggregation = true;
				} else if (writeTo.indexOf("tcp:")  >= 0) {
					processor = new NetcatRowProcessor(writeTo.split(":")[1], Integer.parseInt(writeTo.split(":")[2]));
					isAggregation = true;
				} else {
					processor = new RowProcessor();
					isAggregation = false;
				}
						
				RowConsumer consumer = new RowConsumer(processor);
				Reader reader = new Reader(datasource);
				List<JSONMap> list = reader.read(query, new ConsumableQueueRowMapper(consumer, SimpleUtil.tomap(addattrs), isAggregation));
				model.put("message", Message.newSuccess("Query execution successful. : " + list.size()));
				if (isAggregation) {
					list = list.subList(0, 1);
				}
				model.put("rows", list);
			} catch (Exception e) {
				model.put("message", Message.newError("Query execution failed : " + e.getMessage()));
				LOG.info("Query execution failed", e);
			}
			LOG.info("Done Executing Datasource :" + id);
		} else {
			model.put("query", datasource.getSettings().getProperty("query"));
			model.put("addattrs", datasource.getSettings().getProperty("addattrs"));
		}
		
		model.put("datasource", TAB_HIGHLIGHTER);

		return "datasource-exe";
	}
	
	

	/**
	 * Saves datasources
	 * 
	 * @param model
	 * @param newds
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Map<String, Object> model,
					  @ModelAttribute Datasource newds) {
		model.put("time", new Date());
		boolean shouldSave = true;
		if (newds.getId() == null) {
			newds.setCreatedOn(new Date());
			newds.setModifiedOn(new Date());

			if (StringUtils.isEmpty(newds.getUid()) || StringUtils.isEmpty(newds.getName())) {
				model.put("message", Message.newError("UID or Name can not be blank."));
				shouldSave = false;
			} else if (service.exists(newds.getUid(), newds.getName())) {
				model.put("message", Message.newError("Datasource with UID or Name already exists."));
				shouldSave = false;
			}
		} else {
			//it's existing
			Datasource oldds = service.findById(newds.getId());
			if (oldds == null) {
				model.put("message", Message.newFailure("Datasource not found with ID :" + newds.getId()));
				shouldSave = false;
			} else {
				//check the mac/device id not overwritten
				oldds.setSettings(newds.getSettings());
				oldds.setConnectionString(newds.getConnectionString());
				oldds.setUsername(newds.getUsername());
				oldds.setPassword(newds.getPassword());
				oldds.setDriverClassName(newds.getDriverClassName());
				oldds.setLoginTimeout(newds.getLoginTimeout());
				
				oldds.setModifiedOn(new Date());
				
				newds = oldds;
			}
		}
		
		if (shouldSave) {
			newds = service.save(newds);
			model.put("disabled", "disabled");
			model.put("message", Message.newSuccess("Datasource saved successfully."));
		}
		model.put("fsobject", newds);
		model.put("datasource", TAB_HIGHLIGHTER);

		return "datasource-edit";
	}
	
}