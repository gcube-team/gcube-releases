package gr.cite.geoanalytics.execution;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

public interface ModelLayer {
	public List<Shape> getGeometry() throws Exception;
	public Shape locate(TaxonomyTerm term) throws Exception;
	public Shape locate(Geometry geometry) throws Exception;
	
	public TaxonomyTerm getLayerTerm();

}
