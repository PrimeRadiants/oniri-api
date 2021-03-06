package com.primeradiants.oniri.test.novent;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.Filter;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.primeradiants.oniri.config.ApplicationConfig;
import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.test.user.UserTestData;
import com.primeradiants.oniri.test.user.UserTestUtil;
import com.primeradiants.oniri.user.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class ReaderNoventServletTest {
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
	private static NoventEntity insertedNovent;
	private static UserEntity insertedUser;
	
	@BeforeClass
	public static void initAllTests() {
		UserTestUtil.cleanEmailValidationTokenTable();
		NoventTestUtil.cleanUserNoventTable();
		
	   	UserTestUtil.cleanUserTable();
	   	insertedUser = UserTestUtil.insertUserInDatabase(UserTestData.USER_USERNAME, UserTestData.USER_EMAIL, UserTestData.USER_PASSWORD, true, false);
	   	
	   	NoventTestUtil.cleanNoventTable();
	   	insertedNovent = NoventTestUtil.insertTestNovent(NoventTestData.NOVENT_TITLE, NoventTestData.NOVENT_AUTHORS, NoventTestData.NOVENT_DESCRIPTION, NoventTestUtil.getRessourcePath(NoventTestData.NOVENT_COVERPATH), NoventTestUtil.getRessourcePath(NoventTestData.NOVENT_PATH));
	}
    
    @Before
    public void initEachTest() {
        this.mockMvc =  MockMvcBuilders
        		.webAppContextSetup(this.webApplicationContext)
        		.addFilters(springSecurityFilterChain)
        		.build();
    }
    
    @Test
    public void NoventServletReturns401WhenNotLoggedIn() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/" + insertedNovent.getId() + "/index.html").secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void NoventServletReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/" + insertedNovent.getId() + "/index.html").with(httpBasic(UserTestData.USER_USERNAME + "1", UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void NoventServletReturns404ResponseForInvalidID() throws Exception {
    	ResultMatcher notFound = MockMvcResultMatchers.status().isNotFound();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/" + (insertedNovent.getId() + 1) + "/index.html").with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(notFound);
    }
    
    @Test
    public void NoventServletReturns403ResponseForExistingIDButNotOwnedNovent() throws Exception {
    	ResultMatcher forbiden = MockMvcResultMatchers.status().isForbidden();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/" + insertedNovent.getId() + "/index.html").with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        ResultActions result = this.mockMvc.perform(builder);
        
        result.andExpect(forbiden);
    }
    
    @Test
    public void NoventServletReturns302WhenNotSecured() throws Exception {
    	NoventTestUtil.createUserNoventLink(insertedUser, insertedNovent);
    	ResultMatcher redirection = MockMvcResultMatchers.status().is3xxRedirection();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/" + insertedNovent.getId() + "/index.html").with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(false);
        ResultActions result = this.mockMvc.perform(builder);
        
        result.andExpect(redirection);
        
        NoventTestUtil.cleanUserNoventTable();
    }
    
    @Test
    public void NoventServletReturnsOkResponseForExistingID() throws Exception {
    	NoventTestUtil.createUserNoventLink(insertedUser, insertedNovent);
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/" + insertedNovent.getId() + "/index.html").with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        ResultActions result = this.mockMvc.perform(builder);
        
        result.andExpect(ok);
        
        NoventTestUtil.cleanUserNoventTable();
    }
    
    @Test
    public void NoventServletReturnsNoventFile() throws Exception {
    	NoventTestUtil.createUserNoventLink(insertedUser, insertedNovent);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/" + insertedNovent.getId() + "/index.html").with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);

        byte[] returnedBytes = this.mockMvc.perform(builder).andReturn().getResponse().getContentAsByteArray();
        
        ClassLoader classLoader = getClass().getClassLoader();
        
        File expectedFile = new File(classLoader.getResource(NoventTestData.NOVENT_PATH).getPath());
        ZipFile noventZip = new ZipFile(expectedFile);
        ZipEntry entry = noventZip.getEntry("index.html");
        
        InputStream inputStream = noventZip.getInputStream(entry);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead = -1;
         
        while ((bytesRead = inputStream.read(buffer)) != -1) {
        	outputStream.write(buffer, 0, bytesRead);
        }
        
        byte[] expectedBytes = outputStream.toByteArray();
        
        Assert.assertArrayEquals(expectedBytes, returnedBytes);
        
        inputStream.close();
        outputStream.close(); 
        noventZip.close();
        NoventTestUtil.cleanUserNoventTable();
    }
    
    @AfterClass
	public static void endingAllTests() {
    	NoventTestUtil.cleanUserNoventTable();
    	UserTestUtil.cleanUserTable();
		NoventTestUtil.cleanNoventTable();
	}
	
}
