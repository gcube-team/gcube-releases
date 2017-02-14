package gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink;

public interface TaxonomyTermDao extends Dao<TaxonomyTerm, UUID>
{
	 public List<TaxonomyTerm> findByName(String name);
	 public List<TaxonomyTerm> findByTaxonomy(Taxonomy t);
	 public List<TaxonomyTerm> findByNameAndTaxonomy(String name, Taxonomy t);
	 public List<TaxonomyTerm> findAutoCreatedWithParent(Taxonomy t);
	 public List<String> listNames();
	 public List<String> listNamesOfActive();
	 public List<TaxonomyTerm> getSiblings(TaxonomyTerm t);
	 public List<TaxonomyTerm> getChildren(TaxonomyTerm t);
	 public List<TaxonomyTerm> getClassSiblings(TaxonomyTerm t);
	 public List<TaxonomyTerm> getClassDescendants(TaxonomyTerm t);
	 public List<TaxonomyTerm> getLinked(TaxonomyTerm t);
	 public Shape getShape(TaxonomyTerm t) throws Exception;
	 public List<Shape> getShapes(TaxonomyTerm t) throws Exception;
	 
	 public List<TaxonomyTerm> getLinkedTerms(Taxonomy sourceTaxonomy, TaxonomyTerm destTerm, TaxonomyTermLink.Verb verb);
	 public List<TaxonomyTerm> getLinkedTerms(TaxonomyTerm destTerm, TaxonomyTermLink.Verb verb);
	 public List<TaxonomyTerm> getActiveLinkedTerms(Taxonomy sourceTaxonomy, TaxonomyTerm destTerm, TaxonomyTermLink.Verb verb);
	 public List<TaxonomyTerm> getActiveLinkedTerms(TaxonomyTerm destTerm, TaxonomyTermLink.Verb verb);
	 public List<TaxonomyTerm> findByNameAndTaxonomies(String taxonomyTermName, List<Taxonomy> taxonomies);
	 public List<TaxonomyTerm> findAllTermsByTaxonomies(List<Taxonomy> taxonomies);

	 public boolean isTaxonomyLoaded(TaxonomyTerm tt);
	 public boolean isParentLoaded(TaxonomyTerm tt);
	 public boolean isTaxonomyTermClassLoaded(TaxonomyTerm tt);
	 public boolean isCreatorLoaded(TaxonomyTerm tt);
	 public boolean isDataLoaded(TaxonomyTerm tt);
}
