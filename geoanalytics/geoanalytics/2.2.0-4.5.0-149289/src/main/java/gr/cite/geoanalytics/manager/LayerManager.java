package gr.cite.geoanalytics.manager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.cite.clustermanager.layers.DataCreatorGeoanalytics;
import gr.cite.clustermanager.layers.DataMonitor;
import gr.cite.clustermanager.model.GosDefinition;
import gr.cite.gaap.datatransferobjects.LayerMessengerForAdminPortlet;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeSystemDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.DataSource;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerImport;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTag;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTenant;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerImportDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerTagDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerTagInfo;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerTenantDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;
import gr.cite.geoanalytics.dataaccess.entities.tag.dao.TagDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager.GSManagerGeoNetworkBridge;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.gos.client.GeoserverManagement;
import gr.cite.gos.client.RasterManagement;
import gr.cite.gos.client.ShapeManagement;

@Service
public class LayerManager {

	private static final Logger logger = LoggerFactory.getLogger(LayerManager.class);

	@Autowired 	private LayerDao layerDao;
	@Autowired 	private LayerTenantDao layerTenantDao;
	@Autowired	private LayerTagDao layerTagDao;
	@Autowired	private LayerImportDao layerImportDao;
	@Autowired	private TagDao tagDao;
	@Autowired	private GeocodeSystemDao geocodeSystemDao;
	
	@Autowired 	private GeocodeManager geocodeManager;
	@Autowired	private ConfigurationManager configurationManager;
	
	@Autowired 	private DataMonitor dataMonitor;
	@Autowired 	private DataCreatorGeoanalytics dataCreatorGeoanalytics;
	
	@Autowired 	private GeoserverManagement geoserverManagement;
	@Autowired	private ShapeManagement shapeManagement;
	@Autowired 	private RasterManagement rasterManagement;
	
	@Autowired 	private ViewBuilder builder;

	@Transactional(readOnly = true)
	public Layer findLayerById(UUID layerID) throws Exception {
		return layerDao.getLayerById(layerID);
	}

	@Transactional(rollbackFor = { Exception.class })
	public String createLayer(Layer layer) throws Exception {

		List<LayerTenant> layerTenants = new ArrayList<LayerTenant>();
		if (layer.getLayerTenants() != null && !layer.getLayerTenants().isEmpty()) {
			layerTenants = new ArrayList<LayerTenant>(layer.getLayerTenants());
			layer.setLayerTenants(null);
		}
		layer.setReplicationFactor(1); //DO not remove plz
		Layer createdLayer = layerDao.create(layer);
		for (LayerTenant lt : layerTenants) {
			layerTenantDao.create(lt);
		}
		return createdLayer.getId().toString();
	}

	@Transactional(rollbackFor = { Exception.class })
	public void updateLayer(Layer layer) throws Exception {
		layerDao.update(layer);
	}

	@Transactional(rollbackFor = { Exception.class })
	public void deleteLayer(Layer layer) throws Exception {
		logger.info("Removing the layer (id: " + layer.getId() + ") entry from database");

		layerDao.delete(layer);

		logger.info("Layer " + layer.getId() + " has been removed successfully!");
	}

	@Transactional(readOnly = true)
	public Layer findLayerByName(String layerName) throws Exception {
		List<Layer> layers = layerDao.findLayersByName(layerName);
		if (layers == null || layers.size() == 0) {
			logger.debug("No layers found for layername: " + layerName);
			throw new Exception("No layers found for layername: " + layerName);
		} else if (layers.size() > 1) {
			logger.debug("Found more than 1 layers for layername: " + layerName);
			throw new Exception("Found more than 1 layers for layername: " + layerName);
		} else
			return layers.get(0);
	}

	@Transactional(readOnly = true)
	public List<Layer> getAllLayers() throws Exception {
		List<Layer> layers = layerDao.getAll();
		if (layers == null || layers.size() == 0) {
			logger.debug("No layers found");
			throw new Exception("No layers");
		} else {
			return layers;
		}
	}

	@Transactional(readOnly = true)
	public List<Layer> getLayersByTenant(Tenant tenant) throws Exception {
		List<Layer> layers = new ArrayList<Layer>();

		if (tenant != null) {
			layers = layerDao.findLayersByTenant(tenant);
		}

		return layers;
	}

	@Transactional(readOnly = true)
	public List<Layer> getLayersNotLinkedToSomeTenant() throws Exception {
		List<Layer> layers = new ArrayList<Layer>();
		layers = layerDao.findLayersNotLinkedToSomeTenant();

		return layers;
	}

	@Transactional(readOnly = true)
	public Set<LayerMessengerForAdminPortlet> getLayersInfoOfTenant(Tenant tenant) throws Exception {
		List<Layer> layers = this.getLayersByTenant(tenant);
		List<Layer> layersNotConnectedToSomeTenant = this.getLayersNotLinkedToSomeTenant();
		layers.addAll(layersNotConnectedToSomeTenant);

		Set<LayerMessengerForAdminPortlet> response = new HashSet<LayerMessengerForAdminPortlet>();

		for (Layer l : layers) {
			LayerMessengerForAdminPortlet token = new LayerMessengerForAdminPortlet();
			token.setId(l.getId().toString());
			String dateString = null;
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			dateString = df.format(l.getCreationDate());
			token.setCreated(dateString);
			
			try{
				token.setCreator(l.getCreator().getName());
			}
			catch (Exception e){
				logger.debug("Could not set creator on layer (will set it to empty string) because of error: "+e.getMessage());
				token.setCreator("");
			}
			try {
				token.setGeocodeSystem(l.getGeocodeSystem().getName());
			} catch (Exception e) {
				logger.debug("Could not set geocode system (will set it to empty string) because of error: "+e.getMessage());
				token.setGeocodeSystem("");
			}

			token.setDescription(l.getDescription());
			token.setIsTemplate(l.getIsActive());
			token.setName(l.getName());
			String status = (l.getIsActive() == 1) ? "Active" : "Inactive";
			token.setStatus(status);
			token.setReplicationFactor(l.getReplicationFactor());
			token.setStyle(l.getStyle());
			try {
				token.setTags(this.findTagnamesOfLayer(l));
			} catch (Exception e) {
				logger.error("Error while sending LayerToken", e);
			}
			LayerConfig templateLayerConfig = configurationManager.getLayerConfig(l.getId());
			if (templateLayerConfig == null) {
				continue;
			}
			response.add(token);
		}

		return response;
	}

	public Layer findTemplateLayerByGeocodeSystem(GeocodeSystem geocodeSystem) throws Exception {
		Layer templateLayer = layerDao.findTemplateLayerByGeocodeSystem(geocodeSystem);
		if (templateLayer == null) {
			throw new CustomException(HttpStatus.NOT_FOUND, "Template Layer for Geocode System " + geocodeSystem.getName() + " does not exist");
		}
		return layerDao.findTemplateLayerByGeocodeSystem(geocodeSystem);
	}

	public List<Layer> getTemplateLayers() {
		return layerDao.getTemplateLayers();
	}

	@Transactional
	public void createLayerTenant(LayerTenant layerTenant) {
		layerTenantDao.create(layerTenant);
	}

	@Transactional
	public void deleteLayerTenant(Layer layer) {
		logger.info("Removing tenants of layer with id: " + layer.getId() + " and name: " + layer.getName() + " ...");

		LayerTenant layerTenant = layerTenantDao.findLayerTenantByLayer(layer);
		if (layerTenant != null) {
			layerTenantDao.delete(layerTenant);
		}
		
		logger.info("Tenants of layer with id: " + layer.getId() + " and name: " + layer.getName() + " have been removed...");
	}

	public List<Tag> listAllTags() throws Exception {
		return tagDao.getAll();		
	}

	@Transactional
	public void deleteLayerTags(Layer layer) throws Exception {		
		logger.info("Removing tags of layer with id: " + layer.getId() + " and name: " + layer.getName() + " ...");
		
		List<LayerTag> layerTags = layerTagDao.findLayerTagsByLayer(layer);
		if (layerTags != null) {
			layerTags.forEach(layerTagDao::delete);
		}
		
		logger.info("Tags of layer with id: " + layer.getId() + " and name: " + layer.getName() + " have been removed...");
	}

	@Transactional
	public List<LayerTag> findLayerTagsByLayer(Layer layer) throws Exception{
		return layerTagDao.findLayerTagsByLayer(layer);
	}
	
	@Transactional
	public void deleteLayerTags(Tag tag) throws Exception {
		List<LayerTag> layerTags = layerTagDao.findLayerTagsByTag(tag);
		if (layerTags != null) {
			layerTags.forEach(layerTagDao::delete);
		}
	}

	@Transactional
	public void createTag(Tag tag) throws Exception {
		if (tag != null) {
			if (this.tagDao.create(tag) == null) {
				throw new Exception("Could not create " + tag);
			}
			logger.info("Created " + tag + " successfully!");
		}
	}

	@Transactional
	public void checkTagNotExists(String name) throws Exception {
		Tag tag = this.tagDao.findTagByName(name);
		if (tag != null) {
			throw new CustomException(HttpStatus.CONFLICT, "Tag \"" + name + "\" already exists!");
		}
	}


	
	@Transactional
	public boolean checkIfTagtExists(String name) {
		Tag tag = null;;
		try {
			tag = this.tagDao.findTagByName(name);
		} catch (Exception e) {
			logger.info("Tag with name: " + name + " doesn\'t exists");
		}
		
		if(tag != null) {	
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional
	public void deleteTag(Tag tag) throws Exception {
		this.deleteLayerTags(tag);
		this.tagDao.delete(tag);
	}

	@Transactional
	public void editTag(Tag tag, String name, String description) throws Exception {
		tag.setName(name);
		tag.setDescription(description);
		tagDao.update(tag);

		logger.info(tag + " has been edited successfully!");
	}

	public Tag findTagById(String id) throws Exception {
		Tag tag = tagDao.read(UUID.fromString(id));
		if (tag == null) {
			throw new CustomException(HttpStatus.NOT_FOUND, "Tag not found");
		}
		return tag;
	}

	public Tag findTagById(UUID id) throws Exception {
		Tag tag = tagDao.read(id);
		if (tag == null) {
			throw new CustomException(HttpStatus.NOT_FOUND, "Tag not found");
		}
		return tag;
	}

	@Transactional
	public List<LayerTagInfo> findTagsOfLayer(Layer layer){
		List<LayerTagInfo> tags = null;

		try {
			tags = this.layerTagDao.findTagsOfLayer(layer);
		} catch (Exception e) {
			logger.error("Could find tags of layer " + layer, e);
		}

		return tags;
	}
	
	@Transactional
	public Collection<String> findTagnamesOfLayer(Layer layer){
		return findTagsOfLayer(layer).stream().map(lt -> lt.getName()).collect(Collectors.toList());
	}

	@Transactional
	public void createTagsOfLayer(Layer layer, Collection<Tag> tags) throws Exception {
		for (Tag tag : tags) {
			Tag instance = this.tagDao.findTagByName(tag.getName());
			if (instance == null) {
				this.createTag(tag);
			} else {
				tag = instance;
			}

			LayerTag layerTag = new LayerTag();
			layerTag.setLayer(layer);
			layerTag.setTag(tag);

			if (layerTagDao.create(layerTag) == null) {
				throw new Exception("Could not create " + layerTag);
			}
		}
	}

	@Transactional
	public void createLayerImport(LayerImport layerImport) throws Exception {
		if (layerImportDao.create(layerImport) == null) {
			throw new Exception("Could not create " + layerImport);
		}
	}

	@Transactional
	public void updateLayerImport(LayerImport layerImport) throws Exception {
		if (layerImportDao.update(layerImport) == null) {
			throw new Exception("Could not update " + layerImport);
		}
	}
	
	@Transactional
	public void updateLayerStyle(UUID layerID, String styleName) throws Exception {
		Layer layer = this.layerDao.read(layerID);
		layer.setStyle(styleName);
		this.layerDao.update(layer);
	}

	@Transactional(readOnly = true)
	public List<LayerImport> getLayerImportsOfPrincipal(Principal principal) throws Exception {
		return layerImportDao.findLayerImportsOfPrincipal(principal);	
	}

	@Transactional(readOnly = true)
	public Map<UUID, String> listGeocodeSystmes() {
		Map<UUID, String> geocodeSystems = new HashMap<UUID, String>();
		try {

			geocodeSystems = geocodeSystemDao.getAll().stream().collect(Collectors.toMap(GeocodeSystem::getId, GeocodeSystem::getName));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return geocodeSystems;
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void updateLayerReplication(UUID layerID, int replicationFactor){
		Layer layer = this.layerDao.read(layerID);
		layer.setReplicationFactor(replicationFactor);
		this.layerDao.update(layer);
	}
	
	
	@Transactional(readOnly=true)
	public Collection<LayerTag> findLayerTagsByLayerAndTagName(Layer layer, Collection<String> tagNames) throws Exception {
		return layerTagDao.findLayerTagsByLayerAndTagName(layer, tagNames);
	}
	
	@Transactional(readOnly=true)
	public Collection<LayerTag> findLayerTagsByLayerAndTagNameNotInTagNamesList(Layer layer, Collection<String> tagNames) throws Exception {
		return layerTagDao.findLayerTagsByLayerAndTagNameNotInTagNamesList(layer, tagNames);
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void updateLayerTag(LayerTag lt){
		this.layerTagDao.update(lt);
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void updateLayerTag(UUID layerTagID){
		this.layerTagDao.update(this.layerTagDao.read(layerTagID));
	}
	
	@Transactional(rollbackFor = { Exception.class })
	public void relateExistingTagsWithLayer(Collection<String> tagNames, Layer layer) {
		if(tagNames.isEmpty())
			return;
		
		List<Tag> tags = tagDao.findTagsByNames(tagNames);
		
		tags.forEach(tag -> {
			LayerTag lt = new LayerTag();
			lt.setLayer(layer);
			lt.setTag(tag);
			
			layerTagDao.create(lt);
		});
	}
	
	@Transactional(rollbackFor = { Exception.class })
	public Collection<Tag> createNewTags(Collection<String> tagNames, Principal creator) {
		Set<Tag> tags = new HashSet<Tag>();
		
		tagNames.forEach(tagName -> {
			tags.add(this.createNewTag(tagName, creator));
		});
		
		return tags;
	}
	
	@Transactional(rollbackFor = { Exception.class })
	public Tag createNewTag(String tagName, Principal creator) {
		Tag tag = new Tag();
		tag.setCreationDate(new Date());
		tag.setLastUpdate(new Date());
		tag.setCreator(creator);
		tag.setDescription(tagName);
		tag.setName(tagName);
		
		return tagDao.create(tag);
	}
	
	@Transactional(rollbackFor = { Exception.class })
	public void deleteLayersStyle(String styleName) {
		List<Layer> layers;
		try {
			layers = layerDao.getLayersWithStyle(styleName);
			for(Layer layer : layers) {
				layer.setStyle("line");
				layerDao.update(layer);
			}
		} catch (Exception e) {
			logger.error("Error while deleting styles from layers and replacing them with default style");
		}
		
		return;
	}
	
	@Transactional(rollbackFor = { Exception.class })
	public void editLayersStyle(String newStyleName, String oldStyleName) {
		List<Layer> layers;
		try {
			layers = layerDao.getLayersWithStyle(oldStyleName);
			for(Layer layer : layers) {
				layer.setStyle(newStyleName);
				layerDao.update(layer);
			}
		} catch (Exception e) {
			logger.error("Error while editing styles from layers and replacing them with the new ones");
		}
		
		return;
	}
	
	@Transactional(rollbackFor = { Exception.class })
	public List<Layer> findLayersWithStyle(String styleName) {
		List<Layer> layers = null;
		try {
			layers = layerDao.getLayersWithStyle(styleName);
		} catch (Exception e) {
			logger.error("Error while editing styles from layers and replacing them with the new ones");
		}
		
		return layers;
	}
	
	@Transactional(rollbackFor = { Exception.class })
	public void editLayerGeonetwork(long geonetworkId, String layerId) {
		
		Layer layer = layerDao.getLayerById(UUID.fromString(layerId));
		layer.setGeonetwork(geonetworkId);
		
		layerDao.update(layer);
		
		return;
	}
	
	@Transactional(rollbackFor = { Exception.class })
	public void deleteLayerImport(String layerId) throws Exception {
		LayerImport layerImport = layerImportDao.read(UUID.fromString(layerId));
		this.layerImportDao.delete(layerImport);
	}

	@Transactional(rollbackFor = { Exception.class })
	private void deleteDataBaseView(String gosEndpoint, String identity) throws Exception {
		logger.info("Dropping Materialized View of \"" + identity + "\"");
		
		builder.forIdentity(identity).removeViewStatement().execute(gosEndpoint);
		
		logger.info("Materialized View of \"" + identity + "\" was dropped successfully!");
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteLayerFromInfra(String layerId) throws Exception {
		Layer layer = this.findLayerById(UUID.fromString(layerId));
		String tenantName = layer.getCreator().getTenant().getName();
		
		logger.info("Removing layer with id: " + layer.getId() + " and name: " + layer.getName() + " ...");

		try {
			Set<GosDefinition> layersGos = dataMonitor.getAvailableGosFor(layerId);
			layersGos.addAll(dataMonitor.getNotAvailableGosFor(layerId));

			Set<Boolean> results = layersGos.parallelStream().map(gosDef -> {
				try {
					if (DataSource.isGeoTIFF(layer)) {
						this.rasterManagement.deleteCoverageOfLayer(gosDef.getGosEndpoint(), layerId);
					} else if (DataSource.isPostGIS(layer)) {
						this.shapeManagement.deleteShapesOfLayer(gosDef.getGosEndpoint(), layerId);
						this.deleteDataBaseView(gosDef.getGosEndpoint(), layerId);
					}

					this.geoserverManagement.deleteGeoserverLayer(gosDef.getGosEndpoint(), layerId, layer.getDataSource());
					this.dataCreatorGeoanalytics.deleteLayer(layerId, gosDef.getGosIdentifier());
					
					return new HashSet<Boolean>(Arrays.asList(true));
				} catch (Exception ex) {
					logger.error("Could not delete database view and/or geoserver layer of layerID: " + layerId + " on gos: " + gosDef.getGosEndpoint(), ex);
					return new HashSet<Boolean>(Arrays.asList(false));
				}
			}).reduce((s1, s2) -> {
				HashSet<Boolean> s = new HashSet<Boolean>();
				s.addAll(s2);
				s.addAll(s2);
				return s;
			}).get();
			
			if (results.contains(false)){
				throw new Exception("Could not delete layer " + layerId + " from all GOS endpoints");
			}			
		} catch (Exception e) {
			throw e;
		}

		if (layer.getIsTemplate() > 0) {
			try{
				 this.geocodeManager.deleteGeocodesOfTemplateLayer(layer);
			} catch (Exception e){
				throw new Exception("Could not remove geocodes of template layer " + layerId + " from database", e);
			}
		}
		
		try{
			this.configurationManager.removeLayerConfig(layer);	
		} catch (Exception e){
			throw new Exception("Could not remove layer configuration of " + layerId + " from database", e);
		}
		
		try{
			this.deleteLayerTenant(layer);
		} catch (Exception e){
			throw new Exception("Could not remove tenants of layer " + layerId + " from database", e);
		}
		
		try{
			this.deleteLayerTags(layer);		
		} catch (Exception e){
			throw new Exception("Could not remove tags of layer " + layerId + " from database",e );
		}
		
		try{
			this.deleteLayer(layer);		
		} catch (Exception e){
			throw new Exception("Could not remove layer entry " + layerId + " from database", e);
		}
		
		try {
			Long layerGeonetworkId = layer.getGeonetwork();

			if (layerGeonetworkId != null) {
				new GSManagerGeoNetworkBridge().deleteFromGeonetwork(tenantName, layerGeonetworkId);
			}
		} catch (Exception e) {
			logger.error("Could not remove layer entry " + layerId + " from GeoNetwork", e);
		}	
		
		logger.info("Layer with id: " + layer.getId() + " and name: " + layer.getName() + " has been deleted successfully!");
	}
}
