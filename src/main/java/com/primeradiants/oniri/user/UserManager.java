package com.primeradiants.oniri.user;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.primeradiants.hibernate.util.HibernateUtil;

/**
 * Simple user utility.
 * @author Shanira
 * @since 0.1.0
 */
public class UserManager {

	private SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
	private static final String USERNAME = "username"; 
	
	/**
	 * Returns a User based on user name.
	 * @param username the user name of the user
	 * @return the UserEntity object, or null if the user cannot be found including null userName.
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
}
