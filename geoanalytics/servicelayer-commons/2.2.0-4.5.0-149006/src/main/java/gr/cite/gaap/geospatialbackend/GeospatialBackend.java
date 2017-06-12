//package gr.cite.gaap.geospatialbackend;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//
//import org.springframework.stereotype.Service;
//import com.vividsolutions.jts.geom.Geometry;
//
//import gr.cite.gaap.datatransferobjects.AttributeInfo;
//import gr.cite.gaap.datatransferobjects.Coords;
//import gr.cite.gaap.datatransferobjects.GeoLocation;
//import gr.cite.gaap.datatransferobjects.GeoSearchSelection.SearchType;
//import gr.cite.gaap.datatransferobjects.NewProjectData;
//import gr.cite.gaap.datatransferobjects.ShapeMessenger;
//import gr.cite.gaap.datatransferobjects.WfsShapeInfo;
//import gr.cite.gaap.servicelayer.GeographyHierarchy;
//import gr.cite.gaap.servicelayer.ShapeInfo;
//import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
//import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
//import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
//import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
//import gr.cite.geoanalytics.dataaccess.entities.project.Project;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
//import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
//
//@Service
//public interface GeospatialBackend {
//	
//	Set<String> getAttributeValuesOfShapesByLayer(UUID layerID, Attribute attr) throws Exception;
//	Shape findShapeById(UUID id) throws IOException;
//
//	ShapeInfo findShapeByIdInfo(UUID id) throws Exception;
//
//	String retrieveShapeAttributeValue(Shape s, String attribute) throws Exception;
//
//	AttributeInfo retrieveShapeAttribute(Shape s, String attribute) throws Exception;
//
//	AttributeInfo retrieveShapeAttributeByTaxonomy(Shape s, String taxonomy) throws Exception;
//
//	void addShapeAttribute(Shape s, String attrName, String attrValue, GeocodeSystem taxonomy) throws Exception;
//
//	void setShapeAttributes(Shape s, Map<String, AttributeInfo> attrs) throws Exception;
//
//	void updateShapeAttribute(Shape s, String attrName, String attrValue) throws Exception;
//
//	void removeShapeAttribute(Shape s, String attrName) throws Exception;
//
//	Map<String, AttributeInfo> consolidateAttributes(Shape s) throws Exception;
//
//	Map<String, AttributeInfo> computeAttributes(Shape s) throws Exception;
//
//	Map<String, AttributeInfo> retrieveShapeAttributes(Shape s) throws Exception;
//
//	public String generateShapesOfImport(UUID layerID, Map<String, Map<String,AttributeInfo>> attrInfo, Map<String, Set<String>> valueMappingValues, 
//			UUID importId, String layerTermId, GeographyHierarchy geographyHierarchy, Principal principal) throws Exception;
//
//	public void generateShapeBoundary(UUID layerID, String layerName, Geocode boundaryTerm, Principal principal) throws Exception;
//
//	List<Shape> getShapesOfImport(UUID importId) throws Exception;
//
//	List<Shape> getShapesOfLayerID(UUID layerID) throws Exception;
//	
//	Map<String, Shape> getShapesOfLayer(Layer layer) throws Exception;
//	
//	List<ShapeInfo> getShapeInfoForLayer(UUID layerID) throws Exception;
//	
//	List<ShapeMessenger> getShapeMessengerForLayer(UUID layerID) throws Exception;
//	
////	List<ShapeInfo> getShapeInfoForLayer(String termName, String termTaxonomy) throws Exception;
//
//
////	Shape getShapeFromLayerTermAndShapeTerm(TaxonomyTerm layerTerm, TaxonomyTerm termForShape);
////	Shape getShapeFromLayerTermAndShapeTerm(TaxonomyTerm layerTerm, TaxonomyTerm termForShape, boolean loadDetails);
//	
////	TaxonomyTerm getTermFromLayerTermAndShape(TaxonomyTerm layerTerm, Shape shape);
////	TaxonomyTerm getTermFromLayerTermAndShape(TaxonomyTerm layerTerm, Shape shape, boolean loadDetails);
//	
//	void deleteShapesOfLayer(UUID layerID) throws Exception;
//
////	List<TaxonomyTermShape> findTermMappingsOfLayerShapes(TaxonomyTerm tt) throws Exception;
//
//	public UUID findLayerIDOfShape(Shape s) throws Exception;
////	List<TaxonomyTerm> findTaxonomyTermShapes(Shape s, boolean loadDetails) throws Exception;
//	
//	List<Shape> findShapesOfImport(ShapeImport shapeImport) throws Exception;
//
//	long countShapesOfImport(UUID shapeImport) throws Exception;
//
//	List<ShapeInfo> findShapesOfImport(UUID shapeImport) throws Exception;
//
//	List<ShapeInfo> findShapeWithinBounds(String bounds) throws Exception;
//	
//	List<Shape> findShapesEnclosingGeometry(Shape s) throws Exception;
////	List<Shape> findShapesOfLayerEnclosingGeometry(Shape s, TaxonomyTerm layerTerm) throws Exception;
////	List<Shape> findShapesOfLayerEnclosingGeometry(Shape s, TaxonomyTerm layerTerm, TaxonomyTerm term) throws Exception;
//	
//	List<Shape> findShapesEnclosingGeometry(Geometry geometry) throws Exception;
////	List<Shape> findShapesOfLayerEnclosingGeometry(Geometry geometry, TaxonomyTerm layerTerm) throws Exception;
////	List<Shape> findShapesOfLayerEnclosingGeometry(Geometry geometry, TaxonomyTerm layerTerm, TaxonomyTerm term) throws Exception;
//	
////	boolean existShapesOfTerm(TaxonomyTerm tt) throws Exception;
//
//	ShapeInfo getShape(UUID id) throws Exception;
//
//	Bounds getShapeBounds(UUID id) throws Exception;
//
//	Shape createFromGeometry(Project project, String geometry) throws Exception;
//
//	void updateGeometry(UUID id, String geometry) throws Exception;
//
//	String getGeometry(UUID id) throws Exception;
//	
//	String getBoundingBoxByProjectName(String projectName) throws Exception;
//	
//	String getBoundingBoxByProjectNameAndTenant(String projectName, String tenantName) throws Exception;
//
//	void update(Shape s) throws Exception;
//
//	void delete(List<String> shapes) throws Exception;
//
//	GeographyHierarchy getDefaultGeographyHierarchy() throws Exception;
//	
//	GeographyHierarchy getGeographyHierarchy(GeocodeSystem geogTaxonomy) throws Exception;
//
//	List<Geocode> geoLocate(double x, double y) throws Exception;
//
//	List<GeoLocation> termLocate(SearchType searchType, String term, Principal principal) throws Exception;
//	
//	void createShapesOfLayer(Collection<Shape> shapes);
//
////	Map<UUID, List<Geocode>> getBreadcrumbs(Coords coords) throws Exception;
//	List<String> getBreadcrumbs(Coords coords) throws Exception;
//	
//	List<GeoLocation> attributeLocate(SearchType searchType, Map<String, String> attributes, Principal principal) throws Exception;
//
//	Shape createFromGeometryPolygon(Project project, NewProjectData npd, Principal creator) throws Exception;
//	WfsShapeInfo getShapesFromShapefile(String pathName, String termId, int srid, String charset, boolean forceLonLat,
//			Map<String, Map<String, AttributeInfo>> attrInfo, Principal principal, boolean forceOverwriteMappings)
//			throws Exception;
//}