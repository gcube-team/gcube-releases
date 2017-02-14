package gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm.FieldName;

@Repository
public class TaxonomyTermDaoImpl extends JpaDao<TaxonomyTerm, UUID> implements TaxonomyTermDao {
	
private static Logger log = LoggerFactory.getLogger(TaxonomyDaoImpl.class);
	
	@Override
	public List<TaxonomyTerm> findByName(String name) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery("from TaxonomyTerm t where t.name = :name", TaxonomyTerm.class);
		query.setParameter("name", name);
		
		result = query.getResultList();
		
		log.debug("Taxonomy terms by name: " + name);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (TaxonomyTerm t : (List<TaxonomyTerm>) result) 
				log.debug("TaxonomyTerm (" + t.getName() + ")");
		}
	
		return result;
	}
	
	@Override
	public List<TaxonomyTerm> findByTaxonomy(Taxonomy t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"from TaxonomyTerm t where t.taxonomy = :tax", TaxonomyTerm.class);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (TaxonomyTerm tt : (List<TaxonomyTerm>) result) 
				log.debug("TaxonomyTerm (" + tt.getName() + ")");
		}
	
		return result;
	}
	
	@Override
	public List<TaxonomyTerm> findByNameAndTaxonomy(String name, Taxonomy t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"from TaxonomyTerm t where t.name = :name and t.taxonomy = :tax", TaxonomyTerm.class);
		query.setParameter("name", name);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		log.debug("Taxonomy terms by name: " + name);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (TaxonomyTerm tt : (List<TaxonomyTerm>) result) 
				log.debug("TaxonomyTerm (" + tt.getName() + ")");
		}
	
		return result;
	}


	@Override
	public List<TaxonomyTerm> findAutoCreatedWithParent(Taxonomy t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery("from TaxonomyTerm t where t.extraData= :eData", TaxonomyTerm.class);
		query.setParameter("eData", "auto " + t.getName());
		
		result = query.getResultList();
		
		log.debug("Auto created terms with parent taxonomy: " + t.getName());
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for(TaxonomyTerm tt : result)
				log.debug("TaxonomyTerm (" + tt.getName() + ")");
		}
		
		return result;
	}
	
	@Override
	public List<String> listNames() {
		List<String> result = null;
		
		result = entityManager.createQuery("select t.name from TaxonomyTerm t", String.class).getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("List taxonomy term names");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<String> listNamesOfActive() {
		List<String> result = null;
		
		result = entityManager.createQuery("select t.name from TaxonomyTerm t where t.isActive=1", String.class).getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("List taxonomy term names");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<TaxonomyTerm> getClassSiblings(TaxonomyTerm t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"select t from TaxonomyTerm t where t.id != :id and t.taxonomyTermClass = :cl", TaxonomyTerm.class);
		query.setParameter("id", t.getId());
		query.setParameter("cl", t.getTaxonomyTermClass());
		
		result = query.getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("Get taxonomy term class siblings (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<TaxonomyTerm> getSiblings(TaxonomyTerm t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"select t from TaxonomyTerm t where t.id != :id and t.parent = :tt order by t.order asc", TaxonomyTerm.class);
		query.setParameter("id", t.getId());
		query.setParameter("tt", t.getParent());
		
		result = query.getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("Get taxonomy term siblings (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<TaxonomyTerm> getChildren(TaxonomyTerm t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"select t from TaxonomyTerm t where t.parent = :tt order by t.order asc", TaxonomyTerm.class);
		query.setParameter("tt", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("Get taxonomy term descendants (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<TaxonomyTerm> getClassDescendants(TaxonomyTerm t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"select t from TaxonomyTerm t where t.taxonomyTermClass = :tt", TaxonomyTerm.class);
		query.setParameter("tt", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("Get taxonomy term descendants (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<TaxonomyTerm> getLinked(TaxonomyTerm t) {
		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"select l.sourceTerm from TaxonomyTermLink l where l.destinationTerm = :tt", TaxonomyTerm.class);
		query.setParameter("tt", t);
		
		result = query.getResultList();
		
		query = entityManager.createQuery(
				"select l.destTerm from TaxonomyTermLink l where l.sourceTerm = :tt", TaxonomyTerm.class);
		query.setParameter("tt", t);
		
		result.addAll(query.getResultList());
		
		if(log.isDebugEnabled())
		{
			log.debug("Get linked taxonomy terms (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<TaxonomyTerm> getLinkedTerms(Taxonomy sourceTaxonomy, TaxonomyTerm destTerm, TaxonomyTermLink.Verb verb) {
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"select distinct stt from TaxonomyTermLink ttl, TaxonomyTerm stt, TaxonomyTerm dtt where stt.taxonomy = :t and ttl.sourceTerm = stt and ttl.destinationTerm = :tt and ttl.verb = :v", 
				TaxonomyTerm.class);
		query.setParameter("t", sourceTaxonomy);
		query.setParameter("tt", destTerm);
		query.setParameter("v", verb.verbCode());
		
		return query.getResultList();
	}
	
	@Override
	public List<TaxonomyTerm> getLinkedTerms(TaxonomyTerm destTerm, TaxonomyTermLink.Verb verb) {
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"select distinct stt from TaxonomyTermLink ttl, TaxonomyTerm dtt where ttl.destinationTerm = :tt and ttl.verb = :v", 
				TaxonomyTerm.class);
		query.setParameter("tt", destTerm);
		query.setParameter("v", verb.verbCode());
		
		return query.getResultList();
	}
	
	@Override
	public List<TaxonomyTerm> getActiveLinkedTerms(Taxonomy sourceTaxonomy, TaxonomyTerm destTerm, TaxonomyTermLink.Verb verb) {
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"select distinct stt from TaxonomyTermLink ttl, TaxonomyTerm stt, TaxonomyTerm dtt where stt.isActive = 1 and stt.taxonomy = :t and ttl.sourceTerm = stt and ttl.destinationTerm = :tt and dtt.isActive = 1 and ttl.verb = :v", 
				TaxonomyTerm.class);
		query.setParameter("t", sourceTaxonomy);
		query.setParameter("tt", destTerm);
		query.setParameter("v", verb.verbCode());
		
		return query.getResultList();
	}
	
	@Override
	public List<TaxonomyTerm> getActiveLinkedTerms(TaxonomyTerm destTerm, TaxonomyTermLink.Verb verb) {
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"select distinct stt from TaxonomyTermLink ttl, TaxonomyTerm stt, TaxonomyTerm dtt where stt.isActive = 1 and ttl.sourceTerm = stt and ttl.destinationTerm = :tt and dtt.isActive = 1 and ttl.verb = :v", 
				TaxonomyTerm.class);
		query.setParameter("tt", destTerm);
		query.setParameter("v", verb.verbCode());
		
		return query.getResultList();
	}
	
	@Override
	public List<Shape> getShapes(TaxonomyTerm t) throws Exception {
		TypedQuery<Shape> query = entityManager.createQuery(
				"select tts.shape from TaxonomyTermShape tts where tts.term = :t", Shape.class);
		query.setParameter("t", t);
		
		List<Shape> res = query.getResultList();
		
		return res;
	}
	
	@Override
	public Shape getShape(TaxonomyTerm t) throws Exception {
		List<Shape> res = getShapes(t);
		
		if(res == null || res.isEmpty()) return null;
		if(res.size() != 1) 
			throw new Exception("Non-unique taxonomy term shape");
		return res.get(0);
	}

	@Override
	public List<TaxonomyTerm> findByNameAndTaxonomies(String taxonomyTermName, List<Taxonomy> taxonomies) {

		List<TaxonomyTerm> result = null;
		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"from TaxonomyTerm t where t.name = :taxonomyTermName and t.taxonomy in (:taxonomies)", TaxonomyTerm.class);
		query.setParameter("taxonomyTermName", taxonomyTermName);
		query.setParameter("taxonomies", taxonomies);
		
		result = query.getResultList();
		
		log.debug("Taxonomies that we searched in:");
		
		//System.out.println("Taxonomies that we searched in:");
		//taxonomies.forEach(taxonomy -> System.out.println("Taxonomy: " + taxonomy.getName()));
		
		log.debug("Taxonomy term we searched by: " + taxonomyTermName);
		log.debug((result != null ? result.size() : 0) + " results");
		
		if(log.isDebugEnabled() && result != null) {
			for (TaxonomyTerm tt : (List<TaxonomyTerm>) result)
				log.debug("TaxonomyTerm (" + tt.getName() + ")");
		}
		
		return result;
	}	
	
	public List<TaxonomyTerm> findAllTermsByTaxonomies(List<Taxonomy> taxonomies) {
		List<TaxonomyTerm> result = null;		
		TypedQuery<TaxonomyTerm> query = entityManager.createQuery(
				"from TaxonomyTerm t where t.taxonomy in (:taxonomies)", TaxonomyTerm.class);
		query.setParameter("taxonomies", taxonomies);
		
		result = query.getResultList();	
		
		return result;
	}
	
	@Override
	public boolean isTaxonomyLoaded(TaxonomyTerm tt) {
		return isFieldLoaded(tt, FieldName.TAXONOMY);
	}

	@Override
	public boolean isParentLoaded(TaxonomyTerm tt) {
		return isFieldLoaded(tt, FieldName.PARENT);
	}

	@Override
	public boolean isTaxonomyTermClassLoaded(TaxonomyTerm tt) {
		return isFieldLoaded(tt, FieldName.TAXONOMY_TERM_CLASS);
	}

	@Override
	public boolean isCreatorLoaded(TaxonomyTerm tt) {
		return isFieldLoaded(tt, FieldName.CREATOR);
	}

	@Override
	public boolean isDataLoaded(TaxonomyTerm tt) {
		return isFieldLoaded(tt, FieldName.EXTRA_DATA);
	}

	@Override
	public TaxonomyTerm loadDetails(TaxonomyTerm t) {
		t.getCreator().getName();
		if(t.getParent() != null)
			t.getParent().getId();
		t.getTaxonomy().getId();
		return t;
	}

}
