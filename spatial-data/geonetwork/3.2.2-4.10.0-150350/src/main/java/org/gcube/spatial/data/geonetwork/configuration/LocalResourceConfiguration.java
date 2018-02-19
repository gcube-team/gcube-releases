package org.gcube.spatial.data.geonetwork.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.model.faults.AuthorizationException;
import org.gcube.spatial.data.geonetwork.model.faults.EncryptionException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.gcube.spatial.data.geonetwork.utils.EncryptionUtils;
import org.gcube.spatial.data.geonetwork.utils.RuntimeParameters;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import lombok.Data;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalResourceConfiguration extends DefaultConfiguration {

	public static String DEFAULT_FILENAME="GeonetworkSE.xml";
	
	
	@Data
	public static class ResourceConfiguration{
		private String name;
		private String endpoint;
		private String adminPassword;
		
		private short version=0;
		private short minor=0;
		private short revision=0;
		private short build=0;
		
		private String host;
		
	}
	
	private String fileName;
	
	public LocalResourceConfiguration(ResourceConfiguration config) throws MissingConfigurationException, EncryptionException{
		this(DEFAULT_FILENAME,config);
	}
	
	public LocalResourceConfiguration(String fileName,ResourceConfiguration config) throws EncryptionException, MissingConfigurationException {
		this.fileName=fileName;
		log.debug("Checking file presence.. ");
		File f=new File(fileName);
		if(!f.exists()){
			ServiceEndpoint toGenerate=new ServiceEndpoint();
			Profile profile=toGenerate.newProfile();
			profile.category(props.getProperty(RuntimeParameters.geonetworkCategory));
			profile.name(config.getName());
			profile.newPlatform().name(props.getProperty(RuntimeParameters.geonetworkPlatformName)).version(config.getVersion()).buildVersion(config.getBuild())
			.minorVersion(config.getMinor()).revisionVersion(config.getRevision());
			profile.newRuntime().hostedOn(config.getHost()).status("READY");
			AccessPoint ap=profile.accessPoints().add();
			ap.credentials(EncryptionUtils.encrypt(config.getAdminPassword()), "admin");
			ap.name(props.getProperty(RuntimeParameters.geonetworkEndpointName));
			ap.address(config.getEndpoint());
			ap.properties().add(new Property().nameAndValue(props.getProperty(RuntimeParameters.priorityProperty), "1"));
			log.debug("Storing generated resource..");
			update(toGenerate);
		}
	}

	
	@Override
	@Synchronized
	protected List<ServiceEndpoint> doTheQuery(String geonetworkCategory, String geonetworkPlatformName) {
		try {
			return Collections.singletonList(Resources.unmarshal(ServiceEndpoint.class, new FileInputStream(new File(fileName))));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Unable to read from file system",e);
		}
	}
	
	
	@Override
	protected ServiceEndpoint update(ServiceEndpoint toStore) {
		try{
			File f=new File(fileName);
			Files.deleteIfExists(f.toPath());
			f.createNewFile();
			Resources.marshal(toStore, new FileOutputStream(f));
		}catch(IOException e){
			throw new RuntimeException ("Unable to save ServiceEndpoint",e);
		}
		return toStore;
	}
	
	
	
}