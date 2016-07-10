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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.novent.NoventManager;

@Controller
@RequestMapping("/servlet")
public class NoventServlet {
	
	private static Logger logger = LoggerFactory.getLogger(NoventServlet.class);
	
	@Autowired ServletContext context;
	@Autowired private NoventManager noventManager;
	
	private static final String ID = "id";

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
	
	@RequestMapping("/novent/{id}/**")
	public void getNoventFiles(@PathVariable(ID) Integer id, HttpServletRequest request, HttpServletResponse response) {
		NoventEntity novent = noventManager.getNovent(id);
		
		if(novent == null) {
			response.setStatus(404);
			logger.info("Novent not found.");
			return;
		}
			
		ZipFile noventFile = validateNoventFile(novent.getNoventPath());
		
		if(noventFile == null) {
			response.setStatus(404);
			logger.info("Missing or invalid novent file.");
			return;
		}
		
		ZipEntry requestedFile = validateRequestedFile(request, id, noventFile);
		
		if(requestedFile == null) {
			logger.info("Missing file in novent archive.");
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
