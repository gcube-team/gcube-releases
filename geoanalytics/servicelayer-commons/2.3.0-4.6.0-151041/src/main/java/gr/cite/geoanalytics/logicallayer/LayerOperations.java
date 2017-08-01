package gr.cite.geoanalytics.logicallayer;

import java.util.List;
import java.util.Set;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.GeocodeMessenger;
import gr.cite.gaap.datatransferobjects.GeocodeShapeMessenger;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;
import gr.cite.gaap.servicelayer.ShapeInfo.ShapeInfoMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;

public interface LayerOperations {
	public static final String GET_ATTRIBUTE_VALUES_OF_SHAPES_BY_TERM = "getAttributeValuesOfShapesByTerm";
	public static final String GET_SHAPES_OF_TERM = "getShapesOfTerm";
	public static final String GENERATE_SHAPE_BOUNDARY = "generateShapeBoundary";
	public static final String FIND_TERM_MAPPINGS_OF_LAYER_SHAPES = "findTermMappingsOfLayerShapes";
	public static final String GET_SHAPE_INFO_FOR_TERM = "getShapeInfoForTerm";
	
	public Set<String> getAttributeValuesOfShapesByTerm(GeocodeMessenger geocodeMessenger, Attribute attr) throws Exception;
	public void generateShapeBoundary(GeocodeMessenger layerTermMessenger, GeocodeMessenger boundaryTermMessenger, PrincipalMessenger principalMessenger) throws Exception;
	public List<ShapeMessenger> getShapesOfTerm(String termName, String termTaxonomy) throws Exception;
//	public List<TaxonomyTermShapeMessenger> findTermMappingsOfLayerShapes(TaxonomyTermMessenger layerTermMessenger) throws Exception;
//	public List<ShapeInfoMessenger> getShapeInfoForTerm(String termName, String termTaxonomy) throws Exception;
}
