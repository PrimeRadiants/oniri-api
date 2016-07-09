package com.primeradiants.oniri.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints to manage a user novent library
 * @author Shanira
 * @since 0.1.0
 */
@RestController
@RequestMapping("/rest/api")
public class LibraryResource {

	@RequestMapping(value = "/library/list", method = RequestMethod.GET)
	public ResponseEntity<?> getLibraryNoventList() {
		return null;
	}
	
	
}
