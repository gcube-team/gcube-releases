package org.gcube.data.access.storagehub.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletContext;

import org.gcube.common.storagehub.model.items.Item;
import org.gcube.data.access.storagehub.Constants;
import org.gcube.data.access.storagehub.services.RepositoryInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class VREManager {

	private static final Logger logger = LoggerFactory.getLogger(VREManager.class);
	
	private Map<String, VRE> vreMap = new HashMap<>();
	
	@Inject 
	RepositoryInitializer repository;
	
	ExecutorService executor = Executors.newFixedThreadPool(5);
	
	SimpleCredentials credentials;
	
	@Inject
	public VREManager(ServletContext context) {
		credentials = new SimpleCredentials(context.getInitParameter(Constants.ADMIN_PARAM_NAME),context.getInitParameter(Constants.ADMIN_PARAM_PWD).toCharArray());
	}
	
	
	public synchronized VRE getVRE(String completeName) {
		logger.trace("requesting VRE {}",completeName);
		if (vreMap.containsKey(completeName))
			return vreMap.get(completeName);
		else 
			return null;
		
	}
	
	public synchronized VRE putVRE(Item vreFolder) {
		logger.trace("inserting VRE {}",vreFolder.getTitle());
		if (vreMap.containsKey(vreFolder.getTitle())) throw new RuntimeException("something went wrong (vre already present in the map)");
		else {
			VRE toReturn = new VRE(vreFolder, repository.getRepository(), credentials, executor);
			vreMap.put(vreFolder.getTitle(), toReturn);
			return toReturn;
		}
		
	}
	
}
