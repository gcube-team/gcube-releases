package gr.cite.gos.resources;

import gr.cite.clustermanager.DataCreator;
import gr.cite.clustermanager.DataMonitor;
import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermShapeMessenger;
import gr.cite.gaap.datatransferobjects.layeroperations.NameTaxonomyPair;
import gr.cite.gaap.datatransferobjects.layeroperations.ShapeBoundaryRequest;
import gr.cite.gaap.datatransferobjects.layeroperations.TaxonomyTermAttributePair;
import gr.cite.gaap.servicelayer.EnvironmentInitializer;
import gr.cite.gaap.servicelayer.ShapeInfo.ShapeInfoMessenger;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
import gr.cite.geoanalytics.logicallayer.LayerOperations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Component;

@Path("/")
@Component
public class GeoServerResource {
	
	private DataMonitor dataMonitor;
	private DataCreator dataCreator;
	private LayerOperations layerOperations;
	private EnvironmentInitializer environmentInitializer;
	
	public GeoServerResource(DataMonitor dataMonitor, 
				DataCreator dataCreator, LayerOperations layerOperations, EnvironmentInitializer enviromentEnvironmentInitializer) throws Exception{;
		this.dataMonitor = dataMonitor;
		this.dataCreator = dataCreator;
		this.layerOperations = layerOperations;
		this.environmentInitializer = enviromentEnvironmentInitializer;
		
	}

	@PostConstruct
	private void createZnodeData() throws Exception{
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					environmentInitializer.initializeEnvironment();
					dataCreator.create();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	@GET
	@Path("{name}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getGeoserverData(@PathParam("name") String name) throws IOException, GeoServerBridgeException {
		
		Set<String> geoservers = this.dataMonitor.getLayerKeyData().get(name);
		String data = null;
		
		if (geoservers != null){
			data = new ArrayList<String>(geoservers).get(((int)(Math.random() * 10) * geoservers.size())/10);
		}
		
		return Response.status(200).entity(data).build();
	}
	
	@POST
	@Path(LayerOperations.GET_SHAPES_OF_TERM)
	@Produces({MediaType.APPLICATION_JSON})
	public Response getShapesOfTerm(NameTaxonomyPair nameTaxonomyPair){
		
		List<ShapeMessenger> shapeMessengers = new ArrayList<ShapeMessenger>();
		try {
			shapeMessengers = this.layerOperations.getShapesOfTerm(nameTaxonomyPair.termName, nameTaxonomyPair.termTaxonomy);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Status.OK).entity(shapeMessengers).build();
	}
	
	@POST
	@Path(LayerOperations.FIND_TERM_MAPPINGS_OF_LAYER_SHAPES)
	@Produces({MediaType.APPLICATION_JSON})
	public Response findTermMappingsOfLayerShapes(TaxonomyTermMessenger layerTerm){
		
		List<TaxonomyTermShapeMessenger> taxonomyTermShapeMessengers = new ArrayList<TaxonomyTermShapeMessenger>();
		try {
			taxonomyTermShapeMessengers = this.layerOperations.findTermMappingsOfLayerShapes(layerTerm);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Status.OK).entity(taxonomyTermShapeMessengers).build();
	}
	
	@POST
	@Path(LayerOperations.GET_ATTRIBUTE_VALUES_OF_SHAPES_BY_TERM)
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAttributeValuesOfShapesByTerm(TaxonomyTermAttributePair taxonomyTermAttributePair){
		
		Set<String> attributeValuesOfShapeByTerm = new HashSet<String>();
		try {
			attributeValuesOfShapeByTerm = this.layerOperations.getAttributeValuesOfShapesByTerm(taxonomyTermAttributePair.getLayerTerm(), taxonomyTermAttributePair.getAttr());
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Status.OK).entity(attributeValuesOfShapeByTerm).build();
	}
	
	@POST
	@Path(LayerOperations.GET_SHAPE_INFO_FOR_TERM)
	@Produces({MediaType.APPLICATION_JSON})
	public Response getShapeOfTerm(NameTaxonomyPair nameTaxonomyPair){
		
		List<ShapeInfoMessenger> ShapeInfoMessengers = new ArrayList<ShapeInfoMessenger>();
		try {
			ShapeInfoMessengers = this.layerOperations.getShapeInfoForTerm(nameTaxonomyPair.getTermName(), nameTaxonomyPair.getTermTaxonomy());
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Status.OK).entity(ShapeInfoMessengers).build();
	}
	
	@POST
	@Path(LayerOperations.GENERATE_SHAPE_BOUNDARY)
	@Produces({MediaType.APPLICATION_JSON})
	public Response getShapeOfTerm(ShapeBoundaryRequest shapeBoundaryRequesst){
		
		try {
			this.layerOperations.generateShapeBoundary(shapeBoundaryRequesst.getLayerTerm(), shapeBoundaryRequesst.getBoundaryTerm(), shapeBoundaryRequesst.getUserMessenger());
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Status.OK).build();
	}
}
