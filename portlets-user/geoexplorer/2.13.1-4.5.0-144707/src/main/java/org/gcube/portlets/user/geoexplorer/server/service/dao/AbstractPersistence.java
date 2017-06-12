package org.gcube.portlets.user.geoexplorer.server.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.service.DatabaseServiceException;
import org.gcube.portlets.user.geoexplorer.shared.FetchingElement;
import org.gcube.portlets.user.geoexplorer.shared.GeoResourceParameters;
import org.gcube.portlets.user.geoexplorer.shared.GeoResourceParameters.RESOURCETYPE;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 30, 2013
 *
 * @param <T>
 */

public abstract class AbstractPersistence<T extends FetchingElement> {
	
	protected EntityManagerFactory entityManagerFactory;
	
	public static Logger logger = Logger.getLogger(AbstractPersistence.class);
	
	public AbstractPersistence(EntityManagerFactory factory){
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
	 * @param property
	 * @return
	 * @throws DatabaseServiceException
	 */
	public abstract GeoResourceParameters getLastResourceType(RESOURCETYPE property) throws DatabaseServiceException;
}
