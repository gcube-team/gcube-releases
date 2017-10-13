package org.gcube.spatial.data.gis;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.XMLAdapter;
import org.gcube.spatial.data.geonetwork.iso.BoundingBox;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.gis.is.AbstractGeoServerDescriptor;
import org.gcube.spatial.data.gis.is.GeoServerDescriptor;
import org.gcube.spatial.data.gis.is.cache.ExplicitCache;
import org.gcube.spatial.data.gis.is.cache.GeoServerCache;
import org.gcube.spatial.data.gis.is.cache.ISGeoServerCache;
import org.gcube.spatial.data.gis.meta.MetadataEnricher;
import org.gcube.spatial.data.gis.model.report.DeleteReport;
import org.gcube.spatial.data.gis.model.report.PublishResponse;
import org.gcube.spatial.data.gis.model.report.Report;
import org.gcube.spatial.data.gis.model.report.Report.OperationState;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.DateType;

import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GISInterface{



	public static GISInterface get(AbstractGeoServerDescriptor... descriptors) throws Exception{
		if(descriptors!=null&&descriptors.length>0)
			return new GISInterface(Arrays.asList(descriptors));
		else return new GISInterface();		
	}




	//************ INSTANCE

	private List<XMLAdapter> toRegisterXMLAdapters=null;


	private GISInterface() throws Exception{
		theActualCache=new ISGeoServerCache();		
	}

	private <T extends AbstractGeoServerDescriptor> GISInterface(Collection<T> collection){
		theActualCache=new ExplicitCache<T>(collection);
	}


	public void setToRegisterXMLAdapters(List<XMLAdapter> toRegisterXMLAdapters) {
		this.toRegisterXMLAdapters = toRegisterXMLAdapters;
	}

	//*******************READER getter METHODS

	public GeoNetworkReader getGeoNetworkReader() throws Exception{
		return getGN();
	}

	public GeoNetworkPublisher getGeoNewtorkPublisher()throws Exception{
		return getGN();
	}


	/**
	 * Publishes toPublishFile [GeoTIFF] in the default GeoServer descriptor
	 * 
	 * @param workspace
	 * @param storeName
	 * @param coverageName
	 * @param toPublishFile
	 * @param srs
	 * @param policy
	 * @param defaultStyle
	 * @param bbox
	 * @param geoNetworkMeta
	 * @param gnCategory
	 * @param gnStylesheet
	 * @param level
	 * @param promoteMetadataPublishing
	 * @return
	 */
	public PublishResponse addGeoTIFF(String workspace, String storeName, String coverageName,
			File toPublishFile,String srs,
			ProjectionPolicy policy,String defaultStyle, double[] bbox, 
			Metadata geoNetworkMeta, String gnCategory,String gnStylesheet, LoginLevel level, boolean promoteMetadataPublishing){
		try{
			GeoNetworkPublisher gn=getGN();
			gn.login(level);
			GNInsertConfiguration config=gn.getCurrentUserConfiguration(gnCategory, gnStylesheet);
			return addGeoTIFF(workspace, storeName, coverageName, toPublishFile, srs, policy, defaultStyle, bbox, geoNetworkMeta, config, level,promoteMetadataPublishing);
		}catch(Exception e){
			PublishResponse response=new PublishResponse(geoNetworkMeta);
			response.getMetaOperationMessages().add("Unable to get GN Configuration , cause :"+e.getMessage());
			return response;
		}
	}

	/**
	 * Publishes the specified fte in the default GeoServer
	 * 
	 * @param workspace
	 * @param storeName
	 * @param fte
	 * @param layerEncoder
	 * @param geoNetworkMeta
	 * @param gnCategory
	 * @param gnStylesheet
	 * @param level
	 * @param promoteMetadataPublishing
	 * @return
	 */
	public PublishResponse publishDBTable(String workspace, String storeName, GSFeatureTypeEncoder fte,GSLayerEncoder layerEncoder,
			Metadata geoNetworkMeta, String gnCategory,String gnStylesheet,LoginLevel level, boolean promoteMetadataPublishing){
		try{
			GeoNetworkPublisher gn=getGN();
			gn.login(level);
			GNInsertConfiguration config=gn.getCurrentUserConfiguration(gnCategory, gnStylesheet);
			return publishDBTable(workspace, storeName, fte, layerEncoder, geoNetworkMeta, config, level,promoteMetadataPublishing);
		}catch(Exception e){
			PublishResponse response=new PublishResponse(geoNetworkMeta);
			response.getMetaOperationMessages().add("Unable to get GN Configuration , cause :"+e.getMessage());
			return response;
		}
	}

	/**
	 * @deprecated use addGeoTIFF(String workspace, String storeName, String coverageName,File toPublishFile,String srs,
	 * 			ProjectionPolicy policy,String defaultStyle, double[] bbox,
	 * 			Metadata geoNetworkMeta, String gnCategory,String gnStylesheet, LoginLevel level, boolean promoteMetadataPublishing)
	 * 
	 * @param workspace
	 * @param storeName
	 * @param coverageName
	 * @param toPublishFile
	 * @param srs
	 * @param policy
	 * @param defaultStyle
	 * @param bbox
	 * @param geoNetworkMeta
	 * @param config
	 * @param level
	 * @param promoteMetadataPublishing
	 * @return
	 */
	@Deprecated
	public PublishResponse addGeoTIFF(String workspace, String storeName, String coverageName,
			File toPublishFile,String srs,
			ProjectionPolicy policy,String defaultStyle, double[] bbox, 
			Metadata geoNetworkMeta, GNInsertConfiguration config, LoginLevel level,boolean promoteMetadataPublishing){
		boolean publishResult = false;
		PublishResponse toReturn=new PublishResponse(geoNetworkMeta);
		GeoServerRESTPublisher publisher=null;
		AbstractGeoServerDescriptor desc=getCache().getDefaultDescriptor();
		log.debug("Using "+desc);
		try{
			publisher=desc.getPublisher();
			// Publishing the file to geoserver depends on file type
			publishResult=publisher.publishGeoTIFF(workspace, storeName, coverageName, toPublishFile, srs, policy, defaultStyle, bbox);

			if(publishResult){
				// Data publish ok
				desc.onChangedLayers();
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);

				MetadataEnricher enricher=new MetadataEnricher(geoNetworkMeta, true);

				ArrayList<String> distributionUris=new ArrayList<String>();
				distributionUris.add(URIUtils.getWmsUrl(desc.getUrl(), coverageName, defaultStyle, new BoundingBox(bbox[0],bbox[1],bbox[2],bbox[3])));
				distributionUris.add(URIUtils.getWfsUrl(desc.getUrl(), coverageName));
				distributionUris.add(URIUtils.getWcsUrl(desc.getUrl(), coverageName, new BoundingBox(bbox[0],bbox[1],bbox[2],bbox[3])));
				try{
					distributionUris.add(URIUtils.getGisLinkByUUID(enricher.getMetadataIdentifier()));
				}catch(Exception e){
					log.warn("Unabel to get Gis Link ",e);
					toReturn.setMetaOperationResult(OperationState.WARN);
					toReturn.getMetaOperationMessages().add("Unable to generate GIS link, cause : "+e.getMessage());
				}

				enricher.addDate(new Date(System.currentTimeMillis()), DateType.CREATION);
				enricher.addPreview(distributionUris.get(0));
				enricher.setdistributionURIs(distributionUris,coverageName);	
				toReturn.getMetaOperationMessages().addAll(enricher.getMessages());
				if(enricher.getMessages().size()>0)toReturn.setMetaOperationResult(OperationState.WARN);
				GeoNetworkPublisher pub=getGN();
				getGN().login(level);

				Metadata enriched=enricher.getEnriched();
				toReturn.setPublishedMetadata(enriched);
				long returnedId=promoteMetadataPublishing?pub.insertAndPromoteMetadata(config, enriched):pub.insertMetadata(config,enriched);

				toReturn.setReturnedMetaId(returnedId);
				toReturn.setMetaOperationResult(OperationState.COMPLETE);
			}else toReturn.getDataOperationMessages().add("Publish operation returned false, unable to publish data");
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeUtils.getCurrentScope());
		} catch (IllegalArgumentException e) {
			if(publisher==null){
				toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
				log.debug("Unable to instatiate GeoServerRESTPublisher",e);
			}else {
				toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
				log.debug("Unable to publish data",e);
			}
		} catch (MalformedURLException e) {
			toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
			log.debug("Unable to instatiate GeoServerRESTPublisher",e);
		} catch (FileNotFoundException e) {
			toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
			log.debug("Unable to publish data",e);
		} catch (Exception e) {
			// exceptions raised by publishing metadata, need to clean up
			toReturn.getMetaOperationMessages().add("Unable to publish metadata, cause :"+e.getMessage());
			log.debug("Unable to publish metadata",e);
			DeleteReport delRep=deleteStore(workspace,storeName,null,desc);
			if(!delRep.getDataOperationResult().equals(OperationState.COMPLETE)){
				toReturn.setDataOperationResult(OperationState.WARN);
				toReturn.getDataOperationMessages().add("Unable to rollback data publishing, following messages from delete operation (state : "+delRep.getDataOperationResult()+")");
				toReturn.getDataOperationMessages().addAll(delRep.getDataOperationMessages());				
			}
		}		
		return toReturn;
	}
	
	
	/**
	 * @deprecated use publishDBTable(String workspace, String storeName, GSFeatureTypeEncoder fte,GSLayerEncoder layerEncoder,
	 * 			Metadata geoNetworkMeta, String gnCategory,String gnStylesheet,LoginLevel level, boolean promoteMetadataPublishing)
	 * 
	 * @param workspace
	 * @param storeName
	 * @param fte
	 * @param layerEncoder
	 * @param geoNetworkMeta
	 * @param config
	 * @param level
	 * @param promoteMetadataPublishing
	 * @return
	 */
	@Deprecated
	public PublishResponse publishDBTable(String workspace, String storeName, GSFeatureTypeEncoder fte,GSLayerEncoder layerEncoder,Metadata geoNetworkMeta, GNInsertConfiguration config,LoginLevel level,boolean promoteMetadataPublishing){
		boolean publishResult = false;
		PublishResponse toReturn=new PublishResponse(geoNetworkMeta);
		GeoServerRESTPublisher publisher=null;
		AbstractGeoServerDescriptor desc=getCache().getDefaultDescriptor();
		log.debug("Publish db table : "+storeName+" under ws : "+workspace+", using geoserver "+desc);
		log.debug("Using "+desc);
		try{
			publisher=desc.getPublisher();			
			// Publishing the file to geoserver depends on file type
			publishResult=publisher.publishDBLayer(workspace, storeName, fte, layerEncoder);

			if(publishResult){
				desc.onChangedLayers();
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);


				log.debug("Published data, enriching meta..");				
				MetadataEnricher enricher=new MetadataEnricher(geoNetworkMeta, true);

				ArrayList<String> distributionUris=new ArrayList<String>();
				distributionUris.add(URIUtils.getWmsUrl(desc.getUrl(), fte.getName(), URIUtils.getStyleFromGSLayerEncoder(layerEncoder), BoundingBox.WORLD_EXTENT));
				distributionUris.add(URIUtils.getWfsUrl(desc.getUrl(), fte.getName()));
				distributionUris.add(URIUtils.getWcsUrl(desc.getUrl(), fte.getName(), BoundingBox.WORLD_EXTENT));
				try{
					distributionUris.add(URIUtils.getGisLinkByUUID(enricher.getMetadataIdentifier()));
				}catch(Exception e){
					log.warn("Unabel to get Gis Link ",e);
					toReturn.setMetaOperationResult(OperationState.WARN);
					toReturn.getMetaOperationMessages().add("Unable to generate GIS link, cause : "+e.getMessage());
				}


				enricher.addDate(new Date(System.currentTimeMillis()), DateType.CREATION);
				enricher.addPreview(distributionUris.get(0));
				enricher.setdistributionURIs(distributionUris,fte.getName());	

				toReturn.getMetaOperationMessages().addAll(enricher.getMessages());
				if(enricher.getMessages().size()>0)toReturn.setMetaOperationResult(OperationState.WARN);


				GeoNetworkPublisher pub=getGN();
				getGN().login(level);
				Metadata enriched=enricher.getEnriched();
				toReturn.setPublishedMetadata(enriched);
				long returnedId=promoteMetadataPublishing?pub.insertAndPromoteMetadata(config, enriched):pub.insertMetadata(config,enriched);
				toReturn.setReturnedMetaId(returnedId);
				toReturn.setMetaOperationResult(OperationState.COMPLETE);
			}else {
				toReturn.getDataOperationMessages().add("Publish operation returned false, unable to publish data");

			}
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeUtils.getCurrentScope());
		} catch (IllegalArgumentException e) {
			if(publisher==null){
				toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
				log.debug("Unable to instatiate GeoServerRESTPublisher",e);
			}else {
				toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
				log.debug("Unable to publish data",e);
			}
		} catch (MalformedURLException e) {
			toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
			log.debug("Unable to instatiate GeoServerRESTPublisher",e);
		} catch (FileNotFoundException e) {
			toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
			log.debug("Unable to publish data",e);
		} catch (Exception e) {
			// exceptions raised by publishing metadata, need to clean up
			toReturn.getMetaOperationMessages().add("Unable to publish metadata, cause :"+e.getMessage());
			log.debug("Unable to publish metadata",e);
			DeleteReport delRep=deleteLayer(workspace,fte.getName(),null,desc,level);
			if(!delRep.getDataOperationResult().equals(OperationState.COMPLETE)){
				toReturn.setDataOperationResult(OperationState.WARN);
				toReturn.getDataOperationMessages().add("Unable to rollback data publishing, following messages from delete operation (state : "+delRep.getDataOperationResult()+")");
				toReturn.getDataOperationMessages().addAll(delRep.getDataOperationMessages());				
			}
		}		
		return toReturn;
	}

	/**
	 * Creates the declared style in the default GeoServer descriptor
	 * 
	 * @param sldBody
	 * @param styleName
	 * @return
	 */
	public PublishResponse publishStyle(String sldBody,String styleName){
		boolean publishResult = false;
		PublishResponse toReturn=new PublishResponse();
		GeoServerRESTPublisher publisher=null;
		AbstractGeoServerDescriptor desc=getCache().getDefaultDescriptor();
		log.debug("Using "+desc);
		try{
			publisher=desc.getPublisher();
			// Publishing the file to geoserver depends on file type
			publishResult=publisher.publishStyle(sldBody, styleName);

			if(publishResult){
				desc.onChangedStyles();
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);
			}else toReturn.getDataOperationMessages().add("Publish operation returned false, unable to publish data");
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeUtils.getCurrentScope());
		} catch (IllegalArgumentException e) {
			if(publisher==null){
				toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
				log.debug("Unable to instatiate GeoServerRESTPublisher",e);
			}else {
				toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
				log.debug("Unable to publish data",e);
			}
		} catch (MalformedURLException e) {
			toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
			log.debug("Unable to instatiate GeoServerRESTPublisher",e);
		} catch (Exception e) {
			// exceptions raised by publishing metadata, need to clean up
			toReturn.getMetaOperationMessages().add("Unable to publish metadata, cause :"+e.getMessage());
			log.debug("Unable to publish metadata",e);
			DeleteReport delRep=deleteStyle(styleName,desc);
			if(!delRep.getDataOperationResult().equals(OperationState.COMPLETE)){
				toReturn.setDataOperationResult(OperationState.WARN);
				toReturn.getDataOperationMessages().add("Unable to rollback data publishing, following messages from delete operation (state : "+delRep.getDataOperationResult()+")");
				toReturn.getDataOperationMessages().addAll(delRep.getDataOperationMessages());				
			}
		}		
		return toReturn;
	}

	// ********************* DELETE Logic
	/**
	 * Deletes the specified datastore from the GeoServer instance described in desc
	 * 
	 * @param workspace
	 * @param storeName
	 * @param metadataUUID
	 * @param desc
	 * @return
	 */
	public DeleteReport deleteStore(String workspace,String storeName,Long metadataUUID,AbstractGeoServerDescriptor desc){
		DeleteReport toReturn=new DeleteReport();
		GeoServerRESTPublisher publisher=null;
		try{			
			publisher=desc.getPublisher();
			boolean removed=publisher.removeDatastore(workspace, storeName,true);
			if(removed){		
				desc.onChangedDataStores();
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);
				if(metadataUUID!=null){
					getGN().deleteMetadata(metadataUUID);
				}else {
					toReturn.setMetaOperationResult(OperationState.WARN);
					toReturn.getMetaOperationMessages().add("Passed meta UUID is null, no metadata deleted");
				}
			}else toReturn.getDataOperationMessages().add("Remove data operation returned false, unable to delete Store");
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeUtils.getCurrentScope());
		} catch (IllegalArgumentException e) {
			if(publisher==null)
				toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
			else toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
		} catch (MalformedURLException e) {
			toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
		} catch (Exception e) {
			toReturn.getMetaOperationMessages().add("Unable to delete metadata, cause :"+e.getMessage());
		} 
		return toReturn;
	}

	/**
	 * Deletes the specified layer from the GeoServer instance described by desc.
	 * 
	 * @param workspace
	 * @param layerName
	 * @param metadataUUID
	 * @param desc
	 * @param gnLoginLevel
	 * @return
	 */
	public DeleteReport deleteLayer(String workspace,String layerName, Long metadataUUID,AbstractGeoServerDescriptor desc,LoginLevel gnLoginLevel){
		DeleteReport toReturn=new DeleteReport();
		GeoServerRESTPublisher publisher=null;
		try{
			publisher=desc.getPublisher();
			boolean removed=publisher.removeLayer(workspace, layerName);
			if(removed){		
				desc.onChangedLayers();
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);
				if(metadataUUID!=null){
					GeoNetworkPublisher gnPub=getGN();
					gnPub.login(gnLoginLevel);
					gnPub.deleteMetadata(metadataUUID);
				}else {
					toReturn.setMetaOperationResult(OperationState.WARN);
					toReturn.getMetaOperationMessages().add("Passed meta UUID is null, no metadata deleted");
				}
			}else toReturn.getDataOperationMessages().add("Remove data operation returned false, unable to delete Store");
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeUtils.getCurrentScope());
		} catch (IllegalArgumentException e) {
			if(publisher==null)
				toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
			else toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
		} catch (MalformedURLException e) {
			toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
		} catch (Exception e) {
			toReturn.getMetaOperationMessages().add("Unable to delete metadata, cause :"+e.getMessage());
		} 
		return toReturn;
	}

	/**
	 * Deletes a specific style from the GeoServer described by dec.
	 * 
	 * @param styleName
	 * @param desc
	 * @return
	 */
	public DeleteReport deleteStyle(String styleName,AbstractGeoServerDescriptor desc){
		DeleteReport toReturn=new DeleteReport();
		GeoServerRESTPublisher publisher=null;
		try{
			publisher=desc.getPublisher();
			boolean removed=publisher.removeStyle(styleName, true);
			if(removed){	
				desc.onChangedStyles();
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);
			}else toReturn.getDataOperationMessages().add("Remove data operation returned false, unable to delete Store");
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeUtils.getCurrentScope());
		} catch (IllegalArgumentException e) {
			if(publisher==null)
				toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
			else toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
		} catch (MalformedURLException e) {
			toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
		} catch (Exception e) {
			toReturn.getMetaOperationMessages().add("Unable to delete metadata, cause :"+e.getMessage());
		} 
		return toReturn;
	}

	//************* DATASTORES / WS 



	/**
	 * Creates the specified workspace in all GeoServer instances of the current GeoServer pool
	 * 
	 * @param workspace
	 */
	public void createWorkspace(String workspace){		
		log.info("Create workspace {} in geoservers",workspace);
		if(workspace==null || workspace.length()<1) throw new RuntimeException("Invalid workspace name : "+workspace);
		for(AbstractGeoServerDescriptor gs:getCurrentCacheElements(false)){
			try{
				createWorkspace(workspace,gs);
			}catch(MalformedURLException e){
				log.warn("Wrong URL in descriptor {} ",gs.getUrl(),e);
			}catch(IllegalArgumentException e){
				log.warn("Unable to operate service in {} ",gs.getUrl(),e);
			}
			catch(Exception e){
				log.warn("Unable to check/create ws {} in {} ",workspace,gs.getUrl(),e);
			}
		}
	}


	/**
	 * Creates the specified datastore under the mentioned workspace in all GeoServer instances of the current GeoServer pool.
	 * 
	 * @param workspace
	 * @param datastore
	 */
	public void createDataStore(String workspace,GSAbstractStoreEncoder datastore){
		log.info("Create datastore {}, ws {} in geoservers",datastore,workspace);
		if(workspace==null || workspace.length()<1) throw new RuntimeException("Invalid workspace name : "+workspace);
		if(datastore==null) throw new RuntimeException("Invalid datastore "+datastore);
		for(AbstractGeoServerDescriptor gs:getCurrentCacheElements(false)){
			try{
				createDataStore(workspace,datastore,gs);
			}catch(MalformedURLException e){
				log.warn("Wrong URL in descriptor {} ",gs.getUrl(),e);
			}catch(IllegalArgumentException e){
				log.warn("Unable to operate service in {} ",gs.getUrl(),e);
			}
			catch(Exception e){
				log.warn("Unable to check/create ws {} in {} ",workspace,gs.getUrl(),e);
			}
		}
	}

	private static void createWorkspace(String workspace,AbstractGeoServerDescriptor gs) throws MalformedURLException, IllegalArgumentException,Exception{
		if(gs==null) throw new IllegalArgumentException("GeoServer Descriptor is "+gs);
		log.info("Creating ws {} in {} ",workspace,gs.getUrl());
		if(workspace==null || workspace.length()<1) throw new RuntimeException("Invalid workspace name : "+workspace);
		if(gs.getWorkspaces().contains(workspace))
			log.debug("Workspace {} already existing in {} ",workspace,gs.getUrl());
		else{
			boolean result =gs.getPublisher().createWorkspace(workspace);
			gs.onChangedWorkspaces();				
			if(!gs.getWorkspaces().contains(workspace)) throw new Exception("Workspace is not created. Create operation returned "+result);				
		}
	}



	private static void createDataStore(String workspace,GSAbstractStoreEncoder datastore,AbstractGeoServerDescriptor gs)throws MalformedURLException, IllegalArgumentException,Exception{
		if(gs==null) throw new IllegalArgumentException("GeoServer Descriptor is "+gs);
		log.info("Create datastore {}, ws {} in {} ",datastore,workspace,gs.getUrl());
		if(workspace==null || workspace.length()<1) throw new RuntimeException("Invalid workspace name : "+workspace);
		createWorkspace(workspace,gs);
		if(gs.getDatastores(workspace).contains(datastore.getName()))
			log.debug("Datastore {}:{} already existing in {}",workspace,datastore.getName(),gs.getUrl());
		else{
			boolean result =gs.getDataStoreManager().create(workspace, datastore);
			gs.onChangedDataStores();
			if(!gs.getDatastores(workspace).contains(datastore.getName())) throw new Exception("Datastore not created. Create operation returned "+result);
		}
	}
	//************ CACHE Management

	private GeoServerCache theActualCache;


	private GeoServerCache getCache(){
		return theActualCache;
	}

	//************

	private GeoNetworkPublisher geoNetwork=null;


	private synchronized GeoNetworkPublisher getGN() throws Exception{
		if(geoNetwork==null) {
			geoNetwork=GeoNetwork.get();
			if(toRegisterXMLAdapters!=null)
				for(XMLAdapter adapter:toRegisterXMLAdapters)
					geoNetwork.registerXMLAdapter(adapter);
		}
		return geoNetwork;
	}	

	/**
	 * Returns the current GeoServer from the GeoServer pool. Selection is made according to Configuration file.
	 * 
	 * @return
	 */
	public AbstractGeoServerDescriptor getCurrentGeoServer(){
		return getCache().getDefaultDescriptor();
	}

	/**
	 * Returns the current GeoServer descriptors from the GeoServer pool. 
	 * 
	 * @param forceUpdate Set true to force re-initialization
	 * @return
	 */
	public SortedSet<AbstractGeoServerDescriptor> getCurrentCacheElements(Boolean forceUpdate){
		return getCache().getDescriptorSet(forceUpdate);
	}
	
	/**
	 * Returns a GeoServer descriptor according to specified ResearchMethod method.
	 * 
	 * @param forceUpdate Set true to force re-initialization
	 * @return
	 */
	public AbstractGeoServerDescriptor getGeoServerByMethod(ResearchMethod method, Boolean forceUpdate){
		return getCache().getDescriptor(forceUpdate, method);
	}
	
	//************************ DEPRECATED OBSOLETE METHODS

	@Deprecated
	public GeoServerRESTReader getGeoServerReader(ResearchMethod method,boolean forceRefresh) throws Exception{
		log.warn("*************** ACCESS TO DEPRECATED METHOD GeoServerRESTReader getGeoServerReader(ResearchMethod method,boolean forceRefresh). Please update your code.");
		return getCache().getDescriptor(forceRefresh, method).getReader();		
	}

	@Deprecated
	public GeoServerRESTReader getGeoServerReader(GeoServerDescriptor desc)throws Exception{
		log.warn("*************** ACCESS TO DEPRECATED METHOD GeoServerRESTReader getGeoServerReader(GeoServerDescriptor desc). Please update your code.");
		return desc.getReader();
	}

	@Deprecated
	public GeoServerRESTReader getGeoServerReader(String url,String user,String password) throws IllegalArgumentException, MalformedURLException{
		log.warn("*************** ACCESS TO DEPRECATED METHOD GeoServerRESTReader getGeoServerReader(String url,String user,String password). Please update your code.");
		return new GeoServerDescriptor(url,user,password,0l).getReader();
	}

	@Deprecated
	public GeoServerRESTReader getGeoServerReader(String url) throws MalformedURLException{
		log.warn("*************** ACCESS TO DEPRECATED METHOD GeoServerRESTReader getGeoServerReader(String url). Please update your code.");
		return new GeoServerRESTReader(url);
	}

	@Deprecated
	public GeoServerDescriptor getCurrentGeoServerDescriptor(){
		log.warn("*************** ACCESS TO DEPRECATED METHOD GeoServerDescriptor getCurrentGeoServerDescriptor(). Please update your code.");
		return translate(getCache().getDefaultDescriptor());

	}

	@Deprecated
	public SortedSet<GeoServerDescriptor> getGeoServerDescriptorSet(boolean forceRefresh){
		log.warn("*************** ACCESS TO DEPRECATED METHOD SortedSet<GeoServerDescriptor> getGeoServerDescriptorSet(boolean forceRefresh). Please update your code.");
		ConcurrentSkipListSet<GeoServerDescriptor> toReturn=new ConcurrentSkipListSet<GeoServerDescriptor>();
		for(Object desc: getCache().getDescriptorSet(forceRefresh)){
			toReturn.add(translate((AbstractGeoServerDescriptor) desc));
		}
		return toReturn;
	}

	@Deprecated
	private GeoServerDescriptor translate(AbstractGeoServerDescriptor desc){
		long count=0l;
		try{
			count=desc.getHostedLayersCount();
		}catch(Exception e){
			log.warn("Unable to get layer count from desc {} ",desc,e);
		}
		return new GeoServerDescriptor (desc.getUrl(),desc.getUser(),desc.getPassword(),count);		
	}
}
