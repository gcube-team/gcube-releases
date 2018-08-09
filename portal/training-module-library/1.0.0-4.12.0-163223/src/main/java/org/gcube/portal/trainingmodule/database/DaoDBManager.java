package org.gcube.portal.trainingmodule.database;

import javax.persistence.EntityManagerFactory;

import org.gcube.portal.trainingmodule.persistence.TrainingProjectPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class DaoDBManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2018
 * @param <T> the generic type
 */
public class DaoDBManager<T> {
	
	/** The logger. */
	public static Logger logger = LoggerFactory.getLogger(DaoDBManager.class);
	
	/** The factory. */
	private EntityManagerFactory factory;
	
	/** The training peristence entity. */
	private TrainingProjectPersistence trainingPeristenceEntity;

	/**
	 * Instantiates a new dao gcube builder report db manager.
	 *
	 * @param emFactory the em factory
	 */
	public DaoDBManager(EntityManagerFactory emFactory) {
		this.factory = emFactory;
	}
	
	/**
	 * Instance release entity.
	 */
	@SuppressWarnings("unchecked")
	public void instanceReleaseEntity(){
		this.trainingPeristenceEntity = new TrainingProjectPersistence(factory);
	}
	
	/**
	 * Gets the dao updater.
	 *
	 * @return the dao updater
	 */
	@SuppressWarnings("unchecked")
	public DaoUpdater<T> getDaoUpdater() {
		return (DaoUpdater<T>) trainingPeristenceEntity;
	}

	/**
	 * Gets the dao viewer.
	 *
	 * @return the dao viewer
	 */
	@SuppressWarnings("unchecked")
	public DaoViewer<T> getDaoViewer() {
		return (DaoViewer<T>) trainingPeristenceEntity;
	}
	
	/**
	 * Gets the java persistence handler.
	 *
	 * @return the java persistence handler
	 */
	@SuppressWarnings("unchecked")
	public JavaPersistenceHandler<T> getJavaPersistenceHandler() {
		return (JavaPersistenceHandler<T>) trainingPeristenceEntity;
	}

	/**
	 * Gets the release persistence entity.
	 *
	 * @return the release persistence entity
	 */
	public TrainingProjectPersistence getReleasePersistenceEntity() {
		return trainingPeristenceEntity;
	}

	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	public EntityManagerFactory getFactory() {
		return factory;
	}
	
}
