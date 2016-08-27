package com.primeradiants.model.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple bean for holding a field reference and an error key.
 * @author Shanira
 * @since 0.1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ValidationError {
	private String field;
	private String error;
}
