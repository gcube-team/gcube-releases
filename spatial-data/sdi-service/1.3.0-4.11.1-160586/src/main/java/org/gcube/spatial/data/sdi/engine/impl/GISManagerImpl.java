package org.gcube.spatial.data.sdi.engine.impl;

import java.util.List;

import javax.inject.Singleton;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.GISManager;
import org.gcube.spatial.data.sdi.engine.impl.cluster.AbstractCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.GeoServerCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.GeoServerController;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.is.GeoServerClusterRetriever;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.model.service.GeoServerDescriptor;
import org.gcube.spatial.data.sdi.model.services.GeoServerDefinition;

@Singleton
public class GISManagerImpl extends AbstractManager<GeoServerDescriptor, GeoServerDefinition, GeoServerController> implements GISManager{

	private GeoServerClusterRetriever retriever=null;
	private GeoServerCluster cluster=null;
	
	public GISManagerImpl() {
		retriever=new GeoServerClusterRetriever();
		cluster=new GeoServerCluster(LocalConfiguration.getTTL(LocalConfiguration.GEOSERVER_CACHE_TTL), retriever, "GeoServer - cache");		
	}
	
	@Override
	protected AbstractCluster<GeoServerDescriptor, GeoServerController> getCluster() {
		return cluster;
	}
	
	@Override
	protected ISModule getRetriever() {
		return retriever;
	}
	@Override
	public List<GeoServerDescriptor> getSuggestedInstances() throws ConfigurationNotFoundException {
		return getAvailableInstances();
	}
	

}
