package org.gcube.portlets.user.performfishanalytics.server.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gcube.portlets.user.performfishanalytics.server.database.JavaPersistenceHandler;
import org.gcube.portlets.user.performfishanalytics.shared.GenericDao;
import org.gcube.portlets.user.performfishanalytics.shared.exceptions.DatabaseServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class AbstractPersistence.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 11, 2019
 * @param <T> the generic type
 */
public abstract class AbstractPersistence<T extends GenericDao> implements JavaPersistenceHandler<T>{

	protected Logger log = LoggerFactory.getLogger(AbstractPersistence.class);

	protected EntityManagerFactory entityManagerFactory;
	private String tableName;

	/**
	 * Instantiates a new abstract persistence.
	 *
	 * @param factory the factory
	 * @param tableName the table name
	 */
	AbstractPersistence(EntityManagerFactory factory, String tableName){
		this.entityManagerFactory = factory;
		this.tableName = tableName;
	}

	/**
	 * Gets the entity manager factory.
	 *
	 * @return the entity manager factory
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	/**
	 * Creates the new manager.
	 *
	 * @return the entity manager
	 * @throws DatabaseServiceException the database service exception
	 */
	public EntityManager createNewManager() throws DatabaseServiceException{

		try{
			if(entityManagerFactory!=null)
				return entityManagerFactory.createEntityManager();

		}catch (Exception e) {
			log.error("An error occurred in create new entity manager ",e);
			e.printStackTrace();
			throw new DatabaseServiceException("An error occurred in create new entity manager");
		}

		return null;
	}

	/**
	 * Insert.
	 *
	 * @param item the item
	 * @return true, if successful
	 * @throws DatabaseServiceException the database service exception
	 */
	public boolean insert(T item) throws DatabaseServiceException{
		EntityManager em = createNewManager();
		try {

			em.getTransaction().begin();
			em.persist(item);
			em.getTransaction().commit();

		} catch (Exception e) {

			log.error("Error in insert: "+e.getMessage(), e);
		 }
		  finally {
		      if (em.getTransaction().isActive())
		          em.getTransaction().rollback();
		      em.close();
		 }

		return true;
	}


	/**
	 * Update.
	 *
	 * @param item the item
	 * @return the t
	 * @throws DatabaseServiceException the database service exception
	 */
	public T update(T item) throws DatabaseServiceException{
		EntityManager em = createNewManager();
		try {

			em.getTransaction().begin();
			item = em.merge(item);
			em.getTransaction().commit();

		} catch (Exception e) {
			log.error("Error in update: "+e.getMessage(), e);
		 }
		  finally {
		      if (em.getTransaction().isActive())
		          em.getTransaction().rollback();
		      em.close();
		 }

		return item;
	}

	/**
	 * Removes the.
	 *
	 * @param item the item
	 * @param transaction the transaction
	 * @return true, if successful
	 * @throws DatabaseServiceException the database service exception
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
			log.error("Error in remove: "+e.getMessage(), e);
		 }
		  finally {
		      if (em.getTransaction().isActive())
		          em.getTransaction().rollback();
		      em.close();
		 }

		return true;
	}


	/**
	 * Gets the list.
	 *
	 * @return the list
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<T> getList() throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<T> listT = new ArrayList<T>();
		try {
			Query query = em.createQuery("select t from "+tableName+" t");
			listT = query.getResultList();
		} catch (Exception e) {
			log.error("Error during getList for table: " + tableName, e);
			throw new DatabaseServiceException("Error during getList for table: " + tableName);

		} finally {
			em.close();
		}
		return listT;

	}


	/**
	 * Count items.
	 *
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int countItems() throws DatabaseServiceException {
		return getList().size();
	}


	/**
	 * Removes the all.
	 *
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int removeAll() throws DatabaseServiceException {

		EntityManager em = createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM "+tableName).executeUpdate();
			em.getTransaction().commit();
			log.info("DELETED FROM "+tableName+" " + removed +" item/s");

		} catch (Exception e) {
			log.error("Error in removeAll: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}

	/**
	 * Gets the item by primary key.
	 *
	 * @param id the id
	 * @return the item by primary key
	 * @throws DatabaseServiceException the database service exception
	 */
	public T getItemByPrimaryKey(Integer id) throws DatabaseServiceException {
		log.debug("getItemByKey id:  "+id);
		EntityManager em = createNewManager();
		T theObject = null;
		try {
			theObject = (T) em.getReference(theObject.getClass(), id);
		} finally {
			em.close();
		}

		log.debug("Returning row:  "+theObject);
		return theObject;
	}

	/**
	 * Gets the criteria builder.
	 *
	 * @return the criteria builder
	 * @throws DatabaseServiceException the database service exception
	 */
	public CriteriaBuilder getCriteriaBuilder() throws DatabaseServiceException {
		return createNewManager().getCriteriaBuilder();
	}


	/**
	 * Execute criteria query.
	 *
	 * @param criteriaQuery the criteria query
	 * @return the list
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<T> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery) throws DatabaseServiceException{
		EntityManager em = createNewManager();
		List<T> listOBJ = new ArrayList<T>();
		try {
			Query query = em.createQuery(criteriaQuery);
			listOBJ = query.getResultList();
		} finally {
			em.close();
		}
		return listOBJ;
	}


	/**
	 * Gets the list.
	 *
	 * @param filterMap the filter map
	 * @param startIndex the start index. You must pass -1 if you  do not want to use 'startIndex'
	 * @param offset the offset. You must pass -1 if you  do not want to use 'offset'
	 * @return the list
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<T> getList(Map<String, String> filterMap, int startIndex, int offset) throws DatabaseServiceException{

		EntityManager em = createNewManager();
		List<T> listOBJ = new ArrayList<T>();
		try {
			String queryString = "select t from "+tableName+" t";

			if(filterMap!=null && filterMap.size()>0){
				queryString+=" where";
				for (String param : filterMap.keySet()) {
					String value = filterMap.get(param);
					queryString+=" t."+param+"="+"'"+value+"'";
					queryString+="AND";
				}

				queryString = queryString.substring(0, queryString.lastIndexOf("AND"));
			}
			Query query = em.createQuery(queryString);

			if(startIndex>-1)
				query.setFirstResult(startIndex);
			if(offset>-1)
				query.setMaxResults(offset);

			listOBJ = query.getResultList();
		} finally {
			em.close();
		}
		return listOBJ;
	}



	/**
	 * Root from.
	 *
	 * @param cq the cq
	 * @return the root
	 */
	public abstract Root<T> rootFrom(CriteriaQuery<Object> cq);


	/**
	 * Gets the list.
	 *
	 * @param startIndex the start index
	 * @param offset the offset
	 * @return the list
	 * @throws DatabaseServiceException the database service exception
	 */
	public abstract List<T> getList(int startIndex, int offset) throws DatabaseServiceException;


	/**
	 * Execute typed query.
	 *
	 * @param cq the cq
	 * @param startIndex the start index
	 * @param offset the offset
	 * @return the list
	 * @throws DatabaseServiceException the database service exception
	 */
	public abstract List<T> executeTypedQuery(CriteriaQuery<Object> cq, int startIndex, int offset) throws DatabaseServiceException;


	/**
	 * Delete item by id field.
	 *
	 * @param idField the id field
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public abstract int deleteItemByIdField(String idField) throws DatabaseServiceException;


}
