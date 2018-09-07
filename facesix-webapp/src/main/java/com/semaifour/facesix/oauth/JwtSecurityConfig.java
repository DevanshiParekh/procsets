package com.semaifour.facesix.oauth;
import java.util.Collections;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {

	static Logger LOG = LoggerFactory.getLogger(JwtSecurityConfig.class.getName());

	@Autowired
	private JwtAuthenticationProvider authenticationProvider;

	@Autowired
	private JwtAuthenticationEntryPoint entryPoint;

	
	private static  String gatewayRestApi = "/rest/device/";
	private static  String finderRestApi  = "/rest/beacon/";
	private static  String tokenRestApi   = "/rest/token/";
	
	public static RequestMatcher getWhiteListRestApi() {
		
		String[] urls = new String[] {
        		//gatewayRestApi+"info",
        		//gatewayRestApi+"conf",
        		gatewayRestApi+"configure",
        		//finderRestApi+"device/list",
        		//finderRestApi+"device/info",
        		//finderRestApi+"device/floor_info",
        		//finderRestApi+"list/checkedout",
        		tokenRestApi+"restRegenerateToken",
        		tokenRestApi+"mqttRegenerateToken"
        };
		
        LinkedList<RequestMatcher> matcherList = new LinkedList<>();
        
        for (String url : urls) {
            matcherList.add(new AntPathRequestMatcher(url));
        }
        
        RequestMatcher SECURITY_EXCLUSION_MATCHER = new OrRequestMatcher(matcherList);
        return SECURITY_EXCLUSION_MATCHER;
	}
	
	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(Collections.singletonList(authenticationProvider));
	}

	@Bean
	public JwtAuthenticationTokenFilter authenticationTokenFilter() {
		JwtAuthenticationTokenFilter filter = new JwtAuthenticationTokenFilter();
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(new JwtSuccessHandler());
		return filter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf()
		.disable()
		.authorizeRequests()
		.requestMatchers(getWhiteListRestApi())
		.authenticated()
		.and()
		.exceptionHandling()
		.authenticationEntryPoint(entryPoint).and().sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		http.headers().cacheControl();
	}
}
