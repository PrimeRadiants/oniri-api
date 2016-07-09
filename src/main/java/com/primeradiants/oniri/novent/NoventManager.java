package com.primeradiants.oniri.novent;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.primeradiants.hibernate.util.HibernateUtil;
import com.primeradiants.oniri.user.UserEntity;

/**
 * Simple novent utility.
 * @author Shanira
 * @since 0.1.0
 */
@Service
public class NoventManager {

	private SessionFactory sessionFactory = HibernateUtil.getSessionAnnotationFactory();
	private static final String USER = "user"; 
	
	/**
	 * Return all the novents of the given user
	 * @param user the user object
	 * @return a List of  {@link com.primeradiants.oniri.novent.NoventEntity}
	 */
	@SuppressWarnings("unchecked")
	public List<NoventEntity> getAllUserNovents(UserEntity user) {
		List<NoventEntity> result = new ArrayList<NoventEntity>();
		
		if(user == null)
			return result;
		
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		Criteria criteria = session
			    .createCriteria(UserNoventEntity.class)
			    .add(Restrictions.eq(USER, user));
		
		List<UserNoventEntity> userNoventEntities = (List<UserNoventEntity>) criteria.list();
		
		for(UserNoventEntity userNoventEntity : userNoventEntities)
			result.add(userNoventEntity.getNovent());
		
		return result;
	}
	
}
