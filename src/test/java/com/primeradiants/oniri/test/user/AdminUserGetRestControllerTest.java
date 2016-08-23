package com.primeradiants.oniri.test.user;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import javax.servlet.Filter;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import com.primeradiants.oniri.test.novent.NoventTestData;
import com.primeradiants.oniri.test.novent.NoventTestUtil;
import com.primeradiants.oniri.user.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class AdminUserGetRestControllerTest {
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
    private static UserEntity insertedUser;
    
    private final static String USERNAME = "username";
    private final static String EMAIL = "email";
    private final static String CREATED = "created";
    
    private final static String ENDPOINT_PATH = "/admin/api/user/";
    
    @BeforeClass
	public static void initAllTests() {
	   	UserTestUtil.cleanUserTable();
	   	insertedUser = UserTestUtil.insertUserInDatabase(UserTestData.USER_USERNAME, UserTestData.USER_EMAIL, UserTestData.USER_PASSWORD, false);
	   	UserTestUtil.insertUserInDatabase(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_EMAIL, UserTestData.ADMIN_USER_PASSWORD, true);
	   	
	   	NoventTestUtil.cleanNoventTable();
	   	NoventTestUtil.cleanUserNoventTable();
	   	NoventTestUtil.insertTestNovent(NoventTestData.NOVENT_TITLE, NoventTestData.NOVENT_AUTHORS, NoventTestData.NOVENT_DESCRIPTION, NoventTestUtil.getRessourcePath(NoventTestData.NOVENT_COVERPATH), NoventTestUtil.getRessourcePath(NoventTestData.NOVENT_PATH));
	}
    
	@Before
    public void initEachTest() {
        this.mockMvc =  MockMvcBuilders
        		.webAppContextSetup(this.webApplicationContext)
        		.addFilters(springSecurityFilterChain)
        		.build();
    }
	
	@Test
    public void UserGetListReturns401WhenNotLoggedIn() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + UserTestData.USER_USERNAME).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void UserGetReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + UserTestData.USER_USERNAME).with(httpBasic(UserTestData.USER_USERNAME + "1", UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void UserGetReturns403WithNonAdminUser() throws Exception {
    	ResultMatcher forbiden = MockMvcResultMatchers.status().isForbidden();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + UserTestData.USER_USERNAME).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(forbiden);
    }
	
	@Test
    public void UserGetReturns302WhenNotSecured() throws Exception {
    	ResultMatcher redirection = MockMvcResultMatchers.status().is3xxRedirection();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + UserTestData.USER_USERNAME).with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD)).secure(false);
        this.mockMvc.perform(builder)
                    .andExpect(redirection);
    }
	
	@Test
    public void UserGetReturns400WhenRequestingNonExistingUser() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + UserTestData.USER_USERNAME + "1").with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
	
	@Test
    public void UserGetReturnsOkWhenLoggedInWithExistingAdminUserAndExistingRequestedUser() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + UserTestData.USER_USERNAME).with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(ok);
    }
	
	@Test
    public void UserGetReturnsUtf8Json() throws Exception {
        ResultMatcher json = MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + UserTestData.USER_USERNAME).with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(json);
    }
	
	@Test
    public void UserGetReturnsRequestedUser() throws Exception {
    	JSONObject user = new JSONObject();
    	user.put(USERNAME, UserTestData.USER_USERNAME);
    	user.put(EMAIL, UserTestData.USER_EMAIL);
    	user.put(CREATED, insertedUser.getCreated().getTime());
    	    	
        ResultMatcher jsonMatcher = MockMvcResultMatchers.content().json(user.toString());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + UserTestData.USER_USERNAME).with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(jsonMatcher);
    }
	
	@AfterClass
   	public static void endingAllTests() {
		UserTestUtil.cleanUserTable();
		NoventTestUtil.cleanNoventTable();
	   	NoventTestUtil.cleanUserNoventTable();
   	}
}
