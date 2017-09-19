package org.gcube.common.authorization.library.enpoints;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gcube.common.scan.ClasspathScanner;
import org.gcube.common.scan.ClasspathScannerFactory;
import org.gcube.common.scan.matchers.NameMatcher;
import org.gcube.common.scan.resources.ClasspathResource;
import org.gcube.common.scope.api.ServiceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationEndpointScanner {

	private static Logger log = LoggerFactory.getLogger(AuthorizationEndpointScanner.class);

	private static EndpointsContainer endpoints;

	/**
	 * The path used to find service map configuration files.
	 */
	static final String configurationPattern = ".*\\.authorization";

	/**
	 * Scans the classpath for {@link ServiceMap}s.
	 */
	public static synchronized EndpointsContainer endpoints() {

		if (endpoints==null || endpoints.getEndpoints().size()==0){
			log.trace("starting  authorization endpoint retrieving");
			Map<Integer, AuthorizationEndpoint> endpointsMap = new HashMap<Integer, AuthorizationEndpoint>();
			
			try {

				JAXBContext context = JAXBContext.newInstance(AuthorizationEndpoint.class);
				Unmarshaller um = context.createUnmarshaller();
				
				String defaultInfrastructure = null;
				int defaultInfraPriority= Integer.MAX_VALUE;
				
				for (String r :getEnpointResourceNames()){
					
					URL url = Thread.currentThread().getContextClassLoader().getResource(r);
					
					
					AuthorizationEndpoint endpoint = (AuthorizationEndpoint)um.unmarshal(url);
					if (defaultInfrastructure==null)
						defaultInfrastructure = endpoint.getInfrastructure();
					
					
					if (!endpointsMap.containsKey(endpoint.getInfrastructure()) 
							|| endpointsMap.get(endpoint.getInfrastructure()).getPriority()> endpoint.getPriority()){
						if (r.startsWith("default") && endpoint.getPriority()<defaultInfraPriority ){
							defaultInfrastructure = endpoint.getInfrastructure();
							defaultInfraPriority = endpoint.getPriority();
						}
						endpointsMap.put(endpoint.getInfrastructure().hashCode(), endpoint);
					}
										
					log.info("loaded endpoint {} ",endpoint.toString());
				}
				
				if (endpointsMap.size()==0)
					throw new Exception("no endpoints retreived");
				endpoints = new EndpointsContainer(endpointsMap, defaultInfrastructure);
				log.trace("authorization endpoint retrieving finished");
			} catch (Exception e) {
				throw new RuntimeException("could not load authorization endpoints", e);
			}
			
		}
		return endpoints;
	}
	
	private static Set<String> getEnpointResourceNames() {
		
		ClasspathScanner scanner = ClasspathScannerFactory.scanner();
		Set<String> names = new HashSet<String>();
		for (ClasspathResource r : scanner.scan(new NameMatcher(configurationPattern)))
			names.add(r.name());
		return names;
	}
		
}
