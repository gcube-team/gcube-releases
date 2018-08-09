package org.gcube.data.analysis.tabulardata.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

@Singleton
public class EntityManagerHelper {

	@Inject @TabularDataMetadata
	EntityManagerFactory emFactory;
	
	public EntityManager getEntityManager(){
		return emFactory.createEntityManager();
	}
	
	public <T> List<T> getResults(String queryName, Class<T> entityClass, Map<String, Object> parameters){
		EntityManager em = emFactory.createEntityManager();
		TypedQuery<T> query = em.createNamedQuery(queryName, entityClass);
		for (Entry<String, Object> param: parameters.entrySet())
			query.setParameter(param.getKey(), param.getValue());
		List<T> descriptors = query.getResultList();		
		if (em!=null && em.isOpen())
			em.close();
		return descriptors;
	}
	
	public <T> List<T> getResults(String queryName, Class<T> entityClass){
		EntityManager em = emFactory.createEntityManager();
		TypedQuery<T> query = em.createNamedQuery(queryName, entityClass);
		List<T> descriptors = query.getResultList();		
		if (em!=null && em.isOpen())
			em.close();
		return descriptors;
	}
}
