package com.primeradiants.oniri.test.novent;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import java.io.File;

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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.test.user.UserTestData;
import com.primeradiants.oniri.test.user.UserTestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class AdminNoventPostRestControllerTest {
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
            
    private final static String ENDPOINT_PATH = "/admin/api/novent/";
    private final static String EMPTY_STRING = "";
    private final static String TITLE = "title";
    private final static String AUTHORS = "authors";
    private final static String DESCRIPTION = "description";
    private final static String COVER = "cover";
    private final static String NOVENT = "novent";
    
    private final static String VALID_TITLE = "Novent Title";
    private final static String VALID_AUTHOR = "gabitbol";
    private final static String VALID_DESCRIPTION = "description";
    
    private final static String COVER_FILE_NAME = "cover.png";
    private final static String NOVENT_FILE_NAME = "example.novent";
	
    @BeforeClass
	public static void initAllTests() {    	
    	UserTestUtil.cleanUserTable();
	   	UserTestUtil.insertUserInDatabase(UserTestData.USER_USERNAME, UserTestData.USER_EMAIL, UserTestData.USER_PASSWORD, false);
	   	UserTestUtil.insertUserInDatabase(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_EMAIL, UserTestData.ADMIN_USER_PASSWORD, true);
	   	
	   	NoventTestUtil.cleanNoventTable();
	   	NoventTestUtil.cleanUserNoventTable();
	   	NoventTestUtil.insertTestNovent(NoventTestData.NOVENT_TITLE, NoventTestData.NOVENT_AUTHORS, NoventTestData.NOVENT_DESCRIPTION, NoventTestUtil.getRessourcePath(NoventTestData.NOVENT_COVERPATH), NoventTestUtil.getRessourcePath(NoventTestData.NOVENT_PATH));
	}
    
	@Before
    public void initEachTest() {
		NoventTestUtil.cleanNoventTable();
        this.mockMvc =  MockMvcBuilders
        		.webAppContextSetup(this.webApplicationContext)
        		.addFilters(springSecurityFilterChain)
        		.build();
    }
	
	@Test
    public void NoventPostReturns401WhenNotLoggedIn() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

    	JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);
        
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void NoventPostReturns401WithNonExistingUser() throws Exception {
    	ResultMatcher unauthorized = MockMvcResultMatchers.status().isUnauthorized();

    	JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.USER_USERNAME + "1", UserTestData.USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);
        
        this.mockMvc.perform(builder)
                    .andExpect(unauthorized);
    }
	
	@Test
    public void NoventPostReturns403WithNonAdminUser() throws Exception {
    	ResultMatcher forbiden = MockMvcResultMatchers.status().isForbidden();

    	JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.USER_USERNAME, UserTestData.USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);
        
        this.mockMvc.perform(builder)
                    .andExpect(forbiden);
    }
	
	@Test
    public void NoventPostReturns302WhenNotSecured() throws Exception {
    	ResultMatcher redirection = MockMvcResultMatchers.status().is3xxRedirection();

    	JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(false)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);
        
        this.mockMvc.perform(builder)
                    .andExpect(redirection);
    }
	
	@Test
    public void NoventPostReturns200WithValidArguments() throws Exception {
		JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        this.mockMvc.perform(builder)
                    .andExpect(ok);
    }
	
	@Test
    public void NoventPostReturns400WithMissingTitle() throws Exception {
		JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
	
	@Test
    public void NoventPostReturns400WithEmptyTitle() throws Exception {
		JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
		request.put(TITLE, EMPTY_STRING);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
	
	@Test
    public void NoventPostReturns400WithMissingAuthors() throws Exception {
		JSONObject request = new JSONObject();
		
		request.put(TITLE, VALID_TITLE);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
	
	@Test
    public void NoventPostReturns400WithMissingCoverFile() throws Exception {
		JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
	
	@Test
    public void NoventPostReturns400WithNonImageCoverFile() throws Exception {
		JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
	
	@Test
    public void NoventPostReturns400WithMissingNoventFile() throws Exception {
		JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
	
	@Test
    public void NoventPostReturns400WithInvalidNoventFile() throws Exception {
		JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
    	ResultMatcher badRequest = MockMvcResultMatchers.status().isBadRequest();

    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        this.mockMvc.perform(builder)
                    .andExpect(badRequest);
    }
	
	@Test
    public void NoventPostReturnsNoventEntityId() throws Exception {
		JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
    	ResultMatcher json = MockMvcResultMatchers.jsonPath("$.id").isNumber();
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        this.mockMvc.perform(builder).andExpect(json);
    }
	
	@Test
    public void NoventPostCreateNoventEntityInDB() throws Exception {
		JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        ResultActions result = this.mockMvc.perform(builder);
        
        JSONObject response = new JSONObject(result.andReturn().getResponse().getContentAsString());
        int noventId = response.getInt("id");
        
        SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
    	Session session = sessionFactory.openSession();
    	
    	Criteria criteria = session.createCriteria(NoventEntity.class)
    			.add(Restrictions.eq("id", noventId))
    			.setMaxResults(1);
    	
    	NoventEntity novent = (NoventEntity) criteria.uniqueResult();
    	
    	Assert.assertNotNull(novent);
    }
	
	@Test
    public void NoventPostSaveNoventFilesOnFileSystem() throws Exception {
		JSONObject request = new JSONObject();
		JSONArray authors = new JSONArray();
		authors.put(VALID_AUTHOR);
		
    	request.put(TITLE, VALID_TITLE);
    	request.put(AUTHORS, authors);
    	request.put(DESCRIPTION, VALID_DESCRIPTION);
    	
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(ENDPOINT_PATH)
        		.file(new MockMultipartFile(COVER, COVER_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_COVERPATH)))
        		.file(new MockMultipartFile(NOVENT, NOVENT_FILE_NAME, "", ClassLoader.getSystemResourceAsStream(NoventTestData.NOVENT_PATH)))
        		.with(httpBasic(UserTestData.ADMIN_USER_USERNAME, UserTestData.ADMIN_USER_PASSWORD))
        		.secure(true)
        		.content(request.toString())
        		.contentType(MediaType.APPLICATION_JSON_UTF8);

        
        ResultActions result = this.mockMvc.perform(builder);
        
        JSONObject response = new JSONObject(result.andReturn().getResponse().getContentAsString());
        int noventId = response.getInt("id");
        
        SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
    	Session session = sessionFactory.openSession();
    	
    	Criteria criteria = session.createCriteria(NoventEntity.class)
    			.add(Restrictions.eq("id", noventId))
    			.setMaxResults(1);
    	
    	NoventEntity novent = (NoventEntity) criteria.uniqueResult();
    	
    	File coverFile = new File(novent.getCoverPath());
    	File noventFile = new File(novent.getNoventPath());
    	
    	Assert.assertTrue(coverFile.exists());
    	Assert.assertTrue(noventFile.exists());
    }
	
	@AfterClass
   	public static void endingAllTests() {
		UserTestUtil.cleanUserTable();
		NoventTestUtil.cleanNoventTable();
	   	NoventTestUtil.cleanUserNoventTable();
   	}
}
