package com.primeradiants.oniri.test.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.primeradiants.hibernate.util.HibernateUtil;
import com.primeradiants.oniri.novent.NoventEntity;
import com.primeradiants.oniri.novent.UserNoventEntity;
import com.primeradiants.oniri.user.UserEntity;

public class PrepareTestUtils {

	private static Logger logger = LoggerFactory.getLogger(PrepareTestUtils.class);
	
	private static SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
	
	public final static String USER_USERNAME = "gabitbol";
	public final static String USER_EMAIL = "george.abitbol@prime-radiants.com";
	public final static String USER_PASSWORD = "password";
	
	public final static String ADMIN_USER_USERNAME = "gbiaux";
	public final static String ADMIN_USER_EMAIL = "georges.biaux@prime-radiants.com";
	public final static String ADMIN_USER_PASSWORD = "password";
	
	public final static String NOVENT_TITLE = "Novent title";
	public final static String NOVENT_DESCRIPTION = "Novent description";
	public final static String NOVENT_AUTHOR = "George Abitbol";
	public final static String NOVENT_COVERPATH = "novent/cover.png";
	public final static String NOVENT_PATH = "novent/example.novent";
	
	public void cleanUserTable() {
		logger.info("Cleaning User Table");
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		Query query = session.createQuery("delete from UserEntity");
		int deletedLines = 0;
		deletedLines = query.executeUpdate();
		
		logger.info("User Table cleaned : " + deletedLines + " lines deleted ");
		session.getTransaction().commit();
		session.close();
	}
	
	public void cleanNoventTable() {
		logger.info("Cleaning Novent Table");
				
		Session session = sessionFactory.openSession();
		session.beginTransaction();
				
		//Truncate novent_authors table to avoid foreign key contraint error
		Query query = session.createSQLQuery("delete from novent_authors");
		query.executeUpdate();
		
		query = session.createQuery("delete from NoventEntity");
		int deletedLines = 0;
		deletedLines = query.executeUpdate();
		
		logger.info("Novent Table cleaned : " + deletedLines + " lines deleted ");
		session.getTransaction().commit();
		session.close();
	}
	
	public void cleanUserNoventTable() {
		logger.info("Cleaning UserNovent Table");
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		Query query = session.createQuery("delete from UserNoventEntity");
		int deletedLines = 0;
		deletedLines = query.executeUpdate();
		
		logger.info("UserNovent Table cleaned : " + deletedLines + " lines deleted ");
		session.getTransaction().commit();
		session.close();
	}
	
	public UserEntity insertTestUser() {
		UserEntity user = new UserEntity(0, USER_USERNAME, USER_EMAIL, USER_PASSWORD, new Date(), false);
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		session.save(user);
		session.getTransaction().commit();
		session.close();
		
		session = sessionFactory.openSession();
		//Getting result directly from database
		user = (UserEntity) session.get(UserEntity.class, user.getId());
		
		session.close();
		return user;
	}
	
	public UserEntity insertTestAdminUser() {
		UserEntity user = new UserEntity(0, ADMIN_USER_USERNAME, ADMIN_USER_EMAIL, ADMIN_USER_PASSWORD, new Date(), true);
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		session.save(user);
		session.getTransaction().commit();
		session.close();
		
		session = sessionFactory.openSession();
		//Getting result directly from database
		user = (UserEntity) session.get(UserEntity.class, user.getId());
		
		session.close();
		return user;
	}
	
	public NoventEntity insertTestNovent() {
		ClassLoader classLoader = getClass().getClassLoader();
		
		List<String> authors = new ArrayList<String>();
		authors.add(NOVENT_AUTHOR);
		NoventEntity novent = new NoventEntity(0, NOVENT_TITLE, authors, NOVENT_DESCRIPTION, new Date(), classLoader.getResource(NOVENT_COVERPATH).getPath(), classLoader.getResource(NOVENT_PATH).getPath());
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		session.save(novent);
		session.getTransaction().commit();
		session.close();
		
		session = sessionFactory.openSession();
		//Getting result directly from database
		novent = (NoventEntity) session.get(NoventEntity.class, novent.getId());
		
		session.close();
		return novent;
	}
	
	public UserNoventEntity createUserNoventLink(UserEntity user, NoventEntity novent) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		UserNoventEntity userNoventEntity = new UserNoventEntity(0, user, novent, new Date());
		session.save(userNoventEntity);
		
		session.getTransaction().commit();
		session.close();
		
		return userNoventEntity;
	}
}
