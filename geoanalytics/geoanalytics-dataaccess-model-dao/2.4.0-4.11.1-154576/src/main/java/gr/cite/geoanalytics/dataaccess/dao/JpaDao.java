package gr.cite.geoanalytics.dataaccess.dao;

import gr.cite.geoanalytics.dataaccess.entities.Entity;
import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;

public abstract class JpaDao<T extends Entity, PK extends Serializable> implements Dao<T, PK> {
	protected Class<T> entityClass;

	// protected static EntityManagerFactory entityManagerFactory = null;

	@PersistenceContext
	protected EntityManager entityManager;

	protected PersistenceUnitUtil persistenceUnitUtil;

	// public static void setEntityManagerFactory(EntityManagerFactory factory)
	// {
	// entityManagerFactory = factory;
	// }

	public JpaDao() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
		// entityManager = entityManagerFactory.createEntityManager();
	}

	@PostConstruct
	private void initPersistenceUnitUtil() {
		this.persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
	}

	public T create(T t) {
		if (t instanceof Identifiable) {
			if (((Identifiable) t).getId() == null) {
				((Identifiable) t).setId(UUIDGenerator.randomUUID());
			}
		}
		if (t instanceof Stampable) {
			if (((Stampable) t).getCreationDate() == null) {
				((Stampable) t).setCreationDate(new Date(Calendar.getInstance().getTimeInMillis()));
			}
			if (((Stampable) t).getLastUpdate() == null) {
				((Stampable) t).setLastUpdate(new Date(Calendar.getInstance().getTimeInMillis()));
			}
		}

		entityManager.persist(t);

		return t;
	}

	public T read(PK id) {
		return entityManager.find(entityClass, id);
	}

	public T update(T t) {
		if (t instanceof Stampable) {
			((Stampable) t).setLastUpdate(new Date(Calendar.getInstance().getTimeInMillis()));
		}
		return entityManager.merge(t);
	}

	public void delete(T t) {
		t = entityManager.merge(t);
		entityManager.remove(t);
	}

	public List<T> getAll() {
		List<T> result = entityManager.createQuery("from " + entityClass.getSimpleName(), entityClass).getResultList();
		return result == null ? new ArrayList<>() : result;
	}

	public long count() {
		return ((Number) entityManager.createQuery("select count(e) from " + entityClass.getSimpleName() + " e").getSingleResult()).longValue();
	}

	@Override
	public boolean isLoaded(T t) {
		return persistenceUnitUtil.isLoaded(t);
	}

	protected boolean isFieldLoaded(T t, String fieldName) {
		return persistenceUnitUtil.isLoaded(t, fieldName);
	}
}