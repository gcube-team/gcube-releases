/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.database;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gcube.portlets.user.performfishanalytics.shared.exceptions.DatabaseServiceException;



/**
 * The Interface JavaPersistenceHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 11, 2019
 * @param <T> the generic type
 */
public interface JavaPersistenceHandler<T> {

	/**
	 * Gets the criteria builder.
	 *
	 * @return the criteria builder
	 * @throws DatabaseServiceException the database service exception
	 */
	public CriteriaBuilder getCriteriaBuilder() throws DatabaseServiceException;

	/**
	 * Gets the entity manager factory.
	 *
	 * @return the entity manager factory
	 */
	public EntityManagerFactory getEntityManagerFactory();

	/**
	 * Creates the new manager.
	 *
	 * @return the entity manager
	 * @throws DatabaseServiceException the database service exception
	 */
	public EntityManager createNewManager() throws DatabaseServiceException;

	/**
	 * Execute criteria query.
	 *
	 * @param criteriaQuery the criteria query
	 * @return the list
	 * @throws DatabaseServiceException the database service exception
	 */
	@SuppressWarnings({ "unchecked" })
	public List<T> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery)
			throws DatabaseServiceException;

	/**
	 * Execute typed query.
	 *
	 * @param cq the cq
	 * @param startIndex the start index
	 * @param offset the offset
	 * @return the list
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<T> executeTypedQuery(CriteriaQuery<Object> cq, int startIndex,
			int offset) throws DatabaseServiceException;

	/**
	 * Root from.
	 *
	 * @param cq the cq
	 * @return the root
	 */
	public abstract Root<T> rootFrom(CriteriaQuery<Object> cq);

}
