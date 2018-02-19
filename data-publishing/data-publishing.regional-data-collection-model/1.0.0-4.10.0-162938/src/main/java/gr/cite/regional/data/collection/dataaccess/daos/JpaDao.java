package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.entities.Entity;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;

@Repository
public abstract class JpaDao<T extends Entity, PK extends Serializable> implements Dao<T, PK> {
	protected Class<T> entityClass;
	@PersistenceContext
	protected EntityManager entityManager;
	protected PersistenceUnitUtil persistenceUnitUtil;
	
	@SuppressWarnings("unchecked")
	public JpaDao() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
	}
	
	@PostConstruct
	private void initPersistenceUnitUtil() {
		this.persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
	}
	
	public T create(T t) {
		entityManager.persist(t);
		return t;
	}
	
	public T read(PK id) {
		return entityManager.find(entityClass, id);
	}
	
	public T update(T t) {
		return entityManager.merge(t);
	}
	
	public void delete(T t)	{
		t = entityManager.merge(t);
		entityManager.remove(t);
	}
	
	public List<T> getAll()	{
		return entityManager.createQuery("from " + entityClass.getSimpleName(), entityClass).getResultList();
	}
	
	public long count()	{
		return ((Number)entityManager.createQuery("select count(e) from " + entityClass.getSimpleName() + " e").getSingleResult()).longValue();
	}
	
	@Override
	public boolean isLoaded(T t) {
		return persistenceUnitUtil.isLoaded(t);
	}
	
	protected boolean isFieldLoaded(T t, String fieldName) {
		return persistenceUnitUtil.isLoaded(t, fieldName);
	}
}