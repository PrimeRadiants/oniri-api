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

/**
 * REST endpoints to deal with novents
 * @author gbiaux
 * @since 0.1.0
 */
@RestController
@RequestMapping("/rest/api")
public class NoventResource {

	@Autowired private NoventManager noventManager;
	@Autowired private UserManager userManager;
	
	private static final String ID = "id";
	
	/**
	 * @api {get} /rest/api/novent/list Request list of novents in store
	 * @apiName getStoreNoventList
	 * @apiGroup Novent
	 * @apiVersion 0.1.0
	 * 
	 * @apiSuccess {Object[]} 	novents 				List of novent in current user library.
	 * @apiSuccess {Number} 	novents.id 				Id of the novent.
	 * @apiSuccess {String} 	novents.title 			Novent title.
	 * @apiSuccess {String[]} 	novents.authors 		List of the authors of the novent.
	 * @apiSuccess {Date} 		novents.publication 	Novent publication date.
	 * 
	 * @apiSuccessExample Success-Response:
	 *     HTTP/1.1 200 OK
	 *     {
	 *     	 "novents": [{
	 *       	"id": 1,
	 *       	"title": "Novent title",
	 *       	"authors": ["George Abitbol"],
	 *       	"publication": 1468237452
	 *       }]
	 *     }
	 * 
	 * Returns the list of all the novents in store
	 * @return a List of {@link com.primeradiants.oniri.rest.LibraryResource.NoventResponse}.
	 */
	@RequestMapping(value = "/novent/list", method = RequestMethod.GET)
	public ResponseEntity<?> getStoreNoventList() {
		List<NoventEntity> novents = noventManager.getAllNovents();
		
		List<NoventResponse> response = new ArrayList<NoventResponse>();
		
		for(NoventEntity novent : novents)
			response.add(new NoventResponse(novent.getId(), novent.getTitle(), novent.getAuthors(), novent.getPublication()));
		
		return ResponseEntity.ok(new NoventListResponse(response));
	}
	
	/**
	 * @api {get} /rest/api/novent/:id Request list of novents in store
	 * @apiName getStoreNoventList
	 * @apiGroup Novent
	 * @apiVersion 0.1.0
	 * 
	 * @apiParam {Number} id Novent unique ID.
	 * 
	 * @apiSuccess {Number} 	id 				Id of the novent.
	 * @apiSuccess {String} 	title 			Novent title.
	 * @apiSuccess {String} 	description		Novent description.
	 * @apiSuccess {String[]} 	authors 		List of the authors of the novent.
	 * @apiSuccess {Date} 		publication 	Novent publication date.
	 * 
	 * @apiSuccessExample Success-Response:
	 *     HTTP/1.1 200 OK
	 *     {
	 *       "id": 1,
	 *       "title": "Novent title",
	 *       "description": "Novent description",
	 *       "authors": ["George Abitbol"],
	 *       "publication": 1468237452
	 *     }
	 * 
	 * @apiError {Object[]}
	 * @apiError {String} field Field where lies the input validation error
	 * @apiError {String} error Error description
	 * 
	 * @apiErrorExample {json} Error-Response:
	 *     HTTP/1.1 400 Bad Request
	 *     [{
	 *     	"field": "id"
	 *       "error": "Unknown novent with id 1"
	 *     }]
	 * 
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
	 * * @api {get} /rest/api/novent/:id Purchase a novent for the current user
	 * @apiName purchaseNovent
	 * @apiGroup Novent
	 * @apiVersion 0.1.0
	 * 
	 * @apiParam {Number} id Novent unique ID.
	 * 
	 * @apiSuccessExample {json} Success-Response:
	 *     HTTP/1.1 200 OK
	 * 
	 * @apiError {Object[]}
	 * @apiError {String} field Field where lies the input validation error
	 * @apiError {String} error Error description
	 * 
	 * @apiErrorExample {json} Error-Response:
	 *     HTTP/1.1 400 Bad Request
	 *     [{
	 *     	"field": "id"
	 *       "error": "Unknown novent with id 1"
	 *     }]
	 * 
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
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class NoventListResponse {
		private List<NoventResponse> novents;
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
