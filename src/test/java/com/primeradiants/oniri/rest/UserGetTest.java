package com.primeradiants.oniri.rest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import javax.servlet.Filter;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import com.primeradiants.oniri.user.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class UserGetTest {

private static Logger logger = LoggerFactory.getLogger(SignUpTest.class);
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
    private static UserEntity insertedUser;
    
    private final static String USERNAME = "username";
    private final static String EMAIL = "email";
    private final static String CREATED = "created";
	
    @BeforeClass
	public static void initAllTests() {
    	logger.info("======================== Starting NoventGetTest ========================");
    	PrepareTestUtils.cleanUserNoventTable();
    	PrepareTestUtils.cleanNoventTable();
    	PrepareTestUtils.cleanUserTable();

    	insertedUser = PrepareTestUtils.insertTestUser();
	}
    
	@Before
    public void initEachTest() {
        this.mockMvc =  MockMvcBuilders
        		.webAppContextSetup(this.webApplicationContext)
        		.addFilters(springSecurityFilterChain)
        		.build();
    }
	
	@Test
    public void UserReturns401WhenNotLoggedIn() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/user");
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void UserReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/user").with(httpBasic(PrepareTestUtils.USER_USERNAME + "1", PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void UserReturnsOkWhenLoggedInWithExistingUser() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/user").with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(ok);
    }
    
    @Test
    public void UserReturnsUtf8Json() throws Exception {
        ResultMatcher json = MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/user").with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(json);
    }
    
    @Test
    public void UserReturnsUserInDatabase() throws Exception {
    	JSONObject expectedJson = new JSONObject();
    	expectedJson.put(USERNAME, PrepareTestUtils.USER_USERNAME);
    	expectedJson.put(EMAIL, PrepareTestUtils.USER_EMAIL);
    	expectedJson.put(CREATED, insertedUser.getCreated().getTime());
    	
        ResultMatcher jsonMatcher = MockMvcResultMatchers.content().json(expectedJson.toString());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/user").with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(jsonMatcher);
    }
	
	@AfterClass
   	public static void endingAllTests() {
    	PrepareTestUtils.cleanUserNoventTable();
       	PrepareTestUtils.cleanNoventTable();
       	PrepareTestUtils.cleanUserTable();
       	logger.info("======================== Ending NoventGetTest ========================");
   	}
}
