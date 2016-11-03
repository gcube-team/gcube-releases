package gr.cite.geoanalytics.logicallayer;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermShapeMessenger;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;
import gr.cite.gaap.servicelayer.ShapeInfo.ShapeInfoMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;

import java.net.URL;
import java.util.List;
import java.util.Set;

public abstract class NodeAwareLayerOperationsDecorator implements NodeAwareLayerOperations {

	public NodeAwareLayerOperations nodeAwareLayerOperationsDecorated;
	
	public NodeAwareLayerOperationsDecorator(NodeAwareLayerOperations nodeAwareLayerOperationsDecorated) {
		this.nodeAwareLayerOperationsDecorated = nodeAwareLayerOperationsDecorated;
	}
	
	public NodeAwareLayerOperationsDecorator(){}

	public void setNode(URL url) {
		nodeAwareLayerOperationsDecorated.setNode(url);
	}
	
	public URL getNode() {
		return nodeAwareLayerOperationsDecorated.getNode();
	}

	@Override
	public String getNodeFromURL() {
		return nodeAwareLayerOperationsDecorated.getNodeFromURL();
	}

	@Override
	public Set<String> getAttributeValuesOfShapesByTerm(TaxonomyTermMessenger taxonomyTermMessenger, Attribute attr) throws Exception {
		return nodeAwareLayerOperationsDecorated.getAttributeValuesOfShapesByTerm(taxonomyTermMessenger, attr);
	}

	@Override
	public void generateShapeBoundary(TaxonomyTermMessenger layerTermMessenger, TaxonomyTermMessenger boundaryTermMessenger, PrincipalMessenger principalMessenger) throws Exception {
		nodeAwareLayerOperationsDecorated.generateShapeBoundary(layerTermMessenger, boundaryTermMessenger, principalMessenger);
		
	}

	@Override
	public List<ShapeMessenger> getShapesOfTerm(String termName, String termTaxonomy) throws Exception {
		return nodeAwareLayerOperationsDecorated.getShapesOfTerm(termName, termTaxonomy);
	}

	@Override
	public List<TaxonomyTermShapeMessenger> findTermMappingsOfLayerShapes(TaxonomyTermMessenger layerTermMessenger) throws Exception {
		return nodeAwareLayerOperationsDecorated.findTermMappingsOfLayerShapes(layerTermMessenger);
	}

	@Override
	public List<ShapeInfoMessenger> getShapeInfoForTerm(String termName, String termTaxonomy) throws Exception {
		return nodeAwareLayerOperationsDecorated.getShapeInfoForTerm(termName, termTaxonomy);
	}
	
	 
}
