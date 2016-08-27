package com.primeradiants.oniri.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primeradiants.model.errors.ValidationError;
import com.primeradiants.oniri.user.ReaderUserRestController.UserResponse;

/**
 * REST endpoints to administrate ONIRI users
 * @author Shanira
 * @since 0.1.1
 */
@RestController
@RequestMapping("/admin/api")
public class AdminUserRestController {

	@Autowired private UserManager userManager;
	
	private static final String USERNAME = "username";
	
	/**
	 * @api {get} /admin/api/user/list Request the list of all Oniri users
	 * @apiName getAllUser
	 * @apiGroup User
	 * @apiVersion 0.1.1
	 * 
	 * @apiSuccess {String} username Username of the User.
	 * @apiSuccess {String} email  Email of the User.
	 * @apiSuccess {Date} created  Creation date the User.
	 * 
	 * @apiSuccessExample Success-Response:
	 *     HTTP/1.1 200 OK
	 *     [{
	 *       "username": "gabitbol",
	 *       "email": "george.abitbol@prime-radiants.com",
	 *       "created": "1468237452"
	 *     }]
	 */
	/**
	 * Retreive the list of all Oniri users
	 * @return a list of {@link com.primeradiants.oniri.user.ReaderUserRestController.UserResponse}.
	 */
	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public ResponseEntity<?> getUserList() 
	{
		List<UserResponse> result = new ArrayList<UserResponse>();
		List<UserEntity> users = userManager.getAllUsers();
		
		for(UserEntity user : users)
			result.add(new UserResponse(user.getUsername(), user.getEmail(), user.getCreated()));
		
		return ResponseEntity.ok(result);
	}
	
	/**
	 * @api {get} /admin/api/user/{username} Request a user by its username
	 * @apiName getUser
	 * @apiGroup User
	 * @apiVersion 0.1.1
	 * 
	 * @apiParam {String} username user name.
	 * 
	 * @apiSuccess {String} username Username of the User.
	 * @apiSuccess {String} email  Email of the User.
	 * @apiSuccess {Date} created  Creation date the User.
	 * 
	 * @apiSuccessExample Success-Response:
	 *     HTTP/1.1 200 OK
	 *     {
	 *       "username": "gabitbol",
	 *       "email": "george.abitbol@prime-radiants.com",
	 *       "created": "1468237452"
	 *     }
	 *     
	 * @apiError {Object[]} response
	 * @apiError {String} response.field Field where lies the input validation error
	 * @apiError {String} response.error Error description
	 * 
	 * @apiErrorExample {json} Error-Response:
	 *     HTTP/1.1 400 Bad Request
	 *     [{
	 *     	"field": "username"
	 *       "error": "Unknown user user"
	 *     }]
	 */
	/**
	 * Retrieve a user by its user name
	 * @param username the user name of the user
	 * @return a {@link com.primeradiants.oniri.user.ReaderUserRestController.UserResponse} if user exists, 
	 * 			else a Collection of {@link com.primeradiants.model.errors.ValidationError}
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable String username) 
	{
		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
		
		//input validation
		UserEntity user = validateUsername(username, errors);
		
		if (!errors.isEmpty())
        {
            return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.BAD_REQUEST);
        }
		
		return ResponseEntity.ok(new UserResponse(user.getUsername(), user.getEmail(), user.getCreated()));
	}
	
	/**
	 * @api {delete} /admin/api/user/{username} Delete a user by its username
	 * @apiName deleteUser
	 * @apiGroup User
	 * @apiVersion 0.1.1
	 * 
	 * @apiParam {String} username user name.
	 *  
	 * @apiError {Object[]} response
	 * @apiError {String} response.field Field where lies the input validation error
	 * @apiError {String} response.error Error description
	 * 
	 * @apiErrorExample {json} Error-Response:
	 *     HTTP/1.1 400 Bad Request
	 *     [{
	 *     	"field": "username"
	 *       "error": "Unknown user user"
	 *     }]
	 */
	/**
	 * Delete a user by its user name
	 * @param username the user name of the user to delete
	 * @return else a Collection of {@link com.primeradiants.model.errors.ValidationError} if user do not exist
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable String username) 
	{
		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
		
		//input validation
		UserEntity user = validateUsername(username, errors);
		
		if (!errors.isEmpty())
        {
            return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.BAD_REQUEST);
        }
		
		userManager.deleteUser(user);
		
		return ResponseEntity.ok().build();
	}
	
	//Check if user name corresponds to an existing user in database and return the UserEntity object 
	private UserEntity validateUsername(String username, Collection<ValidationError> errors) {
		UserEntity user = userManager.getUser(username);
		
		if(user == null)
			errors.add(new ValidationError(USERNAME, "Unknown user " + username));
		
		return user;
	}
}
