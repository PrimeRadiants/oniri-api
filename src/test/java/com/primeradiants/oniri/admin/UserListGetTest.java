package com.primeradiants.oniri.admin;

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
import com.primeradiants.oniri.rest.UserGetTest;
import com.primeradiants.oniri.test.utils.PrepareTestUtils;
import com.primeradiants.oniri.user.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class UserListGetTest {

	private static Logger logger = LoggerFactory.getLogger(UserGetTest.class);
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
    private static UserEntity insertedUser;
    private static UserEntity insertedAdminUser;
    
    private final static String USERNAME = "username";
    private final static String EMAIL = "email";
    private final static String CREATED = "created";
    
    private static final PrepareTestUtils prepareTestUtils = new PrepareTestUtils(); 
	
    @BeforeClass
	public static void initAllTests() {
    	logger.info("======================== Starting NoventGetTest ========================");
    	prepareTestUtils.cleanUserNoventTable();
    	prepareTestUtils.cleanNoventTable();
    	prepareTestUtils.cleanUserTable();

    	insertedUser = prepareTestUtils.insertTestUser();
    	insertedAdminUser = prepareTestUtils.insertTestAdminUser();
	}
    
	@Before
    public void initEachTest() {
        this.mockMvc =  MockMvcBuilders
        		.webAppContextSetup(this.webApplicationContext)
        		.addFilters(springSecurityFilterChain)
        		.build();
    }
	
	@Test
    public void UserListReturns401WhenNotLoggedIn() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/api/user/list").secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void UserReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/api/user/list").with(httpBasic(PrepareTestUtils.USER_USERNAME + "1", PrepareTestUtils.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void UserReturns403WithNonAdminUser() throws Exception {
    	ResultMatcher forbiden = MockMvcResultMatchers.status().isForbidden();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/api/user/list").with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(forbiden);
    }
	
	@Test
    public void UserReturns302WhenNotSecured() throws Exception {
    	ResultMatcher redirection = MockMvcResultMatchers.status().is3xxRedirection();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/api/user/list").with(httpBasic(PrepareTestUtils.ADMIN_USER_USERNAME, PrepareTestUtils.ADMIN_USER_PASSWORD)).secure(false);
        this.mockMvc.perform(builder)
                    .andExpect(redirection);
    }
	
	@Test
    public void UserReturnsOkWhenLoggedInWithExistingAdminUser() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/api/user/list").with(httpBasic(PrepareTestUtils.ADMIN_USER_USERNAME, PrepareTestUtils.ADMIN_USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(ok);
    }
	
	@Test
    public void UserReturnsUtf8Json() throws Exception {
        ResultMatcher json = MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/api/user/list").with(httpBasic(PrepareTestUtils.ADMIN_USER_USERNAME, PrepareTestUtils.ADMIN_USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(json);
    }
	
	@Test
    public void UserReturnsUsersInDatabase() throws Exception {
		JSONArray expectedJson = new JSONArray();
    	
    	JSONObject user = new JSONObject();
    	user.put(USERNAME, PrepareTestUtils.USER_USERNAME);
    	user.put(EMAIL, PrepareTestUtils.USER_EMAIL);
    	user.put(CREATED, insertedUser.getCreated().getTime());
    	
    	JSONObject adminUser = new JSONObject();
    	user.put(USERNAME, PrepareTestUtils.ADMIN_USER_USERNAME);
    	user.put(EMAIL, PrepareTestUtils.ADMIN_USER_EMAIL);
    	user.put(CREATED, insertedAdminUser.getCreated().getTime());
    	
    	expectedJson.put(user);
    	expectedJson.put(adminUser);
    	
        ResultMatcher jsonMatcher = MockMvcResultMatchers.content().json(expectedJson.toString());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/api/user/list").with(httpBasic(PrepareTestUtils.ADMIN_USER_USERNAME, PrepareTestUtils.ADMIN_USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(jsonMatcher);
    }
	
	@AfterClass
   	public static void endingAllTests() {
		prepareTestUtils.cleanUserNoventTable();
		prepareTestUtils.cleanNoventTable();
		prepareTestUtils.cleanUserTable();
       	logger.info("======================== Ending NoventGetTest ========================");
   	}
}
