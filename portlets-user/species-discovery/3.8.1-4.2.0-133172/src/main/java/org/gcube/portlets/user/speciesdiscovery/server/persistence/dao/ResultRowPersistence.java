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

import org.apache.log4j.Logger;
import org.gcube.portlets.user.speciesdiscovery.shared.DatabaseServiceException;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;

public class ResultRowPersistence extends AbstractPersistence<ResultRow>{

	// Query for a List of objects.
	protected CriteriaBuilder criteriaBuilder;
	protected CriteriaQuery<Object> criteriaQuery;
	protected Root<ResultRow> rootFrom;
	protected Logger logger = Logger.getLogger(ResultRowPersistence.class);
	
	public ResultRowPersistence(EntityManagerFactory factory) throws DatabaseServiceException{
		super(factory);
		criteriaBuilder = super.createNewManager().getCriteriaBuilder();
//		CriteriaQuery<Object> cq = criteriaBuilder.createQuery();
//		Root<ResultRow> rootFrom = cq.from(ResultRow.class);
	}
	
	@Override
	public Root<ResultRow> rootFrom(CriteriaQuery<Object> cq){
		return cq.from(ResultRow.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ResultRow> getList() throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		List<ResultRow> listResultRow = new ArrayList<ResultRow>();
		try {
			Query query = em.createQuery("select t from ResultRow t");

			listResultRow = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in ResultRow - getList: " + e.getMessage(), e);	
		} finally {
			em.close();
		}
		return listResultRow;
	}

	@Override
	public int countItems() throws DatabaseServiceException{
		 return getList().size();
	}

	@SuppressWarnings({ "unchecked"})
	@Override
	public List<ResultRow> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery) throws DatabaseServiceException{

		EntityManager em = super.createNewManager();
		List<ResultRow> listResultRow = new ArrayList<ResultRow>();
		try {

			Query query = em.createQuery(criteriaQuery);

			listResultRow = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in ResultRow - executeCriteriaQuery: " + e.getMessage(), e);	
		}  finally {
			em.close();
		}

		return listResultRow;
	}

	@Override
	public ResultRow getItemByKey(Integer id) throws DatabaseServiceException{
		logger.trace("getItemByKey id:  "+id);
		EntityManager em = super.createNewManager();
		ResultRow row = null;
		try {
			 row = em.getReference(ResultRow.class, id);
	 
		} catch (Exception e) {
			logger.error("Error in ResultRow - getItemByKey: " + e.getMessage(), e);	
		}  finally {
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
	public CriteriaBuilder getCriteriaBuilder() throws DatabaseServiceException{
		return createNewManager().getCriteriaBuilder();
	}

	/**
	 * 
	 * @return
	 */
	public int removeAll() throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM ResultRow").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETE FROM ResultRow " + removed +" items");

		} catch (Exception e) {
			logger.error("Error in ResultRow - removeAll: " + e.getMessage(), e);	
		} finally {
			em.close();
		}

		return removed;
	}

	@Override
	public List<ResultRow> getList(int startIndex, int offset) throws DatabaseServiceException{
		
		EntityManager em = super.createNewManager();
		List<ResultRow> listResultRow = new ArrayList<ResultRow>();
		try {
			Query query = em.createQuery("select t from ResultRow t");
			query.setFirstResult(startIndex);
			query.setMaxResults(offset);
			listResultRow = query.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in ResultRow - getList: " + e.getMessage(), e);	
		} finally {
			em.close();
		}
		return listResultRow;
	}
	
	@Override
	public List<ResultRow> getList(Map<String, String> filterMap, int startIndex, int offset) throws DatabaseServiceException{
		
		EntityManager em = super.createNewManager();
		List<ResultRow> listResultRow = new ArrayList<ResultRow>();
		try {
			String queryString = "select t from ResultRow t";
			
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

			listResultRow = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in ResultRow - getList: " + e.getMessage(), e);	
		} finally {
			em.close();
		}
		return listResultRow;
	}
	
	@Override
	public List<ResultRow> executeTypedQuery(CriteriaQuery<Object> cq, int startIndex, int offset) throws DatabaseServiceException{
		
		EntityManager em = super.createNewManager();
		List<ResultRow> listOJ = new ArrayList<ResultRow>();
		try {

			TypedQuery typedQuery =  em.createQuery(cq);
			
			if(startIndex>-1)
				typedQuery.setFirstResult(startIndex);
			if(offset>-1)
				typedQuery.setMaxResults(offset);

			listOJ = typedQuery.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in ResultRow - executeTypedQuery: " + e.getMessage(), e);	
		}  finally {
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
			removed = em.createQuery("DELETE FROM ResultRow t WHERE t."+ResultRow.ID_FIELD+"='"+idField+"'").executeUpdate();
			em.getTransaction().commit();
			logger.trace("Item "+ idField + " was deleted from ResultRow");
			
		} catch (Exception e) {
			logger.error("Error in ResultRow - deleteJobById: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}
}