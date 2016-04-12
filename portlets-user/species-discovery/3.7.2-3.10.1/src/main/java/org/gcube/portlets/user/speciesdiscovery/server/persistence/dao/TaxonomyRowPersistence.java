package org.gcube.portlets.user.speciesdiscovery.server.persistence.dao;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;

public class TaxonomyRowPersistence extends AbstractPersistence<TaxonomyRow>{

	public TaxonomyRowPersistence(EntityManagerFactory factory) {
		super(factory);
	}

	@Override
	public int removeAll() throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM TaxonomyRow").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETE FROM TaxonomyRow " + removed +" items");

		} catch (Exception e) {
			logger.error("Error in TaxonomyRow - removeAll: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}

	@Override
	public List<TaxonomyRow> getList() throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		List<TaxonomyRow> listTaxonomy = new ArrayList<TaxonomyRow>();
		try {
			Query query = em.createQuery("select t from TaxonomyRow t");

			listTaxonomy = query.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in TaxonomyRow - getList: " + e.getMessage(), e);

		} finally {
			em.close();
		}
		return listTaxonomy;
	}

	@Override
	public int countItems() throws DatabaseServiceException{
		Map<String, String> filterAndMap = new HashMap<String, String>();
		filterAndMap.put(TaxonomyRow.IS_PARENT, "false");
		return getList(filterAndMap,-1,-1).size();
	}

	@Override
	public TaxonomyRow getItemByKey(Integer id) throws DatabaseServiceException{
		logger.trace("getItemByKey id:  "+id);
		EntityManager em = super.createNewManager();
		TaxonomyRow row = null;
		try {
			 row = em.getReference(TaxonomyRow.class, id);
	 
		}catch (Exception e) {
			logger.error("An error occurred in TaxonomyRow - getItemByKey ",e);
		
		} finally {
			em.close();
		}
		if(row!=null)
			logger.trace("getItemByKey return row:  "+row.getId() + ", service id: " + row.getServiceId());
		else
			logger.trace("getItemByKey return null");
		
		//FOR DEBUG
//		System.out.println("getItemByKey return:  "+row );
		
		return row;
	}

	@Override
	public List<TaxonomyRow> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery) throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		List<TaxonomyRow> listTaxonomyRow = new ArrayList<TaxonomyRow>();
		try {

			Query query = em.createQuery(criteriaQuery);

			listTaxonomyRow = query.getResultList();
		}catch (Exception e) {
			logger.error("An error occurred in TaxonomyRow - executeCriteriaQuery ",e);
		} finally {
			em.close();
		}

		return listTaxonomyRow;
	}


	@Override
	public CriteriaBuilder getCriteriaBuilder() throws DatabaseServiceException{
		return createNewManager().getCriteriaBuilder();
	}

	@Override
	public Root<TaxonomyRow> rootFrom(CriteriaQuery<Object> cq) {
		return cq.from(TaxonomyRow.class);
	}

	@Override
	public List<TaxonomyRow> getList(int startIndex, int offset) throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<TaxonomyRow> listTaxonomyRow = new ArrayList<TaxonomyRow>();
		try {
			Query query = em.createQuery("select t from TaxonomyRow t");
			query.setFirstResult(startIndex);
			query.setMaxResults(offset);
			listTaxonomyRow =  query.getResultList();
			
		}catch (Exception e) {
			logger.error("An error occurred in TaxonomyRow - get List ",e);
		}
		finally {
			em.close();
		}
		return listTaxonomyRow;
	}
	
	@Override
	public List<TaxonomyRow> getList(Map<String, String> filterMap, int startIndex, int offset) throws DatabaseServiceException{
		
		EntityManager em = super.createNewManager();
		List<TaxonomyRow> listTaxonomyRow = new ArrayList<TaxonomyRow>();
		try {
			String queryString = "select t from TaxonomyRow t";
			
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

			listTaxonomyRow = query.getResultList();
		}catch (Exception e) {
			logger.error("An error occurred in TaxonomyRow - get List ",e);
		} finally {
			em.close();
		}
		return listTaxonomyRow;
	}
	
	@Override
	public List<TaxonomyRow> executeTypedQuery(CriteriaQuery cq, int startIndex, int offset) throws DatabaseServiceException{
		
		EntityManager em = super.createNewManager();
		List<TaxonomyRow> listOJ = new ArrayList<TaxonomyRow>();
		try {

			TypedQuery typedQuery =  em.createQuery(cq);
			
			if(startIndex>-1)
				typedQuery.setFirstResult(startIndex);
			if(offset>-1)
				typedQuery.setMaxResults(offset);

			listOJ = typedQuery.getResultList();
		}catch (Exception e) {
			logger.error("An error occurred in TaxonomyRow - executeTypedQuery ",e);
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
			removed = em.createQuery("DELETE FROM TaxonomyRow t WHERE t."+TaxonomyRow.ID_FIELD+"='"+idField+"'").executeUpdate();
			em.getTransaction().commit();
			logger.trace("Item "+ idField + " was deleted from TaxonomyRow");
			
		} catch (Exception e) {
			logger.error("Error in TaxonomyRow deleteJobById: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}

	
}