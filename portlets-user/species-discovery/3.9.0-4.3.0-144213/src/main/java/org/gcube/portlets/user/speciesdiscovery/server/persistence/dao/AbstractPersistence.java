package org.gcube.portlets.user.speciesdiscovery.server.persistence.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.speciesdiscovery.shared.DatabaseServiceException;

public abstract class AbstractPersistence<T> {
	
	protected EntityManagerFactory entityManagerFactory;
	
	public final String AND = "AND";
	
	protected Logger logger = Logger.getLogger(AbstractPersistence.class);
	
	AbstractPersistence(EntityManagerFactory factory){
		this.entityManagerFactory = factory;
	}
	/**
	 * 
	 * @param item
	 * @return
	 * @throws DatabaseServiceException 
	 */
	public boolean insert(T item) throws DatabaseServiceException{
		EntityManager em = createNewManager();
		try {
			
			em.getTransaction().begin();	
			em.persist(item);
			em.getTransaction().commit();
		
		} catch (Exception e) {
			
			logger.error("Error in insert: "+e.getMessage(), e);
		 }
		  finally {
		      if (em.getTransaction().isActive())
		          em.getTransaction().rollback();
		      em.close();
		 }
		
		return true;
	};
	
	
	/**
	 * 
	 * @param item
	 * @return
	 * @throws DatabaseServiceException 
	 */
	public T update(T item) throws DatabaseServiceException{
		EntityManager em = createNewManager();
		try {

			em.getTransaction().begin();	
			item = em.merge(item);
			em.getTransaction().commit();
			
		} catch (Exception e) {
			logger.error("Error in update: "+e.getMessage(), e);
		 }
		  finally {
		      if (em.getTransaction().isActive())
		          em.getTransaction().rollback();
		      em.close();
		 }
		
		return item;
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 * @throws DatabaseServiceException 
	 */
	public boolean remove(T item, boolean transaction) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		try {

			if(transaction){
				em.getTransaction().begin();
				em.remove(item);
				em.getTransaction().commit();
			}
			else
				em.remove(item);
		
		} catch (Exception e) {
			logger.error("Error in remove: "+e.getMessage(), e);
		 }
		  finally {
		      if (em.getTransaction().isActive())
		          em.getTransaction().rollback();
		      em.close();
		 }
		
		
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract int removeAll() throws DatabaseServiceException;
	
	/**
	 * 
	 * @param filerMap 
	 * @return
	 */
	public abstract List<T> getList() throws DatabaseServiceException;
	
	/**
	 * 
	 * @return
	 */
	public abstract List<T> getList(int startIndex, int offset) throws DatabaseServiceException;
		
	/**
	 * 
	 * @return
	 */
	public abstract int countItems() throws DatabaseServiceException;
	
	/**
	 * 
	 * @param employeeId
	 * @return
	 */
	public abstract T getItemByKey(Integer id) throws DatabaseServiceException;
	/**
	 * 
	 * @param builder
	 * @return
	 */
	public abstract List<T> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery) throws DatabaseServiceException;
	
	
	/**
	 * 
	 * @return
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}
	
	/**
	 * 
	 * @return
	 */
	public EntityManager createNewManager() throws DatabaseServiceException{
		
		try{
			if(entityManagerFactory!=null)
				return entityManagerFactory.createEntityManager();
		
		}catch (Exception e) {
			logger.error("An error occurred in create new entity manager ",e);
			e.printStackTrace();
			throw new DatabaseServiceException("An error occurred in create new entity manager");
		}
		
		return null;
		

	}
	/**
	 * 
	 * @return
	 */
	public abstract CriteriaBuilder getCriteriaBuilder() throws DatabaseServiceException;
	
	
	/**
	 * 
	 * @return
	 */
	public abstract List<T> executeTypedQuery(CriteriaQuery<Object> cq, int startIndex, int offset) throws DatabaseServiceException;
	
	/**
	 * 
	 * @param cq
	 * @return
	 */
	public abstract Root<T> rootFrom(CriteriaQuery<Object> cq) ;
	
	/**
	 * 
	 * @param filterMap
	 * @param startIndex
	 * @param offset
	 * @return
	 */
	public abstract List<T> getList(Map<String, String> filterMap, int startIndex, int offset) throws DatabaseServiceException;
	

	/**
	 * 
	 * @param idField
	 * @return
	 */
	public abstract int deleteItemByIdField(String idField) throws DatabaseServiceException;
	
	
}
