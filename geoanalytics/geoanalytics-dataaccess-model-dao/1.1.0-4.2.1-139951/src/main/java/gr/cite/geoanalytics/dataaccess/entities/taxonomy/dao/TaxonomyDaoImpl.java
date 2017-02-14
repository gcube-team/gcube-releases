package gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink;

@Repository
public class TaxonomyDaoImpl extends JpaDao<Taxonomy, UUID> implements TaxonomyDao {

	private static Logger log = LoggerFactory.getLogger(TaxonomyDaoImpl.class);
	
	@Override
	public List<Taxonomy> getActive() {
		List<Taxonomy> result = null;
		
		result = entityManager.createQuery("from Taxonomy t where t.isActive = 1", Taxonomy.class).getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get active taxonomies");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	@Override
	public List<Taxonomy> findByName(String name) {
		List<Taxonomy> result = null;
		
		TypedQuery<Taxonomy> query = entityManager.createQuery("from Taxonomy t where t.name = :name", Taxonomy.class);
		query.setParameter("name", name);
		
		result = query.getResultList();
		
		log.debug("Taxonomies by name: " + name);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Taxonomy t : (List<Taxonomy>) result) 
				log.debug("Taxonomy (" + t.getName() + ")");
		}
	
		return result;
	}

	@Override
	public List<String> listNames() {
		List<String> result = null;
		
		result = entityManager.createQuery("select t.name from Taxonomy t", String.class).getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("List taxonomy names");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<String> listNamesOfActive() {
		List<String> result = null;
		
		result = entityManager.createQuery("select t.name from Taxonomy t where t.isActive=1", String.class).getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("List taxonomy names");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<TaxonomyTerm> getTerms(Taxonomy t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery("select t from TaxonomyTerm t where t.taxonomy = :tax order by t.order asc", TaxonomyTerm.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy terms (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<TaxonomyTerm> getActiveTerms(Taxonomy t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery("select t from TaxonomyTerm t where t.taxonomy = :tax and t.isActive=1 order by t.order", TaxonomyTerm.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy active terms (" + t.getName() +")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<TaxonomyTerm> getTopmostTerms(Taxonomy t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery("select t from TaxonomyTerm t where t.taxonomy = :tax and t.isActive=1 and t.parent is null order by t.order", TaxonomyTerm.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy topmost terms (" + t.getName() +")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<TaxonomyTerm> getBottomTerms(Taxonomy t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> termsWithoutParentQuery = entityManager.createQuery("select t1 from TaxonomyTerm t1 where t1.taxonomy = :tax and t1.parent is null", TaxonomyTerm.class);
		termsWithoutParentQuery.setParameter("tax", t);
		List<TaxonomyTerm> termsWithoutParent = termsWithoutParentQuery.getResultList();
		
		TypedQuery<Long> allTermsQuery = entityManager.createQuery("select count(t1) from TaxonomyTerm t1 where t1.taxonomy = :tax", Long.class);
		allTermsQuery.setParameter("tax", t);
		Long allTermsCount = allTermsQuery.getSingleResult();
		if(termsWithoutParent.size() == allTermsCount)
			return termsWithoutParent;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"select t from TaxonomyTerm t " + 
				"where t.taxonomy = :tax and t.id not in " + 
				"(select distinct t2.parent from TaxonomyTerm t2 where t2.taxonomy = :tax and t2.parent is not null)",
				TaxonomyTerm.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy bottom terms (" + t.getName() +")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<String> listTerms(Taxonomy t) {
		List<String> result = null;
		
		TypedQuery<String> query = entityManager.createQuery("select t.name from TaxonomyTerm t where t.taxonomy = :tax", String.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("List taxonomy terms (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<String> listActiveTerms(Taxonomy t) {
		List<String> result = null;
		
		TypedQuery<String> query = entityManager.createQuery("select t.name from TaxonomyTerm t where t.taxonomy = :tax and t.isActive=1", String.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy active terms (" + t.getName() +")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<Taxonomy> getSiblings(Taxonomy t) {
		List<Taxonomy> result = null;
		
		TypedQuery<Taxonomy> query = entityManager.createQuery("select t from Taxonomy t where t.taxonomyClass = :cl", Taxonomy.class);
		query.setParameter("cl", t.getTaxonomyClass());
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy siblings (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<Taxonomy> getInstances(Taxonomy t) {
		List<Taxonomy> result = null;
		
		TypedQuery<Taxonomy> query = entityManager.createQuery("select t from Taxonomy t where t.taxonomyClass = :tax", Taxonomy.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled()) {
			log.debug("Get taxonomy descendants (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<TaxonomyTermLink> getTermLinks(Taxonomy t) {
		TypedQuery<TaxonomyTermLink> query = entityManager.createQuery(
				"select distinct ttl from TaxonomyTermLink ttl, TaxonomyTerm tt where tt.taxonomy = :t and ttl.sourceTerm = tt", 
				TaxonomyTermLink.class);
		query.setParameter("t", t);
		
		return query.getResultList();
	}
	
	@Override
	public List<TaxonomyTermLink> getActiveTermLinks(Taxonomy t) {
		TypedQuery<TaxonomyTermLink> query = entityManager.createQuery(
				"select distinct ttl from TaxonomyTermLink ttl, TaxonomyTerm stt, TaxonomyTerm dtt where stt.isActive = 1 and  stt.taxonomy = :t and ttl.sourceTerm = stt and ttl.destinationTerm = dtt and dtt.isActive = 1", 
				TaxonomyTermLink.class);
		query.setParameter("t", t);
		
		return query.getResultList();
	}
	
	@Override
	public List<TaxonomyTermLink> getActiveTermLinks(Taxonomy t, TaxonomyTermLink.Verb verb) {
		TypedQuery<TaxonomyTermLink> query = entityManager.createQuery(
				"select distinct ttl from TaxonomyTermLink ttl, TaxonomyTerm stt, TaxonomyTerm dtt where stt.isActive = 1 and  stt.taxonomy = :t and ttl.sourceTerm = stt and ttl.destinationTerm = dtt and ttl.verb = :v and dtt.isActive = 1", 
				TaxonomyTermLink.class);
		query.setParameter("t", t);
		query.setParameter("v", verb.verbCode());
		
		return query.getResultList();
	}
	@Override
	public Taxonomy loadDetails(Taxonomy t) {
		t.getCreator().getName();
		if(t.getTaxonomyClass() != null)
			t.getTaxonomyClass().getId();
		return t;
	}
	@Override
	public List<Taxonomy> getInstancesByID(UUID taxonomyID) {
		List<Taxonomy> result = null;
		
		TypedQuery<Taxonomy> query = entityManager.createQuery("select t from Taxonomy t where t.taxonomyClass.id = :tid", Taxonomy.class);
		query.setParameter("tid", taxonomyID);
		
		try{
			result = query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}
