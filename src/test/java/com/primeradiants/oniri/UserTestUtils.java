package com.primeradiants.oniri;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserTestUtils {

	private static Logger logger = LoggerFactory.getLogger(UserTestUtils.class);
	
	public static void cleanUserTable(Session session) {
		Query query = session.createQuery("delete from UserEntity");
		int deletedLines = 0;
		deletedLines = query.executeUpdate();
		logger.info("Table User cleaned : lines deleted " + deletedLines);
	}

}
