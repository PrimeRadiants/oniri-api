package com.primeradiants.oniri.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST endpoints to manage ONIRI users
 * @author Shanira
 * @since 0.1.0
 */
@RestController
@RequestMapping("/rest/api")
public class UserResource {

//	private static final String USERNAME = "username";
	
	@Autowired private UserManager userManager;
	
	/**
	 * Retrieves the current loged in user.
	 * @return a {@link com.primeradiants.oniri.rest.UserResource.UserResponse}.
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentUser() 
	{
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = userManager.getUser(currentUser.getUsername());
		return ResponseEntity.ok(new UserResponse(user.getUsername(), user.getEmail()));
	}
	
//	/**
//	 * Retrieve a user by its user name
//	 * @param username the user name of the user
//	 * @return a {@link com.primeradiants.oniri.rest.UserResource.UserResponse} if user exists, 
//	 * 			else a Collection of {@link com.primeradiants.model.errors.ValidationError}
//	 */
//	@RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
//	public ResponseEntity<?> getUser(@PathVariable(USERNAME) String username) 
//	{
//		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
//		
//		//input validation
//		UserEntity user = validateUsername(username, errors);
//		
//		if (!errors.isEmpty())
//        {
//            return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.BAD_REQUEST);
//        }
//		
//		return ResponseEntity.ok(new UserResponse(user.getUsername(), user.getEmail()));
//	}
	
//	//Check if user name corresponds to an existing user in database and return the UserEntity object 
//	private UserEntity validateUsername(String username, Collection<ValidationError> errors) {
//		UserEntity user = userManager.getUser(username);
//		
//		if(user == null)
//			errors.add(new ValidationError(USERNAME, "Unknown user " + username));
//		
//		return user;
//	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class UserResponse {
		private String username;
		private String email;
	}
}
