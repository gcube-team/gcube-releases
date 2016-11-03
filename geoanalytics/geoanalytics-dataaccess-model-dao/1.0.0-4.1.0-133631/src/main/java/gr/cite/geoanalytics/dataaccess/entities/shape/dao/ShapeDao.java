package gr.cite.geoanalytics.dataaccess.entities.shape.dao;


import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShapeDao  extends Dao<Shape, UUID> {
	public List<Shape> findShapesByClass(short shp_class);
	public List<Shape> searchShapesByName(String term);
	public List<Shape> searchShapes(List<String> terms);
	public List<Shape> searchShapesWithin(List<String> terms, Shape container);
	public List<Shape> searchShapesWithinByAttributes(Map<String, Attribute> attrs, Shape container);
	public long countShapesByImport(UUID si);
	public List<Shape> findShapesByImport(UUID si);
	public List<Shape> findShapesByImport(ShapeImport si);
	public boolean existShapesOfTerm(TaxonomyTerm tt);
	public TaxonomyTerm findTermOfShape(Shape s) throws Exception;
	public List<TaxonomyTerm> findTaxononyTermShapes(Shape s) throws Exception;
	public List<Document> findDocumentsOfShape(Shape s);
	public Document findDocumentOfShape(Shape s, Taxonomy t) throws Exception;
	public List<Shape> findShapesByTerm(TaxonomyTerm tt);
	public List<TaxonomyTermShape> findTermMappingsOfLayerShapes(TaxonomyTerm tt);
	public Set<String> getAttributeValuesOfShapesByTerm(TaxonomyTerm tt, Attribute attribute);
	public void deleteShapesOfTerm(TaxonomyTerm tt);
	
	public boolean equalsGeom(Shape s1, Shape s2) throws Exception;
	public boolean disjoint(Shape s1, Shape s2) throws Exception;
	public boolean intersects(Shape s1, Shape s2) throws Exception;
	public boolean touches(Shape s1, Shape s2) throws Exception;
	public boolean crosses(Shape s1, Shape s2) throws Exception;
	public boolean within(Shape s1, Shape s2) throws Exception;
	public boolean covers(Shape s1, Shape s2) throws Exception;
	//public boolean dWithin(Shape s1, Shape s2, double d) throws Exception;
	public boolean contains(Shape s1, Shape s2) throws Exception;
	public boolean overlaps(Shape s1, Shape s2) throws Exception;
	public boolean relate(Shape s1, Shape s2) throws Exception;
	public double distance(Shape s1, Shape s2) throws Exception;
	public double area(Shape s) throws Exception;
	
	public List<Shape> findEqualsGeom(Shape s) throws Exception;
	public List<Shape> findDisjoint(Shape s) throws Exception;
	public List<Shape> findIntersects(Shape s) throws Exception;
	public List<Shape> findTouches(Shape s) throws Exception;
	public List<Shape> findCrosses(Shape s) throws Exception;
	public List<Shape> findWithin(Shape s) throws Exception;
	public List<Shape> findWithin(Shape s, TaxonomyTerm layerTerm) throws Exception;
	public List<Shape> findWithin(Shape s, TaxonomyTerm layerTerm, TaxonomyTerm term) throws Exception;
	public List<Shape> findCovers(Shape s) throws Exception;
	//public List<Shape> findDWithin(Shape s, double d) throws Exception;
	public List<Shape> findContains(Shape s) throws Exception;
	public List<Shape> findOverlaps(Shape s) throws Exception;
	public List<Shape> findRelate(Shape s) throws Exception;
	public List<Shape> findDistanceEqual(Shape s, double d) throws Exception;
	public List<Shape> findDistanceLess(Shape s, double d) throws Exception;
	public List<Shape> findDistanceLessOrEqual(Shape s, double d) throws Exception;
	public List<Shape> findDistanceGreater(Shape s, double d) throws Exception;
	public List<Shape> findDistanceGreaterOrEqual(Shape s, double d) throws Exception;
	
	public Shape envelope(Shape s) throws Exception;
	public Shape boundary(Shape s) throws Exception;
	public Shape buffer(Shape s, float d) throws Exception;
	public Shape convexHull(Shape s) throws Exception;
	public Shape intersection(Shape s1, Shape s2) throws Exception;
	public Shape union(Shape s1, Shape s2) throws Exception;
	public Shape difference(Shape s1, Shape s2) throws Exception;
	public Shape symDifference(Shape s1, Shape s2) throws Exception;
	public Shape transform(Shape s, int srid) throws Exception;
	public Shape extent(Shape s) throws Exception;
	
	public Shape getShapeFromLayerTermAndShapeTerm(TaxonomyTerm layerTerm, TaxonomyTerm termForShape);
	public Map<String, Shape> getShapesFromLayerTerm(TaxonomyTerm layerTerm);
	public TaxonomyTerm getTermFromLayerTermAndShape(TaxonomyTerm layerTerm, Shape shape);
}
