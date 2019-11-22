/**
 * 
 */
package org.gcube.portal.trainingmodule.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gcube.portal.trainingmodule.dao.TrainingProject;
import org.gcube.portal.trainingmodule.database.DatabaseServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ReleasePersistence.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class TrainingProjectPersistence extends AbstractPersistence<TrainingProject> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4869244883084783475L;

	/** The Constant tableName. */
	public static final String tableName = TrainingProject.class.getSimpleName();
	
	/** The logger. */
	protected static Logger logger = LoggerFactory.getLogger(TrainingProjectPersistence.class);
	
	//private PackagePersistence packagesPersistence; //REF TO PackagePersistence
	
	/**
	 * Instantiates a new release persistence.
	 *
	 * @param factory the factory
	 */
	public TrainingProjectPersistence(EntityManagerFactory factory) {
		super(factory, tableName);
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.persistence.AbstractPersistence#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public Root<TrainingProject> rootFrom(CriteriaQuery<Object> cq){
		return cq.from(TrainingProject.class);
	}
	

	/**
	 * Gets the training project for internal ID.
	 *
	 * @param internalID the internal ID
	 * @return the training project for internal ID
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<TrainingProject> getTrainingProjectForInternalID(int internalID) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<TrainingProject> rows = new ArrayList<TrainingProject>();
		try {
			Query query = em.createQuery("select p from "+tableName+" p" + " WHERE p.internaId='" + internalID + "'");
			rows = query.getResultList();
		} catch (Exception e) {
			logger.error(
					"Error in " + tableName + " - getRows: " + e.getMessage(),
					e);
		} finally {
			em.close();
		}
		return rows;

	}

	/**
	 * Update training project info.
	 *
	 * @param training the training
	 * @return the training project
	 * @throws DatabaseServiceException the database service exception
	 */
	public TrainingProject updateTrainingProjectInfo(TrainingProject training) throws DatabaseServiceException {
		
		EntityManager em = createNewManager();
		TrainingProject dao = null;
		try {
			
			dao = em.find(TrainingProject.class, training.getInternalId());
			
			//SET NEW DATA
			dao.setTitle(training.getTitle());
			dao.setDescription(training.getDescription());
			dao.setCommitment(training.getCommitment());
			dao.setLanguages(training.getLanguages());
			dao.setWorkspaceFolderName(training.getWorkspaceFolderName());
			dao.setWorkspaceFolderId(training.getWorkspaceFolderId());
			dao.setSharedWith(training.getSharedWith());
			dao.setScope(training.getScope());
			dao.setOwnerLogin(training.getOwnerLogin());
			dao.setCreatedBy(training.getCreatedBy());
			dao.setCourseActive(training.isCourseActive());
			dao = super.update(dao);

		} catch (Exception e) {
			logger.error("Error in updating "+tableName +": "+ e.getMessage(), e);

		} finally {
			em.close();
		}

		return dao;
	}
}
