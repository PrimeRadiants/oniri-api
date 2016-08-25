package com.primeradiants.oniri.test.user;

import java.util.UUID;

import javax.servlet.Filter;

import org.junit.Assert;
import org.junit.Before;
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
import com.primeradiants.oniri.test.novent.NoventTestUtil;
import com.primeradiants.oniri.user.EmailValidationTokenEntity;
import com.primeradiants.oniri.user.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class AllEmailValidationRestControllerTest {

	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
	
    private final static String ENDPOINT_PATH = "/signUp/";
    
    private String emailValidationToken;
    private EmailValidationTokenEntity tokenEntity;
    
    @Before
    public void initEachTest() {
    	UserTestUtil.cleanEmailValidationTokenTable();
    	NoventTestUtil.cleanUserNoventTable();
    	UserTestUtil.cleanUserTable();
    	NoventTestUtil.cleanNoventTable();
	   	
	   	UserEntity user = UserTestUtil.insertUserInDatabase(UserTestData.USER_USERNAME, UserTestData.USER_EMAIL, UserTestData.USER_PASSWORD, false, false);
	   	emailValidationToken = UUID.randomUUID().toString();
	   	tokenEntity = UserTestUtil.insertEmailValidationTokenInDatabase(user, emailValidationToken);
	   	
        this.mockMvc =  MockMvcBuilders
        		.webAppContextSetup(this.webApplicationContext)
        		.addFilters(springSecurityFilterChain)
        		.build();
    }
    
    @Test
    public void SignUpGetReturns302WhenNotSecured() throws Exception {
    	ResultMatcher redirection = MockMvcResultMatchers.status().is3xxRedirection();
    	
    	MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + emailValidationToken)
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
        		.secure(false);
        		
        this.mockMvc.perform(builder)
                    .andExpect(redirection);
    }
    
    @Test
    public void SignUpGetReturns400WithNonExistingToken() throws Exception {    	
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();
        
    	MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + emailValidationToken + "1")
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
        		.secure(true);
        		
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
    
//    @Test
//    public void SignUpGetReturnsOkWithExistingToken() throws Exception {    	
//    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();
//        
//    	MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + emailValidationToken)
//        		.contentType(MediaType.APPLICATION_JSON_UTF8)
//        		.secure(true);
//        		
//        this.mockMvc.perform(builder)
//                    .andExpect(ok);
//    }
//    
//    @Test
//    public void SignUpGetEnablesUserInDatabase() throws Exception {    	        
//    	MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ENDPOINT_PATH + emailValidationToken)
//        		.contentType(MediaType.APPLICATION_JSON_UTF8)
//        		.secure(true);
//        		
//        this.mockMvc.perform(builder);
//        
//        UserEntity user = UserTestUtil.getUserFromDatabase(tokenEntity.getUser().getUsername());
//        
//        Assert.assertTrue(user.getEnabled());
//    }
}
