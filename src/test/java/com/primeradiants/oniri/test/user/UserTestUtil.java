package com.primeradiants.oniri.test.user;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.primeradiants.hibernate.util.HibernateUtil;
import com.primeradiants.oniri.user.EmailValidationTokenEntity;
import com.primeradiants.oniri.user.UserEntity;

public class UserTestUtil {

	private static Logger logger = LoggerFactory.getLogger(UserTestUtil.class);
	
	private static SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
	
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
	
	public static void cleanEmailValidationTokenTable() {
		logger.info("Cleaning EmailValidationToken Table");
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		Query query = session.createQuery("delete from EmailValidationTokenEntity");
		int deletedLines = 0;
		deletedLines = query.executeUpdate();
		
		logger.info("EmailValidationToken Table cleaned : " + deletedLines + " lines deleted ");
		session.getTransaction().commit();
		session.close();
	}
	
	public static UserEntity insertUserInDatabase(String username, String email, String password, boolean idEnabled, boolean isAdmin) {
		UserEntity user = new UserEntity(0, username, email, password, new Date(), idEnabled, isAdmin);
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
	
	public static UserEntity getUserFromDatabase(String username) {
		SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
        Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		Criteria criteria = session.createCriteria(UserEntity.class)
				.add(Restrictions.eq("username", username))
				.setMaxResults(1);
		
		UserEntity user = (UserEntity) criteria.uniqueResult();
		
		session.getTransaction().commit();
    	session.close();
    	
    	return user;
	}
	
	public static EmailValidationTokenEntity insertEmailValidationTokenInDatabase(UserEntity user, String token) {
		EmailValidationTokenEntity tokenEntity = new EmailValidationTokenEntity(0, token, user, new Date());
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		session.save(tokenEntity);
		session.getTransaction().commit();
		session.close();
		
		session = sessionFactory.openSession();
		//Getting result directly from database
		tokenEntity = (EmailValidationTokenEntity) session.get(EmailValidationTokenEntity.class, tokenEntity.getId());
		
		session.close();
		return tokenEntity;
	}
}
