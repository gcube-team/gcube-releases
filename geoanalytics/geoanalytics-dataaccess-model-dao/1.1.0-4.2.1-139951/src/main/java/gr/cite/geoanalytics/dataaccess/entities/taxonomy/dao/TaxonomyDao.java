package gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink;

public interface TaxonomyDao extends Dao<Taxonomy, UUID>
{
	 public List<Taxonomy> findByName(String name);
	 public List<Taxonomy> getActive();
	 public List<String> listNames();
	 public List<String> listNamesOfActive();
	 
	 public List<Taxonomy> getSiblings(Taxonomy t);
	 public List<Taxonomy> getInstances(Taxonomy t);
	 public List<Taxonomy> getInstancesByID(UUID taxonomyID);
	 
	 public List<TaxonomyTerm> getTerms(Taxonomy t);
	 public List<TaxonomyTerm> getActiveTerms(Taxonomy t);
	 public List<TaxonomyTerm> getTopmostTerms(Taxonomy t);
	 public List<TaxonomyTerm> getBottomTerms(Taxonomy t);
	 
	 public List<String> listTerms(Taxonomy t);
	 public List<String> listActiveTerms(Taxonomy t);
	
	 public List<TaxonomyTermLink> getTermLinks(Taxonomy t);
	 public List<TaxonomyTermLink> getActiveTermLinks(Taxonomy t);
	 public List<TaxonomyTermLink> getActiveTermLinks(Taxonomy t, TaxonomyTermLink.Verb verb);
	 
}
