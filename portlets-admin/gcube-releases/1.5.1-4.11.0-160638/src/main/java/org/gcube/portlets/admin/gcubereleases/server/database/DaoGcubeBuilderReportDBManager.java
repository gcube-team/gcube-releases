package org.gcube.portlets.admin.gcubereleases.server.database;

import javax.persistence.EntityManagerFactory;

import org.gcube.portlets.admin.gcubereleases.server.persistence.ReleasePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DaoGcubeBuilderReportDBManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 * @param <T> the generic type
 */
public class DaoGcubeBuilderReportDBManager<T> {
	
	public static Logger logger = LoggerFactory.getLogger(DaoGcubeBuilderReportDBManager.class);
	
	private EntityManagerFactory factory;
	private ReleasePersistence releasePersistenceEntity;

	/**
	 * Instantiates a new dao gcube builder report db manager.
	 */
	public DaoGcubeBuilderReportDBManager(EntityManagerFactory emFactory) {
		this.factory = emFactory;
	}
	
	/**
	 * Instance release entity.
	 */
	@SuppressWarnings("unchecked")
	public void instanceReleaseEntity(){
		this.releasePersistenceEntity = new ReleasePersistence(factory);
	}
	
	/**
	 * Gets the dao updater.
	 *
	 * @return the dao updater
	 */
	@SuppressWarnings("unchecked")
	public DaoUpdater<T> getDaoUpdater() {
		return (DaoUpdater<T>) releasePersistenceEntity;
	}

	/**
	 * Gets the dao viewer.
	 *
	 * @return the dao viewer
	 */
	@SuppressWarnings("unchecked")
	public DaoViewer<T> getDaoViewer() {
		return (DaoViewer<T>) releasePersistenceEntity;
	}
	
	/**
	 * Gets the java persistence handler.
	 *
	 * @return the java persistence handler
	 */
	@SuppressWarnings("unchecked")
	public JavaPersistenceHandler<T> getJavaPersistenceHandler() {
		return (JavaPersistenceHandler<T>) releasePersistenceEntity;
	}

	/**
	 * Gets the release persistence entity.
	 *
	 * @return the release persistence entity
	 */
	public ReleasePersistence getReleasePersistenceEntity() {
		return releasePersistenceEntity;
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
