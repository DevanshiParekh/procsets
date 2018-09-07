package com.semaifour.facesix.oauth;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/rest/token")
public class TokenGeneratorRestController {

	static Logger LOG = LoggerFactory.getLogger(TokenGeneratorRestController.class.getName());

	@Autowired
	CustomerService customerService;
	
	public String md5Checksum(String value) {
		
		String md5sum = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(value.getBytes(), 0, value.length());
			md5sum = new BigInteger(1, md5.digest()).toString(16);
			return md5sum;
		} catch (Exception e) {
			return md5sum;
		}
	}
    
	@RequestMapping(value = "/restToken", method = RequestMethod.POST)
    public JSONObject restToken(@RequestBody String newfso) {

		JSONObject json 	= JSONObject.fromObject(newfso);
		String name 		= (String) json.get("name");
		String address 		= (String) json.get("address");
		
		final String SECRET = "qubercomm";
    	
		
		name 	= name.substring(name.length());
		address = address.substring(address.length());
		
        Claims claims = Jwts.claims()
                .setSubject(name);
        	     claims.put("address", address);

        //LOG.info(" name " +name+ " address " +address);
        
        String  resttoken = 
        		 Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        
		  String md5 = md5Checksum(resttoken);
          JSONObject jsonObject = new JSONObject();
         
          jsonObject.put("restToken", md5);
          jsonObject.put("jwtrestToken", resttoken);
          
        //  LOG.info(" MD5 REST TOKE  " +md5);
        // LOG.info("REST Token " +resttoken);
          
          return jsonObject;
          
  }
	@RequestMapping(value = "/mqttToken", method = RequestMethod.POST)
    public JSONObject mqttToken(@RequestBody String newfso) {

		JSONObject json 	= JSONObject.fromObject(newfso);
		String name 		= (String) json.get("name");
		String postalcode 	= (String) json.get("postalCode");
		
		//LOG.info("name " + name + "postalCode" + postalcode);
     	
		final String SECRET = "qubercomm";
		
		 Claims claims = Jwts.claims()
	                  .setSubject(name);
	        	       claims.put("address", postalcode);
	        	     
	        String  mqttToken = 
	        		 Jwts.builder()
	                .setClaims(claims)
	                .setIssuedAt(new Date())
	                .signWith(SignatureAlgorithm.HS512, SECRET)
	                .compact();
	      
	        String md5 = md5Checksum(mqttToken);
	        
	        //LOG.info("MQTT TOKEN  " +mqttToken);
	        //LOG.info("MD5MQTT " +md5);
	        
	        
          JSONObject jsonObject = new JSONObject();
          jsonObject.put("mqttToken",    md5);
          jsonObject.put("jwtmqttToken", mqttToken);
          
         //LOG.info("MQTT Token " +mqttToken);
         
          return jsonObject;
  }
	
	@RequestMapping(value = "restRefreshToken", method = RequestMethod.POST)
    public  String restRegenerateToken(
							@RequestParam(value = "cid", required = true)   String cid,
							@RequestParam(value = "token", required = true) String authorization) {
		
		Customer customer = customerService.findById(cid);
		
		if (customer != null) {
			
			String restToken = customer.getRestToken();
			
			if (authorization.equals(restToken)) {
				
				String name    = customer.getCustomerName();
				String address = customer.getAddress();
				
				JSONObject json  = new JSONObject();
				json.put("name",    name);
				json.put("address", address);
				
				JSONObject tokenObj  = restToken(json.toString());
				
				String token    = (String)tokenObj.get("restToken"); // md5 token 
				String jwtToken = (String)tokenObj.get("jwtrestToken"); // Reference jwt token
				
				 //LOG.info(" MD5 REST TOKE  " +token);
		         //LOG.info("JWT REST Token " +jwtToken);
		          
				customer.setRestToken(token);
				customer.setJwtrestToken(jwtToken);
				customerService.save(customer);
				
				return token;
			} else {
				//LOG.info(" Incorrect REST Token  " +authorization);
				return "Incorrect Rest Token " + authorization;
			}
		}
		return null;
	}
	
	@RequestMapping(value = "mqttRefreshToken", method = RequestMethod.POST)
	public String mqttRegenerateToken(
					@RequestParam(value = "cid", required = true) String cid,
					@RequestParam(value = "token", required = true) String authorization){
		
		Customer customer = customerService.findById(cid);
		
		if (customer != null) {
			
			String mqttToken = customer.getMqttToken();
			
			if (authorization.equals(mqttToken)) {
				
				String name       = customer.getCustomerName();
				String postalCode = customer.getPostalCode();
				
				JSONObject json  = new JSONObject();
				json.put("name",    name);
				json.put("postalCode", postalCode);
				
				JSONObject tokenObj  = mqttToken(json.toString());
				
				String token   = (String)tokenObj.get("mqttToken"); // md5 token 
				String jwtToken = (String)tokenObj.get("jwtmqttToken"); // refference jwt token
				
				 //LOG.info(" MD5 MQTT TOKE  " +token);
		         //LOG.info("jwtToken MQTT  Token " +jwtToken);
		         
				customer.setMqttToken(token);
				customer.setJwtrestToken(jwtToken);
				customerService.save(customer);
				
				return token;
			} else {
				//LOG.info(" Incorrect MQTT Token  " +authorization);
				return "Incorrect MQTT Token " + authorization;
			}
		}
		return null;
	}
	
}
