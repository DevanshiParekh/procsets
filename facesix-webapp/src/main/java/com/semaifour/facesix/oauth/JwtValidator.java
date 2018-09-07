package com.semaifour.facesix.oauth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtValidator {

	Logger LOG = LoggerFactory.getLogger(JwtValidator.class.getName());

    private final String secret = "qubercomm";

    public JwtUser validate(String token) {

    	JwtUser jwtUser = null;
    	
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            
            //LOG.info("parse Name "   + body.getSubject());
            //LOG.info("Parse address" + body.get("address"));
            
            jwtUser = new JwtUser();
            jwtUser.setUserName(body.getSubject());
            jwtUser.setAddress((String) body.get("address"));
        }
        catch (Exception e) {
        	LOG.warn("exception " +e);
        }

        return jwtUser;
    }
}
