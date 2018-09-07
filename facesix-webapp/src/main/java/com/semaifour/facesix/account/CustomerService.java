package com.semaifour.facesix.account;

import java.util.List;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.semaifour.facesix.schedule.CustomerScheduledTask;
import com.semaifour.facesix.util.CustomerUtils;

@Service
public class CustomerService {
	static Logger LOG = LoggerFactory.getLogger(Customer.class.getName());

	@Autowired(required = false)
	private CustomerRepository repository;
	
	@Autowired
	CustomerScheduledTask customerScheduledTask;
	
	public CustomerService() {
	}

	public List<Customer> findOneById(String id) {
		return repository.findOneById(QueryParser.escape(id));//null
	}

	public List<Customer> findByCustomerName(String customerName) {
		return repository.findByCustomerName(customerName);
	}

	public Customer findByUId(String id) {
		return repository.findOne(QueryParser.escape(id));
	}
	
	public Customer findByEmail(String emailId) {
		return repository.findByEmail(emailId);
	}
	
	public Customer findById(String id) {
		return repository.findById(id);
	}
	
	
	public Iterable<Customer> findAll() {
		return repository.findAll();
	}
	
	
	public Customer save(Customer customer) {
		return save(customer, true);

	}

	public Customer save(Customer customer, boolean flag) {
		customer = repository.save(customer);
		if (customer.getPkid()== null) {
			customer.setPkid(customer.getId());
			customer = repository.save(customer);
		}
		return customer;
	}
	
	public void delete(String id){
		repository.delete(id);
	}
	
	public void delete(Customer customer){
		repository.delete(customer);
	}

	public Iterable<Customer> findByVenueTypeAndStatus(List<String> solution, String status) {
		return repository.findByVenueTypeAndStatus(solution,status);
	}

	public Iterable<Customer> findBySolutionAndStatus(List<String> solution, String status) {
		return repository.findBySolutionAndStatus(solution,status);
	}

	public Customer findByPreferredUrl(String preferredUrl) {
		return repository.findByPreferedUrlName(preferredUrl);
	}

	}
