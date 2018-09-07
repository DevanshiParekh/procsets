package com.semaifour.facesix.data.account;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {

	public List<UserAccount> findByName(String name);

	public List<UserAccount> findByUid(String uid);
	
	public List<UserAccount> findByEmail(String email);

	public Iterable<UserAccount> findAllByCustomerId(String cid);
	
	public List<UserAccount> findByFname(String fname);
	
	public List<UserAccount> findByLname(String lname);

	public List<UserAccount> findByCustomerId(String cid);
	
	@Query("{customerId:?0, isMailalert:?1}")
	public List<UserAccount> findByCustomerIdAndIsMailAlert(String cid, String ismailalert);

	public List<UserAccount> findByRole(String cur_role);
	@Query("{customerId:?0, customizeEmailSms:?1,isMailalert:?2}")
	public List<UserAccount> findByCustomerIdCustomizeEmailSmsAndIsMailAlert(String cid,String customizeEmailSms, String ismailalert);

}