package com.primeradiants.oniri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.primeradiants.hibernate.util.HibernateUtil;
import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;

public class UserManagerTest {
   
	private static Logger logger = LoggerFactory.getLogger(UserManagerTest.class);
	
	private static SessionFactory sessionFactory = null;
	private static Session session = null;
	private static Integer userId = null;

	private final static String USERNAME = "gabitbol";
	private final static String EMAIL = "george.abitbol@prime-radiants.com";
	private final static String PASSWORD = "password";
	
	@BeforeClass
	public static void initAllTests() {
		logger.info("@BEFORECLASS");
		
		sessionFactory = HibernateUtil.getSessionAnnotationFactory();
		// Get Session
		session = sessionFactory.openSession();
	}
	
	@Before
	public void initEachTest() {
		logger.info("@BEFORE");
		
		// Clean User table before each test
		// the table should be empty, but we do it to secure the tests.
		Query query = session.createQuery("delete from UserEntity");
		int deletedLines = 0;
		deletedLines = query.executeUpdate();
		logger.info("Table User cleaned : lines deleted " + deletedLines);
		
		// add the user to test the manager & service
		UserEntity user = new UserEntity(0, USERNAME, EMAIL, PASSWORD);
		
		session.beginTransaction();
		session.save(user);
		userId = user.getId();
		
		session.getTransaction().commit();
	}
	
	@Test
	public void testGetUser() {
		UserManager userManager = new UserManager();
		UserEntity user = userManager.getUser(USERNAME);
		
		Assert.assertTrue(userId == user.getId());
		Assert.assertEquals(USERNAME, user.getUsername());
		Assert.assertEquals(EMAIL, user.getEmail());
		Assert.assertEquals(PASSWORD, user.getPassword());
	}
	
	@After
	public void resetEachTest() {
		logger.info("@AFTER");
		
		// Clean User table after tests
		Query query = session.createQuery("delete from UserEntity");
		int deletedLines = 0;
		deletedLines = query.executeUpdate();
		logger.info("Table User cleaned : lines deleted " + deletedLines);
	}
	
	@AfterClass
	public static void resetAllTests() {
		logger.info("@AFTERCLASS");
		sessionFactory.close();
		sessionFactory = null;
	}
}
