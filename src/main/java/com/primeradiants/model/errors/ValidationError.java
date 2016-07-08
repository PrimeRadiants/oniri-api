package com.primeradiants.model.errors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Simple bean for holding a field reference and an error key as well as some optional parameters.
 * @author Shanira
 * @since 0.1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class ValidationError {
	@XmlElement
	private String field;
	
	@XmlElement
	private String error;
	
}
