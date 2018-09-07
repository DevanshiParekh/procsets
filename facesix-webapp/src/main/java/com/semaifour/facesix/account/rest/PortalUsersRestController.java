package com.semaifour.facesix.account.rest;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.semaifour.facesix.account.*;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.captive.portal.*;
import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.mqtt.DeviceEventPublisher;
import com.semaifour.facesix.schedule.CustomerScheduledTask;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/rest/portal/users")
public class PortalUsersRestController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(PortalUsersRestController.class.getName());

	@Autowired
	CustomerService customerService;

	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	CaptivePortalService captivePortalService;
	
	@Autowired
	PortalUsersService  portalUserService;
	
	@Value("${facesix.cloud.name}")
	private String url;
	
	@Autowired
	CustomerScheduledTask customerScheduledTask;
	
	@Autowired
	PrivilegeService privilegeService;
	
	@Autowired
	DeviceEventPublisher deviceEventMqttPub;
	
	@Autowired
	CustomerUtils CustomerUtils;
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Restponse<String> save(@RequestBody String portalUsers) {

		String message     = "failed";
		boolean flag 	   = false;
		int statusCode	   = 500;
		String usersId     = null;
		
		try {
			
			JSONObject users = JSONObject.fromObject(portalUsers);
			String peerMac   = (String)users.get("uid");
			
			if (peerMac != null && !peerMac.isEmpty()) {
				
				LOG.info("usersdetails " +users);
				
				String usersName = (String)users.get("username");
				String email 	 = (String)users.get("email");
				String phone 	 = (String)users.get("phone");
				boolean terms 	 = (boolean)users.get("terms");
				String cid 	 	 = (String)users.get("cid");
				String portalId  = (String)users.get("portalId");
				String custName  = (String)users.get("customerName");
				
				
				phone 			= phone.trim();
				email 			= email.trim();
				usersName 		= usersName.trim();
				
				//LOG.info("email  " +email +" phone " +phone +"name " +usersName) ;
				
				JSONObject customField   = null;
				PortalUsers portalusers  = null;
				 
				if (users.get("customField") != null) {
					customField = (JSONObject) users.get("customField");
				}
			     
				portalusers = portalUserService.findOneByUid(peerMac);
			     
				if (portalusers == null) {
					portalusers = new PortalUsers();
					portalusers.setCreatedOn(new Date());
					portalusers.setCreatedBy("cloud");
					portalusers.setStatus(CustomerUtils.ACTIVE());
					portalusers.setUid(peerMac);
				}
					portalusers.setUsername(usersName);
					portalusers.setPhone(phone);
					portalusers.setEmail(email);
					portalusers.setTerms(terms);
					portalusers.setCid(cid);
					portalusers.setPortalId(portalId);
					portalusers.setCustomField(String.valueOf(customField));
					portalusers.setCustomerName(custName);
					portalusers.setStatus("revoked");
					portalusers.setModifiedOn(new Date());
					portalusers.setModifiedBy("cloud");	
				
				String otp = generateOTP();
				
				portalusers.setToken(otp);
				portalusers.setTokenStatus("active");
				
				portalusers = portalUserService.save(portalusers,false);
				
				usersId 	   = portalusers.getId();
				message		   = "success";
				statusCode	   = 200;	
				flag		   = true;
				
				String emailMsg = "Hi,\n\n Captive portal has been registerd sucessfully."
						+ "\n Your OTP is "+otp +"\n";
				
				final String subject = "Captive Portal Verification";
				
				CustomerUtils.customizeSupportEmail(cid, email, subject, emailMsg, null);
			}
			
		} catch (Exception e) {
			message = "Error occured while users register Portal.";
			flag 	= false;
			LOG.error("while users register Portal  Error ", e);
		}

		return new Restponse<String>(flag, statusCode, message,usersId);

	}
	
	private String generateOTP() {
		SecureRandom random = new SecureRandom();
		int num 		    = random.nextInt(100000);
		String otp    		= String.format("%05d", num);
		LOG.info("OTP IS " +otp );
		return otp;
	}

	@RequestMapping(value = "/validateOTP", method = RequestMethod.POST)
	public Restponse<String> validateOTP(@RequestBody String portalUsers) {
		
		
		String message     = "invalidOTP";
		boolean flag 	   = false;
		int statusCode	   = 500;
		
		try {

			JSONObject object   = JSONObject.fromObject(portalUsers);
			String userId 	    = (String)object.get("id");
			String otp 			= (String)object.get("otp");
		     
			PortalUsers portalusers = null;
			portalusers 		    = portalUserService.findById(userId);

			//LOG.info(" userId " +userId + " OTP " +otp);
			//LOG.info(" portalusers " +portalusers);
			
			if (portalusers != null) {
				
				String token 		= portalusers.getToken();
				String tokenStatus  = portalusers.getTokenStatus();
				
				if (token.equals(otp) && tokenStatus.equals("active")) {
					message = "validOTP";
					portalusers.setTokenStatus("inactive");
					portalusers.setStatus("approved");
					portalusers.setApprovedDate(new Date());
					portalusers.setModifiedOn(new Date());
					portalusers.setModifiedBy("cloud");
					portalUserService.save(portalusers,true);
				}

				statusCode = 200;
				flag 	   = true;
			}

		} catch (Exception e) {
			statusCode = 500;
			flag 	   = false;
			LOG.error("while Validate OTP Error ", e);
		}
		
		return new Restponse<String>(flag, statusCode, message);
	}
	
	@RequestMapping(value = "/revokeClient", method = RequestMethod.POST)
	public  Restponse<String> revokeClient(@RequestBody String portalUsers,HttpServletRequest request,HttpServletResponse response) {
		
		String status     = "InvalidUser";
		boolean flag 	   = false;
		int statusCode	   = 500;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			JSONObject object 		= JSONObject.fromObject(portalUsers);
			String uid 	      		= (String)object.get("uid");
			String universalId 	  	= (String)object.get("universalId");
			
			PortalUsers portalusers = null;
			portalusers 		    = portalUserService.findOneByUid(uid);
			
			if (portalusers != null) {
				
				portalusers.setStatus("revoked");
				portalusers.setModifiedOn(new Date());
				portalusers.setModifiedBy("cloud");
				portalUserService.save(portalusers, false);

				String peer_mac = portalusers.getUid();

				final String opcode = "HS_BLOCK_STA";

				final String mqttMsgTemplate = "\"opcode\":\"{0}\",\"peer_mac\":\"{1}\"";
				String message = MessageFormat.format(mqttMsgTemplate, new Object[] { opcode, peer_mac });
				deviceEventMqttPub.publish("{" + message + "}", universalId);
				
				statusCode = 200;
				flag 	   = true;
				status     = "VaildUser";

			}
		}

		return new Restponse<String>(flag, statusCode, status);
	}
	
	@RequestMapping(value = "/findById", method = RequestMethod.GET)
	public @ResponseBody Iterable<PortalUsers> findOneById(@RequestParam(value = "id", required = true) String id) {
		return portalUserService.findOneById(id);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody Iterable<PortalUsers> listData(HttpServletRequest request, HttpServletResponse response) {

		Iterable<PortalUsers> portalUsersList = null;
		String id = "";
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			try {
				
				@SuppressWarnings("unchecked")
				Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
			
				if (model != null) {
					if (model.get("id") != null) {
						id = model.get("id").toString();
					}
				}

				boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);
				if (privFlag) {
					portalUsersList = portalUserService.findAll();
				} else {
					portalUsersList = portalUserService.findByCid(id);
				}

				return portalUsersList;
				
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Customer details getting error ", e);
			}
		}
		return portalUsersList;
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public  Restponse<String> delete(@RequestBody String fsObject ,HttpServletRequest request, HttpServletResponse response) {
		
		String message   = " Users  has been deleted successfully.";
		boolean flag 	 = false;
		int statusCode	 = 500;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			try {
				
				JSONObject json 	= JSONObject.fromObject(fsObject);
				String usersId 		= (String) json.get("id");

				LOG.info("delete by Given Portal Users Id " + usersId);
				
				if (usersId != null) {
					PortalUsers portal = portalUserService.findById(usersId);
					if (portal != null) {
						portalUserService.delete(portal);
						flag       = true;
						statusCode = 200;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				message = "Error occured while removing Portal Users"; 
			}
		} else {
			message = "Unauthorized User";
		}
		return new Restponse<String>(flag, statusCode, message);
	}

	
}
