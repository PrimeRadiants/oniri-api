package com.primeradiants.oniri.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primeradiants.oniri.user.dto.ReaderUserGetOutput;

/**
 * REST endpoints to manage ONIRI users
 * @author Shanira
 * @since 0.1.0
 */
@RestController
@RequestMapping("/rest/api")
public class ReaderUserRestController {

	@Autowired private UserManager userManager;
	
	/**
	 * Retrieves the current logged in user.
	 * @return a {@link com.primeradiants.oniri.user.ReaderUserRestController.UserResponse}.
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentUser() 
	{
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = userManager.getUser(currentUser.getUsername());
		return ResponseEntity.ok(new ReaderUserGetOutput(user.getUsername(), user.getEmail(), user.getCreated()));
	}
}
