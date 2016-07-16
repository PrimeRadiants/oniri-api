package com.primeradiants.oniri.rest;

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

import com.primeradiants.hibernate.util.HibernateUtil;
import com.primeradiants.oniri.config.ApplicationConfig;
import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.novent.UserNoventEntity;
import com.primeradiants.oniri.user.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class NoventPostTest {

	private static Logger logger = LoggerFactory.getLogger(NoventPostTest.class);
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
	private static NoventEntity insertedNovent;
	private static UserEntity insertedUser;
	
	private static final PrepareTestUtils prepareTestUtils = new PrepareTestUtils(); 
	
	@BeforeClass
	public static void initAllTests() {
    	logger.info("======================== Starting NoventPostTest ========================");
    	prepareTestUtils.cleanUserNoventTable();
    	prepareTestUtils.cleanNoventTable();
    	prepareTestUtils.cleanUserTable();
    	insertedNovent = prepareTestUtils.insertTestNovent();
    	insertedUser = prepareTestUtils.insertTestUser();
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

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId());
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
    
    @Test
    public void NoventListReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME + "1", PrepareTestUtils.USER_PASSWORD));
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

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + (insertedNovent.getId() + 1)).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(jsonError);
    }
    
    @Test
    public void NoventReturnsOkResponseForExistingID() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
        this.mockMvc.perform(builder)
                    .andExpect(ok);
        
        prepareTestUtils.cleanUserNoventTable();
    }
    
    @Test
    public void NoventUserLinkIsCreatedWhenPostOnUserEndpoint() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
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
    	prepareTestUtils.cleanUserNoventTable();
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest/api/novent/" + insertedNovent.getId()).with(httpBasic(PrepareTestUtils.USER_USERNAME, PrepareTestUtils.USER_PASSWORD));
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
    	prepareTestUtils.cleanUserNoventTable();
    	prepareTestUtils.cleanNoventTable();
    	prepareTestUtils.cleanUserTable();
    	logger.info("======================== Ending NoventPostTest ========================");
	}
}