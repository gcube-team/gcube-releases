package gr.cite.geoanalytics.dataaccess.entities.shape.dao;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeTerm;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeTermPK;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

public interface ShapeTermDao extends Dao<ShapeTerm, ShapeTermPK>
{
	public ShapeTerm find(TaxonomyTerm tt, Shape s);
	public void deleteByTerm(TaxonomyTerm tt);
}
