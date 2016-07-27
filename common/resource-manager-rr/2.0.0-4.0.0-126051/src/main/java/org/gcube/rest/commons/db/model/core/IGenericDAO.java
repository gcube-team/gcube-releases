package org.gcube.rest.commons.db.model.core;

import java.util.List;
import java.util.Map;

import org.gcube.rest.commons.db.dao.core.BaseRecord;
import org.hibernate.criterion.Criterion;

public interface IGenericDAO<T extends BaseRecord> {

	public T load(Long id);

	public T save(T object);

	public void delete(T object);

	public void deleteById(Long id);

	public List<T> findAll();

	public int countAll();
	
	public List<T> findByExample(final T exampleInstance);
	
	public List<T> findByNamedQuery(final String name, Object... params);
	
	public List<T> findByNamedQueryAndNamedParams(final String name,
			final Map<String, ? extends Object> params);
	
	public List<T> findByCriteria(final Criterion... criterion);

}