package com.primeradiants.oniri.novent;

import java.util.ArrayList;
import java.util.Collection;
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
import com.primeradiants.oniri.novent.dto.ReaderNoventDetailsGetOutput;
import com.primeradiants.oniri.novent.dto.ReaderNoventGetOutput;
import com.primeradiants.oniri.novent.dto.ReaderNoventListGetOutput;
import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

/**
 * REST endpoints to deal with novents
 * @author gbiaux
 * @since 0.1.0
 */
@RestController
@RequestMapping("/rest/api")
public class ReaderNoventRestController {

	@Autowired private NoventManager noventManager;
	@Autowired private UserManager userManager;
	
	private static final String ID = "id";
	
	/**
	 * Returns the list of all the novents in store
	 * @return a List of {@link com.primeradiants.oniri.novent.dto.ReaderNoventListGetOutput}.
	 */
	@RequestMapping(value = "/novent/list", method = RequestMethod.GET)
	public ResponseEntity<?> getStoreNoventList() {
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = userManager.getUser(currentUser.getUsername());
		
		List<NoventEntity> novents = noventManager.getAllNovents();
		List<NoventEntity> userNovents = noventManager.getAllUserNovents(user);
		
		List<ReaderNoventGetOutput> response = new ArrayList<ReaderNoventGetOutput>();
		
		for(NoventEntity novent : novents)
			response.add(new ReaderNoventGetOutput(novent.getId(), novent.getTitle(), novent.getAuthors(), novent.getPublication(), userNovents.contains(novent)));
		
		return ResponseEntity.ok(new ReaderNoventListGetOutput(response));
	}
	
	/**
	 * Retrieves a novent by its id
	 * @param id the id of the novent
	 * @return a {@link com.primeradiants.oniri.novent.dto.ReaderNoventDetailsGetOutput} if novent exists, 
	 * 			else a Collection of {@link com.primeradiants.model.errors.ValidationError}.
	 */
	@RequestMapping(value = "/novent/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getNovent(@PathVariable(ID) Integer id) {
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = userManager.getUser(currentUser.getUsername());
		
		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
		
		NoventEntity novent = validateNoventId(id, errors);
		
		if (!errors.isEmpty())
        {
            return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.BAD_REQUEST);
        }
		
		ReaderNoventDetailsGetOutput response = new ReaderNoventDetailsGetOutput(novent.getId(), novent.getTitle(), novent.getDescription(), novent.getAuthors(), novent.getPublication(), noventManager.doesUserOwnNovent(user, novent));
		
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Create an own link between a user and a novent
	 * @param id the id of the novent
	 * @return an ok response if the novent exists, 
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
}
