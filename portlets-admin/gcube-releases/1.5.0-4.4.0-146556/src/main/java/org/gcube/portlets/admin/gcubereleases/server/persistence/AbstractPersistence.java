package org.gcube.portlets.admin.gcubereleases.server.persistence;

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

import org.gcube.portlets.admin.gcubereleases.server.database.DaoUpdater;
import org.gcube.portlets.admin.gcubereleases.server.database.DaoViewer;
import org.gcube.portlets.admin.gcubereleases.server.database.JavaPersistenceHandler;
import org.gcube.portlets.admin.gcubereleases.server.exception.DatabaseServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AbstractPersistence.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 * @param <T> the generic type
 */
public abstract class AbstractPersistence<T> implements DaoUpdater<T>, DaoViewer<T>, JavaPersistenceHandler<T>{

	protected EntityManagerFactory entityManagerFactory;

	public final String AND = "AND";

	protected Logger logger = LoggerFactory.getLogger(AbstractPersistence.class);

	protected String tableName;
	
	/**
	 * The Enum SQL_ORDER.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 19, 2015
	 */
	public enum SQL_ORDER {ASC, DESC};

	/**
	 * Instantiates a new abstract persistence.
	 *
	 * @param factory the factory
	 * @param tableName the table name
	 */
	AbstractPersistence(EntityManagerFactory factory, String tableName) {
		this.entityManagerFactory = factory;
		this.tableName = tableName;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoUpdater#create(java.lang.Object)
	 */
	public boolean create(T item) throws DatabaseServiceException {
		return insert(item);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoUpdater#insert(java.lang.Object)
	 */
	public boolean insert(T item) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		try {

			em.getTransaction().begin();
			em.persist(item);
			em.getTransaction().commit();

		} catch (Exception e) {

			logger.error("Error in insert: " + e.getMessage(), e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return true;
	};

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoUpdater#update(java.lang.Object)
	 */
	public T update(T item) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		try {

			em.getTransaction().begin();
			item = em.merge(item);
			em.getTransaction().commit();

		} catch (Exception e) {
			logger.error("Error in update: " + e.getMessage(), e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return item;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoUpdater#remove(java.lang.Object, boolean)
	 */
	public boolean remove(T item, boolean transaction) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		try {

			if (transaction) {
				em.getTransaction().begin();
				em.remove(item);
				em.getTransaction().commit();
			} else
				em.remove(item);

		} catch (Exception e) {
			logger.error("Error in remove: " + e.getMessage(), e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoViewer#getRows()
	 */
	public List<T> getRows() throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<T> rows = new ArrayList<T>();
		try {
			Query query = em.createQuery("select t from " + tableName + " t");
			rows = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in " + tableName + " - getRows: " + e.getMessage(), e);
		} finally {
			em.close();
		}
		return rows;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.server.database.DaoViewer#getRowsOrdered(java.lang.String)
	 */
	public List<T> getRowsOrdered(String orderByField, SQL_ORDER order) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<T> rows = new ArrayList<T>();
		 
		try {
			Query query = em.createQuery("select t, t."+orderByField+" FROM " + tableName + " t " + "order by t."+orderByField +" "+order.toString());
			List<Object[]>  resultList = query.getResultList();

			for (Object[] result : resultList)
				rows.add((T) result[0]);
			
		} catch (Exception e) {
			logger.error("Error in " + tableName + " - getRows: " + e.getMessage(),e);
		} finally {
			em.close();
		}
		
		return rows;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoViewer#getRows(int, int)
	 */
	public List<T> getRows(int startIndex, int offset)
			throws DatabaseServiceException {

		EntityManager em = createNewManager();
		List<T> listRows = new ArrayList<T>();
		try {
			Query query = em.createQuery("select t from " + tableName + " t");
			query.setFirstResult(startIndex);
			query.setMaxResults(offset);
			listRows = query.getResultList();

		} catch (Exception e) {
			logger.error(
					"Error in " + tableName + " - getRows: " + e.getMessage(),
					e);
		} finally {
			em.close();
		}
		return listRows;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoViewer#countItems()
	 */
	public int countItems() throws DatabaseServiceException {
		return getRows().size();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoViewer#getRowsFiltered(java.util.Map)
	 */
	public List<T> getRowsFiltered(Map<String, String> andFilterMap) throws DatabaseServiceException {

		EntityManager em = createNewManager();
		List<T> listRows = new ArrayList<T>();
		try {
			String queryString = "select t from " + tableName + " t";

			if (andFilterMap != null && andFilterMap.size() > 0) {
				queryString += " where ";
				for (String param : andFilterMap.keySet()) {
					String value = andFilterMap.get(param);
					queryString += " t." + param + "='" + value+"'";
					queryString += " "+AND;
				}

				queryString = queryString.substring(0,queryString.lastIndexOf(AND));
			}
			
			logger.info("getRowsFiltered: "+queryString);
			Query query = em.createQuery(queryString);

			listRows = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in " + tableName + " - getRowsFiltered: "+ e.getMessage(), e);
		} finally {
			em.close();
		}
		return listRows;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoUpdater#removeAll()
	 */
	public int removeAll() throws DatabaseServiceException {
		EntityManager em = createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM " + tableName).executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM " + tableName + " " + removed + " items");

		} catch (Exception e) {
			logger.error(
					"Error in " + tableName + " - removeAll: " + e.getMessage(),
					e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			em.close();
		}

		return removed;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoViewer#getItemByKey(java.lang.Integer, java.lang.Class)
	 */
	public T getItemByKey(Integer id, Class<T> t)
			throws DatabaseServiceException {
		logger.trace("getItemByKey id:  " + id);
		EntityManager em = createNewManager();
		T row = null;
		try {
			row = em.getReference(t, id);
		} catch (Exception e) {
			logger.error(
					"Error in ResultRow - getItemByKey: " + e.getMessage(), e);
		} finally {
			em.close();
		}

		if (row != null)
			logger.trace("getItemByKey returning row");
		else
			logger.trace("getItemByKey return null");

		// FOR DEBUG
		// System.out.println("getItemByKey return:  "+row );

		return row;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.JavaPersistenceHandler#getCriteriaBuilder()
	 */
	public CriteriaBuilder getCriteriaBuilder() throws DatabaseServiceException {
		return createNewManager().getCriteriaBuilder();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.JavaPersistenceHandler#getEntityManagerFactory()
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.JavaPersistenceHandler#createNewManager()
	 */
	public EntityManager createNewManager() throws DatabaseServiceException {

		try {
			if (entityManagerFactory != null)
				return entityManagerFactory.createEntityManager();

		} catch (Exception e) {
			logger.error("An error occurred in create new entity manager ", e);
			e.printStackTrace();
			throw new DatabaseServiceException(
					"An error occurred in create new entity manager");
		}

		return null;

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.JavaPersistenceHandler#executeCriteriaQuery(javax.persistence.criteria.CriteriaQuery)
	 */
	@SuppressWarnings({ "unchecked" })
	public List<T> executeCriteriaQuery(CriteriaQuery<Object> criteriaQuery)
			throws DatabaseServiceException {

		EntityManager em = createNewManager();
		List<T> listResultRow = new ArrayList<T>();
		try {
			Query query = em.createQuery(criteriaQuery);
			listResultRow = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in executeCriteriaQuery: " + e.getMessage(), e);
		} finally {
			em.close();
		}

		return listResultRow;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.JavaPersistenceHandler#executeTypedQuery(javax.persistence.criteria.CriteriaQuery, int, int)
	 */
	public List<T> executeTypedQuery(CriteriaQuery<Object> cq, int startIndex,
			int offset) throws DatabaseServiceException {

		EntityManager em = createNewManager();
		List<T> listOJ = new ArrayList<T>();
		try {

			TypedQuery<T> typedQuery = (TypedQuery<T>) em.createQuery(cq);

			if (startIndex > -1)
				typedQuery.setFirstResult(startIndex);
			if (offset > -1)
				typedQuery.setMaxResults(offset);

			listOJ = typedQuery.getResultList();

		} catch (Exception e) {
			logger.error("Error in executeTypedQuery: " + e.getMessage(), e);
		} finally {
			em.close();
		}

		return listOJ;
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoUpdater#deleteItemByInternalId(int)
	 */
	public int deleteItemByInternalId(int internalId) throws DatabaseServiceException {
		
		EntityManager em = createNewManager();
		int removed = 0;

		try {
			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM "+tableName+" t WHERE t.internalId =" + internalId).executeUpdate();
			em.getTransaction().commit();
			logger.trace("Item with internal id: " + internalId + " was deleted from "+tableName);

		} catch (Exception e) {
			logger.error("Error in "+tableName+" - deleteItemByInternalId: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.JavaPersistenceHandler#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	public abstract Root<T> rootFrom(CriteriaQuery<Object> cq);

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.database.DaoUpdater#deleteItemByIdField(java.lang.String)
	 */
	public abstract int deleteItemByIdField(String idField) throws DatabaseServiceException;

	/**
	 * Removes the all releations.
	 *
	 * @return the int
	 */
	public abstract int removeAllReleations();
	
	/**
	 * Removes the relations.
	 *
	 * @param item the item
	 * @return the int
	 */
	public abstract int removeRelations(T item);
}
