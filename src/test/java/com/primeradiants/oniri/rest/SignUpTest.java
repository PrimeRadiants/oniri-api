package com.primeradiants.oniri.rest;

import javax.servlet.Filter;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.primeradiants.hibernate.util.HibernateUtil;
import com.primeradiants.oniri.config.ApplicationConfig;
import com.primeradiants.oniri.user.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class SignUpTest {

	private static Logger logger = LoggerFactory.getLogger(SignUpTest.class);
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
    
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    
    private static final String EMPTY_STRING = "";
    
    private static final String VALID_USERNAME = "gbiaux";
    private static final String INVALID_USERNAME_WITH_SPACE = "gabit bol";
    private static final String INVALID_USERNAME_TOO_SHORT = "ga";
    private static final String INVALID_USERNAME_TOO_LONG = "gabitbolgabitbolgabitbolgabitbolgaba";
    
    private static final String VALID_PASSWORD = "abcd123A";
    private static final String INVALID_PASSWORD_MISSING_DIGIT = "abcdadsA";
    private static final String INVALID_PASSWORD_MISSING_UPPERCASE = "abcdads1";
    private static final String INVALID_PASSWORD_MISSING_LOWERCASE = "ABCDEFG1";
    private static final String INVALID_PASSWORD_TOO_SHORT = "A1a";
    private static final String INVALID_PASSWORD_WITH_SPACE = "ABCDa FG1";
    
    private static final String VALID_EMAIL = "georges.biaux@prime-radiants.com";
    private static final String INVALID_EMAIL_MISSING_AT = "georges.biauxprime-radiants.com";
    private static final String INVALID_EMAIL_MISSING_DOMAIN = "georges.biaux@.com";
    private static final String INVALID_EMAIL_MISSING_EXT = "georges.biaux@prime-radiantscom";
    private static final String INVALID_EMAIL_MISSING_LOCAL = "@prime-radiants.com";
    private static final String INVALID_EMAIL_DOT_BEFORE_AT = "georges.@prime-radiants.com";
    private static final String INVALID_EMAIL_TWO_DOTS = "georges..biaux@prime-radiants.com";
    
    @BeforeClass
	public static void initAllTests() {
    	logger.info("======================== Starting SignUpTest ========================");
	}
    
    @Before
    public void initEachTest() {
    	PrepareTestUtils.cleanUserNoventTable();
    	PrepareTestUtils.cleanNoventTable();
    	PrepareTestUtils.cleanUserTable();
    	
    	PrepareTestUtils.insertTestUser();
    	
        this.mockMvc =  MockMvcBuilders
        		.webAppContextSetup(this.webApplicationContext)
        		.addFilters(springSecurityFilterChain)
        		.build();
    }

    @Test
    public void SignUpReturnsOkWithValidArguments() throws Exception {    	
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        result.andExpect(ok);
    }
    
    @Test
    public void SignUpAndCreateUserInDatabase() throws Exception {    	
        sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        
        SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
        Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		Criteria criteria = session.createCriteria(UserEntity.class)
				.add(Restrictions.eq(USERNAME, VALID_USERNAME))
				.setMaxResults(1);
		
		UserEntity user = (UserEntity) criteria.uniqueResult();
		
		session.getTransaction().commit();
    	session.close();
    	
		Assert.assertNotNull(user);
    }
    
    @Test
    public void SignUpReturns400WithAlreadyExistingUsername() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(PrepareTestUtils.USER_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithEmptyUsername() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(EMPTY_STRING, VALID_PASSWORD, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithNullUsername() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(null, VALID_PASSWORD, VALID_EMAIL);
        result.andExpect(badRequest);
    }
   
    @Test
    public void SignUpReturns400WithUsernameWithSpace() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(INVALID_USERNAME_WITH_SPACE, VALID_PASSWORD, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithTooShortUsername() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(INVALID_USERNAME_TOO_SHORT, VALID_PASSWORD, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithTooLongUsername() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(INVALID_USERNAME_TOO_LONG, VALID_PASSWORD, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithAlreadyEmptyPassword() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, EMPTY_STRING, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithAlreadyNullPassword() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, null, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithPasswordWithMissingDigits() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, INVALID_PASSWORD_MISSING_DIGIT, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithPasswordWithMissingUppercase() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, INVALID_PASSWORD_MISSING_UPPERCASE, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithPasswordWithMissingLowercase() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, INVALID_PASSWORD_MISSING_LOWERCASE, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithTooShortPassword() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, INVALID_PASSWORD_TOO_SHORT, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithPasswordWithSpace() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, INVALID_PASSWORD_WITH_SPACE, VALID_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithAlreadyExistingEmail() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, PrepareTestUtils.USER_EMAIL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithEmptyEmail() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, EMPTY_STRING);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithNullEmail() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, null);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithEmailWithMissingAt() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, INVALID_EMAIL_MISSING_AT);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithEmailWithMissingDomain() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, INVALID_EMAIL_MISSING_DOMAIN);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithEmailWithMissingExtension() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, INVALID_EMAIL_MISSING_EXT);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithEmailWithMissingLocal() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, INVALID_EMAIL_MISSING_LOCAL);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithEmailWithDotBeforeAt() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, INVALID_EMAIL_DOT_BEFORE_AT);
        result.andExpect(badRequest);
    }
    
    @Test
    public void SignUpReturns400WithEmailWithTwoDots() throws Exception {
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
        ResultActions result = sendSignUpRequest(VALID_USERNAME, VALID_PASSWORD, INVALID_EMAIL_TWO_DOTS);
        result.andExpect(badRequest);
    }
    
    @AfterClass
	public static void endingAllTests() {
    	PrepareTestUtils.cleanUserNoventTable();
    	PrepareTestUtils.cleanNoventTable();
    	PrepareTestUtils.cleanUserTable();
    	logger.info("======================== Ending SignUpTest ========================");
	}
    
    private ResultActions sendSignUpRequest(String username, String email, String password) throws Exception {
    	JSONObject request = new JSONObject();
    	request.put(USERNAME, username);
    	request.put(PASSWORD, email);
    	request.put(EMAIL, password);
    	
    	MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/signUp")
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
        		.content(request.toString());
    	
    	return this.mockMvc.perform(builder);
    }
}
