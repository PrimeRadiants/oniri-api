package com.primeradiants.oniri.novent;

import java.util.ArrayList;
import java.util.Date;
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
	private static final String ID = "id";
	private static final String NOVENT = "novent"; 
	
	/**
	 * Returns a novent based on id.
	 * @param id the id of the novent
	 * @return the NoventEntity object, or null if the novent cannot be found including null id.
	 */
	public NoventEntity getNovent(Integer id) {
		if(id == null)
			return null;
		
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		Criteria criteria = session
			    .createCriteria(NoventEntity.class)
			    .add(Restrictions.eq(ID, id))
			    .setMaxResults(1);
		
		NoventEntity novent = (NoventEntity) criteria.uniqueResult();
		session.getTransaction().commit();
		
		return novent;
	}
	
	/**
	 * Return all the novents in store
	 * @return a List of  {@link com.primeradiants.oniri.novent.NoventEntity}
	 */
	@SuppressWarnings("unchecked")
	public List<NoventEntity> getAllNovents() {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		Criteria criteria = session
			    .createCriteria(NoventEntity.class);
		
		List<NoventEntity> result = (List<NoventEntity>) criteria.list();
		session.getTransaction().commit();
		
		return result;
	}
	
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
		session.getTransaction().commit();
		
		for(UserNoventEntity userNoventEntity : userNoventEntities)
			result.add(userNoventEntity.getNovent());
		
		return result;
	}
	
	public UserNoventEntity createUserNoventLink(UserEntity user, NoventEntity novent) {
		UserNoventEntity userNoventEntity = new UserNoventEntity(0, user, novent, new Date());
		
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		session.save(userNoventEntity);
		session.getTransaction().commit();
		
		return userNoventEntity;
	}
	
	public boolean doesUserOwnNovent(UserEntity user, NoventEntity novent) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		Criteria criteria = session
			    .createCriteria(UserNoventEntity.class)
			    .add(Restrictions.eq(USER, user))
			    .add(Restrictions.eq(NOVENT, novent))
			    .setMaxResults(1);
		
		UserNoventEntity link = (UserNoventEntity) criteria.uniqueResult();
		session.getTransaction().commit();
		
		return (link != null);
	}
}
