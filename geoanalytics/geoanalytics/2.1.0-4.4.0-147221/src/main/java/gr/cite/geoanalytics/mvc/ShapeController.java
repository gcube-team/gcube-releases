package gr.cite.geoanalytics.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import gr.cite.clustermanager.layers.DataMonitor;
import gr.cite.gaap.datatransferobjects.Coords;
import gr.cite.gaap.datatransferobjects.ServiceResponse;
import gr.cite.gaap.datatransferobjects.ShapeImportInstance;
import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.ShapeSearchSelection;
import gr.cite.gaap.datatransferobjects.LayerInfo;
import gr.cite.gaap.datatransferobjects.LayerMessengerForAdminPortlet;
import gr.cite.gaap.datatransferobjects.GeocodeMessenger;
import gr.cite.gaap.datatransferobjects.UpdateResponse;
import gr.cite.gaap.datatransferobjects.ShapeSearchSelection.GeoSearchType;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.ShapeInfo;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.gaap.servicelayer.GeospatialBackendClustered;
import gr.cite.gaap.utilities.HtmlUtils;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.geoanalytics.manager.TenantManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vividsolutions.jts.io.WKTReader;

@Controller
public class ShapeController {
	private static final Logger log = LoggerFactory.getLogger(ShapeController.class);
	
	@Autowired private GeospatialBackendClustered geospatialBackendClustered;
	@Autowired private ImportManager importManager;
	@Autowired private GeocodeManager taxonomyManager;
	@Autowired private LayerManager layerManager;
	@Autowired private SecurityContextAccessor securityContextAccessor;
	
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/getBounds"}, consumes="application/json")
	public @ResponseBody Bounds getShapeBounds(String id) throws Exception
	{
		log.debug("Getting shape bounds...");
		return geospatialBackendClustered.getShapeBounds(UUID.fromString(id));
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/retrieveShape"}, consumes="application/json")
	public @ResponseBody ShapeMessenger getSingleShape(
			@RequestBody String id) throws Exception
	{
		log.debug("Retrieving shape by id: "+id);
		Shape s = geospatialBackendClustered.findShapeById(UUID.fromString(id));
		if(s == null) {
			log.warn("There is no shape with id: "+id);
			return null;
		}
		
		ShapeMessenger sm = new ShapeMessenger();
		sm.setId(s.getId().toString());
		sm.setCode(s.getCode());
		sm.setExtraData(s.getExtraData());
		sm.setGeometry(s.getGeography().toText());
//		sm.setImportId(s.getShapeImport().toString());
		sm.setName(s.getName());
		sm.setShapeClass(s.getShapeClass());
//		if(si.getLayerID() != null){
//			sm.setTermName(si.getTerm().getName());
//			sm.setTermTaxonomy(si.getTerm().getTaxonomy().getName());
//		}
		log.debug("Retrieving shape has been succeeded");
		return sm;
	
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/shapes/retrieveByTerm"}, consumes="application/json")
	public @ResponseBody ShapeMessenger getShapeByTerm(
			@RequestBody LayerInfo term) throws Exception
	{
		log.debug("Retrieving shapes by layer id: "+term.getLayerID());
		GeocodeSystem t = taxonomyManager.findGeocodeSystemByName(term.getGeocodeSystem(), false);
		if(t == null) {
			log.error("Geocode " + term.getGeocodeSystem() + " not found");
			throw new Exception("Geocode " + term.getGeocodeSystem() + " not found"); //this will commonly be the geography taxonomy, but no check is enforced
		}
		
		Geocode tt = taxonomyManager.findTermByNameAndTaxonomy(term.getlayerName(), term.getGeocodeSystem(), false);
		if(tt == null){
			log.error("Geocode " + term.getGeocodeSystem() + " or Layer " + term.getlayerName() + " not found");
			throw new Exception("Geocode " + term.getGeocodeSystem() + " or Layer " + term.getlayerName() + " not found");
		}
		
		Shape s = taxonomyManager.getShapeOfTerm(tt, true);
		if(s == null) {
			log.error("There is no shape with geocode: "+tt.getName());
			return null;
		}
		
		ShapeMessenger sm = new ShapeMessenger();
		sm.setId(s.getId().toString());
		sm.setCode(s.getCode());
		sm.setExtraData(s.getExtraData());
		sm.setGeometry(s.getGeography().toText());
//		if(s.getShapeImport() != null) sm.setImportId(s.getShapeImport().toString());
//		sm.setName(s.getName());
		sm.setShapeClass(s.getShapeClass());
	
		log.debug("Retrieving shapes has been succeeded");
		return sm;
	
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/shapes/searchFast"}, consumes="application/json")
	public @ResponseBody List<ShapeMessenger> searchShapesFast(@RequestBody ShapeSearchSelection selection) throws Exception {
		log.debug("Searching shapes in a fast way...");
		List<ShapeMessenger> shapes = new ArrayList<ShapeMessenger>();
		List<ShapeMessenger> filtered = new ArrayList<ShapeMessenger>();
		
		if(selection.getTerms() != null && !selection.getTerms().isEmpty())	{
			shapes.addAll(geospatialBackendClustered.getShapeMessengerForLayer(UUID.fromString(selection.getTerms().get(0))));		
			filtered = shapes;
		} else if(selection.getImportInstances() != null) {			
			if(selection.getImportInstances() != null){	 //nothing to search for if null			
				for(String importId : selection.getImportInstances()){
					filtered.addAll(geospatialBackendClustered.getShapeMessengerForLayer(UUID.fromString(importId)));
				}
			}
		}
		log.debug("Searching shapes in a fast way has been succeeded");
		return filtered;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/search"}, consumes="application/json")
	public @ResponseBody List<ShapeMessenger> searchShapes(@RequestBody ShapeSearchSelection selection) throws Exception
	{
		log.debug("Searching shapes...");
		List<ShapeInfo> shapes = new ArrayList<ShapeInfo>();
		List<ShapeInfo> filtered = new ArrayList<ShapeInfo>();
		if(selection.getGeoSearchType() == GeoSearchType.None)
		{
			if(selection.getTerms() != null && !selection.getTerms().isEmpty())
			{
				for(String term : selection.getTerms())
				{
					shapes.addAll(geospatialBackendClustered.getShapeInfoForLayer(UUID.fromString(term)));
					
				}
				if(selection.getImportInstances() != null && !selection.getImportInstances().isEmpty())
				{
//					for(ShapeInfo s : shapes)
//					{
//						for(String importId : selection.getImportInstances())
//						{
//							if(s.getShape().getShapeImport().toString().equals(importId))
//								filtered.add(s);
//						}
//					}
				}else
					filtered = shapes;
			}
			else 
				if(selection.getImportInstances() != null)
			{
				if(selection.getImportInstances() != null) //nothing to search for if null
				{
//					for(String importId : selection.getImportInstances())
//						filtered.addAll(shapeManager.findShapesOfImport(UUID.fromString(importId)));
				}
			}else if(selection.getId() != null) {
				ShapeInfo foundShape = geospatialBackendClustered.findShapeByIdInfo(selection.getId());
				if(foundShape != null)
					filtered.add(foundShape);
			}
		}else
		{
			switch(selection.getGeoSearchType())
			{
			case BoundingBox:
				shapes = geospatialBackendClustered.findShapeWithinBounds(selection.getGeometry());
				break;
			case Proximity:
				//TODO
				break;
			case Overlap:
				//TODO
				break;
			}
			
			for(ShapeInfo s : shapes)
			{
//				if(selection.getImportInstances() != null && !selection.getImportInstances().isEmpty())
//				{
//					for(String importId : selection.getImportInstances())
//					{
//						if(s.getShape().getShapeImport().toString().equals(importId))
				filtered.add(s);
//					}
//				}
			}
		}
		
		List<ShapeMessenger> res = new ArrayList<ShapeMessenger>();
	
		for(ShapeInfo s : filtered)
		{
			ShapeMessenger sm = new ShapeMessenger();
			sm.setId(s.getShape().getId().toString());
			sm.setCode(s.getShape().getCode());
			sm.setExtraData(s.getShape().getExtraData());
			sm.setGeometry(s.getShape().getGeography().toText());
//			if(s.getShape().getShapeImport() != null)
//				sm.setImportId(s.getShape().getShapeImport().toString());
			sm.setName(s.getShape().getName());
			sm.setShapeClass(s.getShape().getShapeClass());
			Layer layer = layerManager.findLayerById(s.getLayerID());
			
			if(layer != null)
			{
				sm.setLayerId(layer.getId().toString());
				sm.setLayerGeocodeSystem(layer.getGeocodeSystem().getName());
			}
			res.add(sm);
		}
		log.debug("Searching shapes has been succeeded");
		return res;
	}

	@RequestMapping(method = RequestMethod.GET, value = {"/shapes/layers"}, produces="application/json")
	public @ResponseBody List<LayerInfo> getLayerTerms(HttpServletRequest request) {
		log.debug("Retrieving layers...");
		Tenant tenant = securityContextAccessor.getTenant();
		try {
			List<LayerInfo> layers = new ArrayList<>();

			Function<Layer, LayerInfo> createLayerInfo = new Function<Layer, LayerInfo>() {
				public LayerInfo apply(Layer layer){
					LayerInfo layerInfo = new LayerInfo();
					if(layer.getGeocodeSystem()!=null)
						layerInfo.setGeocodeSystem(layer.getGeocodeSystem().getName().toString());
					layerInfo.setlayerName(layer.getName());
					layerInfo.setLayerID(layer.getId().toString());			
					return layerInfo;
				}
			};
			
			List<Layer> tenantLayers = layerManager.getLayersByTenant(tenant);
			List<Layer> templateLayers = layerManager.getTemplateLayers();
			
			if(tenantLayers != null){
				layers.addAll(tenantLayers.stream().map(createLayerInfo).collect(Collectors.toList()));
			}
			
			if(templateLayers != null){
				layers.addAll(templateLayers.stream().map(createLayerInfo).collect(Collectors.toList()));
			}					
			log.debug("Retrieving layers has been succeeded");			
			return layers;
		} catch (Exception e) {
			log.error(null, e);
		}
		
		return null;	
	}
	
//	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/importInstances"}, consumes="application/json")
//	public @ResponseBody ServiceResponse getImportInstances() throws Exception {
//		log.debug("ShapeImporting instances... ");
//		try {
//			List<ShapeImportInstance> shapeImportInstances = shapeImportManager.getImportInstances(true);
//			log.debug("ShapeImporting instances has been succeeded");
//			return new ServiceResponse(true, shapeImportInstances, "shape import instances returned");
//		} catch(Exception e) {
//			log.error("Error while importing shape instances", e);
//			return new ServiceResponse(false, null, e.getMessage());
//		}
//	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/update"}, consumes="application/json")
	public @ResponseBody UpdateResponse update(@RequestBody ShapeMessenger sm)
	{
		log.debug("Updating shape with id: "+sm.getId());
		try
		{
			Shape s = new Shape();
			s.setId(UUID.fromString(sm.getId()));
			s.setExtraData(HtmlUtils.htmlEscape(sm.getExtraData().trim()));
			if(sm.getGeometry() != null) s.setGeography(new WKTReader().read(sm.getGeometry()));
//			s.setName(HtmlUtils.htmlEscape(sm.getName().trim()));
			s.setShapeClass(sm.getShapeClass());
			s.setCode(HtmlUtils.htmlEscape(sm.getCode().trim()));
			geospatialBackendClustered.update(s);
			
			log.debug("Updating shape has been succeeded");
			return new UpdateResponse(true, "OK");
		}catch(Exception e)
		{
			log.error("An error has occurred while updating shape " + sm.getId(), e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/delete"}, consumes="application/json")
	public @ResponseBody UpdateResponse delete(@RequestBody List<String> shapes)
	{
		log.debug("Deleting shapes...");
		try
		{
			geospatialBackendClustered.delete(shapes);
			log.debug("Deleting shapes has been succeeded");
			return new UpdateResponse(true, "Ok");
		}catch(Exception e)
		{
			log.error("An error has occurred while deleting shapes", e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/shapes/deleteLayer"}, consumes="application/json")
	public @ResponseBody UpdateResponse deleteLayer(@RequestBody String layerId)
	{
		log.debug("Deleting layer with id: "+layerId+" and any related information...");
		try
		{
//			Layer layer = layerManager.findLayerById(UUID.fromString(layerId));
//			if(layer == null) return new UpdateResponse(false, "Layer " + layerId + " not found");
			
			Layer layer = new Layer();
			layer.setId(UUID.fromString(layerId));
			
			boolean result = importManager.deleteLayerFromInfra(layerId);
			
			if(result){
				log.debug("Deleting layer with id: "+layerId+" and any related information has been succeeded");
				return new UpdateResponse(true, "Ok");
			}
			else{
				log.debug("Deleting layer with id: "+layerId+" and any related information has FAILED");
				return new UpdateResponse(false, "Could not delete layer from Infrastructure");
			}
		}catch(Exception e)
		{
			log.error("An error has occurred during layer deletion", e);
			return new UpdateResponse(false, e.getMessage());
		}
	}	
	
	@RequestMapping(value= "/shapes/updateLayer" , method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody UpdateResponse editLayer(@RequestBody LayerMessengerForAdminPortlet lmfa)
	{
		
		try {
			UUID layerId = UUID.fromString(lmfa.getId());
			log.debug("Updating layer with id: "+ layerId +" and any related information...");
		
			Layer layer = layerManager.findLayerById(layerId);
			if(layer == null) return new UpdateResponse(false, "Layer " + layerId + " not found");
			
			importManager.editLayer(layerId, lmfa);
			
			log.debug("Layer with id: "+ layerId+" and any related information has been updaetd successfully");
			return new UpdateResponse(true, "Ok");
		} catch(Exception e) {
			log.error("An error has occurred during layer editing", e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	

	
	@RequestMapping(value= "/shapes/mostSpecificBreadcrumbsByCoordinates" , method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody List<String> mostSpecificBreadcrumbsByCoordinates(@RequestBody Coords coords) throws Exception {
		log.debug("Retrieving most specific breadcrums by coordinates...");
		try{
			return geospatialBackendClustered.getBreadcrumbs(coords);	
		} catch(Exception e) {
			log.error("Error while retrieving most specific breadcrums by coordinates");
			return null;
		}
	}
	
	
	@RequestMapping(value= "/shapes/breadcrumbsByCoordinates" , method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody List<String> breadcrumbsByCoordinates(@RequestBody Coords coords) throws Exception {
		log.debug("Retrieving specific breadcrums by coordinates...");
		try {
			return geospatialBackendClustered.getBreadcrumbs(coords);
		} catch(Exception e) {
			log.error("Error while retrieving specific breadcrums by coordinates");
			return null;
		}
	}
	
	private List<GeocodeMessenger> getBreadcrumbMessenger(List<Geocode> breadcrumb) {
		return breadcrumb == null ? null : 
			breadcrumb.stream().
				map(t -> {
					GeocodeMessenger ttm = new GeocodeMessenger();
					ttm.setId(t.getId().toString());
					ttm.setName(t.getName());
					return ttm;
				}).
				collect(Collectors.toList());
	}

}
