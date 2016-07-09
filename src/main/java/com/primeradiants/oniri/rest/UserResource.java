package com.primeradiants.oniri.rest;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.primeradiants.model.errors.ValidationError;
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
@Path("/rest/user")
public class UserResource {

	private static final String USERNAME = "username";
	
	private UserManager userManager = new UserManager();
	
	/**
	 * Retrieve a user by its user name
	 * @param username the user name of the user
	 * @return a {@link com.primeradiants.oniri.rest.UserResource.UserResponse} if user exists, 
	 * 			else a Collection of {@link com.primeradiants.model.errors.ValidationError}
	 */
	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam(USERNAME) String username) 
	{
		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
		
		//input validation
		UserEntity user = validateUsername(username, errors);
		
		if (!errors.isEmpty())
        {
            return Response.status(400).entity(errors).build();
        }
		
		return Response.status(200).entity(new UserResponse(user.getUsername(), user.getEmail())).build();
	}
	
	//Check if user name corresponds to an existing user in database and return the UserEntity object 
	private UserEntity validateUsername(String username, Collection<ValidationError> errors) {
		UserEntity user = userManager.getUser(username);
		
		if(user == null)
			errors.add(new ValidationError(USERNAME, "Unknown user " + username));
		
		return user;
	}
	
	@XmlRootElement
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class UserResponse {
		@XmlElement
		private String username;
		
		@XmlElement
		private String email;
	}
}
