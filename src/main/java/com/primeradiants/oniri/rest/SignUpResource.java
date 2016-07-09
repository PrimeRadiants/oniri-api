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

@RestController
public class SignUpResource {

	@Autowired private UserManager userManager;
	
	private static final String USERNAME = "username";
	private static final String EMAIL = "email";
	private static final String DIGIT_REGEX = "(?=.*\\d)";
	private static final String LOWERCASE_REGEX = "(?=.*[a-z])";
	private static final String UPPERCASE_REGEX = "(?=.*[A-Z])";
	
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

	private String validateUsername(String username, Collection<ValidationError> errors) {
		if(username == null) {
			errors.add(new ValidationError(USERNAME, "Missing parameter 'username'"));
			return username;
		}
			
		
		UserEntity user = userManager.getUser(username);
		
		if(user != null)
			errors.add(new ValidationError(USERNAME, "User with username " + username + " already exists"));
		
		return username;
	}
	
	private String validateEmail(String email, Collection<ValidationError> errors) {
		if(email == null) {
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

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	private static class SignUpInput {
		private String username;
		private String email;
		private String password;
	}
}
