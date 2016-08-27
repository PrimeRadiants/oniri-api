package com.primeradiants.oniri.novent.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple bean representing a novent
 * @author Shanira
 * @since 0.1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReaderNoventGetOutput {
	private Integer id;
	private String title;
	private List<String> authors;
	private Date publication;
	private boolean userOwn;
}
