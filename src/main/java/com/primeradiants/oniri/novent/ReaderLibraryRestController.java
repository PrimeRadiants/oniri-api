package com.primeradiants.oniri.novent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primeradiants.oniri.novent.dto.ReaderNoventGetOutput;
import com.primeradiants.oniri.novent.dto.ReaderNoventListGetOutput;
import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

/**
 * REST endpoints to manage a user novent library
 * @author Shanira
 * @since 0.1.0
 */
@RestController
@RequestMapping("/rest/api")
public class ReaderLibraryRestController {

	@Autowired private UserManager userManager;
	@Autowired private NoventManager noventManager;

	/**
	 * Returns the list of all the current user novents
	 * @return a List of {@link com.primeradiants.oniri.novent.ReaderNoventRestController.NoventResponse}
	 */
	@RequestMapping(value = "/library/list", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentUserLibraryNoventList() {
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = userManager.getUser(currentUser.getUsername());
		
		List<NoventEntity> novents = noventManager.getAllUserNovents(user);
		List<ReaderNoventGetOutput> response = new ArrayList<ReaderNoventGetOutput>();
		
		for(NoventEntity novent : novents)
			response.add(new ReaderNoventGetOutput(novent.getId(), novent.getTitle(), novent.getAuthors(), new Date(novent.getPublication().getTime()), true));
		
		return ResponseEntity.ok(new ReaderNoventListGetOutput(response));
	}
}
