package com.primeradiants.oniri.rest;

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
	
	public final static String NOVENT_TITLE = "Novent title";
	public final static String NOVENT_DESCRIPTION = "Novent description";
	public final static String NOVENT_AUTHOR = "George Abitbol";
	public final static String NOVENT_COVERPATH = "/path/to/cover.png";
	public final static String NOVENT_PATH = "/path/to/file.novent";
	
	public static void cleanUserTable() {
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
	
	public static void cleanNoventTable() {
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
	
	public static void cleanUserNoventTable() {
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
	
	public static UserEntity insertTestUser() {
		UserEntity user = new UserEntity(0, USER_USERNAME, USER_EMAIL, USER_PASSWORD, new Date());
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
	
	public static NoventEntity insertTestNovent() {
		List<String> authors = new ArrayList<String>();
		authors.add(NOVENT_AUTHOR);
		NoventEntity novent = new NoventEntity(0, NOVENT_TITLE, authors, NOVENT_DESCRIPTION, new Date(), NOVENT_COVERPATH, NOVENT_PATH);
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
	
	public static UserNoventEntity createUserNoventLink(UserEntity user, NoventEntity novent) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		UserNoventEntity userNoventEntity = new UserNoventEntity(0, user, novent, new Date());
		session.save(userNoventEntity);
		
		session.getTransaction().commit();
		session.close();
		
		return userNoventEntity;
	}
}
