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
import org.gcube.portlets.user.speciesdiscovery.shared.Occurrence;

public class OccurrenceRowPersistence extends AbstractPersistence<Occurrence>{

	protected CriteriaBuilder criteriaBuilder;
	protected CriteriaQuery<Object> criteriaQuery;
	protected Root<Occurrence> rootFrom;
	protected Logger logger = Logger.getLogger(OccurrenceRowPersistence.class);

	public OccurrenceRowPersistence(EntityManagerFactory factory) throws DatabaseServiceException{
		super(factory);
		criteriaBuilder = super.createNewManager().getCriteriaBuilder();
	}

	@Override
	public Root<Occurrence> rootFrom(CriteriaQuery<Object> cq){
		return cq.from(Occurrence.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Occurrence> getList() throws DatabaseServiceException{
		EntityManager em = super.createNewManager();
		List<Occurrence> listResultRow = new ArrayList<Occurrence>();
		try {
			Query query = em.createQuery("select t from Occurrence t");

			listResultRow = query.getResultList();
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
	public List<Occurrence> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery) throws DatabaseServiceException{

		EntityManager em = super.createNewManager();
		List<Occurrence> listResultRow = new ArrayList<Occurrence>();
		try {

			Query query = em.createQuery(criteriaQuery);

			listResultRow = query.getResultList();
		} finally {
			em.close();
		}

		return listResultRow;
	}

	@Override
	public Occurrence getItemByKey(Integer id) throws DatabaseServiceException{
		logger.trace("getItemByKey id:  "+id);
		EntityManager em = super.createNewManager();
		Occurrence row = null;
		try {
			 row = em.getReference(Occurrence.class, id);

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
			removed = em.createQuery("DELETE FROM Occurrence").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM Occurrence " + removed +" items");

		} catch (Exception e) {
			logger.error("Error in removeAll: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}

	@Override
	public List<Occurrence> getList(int startIndex, int offset) throws DatabaseServiceException {

		EntityManager em = super.createNewManager();
		List<Occurrence> listOccurrence = new ArrayList<Occurrence>();
		try {
			Query query = em.createQuery("select t from Occurrence t");
			query.setFirstResult(startIndex);
			query.setMaxResults(offset);
			listOccurrence = query.getResultList();

		} finally {
			em.close();
		}
		return listOccurrence;
	}

	@Override
	public List<Occurrence> getList(Map<String, String> filterMap, int startIndex, int offset) throws DatabaseServiceException{

		EntityManager em = super.createNewManager();
		List<Occurrence> listOccurrence = new ArrayList<Occurrence>();
		try {
			String queryString = "select t from Occurrence t";

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

			listOccurrence = query.getResultList();
		} finally {
			em.close();
		}
		return listOccurrence;
	}


	@Override
	public List<Occurrence> executeTypedQuery(CriteriaQuery<Object> cq, int startIndex, int offset) throws DatabaseServiceException{

		EntityManager em = super.createNewManager();
		List<Occurrence> listOJ = new ArrayList<Occurrence>();
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
			removed = em.createQuery("DELETE FROM Occurrence t WHERE t."+Occurrence.ID_FIELD+"='"+idField+"'").executeUpdate();
			em.getTransaction().commit();
			logger.trace("Item "+ idField + " was deleted from Occurrence");

		} catch (Exception e) {
			logger.error("Error in Occurrence deleteJobById: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}
}