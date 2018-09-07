
package com.semaifour.facesix.data.account;

import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.schedule.CustomerScheduledTask;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.EmailNotification;

@Service 
public class UserAccountService {
	
	static Logger LOG = LoggerFactory.getLogger(UserAccountService.class.getName());
	

	@Autowired(required=false)
	private UserAccountRepository repository;
	
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private CustomerScheduledTask customerScheduledTask;
	
	@Autowired
	CustomerUtils customerUtils;

	public UserAccountService() {
	}
	
	public Page<UserAccount> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public List<UserAccount> findByEmail(String name) {
		return repository.findByEmail(QueryParser.escape(name));
	}	
	
	public List<UserAccount> findByName(String name) {
		return repository.findByName(QueryParser.escape(name));
	}
	
	public List<UserAccount> findByUid(String uid) {
		return repository.findByUid(QueryParser.escape(uid));
	}
	
	public List<UserAccount> findByFname(String fname) {
		return repository.findByFname(QueryParser.escape(fname));
	}
	
	public List<UserAccount> findByLname(String lname) {
		return repository.findByLname(QueryParser.escape(lname));
	}
	
	public UserAccount findOneByEmail(String name) {
		List<UserAccount> list = findByEmail(name);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public UserAccount findOneByName(String name) {
		List<UserAccount> list = findByName(name);
		if (list != null & list.size() > 0) return list.get(0);
		return null;
	}
	
	public UserAccount findOneByUid(String uid) {
		List<UserAccount> list = findByUid(uid);
		if (list != null & list.size() > 0 ) {
			UserAccount bdev = list.get(0);
			if (uid.equalsIgnoreCase(bdev.getUid())){
				return bdev;
			}			
		}
		return null;
	}
	
	public UserAccount findById(String id) {
		return repository.findOne(id);
	}
	
	public boolean exists(String id) {
		return repository.exists(QueryParser.escape(id));
	}
	
	public boolean exists(String uid, String name) {
		if (findOneByUid(uid) != null) return true;
		if (findOneByName(name) != null) return true;
		return false;
	}
	
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public void delete(String id) {
		repository.delete(QueryParser.escape(id));
	}
	
	public void delete(UserAccount userAccount) {
		repository.delete(userAccount);
	}
	
	public long count() {
		return repository.count();
	}
	
	/**
	 * Save userAccount and notify 
	 * 
	 * @param userAccount
	 * @return
	 */
	public UserAccount save(UserAccount userAccount) {		
		return save(userAccount, true, null);
	}
	
	public UserAccount save(UserAccount userAccount,String url) {
		LOG.info(" url "+url);
		return save(userAccount, true, url);
	}	
	/**
	 * 
	 * Save userAccount and notify=true or false
	 * 
	 * @param userAccount
	 * @param notify
	 * @return
	 */
	public UserAccount save(UserAccount userAccount, boolean notify, String peferred_url) {
		
		String subject = "ACCOUNT UPDATE NOTIFICATION";
		
		String default_url = customerUtils.cloudUrl()+"/facesix/";
		String name        = userAccount.getFname().trim();
		String role        = userAccount.getRole().trim();
		String userId      = userAccount.getId();
		
		String message = "Hi "+name+",\n\n";
		
		//LOG.info(" useraccount.getid "+userAccount.getId() +" peferred_url "+peferred_url);
		
		
		if (!role.equalsIgnoreCase("superadmin")) {
			
			if (userId == null) {
				subject = "ACCOUNT CREATE NOTIFICATION";
				message += " Your account has been created successfully.Click the below link to login to your account"
						+ "\n "+default_url+peferred_url +"\n Login id : "+userAccount.getEmail();
			}
			if (userId != null) {
				message += " Your account has been updated successfully.Click the below link to login to your account"
						+ "\n "+default_url+peferred_url +"\n Login id : "+userAccount.getEmail();
			}
		} else {

			if (role.equalsIgnoreCase("superadmin") && userId == null) {
				
				subject = "ACCOUNT CREATE NOTIFICATION";
				
				message = " Your account has been created successfully.Click the below link to login to your account"
						+ "\n "+default_url +"\n Login id : "+userAccount.getEmail();
			}
			
			if (role.equalsIgnoreCase("superadmin") && userId != null) {
				message += " Your account has been Updated successfully.Click the below link to login to your account"
						+ "\n "+default_url +"\n Login id : "+userAccount.getEmail();
			}
		}
		
		if (userAccount.getStatus().equals(CustomerUtils.INACTIVE())) {
			subject = "ACCOUNT DEACTIVATION NOTIFICATION";
			message = "Hi "+name+",\n\n Your account has been deactived.";
		}
		
		if (userAccount.getEmail() != null && userAccount.getPhone() != null) {
			customerUtils.customizeSupportEmail(userAccount.getCustomerId(), userAccount.getEmail(), subject, message, null);
		}

		userAccount = repository.save(userAccount);
		if (userAccount.getPkid()== null) {
			userAccount.setPkid(userAccount.getId());
			userAccount = repository.save(userAccount);
		}
		
		LOG.info("userAccount saved successfully :" + userAccount.getId());
		return userAccount;
	}

	public Iterable<UserAccount> findAll() {
		return repository.findAll();
	}

	public Iterable<UserAccount> findAllByCustomerId(String cid) {
		return repository.findAllByCustomerId(cid);
	}
	
	public List<UserAccount> findByCustomerId(String cid) {
		return repository.findByCustomerId(cid);
	}

	public UserAccount saveContact(UserAccount user) {
		user=repository.save(user);
		return user;
	}

	public List<UserAccount> findByCustomerIdAndIsMailAlert(String cid, String ismailalert) {
		return repository.findByCustomerIdAndIsMailAlert(cid,ismailalert);
	}

	public List<UserAccount> findByRole(String cur_role) {
		return repository.findByRole(cur_role);
	}

	public List<UserAccount> findByCustomerIdCustomizeEmailSmsAndIsMailAlert(String cid, String customizeEmailSms,String ismailalert) {
		return repository.findByCustomerIdCustomizeEmailSmsAndIsMailAlert(cid,customizeEmailSms,ismailalert);
	}
	
}
