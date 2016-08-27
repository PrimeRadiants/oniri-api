package com.primeradiants.oniri.novent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.primeradiants.model.errors.ValidationError;
import com.primeradiants.oniri.novent.dto.AdminNoventPostInput;
import com.primeradiants.oniri.novent.dto.AdminNoventPostOutput;

/**
 * REST endpoints to administrate ONIRI novents
 * @author Shanira
 * @since 0.1.1
 */
@RestController
@RequestMapping("/admin/api")
public class AdminNoventRestController {

	@Autowired private NoventManager noventManager;
	
	private final static String ID = "id";
	private final static String TITLE = "title";
	private final static String AUTHORS = "authors";
	private final static String COVER = "cover";
	private final static String NOVENT = "novent";
	
	private final static String TMP_FOLDER = "/tmp/primeradiants/oniri-data/";

	/**
	 * Allow an admin user to add a novent to Oniri
	 * @param cover Cover file of the novent
	 * @param novent Novent file
	 * @param input Input object containing novent data
	 * @return a {@link com.primeradiants.oniri.novent.AdminNoventRestController.AdminNoventPostOutput.NoventPostOutput} object,
	 * 			or a list of {@link com.primeradiants.model.errors.ValidationError} in case of invalid inputs
	 */
	@RequestMapping(value = "/novent", method = RequestMethod.POST)
	public ResponseEntity<?> addNovent(@RequestParam MultipartFile cover, @RequestParam MultipartFile novent, @RequestBody AdminNoventPostInput input) 
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
			
			return ResponseEntity.ok(new AdminNoventPostOutput(noventEntity.getId()));
		} catch (IOException e) {
			e.printStackTrace();
			errors.add(new ValidationError("", "Internal Error, try again or contact your system administrator"));
			return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Allow a user to delete an existing novent
	 * @param id the id of the novent
	 * @return a list of {@link com.primeradiants.model.errors.ValidationError} in case of invalid inputs
	 */
	@RequestMapping(value = "/novent/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> getNovents(@PathVariable(ID) Integer id) 
	{
		final Collection<ValidationError> errors = new ArrayList<ValidationError>();
		NoventEntity novent = validateNoventId(id, errors);
		
		if (!errors.isEmpty())
        {
            return new ResponseEntity<Collection<ValidationError>>(errors, HttpStatus.BAD_REQUEST);
        }
		
		noventManager.deleteNovent(novent);
		
		return ResponseEntity.ok().build();
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
	
	//Checks if id corresponds to an existing novent in database and returns the NoventEntity object
	private NoventEntity validateNoventId(Integer id, Collection<ValidationError> errors) {
		NoventEntity novent = noventManager.getNovent(id);
		
		if(novent == null)
			errors.add(new ValidationError(ID, "Unknown novent with id " + id));
		
		return novent;
	}
}
