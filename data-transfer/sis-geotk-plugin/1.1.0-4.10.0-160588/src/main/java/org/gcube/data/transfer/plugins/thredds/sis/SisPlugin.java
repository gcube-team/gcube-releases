package org.gcube.data.transfer.plugins.thredds.sis;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.UnsupportedStorageException;
import org.apache.sis.xml.XML;
import org.gcube.data.transfer.model.ExecutionReport;
import org.gcube.data.transfer.model.ExecutionReport.ExecutionReportFlag;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.model.plugins.thredds.DataSet;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsCatalog;
import org.gcube.data.transfer.plugin.AbstractPlugin;
import org.gcube.data.transfer.plugin.fails.PluginCleanupException;
import org.gcube.data.transfer.plugin.fails.PluginExecutionException;
import org.gcube.data.transfer.plugins.thredds.ThreddsInstanceManager;
import org.gcube.spatial.data.sdi.model.metadata.MetadataPublishOptions;
import org.gcube.spatial.data.sdi.model.metadata.MetadataReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocation;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocationBuilder;
import org.gcube.spatial.data.sdi.plugins.SDIAbstractPlugin;
import org.opengis.metadata.Metadata;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SisPlugin extends AbstractPlugin {

	File tmp=null;
	
	ThreddsInstanceManager instanceManager;

	public SisPlugin(PluginInvocation invocation, ThreddsInstanceManager instanceManager) {
		super(invocation);
		this.instanceManager=instanceManager;
	}

	@Override
	public void cleanup() throws PluginCleanupException {
		try{
			if(tmp!=null) tmp.delete();
		}catch(Throwable t){
			throw new PluginCleanupException("Unable to delete tmp file "+(tmp==null?tmp:tmp.getAbsolutePath()), t);
		}
	}

	@Override
	public ExecutionReport run() throws PluginExecutionException {		
		try{

			Map<String,String> params=invocation.getParameters();
			String dataStorePath=params.get(SISPluginFactory.SOURCE_PARAMETER);
			String category=params.get(SISPluginFactory.GEONETWORK_CATEGORY);
			String stylesheet=params.get(SISPluginFactory.GEONETWORK_STYLESHEET);
			File dataStore=new File(dataStorePath);
			log.debug("Extracting meta from {} ",dataStore.getAbsolutePath());
			Metadata meta=getMetaFromFile(dataStore);
			
			tmp=File.createTempFile("tmp_meta_", ".xml");

			XML.marshal(meta,tmp);
			
			org.gcube.spatial.data.sdi.interfaces.Metadata metadataInterface=SDIAbstractPlugin.metadata().build();
			
			String hostname=instanceManager.getCurrentHostname();
			String filename=dataStore.getName();			
			ThreddsCatalog catalog=instanceManager.getInfo().getCatalogByFittingLocation(dataStorePath);
			log.debug("Catalog for transferred File at {} is {} ",dataStorePath,catalog);
			DataSet catalogDataset=catalog.getDataSetFromLocation(dataStorePath);
			
			String datasetSubPath=dataStorePath.substring(catalogDataset.getLocation().length(), dataStorePath.lastIndexOf("/"));
			String datasetPath=catalogDataset.getPath()+datasetSubPath;
			
			
			log.info("Publishing dataset {} with path {} ",dataStorePath,datasetPath);
			Set<TemplateInvocation> invocations=new TemplateInvocationBuilder().
					threddsOnlineResources(hostname,filename, datasetPath).get();
			
			MetadataPublishOptions options=new MetadataPublishOptions(invocations);
			options.setGeonetworkCategory(category);
			options.setGeonetworkStyleSheet(stylesheet);
			options.setValidate(catalogDataset.getPath().startsWith("public"));
			
			
			MetadataReport report=metadataInterface.pushMetadata(tmp, options);
			log.debug("Metadata report is {} ",report);
			return new ExecutionReport(invocation,
					"Published/Updated meta with id : "+report.getPublishedID()+" , UUID "+report.getPublishedUUID(),ExecutionReportFlag.SUCCESS);


		}catch(DataStoreException e){
			log.error("Unable to parse source ",e);
			throw new PluginExecutionException("Unable to extract metadata.", e);
		}catch(Throwable t){
			log.error("Unexpected error while generating metadata.",t);
			throw new PluginExecutionException("Unexpected error while generating meta.",t);
		}
	}
	
	public static final Metadata getMetaFromFile(Object dataStore) throws UnsupportedStorageException, DataStoreException{
		return DataStores.open(dataStore).getMetadata();
	}

	
	
}
