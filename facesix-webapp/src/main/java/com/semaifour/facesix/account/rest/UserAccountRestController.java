package com.semaifour.facesix.account.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.graylog2.restclient.models.api.requests.ChangePasswordRequest;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.account.Privilege;
import com.semaifour.facesix.account.PrivilegeService;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.graylog.GraylogRestClient;
import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.schedule.CustomerScheduledTask;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;
import net.sf.json.JSONObject;

@RequestMapping("/rest/user")
@RestController
public class UserAccountRestController extends WebController {

	Logger LOG = LoggerFactory.getLogger(UserAccountRestController.class.getName());

	@Autowired
	UserAccountService userAccountService;

	@Autowired
	CustomerService customerService;

	@Autowired
	PrivilegeService privilegeService;
	
	@Autowired
	CustomerScheduledTask customerScheduledTask;
	
	@Autowired
	CustomerUtils customerUtils;

	@RequestMapping(value = "/listAll", method = RequestMethod.GET)
	public @ResponseBody Iterable<UserAccount> get() {
		return userAccountService.findAll();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(HttpServletRequest request) {

		try {
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonList = new JSONObject();
			JSONObject json 	= null;
			String id 			= "";
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
				
				if (model != null) {
					if (model.get("id") != null) {
						id = model.get("id").toString();
					}
				}

				Iterable<UserAccount> userAccount = null;
				Iterable<Customer> customer 	  = null;

				boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);

				if (privFlag) {
					customer = customerService.findAll();
				} else {
					customer = customerService.findOneById(id);
				}

				String currentuser   = SessionUtil.currentUser(request.getSession());
				UserAccount cur_user = userAccountService.findOneByEmail(currentuser);
				String cur_role		 = cur_user.getRole();
				
				if ("superadmin".equals(cur_role)) {
					
					List<UserAccount> superadminList = userAccountService.findByRole(cur_role);
					
					for(UserAccount account: superadminList) {
						
						if (account.getCustomerId() != null && !account.getCustomerId().isEmpty()) {continue;}

						String email = account.getEmail();
						if(email.equalsIgnoreCase(currentuser)){continue;}
						
						json = new JSONObject();
						json.put("id", 			account.getId());
						json.put("fname", 		account.getFname());
						json.put("lname", 		account.getLname());
						json.put("designation", account.getDesignation());
						json.put("isMailalert", account.getIsMailalert());
						json.put("isSmsalert",  account.getIsSmsalert());
						json.put("email", 		account.getEmail());
						json.put("phone", 		account.getPhone());
						json.put("role", 		account.getRole());
						jsonArray.add(json);
					}
				}
				
				if (customer != null) {
					for (Customer cust : customer) {
						if (cust.getStatus() != null && cust.getStatus().equals(CustomerUtils.ACTIVE())) {

							String usersCustId = cust.getId();
							String custName = cust.getCustomerName();
							userAccount 	   = userAccountService.findByCustomerId(usersCustId);

							for (UserAccount account : userAccount) {

								if (account.getEmail().equalsIgnoreCase(currentuser)) {
									continue;
								}

								String role = account.getRole();
								int flag    = validateRole(role, cur_role);

								if (flag == 1) {
									json = new JSONObject();
									json.put("id", account.getId());
									json.put("fname", account.getFname());
									json.put("lname", account.getLname());
									json.put("designation", account.getDesignation());
									json.put("isMailalert", account.getIsMailalert());
									json.put("isSmsalert", account.getIsSmsalert());
									json.put("email", account.getEmail());
									json.put("phone", account.getPhone());
									json.put("role", account.getRole());
									if (customerUtils.Gateway(usersCustId)) {
										json.put("solution", "gateway");
									} else if (customerUtils.GatewayFinder(usersCustId)) {
										json.put("solution", "gatewayfinder");
									} else if (customerUtils.GeoFinder(usersCustId)) {
										json.put("solution", "geofinder");
									} else if (customerUtils.GeoLocation(usersCustId)) {
										json.put("solution", "geolocation");
									}
									json.put("customerName",custName);
									json.put("customerId", usersCustId);
									json.put("loginCount", account.getCount());
									jsonArray.add(json);
								}

							}
						}
					}
					jsonList.put("users", jsonArray);
				}
				return jsonList;
			}

		} catch (Exception e) {
			LOG.error("getting customer list Error ", e);
		}

		return null;
	}

	public int validateRole(String acc_role ,String cur_role) {
		
		int flag = 1;
		
		switch (acc_role) {
		
		case "superadmin":
			if (!cur_role.equals("superadmin")) {
				flag = 0;
			}
			break;
		case "appadmin":
			if (!cur_role.equals("superadmin") && !cur_role.equals("appadmin")) {
				flag = 0;
			}
			break;
		case "siteadmin":
			if(!cur_role.equals("superadmin") && !cur_role.equals("appadmin") && !cur_role.equals("siteadmin")){
				flag = 0;
			}
			break;
		case "systemadmin":
			if(!cur_role.equals("superadmin") && !cur_role.equals("appadmin") && !cur_role.equals("systemadmin")){
				flag = 0;
			}
			break;
		case "useradmin":
			if(cur_role.equals("user")){
				flag = 0;
			}
			break;
		case "user":
			if(cur_role.equals("user") || cur_role.equals("siteadmin") || cur_role.equals("systemadmin") ){
				flag = 0;
			}
			break;
		}
		
		return flag;
	}
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public JSONObject profile(HttpServletRequest request, HttpServletResponse response) {

		try {
			if (SessionUtil.isAuthorized(request.getSession())) {
				List<UserAccount> users = userAccountService.findByUid(SessionUtil.currentUser(request.getSession()));
				if (users.get(0) != null) {
					UserAccount user 	= users.get(0);
					JSONObject json 	= new JSONObject();
					json.put("id", 			user.getId());
					json.put("fname", 		user.getFname());
					json.put("lname", 		user.getLname());
					json.put("email", 		user.getEmail());
					json.put("phone", 		user.getPhone());
					json.put("designation", user.getDesignation());
					json.put("role", 		user.getRole());
					json.put("isMailalert", user.getIsMailalert());
					json.put("isSmsalert", 	user.getIsSmsalert());
					json.put("customerId", 	user.getCustomerId());
					if (user.getPath() == null || user.getPath().isEmpty()) {
						json.put("imgpath", "");// for UI Validation
					} else {
						json.put("imgpath", user.getPath());
					}
					
					return json;
				}
			}

		} catch (Exception e) {
			LOG.error("getting profile Error ", e);
		}

		return null;
	}

	@RequestMapping(value = "/profileupload" , method = RequestMethod.POST)
	public Restponse<String> upload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		boolean flag 	= true;
		String message  = "Account has been created sucessfully.";
			
			try {
				
				if (SessionUtil.isAuthorized(request.getSession())) {
					
					String userId 		  	= request.getParameter("userid");
					UserAccount account 	=  null;
					
					if (userId != null) {
						account = userAccountService.findById(userId);
					}
					
					if (account != null) {
						
						String rootPath  					  = _CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_");
						MultipartHttpServletRequest mRequest  = (MultipartHttpServletRequest) request;
					    MultipartFile imgFile 			 	  = mRequest.getFile("profilepic");
					
					    if (imgFile != null && !imgFile.isEmpty() && imgFile.getSize() > 1) {
							
					    	Path path = Paths.get(rootPath, (userId+"_profile_"+account.getFname()));
					    	Files.copy(imgFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
							
					    	account.setPath(path.toString());
					    	account.setModifiedBy(whoami(request, response));
					    	account.setModifiedOn(now());
					    	account = userAccountService.saveContact(account);
							
							LOG.info(" IMG NAME   " + imgFile.getOriginalFilename() );
							LOG.info(" FILE PATH  " +  account.getPath() );
							
						} else {
							
							account.setPath("");
							account.setModifiedBy(whoami(request, response));
							account.setModifiedOn(now());
							account = userAccountService.saveContact(account);
							
							LOG.info(" REMOVED PROFILE  " +  account.getPath() );
							
						}
					}
					
				} else {
					message = "Unauthorized User.";
					flag = false;
					return new Restponse<String>(flag, 200, message);
				}

			} catch (Exception e) {
				LOG.error("Profile upload Error  ", e);
				message 	= "Profile update Error.";
				flag 		= false;
			}
		
		return new Restponse<String>(flag, 200, message);

	}
	
	@RequestMapping(value = "/findByEmail", method = RequestMethod.GET)
	public boolean email(@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "email", required = false) String email) {
		boolean emailFlag = false;
		if (email != null && email != "" && !email.isEmpty() && email.length() > 0) {
			UserAccount user = userAccountService.findOneByEmail(email);
			if (user == null) {
				emailFlag = false;
			} else {
				emailFlag = true;
			}
		}
		return emailFlag;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(@RequestBody UserAccount account) {
		 userAccountService.delete(account.getId());
	}
	
	@RequestMapping(value = "/checkDuplicateUID", method = RequestMethod.GET)
	public JSONObject checkDuplicateUID(@RequestParam("email") String uid, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String Retresponse  = "new";
		JSONObject json 	= new JSONObject();
		UserAccount acc = userAccountService.findOneByUid(uid);
		if(acc !=null && acc.getUid().equalsIgnoreCase(uid.trim())){
		     Retresponse="duplicate";
		}
		json.put("data",Retresponse );
		return json;
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public Restponse<String> save(@RequestBody UserAccount newaccount, HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserAccount account = null;
		boolean flag 	= false;
		String message 	= null;
		String pref_url = null;
		Customer cx     = null;
		String version  = null;
		try {
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				String cid = newaccount.getCustomerId();
				cx		   = customerService.findById(cid);
				
				if (cx  != null) {
					version   = cx.getVersion();
					pref_url  = cx.getPreferedUrlName();
				}
				
				if (newaccount.getId() == null) {
					newaccount.setCreatedBy(whoami(request, response));
					newaccount.setCreatedOn(now());
					newaccount.setStatus(CustomerUtils.ACTIVE());
					newaccount.setCustomerId(newaccount.getCustomerId());
					newaccount.setUid(newaccount.getEmail());
					if (newaccount.getPassword() != null && !StringUtils.isEmpty(newaccount.getPassword())) {
						newaccount.setPassword(_CCC.cryptor.encrypt(newaccount.getPassword()));
					}
					newaccount.setVersion(version);
					account = newaccount;
					message = "Account has been created sucessfully.";
					flag = true;
				} else {
					account = userAccountService.findById(newaccount.getId());
					if (account != null) {
						String fname = newaccount.getFname();
						String lname = newaccount.getLname();
						String email = newaccount.getEmail();
						String designation = newaccount.getDesignation();
						
						if (cx != null) {
							String userAccId = cx.getUserAccId() == null ? "" : cx.getUserAccId();
							pref_url 		 = cx.getPreferedUrlName();
							
							if (userAccId.equals(account.getId())) {
								cx.setContactPerson(fname);
								cx.setContactPersonlname(lname);
								cx.setEmail(email);
								cx.setDesignation(designation);
								customerService.save(cx);
							}
							
						}
						
						account.setUid(newaccount.getEmail());
						account.setFname(newaccount.getFname());
						account.setLname(newaccount.getLname());
						account.setEmail(newaccount.getEmail());
						account.setPhone(newaccount.getPhone());
						account.setDesignation(newaccount.getDesignation());
						account.setRole(newaccount.getRole());
						account.setIsMailalert(newaccount.getIsMailalert());
						account.setIsSmsalert(newaccount.getIsSmsalert());
						if(newaccount.getRole().equals("superadmin")){
							account.setCustomerId(null);
						}else{
							account.setCustomerId(newaccount.getCustomerId());
						}
						message = "Account has been updated sucessfully.";
						flag = true;
					} else {
						message = "Account not found.";
						flag = false;
						return new Restponse<String>(flag, 200, message);
					}
				}
				
				account.setModifiedBy(whoami(request, response));
				account.setModifiedOn(now());
				account = userAccountService.save(account,pref_url);
				return new Restponse<String>(flag, 200, message);
			} else {
				message = "Unauthorized User.";
				flag = false;
				return new Restponse<String>(flag, 200, message);
			}
		} catch (Exception e) {
			LOG.error("Account saving Error ", e);
			message = "Account saving Error.";
			flag = false;
		}
		return new Restponse<String>(flag, 200, message);
	}

	@RequestMapping(value = "/profile/chpwd", method = RequestMethod.POST)
	public Restponse<String> profilechpwd(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
		String pwd = params.get("p");
		String cpwd = params.get("cp");
		boolean flag = false;
		String message = null;

		try {

			String id = sessionCache.getStringAttribute(request.getSession(), "userId");
			UserAccount user = userAccountService.findById(id);
			if (user != null) {
				if (pwd.equals(cpwd)) {
					flag = true;
					user.setPassword(_CCC.cryptor.encrypt(pwd));
					user = userAccountService.saveContact(user);
					message = "Password updated.";
				} else {
					message = "Passwords didn't match";
				}
			} else {
				message = "Profile not found.";
			}
		} catch (Exception e) {
			LOG.error("error updating profile password {}", params, e);
			message = "Password update failed.";
		}
		return new Restponse<String>(flag, 200, message);
	}

	@RequestMapping(value = "/chpwd", method = RequestMethod.POST)
	public Restponse<String> chpwd(@RequestBody Map<String, String> params) {
		String id = params.get("id");
		String pwd = params.get("p");
		String cpwd = params.get("cp");
		boolean flag = false;
		String message = null;

		try {
			UserAccount user = userAccountService.findById(id);
			
			if (user != null) {
				
				String pref_url = null;
				String role 	= user.getRole();
				
				if (!"superadmin".equalsIgnoreCase(role)) {
					Customer cx = customerService.findById(user.getCustomerId());
					if (cx != null) {
						pref_url = cx.getPreferedUrlName();
					}
				}
				if (pwd.equals(cpwd)) {
					flag = true;
					user.setPassword(_CCC.cryptor.encrypt(pwd));
					user = userAccountService.save(user,pref_url);
					message = "Password updated.";
				} else {
					message = "Passwords didn't match";
				}
			} else {
				message = "Profile not found.";
			}
		} catch (Exception e) {
			LOG.error("error updating profile password {}", params, e);
			message = "Password update failed.";
		}
		return new Restponse<String>(flag, 200, message);
	}

	@RequestMapping(value = "/changepwd", method = RequestMethod.POST)
	public String changepwd(@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "confirmpassword", required = false) String confirmpassword, HttpServletRequest request, HttpServletResponse response) {

		boolean flag = false;
		String returnString = "";

		UserAccount user = userAccountService.findById(SessionUtil.currentUser(request.getSession()));

		if (SessionUtil.isAuthorized(request.getSession())) {
			if (user.getId().equals(id)) {
				if (password.equals(confirmpassword)) {
					flag = true;
				} else {
					returnString = "Your password doesn't match";
				}

			}
		}

		if (flag) {
			String pref_url = null;
			try {
				user.setPassword(_CCC.cryptor.encrypt(password));
			} catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if(user.getCustomerId()!= null){
				Customer cx = customerService.findById(user.getCustomerId());
				pref_url 	= cx.getPreferedUrlName();
			}

			user = userAccountService.save(user,pref_url);
			returnString = setpasswd(user.getUid(), password);
			return returnString;
		}

		return returnString;
	}

	private String setpasswd(String user, String pwd) {
		ChangePasswordRequest pwdr = new ChangePasswordRequest();
		pwdr.setPassword(pwd);
		String flag = "";
		GraylogRestClient graylogRestClient = new GraylogRestClient(_CCC.graylog.getRestUrl(),
				_CCC.graylog.getPrincipal(), _CCC.graylog.getSecret());
		try {
			ResponseEntity<Object> response = graylogRestClient.invoke(HttpMethod.PUT, "/users/" + user + "/password",
					pwdr, Object.class);
			switch (response.getStatusCode().value()) {
			case 204:
				flag = "The password was successfully updated. Subsequent requests must be made with the new password.";
				break;
			case 404:
				flag = "If the user does not exist.";
				break;
			}
		} catch (Exception e) {
			flag = "Failed to change password.";
			LOG.warn("Failed to change password ", e);
		}
		return flag;
	}

}
