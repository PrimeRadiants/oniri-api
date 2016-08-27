package com.primeradiants.model.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple bean for holding a field reference and an error key.
 * @author Shanira
 * @since 0.1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ValidationError {
	private String field;
	private String error;
}
