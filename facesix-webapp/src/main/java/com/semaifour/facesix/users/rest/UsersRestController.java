package com.semaifour.facesix.users.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.graylog2.restclient.models.api.requests.ChangePasswordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.semaifour.facesix.data.graylog.GraylogRestClient;
import com.semaifour.facesix.users.data.Users;
import com.semaifour.facesix.users.data.UsersService;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;

@RequestMapping("/rest/new/user")
@RestController
public class UsersRestController extends WebController {

	Logger LOG=LoggerFactory.getLogger(UsersRestController.class.getName());
	
	@Autowired
	UsersService userService;
	
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody Iterable<Users> list() {
		return userService.findAll();
	}
	
	/*
	 * Users associate customer(comapny)
	 * 
	 */
	@RequestMapping(value = "/userslist", method = RequestMethod.GET)
	public @ResponseBody Iterable<Users> listByCustomerId(String cid, HttpServletRequest request, HttpServletResponse response) {
		Users user = userService.findOneByUid(SessionUtil.currentUser(request.getSession()));
		return userService.findAllByCustomerId(user.getId());
	}
	
	/*
	 *  users list
	 * 
	 */
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public @ResponseBody Users get(HttpServletRequest request, HttpServletResponse response) {
		Users user = userService.findOneByUid(SessionUtil.currentUser(request.getSession()));
			return userService.findById(user.getId());
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public void delete(@RequestParam(value = "id", required = false) String id) {
		userService.delete(id);
	}
	
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public Users profilePost(@RequestBody Users users,@RequestParam("customerId ") String customerId,
					@RequestParam(value = "user_pic", required = false) MultipartFile proFile, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (SessionUtil.isAuthorized(request.getSession())) {
			users.setModifiedBy(whoami(request, response));
			users.setModifiedOn(now());
			users.setCustomerId(customerId);
			users.setPassword(_CCC.cryptor.iencrypt(users.getPassword()));
			users.setJedittings(users.getJedittings());
			users = userService.save(users);
			LOG.info("User save" +users);
			
			/*if(!proFile.isEmpty() && proFile.getSize() > 1) {
				try {
					Path path = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSPROFILES_"), (account.getId() + "_" + proFile.getOriginalFilename()));
					Files.createDirectories(path.getParent());
					Files.copy(proFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					account.setPath(path.toString());
					account = userService.save(account);
				} catch (IOException e) {
					LOG.warn("Failed to save profile pic file", e);
				}
			}*/
			
			
			if (StringUtils.isNotEmpty(users.getPassword()) && StringUtils.equals(users.getPassword(), request.getParameter("c_password"))) {
				setpasswd (users.getUid(), users.getPassword());
			}

			return users;
		} else {
			return null;
		}
	}

	private  void setpasswd(String user, String pwd) {
		ChangePasswordRequest pwdr = new ChangePasswordRequest();
		pwdr.setPassword(pwd);
		GraylogRestClient graylogRestClient = new GraylogRestClient(_CCC.graylog.getRestUrl(),
				_CCC.graylog.getPrincipal(), _CCC.graylog.getSecret());
		try {
			ResponseEntity<Object> response = graylogRestClient.invoke(HttpMethod.PUT, "/users/" + user + "/password",
					pwdr, Object.class);
			switch (response.getStatusCode().value()) {
			case 204:
				break;
			case 400:
				break;
			case 403:
				break;
			case 404:
				break;
			}
		} catch (Exception e) {
			LOG.warn("Failed to change password ", e);
		}
	}
		

}
