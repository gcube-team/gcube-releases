package org.gcube.portlets.user.performfishanalytics.server.persistence;

import javax.persistence.EntityManagerFactory;

import org.gcube.portlets.user.performfishanalytics.shared.GenericDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.ToString;


/**
 * Gets the persistence entity.
 *
 * @return the persistence entity
 */
@Getter

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@ToString
public class GenericPersistenceDaoBuilder<T extends GenericDao> {

	public static Logger logger = LoggerFactory.getLogger(GenericPersistenceDaoBuilder.class);

	private EntityManagerFactory factory;
	private String tableName;
	private GenericPersistence<T> persistenceEntity;

	/**
	 * Instantiates a new dao generci builder manager.
	 *
	 * @param emFactory the em factory
	 * @param tableName the table name
	 */
	public GenericPersistenceDaoBuilder(EntityManagerFactory emFactory, String tableName) {
		this.factory = emFactory;
		this.tableName = tableName;
		instance();
	}

	/**
	 * Instance.
	 */
	private void instance(){
		this.persistenceEntity = new GenericPersistence<T>(factory, tableName);
	}
	
	public GenericPersistence<T> getPersistenceEntity() {
		return persistenceEntity;
	}

}
