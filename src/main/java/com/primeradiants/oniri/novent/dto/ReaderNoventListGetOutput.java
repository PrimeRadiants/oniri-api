package com.primeradiants.oniri.novent.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple bean representing a list of novents
 * @author Shanira
 * @since 0.1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReaderNoventListGetOutput {
	private List<ReaderNoventGetOutput> novents;
}
