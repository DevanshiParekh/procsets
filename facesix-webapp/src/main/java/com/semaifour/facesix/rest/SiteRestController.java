package com.semaifour.facesix.rest;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.account.Privilege;
import com.semaifour.facesix.account.PrivilegeService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.web.WebController;

@RequestMapping("/rest/site")
@RestController
public class SiteRestController extends WebController{
	
	Logger LOG = LoggerFactory.getLogger(SiteRestController.class.getName());
	
	private final static int QUBER_UNAUTHORIZE_ERR_CODE = 401;
	
	@Autowired
	SiteService siteService;
	
	@Autowired
	PrivilegeService privilegeService;
	
	@Autowired
	CustomerService customerService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody JSONObject list(HttpServletRequest request) {

		JSONObject jsonList = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {

			Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
			String id = "" + model.get("id");
			Iterable<Site> siteList = new ArrayList<Site>();

			boolean flag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);
			if (flag) {
				siteList = siteService.findAll();
			} else {
				siteList = siteService.findByCustomerId(id);
			}

			JSONObject json = null;
			for (Site site : siteList) {
				json = new JSONObject();
				if (site.getStatus() != null) {
					if (site.getStatus().equals(CustomerUtils.ACTIVE())) {
						json.put("id", site.getId());
						json.put("uid", site.getUid());
						json.put("status", site.getStatus());
						if (site.getCustomerId() != null) {
							Customer cust = customerService.findById(site.getCustomerId());
							json.put("custname", cust.getCustomerName());
						}
						json.put("supportFlag", site.isSupportFlag());
						jsonArray.add(json);
					}
				}
			}
			jsonList.put("site", jsonArray);
		} catch (Exception e) {
			LOG.info("Site Support getting Error ", e);
		}

	return jsonList;
	}
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public @ResponseBody Site get(@RequestParam(value = "id", required = false) String id) {
		return siteService.findById(id);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(@RequestBody Site site) {
		siteService.delete(site.getId());
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public @ResponseBody Site save(@RequestBody Site site, HttpServletRequest request, HttpServletResponse response) {
		site.setStatus(CustomerUtils.ACTIVE());
		site.setModifiedBy(whoami(request, response));
		site.setModifiedOn(now());
		site = siteService.save(site);
		return site;

	}
	
	@RequestMapping(value = "/support", method = RequestMethod.POST)
	public @ResponseBody Restponse<Site> support(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
		try {
			Site site = siteService.findById(params.get("sid"));
			if (site != null) {
				site.setSupportFlag(Boolean.valueOf(params.get("flag")));
				site.setModifiedBy(whoami(request, response));
				site.setModifiedOn(now());
				siteService.save(site);
				return new Restponse<Site>(site);
			}
		} catch (Exception e) {
			LOG.error("eror updating support flag for {} ", params, e);
		}
		return new Restponse<Site>(false, QUBER_UNAUTHORIZE_ERR_CODE);
	}

}
