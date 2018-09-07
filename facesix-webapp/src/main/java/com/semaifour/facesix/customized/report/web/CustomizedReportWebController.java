package com.semaifour.facesix.customized.report.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;

@Controller
@RequestMapping("/web/custom/report")
public class CustomizedReportWebController {

	static Logger LOG = LoggerFactory.getLogger(CustomizedReportWebController.class.getName());

	@Autowired
	private CCC _CCC;
	
	@Autowired
	private CustomerUtils customerUtils;
	
	@RequestMapping("/index")
	public String index(Map<String, Object> model, @RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "proc", required = false) String proc,
			HttpServletRequest request, HttpServletResponse response) {
		String queryPage = null;
		if (SessionUtil.isAuthorized(request.getSession())) {
			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			model.put("cid", cid);
			model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
			model.put("Gateway", 		customerUtils.Gateway(cid));
			model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
			model.put("proc", 	proc);
			
			queryPage = _CCC.pages.getPage("facesix.customize.report.index", "index");
		} else {
			queryPage = _CCC.pages.getPage("facesix.login", "login");
		}
		return queryPage;
	}
	
	@RequestMapping("/query")
	public String query(Map<String, Object> model, @RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "proc", required = false) String proc,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "name", required = false) String name,
			HttpServletRequest request, HttpServletResponse response) {
		String queryPage = null;
		if (SessionUtil.isAuthorized(request.getSession())) {
			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			model.put("cid", cid);
			model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
			model.put("Gateway", 		customerUtils.Gateway(cid));
			model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
			model.put("id", 	id);
			model.put("proc", 	proc);
			model.put("name", 	name);
			
			queryPage = _CCC.pages.getPage("facesix.customize.report.query", "query");
		} else {
			queryPage = _CCC.pages.getPage("facesix.login", "login");
		}
		return queryPage;
	}
	
	@RequestMapping("/visual")
	public String visual(Map<String, Object> model, @RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "proc", required = false) String proc,
			@RequestParam(value = "queryid", required = false) String queryid,
			@RequestParam(value = "name", required = false) String name,
			HttpServletRequest request, HttpServletResponse response) {
		String queryPage = null;
		if (SessionUtil.isAuthorized(request.getSession())) {
			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
				model.put("cid", cid);
				model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
				model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
				model.put("Gateway", 		customerUtils.Gateway(cid));
				model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
				model.put("proc", 	proc);
				model.put("queryid", 	queryid);
				model.put("name", 	name);
				
				queryPage = _CCC.pages.getPage("facesix.customize.report.visuals", "visual");
			}
		} else {
			queryPage = _CCC.pages.getPage("facesix.login", "login");
		}
		return queryPage;
	}
	
	@RequestMapping("/dashboard")
	public String dashboard(Map<String, Object> model, @RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "proc", required = false) String proc,
			HttpServletRequest request, HttpServletResponse response) {
		String queryPage = null;
		if (SessionUtil.isAuthorized(request.getSession())) {
			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
				model.put("cid", cid);
				model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
				model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
				model.put("Gateway", 		customerUtils.Gateway(cid));
				model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
				model.put("proc", 	proc);
				
				queryPage = _CCC.pages.getPage("facesix.customize.report.dashbaord", "dashboard");
			}
		} else {
			queryPage = _CCC.pages.getPage("facesix.login", "login");
		}
		return queryPage;
	}

}
