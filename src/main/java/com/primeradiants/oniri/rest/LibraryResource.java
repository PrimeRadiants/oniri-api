package com.primeradiants.oniri.rest;

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

import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.novent.NoventManager;
import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
		List<NoventResponse> response = new ArrayList<NoventResponse>();
		
		for(NoventEntity novent : novents)
			response.add(new NoventResponse(novent.getId(), novent.getTitle(), novent.getAuthors(), novent.getPublication()));
		
		return ResponseEntity.ok(response);
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class NoventResponse {
		private Integer id;
		private String title;
		private List<String> authors;
		private Date publication;
	}
}
