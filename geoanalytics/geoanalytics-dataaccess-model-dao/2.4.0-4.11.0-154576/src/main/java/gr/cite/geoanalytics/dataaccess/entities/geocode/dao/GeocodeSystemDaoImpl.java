package gr.cite.geoanalytics.dataaccess.entities.geocode.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;

@Repository
public class GeocodeSystemDaoImpl extends JpaDao<GeocodeSystem, UUID> implements GeocodeSystemDao {

	private static Logger log = LoggerFactory.getLogger(GeocodeSystemDaoImpl.class);
	
	@Override
	public List<GeocodeSystem> getActive() {
		List<GeocodeSystem> result = null;
		
		result = entityManager.createQuery("from GeocodeSystem t where t.isActive = 1", GeocodeSystem.class).getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get active taxonomies");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	@Override
	public List<GeocodeSystem> findByName(String name) {
		List<GeocodeSystem> result = null;
		
		TypedQuery<GeocodeSystem> query = entityManager.createQuery("from GeocodeSystem t where t.name = :name", GeocodeSystem.class);
		query.setParameter("name", name);
		
		result = query.getResultList();
		
		log.debug("Geocode Systems by name: " + name);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (GeocodeSystem t : result) 
				log.debug("GeocodeSystem (" + t.getName() + ")");
		}
	
		return result == null ? new ArrayList<>() : result;
	}

	@Override
	public List<String> listNames() {
		List<String> result = null;
		
		result = entityManager.createQuery("select t.name from GeocodeSystem t", String.class).getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("List taxonomy names");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<String> listNamesOfActive() {
		List<String> result = null;
		
		result = entityManager.createQuery("select t.name from GeocodeSystem t where t.isActive=1", String.class).getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("List taxonomy names");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<Geocode> getGeocodes(GeocodeSystem t) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery("select t from Geocode t where t.geocodeSystem = :tax order by t.order asc", Geocode.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy terms (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<Geocode> getActiveGeocodes(GeocodeSystem t) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery("select t from Geocode t where t.geocodeSystem = :tax and t.isActive=1 order by t.order", Geocode.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get active geocodes (" + t.getName() +")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<Geocode> getTopmostGeocodes(GeocodeSystem t) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery("select t from Geocode t where t.geocodeSystem = :tax and t.isActive=1 and t.parent is null order by t.order", Geocode.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy topmost terms (" + t.getName() +")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<Geocode> getBottomGeocodes(GeocodeSystem t) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> termsWithoutParentQuery = entityManager.createQuery("select t1 from Geocode t1 where t1.geocodeSystem = :tax and t1.parent is null", Geocode.class);
		termsWithoutParentQuery.setParameter("tax", t);
		List<Geocode> termsWithoutParent = termsWithoutParentQuery.getResultList();
		
		TypedQuery<Long> allTermsQuery = entityManager.createQuery("select count(t1) from Geocode t1 where t1.geocodeSystem = :tax", Long.class);
		allTermsQuery.setParameter("tax", t);
		Long allTermsCount = allTermsQuery.getSingleResult();
		if(termsWithoutParent.size() == allTermsCount)
			return termsWithoutParent;
		
		TypedQuery<Geocode> query = entityManager.createQuery(
				"select t from Geocode t " + 
				"where t.geocodeSystem = :tax and t.id not in " + 
				"(select distinct t2.parent from Geocode t2 where t2.geocodeSystem = :tax and t2.parent is not null)",
				Geocode.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy bottom terms (" + t.getName() +")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<String> listGeocodes(GeocodeSystem t) {
		List<String> result = null;
		
		TypedQuery<String> query = entityManager.createQuery("select t.name from Geocode t where t.geocodeSystem = :tax", String.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("List taxonomy terms (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<String> listActiveGeocodes(GeocodeSystem t) {
		List<String> result = null;
		
		TypedQuery<String> query = entityManager.createQuery("select t.name from Geocode t where t.geocodeSystem = :tax and t.isActive=1", String.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy active terms (" + t.getName() +")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<GeocodeSystem> getSiblings(GeocodeSystem t) {
		List<GeocodeSystem> result = null;
		
		TypedQuery<GeocodeSystem> query = entityManager.createQuery("select t from GeocodeSystem t where t.taxonomyClass = :cl", GeocodeSystem.class);
		query.setParameter("cl", t.getTaxonomyClass());
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy siblings (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<GeocodeSystem> getInstances(GeocodeSystem t) {
		List<GeocodeSystem> result = null;
		
		TypedQuery<GeocodeSystem> query = entityManager.createQuery("select t from GeocodeSystem t where t.taxonomyClass = :tax", GeocodeSystem.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy descendants (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public GeocodeSystem loadDetails(GeocodeSystem t) {
		t.getCreator().getName();
		if(t.getTaxonomyClass() != null)
			t.getTaxonomyClass().getId();
		return t;
	}
	@Override
	public List<GeocodeSystem> getInstancesByID(UUID taxonomyID) {
		List<GeocodeSystem> result = null;
		
		TypedQuery<GeocodeSystem> query = entityManager.createQuery("select t from GeocodeSystem t where t.taxonomyClass.id = :tid", GeocodeSystem.class);
		query.setParameter("tid", taxonomyID);
		
		try{
			result = query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}
