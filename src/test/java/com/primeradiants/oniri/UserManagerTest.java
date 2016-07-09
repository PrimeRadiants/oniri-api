package com.primeradiants.oniri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

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
	
	private static PasswordEncoder passwordEncoder = null;
	private static UserManager userManager = null;
	private static SessionFactory sessionFactory = null;
	private static Session session = null;
	private static Integer userId = null;

	private final static String USERNAME1 = "gabitbol";
	private final static String EMAIL1 = "george.abitbol@prime-radiants.com";
	private final static String PASSWORD1 = "password";
	
	private final static String USERNAME2 = "gbiaux";
	private final static String EMAIL2 = "georges.biaux@prime-radiants.com";
	private final static String PASSWORD2 = "motdepasse";
	
	@BeforeClass
	public static void initAllTests() {
		logger.info("@BEFORECLASS");
		
		passwordEncoder = new BCryptPasswordEncoder();
		userManager = new UserManager(passwordEncoder);
		sessionFactory = HibernateUtil.getSessionAnnotationFactory();
		// Get Session
		session = sessionFactory.openSession();
	}
	
	@Before
	public void initEachTest() {
		logger.info("@BEFORE");
		
		// Clean User table before each test
		// the table should be empty, but we do it to secure the tests.
		UserTestUtils.cleanUserTable(session);
		
		// add the user to test the manager & service
		UserEntity user = new UserEntity(0, USERNAME1, EMAIL1, PASSWORD1, new Date());
		
		session.beginTransaction();
		session.save(user);
		userId = user.getId();
		
		session.getTransaction().commit();
	}
	
	@Test
	public void testCreateUser() {
		UserEntity user = userManager.createUser(USERNAME2, EMAIL2, PASSWORD2);
		
		Assert.assertEquals(USERNAME2, user.getUsername());
		Assert.assertEquals(EMAIL2, user.getEmail());
		Assert.assertTrue(passwordEncoder.matches(PASSWORD2, user.getPassword()));
	}
	
	@Test
	public void testGetExistingUserByUsername() {
		UserEntity user = userManager.getUser(USERNAME1);
		
		Assert.assertTrue(userId == user.getId());
		Assert.assertEquals(USERNAME1, user.getUsername());
		Assert.assertEquals(EMAIL1, user.getEmail());
		Assert.assertEquals(PASSWORD1, user.getPassword());
	}
	
	@Test
	public void testGetNonExistingUserByUsername() {
		UserEntity user = userManager.getUser(USERNAME2);
		
		Assert.assertEquals(null, user);
	}
	
	@Test
	public void testGetUserWithNullUsername() {
		UserEntity user = userManager.getUser(null);
		
		Assert.assertEquals(null, user);
	}
	
	@Test
	public void testGetExistingUserByEmail() {
		UserEntity user = userManager.getUserByEmail(EMAIL1);
		
		Assert.assertTrue(userId == user.getId());
		Assert.assertEquals(USERNAME1, user.getUsername());
		Assert.assertEquals(EMAIL1, user.getEmail());
		Assert.assertEquals(PASSWORD1, user.getPassword());
	}
	
	@Test
	public void testGetNonExistingUserByEmail() {
		UserEntity user = userManager.getUserByEmail(EMAIL2);
		
		Assert.assertEquals(null, user);
	}
	
	@Test
	public void testGetUserWithNullEmail() {
		UserEntity user = userManager.getUserByEmail(null);
		
		Assert.assertEquals(null, user);
	}
	
	@After
	public void resetEachTest() {
		logger.info("@AFTER");
		
		// Clean User table after tests
		UserTestUtils.cleanUserTable(session);
	}
	
	@AfterClass
	public static void resetAllTests() {
		logger.info("@AFTERCLASS");
		sessionFactory.close();
		sessionFactory = null;
	}
}
