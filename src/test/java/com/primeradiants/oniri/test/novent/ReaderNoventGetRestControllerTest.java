package com.primeradiants.oniri.test.novent;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import javax.servlet.Filter;

import org.json.JSONArray;
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
import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.test.user.UserTestData;
import com.primeradiants.oniri.test.user.UserTestUtil;
import com.primeradiants.oniri.user.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class ReaderNoventGetRestControllerTest {
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
	private static NoventEntity insertedNovent;
	private static UserEntity insertedUser;
	
	@BeforeClass
	public static void initAllTests() {
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
    public void NoventListReturns401WhenNotLoggedIn() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void NoventListReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME + "1", UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void NoventReturns400ResponseForInvalidID() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + (insertedNovent.getId() + 1)).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
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

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + (insertedNovent.getId() + 1)).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(jsonError);
    }
    
    @Test
    public void NoventReturns302WhenNotSecured() throws Exception {
    	ResultMatcher redirection = MockMvcResultMatchers.status().is3xxRedirection();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(false);
        this.mockMvc.perform(builder)
                    .andExpect(redirection);
    }
    
    @Test
    public void NoventReturnsOkResponseForExistingID() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(ok);
    }
    
    @Test
    public void NoventListReturnsUtf8Json() throws Exception {
        ResultMatcher json = MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(json);
    }
    
    @Test
    public void NoventReturnsNoventInDatabase() throws Exception {
    	JSONObject novent = new JSONObject();
    	
    	novent.put("id", insertedNovent.getId());
    	novent.put("title", NoventTestData.NOVENT_TITLE);
    	novent.put("description", NoventTestData.NOVENT_DESCRIPTION);
    	
    	JSONArray authors = new JSONArray(NoventTestData.NOVENT_AUTHORS);
    	novent.put("authors", authors);
    	novent.put("publication", insertedNovent.getPublication().getTime());
    	    	
        ResultMatcher noventMatcher = MockMvcResultMatchers.content().json(novent.toString());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(noventMatcher);
    }
    
    @Test
    public void NoventReturnsNoventNotOwnInDatabase() throws Exception {
    	JSONObject novent = new JSONObject();
    	
    	novent.put("id", insertedNovent.getId());
    	novent.put("title", NoventTestData.NOVENT_TITLE);
    	novent.put("description", NoventTestData.NOVENT_DESCRIPTION);
    	
    	JSONArray authors = new JSONArray(NoventTestData.NOVENT_AUTHORS);
    	novent.put("authors", authors);
    	novent.put("publication", insertedNovent.getPublication().getTime());
    	novent.put("userOwn", false);
    	    	
        ResultMatcher noventMatcher = MockMvcResultMatchers.content().json(novent.toString());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(noventMatcher);
    }
    
    @Test
    public void NoventReturnsNoventOwnInDatabase() throws Exception {
    	NoventTestUtil.createUserNoventLink(insertedUser, insertedNovent);
    	JSONObject novent = new JSONObject();
    	
    	novent.put("id", insertedNovent.getId());
    	novent.put("title", NoventTestData.NOVENT_TITLE);
    	novent.put("description", NoventTestData.NOVENT_DESCRIPTION);
    	
    	JSONArray authors = new JSONArray(NoventTestData.NOVENT_AUTHORS);
    	novent.put("authors", authors);
    	novent.put("publication", insertedNovent.getPublication().getTime());
    	novent.put("userOwn", true);
    	    	
        ResultMatcher noventMatcher = MockMvcResultMatchers.content().json(novent.toString());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(noventMatcher);
        
        NoventTestUtil.cleanUserNoventTable();
    }
    
    @AfterClass
   	public static void endingAllTests() {
    	NoventTestUtil.cleanUserNoventTable();
		UserTestUtil.cleanUserTable();
		NoventTestUtil.cleanNoventTable();
   	}
}
