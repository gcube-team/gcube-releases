package org.gcube.portlets.user.speciesdiscovery.server.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gcube.portlets.user.speciesdiscovery.shared.DatabaseServiceException;
import org.gcube.portlets.user.speciesdiscovery.shared.GisLayerJob;


/**
 * The Class GisLayerJobPersistence.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2017
 */
public class GisLayerJobPersistence extends AbstractPersistence<GisLayerJob>{

	/**
	 * Instantiates a new gis layer job persistence.
	 *
	 * @param factory the factory
	 */
	public GisLayerJobPersistence(EntityManagerFactory factory) {
		super(factory);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#removeAll()
	 */
	@Override
	public int removeAll() throws DatabaseServiceException {

		EntityManager em = super.createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM "+GisLayerJob.class.getSimpleName()).executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM "+GisLayerJob.class.getSimpleName()+" " + removed +" items");

		} catch (Exception e) {
			logger.error("Error in removeAll: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#getList()
	 */
	@Override
	public List<GisLayerJob> getList() throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<GisLayerJob> listGisLayerJob = new ArrayList<GisLayerJob>();
		try {
			Query query = em.createQuery("select t from "+GisLayerJob.class.getSimpleName()+ " t");

			listGisLayerJob = query.getResultList();

		} catch (Exception e) {
			logger.error("Error in "+GisLayerJob.class.getSimpleName()+" - getList: " + e.getMessage(), e);

		} finally {
			em.close();
		}
		return listGisLayerJob;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#countItems()
	 */
	@Override
	public int countItems() throws DatabaseServiceException {
		return getList().size();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#getItemByKey(java.lang.Integer)
	 */
	@Override
	public GisLayerJob getItemByKey(Integer id) throws DatabaseServiceException {
		logger.trace("getItemByKey id:  "+id);
		EntityManager em = super.createNewManager();
		GisLayerJob gisLayerJob = null;
		try {
			gisLayerJob = em.getReference(GisLayerJob.class, id);

		} finally {
			em.close();
		}
		if(gisLayerJob!=null)
			logger.trace("getItemByKey return row:  "+gisLayerJob.getId());
		else
			logger.trace("getItemByKey return null");

		//FOR DEBUG
//		System.out.println("getItemByKey return:  "+row );

		return gisLayerJob;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#executeCriteriaQuery(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public List<GisLayerJob> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery) throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<GisLayerJob> listOJ = new ArrayList<GisLayerJob>();
		try {

			Query query = em.createQuery(criteriaQuery);

			listOJ = query.getResultList();
		} finally {
			em.close();
		}

		return listOJ;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#getCriteriaBuilder()
	 */
	@Override
	public CriteriaBuilder getCriteriaBuilder() throws DatabaseServiceException {
		return createNewManager().getCriteriaBuilder();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public Root<GisLayerJob> rootFrom(CriteriaQuery<Object> cq) {
		return cq.from(GisLayerJob.class);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#getList(int, int)
	 */
	@Override
	public List<GisLayerJob> getList(int startIndex, int offset) throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<GisLayerJob> listOJ = new ArrayList<GisLayerJob>();
		try {
			Query query = em.createQuery("select t from "+GisLayerJob.class.getSimpleName()+" t");
			query.setFirstResult(startIndex);
			query.setMaxResults(offset);
			listOJ =  query.getResultList();

		} finally {
			em.close();
		}
		return listOJ;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#getList(java.util.Map, int, int)
	 */
	@Override
	public List<GisLayerJob> getList(Map<String, String> filterMap, int startIndex, int offset) throws DatabaseServiceException{

		EntityManager em = super.createNewManager();
		List<GisLayerJob> listOJ = new ArrayList<GisLayerJob>();
		try {
			String queryString = "select t from "+GisLayerJob.class.getSimpleName()+" t";

			if(filterMap!=null && filterMap.size()>0){
				queryString+=" where ";
				for (String param : filterMap.keySet()) {
					String value = filterMap.get(param);
					queryString+=" t."+param+"="+value;
					queryString+=AND;
				}

				queryString = queryString.substring(0, queryString.lastIndexOf(AND));
			}
			Query query = em.createQuery(queryString);

			if(startIndex>-1)
				query.setFirstResult(startIndex);
			if(offset>-1)
				query.setMaxResults(offset);

			listOJ = query.getResultList();
		} finally {
			em.close();
		}
		return listOJ;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#executeTypedQuery(javax.persistence.criteria.CriteriaQuery, int, int)
	 */
	@Override
	public List<GisLayerJob> executeTypedQuery(CriteriaQuery<Object> cq, int startIndex, int offset) throws DatabaseServiceException {

		EntityManager em = super.createNewManager();
		List<GisLayerJob> listOJ = new ArrayList<GisLayerJob>();
		try {

			TypedQuery typedQuery = em.createQuery(cq);

			if(startIndex>-1)
				typedQuery.setFirstResult(startIndex);
			if(offset>-1)
				typedQuery.setMaxResults(offset);

			listOJ = typedQuery.getResultList();

		} finally {
			em.close();
		}

		return listOJ;

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.AbstractPersistence#deleteItemByIdField(java.lang.String)
	 */
	@Override
	public int deleteItemByIdField(String idField) throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		int removed = 0;

		try {
			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM "+GisLayerJob.class.getSimpleName()+" t WHERE t."+GisLayerJob.ID_FIELD+"='"+idField+"'").executeUpdate();
			em.getTransaction().commit();
			logger.trace("Item "+ idField + " was deleted from "+GisLayerJob.class.getSimpleName());

		} catch (Exception e) {
			logger.error("Error in "+GisLayerJob.class.getSimpleName()+" deleteItemByIdField: " + e.getMessage(), e);
			e.printStackTrace();

		} finally {
			em.close();
		}

		return removed;
	}


	/**
	 * Gets the item by id field.
	 *
	 * @param idField the id field
	 * @return the item by id field
	 * @throws DatabaseServiceException the database service exception
	 */
	public GisLayerJob getItemByIdField(String idField) throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		try {
			Query query = em.createQuery("Select t FROM "+GisLayerJob.class.getSimpleName()+" t WHERE t."+GisLayerJob.ID_FIELD+"='"+idField+"'");
			List<GisLayerJob> listOJ = query.getResultList();

			if(listOJ!=null && listOJ.get(0)!=null)
				return listOJ.get(0);

			return null;

		} catch (Exception e) {
			logger.error("Error in OccurrencesJob deleteItemByIdField: " + e.getMessage(), e);
			throw new DatabaseServiceException();

		} finally {
			if(em!=null)
				em.close();
		}

	}


}