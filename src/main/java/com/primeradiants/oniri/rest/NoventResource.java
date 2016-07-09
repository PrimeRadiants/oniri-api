package com.primeradiants.oniri.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primeradiants.model.errors.ValidationError;
import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.novent.NoventManager;
import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/rest/api")
public class NoventResource {

	@Autowired private NoventManager noventManager;
	@Autowired private UserManager userManager;
	
	private static final String ID = "id";
	
	/**
	 * Returns the list of all the novents in store
	 * @return a List of {@link com.primeradiants.oniri.rest.LibraryResource.NoventResponse}.
	 */
	@RequestMapping(value = "/novent/list", method = RequestMethod.GET)
	public ResponseEntity<?> getStoreNoventList() {
		List<NoventEntity> novents = noventManager.getAllNovents();
		
		List<NoventResponse> response = new ArrayList<NoventResponse>();
		
		for(NoventEntity novent : novents)
			response.add(new NoventResponse(novent.getId(), novent.getTitle(), novent.getAuthors(), novent.getPublication()));
		
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Retrieves a novent by its id
	 * @param id the id of the novent
	 * @return a {@link com.primeradiants.oniri.rest.LibraryResource.NoventResponse} if novent exists, 
	 * 			else a Collection of {@link com.primeradiants.model.errors.ValidationError}.
	 */
	@RequestMapping(value = "/novent/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getNovent(@PathVariable(ID) Integer id) {
		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
		
		NoventEntity novent = validateNoventId(id, errors);
		
		if (!errors.isEmpty())
        {
            return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.BAD_REQUEST);
        }
		
		NoventDetailedResponse response = new NoventDetailedResponse(novent.getId(), novent.getTitle(), novent.getDescription(), novent.getAuthors(), novent.getPublication());
		
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Create an own link between a user and a novent
	 * @param id the id of the novent
	 * @return a {@link com.primeradiants.oniri.rest.LibraryResource.NoventResponse} if novent exists, 
	 * 			else a Collection of {@link com.primeradiants.model.errors.ValidationError}.
	 */
	@RequestMapping(value = "/novent/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> purchaseNovent(@PathVariable(ID) Integer id) {
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = userManager.getUser(currentUser.getUsername());
		
		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
		
		NoventEntity novent = validateNoventId(id, errors);
		
		if (!errors.isEmpty())
        {
            return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.BAD_REQUEST);
        }
		
		noventManager.createUserNoventLink(user, novent);
		
		return ResponseEntity.ok().build();
	}

	//Checks if id corresponds to an existing novent in database and returns the NoventEntity object
	private NoventEntity validateNoventId(Integer id, Collection<ValidationError> errors) {
		NoventEntity novent = noventManager.getNovent(id);
		
		if(novent == null)
			errors.add(new ValidationError(ID, "Unknown novent with id " + id));
		
		return novent;
	}
	
	/**
	 * Simple bean representing a novent
	 * @author Shanira
	 * @since 0.1.0
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class NoventResponse {
		private Integer id;
		private String title;
		private List<String> authors;
		private Date publication;
	}
	
	/**
	 * Simple bean representing a novent with all details
	 * @author Shanira
	 * @since 0.1.0
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class NoventDetailedResponse {
		private Integer id;
		private String title;
		private String description;
		private List<String> authors;
		private Date publication;
	}
}
