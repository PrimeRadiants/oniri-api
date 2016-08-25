package com.primeradiants.oniri.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primeradiants.model.errors.ValidationError;
import com.primeradiants.oniri.user.EmailValidationTokenEntity;
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
	@Autowired private MailSender mailSender;
	
	private static final String USERNAME = "username";
	private static final String EMAIL = "email";
	private static final String PASSWORD = "password";
	private static final String TOKEN = "token";
	private static final String DIGIT_REGEX = ".*[1-9].*";
	private static final String LOWERCASE_REGEX = ".*[a-z].*";
	private static final String UPPERCASE_REGEX = ".*[A-Z].*";
	
	/**
	 * @api {get} /rest/api/novent/list Create a new user
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
	 */
	/**
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
		
		UserEntity user = userManager.createUser(username, email, password, false);
		
		String emailValidationToken = UUID.randomUUID().toString();
		userManager.createEmailValidationTokenByToken(user, emailValidationToken);
		
//		SimpleMailMessage validationEmail = new SimpleMailMessage();
//		validationEmail.setTo(user.getEmail());
//		validationEmail.setSubject("Email confirmation");
//		validationEmail.setText(emailValidationToken);
//		validationEmail.setFrom("noreply@oniri.io");
//		
//		mailSender.send(validationEmail);
		
		return ResponseEntity.ok(new UserResource.UserResponse(user.getUsername(), user.getEmail(), user.getCreated()));
	}
	
	@RequestMapping(value = "/signUp/{token}", method = RequestMethod.GET)
	public ResponseEntity<?> signUp(@PathVariable String token) {
		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
		
		EmailValidationTokenEntity tokenEntity = validateEmailValidationToken(token, errors);
		
		if (!errors.isEmpty())
        {
            return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.BAD_REQUEST);
        }
		
		UserEntity user = tokenEntity.getUser();
		user.setEnabled(true);
		
		userManager.updateUser(user);
		userManager.deleteEmailValidationToken(tokenEntity);
		
		return ResponseEntity.ok().build();
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
		}
		
		if(username.length() > 35) {
			errors.add(new ValidationError(USERNAME, "Invalid username : the username must be shorter than 35 characters"));
		}
		
		if(username.length() < 3) {
			errors.add(new ValidationError(USERNAME, "Invalid username : the username must be at least 3 characters"));
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
			errors.add(new ValidationError(EMAIL, "Missing parameter 'email'"));
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
			errors.add(new ValidationError(PASSWORD, "Missing parameter 'email'"));
			return password;
		}
		
		if(password.contains(" ")) {
			errors.add(new ValidationError(PASSWORD, "Invalid password : the password must not contain any spaces"));
		}
		
		if(password.length() < 8) {
			errors.add(new ValidationError(PASSWORD, "Invalid password : the password must be at least 8 characters"));
		}
		
		Pattern digitPattern = Pattern.compile(DIGIT_REGEX);
		if(!digitPattern.matcher(password).matches())
			errors.add(new ValidationError(PASSWORD, "Password must contain at least one digit"));
		
		Pattern lowercasePattern = Pattern.compile(LOWERCASE_REGEX);
		if(!lowercasePattern.matcher(password).matches())
			errors.add(new ValidationError(PASSWORD, "Password must contain at least one lowercase character"));
		
		Pattern uppercasePattern = Pattern.compile(UPPERCASE_REGEX);
		if(!uppercasePattern.matcher(password).matches())
			errors.add(new ValidationError(PASSWORD, "Password must contain at least one uppercase character"));
		
		return password;
	}

	private EmailValidationTokenEntity validateEmailValidationToken(String token, Collection<ValidationError> errors) {
		if(token == null) {
			errors.add(new ValidationError(TOKEN, "Missing parameter token"));
			return null;
		}
		
		EmailValidationTokenEntity tokenEntity = userManager.getEmailValidationTokenByToken(token);
		
		if(tokenEntity == null)
			errors.add(new ValidationError(TOKEN, "Invalid validation token"));
		
		return tokenEntity;
	}
	
	/**
	 * Simple bean representing the data needed to sign up to Oniri
	 * @author Shanira
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
