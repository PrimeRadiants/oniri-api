package com.primeradiants.oniri.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primeradiants.model.errors.ValidationError;
import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST endpoint to allow user to sign up to Oniri
 * @author gbiaux
 * @since 0.1.0
 */
@RestController
public class SignUpResource {

	@Autowired private UserManager userManager;
	
	private static final String USERNAME = "username";
	private static final String EMAIL = "email";
	private static final String DIGIT_REGEX = "(?=.*\\d)";
	private static final String LOWERCASE_REGEX = "(?=.*[a-z])";
	private static final String UPPERCASE_REGEX = "(?=.*[A-Z])";
	
	/**
	 * @api {get} /rest/api/novent/list Request list of novents in store
	 * @apiName signUp
	 * @apiGroup SignUp
	 * @apiVersion 0.1.0
	 * 
	 * @apiParam {String} username      User username.
	 * @apiParam {String} email			User email.
	 * @apiParam {String} password      User password.
	 * 
	 * @apiSuccess {String} username    User username.
	 * @apiSuccess {String} email		User email.
	 * @apiSuccess {Date} 	created     User creation date.
	 * 
	 * @apiSuccessExample Success-Response:
	 *     HTTP/1.1 200 OK
	 *     {
	 *       "username": "gabitbol",
	 *       "email": "george.abitbol@prime-radiants.com",
	 *       "created": 1468237452
	 *     }
	 * 
	 * Allow a user to create an account on Oniri
	 * 
	 * @param input Object representing sign up data
	 * @return The created user data
	 * @since 0.1.0
	 */
	@RequestMapping(value = "/signUp", method = RequestMethod.POST)
	public ResponseEntity<?> signUp(@RequestBody SignUpInput input) {
		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
		
		String username = validateUsername(input.getUsername(), errors);
		String email = validateEmail(input.getEmail(), errors);
		String password = validatePassword(input.getPassword(), errors);
		
		if (!errors.isEmpty())
        {
            return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.BAD_REQUEST);
        }
		
		UserEntity user = userManager.createUser(username, email, password);
		
		return ResponseEntity.ok(new UserResource.UserResponse(user.getUsername(), user.getEmail(), user.getCreated()));
	}

	/*
	 * Check if the given username is valid :
	 * 
	 * Is not null
	 * Is not empty
	 * Do not correspond to an existing user
	 * Do not have spaces
	 */
	private String validateUsername(String username, Collection<ValidationError> errors) {
		if(username == null || username.equals("")) {
			errors.add(new ValidationError(USERNAME, "Missing parameter 'username'"));
			return username;
		}
		
		if(username.contains(" ")) {
			errors.add(new ValidationError(USERNAME, "Invalid username : the username must not contain any spaces"));
			return username;
		}
		
		UserEntity user = userManager.getUser(username);
		
		if(user != null)
			errors.add(new ValidationError(USERNAME, "User with username " + username + " already exists"));
		
		return username;
	}
	
	/*
	 * Check if the given email is valid :
	 * 
	 * Is not null
	 * Is not empty
	 * Is not used by an existing user
	 * Has the email format
	 */
	private String validateEmail(String email, Collection<ValidationError> errors) {
		if(email == null || email.equals("")) {
			errors.add(new ValidationError(USERNAME, "Missing parameter 'email'"));
			return email;
		}
		
		UserEntity user = userManager.getUserByEmail(email);
		
		if(user != null)
			errors.add(new ValidationError(EMAIL, "User with email " + email + " already exists"));
		
		EmailValidator emailValidator = EmailValidator.getInstance();
		
		if(!emailValidator.isValid(email))
			errors.add(new ValidationError(EMAIL, "Email is invalid"));
		
		return email;
	}
	
	/*
	 * Check if the given password is valid :
	 * 
	 * Is not null
	 * Is not empty
	 * Has at least one digit
	 * Has at least one lowercase character
	 * Has at least one uppercase character
	 */
	private String validatePassword(String password, Collection<ValidationError> errors) {
		if(password == null) {
			errors.add(new ValidationError(USERNAME, "Missing parameter 'email'"));
			return password;
		}
		
		Pattern digitPattern = Pattern.compile(DIGIT_REGEX);
		if(!digitPattern.matcher(password).matches())
			errors.add(new ValidationError(USERNAME, "Password must contain at least one digit"));
		
		Pattern lowercasePattern = Pattern.compile(LOWERCASE_REGEX);
		if(!lowercasePattern.matcher(password).matches())
			errors.add(new ValidationError(USERNAME, "Password must contain at least one lowercase character"));
		
		Pattern uppercasePattern = Pattern.compile(UPPERCASE_REGEX);
		if(!uppercasePattern.matcher(password).matches())
			errors.add(new ValidationError(USERNAME, "Password must contain at least one uppercase character"));
		
		return password;
	}

	/**
	 * Simple bean representing the data needed to sign up to Oniri
	 * @author gbiaux
	 * @since 0.1.0
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class SignUpInput {
		private String username;
		private String email;
		private String password;
	}
}
