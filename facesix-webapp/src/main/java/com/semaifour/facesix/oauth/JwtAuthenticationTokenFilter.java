package com.semaifour.facesix.oauth;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;

public class JwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {

	
	Logger LOG = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class.getName());
	
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	NetworkDeviceService networkDeviceService;
	
    public JwtAuthenticationTokenFilter() {
        super(JwtSecurityConfig.getWhiteListRestApi());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {

        String userToken 	= httpServletRequest.getParameter("token"); 
        String cid 			= httpServletRequest.getParameter("cid");
        String sid 			= httpServletRequest.getParameter("sid");
        String spid 		= httpServletRequest.getParameter("spid");
        String uid  		= httpServletRequest.getParameter("uid");
        
       /* LOG.info("cid "  + cid);
		LOG.info("sid "  + sid);
		LOG.info("spid " + spid);
		LOG.info("uid "  + uid);
		LOG.info("userToken " + userToken);*/
		
		Customer customer 			   = null;
		List<NetworkDevice> networkDev = null;
		NetworkDevice nd			   = null;
		JwtAuthenticationToken token   = null;
		
		if (cid != null) {
			customer = customerService.findById(cid);
		} else if (sid != null) {
			networkDev = networkDeviceService.findBySid(sid);
		} else if (spid != null) {
			networkDev  = networkDeviceService.findBySpid(spid);
		} else if (uid != null) {
			String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
			networkDev  = networkDeviceService.findByUuid(uuid);
		}
         
		if (networkDev != null && networkDev.size() > 0) {
			nd 			= networkDev.get(0);
			cid         = nd.cid;
			customer    = customerService.findById(cid);
		}
		
		//LOG.info(" Customer " +String.valueOf(customer));
		
		String defaultToken = "eyJhbGciOiJIUzUxMiJ9."
						      +"eyJzdWIiOiJPQVVUSCIsImFkZHJlc3MiOiJDaGVubmFpIn0."
						      +"ufOOFtfGuMql1rwgJCmnkNaEQPLvEWcpxzQzROWLROYBk9tBDxdV5yf9fdX5CMerRDkW2175xHrUbzXxDiqN4A";
		
			if (customer == null || customer.getOauth() == null || customer.getOauth().equals("false")) {
				//LOG.info("========== OATHU DISABLED CUSTOMER AND  ALLOW TO DEFAULT TOKEN ================== ");
				token = new JwtAuthenticationToken(defaultToken);
				return getAuthenticationManager().authenticate(token);
			} else if (customer.getOauth().equals("true") && (userToken == null || userToken.isEmpty())) {
				//LOG.info("!!!!!!!!!!!!!!!!  Token is null !!!!!!!!!!!!!!! ");
				throw new RuntimeException("token is null");
			}
				
		
		if (customer.getOauth() != null) {
			
			String oauth = customer.getOauth();
			
			//LOG.info("Oauth Flag " + oauth);
			
			if (oauth.equals("true")) {
				
				String restToken   = customer.getRestToken();
				String jwtrestToken = customer.getJwtrestToken();
				
				//LOG.info("userToken " +  userToken);
				//LOG.info("jwtrestToken  " + jwtrestToken);
				
				if (userToken.equals(restToken)) {
					//LOG.info(" ========== TOKEN EQULSE ===== ");
					token = new JwtAuthenticationToken(jwtrestToken);
			        return getAuthenticationManager().authenticate(token);
				} else {
					//LOG.info("==========TOKEN INCORRECT==========");
					throw new RuntimeException("token is incorrect");
				}
		        
			}
		}
    
        return getAuthenticationManager().authenticate(token);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
      super.successfulAuthentication(request, response, chain, authResult);
      chain.doFilter(request, response);
    }
}
