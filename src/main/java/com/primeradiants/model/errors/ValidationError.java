package com.primeradiants.model.errors;

import com.primeradiants.coverage.util.CoverageIgnore;

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
@CoverageIgnore
@Data
public class ValidationError {
	private String field;
	private String error;
}
