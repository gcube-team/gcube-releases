package gr.cite.geoanalytics.logicallayer;

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermShapeMessenger;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;
import gr.cite.gaap.datatransferobjects.layeroperations.NameTaxonomyPair;
import gr.cite.gaap.datatransferobjects.layeroperations.ShapeBoundaryRequest;
import gr.cite.gaap.datatransferobjects.layeroperations.TaxonomyTermAttributePair;
import gr.cite.gaap.servicelayer.ShapeInfo.ShapeInfoMessenger;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
@Service
public class HttpLayerOperations implements NodeAwareLayerOperations {
	
	private URL node;
	private Client client;
	private WebResource webResource = null;
	private Protocol protocol;
	
	public URL getNode() {
		return node;
	}

	public void setNode(URL node) {
		this.node = node;
		this.webResource = client.resource(this.node + "/" + Configuration.GEOSPATIAL_OPERATION_SERVICE);
	}
	
	@Override
	public String getNodeFromURL() {
		return getNode().toString().replaceFirst(protocol.getName(), "");
	}
	
	@Inject
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	@Inject
	public void setClient(Client client) {
		this.client = client;
	}
	
	@Override
	public Set<String> getAttributeValuesOfShapesByTerm(TaxonomyTermMessenger layerTerm, Attribute attr) throws Exception {
		return webResource.
				path(GET_ATTRIBUTE_VALUES_OF_SHAPES_BY_TERM).
				entity(new TaxonomyTermAttributePair(layerTerm, attr), MediaType.APPLICATION_JSON).
				post(new GenericType<Set<String>>() { });
	}

	@Override
	public List<ShapeMessenger> getShapesOfTerm(String termName, String termTaxonomy) throws Exception {
		return webResource.
				path(GET_SHAPES_OF_TERM).
				entity(new NameTaxonomyPair(termName, termTaxonomy), MediaType.APPLICATION_JSON).
				post(new GenericType<List<ShapeMessenger>>() { });
	}
	
	@Override
	public void generateShapeBoundary(TaxonomyTermMessenger layerTerm, TaxonomyTermMessenger boundaryTerm, PrincipalMessenger user) throws Exception {
		webResource.
				path(GENERATE_SHAPE_BOUNDARY).
				entity(new ShapeBoundaryRequest(layerTerm, boundaryTerm, user), MediaType.APPLICATION_JSON).
				post(Void.class);
		
	}

	@Override
	public List<TaxonomyTermShapeMessenger> findTermMappingsOfLayerShapes(TaxonomyTermMessenger layerTerm) throws Exception {
		return webResource.
				path(FIND_TERM_MAPPINGS_OF_LAYER_SHAPES).
				entity(layerTerm, MediaType.APPLICATION_JSON).
				post(new GenericType<List<TaxonomyTermShapeMessenger>>() { });
	}

	@Override
	public List<ShapeInfoMessenger> getShapeInfoForTerm(String termName, String termTaxonomy) throws Exception {
		
		return webResource.
				path(GET_SHAPE_INFO_FOR_TERM).
				entity(new NameTaxonomyPair(termName, termTaxonomy), MediaType.APPLICATION_JSON).
				post(new GenericType<List<ShapeInfoMessenger>>() { });
	}
}
