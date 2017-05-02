package gr.cite.gaap.servicelayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import gr.cite.clustermanager.layers.DataMonitor;
import gr.cite.gaap.datatransferobjects.StyleMessenger;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.geoanalytics.dataaccess.entities.style.Style;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation.SystemPresentationConfig;
import gr.cite.gos.client.GeoserverManagement;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.Monitor;


@Service
public class EnvironmentInitializer {

	public static Logger log = LoggerFactory.getLogger(EnvironmentInitializer.class);

	private GeoserverManagement geoserverManagement = null;
	private DataMonitor dataMonitor = null;
	private ConfigurationManager configurationManager = null;
	
	@Autowired StyleManagerCommons styleManager;
	

	private static int PREVIOUS_NUM_OF_GOS = -1;
	
	@Inject
	public void setGeoserverManagement(GeoserverManagement geoserverManagement){
		this.geoserverManagement = geoserverManagement;
	}
	
	@Inject
	public void setDataMonitor(DataMonitor dataMonitor){
		this.dataMonitor = dataMonitor;
	}
	
	@Inject
	public void setConfigurationManager(
			ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}

	
	
	
	public void asyncInitializeEnvironment() throws Exception {

		//TODO: FIND A SMARTER WAY TO ISSUE THE INITIATION. IT MIGHT BE ISSUED BEFORE COMPLETE LOADING OF ALL GOS'es
		
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		ses.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				int current = dataMonitor.getAllGosEndpoints().size();
				try {
					if((current != 0) && (PREVIOUS_NUM_OF_GOS != dataMonitor.getAllGosEndpoints().size()))
						initializeEnvironment();
					PREVIOUS_NUM_OF_GOS = current;
				} catch (Exception e) {
					log.error("Error while initializing environment", e);
				}
			}
		}, 0, 5, TimeUnit.SECONDS);
			
	}
	
	
	public void initializeEnvironment() throws Exception {

		List<LayerConfig> layerConfigs = configurationManager.getLayerConfig();
		SystemPresentationConfig systemPresentationConfig = configurationManager.getSystemPresentationConfig();
		List<Style> styles = styleManager.listAllStyles();
		List<StyleMessenger> stylesMessenger = new ArrayList<StyleMessenger>();
		
		for(Style st : styles) {
			StyleMessenger styleMessenger = new StyleMessenger();
			styleMessenger.setName(st.getName());
			styleMessenger.setDescription(st.getContent());
			stylesMessenger.add(styleMessenger);
		}
		
		dataMonitor.getAllGosEndpoints().parallelStream().forEach(gosDefinition -> {
			try {
				log.debug("Initializing the geoserver environment on geoserver of GOS: "+gosDefinition.getGosEndpoint());
				geoserverManagement.initializeEnvironment(gosDefinition.getGosEndpoint(), layerConfigs, systemPresentationConfig, stylesMessenger);
			} catch (IOException e) {
				log.error("Could not initialize geoserver environment of gos... reason -> "+e);
			}
		});
		
	}

}