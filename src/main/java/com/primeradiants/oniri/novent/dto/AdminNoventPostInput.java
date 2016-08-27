package com.primeradiants.oniri.novent.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple bean representing the data needed to create a Novent
 * @author Shanira
 * @since 0.1.1
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminNoventPostInput {
	private String title;
	private List<String> authors;
	private String description;
}
