package gr.cite.geoanalytics.dataaccess.entities.geocode.dao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode.FieldName;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

@Repository
public class GeocodeDaoImpl extends JpaDao<Geocode, UUID> implements GeocodeDao {
	
private static Logger log = LoggerFactory.getLogger(GeocodeDaoImpl.class);
	
	@Override
	public List<Geocode> findByName(String name) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery("from Geocode t where t.name = :name", Geocode.class);
		query.setParameter("name", name);
		
		result = query.getResultList();
		
		log.debug("Geocode by name: " + name);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Geocode t : result) 
				log.debug("Geocode (" + t.getName() + ")");
		}
	
		return result;
	}
	
	@Override
	public List<Geocode> findByGeocodeSystem(GeocodeSystem geocodeSystem) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery("from Geocode g where g.geocodeSystem = :gcs", Geocode.class);
		query.setParameter("gcs", geocodeSystem);
		
		result = query.getResultList();
		
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null){
			for (Geocode tt : result) 
				log.debug("Geocode (" + tt.getName() + ")");
		}
	
		return result;
	}
	
	@Override
	public List<Geocode> findByNameAndGeocodeSystem(String name, GeocodeSystem t) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery(
				"from Geocode t where t.name = :name and t.geocodeSystem = :tax", Geocode.class);
		query.setParameter("name", name);
		query.setParameter("tax", t);
		
		result = query.getResultList();
		
		log.debug("Geocodes by name: " + name);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Geocode tt : result) 
				log.debug("Geocode (" + tt.getName() + ")");
		}
	
		return result;
	}


	@Override
	public List<Geocode> findAutoCreatedWithParent(GeocodeSystem t) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery("from Geocode t where t.extraData= :eData", Geocode.class);
		query.setParameter("eData", "auto " + t.getName());
		
		result = query.getResultList();
		
		log.debug("Auto created terms with parent taxonomy: " + t.getName());
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for(Geocode tt : result)
				log.debug("Geocode (" + tt.getName() + ")");
		}
		
		return result;
	}
	
	@Override
	public List<String> listNames() {
		List<String> result = null;
		
		result = entityManager.createQuery("select t.name from Geocode t", String.class).getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("List geocode names");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<String> listNamesOfActive() {
		List<String> result = null;
		
		result = entityManager.createQuery("select t.name from Geocode t where t.isActive=1", String.class).getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("List geocode names");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<Geocode> getClassSiblings(Geocode t) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery(
				"select t from Geocode t where t.id != :id and t.geocodeClass = :cl", Geocode.class);
		query.setParameter("id", t.getId());
		query.setParameter("cl", t.getGeocodeClass());
		
		result = query.getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("Get geocode class siblings (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<Geocode> getSiblings(Geocode t) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery(
				"select t from Geocode t where t.id != :id and t.parent = :tt order by t.order asc", Geocode.class);
		query.setParameter("id", t.getId());
		query.setParameter("tt", t.getParent());
		
		result = query.getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("Get geocode siblings (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<Geocode> getChildren(Geocode t) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery(
				"select t from Geocode t where t.parent = :tt order by t.order asc", Geocode.class);
		query.setParameter("tt", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("Get geocode descendants (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<Geocode> getClassDescendants(Geocode t) {
		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery(
				"select t from Geocode t where t.geocodeClass = :tt", Geocode.class);
		query.setParameter("tt", t);
		
		result = query.getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("Get geocode descendants (" + t.getName() + ")");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}

	@Override
	public List<Geocode> getGeocodesByShapes(Collection<Shape> shapes) {
		StringBuilder queryB = new StringBuilder("");
		queryB.append("FROM Geocode gc WHERE gc.shape IN :shapes");
		
		TypedQuery<Geocode> query = entityManager.createQuery(queryB.toString(), Geocode.class);
		query.setParameter("shapes", shapes);
		
		return query.getResultList();
	}
	
	@Override
	public List<Shape> getShapes(Geocode t) throws Exception {
		TypedQuery<Shape> query = entityManager.createQuery(
				"select tts.shape from GeocodeShape tts where tts.geocode = :t", Shape.class);
		query.setParameter("t", t);
		
		List<Shape> res = query.getResultList();
		
		return res;
	}
	
	@Override
	public Shape getShape(Geocode t) throws Exception {
		List<Shape> res = getShapes(t);
		
		if(res == null || res.isEmpty()) return null;
		if(res.size() != 1) 
			throw new Exception("Non-unique geocode shape");
		return res.get(0);
	}

	@Override
	public List<Geocode> findByNameAndTaxonomies(String geocodeName, List<GeocodeSystem> taxonomies) {

		List<Geocode> result = null;
		
		TypedQuery<Geocode> query = entityManager.createQuery(
				"from Geocode t where t.name = :geocodeName and t.geocodeSystem in (:taxonomies)", Geocode.class);
		query.setParameter("geocodeName", geocodeName);
		query.setParameter("taxonomies", taxonomies);
		
		result = query.getResultList();
		
		log.debug("Taxonomies that we searched in:");
		
		//System.out.println("Taxonomies that we searched in:");
		//taxonomies.forEach(taxonomy -> System.out.println("Taxonomy: " + taxonomy.getName()));
		
		log.debug("Geocode we searched by: " + geocodeName);
		log.debug((result != null ? result.size() : 0) + " results");
		
		if(log.isDebugEnabled() && result != null) {
			for (Geocode tt : result)
				log.debug("Geocode (" + tt.getName() + ")");
		}
		
		return result;
	}	
	
	@Override
	public List<Geocode> findAllTermsByTaxonomies(List<GeocodeSystem> taxonomies) {
		List<Geocode> result = null;		
		TypedQuery<Geocode> query = entityManager.createQuery(
				"from Geocode t where t.geocodeSystem in (:taxonomies)", Geocode.class);
		query.setParameter("taxonomies", taxonomies);
		
		result = query.getResultList();	
		
		return result;
	}

	@Override
	public boolean isGeocodeSystemLoaded(Geocode tt) {
		return isFieldLoaded(tt, FieldName.GEOCODESYSTEM);
	}

	@Override
	public boolean isParentLoaded(Geocode tt) {
		return isFieldLoaded(tt, FieldName.PARENT);
	}

	@Override
	public boolean isGeocodeClassLoaded(Geocode tt) {
		return isFieldLoaded(tt, FieldName.GEOCODE_CLASS);
	}

	@Override
	public boolean isCreatorLoaded(Geocode tt) {
		return isFieldLoaded(tt, FieldName.CREATOR);
	}

	@Override
	public boolean isDataLoaded(Geocode tt) {
		return isFieldLoaded(tt, FieldName.EXTRA_DATA);
	}
	
	@Override
	public void deleteById(Geocode tt) {
		Query query = entityManager.createQuery("delete Geocode tt where tt.id = :tt_id");
		query.setParameter("tt_id", tt.getId());
		query.executeUpdate();
	}

	@Override
	public Geocode loadDetails(Geocode t) {
		t.getCreator().getName();
		if(t.getParent() != null)
			t.getParent().getId();
		t.getGeocodeSystem().getId();
		return t;
	}
}
