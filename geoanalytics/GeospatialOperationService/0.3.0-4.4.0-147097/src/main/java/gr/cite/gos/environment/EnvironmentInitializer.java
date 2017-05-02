package gr.cite.gos.environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.cite.gaap.datatransferobjects.StyleMessenger;
import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.dataaccess.entities.style.Style;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.LayerStyle;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.Theme;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.DataStore;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.GeoserverLayer;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.util.ScaledStyleCreator;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentInitializer {

	public static Logger log = LoggerFactory.getLogger(EnvironmentInitializer.class);

	private GeoServerBridge geoServerBridge = null;
	private GeoServerBridgeConfig configuration = null;
	
	
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

	private void assertDataStore() throws GeoServerBridgeException {

		DataStore dataStore = new DataStore();

		String workspaceName = configuration.getGeoServerBridgeWorkspace();
		String datastoreName = configuration.getDataStoreConfig().getDataStoreName();
		Boolean dataStoreExists = geoServerBridge.dataStoreExists(workspaceName, datastoreName);

		if (!dataStoreExists) {
			dataStore.setHost(configuration.getDataStoreConfig().getHost());
			dataStore.setPort(configuration.getDataStoreConfig().getPort());
			dataStore.setDatabase(configuration.getDataStoreConfig().getDatabaseName());
			dataStore.setUser(configuration.getDataStoreConfig().getUser());
			dataStore.setPassword(configuration.getDataStoreConfig().getPassword());
			dataStore.setWorkspace(configuration.getGeoServerBridgeWorkspace());
			dataStore.setDataStoreName(configuration.getDataStoreConfig().getDataStoreName());
			dataStore.setDescription(configuration.getDataStoreConfig().getDescription());
			geoServerBridge.addDataStore(dataStore);
		}
	}

	private void assertLayers(List<LayerConfig> layerConfigs,
			SystemPresentationConfig systemPresentationConfig,
			Map<String, String> slds) throws Exception {

		log.info("Synchronizing layer configuration with remote geospatial server");
		for (LayerConfig lcfg : layerConfigs) {
			log.info("Checking layer: " + lcfg.getName());
			GeoserverLayer l = geoServerBridge.getGeoserverLayer(lcfg.getTermId());
			if (l == null) {
				log.info("Layer "
						+ lcfg.getName()
						+ " not found in remote geospatial server. Publishing...");
				Bounds b = new Bounds(lcfg.getBoundingBox().getMinY(), lcfg
						.getBoundingBox().getMinY(), lcfg.getBoundingBox()
						.getMaxX(), lcfg.getBoundingBox().getMaxY(),
						"EPSG:4326");

				FeatureType featureType = new FeatureType();
				featureType.setDatastore(configuration.getDataStoreConfig()
						.getDataStoreName());
				featureType.setWorkspace(configuration
						.getGeoServerBridgeWorkspace());
				featureType.setEnabled(true);
				featureType.setName(lcfg.getTermId());
				featureType.setTitle(lcfg.getName());
				featureType.setSrs("EPSG:4326");
				featureType.setNativeCRS("EPSG:4326");
				featureType.setNativeBoundingBox(b);
				featureType.setLatLonBoundingBox(b);

				l = new GeoserverLayer();
				l.setWorkspace(configuration.getGeoServerBridgeWorkspace());
				l.setDatastore(configuration.getDataStoreConfig()
						.getDataStoreName());
				l.setEnabled(true);
//				String defStyle = systemPresentationConfig.getTermStyle(lcfg.getTermId());
				String defStyle = lcfg.getStyle();
				if (defStyle == null)
					defStyle = SystemPresentationConfig.DEFAULT_STYLE;
				l.setDefaultStyle(defStyle);
				for (Theme th : systemPresentationConfig.getThemes()) {
					String st = systemPresentationConfig.getTermStyle(
							th.getTitle(), lcfg.getTermId());
					if (st != null)
						l.addStyle(st);
				}
				l.setId(lcfg.getName());
				l.setType("VECTOR");

				geoServerBridge.addGeoserverLayer(l, featureType, slds,
						lcfg.getMinScale(), lcfg.getMaxScale());
				log.info("Layer " + lcfg.getName()
						+ " successfully published in remote geospatial server");
			} else
				log.info("Done checking layer: " + lcfg.getName()
						+ " .Layer was found in remote geospatial server");
		}
		log.info("Done synchronizing layer configuration with remote geospatial server");
	}

	private void assertStyles(List<LayerConfig> layerConfigs,
			SystemPresentationConfig systemPresentationConfig,
			Map<String, String> slds) throws Exception {
		log.info("Synchronizing style configuration with remote geospatial server");
		
		for (Map.Entry<String, String> entry : slds.entrySet())
		{
			if (geoServerBridge.getStyle(entry.getKey()) == null) {
				log.info("Style "
						+ entry.getKey()
						+ " was not found in remote geospatial server. Publishing...");
				geoServerBridge.addStyle(entry.getKey(), entry.getValue());
				log.info("Style "
						+ entry.getKey()
						+ " successfully published to remote geospatial server.");
			} else {
				log.info("Done checking style: "
						+ entry.getKey()
						+ ". Style was found in remote geospatial server");
			}		}
		
		log.info("Done synchronizing style configuration with remote geospatial server");
	}


	public void initializeGeoserverEnvironment(List<LayerConfig> layerConfigs, SystemPresentationConfig systemPresentationConfig, List<StyleMessenger> styles) throws Exception {

		assertWorkspace();
		assertDataStore();

		Map<String, String> slds = new HashMap<String, String>();

		for (StyleMessenger st : styles)
			slds.put(st.getName(), st.getDescription());

		assertStyles(layerConfigs, systemPresentationConfig, slds);
		assertLayers(layerConfigs, systemPresentationConfig, slds);
	}

}