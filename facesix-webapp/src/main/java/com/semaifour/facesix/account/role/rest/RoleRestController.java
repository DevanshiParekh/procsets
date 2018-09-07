package com.semaifour.facesix.account.role.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.account.Privilege;
import com.semaifour.facesix.account.PrivilegeService;
import com.semaifour.facesix.account.role.RoleService;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;

@RequestMapping("/rest/role")
@RestController
public class RoleRestController extends WebController {

	Logger LOG = LoggerFactory.getLogger(RoleRestController.class.getName());

	@Autowired
	RoleService roleService;

	@Autowired
	PrivilegeService privilegeService;
	
	@Autowired
	UserAccountService userAccountService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody Iterable<String> list(HttpServletRequest request) {

		List<String> roleList = null;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			UserAccount currentuser = userAccountService.findOneByUid(SessionUtil.currentUser(request.getSession()));
			String curRole 			= currentuser.getRole();
			
			try {

				if (curRole.equalsIgnoreCase("superadmin")) {
					roleList = roleService.roleList();
				} else if (curRole.equalsIgnoreCase("appadmin")) {
					roleList = new ArrayList<String>();
					roleList.add("appadmin");
					roleList.add("siteadmin");
					roleList.add("sysadmin");
					roleList.add("useradmin");
					roleList.add("user");
				} else if (curRole.equalsIgnoreCase("useradmin")) {
					roleList = new ArrayList<String>();
					roleList.add("user");
				}

			} catch (Exception e) {
				LOG.error("Getting Role List Error ", e);
			}
		}
		
		return roleList;

	}
}
