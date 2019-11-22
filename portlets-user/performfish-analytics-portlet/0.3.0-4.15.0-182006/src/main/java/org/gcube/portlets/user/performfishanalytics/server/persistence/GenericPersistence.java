/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.persistence;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gcube.portlets.user.performfishanalytics.shared.GenericDao;
import org.gcube.portlets.user.performfishanalytics.shared.exceptions.DatabaseServiceException;

/**
 * The Class GenericPersistence.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 11, 2019
 * @param <T> the generic type
 */
public class GenericPersistence<T extends GenericDao> extends AbstractPersistence<T>{

	/**
	 * Instantiates a new generic persistence.
	 *
	 * @param factory the factory
	 * @param tableName the table name
	 */
	GenericPersistence(EntityManagerFactory factory, String tableName) {
		super(factory, tableName);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.server.persistence.AbstractPersistence#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public Root rootFrom(CriteriaQuery cq) {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.server.persistence.AbstractPersistence#getList(int, int)
	 */
	@Override
	public List getList(int startIndex, int offset)
		throws DatabaseServiceException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.server.persistence.AbstractPersistence#executeTypedQuery(javax.persistence.criteria.CriteriaQuery, int, int)
	 */
	@Override
	public List executeTypedQuery(CriteriaQuery cq, int startIndex, int offset)
		throws DatabaseServiceException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.server.persistence.AbstractPersistence#deleteItemByIdField(java.lang.String)
	 */
	@Override
	public int deleteItemByIdField(String idField)
		throws DatabaseServiceException {

		// TODO Auto-generated method stub
		return 0;
	}


}
