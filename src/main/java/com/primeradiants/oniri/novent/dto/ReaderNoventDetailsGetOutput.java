package com.primeradiants.oniri.novent.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple bean representing a novent with all details
 * @author Shanira
 * @since 0.1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReaderNoventDetailsGetOutput {
	private Integer id;
	private String title;
	private String description;
	private List<String> authors;
	private Date publication;
	private boolean userOwn;
}
