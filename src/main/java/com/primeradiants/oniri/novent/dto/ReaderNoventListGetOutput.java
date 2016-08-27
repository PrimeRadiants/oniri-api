package com.primeradiants.oniri.novent.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple bean representing a list of novents
 * @author Shanira
 * @since 0.1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReaderNoventListGetOutput {
	private List<ReaderNoventGetOutput> novents;
}
