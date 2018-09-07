package com.semaifour.facesix.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.domain.JSONMap;
import com.semaifour.facesix.util.Cryptor;
import com.semaifour.facesix.web.WebController;

@RestController
@RequestMapping("/rest/security")
public class SecurityController  extends WebController {
	static Logger LOG = LoggerFactory.getLogger(SecurityController.class.getName());

	@Autowired
	Cryptor cryptor;
	
    @RequestMapping("gentoken")
    public @ResponseBody String gentoken(@RequestParam("id") String id, @RequestParam("key") String key) {
    	JSONMap map = new JSONMap();

    	try {
	    	String sig = Long.toString(System.currentTimeMillis(), 16);
	    	id = cryptor.encrypt(id + ":" + sig);
	    	key = cryptor.encrypt(key + ":" + sig);
	    	sig = cryptor.encrypt(sig);
	
	    	map.put("what", "facesix api token");
	    	map.put("id", id);
	    	map.put("key", key);
	    	map.put("sig", sig);
	    	LOG.info("gentoken JSON :" + map.toJSONString());
    	} catch(Throwable t) {
    		LOG.warn("Error while preparing token for :" + id, t);
    	}
    	return map.toJSONString();
    }
    
    public JSONMap parsetoken(String id, String key, String sig) {
    	JSONMap map = new JSONMap();
    	boolean isvalid = false;
    	try {
	    	sig = cryptor.decrypt(sig);
	    	id = cryptor.decrypt(id);
	    	key = cryptor.decrypt(key);
	    	int i = id.indexOf(sig);
	    	if (i > 0) {
	    		id = id.substring(0, i - 1);
	    		i = key.indexOf(sig);
	    		if (i > 0) {
	    			key = key.substring(0, i-1);
	    			if (id != null & id.length() > 0 && key != null & key.length() > 0) {
	    	    		isvalid = true;
	    	    	}
	    		}
	    	}
	    	map.put("id", id);
	    	map.put("key", key);
	    	map.put("sig", sig);
    	} catch (Throwable t) {
    		LOG.warn("Error while parsing token for :" + id, t);
    	}
    	map.put("isvalid", isvalid);
    	return map;
    }
    
    @RequestMapping("valtoken")
    public @ResponseBody String valtoken(@RequestParam("id") String id, @RequestParam("key") String key, @RequestParam("sig") String sig) {
    	JSONMap map = parsetoken(id, key, sig);
    	map.put("what", "facesix val token");
    	map.put("id", id);
    	map.put("key", key);
    	map.put("sig", sig);
    	LOG.info("valtoken JSON :" + map.toJSONString());
    	return map.toJSONString();
    }
    
    @RequestMapping("encrypt")
    public @ResponseBody String encrypt(@RequestParam("k") String key) {
    	try {
			return cryptor.encrypt(key);
		} catch (Exception e) {
			LOG.warn("Encrypt failed", e);
			return e.getMessage();
		}
    }
    
    @RequestMapping("decrypt")
    public @ResponseBody String decrypt(@RequestParam("v") String value) {
    	try {
			return cryptor.decrypt(value);
		} catch (Exception e) {
			LOG.warn("Decrypt failed", e);
			return e.getMessage();
		}
    }
    
}