package gr.cite.geoanalytics.dataaccess.entities.geocode.dao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

public interface GeocodeDao extends Dao<Geocode, UUID> {
	 public List<Geocode> findAutoCreatedWithParent(GeocodeSystem t);
	 public List<Geocode> findByGeocodeSystem(GeocodeSystem t);
	 public List<Geocode> findByName(String name);
	 public List<Geocode> findByNameAndGeocodeSystem(String name, GeocodeSystem t);
	 public List<Geocode> findByNameAndTaxonomies(String geocodeName, List<GeocodeSystem> taxonomies);
	 public List<Geocode> findAllTermsByTaxonomies(List<GeocodeSystem> taxonomies);
	 
	 public List<Geocode> getSiblings(Geocode t);
	 public List<Geocode> getChildren(Geocode t);
	 public List<Geocode> getClassSiblings(Geocode t);
	 public List<Geocode> getClassDescendants(Geocode t);
	 public List<Geocode> getLinked(Geocode t);
	 public List<Geocode> getLinkedTerms(GeocodeSystem sourceGeocodeSystem, Geocode destTerm, TaxonomyTermLink.Verb verb);
	 public List<Geocode> getLinkedTerms(Geocode destTerm, TaxonomyTermLink.Verb verb);
	 public List<Geocode> getActiveLinkedTerms(GeocodeSystem sourceGeocodeSystem, Geocode destTerm, TaxonomyTermLink.Verb verb);
	 public List<Geocode> getActiveLinkedTerms(Geocode destTerm, TaxonomyTermLink.Verb verb);
	 public Shape getShape(Geocode t) throws Exception;
	 public List<Shape> getShapes(Geocode t) throws Exception;
	 public List<Geocode> getGeocodesByShapes(Collection<Shape> shapes);

	 public boolean isGeocodeSystemLoaded(Geocode tt);
	 public boolean isParentLoaded(Geocode tt);
	 public boolean isGeocodeClassLoaded(Geocode tt);
	 public boolean isCreatorLoaded(Geocode tt);
	 public boolean isDataLoaded(Geocode tt);
	 
	 public void deleteById(Geocode tt);	 
	 
	 public List<String> listNames();
	 public List<String> listNamesOfActive();
}
