package com.primeradiants.oniri.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.novent.NoventManager;
import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

/**
 * Servlets the return novent files
 * @author gbiaux
 * @since 0.1.0
 */
@Controller
@RequestMapping("/servlet")
public class NoventServlet {
	
	@Autowired ServletContext context;
	@Autowired private NoventManager noventManager;
	@Autowired private UserManager userManager;
	
	private static final String ID = "id";

	/**
	 * @api {get} /servlet/novent/cover/:id Get novent cover image
	 * @apiName getNoventCover
	 * @apiGroup Novent
	 * @apiVersion 0.1.0
	 * 
	 * @apiParam {Number} id      Novent unique ID.
	 */
	/**
	 * Get the requested novent cover image
	 * @param id the id of the novent
	 * @param request
	 * @param response
	 */
	@RequestMapping("/novent/cover/{id}")
	public void getNoventCover(@PathVariable(ID) Integer id, HttpServletRequest request, HttpServletResponse response) {
		NoventEntity novent = noventManager.getNovent(id);
		
		if(novent == null) {
			response.setStatus(400);
			return;
		}
			
		File coverFile = new File(novent.getCoverPath());
		
		if(!coverFile.exists()) {
			response.setStatus(400);
			return;
		}
		
		try {
			returnFileAsResponse(coverFile.getName(), coverFile, response);
		} catch (IOException e) {
			response.setStatus(500);
			e.printStackTrace();
		}
	}
	
	 /**
	  * @api {get} /servlet/novent/:id/:filePath Get novent cover image
	  * @apiName getNoventFile
	  * @apiGroup Novent
	  * @apiVersion 0.1.0
	  * 
	  * @apiParam {Number} id      		Novent unique ID.
	  * @apiParam {String} filePath     The path of requested file in novent archive.
	  */
	 /**
	  * Get a file in requested novent archive
	  * @param id the id of the novent
	  * @param request
	  * @param response
	  */
	@RequestMapping("/novent/{id}/**")
	public void getNoventFile(@PathVariable(ID) Integer id, HttpServletRequest request, HttpServletResponse response) {
		NoventEntity novent = noventManager.getNovent(id);
		
		//Check if the novent exists
		if(novent == null) {
			response.setStatus(404);
			return;
		}
		
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = userManager.getUser(currentUser.getUsername());
		
		//Check if the user has purchased the novent
		if(!noventManager.doesUserOwnNovent(user, novent)) {
			response.setStatus(403);
			return;
		}
		
		ZipFile noventFile = validateNoventFile(novent.getNoventPath());
		
		//Is the file a valid novent file
		if(noventFile == null) {
			response.setStatus(404);
			return;
		}
		
		ZipEntry requestedFile = validateRequestedFile(request, id, noventFile);
		
		//Does the requested file in novent archive exists
		if(requestedFile == null) {
			response.setStatus(404);
			return;
		}
		
		try {
			returnZipedFileAsResponse(noventFile.getName(), noventFile, requestedFile, response);
		} catch (IOException e) {
			response.setStatus(404);
			e.printStackTrace();
		}
	}
	
	private ZipFile validateNoventFile(String noventPath) {
		if(noventPath == null)
			return null;
		
		File noventFile = new File(noventPath);
		
		if(!noventFile.exists())
			return null;
		
		try {
			ZipFile noventZip = new ZipFile(noventFile);
			
			return noventZip;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private ZipEntry validateRequestedFile(HttpServletRequest request, int id, ZipFile novent) {
		try {
			URL servletUrl = new URL(request.getRequestURL().toString());
			String path = servletUrl.getPath();
	    	path = path.replace(request.getContextPath() + "/servlet/novent/" + id + "/", "");
	    	
	    	return novent.getEntry(path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Read the given file and sends it as response according to its mime-type
    private void returnFileAsResponse(String fileName, File file, HttpServletResponse resp) throws IOException {
    	
    	String mimeType = getMimeType(fileName);
        
        resp.setContentType(mimeType);
        resp.setHeader("Content-Type", mimeType);
        resp.setContentLength((int) file.length());
        
    	FileInputStream inStream = new FileInputStream(file);
    	
    	OutputStream outStream = resp.getOutputStream();
        
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
         
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
         
        inStream.close();
        outStream.close(); 
    }
	
	private void returnZipedFileAsResponse(String fileName, ZipFile file, ZipEntry zipedFile, HttpServletResponse resp) throws IOException {
    	
    	String mimeType = getMimeType(fileName);
        
        resp.setContentType(mimeType);
        resp.setHeader("Content-Type", mimeType);
        resp.setContentLength((int) zipedFile.getSize());
        
        InputStream inStream = file.getInputStream(zipedFile);
    	
    	OutputStream outStream = resp.getOutputStream();
        
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
         
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
         
        inStream.close();
        outStream.close(); 
    }
	
	private String getMimeType(String fileName) {    	
    	String mimeType = context.getMimeType(fileName);
        if (mimeType == null) {        
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
        
        return mimeType;
    }
}
