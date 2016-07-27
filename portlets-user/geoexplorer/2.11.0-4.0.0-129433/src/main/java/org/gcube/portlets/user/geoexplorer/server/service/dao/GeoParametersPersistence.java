/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.service.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.gcube.portlets.user.geoexplorer.server.service.DatabaseServiceException;
import org.gcube.portlets.user.geoexplorer.shared.GeoResourceParameters;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 29, 2013
 *
 */
public class GeoParametersPersistence extends AbstractPersistence<GeoResourceParameters>{

	/**
	 * @param factory
	 */
	public GeoParametersPersistence(EntityManagerFactory factory) {
		super(factory);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.service.AbstractPersistence#removeAll()
	 */
	@Override
	public int removeAll() throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM GeoResourceParameters").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETE FROM GeoResourceParameters " + removed +" items");

		} catch (Exception e) {
			logger.error("Error in removeAll: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.service.AbstractPersistence#getList()
	 */
	@Override
	public List<GeoResourceParameters> getList() throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<GeoResourceParameters> list = new ArrayList<GeoResourceParameters>();
		try {
			Query query = em.createQuery("select t from GeoResourceParameters t");

			list = query.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in GeonetworkMetadata - getList: " + e.getMessage(), e);
			
		} finally {
			em.close();
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.service.AbstractPersistence#getList(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<GeoResourceParameters> getList(int startIndex, int offset) throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<GeoResourceParameters> listOJ = new ArrayList<GeoResourceParameters>();
		try {
			Query query = em.createQuery("select t from GeoResourceParameters t");
			query.setFirstResult(startIndex);
			query.setMaxResults(offset);
			listOJ =  query.getResultList();
			
		} finally {
			em.close();
		}
		return listOJ;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.service.AbstractPersistence#countItems()
	 */
	@Override
	public int countItems() throws DatabaseServiceException {
		return getList().size();
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public GeoResourceParameters getLastResourceType(GeoResourceParameters.RESOURCETYPE property) throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<GeoResourceParameters> listOJ = new ArrayList<GeoResourceParameters>();
		try {
			
			CriteriaBuilder queryBuilder =  em.getCriteriaBuilder();
			CriteriaQuery<Object> cq = queryBuilder.createQuery();
			Root<GeoResourceParameters> e = cq.from(GeoResourceParameters.class);
			Predicate pr1 =  queryBuilder.equal(e.get("resourceType"), property);
			cq.where(pr1);
			
		
			
			//ORDER BY WAS TEST AND NOT WORK
//			cq.orderBy(queryBuilder.asc(e.get("creationDate"))); return  listOJ.get(0);
			
			Query query = em.createQuery(cq);
			
			logger.info("Get LastResourceType, property: "+property +" executing query: "+query);
			
			listOJ = query.getResultList();

			if(listOJ.size()==0){
				logger.warn("Resource type: "+property +" not found, throw new DatabaseServiceException");
				throw new DatabaseServiceException("Resource type: "+property +" not found");
			}
			
			return  listOJ.get(listOJ.size()-1); //work around
			
		} finally {
			em.close();
		}
	}
	

}
