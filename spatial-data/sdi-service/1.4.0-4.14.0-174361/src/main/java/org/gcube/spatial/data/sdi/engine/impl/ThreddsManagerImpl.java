package org.gcube.spatial.data.sdi.engine.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.transfer.model.plugins.thredds.ThreddsCatalog;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.TemplateManager;
import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.engine.impl.cluster.AbstractCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.ThreddsCluster;
import org.gcube.spatial.data.sdi.engine.impl.cluster.ThreddsController;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ThreddsOperationFault;
import org.gcube.spatial.data.sdi.engine.impl.is.ISModule;
import org.gcube.spatial.data.sdi.engine.impl.is.ThreddsRetriever;
import org.gcube.spatial.data.sdi.engine.impl.metadata.GenericTemplates;
import org.gcube.spatial.data.sdi.model.service.ThreddsDescriptor;
import org.gcube.spatial.data.sdi.model.services.ThreddsDefinition;

import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ThreddsManagerImpl extends AbstractManager<ThreddsDescriptor, ThreddsDefinition, ThreddsController> implements ThreddsManager  {

	private ThreddsCluster cluster=null;
	
	private ThreddsRetriever retriever=null;
	
	private TemplateManager templateManager=null;	
	
	
	@Inject
	public ThreddsManagerImpl(TemplateManager templateManager) {
		retriever=new ThreddsRetriever();
		cluster=new ThreddsCluster(LocalConfiguration.getTTL(LocalConfiguration.THREDDS_CACHE_TTL),retriever,"Thredds Cache");
		this.templateManager=templateManager;
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
	public ThreddsCatalog publishCatalog(File catalogFile, String catalogReference) throws ConfigurationNotFoundException, ThreddsOperationFault {
		return getCluster().getDefaultController().publishCatalog(catalogFile, catalogReference);
	}
	
	@Override
	public ThreddsCatalog createCatalogFromTemplate(String authorityUrl, String catalogPath, String datasetScanId,
			String datasetScanName, String subFolder, String catalogReference) throws Exception {
		ThreddsController controller=getCluster().getDefaultController();
		ThreddsInfo info=controller.getThreddsInfo();
		
		
		log.info("Going to create catalog for authorityURL {}, path {}, subFolder {} ",authorityUrl,catalogPath,subFolder);
		
		HashMap<String,String> parameters=new HashMap<String,String>();
		parameters.put(GenericTemplates.ThreddsCatalogTemplate.AUTHORITY_URL, authorityUrl);
		parameters.put(GenericTemplates.ThreddsCatalogTemplate.CATALOG_PATH, catalogPath);
		parameters.put(GenericTemplates.ThreddsCatalogTemplate.DATASET_SCAN_ID, datasetScanId);
		parameters.put(GenericTemplates.ThreddsCatalogTemplate.DATASET_SCAN_NAME, datasetScanName);
		parameters.put(GenericTemplates.ThreddsCatalogTemplate.LOCATION, info.getLocalBasePath()+"/"+subFolder);
		
		File catalog=
				templateManager.generateFromTemplate(parameters, GenericTemplates.ThreddsCatalogTemplate.FILENAME);
		
		return controller.publishCatalog(catalog, catalogReference);
	}
}
