package org.gcube.spatial.data.sdi.engine.impl;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.GeoNetworkManager;
import org.gcube.spatial.data.sdi.engine.RoleManager;
import org.gcube.spatial.data.sdi.engine.impl.cluster.AbstractCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.GeoNetworkCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.GeoNetworkController;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.gn.extension.GeoNetworkClient;
import org.gcube.spatial.data.sdi.engine.impl.is.GeoNetworkRetriever;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkDescriptor;
import org.gcube.spatial.data.sdi.model.services.GeoNetworkServiceDefinition;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class GeoNetworkManagerImpl extends AbstractManager<GeoNetworkDescriptor, GeoNetworkServiceDefinition, GeoNetworkController> implements GeoNetworkManager {

	RoleManager roleManager;
	
	
	private GeoNetworkRetriever retriever=null;
	private GeoNetworkCluster cluster=null;

	@Inject
	public GeoNetworkManagerImpl(RoleManager roleManager) {
		this.roleManager=roleManager;
		retriever=new GeoNetworkRetriever();
		cluster=new GeoNetworkCluster(LocalConfiguration.getTTL(LocalConfiguration.GEONETWORK_CACHE_TTL), retriever, "GeoNEtwork - cache");
	}


	@Override
	protected AbstractCluster<GeoNetworkDescriptor, GeoNetworkController> getCluster() {
		return cluster;
	}	

	@Override
	protected ISModule getRetriever() {
		return retriever;
	}
	
	@Override
	public List<GeoNetworkDescriptor> getSuggestedInstances() throws ConfigurationNotFoundException {
		return Collections.singletonList(getCluster().getDefaultController().getDescriptor());
	}

	@Override
	public GeoNetworkClient getClient() throws ConfigurationNotFoundException {
		return getClient(getCluster().getDefaultController().getDescriptor());
	}
	
	@Override
	public GeoNetworkClient getClient(GeoNetworkDescriptor descriptor) {
		Credentials selected=roleManager.getMostAccessible(descriptor.getAccessibleCredentials(), false);
		log.info("Logging in {} using {} ",descriptor,selected);
		return new GeoNetworkClient(descriptor.getBaseEndpoint(), descriptor.getVersion(), selected.getPassword(), selected.getUsername(),descriptor);
	}
}
