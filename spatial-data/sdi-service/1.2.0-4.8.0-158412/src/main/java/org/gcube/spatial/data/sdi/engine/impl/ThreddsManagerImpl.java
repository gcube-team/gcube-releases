package org.gcube.spatial.data.sdi.engine.impl;

import java.io.File;
import java.util.List;

import javax.inject.Singleton;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.engine.impl.cluster.AbstractCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.ThreddsCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.ThreddsController;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.engine.impl.is.ThreddsRetriever;
import org.gcube.spatial.data.sdi.model.CatalogDescriptor;
import org.gcube.spatial.data.sdi.model.service.ThreddsDescriptor;
import org.gcube.spatial.data.sdi.model.services.ThreddsDefinition;

@Singleton
public class ThreddsManagerImpl extends AbstractManager<ThreddsDescriptor, ThreddsDefinition, ThreddsController> implements ThreddsManager  {

	private ThreddsCluster cluster=null;
	
	private ThreddsRetriever retriever=null;
	
	public ThreddsManagerImpl() {
		retriever=new ThreddsRetriever();
		cluster=new ThreddsCluster(LocalConfiguration.getTTL(LocalConfiguration.THREDDS_CACHE_TTL),retriever,"Thredds Cache");		
	}
	
	@Override
	protected AbstractCluster<ThreddsDescriptor, ThreddsController> getCluster() {
		return cluster;
	}
	
	@Override
	protected ISModule getRetriever() {
		return retriever;
	}
	@Override
	public List<ThreddsDescriptor> getSuggestedInstances() throws ConfigurationNotFoundException {
		return getAvailableInstances();
	}
	
	
	@Override
	public CatalogDescriptor createCatalog(File catalogFile, String catalogReference) throws ConfigurationNotFoundException {
		return getCluster().getDefaultController().createCatalog(catalogFile, catalogReference);
	}
	
}
