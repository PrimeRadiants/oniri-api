package com.primeradiants.oniri.rest;

import java.util.ArrayList;
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
import com.primeradiants.oniri.rest.NoventResource.NoventListResponse;
import com.primeradiants.oniri.rest.NoventResource.NoventResponse;
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
	
	/**
	 * @api {get} /rest/api/library/list Request list of novents in current user library
	 * @apiName getCurrentUserLibraryNoventList
	 * @apiGroup Library
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
	 * Returns the list of all the current user novents
	 * @return a List of {@link com.primeradiants.oniri.rest.LibraryResource.NoventResponse}.
	 */
	@RequestMapping(value = "/library/list", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentUserLibraryNoventList() {
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = userManager.getUser(currentUser.getUsername());
		
		List<NoventEntity> novents = noventManager.getAllUserNovents(user);
		List<NoventResponse> response = new ArrayList<NoventResponse>();
		
		for(NoventEntity novent : novents)
			response.add(new NoventResponse(novent.getId(), novent.getTitle(), novent.getAuthors(), novent.getPublication()));
		
		return ResponseEntity.ok(new NoventListResponse(response));
	}
}
