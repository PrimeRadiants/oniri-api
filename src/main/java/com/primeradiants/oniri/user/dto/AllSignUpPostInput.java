package com.primeradiants.oniri.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple bean representing the data needed to sign up to Oniri
 * @author Shanira
 * @since 0.1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AllSignUpPostInput {
	private String username;
	private String email;
	private String password;
}
