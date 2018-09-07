package com.semaifour.facesix.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.rest.ESBeatsRestController;

/**
 * 
 * Configuration Controller for the webapp
 * 
 * @author mjs
 *
 */


@Controller
@RequestMapping("/web/esbeats")
public class ESBeatsWebController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(ESBeatsWebController.class.getName());
			
	@Autowired
	ESBeatsRestController esbeats;

	
	@RequestMapping("/packetology")
	public String topology(Map<String, Object> model, @RequestParam( value="q", defaultValue="+@timestamp:>now-12h") String query) {
		
		//String tree = esbeats.packettopology("*", "query");
		try {
			model.put("query", query);
		} catch (Exception e) {
			model.put("message", Message.newError("Failed to make packet topology. Check internal error."));
			LOG.warn("Exception parsing device :", e);
		}
		return "esbeats-topology";
	}
	
}