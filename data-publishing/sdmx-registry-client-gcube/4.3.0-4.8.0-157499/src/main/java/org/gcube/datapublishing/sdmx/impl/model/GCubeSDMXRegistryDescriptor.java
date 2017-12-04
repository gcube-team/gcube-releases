package org.gcube.datapublishing.sdmx.impl.model;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryDescriptor;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryInterfaceType;
import org.gcube.datapublishing.sdmx.security.model.Credentials;
import org.gcube.datapublishing.sdmx.security.model.impl.Base64Credentials;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCubeSDMXRegistryDescriptor implements SDMXRegistryDescriptor {

	private Base64Credentials credentials;
	private boolean versionAware;
	
	private enum RegistryInterfaceCode 
	{
		RESTV1, RESTV2, RESTV2_1, SOAPV1, SOAPV2, SOAPV2_1
	}

	private Logger log = LoggerFactory.getLogger(GCubeSDMXRegistryDescriptor.class);

	private Map<String,SDMXRegistryDescriptor> delegates = new HashMap<String, SDMXRegistryDescriptor>();
	
	public GCubeSDMXRegistryDescriptor() {
		this.credentials = null;
		this.versionAware = false;
	}
	
	public void setVersionAware (boolean versionAware)
	{
		this.versionAware = versionAware;
	}
	
	@Override
	public String getUrl(SDMXRegistryInterfaceType interfaceType) {
		String scope = ScopeProvider.instance.get();
		if (scope == null || scope.isEmpty()) {
			log.error("No scope provided, unable to retrieve SDMX registry");
			return null;
		}
		if (!dataRetrieved(scope))
			delegates.put(scope, retrieveDescriptor());
		return delegates.get(scope).getUrl(interfaceType);
	}

	private boolean dataRetrieved(String scope){
		return delegates.containsKey(ScopeProvider.instance.get()); 
	}

	private SDMXRegistryDescriptor retrieveDescriptor() {
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		String scope = ScopeProvider.instance.get();
		
//		if (scope == null || scope.isEmpty()) {
//			logger.error("No scope provided, unable to retrieve SDMX registry");
//			return null;
//		}
		
		log.debug("Looking for SDMXRegistry resources on scope: " + scope);
		
		query.addCondition("$resource/Profile/Category/text() eq 'SDMX'");
		query.addCondition("$resource/Profile/Name/text() eq 'SDMXRegistry'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);

		if (resources.size() > 1 || resources.isEmpty()) {
			log.error("Invalid number of SDMX registry resources found: " + resources.size());
			return null;
		}
		
		SDMXRegistryDescriptorImpl descriptor = new SDMXRegistryDescriptorImpl();
		
		for (AccessPoint ap : resources.get(0).profile().accessPoints()) {
			log.trace("Retrieved " + ap.name() + ": "
					+ ap.address());
			if (ap.name().equals(
					RegistryInterfaceCode.RESTV1.toString()))
				descriptor.setUrl(SDMXRegistryInterfaceType.RESTV1, ap.address());
			if (ap.name().equals(
					RegistryInterfaceCode.RESTV2.toString()))
				descriptor.setUrl(SDMXRegistryInterfaceType.RESTV2,ap.address());
			if (ap.name().equals(
					RegistryInterfaceCode.RESTV2_1.toString()))
				descriptor.setUrl(SDMXRegistryInterfaceType.RESTV2_1, ap.address());
			if (ap.name().equals(
					RegistryInterfaceCode.SOAPV1.toString()))
				descriptor.setUrl(SDMXRegistryInterfaceType.SOAPV1, ap.address());
			if (ap.name().equals(
					RegistryInterfaceCode.SOAPV2.toString()))
				descriptor.setUrl(SDMXRegistryInterfaceType.SOAPV2, ap.address());
			if (ap.name().equals(
					RegistryInterfaceCode.SOAPV2_1.toString()))
				descriptor.setUrl(SDMXRegistryInterfaceType.SOAPV2_1, ap.address());
		}
		log.debug("SDMX registry resource retrieved from IS: " + descriptor);
		return descriptor;

	}

	public void setCredentials (String username, String password)
	{
		if (username != null && password != null) this.credentials = new Base64Credentials(username, password);
	}
	
	
	@Override
	public Credentials getCredentials() {

		return this.credentials;
	}

	@Override
	public boolean versionAware() {

		return this.versionAware;
	}

}
