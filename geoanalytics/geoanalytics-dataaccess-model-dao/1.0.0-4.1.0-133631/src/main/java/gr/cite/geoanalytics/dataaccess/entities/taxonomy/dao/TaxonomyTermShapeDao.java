package gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;

public interface TaxonomyTermShapeDao extends Dao<TaxonomyTermShape, UUID>
{
	public TaxonomyTermShape find(TaxonomyTerm tt, Shape s);
	public TaxonomyTermShape findUniqueByTerm(TaxonomyTerm tt);
	public List<TaxonomyTermShape> findByTerm(TaxonomyTerm tt);
	public List<TaxonomyTermShape> findNonProjectByTerm(TaxonomyTerm tt);
	public List<TaxonomyTermShape> findByShape(Shape s);
	public void deleteByTerm(TaxonomyTerm tt);
}
