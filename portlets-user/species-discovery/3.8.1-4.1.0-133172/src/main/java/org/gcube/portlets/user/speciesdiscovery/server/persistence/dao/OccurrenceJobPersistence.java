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
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesJob;

public class OccurrenceJobPersistence extends AbstractPersistence<OccurrencesJob>{

	public OccurrenceJobPersistence(EntityManagerFactory factory) {
		super(factory);
	}

	@Override
	public int removeAll() throws DatabaseServiceException {
		
		EntityManager em = super.createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM OccurrencesJob").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETE FROM OccurrenceJob " + removed +" items");

		} catch (Exception e) {
			logger.error("Error in removeAll: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}

	@Override
	public List<OccurrencesJob> getList() throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<OccurrencesJob> listOccurrencesJob = new ArrayList<OccurrencesJob>();
		try {
			Query query = em.createQuery("select t from OccurrencesJob t");

			listOccurrencesJob = query.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in OccurrencesJob - getList: " + e.getMessage(), e);
			
		} finally {
			em.close();
		}
		return listOccurrencesJob;
	}

	@Override
	public int countItems() throws DatabaseServiceException {
		return getList().size();
	}

	@Override
	public OccurrencesJob getItemByKey(Integer id) throws DatabaseServiceException {
		logger.trace("getItemByKey id:  "+id);
		EntityManager em = super.createNewManager();
		OccurrencesJob occurrencesJob = null;
		try {
			occurrencesJob = em.getReference(OccurrencesJob.class, id);
	 
		} finally {
			em.close();
		}
		if(occurrencesJob!=null)
			logger.trace("getItemByKey return row:  "+occurrencesJob.getId());
		else
			logger.trace("getItemByKey return null");
		
		//FOR DEBUG
//		System.out.println("getItemByKey return:  "+row );
		
		return occurrencesJob;
	}

	@Override
	public List<OccurrencesJob> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery) throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<OccurrencesJob> listOJ = new ArrayList<OccurrencesJob>();
		try {

			Query query = em.createQuery(criteriaQuery);

			listOJ = query.getResultList();
		} finally {
			em.close();
		}

		return listOJ;
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() throws DatabaseServiceException {
		return createNewManager().getCriteriaBuilder();
	}

	@Override
	public Root<OccurrencesJob> rootFrom(CriteriaQuery<Object> cq) {
		return cq.from(OccurrencesJob.class);
	}

	@Override
	public List<OccurrencesJob> getList(int startIndex, int offset) throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<OccurrencesJob> listOJ = new ArrayList<OccurrencesJob>();
		try {
			Query query = em.createQuery("select t from OccurrencesJob t");
			query.setFirstResult(startIndex);
			query.setMaxResults(offset);
			listOJ =  query.getResultList();
			
		} finally {
			em.close();
		}
		return listOJ;
	}
	
	@Override
	public List<OccurrencesJob> getList(Map<String, String> filterMap, int startIndex, int offset) throws DatabaseServiceException{
		
		EntityManager em = super.createNewManager();
		List<OccurrencesJob> listOJ = new ArrayList<OccurrencesJob>();
		try {
			String queryString = "select t from OccurrencesJob t";
			
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

	@Override
	public List<OccurrencesJob> executeTypedQuery(CriteriaQuery<Object> cq, int startIndex, int offset) throws DatabaseServiceException {
		
		EntityManager em = super.createNewManager();
		List<OccurrencesJob> listOJ = new ArrayList<OccurrencesJob>();
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

	@Override
	public int deleteItemByIdField(String idField) throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		int removed = 0;
		
		try {
			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM OccurrencesJob t WHERE t."+OccurrencesJob.ID_FIELD+"='"+idField+"'").executeUpdate();
			em.getTransaction().commit();
			logger.trace("Item "+ idField + " was deleted from OccurrencesJob");
			
		} catch (Exception e) {
			logger.error("Error in OccurrencesJob deleteItemByIdField: " + e.getMessage(), e);
			e.printStackTrace();

		} finally {
			em.close();
		}

		return removed;
	}

	
}