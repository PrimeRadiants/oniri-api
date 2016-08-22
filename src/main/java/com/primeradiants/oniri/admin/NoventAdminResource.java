package com.primeradiants.oniri.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.primeradiants.model.errors.ValidationError;
import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.novent.NoventManager;
import com.primeradiants.oniri.novent.NoventUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST endpoints to administrate ONIRI novents
 * @author Shanira
 * @since 0.1.1
 */
@RestController
@RequestMapping("/admin/api")
public class NoventAdminResource {

	@Autowired private NoventManager noventManager;
	
	private final static String TITLE = "title";
	private final static String AUTHORS = "authors";
	private final static String COVER = "cover";
	private final static String NOVENT = "novent";
	
	private final static String TMP_FOLDER = "/tmp/primeradiants/oniri-data/";

	@RequestMapping(value = "/novent", method = RequestMethod.POST)
	public ResponseEntity<?> addNovent(@RequestParam MultipartFile cover, @RequestParam MultipartFile novent, @RequestBody NoventPostInput input) 
	{
		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
		
		String title = validateTitle(input.getTitle(), errors);
		List<String> authors = validateAuthors(input.getAuthors(), errors);
		File coverFile = validateCover(cover, errors);
		File noventFile = validateNovent(novent, errors);
		
		if (!errors.isEmpty())
        {
            return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.BAD_REQUEST);
        }
		
		NoventEntity noventEntity;
		try {
			noventEntity = noventManager.createNovent(title, authors, input.getDescription(), coverFile, noventFile);
			
			return ResponseEntity.ok(new NoventPostOutput(noventEntity.getId()));
		} catch (IOException e) {
			e.printStackTrace();
			errors.add(new ValidationError("", "Internal Error, try again or contact your system administrator"));
			return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String validateTitle(String title, Collection<ValidationError> errors) {
		if(title == null || title.equals(""))
			errors.add(new ValidationError(TITLE, "Missing argument 'title'"));
		
		return title;
	}
	
	private List<String> validateAuthors(List<String> authors, Collection<ValidationError> errors) {
		if(authors == null || authors.size() == 0)
			errors.add(new ValidationError(AUTHORS, "Missing argument 'authors'"));
		
		return authors;
	}
	
	private File validateCover(MultipartFile cover, Collection<ValidationError> errors) {
		if(cover == null) {
			errors.add(new ValidationError(COVER, "Missing file 'cover'"));
		}

		File folder = new File(TMP_FOLDER);
		folder.mkdirs();
		File coverFile = new File(TMP_FOLDER + cover.getOriginalFilename());
		try {
			cover.transferTo(coverFile);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			errors.add(new ValidationError(COVER, "Error reading file " + cover.getOriginalFilename()));
		}
		
		if(!NoventUtil.isValidCoverImg(coverFile)) {
			errors.add(new ValidationError(COVER, "Invalid 'cover' file"));
		}
		
		return coverFile;
	}
	
	private File validateNovent(MultipartFile novent, Collection<ValidationError> errors) {
		if(novent == null) {
			errors.add(new ValidationError(NOVENT, "Missing file 'novent'"));
		}

		File folder = new File(TMP_FOLDER);
		folder.mkdirs();
		File noventFile = new File(TMP_FOLDER + novent.getOriginalFilename());
		try {
			novent.transferTo(noventFile);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			errors.add(new ValidationError(NOVENT, "Error reading file " + novent.getOriginalFilename()));
		}
		
		if(!NoventUtil.isValidNoventFile(noventFile)) {
			errors.add(new ValidationError(NOVENT, "Invalid 'novent' file"));
		}
		
		return noventFile;
	}

	/**
	 * Simple bean representing the data needed to create a Novent
	 * @author Shanira
	 * @since 0.1.1
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class NoventPostInput {
		private String title;
		private List<String> authors;
		private String description;
	}
	
	/**
	 * Simple bean representing the data returned when creating a novent
	 * @author Shanira
	 * @since 0.1.1
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class NoventPostOutput {
		private int id;
	}
}
