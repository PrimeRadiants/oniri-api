package com.primeradiants.oniri.rest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import javax.servlet.Filter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
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
public class NoventGetTest {
	
private static Logger logger = LoggerFactory.getLogger(NoventGetTest.class);
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
	private static NoventEntity insertedNovent;
	
	@BeforeClass
	public static void initAllTests() {
    	logger.info("======================== Starting NoventGetTest ========================");
    	PrepareTestUtils.cleanUserNoventTable();
    	PrepareTestUtils.cleanNoventTable();
    	PrepareTestUtils.cleanUserTable();
    	insertedNovent = PrepareTestUtils.insertTestNovent();
    	PrepareTestUtils.insertTestUser();
	}
    
	@Before
    public void initEachTest() {
        this.mockMvc =  MockMvcBuilders
        		.webAppContextSetup(this.webApplicationContext)
        		.addFilters(springSecurityFilterChain)
        		.build();
    }
	
	@Test
    public void NoventListReturns401WhenNotLoggedIn() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId());
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void NoventListReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME + "1", PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void NoventReturns400ResponseForInvalidID() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + (insertedNovent.getId() + 1)).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
    
    @Test
    public void NoventReturnsErrorObjectForInvalidID() throws Exception {
    	JSONArray expectedJson = new JSONArray();
    	JSONObject error = new JSONObject();
    	error.put("field", "id");
    	error.put("error", "Unknown novent with id " + (insertedNovent.getId() + 1));
    	
    	expectedJson.put(error);
    	
    	ResultMatcher jsonError = MockMvcResultMatchers.content().json(expectedJson.toString());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + (insertedNovent.getId() + 1)).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(jsonError);
    }
    
    @Test
    public void NoventReturnsOkResponseForExistingID() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(ok);
    }
    
    @Test
    public void NoventReturnsNoventInDatabase() throws Exception {
    	JSONObject novent = new JSONObject();
    	
    	novent.put("id", insertedNovent.getId());
    	novent.put("title", PrepareTestUtils.NOVENT_TITLE);
    	novent.put("description", PrepareTestUtils.NOVENT_DESCRIPTION);
    	
    	JSONArray authors = new JSONArray();
    	authors.put(PrepareTestUtils.NOVENT_AUTHOR);
    	novent.put("authors", authors);
    	novent.put("publication", insertedNovent.getPublication().getTime());
    	    	
        ResultMatcher noventMatcher = MockMvcResultMatchers.content().json(novent.toString());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(noventMatcher);
    }
    
    @AfterClass
   	public static void endingAllTests() {
    	PrepareTestUtils.cleanUserNoventTable();
       	PrepareTestUtils.cleanNoventTable();
       	PrepareTestUtils.cleanUserTable();
       	logger.info("======================== Ending NoventGetTest ========================");
   	}
}
