package com.primeradiants.oniri.user;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.primeradiants.hibernate.util.HibernateUtil;

/**
 * Simple user utility.
 * @author Shanira
 * @since 0.1.0
 */
@Service
public class UserManager {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
	private static final String USERNAME = "username"; 
	private static final String EMAIL = "email"; 
	
	/**
	 * Creates a new UserEntity and persists it into database
	 * @param username the user name of the new user
	 * @param email the email of the new user
	 * @param password the password of the new user (will be hashed)
	 * @return the newly created UserEntity
	 */
	public UserEntity createUser(String username, String email, String password) {
		UserEntity user = new UserEntity(0, username, email, passwordEncoder.encode(password), new Date());
		
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		session.save(user);
		session.getTransaction().commit();
		
		return user;
	}
	
	/**
	 * Returns a User based on user name.
	 * @param username the user name of the user
	 * @return the UserEntity object, or null if the user cannot be found including null user name.
	 */
	public UserEntity getUser(String username) {
		if(username == null)
			return null;
		
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		Criteria criteria = session
			    .createCriteria(UserEntity.class)
			    .add(Restrictions.eq(USERNAME, username))
			    .setMaxResults(1);
		
		UserEntity user = (UserEntity) criteria.uniqueResult();
		session.getTransaction().commit();
		
		return user;
	}
	
	/**
	 * Returns a User based on email.
	 * @param email the email of the user
	 * @return the UserEntity object, or null if the user cannot be found including null email.
	 */
	public UserEntity getUserByEmail(String email) {
		if(email == null)
			return null;
		
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		Criteria criteria = session
			    .createCriteria(UserEntity.class)
			    .add(Restrictions.eq(EMAIL, email))
			    .setMaxResults(1);
		
		UserEntity user = (UserEntity) criteria.uniqueResult();
		session.getTransaction().commit();
		
		return user;
	}
}
