package com.primeradiants.oniri.test.novent;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import java.io.File;
import java.nio.file.Files;

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

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class ReaderNoventCoverServletTest {
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
	private static NoventEntity insertedNovent;
		
	@BeforeClass
	public static void initAllTests() {
		NoventTestUtil.cleanUserNoventTable();
		
	   	UserTestUtil.cleanUserTable();
	   	UserTestUtil.insertUserInDatabase(UserTestData.USER_USERNAME, UserTestData.USER_EMAIL, UserTestData.USER_PASSWORD, false);
	   	UserTestUtil.insertUserInDatabase(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_EMAIL, UserTestData.ADMIN_USER_PASSWORD, true);
	   	
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
    public void NoventCoverServletReturns401WhenNotLoggedIn() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + insertedNovent.getId()).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void NoventCoverServletReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME + "1", UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void NoventCoverServletReturns400ResponseForInvalidID() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + (insertedNovent.getId() + 1)).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
    
    @Test
    public void NoventCoverServletReturns302WhenNotSecured() throws Exception {
    	ResultMatcher redirection = MockMvcResultMatchers.status().is3xxRedirection();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(false);
        this.mockMvc.perform(builder)
                    .andExpect(redirection);
    }
    
    @Test
    public void NoventCoverServletReturnsOkResponseForExistingID() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(ok);
    }
    
    @Test
    public void NoventCoverServletReturnsCoverImage() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        
        byte[] returnedBytes = this.mockMvc.perform(builder).andReturn().getResponse().getContentAsByteArray();
        
        ClassLoader classLoader = getClass().getClassLoader();
        
        File expectedFile = new File(classLoader.getResource(NoventTestData.NOVENT_COVERPATH).getPath());
        byte[] expectedBytes = Files.readAllBytes(expectedFile.toPath());
        
        Assert.assertArrayEquals(expectedBytes, returnedBytes);
    }
    
    @AfterClass
	public static void endingAllTests() {
    	NoventTestUtil.cleanUserNoventTable();
		UserTestUtil.cleanUserTable();
		NoventTestUtil.cleanNoventTable();
	}
	
}
