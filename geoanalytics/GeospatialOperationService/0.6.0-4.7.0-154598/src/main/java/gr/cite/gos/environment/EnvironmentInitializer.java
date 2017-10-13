package gr.cite.gos.environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gr.cite.gaap.datatransferobjects.StyleMessenger;
import gr.cite.geoanalytics.context.DataStoreConfig;
import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;
import gr.cite.geoanalytics.dataaccess.entities.coverage.dao.CoverageDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.DataSource;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.DataStore;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.GeoserverLayer;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.PublishConfig;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentInitializer {

	public static Logger log = LoggerFactory.getLogger(EnvironmentInitializer.class);

	private GeoServerBridge geoServerBridge = null;
	private GeoServerBridgeConfig configuration = null;
	
	private CoverageDao coverageDao = null;
	
	@Inject
	public void setGeoServerBridge(CoverageDao coverageDao) {
		this.coverageDao = coverageDao;
	}
	
	@Inject
	public void setGeoServerBridge(GeoServerBridge geoServerBridge) {
		this.geoServerBridge = geoServerBridge;
	}

	@Inject
	public void setConfiguration(GeoServerBridgeConfig configuration) {
		this.configuration = configuration;
	}

	private void assertWorkspace() throws GeoServerBridgeException {
		String workspaceName = configuration.getGeoServerBridgeWorkspace();
		Boolean workspaceExists = geoServerBridge.workspaceExists(workspaceName);

		if (!workspaceExists) {
			geoServerBridge.addWorkspace(workspaceName);
			log.debug("Workspace with name " + workspaceName + " just created");
		} else {
			log.debug("Workspace already exists");
		}
	}

	private void assertDataStores() throws GeoServerBridgeException {
		String workspaceName = configuration.getGeoServerBridgeWorkspace();
		String datastoreName = configuration.getPostgisDataStoreConfig().getDataStoreName();

		Boolean dataStoreExists = geoServerBridge.dataStoreExists(workspaceName, datastoreName);

		if (!dataStoreExists) {
			DataStoreConfig postgisDataStoreConfig = configuration.getPostgisDataStoreConfig();
			DataStore dataStore = new DataStore();
			dataStore.setWorkspace(configuration.getGeoServerBridgeWorkspace());
			dataStore.setDataStoreName(postgisDataStoreConfig.getDataStoreName());
			dataStore.setDatabase(postgisDataStoreConfig.getDatabaseName());
			dataStore.setHost(postgisDataStoreConfig.getHost());
			dataStore.setPort(postgisDataStoreConfig.getPort());
			dataStore.setUser(postgisDataStoreConfig.getUser());
			dataStore.setPassword(postgisDataStoreConfig.getPassword());
			dataStore.setDescription(postgisDataStoreConfig.getDescription());
			geoServerBridge.addDataStore(dataStore);
		}
	}

	private void assertLayers(List<LayerConfig> layerConfigs, Map<String, String> slds) throws Exception {
		log.info("Synchronizing layer configuration with remote geospatial server");
		
		String workspaceName = configuration.getGeoServerBridgeWorkspace();
		DataStoreConfig postgisDataStoreConfig = configuration.getPostgisDataStoreConfig();
		
		for (LayerConfig layerConfig : layerConfigs) {
			log.info("Checking layer: " + layerConfig.getName());

			boolean layerNotExists = !geoServerBridge.layerExists(workspaceName, layerConfig.getLayerId());

			if (layerNotExists) {
				try {
					log.info("Layer " + layerConfig.getName() + " not found in remote geospatial server. Publishing...");

					if (DataSource.isGeoTIFF(layerConfig.getDataSource())) {
						assertGeoTIFFLayer(layerConfig, slds);
					} else if (DataSource.isPostGIS(layerConfig.getDataSource())) {
						assertPostGISLayer(layerConfig, slds, postgisDataStoreConfig);
					}

					log.info("Layer " + layerConfig.getName() + " successfully published in remote geospatial server");
				} catch (Exception e) {
					log.error("Could initialize layer " + layerConfig.getName() + ":" + layerConfig.getLayerId() + " in GeoServer", e);
				}
			}
		}

		log.info("Done synchronizing layer configuration with remote geospatial server");
	}

	private void assertPostGISLayer(LayerConfig layerConfig, Map<String, String> slds, DataStoreConfig dataStoreConfig) throws Exception {
		Bounds bounds = new Bounds();
		bounds.setMinx(layerConfig.getBoundingBox().getMinX());
		bounds.setMaxx(layerConfig.getBoundingBox().getMaxX());
		bounds.setMiny(layerConfig.getBoundingBox().getMinY());
		bounds.setMaxy(layerConfig.getBoundingBox().getMaxY());
		bounds.setCrs("EPSG:4326");

		FeatureType featureType = new FeatureType();
		featureType.setDatastore(dataStoreConfig.getDataStoreName());
		featureType.setWorkspace(configuration.getGeoServerBridgeWorkspace());
		featureType.setEnabled(true);
		featureType.setName(layerConfig.getLayerId());
		featureType.setTitle(layerConfig.getName());
		featureType.setSrs("EPSG:4326");
		featureType.setNativeCRS("EPSG:4326");
		featureType.setNativeBoundingBox(bounds);
		featureType.setLatLonBoundingBox(bounds);

		GeoserverLayer layer = new GeoserverLayer();
		layer.setWorkspace(configuration.getGeoServerBridgeWorkspace());
		layer.setDatastore(dataStoreConfig.getDataStoreName());
		layer.setEnabled(true);
		layer.setId(layerConfig.getName());
		layer.setType("vector");

		String defStyle = layerConfig.getStyle();
		if (defStyle == null) {
			defStyle = SystemPresentationConfig.DEFAULT_STYLE;
		}

		layer.setDefaultStyle(defStyle);
		layer.addStyle(defStyle);

		geoServerBridge.addGeoserverLayer(layer, featureType, slds, layerConfig.getMinScale(), layerConfig.getMaxScale());
	}

	private void assertGeoTIFFLayer(LayerConfig layerConfig, Map<String, String> slds) throws Exception {
		Coverage geotiff = coverageDao.findCoverageByLayer(UUID.fromString(layerConfig.getLayerId()));
		PublishConfig publishConfig = new PublishConfig(layerConfig.getLayerId().toString(), layerConfig.getName(), null, layerConfig.getBoundingBox(), layerConfig.getStyle());
		geoServerBridge.publishGeoTIFF(publishConfig, geotiff);
	}

	private void assertStyles(List<LayerConfig> layerConfigs, Map<String, String> slds) throws Exception {
		log.info("Synchronizing style configuration with remote geospatial server");

		for (Map.Entry<String, String> entry : slds.entrySet()) {
			if (geoServerBridge.getStyle(entry.getKey()) == null) {
				log.info("Style " + entry.getKey() + " was not found in remote geospatial server. Publishing...");
				geoServerBridge.addStyle(entry.getKey(), entry.getValue());
				log.info("Style " + entry.getKey() + " successfully published to remote geospatial server.");
			} else {
				log.info("Done checking style: " + entry.getKey() + ". Style was found in remote geospatial server");
			}
		}

		log.info("Done synchronizing style configuration with remote geospatial server");
	}

	public void initializeGeoserverEnvironment(List<LayerConfig> layerConfigs, SystemPresentationConfig systemPresentationConfig, List<StyleMessenger> styles) throws Exception {
		assertWorkspace();
		assertDataStores();

		Map<String, String> slds = new HashMap<String, String>();

		for (StyleMessenger st : styles)
			slds.put(st.getName(), st.getDescription());

		assertStyles(layerConfigs, slds);
		assertLayers(layerConfigs, slds);
	}
}