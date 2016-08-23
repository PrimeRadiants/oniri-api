package com.primeradiants.oniri.test.novent;

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

public class NoventTestUtil {

	private static Logger logger = LoggerFactory.getLogger(NoventTestUtil.class);
	
	private static SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
	
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
	
	public static NoventEntity insertTestNovent(String title, List<String> authors, String description, String coverPath, String noventPath) {
		NoventEntity novent = new NoventEntity(0, title, authors, description, new Date(), coverPath, noventPath);
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
	
	public static String getRessourcePath(String name) {
		return NoventTestUtil.class
				.getClassLoader()
				.getResource(name)
				.getPath()
				.toString();
	}
}
