package gr.cite.gaap.geospatialbackend;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Geometry;

import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.datatransferobjects.Coords;
import gr.cite.gaap.datatransferobjects.GeoLocation;
import gr.cite.gaap.datatransferobjects.GeoSearchSelection.SearchType;
import gr.cite.gaap.datatransferobjects.NewProjectData;
import gr.cite.gaap.servicelayer.ShapeInfo;
import gr.cite.gaap.servicelayer.ShapeManager.GeographyHierarchy;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;

@Service
public interface GeospatialBackend {

	Shape findShapeById(UUID id) throws IOException;

	ShapeInfo findShapeByIdInfo(UUID id) throws Exception;

	String retrieveShapeAttributeValue(Shape s, String attribute) throws Exception;

	AttributeInfo retrieveShapeAttribute(Shape s, String attribute) throws Exception;

	AttributeInfo retrieveShapeAttributeByTaxonomy(Shape s, String taxonomy) throws Exception;

	void addShapeAttribute(Shape s, String attrName, String attrValue, Taxonomy taxonomy) throws Exception;

	void setShapeAttributes(Shape s, Map<String, AttributeInfo> attrs) throws Exception;

	void updateShapeAttribute(Shape s, String attrName, String attrValue) throws Exception;

	void removeShapeAttribute(Shape s, String attrName) throws Exception;

	Map<String, AttributeInfo> consolidateAttributes(Shape s) throws Exception;

	Map<String, AttributeInfo> computeAttributes(Shape s) throws Exception;

	Map<String, AttributeInfo> retrieveShapeAttributes(Shape s) throws Exception;

	Set<String> getShapeAttributeValues(Taxonomy t) throws Exception;

	String generateShapesOfImport(TaxonomyTerm tt, Map<String, Map<String, AttributeInfo>> attrInfo,
			Map<String, Set<String>> valueMappingValues, UUID importId, String layerTermId,
			GeographyHierarchy geographyHierarchy, Principal principal) throws Exception;

	void generateShapeBoundary(TaxonomyTerm layerTerm, TaxonomyTerm boundaryTerm, Principal principal) throws Exception;

	List<Shape> getShapesOfImport(UUID importId) throws Exception;

	List<Shape> getShapesOfLayer(String termName, String termTaxonomy) throws Exception;
	
	List<Shape> getShapesOfLayer(TaxonomyTerm tt) throws Exception;

	List<ShapeInfo> getShapeInfoForTerm(String termName, String termTaxonomy) throws Exception;

	Map<String, Shape> getShapesFromLayerTerm(TaxonomyTerm layerTerm);
	Shape getShapeFromLayerTermAndShapeTerm(TaxonomyTerm layerTerm, TaxonomyTerm termForShape);
	Shape getShapeFromLayerTermAndShapeTerm(TaxonomyTerm layerTerm, TaxonomyTerm termForShape, boolean loadDetails);
	
	TaxonomyTerm getTermFromLayerTermAndShape(TaxonomyTerm layerTerm, Shape shape);
	TaxonomyTerm getTermFromLayerTermAndShape(TaxonomyTerm layerTerm, Shape shape, boolean loadDetails);
	
	void deleteShapesOfTerm(TaxonomyTerm tt) throws Exception;

	List<TaxonomyTermShape> findTermMappingsOfLayerShapes(TaxonomyTerm tt) throws Exception;

	List<TaxonomyTerm> findTaxonomyTermShapes(Shape s) throws Exception;
	List<TaxonomyTerm> findTaxonomyTermShapes(Shape s, boolean loadDetails) throws Exception;
	
	List<Shape> findShapesOfImport(ShapeImport shapeImport) throws Exception;

	long countShapesOfImport(UUID shapeImport) throws Exception;

	List<ShapeInfo> findShapesOfImport(UUID shapeImport) throws Exception;

	List<ShapeInfo> findShapeWithinBounds(String bounds) throws Exception;
	
	List<Shape> findShapesEnclosingGeometry(Shape s) throws Exception;
	List<Shape> findShapesOfLayerEnclosingGeometry(Shape s, TaxonomyTerm layerTerm) throws Exception;
	List<Shape> findShapesOfLayerEnclosingGeometry(Shape s, TaxonomyTerm layerTerm, TaxonomyTerm term) throws Exception;
	
	List<Shape> findShapesEnclosingGeometry(Geometry geometry) throws Exception;
	List<Shape> findShapesOfLayerEnclosingGeometry(Geometry geometry, TaxonomyTerm layerTerm) throws Exception;
	List<Shape> findShapesOfLayerEnclosingGeometry(Geometry geometry, TaxonomyTerm layerTerm, TaxonomyTerm term) throws Exception;
	
	boolean existShapesOfTerm(TaxonomyTerm tt) throws Exception;

	ShapeInfo getShape(UUID id) throws Exception;

	Bounds getShapeBounds(UUID id) throws Exception;

	Shape createFromGeometry(Project project, String geometry) throws Exception;

	void updateGeometry(UUID id, String geometry) throws Exception;

	String getGeometry(UUID id) throws Exception;
	
	String getBoundingBoxByProjectName(String projectName) throws Exception;
	
	String getBoundingBoxByProjectNameAndTenant(String projectName, String tenantName) throws Exception;

	void update(Shape s) throws Exception;

	void delete(List<String> shapes) throws Exception;

	GeographyHierarchy getDefaultGeographyHierarchy() throws Exception;
	
	GeographyHierarchy getGeographyHierarchy(Taxonomy geogTaxonomy) throws Exception;

	List<TaxonomyTerm> geoLocate(double x, double y) throws Exception;

	List<GeoLocation> termLocate(SearchType searchType, String term, Principal principal) throws Exception;

	Map<UUID, List<TaxonomyTerm>> getBreadcrumbs(Coords coords) throws Exception;
	
	List<GeoLocation> attributeLocate(SearchType searchType, Map<String, String> attributes, Principal principal) throws Exception;

	Set<String> getAttributeValuesOfShapesByTerm(TaxonomyTerm layerTerm, Attribute attr) throws Exception;

	void createShapeAssociationsWithLayerTerm(TaxonomyTerm taxonomyTerm, List<Shape> shapes);

	Shape createFromGeometryPolygon(Project project, NewProjectData npd, Principal creator) throws Exception;
}