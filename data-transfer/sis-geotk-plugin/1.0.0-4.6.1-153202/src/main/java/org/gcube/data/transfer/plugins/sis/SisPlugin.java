package org.gcube.data.transfer.plugins.sis;

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
import org.gcube.data.transfer.plugin.AbstractPlugin;
import org.gcube.data.transfer.plugin.fails.PluginCleanupException;
import org.gcube.data.transfer.plugin.fails.PluginExecutionException;
import org.gcube.data.transfer.plugin.model.DataTransferContext;
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
	DataTransferContext ctx;
	String publicCatalogLocation;
	public SisPlugin(PluginInvocation invocation, DataTransferContext ctx, String publicCatalogLocation) {
		super(invocation);
		this.ctx=ctx;
		this.publicCatalogLocation=publicCatalogLocation;
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
			
			String hostname=ctx.getCtx().container().configuration().hostname();
			String filename=dataStore.getName();
			String catalog=getCatalogFromPath(dataStorePath);
			
			Set<TemplateInvocation> invocations=new TemplateInvocationBuilder().
					threddsOnlineResources(hostname,filename, catalog).get();
			
			MetadataPublishOptions options=new MetadataPublishOptions(invocations);
			options.setGeonetworkCategory(category);
			options.setGeonetworkStyleSheet(stylesheet);
			options.setValidate(false);
			
			
			MetadataReport report=metadataInterface.pushMetadata(tmp, options);
			log.debug("Metadata report is {} ",report);
			return new ExecutionReport(invocation,
					"Published/Updated meta with id : "+report.getPublishedID()+" , UUID "+report.getPublishedUUID(),ExecutionReportFlag.SUCCESS);
			
////			TemplateManager.getTHREDDSLinks(new ThreddsLinkRequest(hostname, filename, catalog, gisViewerLink))
//			// TODO Info from infrastructure			
//			try{
//				long id=publishMetadata(tmp, category, stylesheet,getGNPublisher());
//				return new ExecutionReport(invocation, "Published meta with id : "+id, ExecutionReportFlag.SUCCESS);
//			}catch(GNServerException e){
//				//Fail can be due to already existing uuid.
//				log.debug("Trying to update existing uuid");
//				
//				MetadataHandler metaHandler=new MetadataHandler(tmp);
//				String uuid=metaHandler.getUUID();
//				long id= updateMetadata(tmp, uuid, category, stylesheet,getGNPublisher());
//				return new ExecutionReport(invocation, "Updated meta with id : "+id+", UUID : "+uuid, ExecutionReportFlag.SUCCESS);
//			}



		}catch(DataStoreException e){
			log.error("Unable to parse source ",e);
			throw new PluginExecutionException("Unable to extract metadata.", e);
		}catch(Throwable t){
			log.error("Unexpected error while generating metadata.",t);
			throw new PluginExecutionException("Unexpected error while generating meta.",t);
		}
	}

//	public GeoNetworkPublisher getGNPublisher() throws Exception{
//		if(publisher==null){
//		publisher=GeoNetwork.get();
//		publisher.login(LoginLevel.DEFAULT);
//		}
//		return publisher;
//	}
//	
	
	public static final Metadata getMetaFromFile(Object dataStore) throws UnsupportedStorageException, DataStoreException{
		return DataStores.open(dataStore).getMetadata();
	}

	private String getCatalogFromPath(String path){
		return getCatalogFromPath(path, publicCatalogLocation);
	}
	
	public static String getCatalogFromPath(String path, String publicCatalogLocation) {
		log.debug("Getting catalog from path {} ",path);
		String catalog=path.substring(publicCatalogLocation.length());
		if(catalog.contains("/")) {
			catalog=catalog.substring(0, catalog.lastIndexOf("/"));
			log.debug("Subcatalog found {} ",catalog);			
		}else {
			log.debug("No catalog found");
			catalog=null;
		}		
		return catalog;
	}
	
//
//	public long publishMetadata(File toPublish,String category, String stylesheet, GeoNetworkPublisher publisher) throws Exception{			
//		GNInsertConfiguration config=publisher.getCurrentUserConfiguration(category, stylesheet);
//		config.setValidate(false);		
//		long toReturn= publisher.insertMetadata(config, toPublish);		
//		return toReturn;
//	}
//	
//	public long updateMetadata(File toPublish,String uuid,String category,String styleSheet, GeoNetworkPublisher publisher)throws Exception{
//		long id=0l;
//		if(publisher.getConfiguration().getGeoNetworkVersion().equals(Version.DUE)){
//			GNSearchRequest req=new GNSearchRequest();
//			req.addParam(GNSearchRequest.Param.any,uuid);
//			
//			GNSearchResponse resp=publisher.query(req);
//			id=resp.iterator().next().getId();				
//		}else id=publisher.getInfo(uuid).getId();
//		publisher.updateMetadata(id, toPublish);
//		return id;
//	}
}
