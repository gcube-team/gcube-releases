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
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyJob;

public class TaxonomyJobPersistence extends AbstractPersistence<TaxonomyJob>{

	public TaxonomyJobPersistence(EntityManagerFactory factory) {
		super(factory);
	}

	@Override
	public int removeAll() throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM TaxonomyJob").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETE FROM TaxonomyJob " + removed +" items");

		} catch (Exception e) {
			logger.error("Error in removeAll: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}
	
	@Override
	public int deleteItemByIdField(String idField) throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		int removed = 0;
		
		try {
			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM TaxonomyJob t WHERE t."+TaxonomyJob.ID_FIELD+"='"+idField+"'").executeUpdate();
			em.getTransaction().commit();
			logger.trace("Item "+ idField + " was deleted from TaxonomyJob. removed "+ removed + " item" );	
//			System.out.println("Item "+ idField + " was deleted from TaxonomyJob. removed "+ removed + " item" );
			
		} catch (Exception e) {
			logger.error("Error in TaxonomyJob deleteJobById: " + e.getMessage(), e);
			e.printStackTrace();

		} finally {
			em.close();
		}

		return removed;
	}

	@Override
	public List<TaxonomyJob> getList() throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		List<TaxonomyJob> listTaxJob = new ArrayList<TaxonomyJob>();
		try {
			Query query = em.createQuery("select t from TaxonomyJob t");

			listTaxJob = query.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in TaxonomyJob - getList: " + e.getMessage(), e);

		} finally {
			em.close();
		}
		return listTaxJob;
	}

	@Override
	public int countItems() throws DatabaseServiceException{
		return getList().size();
	}

	@Override
	public TaxonomyJob getItemByKey(Integer id) throws DatabaseServiceException{
		logger.trace("getItemByKey id:  "+id);
		EntityManager em = super.createNewManager();
		TaxonomyJob taxJob = null;
		try {
			taxJob = em.getReference(TaxonomyJob.class, id);
	 
		} catch (Exception e) {
			logger.error("Error in TaxonomyJob - getItemByKey: " + e.getMessage(), e);

		} finally {
			em.close();
		}
		if(taxJob!=null)
			logger.trace("getItemByKey return row:  "+taxJob.getId());
		else
			logger.trace("getItemByKey return null");
		
		//FOR DEBUG
//		System.out.println("getItemByKey return:  "+row );
		
		return taxJob;
	}

	@Override
	public List<TaxonomyJob> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery) throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		List<TaxonomyJob> listTaxJob = new ArrayList<TaxonomyJob>();
		try {

			Query query = em.createQuery(criteriaQuery);

			listTaxJob = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in TaxonomyJob - executeCriteriaQuery: " + e.getMessage(), e);

		}  finally {
			em.close();
		}

		return listTaxJob;
	}


	@Override
	public CriteriaBuilder getCriteriaBuilder() throws DatabaseServiceException{
		return createNewManager().getCriteriaBuilder();
	}

	@Override
	public Root<TaxonomyJob> rootFrom(CriteriaQuery<Object> cq) {
		return cq.from(TaxonomyJob.class);
	}

	@Override
	public List<TaxonomyJob> getList(int startIndex, int offset) throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<TaxonomyJob> listTaxJob = new ArrayList<TaxonomyJob>();
		try {
			Query query = em.createQuery("select t from TaxonomyJob t");
			query.setFirstResult(startIndex);
			query.setMaxResults(offset);
			listTaxJob =  query.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in TaxonomyJob - getList: " + e.getMessage(), e);

		}  finally {
			em.close();
		}
		return listTaxJob;
	}
	
	@Override
	public List<TaxonomyJob> getList(Map<String, String> filterMap, int startIndex, int offset) throws DatabaseServiceException {
		
		EntityManager em = super.createNewManager();
		List<TaxonomyJob> listTaxJob = new ArrayList<TaxonomyJob>();
		try {
			String queryString = "select t from TaxonomyJob t";
			
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

			listTaxJob = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in TaxonomyJob - getList: " + e.getMessage(), e);

		}  finally {
			em.close();
		}
		return listTaxJob;
	}
	
	@Override
	public List<TaxonomyJob> executeTypedQuery(CriteriaQuery<Object> cq, int startIndex, int offset) throws DatabaseServiceException {
		
		EntityManager em = super.createNewManager();
		List<TaxonomyJob> listOJ = new ArrayList<TaxonomyJob>();
		try {

			TypedQuery typedQuery = em.createQuery(cq);
			
			if(startIndex>-1)
				typedQuery.setFirstResult(startIndex);
			if(offset>-1)
				typedQuery.setMaxResults(offset);

			listOJ = typedQuery.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in TaxonomyJob - executeTypedQuery: " + e.getMessage(), e);

		}  finally {
			em.close();
		}
		
		return listOJ;

	}

	
}