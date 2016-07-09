package com.primeradiants.oniri.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.novent.NoventManager;
import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

/**
 * REST endpoints to manage a user novent library
 * @author Shanira
 * @since 0.1.0
 */
@RestController
@RequestMapping("/rest/api")
public class LibraryResource {

	@Autowired private UserManager userManager;
	@Autowired private NoventManager noventManager;
	
	@RequestMapping(value = "/library/list", method = RequestMethod.GET)
	public ResponseEntity<?> getLibraryNoventList() {
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = userManager.getUser(currentUser.getUsername());
		
		List<NoventEntity> novents = noventManager.getAllUserNovents(user);
		return null;
	}
	
	
}
