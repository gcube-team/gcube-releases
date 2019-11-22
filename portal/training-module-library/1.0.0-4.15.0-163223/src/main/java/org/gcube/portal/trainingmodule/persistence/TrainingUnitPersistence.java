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
import org.gcube.portal.trainingmodule.dao.TrainingUnit;
import org.gcube.portal.trainingmodule.database.DatabaseServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class TrainingUnitPersistence.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 12, 2018
 */
public class TrainingUnitPersistence extends AbstractPersistence<TrainingUnit> implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4869244883084783475L;

	/** The Constant tableName. */
	public static final String tableName = TrainingUnit.class.getSimpleName();
	
	/** The logger. */
	protected static Logger logger = LoggerFactory.getLogger(TrainingUnitPersistence.class);
	
	/**
	 * Instantiates a new release persistence.
	 *
	 * @param factory the factory
	 */
	public TrainingUnitPersistence(EntityManagerFactory factory) {
		super(factory, tableName);
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.persistence.AbstractPersistence#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public Root<TrainingUnit> rootFrom(CriteriaQuery<Object> cq){
		return cq.from(TrainingUnit.class);
	}

	/**
	 * Gets the training unit for internal ID.
	 *
	 * @param internalID the internal ID
	 * @return the training unit for internal ID
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<TrainingUnit> getTrainingUnitForInternalID(int internalID) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<TrainingUnit> rows = new ArrayList<TrainingUnit>();
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
	 * Update training unit info.
	 *
	 * @param training the training
	 * @return the training unit
	 * @throws DatabaseServiceException the database service exception
	 */
	public TrainingUnit updateTrainingUnitInfo(TrainingUnit training) throws DatabaseServiceException {
		
		EntityManager em = createNewManager();
		TrainingUnit dao = null;
		try {
			
			dao = em.find(TrainingUnit.class, training.getInternalId());
			
			//SET NEW DATA
			dao.setTitle(training.getTitle());
			dao.setDescription(training.getDescription());
			dao.setWorkspaceFolderName(training.getWorkspaceFolderName());
			dao.setWorkspaceFolderId(training.getWorkspaceFolderId());
			dao.setScope(training.getScope());
			dao.setOwnerLogin(training.getOwnerLogin());
			dao = super.update(dao);

		} catch (Exception e) {
			logger.error("Error in updating "+tableName +": "+ e.getMessage(), e);

		} finally {
			em.close();
		}

		return dao;
	}
	
	
	/**
	 * Delete units for training project.
	 *
	 * @param project the project
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deleteUnitsForTrainingProject(TrainingProject project) throws DatabaseServiceException{
		
		EntityManager em = createNewManager();
		int removed = 0;
		try {

			TrainingProject daoProject = em.find(TrainingProject.class, project.getInternalId());
			
			if(daoProject==null)
				throw new Exception(TrainingProject.class.getSimpleName()+ " with id "+project.getInternalId() +" not found");
			
			//REMOVE PACKAGES ONE SHOT
			em.getTransaction().begin();
			Query query = em.createQuery("DELETE FROM "+tableName+" p WHERE p.trainingProjectRef = :project");
			removed = query.setParameter("project", daoProject).executeUpdate();
			em.getTransaction().commit();
			logger.debug("DELETED FROM "+tableName+" " + removed + " items");

		} catch (Exception e) {
			logger.error("Error in Delete Units for project: " +project, e);
			throw new DatabaseServiceException("Error in Delete Units for project: " +project);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}
	

	/**
	 * Gets the list unit for training project.
	 *
	 * @param project the project
	 * @return the list unit for training project
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<TrainingUnit> getListUnitForTrainingProject(TrainingProject project) throws DatabaseServiceException{
		
		EntityManager em = createNewManager();
		try {

			TrainingProject daoRelease = em.find(TrainingProject.class, project.getInternalId());
			
			if(daoRelease==null)
				throw new Exception("TrainingProject with id "+project.getInternalId() +" not found");
			
			Query query = em.createQuery("SELECT p FROM "+tableName+" p WHERE p.trainingProjectRef = :project");
			List<TrainingUnit> list = query.setParameter("project", daoRelease).getResultList();
			return list;

		} catch (Exception e) {
			logger.error("Error in Delete Units for project: " +project, e);
			throw new DatabaseServiceException("Error in Delete Units for project: " +project);
		} finally {
			em.close();
		}
	}
}
