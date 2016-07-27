package com.primeradiants.oniri.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primeradiants.oniri.rest.UserResource.UserResponse;
import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

/**
 * REST endpoints to administrate ONIRI users
 * @author Shanira
 * @since 0.1.0
 */
@RestController
@RequestMapping("/admin/api")
public class UserAdminResource {

	@Autowired private UserManager userManager;
	
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
	 * @return a list of {@link com.primeradiants.oniri.rest.UserResource.UserResponse}.
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
	
}
