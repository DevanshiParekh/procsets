package com.semaifour.facesix.gustpass.rest;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.semaifour.facesix.account.Privilege;
import com.semaifour.facesix.account.PrivilegeService;
import com.semaifour.facesix.account.rest.CustomerRestController;
import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.gustpass.Gustpass;
import com.semaifour.facesix.gustpass.GustpassService;
import com.semaifour.facesix.schedule.CustomerScheduledTask;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.web.WebController;

@RequestMapping("/rest/gustpass")
@RestController
public class GustpassRestController extends WebController{
	
	Logger LOG = org.slf4j.LoggerFactory.getLogger(CustomerRestController.class.getName());
	
	@Autowired
	GustpassService gustpassService;
	
	@Autowired
	PrivilegeService privilegeService;
	
	@Autowired
	CustomerScheduledTask customerScheduledTask;

	@Autowired
	CustomerUtils customerUtils;
	
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	public static final String ACCOUNT_SID = "AC38ee76f18327cbd3f8303fc62dc08640";
 	public static final String AUTH_TOKEN = "b6ac1ba74de883f212ea7eafb2210faf";
			
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody Iterable<Gustpass> list(HttpServletRequest request) {

		ArrayList<Gustpass> list=new ArrayList<Gustpass>();
		
		try {
			Map<String, Object> model = (Map<String, Object>) sessionCache.getAttribute(request.getSession(), "privs");
			
			String id = "";
			if (model != null) {
				if (model.get("id") != null) {
					id = model.get("id").toString();
				}

			}

			boolean privFlag = privilegeService.hasPrivilege(request.getSession().getId(), Privilege.CUST_WRITE);
			Iterable<Gustpass> gustPassList = null;
			
			if (privFlag) {
				gustPassList = gustpassService.findAll();
			} else {
				gustPassList = gustpassService.findByCustomerId(id);
			}
			
			if (gustPassList != null) {
				for (Gustpass gustpass : gustPassList) {
					if (gustpass.getPassStatus()!= null) {
						if (gustpass.getPassStatus().equals(CustomerUtils.ACTIVE())) {
							list.add(gustpass);
						}
					}

				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@RequestMapping(value="/get",method=RequestMethod.GET)
	public @ResponseBody Gustpass get(@RequestParam(value="id",required=false) String id){
		return gustpassService.findById(id);
	}
	
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public  void delete(@RequestBody Gustpass gustpass){
		gustpassService.delete(gustpass.getId());
		
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public @ResponseBody Gustpass save(@RequestBody Gustpass guestpass, HttpServletRequest request, HttpServletResponse response) {
	
		boolean flag = false;
		try {
			if (guestpass.getId() == null) {
				guestpass.setPassStatus(CustomerUtils.ACTIVE());
				guestpass.setGuestpassstartingon(CustomerUtils.formatDate(new Date()));
				guestpass.setCreatedBy(whoami(request, response));
				guestpass.setCreatedOn(new Date());
				flag = true;
			}

			if (guestpass.getPortalType() != null) {
				if (guestpass.getPortalType().equals("radiocaptive")) {
					guestpass.setNoOfdevices(guestpass.getNoOfdevices());
				} else if (guestpass.getPortalType().equals("radioguest")) {
						guestpass.setGuestpassexpireson(CustomerUtils.formatDate((guestpass.getGuestpassexpireson())));
				}
			}

			guestpass.setPortalType(guestpass.getPortalType());
			guestpass.setModifiedBy(whoami(request, response));
			guestpass.setModifiedOn(now());
			guestpass = gustpassService.save(guestpass);
			
			guestPassEmailTrigger(guestpass, flag,request,response); //email trigger gust pass
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return guestpass;

	}
	
	private void guestPassEmailTrigger(Gustpass guestPass,boolean flag,HttpServletRequest request, HttpServletResponse response) {
		String message="";
		try {
			if (guestPass != null) {
				if (guestPass.getPortalType() != null) {
					if (guestPass.getPortalType().equals("radioguest")) {
						Date startingDate = CustomerUtils.formatDate(guestPass.getGuestpassstartingon());
						Date endingDate = CustomerUtils.formatDate(guestPass.getGuestpassexpireson());
						long days = CustomerUtils.getRemainglicenceDays(endingDate);

					if (flag) {
						message = "Your guestPass has been created sucessfully.guestpass will be expired" + " in "
								+ days + "(days)";
					} else {
						message = "Your guestpass has been updated sucessfully.guestpass will be expired " + " in "
								+ days + "(days)";
					}
					
					LOG.info("guestpass expired days In " + message);
					
					} else if (guestPass.getPortalType().equals("radiocaptive")) {
					
					SecureRandom random = new SecureRandom();
					int num = random.nextInt(100000);
					String formatted = String.format("%05d", num);
					guestPass.setToken(formatted);
					guestPass=gustpassService.save(guestPass);
					
					LOG.info("CAPTIVE PORTAL TOCKEN NO" + formatted +"num " +num);
					
					String hrs = guestPass.getNoOfdevices();
					String appUrl = customerUtils.cloudUrl()+request.getContextPath();
					String url = appUrl + "/captiveportal?id=" + guestPass.getId();
				    
					if (flag) {
						message = " Your captive portal has been created sucessfully.captive portal will be expired "
								  + " in " + hrs + "(hrs)."
							 	  + " Please click below link to  activate your captive portal " + url;
					} else {
						message = "Your captive portal has been updated sucessfully.captive portal will be expired "
								+ " in " + hrs + "(hrs)."
								 + " Please click below link to  activate your captive portal " + url;
					}
				}
				
				if (guestPass.getSendByMobile() != null) {
					if (guestPass.getSendByMobile().equals("email")) {
						customerScheduledTask.EmailandSMSTrigger(guestPass.getEmail(), null, guestPass.getPassName(),message);
					}
				}
				
				
				if (guestPass.getSendByMobile() != null) {
					if (guestPass.getSendByMobile().equals("sms")) {
						customerScheduledTask.EmailandSMSTrigger(null, guestPass.getMobileNumber(), guestPass.getPassName(),message);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@RequestMapping(value = "/networks", method = RequestMethod.GET)
	public @ResponseBody Restponse<List<Map<String ,String>>> networks() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> network = null;
		
		network = new HashMap<String, String>();
		network.put("id", "2G");
		network.put("name", "2G Network");
		list.add(network);
		
		network = new HashMap<String, String>();
		network.put("id", "5G");
		network.put("name", "5G Network");
		list.add(network);

		return  new Restponse<List<Map<String, String>>>(list);
	}
}
