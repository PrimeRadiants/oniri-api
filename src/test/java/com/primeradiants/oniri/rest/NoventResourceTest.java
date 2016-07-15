package com.primeradiants.oniri.rest;

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
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.primeradiants.oniri.config.ApplicationConfig;
import com.primeradiants.oniri.novent.NoventEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfig.class)
public class NoventResourceTest {

	private static Logger logger = LoggerFactory.getLogger(NoventResourceTest.class);
	
	@Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
	private static NoventEntity insertedUser;
    
    @BeforeClass
	public static void initAllTests() {
    	logger.info("======================== Starting NoventResourceTest ========================");
    	PrepareTestUtils.cleanNoventTable();
    	insertedUser = PrepareTestUtils.insertTestNovent();
	}
    
    @Before
    public void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.webApplicationContext);
        this.mockMvc = builder.build();
    }
    
    @Test
    public void NoventListReturnsOkResponse() throws Exception {
    	ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/list");
        this.mockMvc.perform(builder)
                    .andExpect(ok);
    }
    
    @Test
    public void NoventListReturnsUtf8Json() throws Exception {
        ResultMatcher json = MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/list");
        this.mockMvc.perform(builder)
                    .andExpect(json);
    }
    
    @Test
    public void NoventListReturnsUserInDatabase() throws Exception {
    	JSONObject expectedJson = new JSONObject();
    	JSONArray novents = new JSONArray();
    	JSONObject novent = new JSONObject();
    	
    	novent.put("id", insertedUser.getId());
    	novent.put("title", PrepareTestUtils.NOVENT_TITLE);
    	
    	JSONArray authors = new JSONArray();
    	authors.put(PrepareTestUtils.NOVENT_AUTHOR);
    	novent.put("authors", authors);
    	novent.put("publication", insertedUser.getPublication().getTime());
    	
    	novents.put(novent);
    	expectedJson.put("novents", novents);
    	
        ResultMatcher user = MockMvcResultMatchers.content().json(expectedJson.toString());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/rest/api/novent/list");
        this.mockMvc.perform(builder)
                    .andExpect(user);
    }
    
    @AfterClass
	public static void endingAllTests() {
    	PrepareTestUtils.cleanNoventTable();
    	logger.info("======================== Ending NoventResourceTest ========================");
	}
}
