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
import org.gcube.portlets.user.speciesdiscovery.shared.Taxon;


public class TaxonRowPersistence extends AbstractPersistence<Taxon>{

	public TaxonRowPersistence(EntityManagerFactory factory) {
		super(factory);
	}

	@Override
	public int removeAll() throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM Taxon").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETE FROM Taxon " + removed +" items");

		} catch (Exception e) {
			logger.error("Error in removeAll: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}

	@Override
	public List<Taxon> getList() throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		List<Taxon> listTaxon = new ArrayList<Taxon>();
		try {
			Query query = em.createQuery("select t from Taxon t");

			listTaxon = query.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in Taxon - removeAll: " + e.getMessage(), e);

		} finally {
			em.close();
		}
		return listTaxon;
	}

	@Override
	public List<Taxon> getList(int startIndex, int offset) throws DatabaseServiceException {
		
		EntityManager em = super.createNewManager();
		List<Taxon> listTaxon = new ArrayList<Taxon>();
		try {
			Query query = em.createQuery("select t from Taxon t");
			query.setFirstResult(startIndex);
			query.setMaxResults(offset);
			listTaxon = query.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in Taxon - getList: " + e.getMessage(), e);

		} finally {
			em.close();
		}
		return listTaxon;
	}

	@Override
	public int countItems() throws DatabaseServiceException{
		 return getList().size();
	}

	@Override
	public Taxon getItemByKey(Integer id) throws DatabaseServiceException{
		logger.trace("getItemByKey id:  "+id);
		EntityManager em = super.createNewManager();
		Taxon row = null;
		try {
			 row = em.getReference(Taxon.class, id);
	 
		} catch (Exception e) {
			logger.error("Error in Taxon - getItemByKey: " + e.getMessage(), e);

		} finally {
			em.close();
		}
		if(row!=null)
			logger.trace("getItemByKey return row id:  "+row.getId());
		else
			logger.trace("getItemByKey return null");
		
		//FOR DEBUG
//		System.out.println("getItemByKey return:  "+row );
		
		return row;
	}

	@Override
	public List<Taxon> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery) throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		List<Taxon> listTaxon = new ArrayList<Taxon>();
		try {

			Query query = em.createQuery(criteriaQuery);

			listTaxon = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in Taxon - executeCriteriaQuery: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return listTaxon;
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() throws DatabaseServiceException{
		return createNewManager().getCriteriaBuilder();
	}

	@Override
	public Root<Taxon> rootFrom(CriteriaQuery<Object> cq) {
		return cq.from(Taxon.class);
	}

	@Override
	public List<Taxon> getList(Map<String, String> filterMap, int startIndex, int offset) throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		List<Taxon> listTaxon = new ArrayList<Taxon>();
		try {
			String queryString = "select t from Taxon t";
			
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

			listTaxon = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in Taxon - getList: " + e.getMessage(), e);

		} finally {
			em.close();
		}
		return listTaxon;
	}

	@Override
	public List<Taxon> executeTypedQuery(CriteriaQuery<Object> cq, int startIndex, int offset) throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		List<Taxon> listTaxon = new ArrayList<Taxon>();
		try {

			TypedQuery typedQuery =  em.createQuery(cq);
			
			if(startIndex>-1)
				typedQuery.setFirstResult(startIndex);
			if(offset>-1)
				typedQuery.setMaxResults(offset);

			listTaxon = typedQuery.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in Taxon - executeTypedQuery: " + e.getMessage(), e);

		}finally {
			em.close();
		}
		
		return listTaxon;
	}

	@Override
	public int deleteItemByIdField(String idField) throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		int removed = 0;
		
		try {
			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM Taxon t WHERE t."+Taxon.ID_FIELD+"='"+idField+"'").executeUpdate();
			em.getTransaction().commit();
			logger.trace("Item "+ idField + " was deleted from Taxon");
			
		} catch (Exception e) {
			logger.error("Error in Taxon deleteJobById: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}

}
