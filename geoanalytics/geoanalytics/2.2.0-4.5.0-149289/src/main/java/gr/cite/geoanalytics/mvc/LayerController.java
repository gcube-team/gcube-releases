package gr.cite.geoanalytics.mvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import gr.cite.gaap.datatransferobjects.GenericResponse;
import gr.cite.gaap.datatransferobjects.GenericResponse.Status;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.datatransferobjects.LayerMessengerForAdminPortlet;
import gr.cite.gaap.datatransferobjects.UpdateResponse;
import gr.cite.geoanalytics.dataaccess.entities.layer.DataSource;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTenant;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerBounds;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.PublishConfig;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.geoanalytics.manager.TenantManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.util.http.CustomResponseEntity;

@Controller
public class LayerController {

	private static final Logger logger = LoggerFactory.getLogger(LayerController.class);
	
	@Autowired private LayerManager layerManager;
	@Autowired private ImportManager importManager;
	@Autowired private TenantManager tenantManager;
	@Autowired private SecurityContextAccessor  securityContextAccessor;
	@Autowired private ConfigurationManager configurationManager;
	
//	private static ObjectMapper mapper = new ObjectMapper();
	
	
	@RequestMapping(method = RequestMethod.GET, value = {"/getLayerById"} )
	public @ResponseBody Layer getLayerById(@RequestParam String layerId) throws Exception {
		logger.debug("Finding layer by id: "+layerId);
		return layerManager.findLayerById(UUID.fromString(layerId));
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = {"/createLayerSpark"}, consumes={"application/json","application/x-www-form-urlencoded", "application/xml"})//"application/x-www-form-urlencoded",
	public @ResponseBody String createLayerSpark(RequestEntity<String> requestEntity) {
		
		Layer layer = new Gson().fromJson(requestEntity.getBody(), Layer.class);
		
		Date now = new Date();
		layer.setCreationDate(now);
		layer.setLastUpdate(now);
		logger.debug("Creating layer...");
		String layerID;
		try {
			//copy the tenants first
			List<LayerTenant> layerTenants = new ArrayList<LayerTenant>(layer.getLayerTenants());
			//createLayer
			layer.setLayerTenants(null); //null them (or hibernate will whine)
			layerID = layerManager.createLayer(layer);
			layer.setId(UUID.fromString(layerID)); //set back to layer the generated id by the db
			//and set tenant entries also
			for(LayerTenant layerTenant : layerTenants){
				layerTenant.setLayer(layer);
				layerTenant.setTenant(tenantManager.findById(layerTenant.getTenant().getId().toString())); //the layerTenant.getTenant().getId() is the only not null in object
				layerManager.createLayerTenant(layerTenant);
			}
			
			//add also the layer config (should be removed in the near future)
			LayerBounds layerBounds = new LayerBounds();
			layerBounds.setMinX(0); layerBounds.setMinY(0);	layerBounds.setMaxX(0);	layerBounds.setMaxY(0);
			
			LayerConfig layerConfig = new LayerConfig();
			layerConfig.setName(layer.getName());
			layerConfig.setLayerId(layer.getId().toString());
			layerConfig.setBoundingBox(layerBounds);
			layerConfig.setStyle(layer.getStyle());
			layerConfig.setDataSource(DataSource.PostGIS);
			
			//TODO: this addLayerConfig should be removed in the feature... should be added on LayerManager.addLayer()
			this.configurationManager.addLayerConfig(layerConfig);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			layerID = "";
		}
		logger.debug("Layer created! LayerID="+layerID);
		return layerID;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = {"/deleteLayerSpark"}, consumes={"application/json","application/x-www-form-urlencoded", "application/xml"})//"application/x-www-form-urlencoded",
	public @ResponseBody String deleteLayerSpark(RequestEntity<String> requestEntity) {
		
		Layer layer = new Gson().fromJson(requestEntity.getBody(), Layer.class);
		
		logger.debug("Deleting layer with id: "+layer.getId().toString());
		try {
			layerManager.deleteLayerFromInfra(layer.getId().toString());
//			configurationManager.removeLayerConfig(layer.getId());
		} 
		catch (Exception e1) {
			return "";
		}
		logger.debug("Layer deleted!");
		return layer.getId().toString();
		
	}
	
	
	
	
	@RequestMapping(method = RequestMethod.POST, value = {"/layers/createLayer"}, consumes="application/json")
	public @ResponseBody void createLayer(@RequestBody Layer layer) throws Exception {
		Date now = new Date();
		layer.setCreationDate(now);
		layer.setLastUpdate(now);
		logger.debug("Creating layer...");
		String layerID = layerManager.createLayer(layer);
		logger.debug("Layer created! LayerID="+layerID);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/layers/updateLayer"}, consumes="application/json")
	public @ResponseBody void updateLayer(@RequestBody Layer layer) throws Exception {
		logger.debug("Updating layer with id: "+layer.getId().toString());
		layerManager.updateLayer(layer);
		logger.debug("Layer updated!");
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/layers/deleteLayer"}, consumes="application/json")
	public @ResponseBody void deleteLayer(@RequestBody Layer layer) throws Exception {
		logger.debug("Deleting layer with id: "+layer.getId().toString());
		layerManager.deleteLayer(layer);
		logger.debug("Layer deleted!");
	}
	
	
//	@RequestMapping(method = RequestMethod.POST, value = {"/addLayerTenant"}, consumes="application/json")
//	public @ResponseBody void addLayerTenant(@RequestBody LayerTenant layerTenant) throws Exception {
//		Date now = new Date();
//		layer.setCreationDate(now);
//		layer.setLastUpdate(now);
//		layerManager.createLayer(layer, layer.getLayerReplication().getReplicationFactor());
//	}
	
	
//	@RequestMapping(method = RequestMethod.GET, value = {"/listLayerTenants"} )
//	public @ResponseBody List<LayerTenant> listLayerTenants() throws Exception {
//		return layerManager.getLayerTenants();
//	}
	
	@RequestMapping(value= "/layers/listLayersByTenant" , method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody Set<LayerMessengerForAdminPortlet> getLayersByTenant(/*HttpServletRequest request*/) {
		logger.debug("Getting Layers by Tenant...");
		Tenant tenant = securityContextAccessor.getTenant();
		try {
			Set<LayerMessengerForAdminPortlet> response = layerManager.getLayersInfoOfTenant(tenant);
			
			logger.debug("Getting Layers by Tenant has been succeeded");
			return response;

		} catch (Exception e) {
			logger.error("Error while retrieving layers for tenant " + tenant.getName());
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value= "/layers/updateLayer" , method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody UpdateResponse editLayer(@RequestBody LayerMessengerForAdminPortlet lmfa)
	{
		
		try {
			UUID layerId = UUID.fromString(lmfa.getId());
			logger.debug("Updating layer with id: "+ layerId +" and any related information...");
		
			Layer layer = layerManager.findLayerById(layerId);
			if(layer == null) return new UpdateResponse(false, "Layer " + layerId + " not found");
			
			importManager.editLayer(layerId, lmfa);
			
			logger.debug("Layer with id: "+ layerId+" and any related information has been updaetd successfully");
			return new UpdateResponse(true, "Ok");
		} catch(Exception e) {
			logger.error("An error has occurred during layer editing", e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value= "/layers/getLayerStyle" , method=RequestMethod.POST, consumes={"application/json"}, produces={"application/json"})
	public @ResponseBody ResponseEntity<?> getLayerStyle(@RequestBody String layerID)
	{
		
		try {
			UUID layerId = UUID.fromString(layerID);
			logger.debug("Getting layer's style with layer id: "+ layerId +"...");
		
			Layer layer = layerManager.findLayerById(layerId);
			if(layer == null) return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Layer " + layerId + " not found");
			String style = layer.getStyle();
			
			logger.debug("Layer's style : "+ style+"  has been retrieved successfully");
			return new CustomResponseEntity<String>(HttpStatus.OK, style);
		} catch(Exception e) {
			logger.error("An error has occurred while getting layer's style", e);
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "An error has occurred while getting layer's style",e);
		}
	}
	
	@RequestMapping(value = "/layers/listGeocodeSystems", method = RequestMethod.POST, consumes= {"application/json"}, produces={"application/json"})
	public @ResponseBody GenericResponse getAllGeocodeSystems(HttpServletRequest request) {
		logger.debug("Getting Geocodes...");
		try {
			return new GenericResponse(Status.Success, layerManager.listGeocodeSystmes(), "geocodeSystems");
		} catch(Exception e) {
			e.printStackTrace();
			return new GenericResponse(Status.Failure, null, "geocodeSystems failure");
		}
		
	}
	
}
