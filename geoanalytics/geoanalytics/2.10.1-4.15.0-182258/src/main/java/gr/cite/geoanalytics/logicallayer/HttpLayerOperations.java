package gr.cite.geoanalytics.logicallayer;

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.JerseyClient;
import org.springframework.stereotype.Service;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.GeocodeMessenger;
import gr.cite.gaap.datatransferobjects.GeocodeShapeMessenger;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;
import gr.cite.gaap.datatransferobjects.layeroperations.NameTaxonomyPair;
import gr.cite.gaap.datatransferobjects.layeroperations.ShapeBoundaryRequest;
import gr.cite.gaap.datatransferobjects.layeroperations.GeocodeAttributePair;
import gr.cite.gaap.servicelayer.ShapeInfo.ShapeInfoMessenger;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;

@Service
public class HttpLayerOperations implements NodeAwareLayerOperations {
	
	private URL node;
	private Client client;
	private WebTarget webTarget = null;
	private Protocol protocol;
	
	public URL getNode() {
		return node;
	}

	public void setNode(URL node) {
		this.node = node;
		this.webTarget = client.target(this.node + "/" + Configuration.GEOSPATIAL_OPERATION_SERVICE);
	}
	
	@Override
	public String getNodeFromURL() {
		return getNode().toString().replaceFirst(protocol.getName(), "");
	}
	
	@Inject
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	public void setClient(Client Client) {
		this.client = Client;
	}
	
	@Override
	public Set<String> getAttributeValuesOfShapesByTerm(GeocodeMessenger layerTerm, Attribute attr) throws Exception {

		Invocation.Builder invocationBuilder = webTarget.
				path(GET_ATTRIBUTE_VALUES_OF_SHAPES_BY_TERM).request( MediaType.APPLICATION_JSON);
     	Response response = invocationBuilder.post(Entity.entity(new GeocodeAttributePair(layerTerm, attr), MediaType.APPLICATION_JSON));
     	return response.readEntity(new GenericType<Set<String>>() { });
	}

	@Override
	public List<ShapeMessenger> getShapesOfTerm(String termName, String termTaxonomy) throws Exception {

		Invocation.Builder invocationBuilder = webTarget.
				path(GET_ATTRIBUTE_VALUES_OF_SHAPES_BY_TERM).request( MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(new NameTaxonomyPair(termName, termTaxonomy), MediaType.APPLICATION_JSON));

		return response.readEntity(new GenericType<List<ShapeMessenger>>() { });
	}
	
	@Override
	public void generateShapeBoundary(GeocodeMessenger layerTerm, GeocodeMessenger boundaryTerm, PrincipalMessenger user) throws Exception {
		Invocation.Builder invocationBuilder = webTarget.
				path(GENERATE_SHAPE_BOUNDARY).request(MediaType.APPLICATION_JSON);
		invocationBuilder.post(Entity.entity(new ShapeBoundaryRequest(layerTerm, boundaryTerm, user), MediaType.APPLICATION_JSON));

	}

//	@Override
//	public List<TaxonomyTermShapeMessenger> findTermMappingsOfLayerShapes(TaxonomyTermMessenger layerTerm) throws Exception {
//		return webResource.
//				path(FIND_TERM_MAPPINGS_OF_LAYER_SHAPES).
//				entity(layerTerm, MediaType.APPLICATION_JSON).
//				post(new GenericType<List<TaxonomyTermShapeMessenger>>() { });
//	}
//
//	@Override
//	public List<ShapeInfoMessenger> getShapeInfoForTerm(String termName, String termTaxonomy) throws Exception {
//		
//		return webResource.
//				path(GET_SHAPE_INFO_FOR_TERM).
//				entity(new NameTaxonomyPair(termName, termTaxonomy), MediaType.APPLICATION_JSON).
//				post(new GenericType<List<ShapeInfoMessenger>>() { });
//	}
}
