package org.gcube.rest.commons.db.model.core;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.gcube.rest.commons.db.dao.core.BaseRecord;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;


public abstract class GenericDaoImpl<T extends BaseRecord> implements
		IGenericDAO<T> {

	@Inject
	private Provider<EntityManager> em;

	public abstract Class<T> getClazz();
//	@SuppressWarnings("unchecked")
//	public Class<T> getClazz(){
//		return (Class<T>) ((ParameterizedType) getClass()
//				.getGenericSuperclass()).getActualTypeArguments()[0];
//	}

	@Transactional
	public T load(Long id) {
		return em.get().find(getClazz(), id);
	}

	@Transactional
	public T save(T object) {
		final T savedEntity = em.get().merge(object);
		return savedEntity;
	}

	@Transactional
	public void delete(T object) {
//		this.deleteById(object.getId());
		em.get().remove(object);
	}

	@Transactional
	public void deleteById(Long id) {
		em.get().remove(this.load(id));
	}

	public List<T> findAll() {
		return findByCriteria();
	}

	public int countAll() {
		return countByCriteria();
	}

	protected int countByCriteria(Criterion... criterion) {
		Session session = (Session) em.get().getDelegate();

		Criteria crit = session.createCriteria(getClazz());
		crit.setProjection(Projections.rowCount());

		for (final Criterion c : criterion) {
			crit.add(c);
		}

		return (Integer) crit.list().get(0);
	}

	public int countByExample(final T exampleInstance) {
		Session session = (Session) em.get().getDelegate();
		Criteria crit = session.createCriteria(getClazz());
		crit.setProjection(Projections.rowCount());
		crit.add(Example.create(exampleInstance));

		return (Integer) crit.list().get(0);
	}

	public List<T> findByExample(final T exampleInstance) {
		Session session = (Session) em.get().getDelegate();
		Criteria crit = session.createCriteria(getClazz());

		@SuppressWarnings("unchecked")
		final List<T> result = crit.list();
		return result;
	}

	public List<T> findByNamedQuery(final String name, Object... params) {
		javax.persistence.Query query = em.get().createNamedQuery(name);

		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}

		@SuppressWarnings("unchecked")
		final List<T> result = (List<T>) query.getResultList();

		return result;
	}

	public List<T> findByNamedQueryAndNamedParams(final String name,
			final Map<String, ? extends Object> params) {
		javax.persistence.Query query = em.get().createNamedQuery(name);

		for (final Map.Entry<String, ? extends Object> param : params
				.entrySet()) {
			query.setParameter(param.getKey(), param.getValue());
		}

		@SuppressWarnings("unchecked")
		final List<T> result = (List<T>) query.getResultList();
		return result;
	}
	
	

	public List<T> findByCriteria(final Criterion... criterion) {
		return findByCriteria(null, -1, -1, criterion);
	}
	
	public List<T> findByCriteria(final Map<String, String> aliases, final Criterion... criterion) {
		return findByCriteria(aliases, -1, -1, criterion);
	}

	protected List<T> findByCriteria(final Map<String, String> aliases, final int firstResult,
			final int maxResults, final Criterion... criterion) {
		Session session = (Session) em.get().getDelegate();
		Criteria crit = session.createCriteria(getClazz());

		if (aliases != null) {
			for (Map.Entry<String, String> alias : aliases.entrySet()) {
				crit.createAlias(alias.getKey(), alias.getValue());
			}
		}
		
		for (final Criterion c : criterion) {
			crit.add(c);
		}

		if (firstResult > 0) {
			crit.setFirstResult(firstResult);
		}

		if (maxResults > 0) {
			crit.setMaxResults(maxResults);
		}

		@SuppressWarnings("unchecked")
		final List<T> result = crit.list();
		return result;
	}

	public T findById(final Long id) {
		final T result = em.get().find(getClazz(), id);
		return result;
	}
	
	public List<T> getByResourceID(String resourceID) {
		Criterion criterion = Restrictions.eq("resourceId", resourceID);
		List<T> results = this.findByCriteria(criterion);

		return results;
	}
	
	public List<T> deleteByResourceID(String resourceID) {
		Criterion criterion = Restrictions.eq("resourceId", resourceID);
		List<T> results = this.findByCriteria(criterion);
		
		for(T result : results) {
			delete(result);
		}
		
		return results;
	}
}
