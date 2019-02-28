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

import org.gcube.portal.trainingmodule.dao.ProgressPerUnit;
import org.gcube.portal.trainingmodule.database.DatabaseServiceException;
//import org.gcube.portal.trainingmodule.shared.TrainingUnitProgressDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class TrainingProgressUnitPersistence.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2018
 */
public class TrainingProgressUnitPersistence extends AbstractPersistence<ProgressPerUnit> implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4869244883084783475L;

	/** The Constant tableName. */
	public static final String tableName = ProgressPerUnit.class.getSimpleName();
	
	/** The logger. */
	protected static Logger logger = LoggerFactory.getLogger(ProgressPerUnit.class);
	
	/**
	 * Instantiates a new release persistence.
	 *
	 * @param factory the factory
	 */
	public TrainingProgressUnitPersistence(EntityManagerFactory factory) {
		super(factory, tableName);
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.persistence.AbstractPersistence#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public Root<ProgressPerUnit> rootFrom(CriteriaQuery<Object> cq){
		return cq.from(ProgressPerUnit.class);
	}

	/**
	 * Gets the training unit for internal ID.
	 *
	 * @param internalID the internal ID
	 * @return the training unit for internal ID
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<ProgressPerUnit> getTrainingUnitForInternalID(int internalID) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<ProgressPerUnit> rows = new ArrayList<ProgressPerUnit>();
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
}
