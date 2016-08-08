package com.primeradiants.oniri.novent;

import java.io.File;
import java.io.IOException;
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

import org.apache.commons.io.FileUtils;

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
	 * Persist a new novent in database
	 * @param title The title of the novent
	 * @param authors The author list
	 * @param description The description of the novent
	 * @param coverFile The cover of the novent
	 * @param noventFile The novent archive
	 * @return The {@link com.primeradiants.oniri.novent.NoventEntity}
	 * @throws IOException 
	 */
	public NoventEntity createNoven(String title, List<String> authors, String description, File coverFile, File noventFile) throws IOException {
		NoventEntity noventEntity = new NoventEntity(0, title, authors, description, new Date(), null, null);
		
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		session.save(noventEntity);
		
		File cover = new File("/oniri-data/novents/" + noventEntity.getId() + "/" + coverFile.getName());
		FileUtils.copyFile(coverFile, cover);
		
		File novent = new File("/oniri-data/novents/" + noventEntity.getId() + "/" + noventFile.getName());
		FileUtils.copyFile(noventFile, novent);
		
		noventEntity.setCoverPath(cover.getAbsolutePath());
		noventEntity.setNoventPath(novent.getAbsolutePath());
		
		session.saveOrUpdate(noventEntity);
		session.getTransaction().commit();
		
		return noventEntity;
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
	
	/**
	 * Create a link between a user and a novent he just purchased
	 * @param user The user that will own the novent
	 * @param novent The novent that will be owned
	 * @return The object representing the link between user and novent
	 */
	public UserNoventEntity createUserNoventLink(UserEntity user, NoventEntity novent) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		Criteria criteria = session
			    .createCriteria(UserNoventEntity.class)
			    .add(Restrictions.eq(USER, user))
			    .add(Restrictions.eq(NOVENT, novent))
			    .setMaxResults(1);
		
		UserNoventEntity userNoventEntity = (UserNoventEntity) criteria.uniqueResult();
		
		//Creating link only if it doesn't already exists
		if(userNoventEntity == null) {
			userNoventEntity = new UserNoventEntity(0, user, novent, new Date());
			session.save(userNoventEntity);
		}
		
		session.getTransaction().commit();
		
		return userNoventEntity;
	}
	
	/**
	 * Check if a user own a particular novent
	 * @param user The user
	 * @param novent The novent
	 * @return a boolean value
	 */
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
