package com.primeradiants.oniri.rest;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class NoventCoverServletTest {

	private static Logger logger = LoggerFactory.getLogger(NoventCoverServletTest.class);
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
	private static NoventEntity insertedNovent;
	
	private static final PrepareTestUtils prepareTestUtils = new PrepareTestUtils(); 
	
	@BeforeClass
	public static void initAllTests() {
    	logger.info("======================== Starting NoventCoverServletTest ========================");
    	prepareTestUtils.cleanUserNoventTable();
    	prepareTestUtils.cleanNoventTable();
    	prepareTestUtils.cleanUserTable();
    	insertedNovent = prepareTestUtils.insertTestNovent();
    	prepareTestUtils.insertTestUser();
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

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME + "1", PrepareTestUtils.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void NoventCoverServletReturns400ResponseForInvalidID() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + (insertedNovent.getId() + 1)).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
    
    @Test
    public void NoventCoverServletReturns302WhenNotSecured() throws Exception {
    	ResultMatcher redirection = MockMvcResultMatchers.status().is3xxRedirection();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD)).secure(false);
        this.mockMvc.perform(builder)
                    .andExpect(redirection);
    }
    
    @Test
    public void NoventCoverServletReturnsOkResponseForExistingID() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(ok);
    }
    
    @Test
    public void NoventCoverServletReturnsCoverImage() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/servlet/novent/cover/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD)).secure(true);
        
        byte[] returnedBytes = this.mockMvc.perform(builder).andReturn().getResponse().getContentAsByteArray();
        
        ClassLoader classLoader = getClass().getClassLoader();
        
        File expectedFile = new File(classLoader.getResource(PrepareTestUtils.NOVENT_COVERPATH).getPath());
        byte[] expectedBytes = Files.readAllBytes(expectedFile.toPath());
        
        Assert.assertArrayEquals(expectedBytes, returnedBytes);
    }
    
    @AfterClass
	public static void endingAllTests() {
    	prepareTestUtils.cleanUserNoventTable();
    	prepareTestUtils.cleanNoventTable();
    	prepareTestUtils.cleanUserTable();
    	logger.info("======================== Ending NoventCoverServletTest ========================");
	}
	
}
