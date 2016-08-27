package com.primeradiants.oniri.novent.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple bean representing a novent
 * @author Shanira
 * @since 0.1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReaderNoventGetOutput {
	private Integer id;
	private String title;
	private List<String> authors;
	private Date publication;
	private boolean userOwn;
}
