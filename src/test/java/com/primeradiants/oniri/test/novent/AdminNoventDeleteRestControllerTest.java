package com.primeradiants.oniri.test.novent;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

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
public class AdminNoventDeleteRestControllerTest {
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
	
    private static NoventEntity insertedNovent;
    
    private final static String ENDPOINT_PATH = "/admin/api/novent/";
        
	@BeforeClass
	public static void initAllTests() {
		UserTestUtil.cleanEmailValidationTokenTable();
		NoventTestUtil.cleanUserNoventTable();
		
	   	UserTestUtil.cleanUserTable();
	   	UserTestUtil.insertUserInDatabase(UserTestData.USER_USERNAME, UserTestData.USER_EMAIL, UserTestData.USER_PASSWORD, true, false);
	   	UserTestUtil.insertUserInDatabase(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_EMAIL, UserTestData.ADMIN_USER_PASSWORD, true, true);
	   	
	   	NoventTestUtil.cleanNoventTable();
	   	insertedNovent = NoventTestUtil.insertTestNovent(NoventTestData.NOVENT_TITLE, NoventTestData.NOVENT_AUTHORS, NoventTestData.NOVENT_DESCRIPTION, NoventTestUtil.getRessourcePath(NoventTestData.NOVENT_COVERPATH), NoventTestUtil.getRessourcePath(NoventTestData.NOVENT_PATH));
	}
       
	@Before
	public void initEachTest() {
		NoventTestUtil.cleanNoventTable();
	   	insertedNovent = NoventTestUtil.insertTestNovent(NoventTestData.NOVENT_TITLE, NoventTestData.NOVENT_AUTHORS, NoventTestData.NOVENT_DESCRIPTION, NoventTestUtil.getRessourcePath(NoventTestData.NOVENT_COVERPATH), NoventTestUtil.getRessourcePath(NoventTestData.NOVENT_PATH));
	   	
		this.mockMvc =  MockMvcBuilders
			.webAppContextSetup(this.webApplicationContext)
       		.addFilters(springSecurityFilterChain)
       		.build();
	}
	
	@Test
    public void NoventDeleteReturns401WhenNotLoggedIn() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(ENDPOINT_PATH + insertedNovent.getId())
        		.secure(true);
        
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void NoventDeleteReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(ENDPOINT_PATH + insertedNovent.getId())
        		.with(httpBasic(UserTestData.USER_USERNAME + "1", UserTestData.USER_PASSWORD))
        		.secure(true);
        
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void NoventDeleteReturns403WithNonAdminUser() throws Exception {
    	ResultMatcher forbiden = MockMvcResultMatchers.status().isForbidden();
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(ENDPOINT_PATH + insertedNovent.getId())
        		.with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD))
        		.secure(true);
        
        this.mockMvc.perform(builder)
                    .andExpect(forbiden);
    }
	
	@Test
    public void NoventDeleteReturns302WhenNotSecured() throws Exception {
    	ResultMatcher redirection = MockMvcResultMatchers.status().is3xxRedirection();
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(ENDPOINT_PATH + insertedNovent.getId())
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(false);
        
        this.mockMvc.perform(builder)
                    .andExpect(redirection);
    }
	
	@Test
    public void NoventDeleteReturns200WithValidUser() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(ENDPOINT_PATH + insertedNovent.getId())
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true);

        this.mockMvc.perform(builder)
                    .andExpect(ok);
    }
	
	@Test
    public void NoventDeleteDeletesNoventInDatabase() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH)
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true);

        this.mockMvc.perform(builder);
        
        NoventEntity novent = NoventTestUtil.getNoventFromDatabase(insertedNovent.getId());
        
        Assert.assertNull(novent);
    }
	
	@AfterClass
   	public static void endingAllTests() {
		NoventTestUtil.cleanUserNoventTable();
		UserTestUtil.cleanUserTable();
		NoventTestUtil.cleanNoventTable();
   	}
}
