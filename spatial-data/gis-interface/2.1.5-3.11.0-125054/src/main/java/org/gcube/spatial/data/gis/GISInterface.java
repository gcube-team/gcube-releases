package org.gcube.spatial.data.gis;

import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.XMLAdapter;
import org.gcube.spatial.data.gis.is.GeoServerDescriptor;
import org.gcube.spatial.data.gis.is.InfrastructureCrawler;
import org.gcube.spatial.data.gis.meta.MetadataEnricher;
import org.gcube.spatial.data.gis.model.BoundingBox;
import org.gcube.spatial.data.gis.model.report.DeleteReport;
import org.gcube.spatial.data.gis.model.report.PublishResponse;
import org.gcube.spatial.data.gis.model.report.Report;
import org.gcube.spatial.data.gis.model.report.Report.OperationState;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.DateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GISInterface {

	final static Logger logger= LoggerFactory.getLogger(GISInterface.class);
	
	private static final long MAX_GEOSERVER_CACHE_TIME=2*60*1000;
	
	public static GISInterface get() throws Exception{
		return new GISInterface();
	}

	
	
	
	//************ INSTANCE
	
	private List<XMLAdapter> toRegisterXMLAdapters=null;
	
	
	private GISInterface() throws Exception{
		
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
	
	public GeoServerRESTReader getGeoServerReader(ResearchMethod method,boolean forceRefresh) throws Exception{
		GeoServerDescriptor desc=getGeoServerSet(forceRefresh).last();
		return getGeoServerReader(desc);
	}
	
	public GeoServerRESTReader getGeoServerReader(GeoServerDescriptor desc)throws Exception{		
		return getGeoServerManager(desc.getUrl(), desc.getUser(), desc.getPassword()).getReader();
	}
	
	public GeoServerRESTReader getGeoServerReader(String url,String user,String password) throws IllegalArgumentException, MalformedURLException{
		return getGeoServerManager(url, user, password).getReader();
	}
	
	public GeoServerRESTReader getGeoServerReader(String url) throws MalformedURLException{
		return new GeoServerRESTReader(url);
	}
	
	public GeoServerDescriptor getCurrentGeoServerDescriptor(){
		return getGeoServerSet(false).last();
	}
	
	public SortedSet<GeoServerDescriptor> getGeoServerDescriptorSet(boolean forceRefresh){
		return getGeoServerSet(forceRefresh);
	}
	
	//******************* Create logic
	
	public Report.OperationState createWorkspace(String workspace){
		return null;
	}
	
	
	
	/**
	 * @see it.geosolutions.geoserver.rest.GeoServerRESTPublisher#publishGeoTIFF(String, String, String, File, String, ProjectionPolicy, String, double[])
	 *
	 */
	public PublishResponse addGeoTIFF(String workspace, String storeName, String coverageName,
			File toPublishFile,String srs,
			ProjectionPolicy policy,String defaultStyle, double[] bbox, 
			Metadata geoNetworkMeta, GNInsertConfiguration config, LoginLevel level){
		boolean publishResult = false;
		PublishResponse toReturn=new PublishResponse(geoNetworkMeta);
		GeoServerRESTPublisher publisher=null;
		GeoServerDescriptor desc=getGeoServerSet(false).last();
		logger.debug("Using "+desc);
		try{
			publisher=getGeoServerManager(desc.getUrl(), desc.getUser(), desc.getPassword()).getPublisher();
			// Publishing the file to geoserver depends on file type
			publishResult=publisher.publishGeoTIFF(workspace, storeName, coverageName, toPublishFile, srs, policy, defaultStyle, bbox);
			
			if(publishResult){
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);
				
				
				ArrayList<String> distributionUris=new ArrayList<String>();
				distributionUris.add(URIUtils.getWmsUrl(desc.getUrl(), coverageName, defaultStyle, new BoundingBox(bbox)));
				distributionUris.add(URIUtils.getWfsUrl(desc.getUrl(), coverageName));
				distributionUris.add(URIUtils.getWcsUrl(desc.getUrl(), coverageName, new BoundingBox(bbox)));
				
				MetadataEnricher enricher=new MetadataEnricher(geoNetworkMeta, true);
				enricher.addDate(new Date(System.currentTimeMillis()), DateType.CREATION);
				enricher.addPreview(distributionUris.get(0));
				enricher.setdistributionURIs(distributionUris,coverageName);	
				toReturn.getMetaOperationMessages().addAll(enricher.getMessages());
				if(enricher.getMessages().size()>0)toReturn.setMetaOperationResult(OperationState.WARN);
				GeoNetworkPublisher pub=getGN();
				getGN().login(level);
				
				Metadata enriched=enricher.getEnriched();
				toReturn.setPublishedMetadata(enriched);
				long returnedId=pub.insertMetadata(config,enriched);
				
				toReturn.setReturnedMetaId(returnedId);
				toReturn.setMetaOperationResult(OperationState.COMPLETE);
			}else toReturn.getDataOperationMessages().add("Publish operation returned false, unable to publish data");
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeProvider.instance.get());
		} catch (IllegalArgumentException e) {
			if(publisher==null){
				toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
				logger.debug("Unable to instatiate GeoServerRESTPublisher",e);
			}else {
				toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
				logger.debug("Unable to publish data",e);
			}
		} catch (MalformedURLException e) {
			toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
			logger.debug("Unable to instatiate GeoServerRESTPublisher",e);
		} catch (FileNotFoundException e) {
			toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
			logger.debug("Unable to publish data",e);
		} catch (Exception e) {
			// exceptions raised by publishing metadata, need to clean up
			toReturn.getMetaOperationMessages().add("Unable to publish metadata, cause :"+e.getMessage());
			logger.debug("Unable to publish metadata",e);
			DeleteReport delRep=deleteStore(workspace,storeName,null,desc);
			if(!delRep.getDataOperationResult().equals(OperationState.COMPLETE)){
				toReturn.setDataOperationResult(OperationState.WARN);
				toReturn.getDataOperationMessages().add("Unable to rollback data publishing, following messages from delete operation (state : "+delRep.getDataOperationResult()+")");
				toReturn.getDataOperationMessages().addAll(delRep.getDataOperationMessages());				
			}
		}		
		return toReturn;
	}
	

	public PublishResponse publishDBTable(String workspace, String storeName, GSFeatureTypeEncoder fte,GSLayerEncoder layerEncoder,Metadata geoNetworkMeta, GNInsertConfiguration config,LoginLevel level){
		boolean publishResult = false;
		PublishResponse toReturn=new PublishResponse(geoNetworkMeta);
		GeoServerRESTPublisher publisher=null;
		GeoServerDescriptor desc=getGeoServerSet(false).last();
		logger.debug("Publish db table : "+storeName+" under ws : "+workspace+", using geoserver "+desc);
		logger.debug("Using "+desc);
		try{
			GeoServerRESTManager mng=getGeoServerManager(desc.getUrl(), desc.getUser(), desc.getPassword());
			publisher=mng.getPublisher();
			// Publishing the file to geoserver depends on file type
			publishResult=publisher.publishDBLayer(workspace, storeName, fte, layerEncoder);
			
			if(publishResult){
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);
				
				
				logger.debug("Published data, enriching meta..");				
				ArrayList<String> distributionUris=new ArrayList<String>();
				distributionUris.add(URIUtils.getWmsUrl(desc.getUrl(), fte.getName(), URIUtils.getStyleFromGSLayerEncoder(layerEncoder), null));
				distributionUris.add(URIUtils.getWfsUrl(desc.getUrl(), fte.getName()));
				distributionUris.add(URIUtils.getWcsUrl(desc.getUrl(), fte.getName(), null));
				
				MetadataEnricher enricher=new MetadataEnricher(geoNetworkMeta, true);
				enricher.addDate(new Date(System.currentTimeMillis()), DateType.CREATION);
				enricher.addPreview(distributionUris.get(0));
				enricher.setdistributionURIs(distributionUris,fte.getName());	
				
				toReturn.getMetaOperationMessages().addAll(enricher.getMessages());
				if(enricher.getMessages().size()>0)toReturn.setMetaOperationResult(OperationState.WARN);
				
				
				GeoNetworkPublisher pub=getGN();
				getGN().login(level);
				Metadata enriched=enricher.getEnriched();
				toReturn.setPublishedMetadata(enriched);
				long returnedId=pub.insertMetadata(config,enriched);
				toReturn.setReturnedMetaId(returnedId);
				toReturn.setMetaOperationResult(OperationState.COMPLETE);
			}else {
				toReturn.getDataOperationMessages().add("Publish operation returned false, unable to publish data");
				
			}
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeProvider.instance.get());
		} catch (IllegalArgumentException e) {
			if(publisher==null){
				toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
				logger.debug("Unable to instatiate GeoServerRESTPublisher",e);
			}else {
				toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
				logger.debug("Unable to publish data",e);
			}
		} catch (MalformedURLException e) {
			toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
			logger.debug("Unable to instatiate GeoServerRESTPublisher",e);
		} catch (FileNotFoundException e) {
			toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
			logger.debug("Unable to publish data",e);
		} catch (Exception e) {
			// exceptions raised by publishing metadata, need to clean up
			toReturn.getMetaOperationMessages().add("Unable to publish metadata, cause :"+e.getMessage());
			logger.debug("Unable to publish metadata",e);
			DeleteReport delRep=deleteLayer(workspace,fte.getName(),null,desc,level);
			if(!delRep.getDataOperationResult().equals(OperationState.COMPLETE)){
				toReturn.setDataOperationResult(OperationState.WARN);
				toReturn.getDataOperationMessages().add("Unable to rollback data publishing, following messages from delete operation (state : "+delRep.getDataOperationResult()+")");
				toReturn.getDataOperationMessages().addAll(delRep.getDataOperationMessages());				
			}
		}		
		return toReturn;
	}
	
	
	public PublishResponse publishStyle(String sldBody,String styleName){
		boolean publishResult = false;
		PublishResponse toReturn=new PublishResponse();
		GeoServerRESTPublisher publisher=null;
		GeoServerDescriptor desc=getGeoServerSet(false).last();
		logger.debug("Using "+desc);
		try{
			publisher=getGeoServerManager(desc.getUrl(), desc.getUser(), desc.getPassword()).getPublisher();
			// Publishing the file to geoserver depends on file type
			publishResult=publisher.publishStyle(sldBody, styleName);
			
			if(publishResult){
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);
			}else toReturn.getDataOperationMessages().add("Publish operation returned false, unable to publish data");
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeProvider.instance.get());
		} catch (IllegalArgumentException e) {
			if(publisher==null){
				toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
				logger.debug("Unable to instatiate GeoServerRESTPublisher",e);
			}else {
				toReturn.getDataOperationMessages().add("Unable to publish data, cause :"+e.getMessage());
				logger.debug("Unable to publish data",e);
			}
		} catch (MalformedURLException e) {
			toReturn.getDataOperationMessages().add("Unable to instatiate GeoServerRESTPublisher, cause :"+e.getMessage());
			logger.debug("Unable to instatiate GeoServerRESTPublisher",e);
		} catch (Exception e) {
			// exceptions raised by publishing metadata, need to clean up
			toReturn.getMetaOperationMessages().add("Unable to publish metadata, cause :"+e.getMessage());
			logger.debug("Unable to publish metadata",e);
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
	 * @see it.geosolutions.geoserver.rest.GeoServerRESTPublisher#removeDatastore(String, String, boolean)
	 * 
	 */
	public DeleteReport deleteStore(String workspace,String storeName,Long metadataUUID,GeoServerDescriptor desc){
		DeleteReport toReturn=new DeleteReport();
		GeoServerRESTPublisher publisher=null;
		try{			
			publisher=getGeoServerManager(desc.getUrl(), desc.getUser(), desc.getPassword()).getPublisher();
			boolean removed=publisher.removeDatastore(workspace, storeName,true);
			if(removed){				
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);
				if(metadataUUID!=null){
					getGN().deleteMetadata(metadataUUID);
				}else {
					toReturn.setMetaOperationResult(OperationState.WARN);
					toReturn.getMetaOperationMessages().add("Passed meta UUID is null, no metadata deleted");
				}
			}else toReturn.getDataOperationMessages().add("Remove data operation returned false, unable to delete Store");
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeProvider.instance.get());
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
	
	public DeleteReport deleteLayer(String workspace,String layerName, Long metadataUUID,GeoServerDescriptor desc,LoginLevel gnLoginLevel){
		DeleteReport toReturn=new DeleteReport();
		GeoServerRESTPublisher publisher=null;
		try{
			publisher=getGeoServerManager(desc.getUrl(), desc.getUser(), desc.getPassword()).getPublisher();
			boolean removed=publisher.removeLayer(workspace, layerName);
			if(removed){				
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
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeProvider.instance.get());
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
	
	public DeleteReport deleteStyle(String styleName,GeoServerDescriptor desc){
		DeleteReport toReturn=new DeleteReport();
		GeoServerRESTPublisher publisher=null;
		try{
			publisher=getGeoServerManager(desc.getUrl(), desc.getUser(), desc.getPassword()).getPublisher();
			boolean removed=publisher.removeStyle(styleName, true);
			if(removed){				
				toReturn.setDataOperationResult(Report.OperationState.COMPLETE);
			}else toReturn.getDataOperationMessages().add("Remove data operation returned false, unable to delete Store");
		}catch(NoSuchElementException e){
			toReturn.getDataOperationMessages().add("No GeoServer Found under scope "+ScopeProvider.instance.get());
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
	
	//************ PRIVATE
	
	private GeoNetworkPublisher geoNetwork=null;
	private ConcurrentSkipListSet<GeoServerDescriptor> geoservers=new ConcurrentSkipListSet<GeoServerDescriptor>();
	private long lastAccessedTime=0l;
	
	private synchronized SortedSet<GeoServerDescriptor> getGeoServerSet(boolean forceRefresh){
		if(forceRefresh||geoservers.size()==0||System.currentTimeMillis()-lastAccessedTime>MAX_GEOSERVER_CACHE_TIME){
			geoservers.clear();
			geoservers.addAll(InfrastructureCrawler.queryforGeoServer());
			lastAccessedTime=System.currentTimeMillis();
		}
		return geoservers;
	}
	
	private synchronized GeoNetworkPublisher getGN() throws Exception{
		if(geoNetwork==null) {
			geoNetwork=GeoNetwork.get();
			if(toRegisterXMLAdapters!=null)
				for(XMLAdapter adapter:toRegisterXMLAdapters)
					geoNetwork.registerXMLAdapter(adapter);
		}
		return geoNetwork;
	}	
	
	private GeoServerRESTManager getGeoServerManager(String url,String user,String password) throws IllegalArgumentException, MalformedURLException{
		return new GeoServerRESTManager(new URL(url), user, password);
	}
	
	
	
	
	
}
