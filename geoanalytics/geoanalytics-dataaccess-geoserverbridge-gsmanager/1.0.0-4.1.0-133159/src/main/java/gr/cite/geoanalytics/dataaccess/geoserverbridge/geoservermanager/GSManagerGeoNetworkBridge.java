package gr.cite.geoanalytics.dataaccess.geoserverbridge.geoservermanager;

import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoNetworkBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoNetworkBridgeException;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.elements.MetaData;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.metadata.elements.MetaDataForm;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.DefaultConfiguration;
import org.gcube.spatial.data.geonetwork.iso.MissingInformationException;
import org.gcube.spatial.data.geonetwork.model.faults.AuthorizationException;
import org.gcube.spatial.data.geonetwork.model.faults.EncryptionException;
import org.gcube.spatial.data.geonetwork.model.faults.GeoNetworkException;
import org.gcube.spatial.data.geonetwork.model.faults.InvalidInsertConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.opengis.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.access.InvocationFailureException;



public class GSManagerGeoNetworkBridge implements GeoNetworkBridge{
	private static Logger logger = LoggerFactory.getLogger(GSManagerGeoNetworkBridge.class);

	private static DefaultConfiguration defaultConf = null;
	private static GeoNetworkPublisher publisher = null;

	@Override	
	public void publishGeonetwork(String tenantName,MetaDataForm meta) throws GeoNetworkBridgeException
	{
		ScopeProvider.instance.set(tenantName);
		
		try {
			defaultConf = new DefaultConfiguration();
			publisher = GeoNetwork.get(defaultConf);
		} catch (MissingConfigurationException 
				| EncryptionException 
				| MissingServiceEndpointException 
				| GNLibException 
				| GNServerException 
				| AuthorizationException e) {
			
			logger.error("Error while initializing configuration and publisher", e);
			throw new GeoNetworkBridgeException(
					"Error while initializing configuration and publisher", e);
		}
		
		//Cannot publish without logging in
		try {
			publisher.login(LoginLevel.DEFAULT);
		} catch (MissingConfigurationException | AuthorizationException
				| MissingServiceEndpointException e) {
			logger.error("Error while trying to log in", e);
			throw new GeoNetworkBridgeException(
					"Error while trying to log in", e);
		}
		
		//Get publish configuration
		GNInsertConfiguration config;
		try {
			config = publisher.getCurrentUserConfiguration("datasets", "_none_");
		} catch (GeoNetworkException e) {
			logger.error("Error while getting current configuration", e);
			throw new GeoNetworkBridgeException(
					"Error while getting current configuration", e);
		}
		
		//Publish Metadata object
		Metadata toPublish = null;
		try {
			MetaData m = translate(meta,tenantName);
			toPublish = m.getMetadata();
		} catch (InvocationFailureException | URISyntaxException | MissingInformationException e) {
			logger.error("Error while transforming from GcubeISOMetadata to Metadata", e);
			throw new GeoNetworkBridgeException(
					"Error while transforming from GcubeISOMetadata to Metadata", e);
		}

		long publishedObjectId;
		try {
			
			publishedObjectId = publisher.insertMetadata(config, toPublish);
			logger.debug("Published in geonetwork with id : "+publishedObjectId);
		} catch (MissingConfigurationException | GNLibException
				| GNServerException | MissingServiceEndpointException
				| InvalidInsertConfigurationException | AuthorizationException
				| IOException | JAXBException e) {
			logger.error("Error while inserting to Geonetwork", e);
			throw new GeoNetworkBridgeException(
					"Error while inserting to Geonetwork", e);
		}
	}
	
	private MetaData translate(MetaDataForm metadata, String scope) throws GeoNetworkBridgeException{
		
		MetaData meta;
		try {
			meta = new MetaData(scope);
		} catch (Exception e) {
			logger.error("Error while initializing Metadata", e);
			throw new GeoNetworkBridgeException(
					"Error while initializing Metadata", e);
		}
		
		meta.setMetadataAbstract(metadata.getAbstractField());
		meta.setMetadataPurpose(metadata.getPurpose());
		meta.setCredits(metadata.getCredits());
		meta.setDate(metadata.getDate());
		meta.setDistributorIndividualName(metadata.getDistributor().getIndividualName());
		meta.setDistributorOrganisationName(metadata.getDistributor().getOrganisationName());
		meta.setDistributorSite(metadata.getDistributor().getSite());
		meta.setExtent(metadata.getExtent());
		meta.setGeometricObjectType(metadata.getGeometricObjectType());
		meta.setGraphicOverview(metadata.getGraphicOverview());
		meta.setKeywords(metadata.getKeywords());
		meta.setLanguage(metadata.getLanguage());
		meta.setPresentationForm(metadata.getPresentationForm());
		meta.setProviderIndividualName(metadata.getProvider().getIndividualName());
		meta.setProviderOrganisationName(metadata.getProvider().getOrganisationName());
		meta.setProviderSite(metadata.getProvider().getSite());
		meta.setResolution(metadata.getResolution());
		meta.setTitle(metadata.getTitle());
		meta.setTopicCategory(metadata.getTopicCategory());
		meta.setTopologyLevel(metadata.getTopologyLevel());
		meta.setUser(metadata.getUser());
		meta.setUserLimitation(metadata.getUserLimitation());
		meta.setProjectName(metadata.getProjectName());
		meta.setGeometryCount(metadata.getGeometrycount());
		meta.setLanguage(metadata.getLanguage());
		
		return meta;
	}
}
