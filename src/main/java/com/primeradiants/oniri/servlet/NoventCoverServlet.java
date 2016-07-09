package com.primeradiants.oniri.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.novent.NoventManager;

@Controller
@RequestMapping("/servlet")
public class NoventCoverServlet {
	
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
	
	private String getMimeType(String fileName) {    	
    	String mimeType = context.getMimeType(fileName);
        if (mimeType == null) {        
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
        
        return mimeType;
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
}
