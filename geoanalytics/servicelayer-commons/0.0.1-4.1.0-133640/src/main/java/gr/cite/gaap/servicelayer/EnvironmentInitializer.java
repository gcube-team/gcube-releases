package gr.cite.gaap.servicelayer;

import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.context.GeoServerBridgeConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig.SysConfigClass;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.SystemLayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.LayerStyle;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.Theme;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.DataStore;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Layer;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.util.ScaledStyleCreator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentInitializer {

	public static Logger log = LoggerFactory
			.getLogger(EnvironmentInitializer.class);

	private GeoServerBridge geoServerBridge = null;
	private GeoServerBridgeConfig configuration = null;
	private ConfigurationManager configurationManager = null;

	@Inject
	public void setGeoServerBridge(GeoServerBridge geoServerBridge) {
		this.geoServerBridge = geoServerBridge;
	}

	@Inject
	public void setConfiguration(GeoServerBridgeConfig configuration) {
		this.configuration = configuration;
	}

	@Inject
	public void setConfigurationManager(
			ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}

	private void assertWorkspace() throws GeoServerBridgeException {

		String workSpaceName = configuration.getGeoServerBridgeWorkspace();
		Boolean workSpaceExists = geoServerBridge
				.workspaceExists(workSpaceName);

		if (!workSpaceExists) {
			geoServerBridge.addWorkspace(workSpaceName);
			log.debug("Workspace with name " + workSpaceName + " just created");
		} else {
			log.debug("Workspace already exists");
		}

	}

	private void assertDataStore() throws GeoServerBridgeException {

		DataStore dataStore = new DataStore();

		String workspaceName = configuration.getGeoServerBridgeWorkspace();
		String dataStoreName = configuration.getDataStoreConfig()
				.getDataStoreName();
		Boolean dataStoreExists = geoServerBridge.dataStoreExists(
				workspaceName, dataStoreName);

		if (!dataStoreExists) {
			dataStore.setHost(configuration.getDataStoreConfig().getHost());
			dataStore.setPort(configuration.getDataStoreConfig().getPort());
			dataStore.setDatabase(configuration.getDataStoreConfig()
					.getDatabaseName());
			dataStore.setUser(configuration.getDataStoreConfig().getUser());
			dataStore.setPassword(configuration.getDataStoreConfig()
					.getPassword());
			dataStore.setWorkspace(configuration.getGeoServerBridgeWorkspace());
			dataStore.setDataStoreName(configuration.getDataStoreConfig()
					.getDataStoreName());
			dataStore.setDescription(configuration.getDataStoreConfig()
					.getDescription());

			geoServerBridge.addDataStore(dataStore);
		}
	}

	private void assertLayers(List<LayerConfig> layerConfigs,
			SystemPresentationConfig systemPresentationConfig,
			Map<String, String> slds) throws Exception {

		log.info("Synchronizing layer configuration with remote geospatial server");
		for (LayerConfig lcfg : layerConfigs) {
			log.info("Checking layer: " + lcfg.getName());
			Layer l = geoServerBridge.getLayer(lcfg.getName());
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
				featureType.setName(lcfg.getName());
				featureType.setTitle(lcfg.getName());
				featureType.setSrs("EPSG:4326");
				featureType.setNativeCRS("EPSG:4326");
				featureType.setNativeBoundingBox(b);
				featureType.setLatLonBoundingBox(b);

				l = new Layer();
				l.setWorkspace(configuration.getGeoServerBridgeWorkspace());
				l.setDatastore(configuration.getDataStoreConfig()
						.getDataStoreName());
				l.setEnabled(true);
				String defStyle = systemPresentationConfig.getTermStyle(lcfg
						.getTermId());
				if (defStyle == null)
					defStyle = SystemPresentationConfig.DEFAULT_STYLE;
				l.setDefaultStyle(defStyle);
				for (Theme th : systemPresentationConfig.getThemes()) {
					String st = systemPresentationConfig.getTermStyle(
							th.getTitle(), lcfg.getTermId());
					if (st != null)
						l.addStyle(st);
				}
				l.setName(lcfg.getName());
				l.setType("VECTOR");

				geoServerBridge.addLayer(l, featureType, slds,
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
		Map<String, String> styles = new HashMap<String, String>();
		for (LayerConfig lcfg : layerConfigs) {
			for (Theme th : systemPresentationConfig.getThemes()) {
				String st = systemPresentationConfig.getTermStyle(
						th.getTitle(), lcfg.getTermId());
				if (st != null) {
					String scaled = ScaledStyleCreator.getScaledName(st,
							lcfg.getMinScale(), lcfg.getMaxScale());
					if (styles.get(scaled) == null) {
						styles.put(scaled, st);
						log.info("Checking style: " + scaled);
						if (geoServerBridge.getStyle(scaled) == null) {
							log.info("Style "
									+ scaled
									+ " was not found in remote geospatial server. Publishing...");
							geoServerBridge.addStyle(st,
									slds.get(styles.get(scaled)),
									lcfg.getMinScale(), lcfg.getMaxScale());
							log.info("Style "
									+ scaled
									+ " successfully published to remote geospatial server.");
						} else
							log.info("Done checking style: "
									+ scaled
									+ ". Style was found in remote geospatial server");
					}
				}
			}
		}
		log.info("Done synchronizing style configuration with remote geospatial server");
	}

	public void asyncInitializeEnvironment() throws Exception {

		ExecutorService es = Executors.newFixedThreadPool(1);
		es.execute(new Runnable() {

			@Override
			public void run() {
				try {
					initializeEnvironment();
				} catch (Exception e) {
					log.error("Error while initializing environment", e);
				}
			}
		});
	}

	public void initializeEnvironment() throws Exception {

		List<LayerConfig> layerConfigs = configurationManager.getLayerConfig();
		SystemPresentationConfig systemPresentationConfig = configurationManager
				.getSystemPresentationConfig();

		assertWorkspace();
		assertDataStore();

		Map<String, String> slds = new HashMap<String, String>();
		List<LayerStyle> sts = systemPresentationConfig.getLayerStyles();
		for (LayerStyle st : sts)
			slds.put(st.getName(), st.getStyle());

		assertLayers(layerConfigs, systemPresentationConfig, slds);

		assertStyles(layerConfigs, systemPresentationConfig, slds);

	}

}