package com.primeradiants.oniri.user.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple bean representing a user
 * Used as a response by rest endpoints
 * @author Shanira
 * @since 0.1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReaderUserGetOutput {
	private String username;
	private String email;
	private Date created;
}