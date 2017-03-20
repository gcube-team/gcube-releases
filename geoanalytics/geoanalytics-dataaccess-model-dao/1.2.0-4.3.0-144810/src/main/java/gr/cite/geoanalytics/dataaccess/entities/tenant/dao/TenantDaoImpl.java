package gr.cite.geoanalytics.dataaccess.entities.tenant.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

@Repository
public class TenantDaoImpl extends JpaDao<Tenant, UUID> implements TenantDao
{
	private static final Logger log = LoggerFactory.getLogger(TenantDaoImpl.class);
	
	@Override
	public List<Tenant> findByName(String name) {
		List<Tenant> result = null;
		
		TypedQuery<Tenant> query = entityManager.createQuery("from Tenant t where t.name = :name", Tenant.class);
		query.setParameter("name", name);
		
		result = query.getResultList();
		
		log.debug("Tenant by name: " + name);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Tenant t : (List<Tenant>) result) 
				log.debug("Tenant (" + t.getName() + ")");
		}

		return result;
	}
	
	@Override
	public List<String> listNames() {
		List<String> result = null;
		
		result = entityManager.createQuery("select t.name from Tenant t", String.class).getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("List tenant names");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<String> listNamesByCode(String code) {
		List<String> result = null;
		
		TypedQuery<String> query = entityManager.createQuery("select t.name from Tenant t where t.code like :code", String.class);
		query.setParameter("code", code);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("List tenant names by matching code");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<Tenant> findByCode(String code) {
		List<Tenant> result = null;
		
		TypedQuery<Tenant> query = entityManager.createQuery("from Tenant t where t.code like :code", Tenant.class);
		query.setParameter("code", code);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get tenants names by matching code");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<Tenant> searchByName(List<String> names) {
		List<Tenant> result = null;
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("from Tenant t");

		if(!names.isEmpty()) queryB.append(" where ");
		for(int i=0; i<names.size(); i++) {
			queryB.append("lower(t.name) like :name" + i);
			if(i < names.size()-1)
				queryB.append(" or ");
		}
		TypedQuery<Tenant> query = entityManager.createQuery(queryB.toString(), Tenant.class);
		for(int i=0; i<names.size(); i++) {
			String lower = names.get(i).toLowerCase();
			query.setParameter("name"+i, "%"+lower+"%");
		}

		result = query.getResultList();
		
		log.debug("Tenants by name pattern matching:");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Tenant t : (List<Tenant>) result) 
				log.debug("Tenant (" + t.getName() + ")");
		}
		
		return result;
	}

	@Override
	public Tenant loadDetails(Tenant t) {
		t.getCreator().getName();
		t.getTenantActivations().forEach(ta -> ta.getId());
		return t;
	}

	@Override
	public Tenant getTenantByUUID(UUID id) {
		Tenant result = null;
		
		TypedQuery<Tenant> query = entityManager.createQuery("from Tenant t where t.id = :tenantID", Tenant.class);
		query.setParameter("tenantID", id);
		
		result = query.getSingleResult();
		
		if(log.isDebugEnabled()) {
			log.debug("Get tenants names by matching id");
		}
		return result;
	}
}
