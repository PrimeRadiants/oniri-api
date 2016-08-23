package com.primeradiants.oniri.test.novent;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import java.util.List;

import javax.servlet.Filter;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
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

import com.primeradiants.hibernate.util.HibernateUtil;
import com.primeradiants.oniri.config.ApplicationConfig;
import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.novent.UserNoventEntity;
import com.primeradiants.oniri.test.user.UserTestData;
import com.primeradiants.oniri.test.user.UserTestUtil;
import com.primeradiants.oniri.user.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class ReaderNoventPostRestControllerTest {
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
	private static NoventEntity insertedNovent;
	private static UserEntity insertedUser;
	
	@BeforeClass
	public static void initAllTests() {
	   	UserTestUtil.cleanUserTable();
	   	insertedUser = UserTestUtil.insertUserInDatabase(UserTestData.USER_USERNAME, UserTestData.USER_EMAIL, UserTestData.USER_PASSWORD, false);
	   	
	   	NoventTestUtil.cleanNoventTable();
	   	NoventTestUtil.cleanUserNoventTable();
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

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId()).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void NoventListReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME + "1", UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void NoventReturnsErrorObjectForInvalidID() throws Exception {
    	JSONArray expectedJson = new JSONArray();
    	JSONObject error = new JSONObject();
    	error.put("field", "id");
    	error.put("error", "Unknown novent with id " + (insertedNovent.getId() + 1));
    	
    	expectedJson.put(error);
    	
    	ResultMatcher jsonError = MockMvcResultMatchers.content().json(expectedJson.toString());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + (insertedNovent.getId() + 1)).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(jsonError);
    }
    
    @Test
    public void NoventReturns302WhenNotSeccured() throws Exception {
    	ResultMatcher redirection = MockMvcResultMatchers.status().is3xxRedirection();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(false);
        this.mockMvc.perform(builder)
                    .andExpect(redirection);
        
        NoventTestUtil.cleanUserNoventTable();
    }
    
    @Test
    public void NoventReturnsOkResponseForExistingID() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder)
                    .andExpect(ok);
        
        NoventTestUtil.cleanUserNoventTable();
    }
    
    @Test
    public void NoventUserLinkIsCreatedWhenPostOnUserEndpoint() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder);
        
        SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
    	Session session = sessionFactory.openSession();
    	session.beginTransaction();
    	
    	Criteria criteria = session.createCriteria(UserNoventEntity.class)
    			.add(Restrictions.eq("user", insertedUser))
    			.add(Restrictions.eq("novent", insertedNovent))
    			.setMaxResults(1);
    	
    	UserNoventEntity userNoventLink = (UserNoventEntity) criteria.uniqueResult();
    	
    	session.getTransaction().commit();
    	session.close();
    	
    	Assert.assertNotNull(userNoventLink);
    }
    
    @Test
    public void LinkBetweenNoventAndUserCanBeCreatedOnlyOneTime() throws Exception {
    	//starting with a clean table
    	NoventTestUtil.cleanUserNoventTable();
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD)).secure(true);
        this.mockMvc.perform(builder);

        //performing request twice
        this.mockMvc.perform(builder);
        
        SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
    	Session session = sessionFactory.openSession();
    	
    	Criteria criteria = session.createCriteria(UserNoventEntity.class)
    			.add(Restrictions.eq("user", insertedUser))
    			.add(Restrictions.eq("novent", insertedNovent));
    	
    	@SuppressWarnings("unchecked")
		List<UserNoventEntity> userNoventLinks = (List<UserNoventEntity>) criteria.list();
    	
    	Assert.assertEquals(1, userNoventLinks.size());
    }
	
    @AfterClass
	public static void endingAllTests() {
    	UserTestUtil.cleanUserTable();
		NoventTestUtil.cleanNoventTable();
	   	NoventTestUtil.cleanUserNoventTable();
	}
}
