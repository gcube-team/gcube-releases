package org.gcube.dataanalysis.geo.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.transfer.library.TransferResult;
import org.gcube.dataanalysis.executor.util.DataTransferer;
import org.gcube.dataanalysis.geo.connectors.netcdf.NetCDFDataExplorer;
import org.gcube.dataanalysis.geo.infrastructure.GeoNetworkInspector;
import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.dataanalysis.geo.meta.OGCFormatter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.opengis.metadata.identification.TopicCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.nc2.dt.GridDatatype;

public class ThreddsPublisher {
	
	private static final String threddsServiceName = "Thredds";
	private static final String threddsServiceClass = "SDI";
	
	private static final String dataTransferName = "data-transfer-service";
	private static final String dataTransferClass = "DataTransfer";
	private static final String dataTransferEndpoint = "org.gcube.data.transfer.service.DTService";
	
	private static final String threddsPersistenceID = "thredds";
	private static final String threddsRemoteFolder = "/public/netcdf";
	
	private static final String threddsFileServerPath = "/thredds/fileServer/public/netcdf/";
	private static final String threddsCatalogPath = "/thredds/catalog/public/netcdf/catalog.xml";
	
	private static final String netCDFExtension = ".nc";
	
	private static final Logger log = LoggerFactory.getLogger(ThreddsPublisher.class);
	
	public static boolean publishOnThredds(String username, String fileAbsolutePath, String layerTitle, String layerName, String abstractField, String[] topics, double resolution, boolean isprivate) throws Exception{
			//TODO manage faults
					
			DiscoveryClient<String> threddsClient = ICFactory.client();
			
			SimpleQuery thrredsQuery = ICFactory.queryFor(GCoreEndpoint.class);
			thrredsQuery.addCondition(String.format("$resource/Profile/ServiceName eq '%s'",threddsServiceName));
			thrredsQuery.addCondition(String.format("$resource/Profile/ServiceClass eq '%s'",threddsServiceClass));
			thrredsQuery.setResult("$resource/Profile/GHN/@UniqueID/string()");
			List<String> threddsAddress = threddsClient.submit(thrredsQuery);
			
			if (threddsAddress.size()==0)
				throw new Exception("Thredds Endpoint not found in scope "+ScopeProvider.instance.get());
			
			String threddsWhnId  = threddsAddress.get(0); 
			
			DiscoveryClient<String> dataTransferClient = ICFactory.client();
			
			SimpleQuery dataTransferQuery = ICFactory.queryFor(GCoreEndpoint.class);
			dataTransferQuery.addCondition(String.format("$resource/Profile/ServiceName/string() eq '%s'",dataTransferName));
			dataTransferQuery.addCondition(String.format("$resource/Profile/ServiceClass/string() eq '%s'",dataTransferClass));
			dataTransferQuery.addCondition(String.format("$resource/Profile/GHN/@UniqueID/string() eq '%s'",threddsWhnId));
			dataTransferQuery.setResult(String.format("$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint[@EntryName/string() eq \"%s\"]/string()",dataTransferEndpoint));
			
			List<String> dataTransferAddress = dataTransferClient.submit(dataTransferQuery);
					
			if (dataTransferAddress.size()==0)
				throw new Exception("Data Transfer services is not available in scope "+ScopeProvider.instance.get());
				
			
			String threddsDTService = dataTransferAddress.get(0);
			log.debug("data transfer found is {}",threddsDTService);
			
			Pattern pattern = Pattern.compile("(https?)://([^:/]*)(:(\\d{2,5}))?.*");
			Matcher matcher = pattern.matcher(threddsDTService);
			
			if (!matcher.find())
				throw new Exception("wrong address found "+threddsDTService);
			
			String dataTransferProtocol = matcher.group(1);
			String dataTransferHost = matcher.group(2);
			String portAsString = matcher.group(4);
			Integer dataTransferPort = portAsString==null?null:Integer.parseInt(portAsString);
			
			StringBuilder threedsBaseURL = new StringBuilder(dataTransferProtocol).append("://").append(dataTransferHost);
			if (portAsString!=null)
				threedsBaseURL.append(":").append(portAsString);
			
			boolean gridded=true;
			if (fileAbsolutePath.endsWith(".nc")){
				log.debug("checking NetCDF file coherence {}",fileAbsolutePath);
				//let's publish also if the netCDF is not gridded
				try{
					NetCDFDataExplorer.getGrid(layerName, fileAbsolutePath);
				}catch(Exception e){
					gridded=false;
					log.debug("NetCDF is not gridded {}",fileAbsolutePath);
				}
			}
			log.debug("Transferring via DT to {} with parameters {} {} {} {} ",threddsDTService, dataTransferHost, dataTransferPort, fileAbsolutePath, threddsRemoteFolder);
			TransferResult transferResult = DataTransferer.transferFileToService(ScopeProvider.instance.get(), username, dataTransferHost, dataTransferPort, fileAbsolutePath, threddsRemoteFolder, threddsPersistenceID);
			
			String realFileName = transferResult.getRemotePath().substring(transferResult.getRemotePath().lastIndexOf("/")+1);
			
			log.debug("Adding metadata on GeoNetwork, real file name on threadds is {} ",realFileName);
			
			if (fileAbsolutePath.endsWith(netCDFExtension) && gridded)
				publishNetCDFMeta(ScopeProvider.instance.get(), layerTitle, abstractField, realFileName,layerName,threedsBaseURL.toString(),username,topics,isprivate);
			else{
				if (resolution==-1 && gridded)
					throw new Exception ("Specify valid resolution parameter for non-NetCDF raster datasets");
				publishOtherFileMeta(ScopeProvider.instance.get(), layerTitle, resolution, abstractField, realFileName, threedsBaseURL.toString(),username,topics,isprivate);
			}
			
			
			log.debug("Finished");
			return true;
	}
	
	private static void publishOtherFileMeta(String scope, String layerTitle, double resolution, String abstractField, String filename, String threddsURL, String username, String [] topics, boolean isprivate) throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		
		GeoNetworkInspector gninspector =new GeoNetworkInspector();
		gninspector.setScope(scope);
		String geonetworkURL = gninspector.getGeonetworkURLFromScope();
		String geonetworkUser = gninspector.getGeonetworkUserFromScope();
		String geonetworkPassword = gninspector.getGeonetworkPasswordFromScope();
		String geonetworkGroup = "";
		if (isprivate)
			geonetworkGroup = gninspector.getGeonetworkPrivateGroup();
		else
			geonetworkGroup = gninspector.getGeonetworkPublicGroup();
				
		log.debug("GeoNetwork Info: "+geonetworkURL+" "+geonetworkUser+" "+geonetworkGroup);
		
		metadataInserter.setGeonetworkUrl(geonetworkURL);
		metadataInserter.setGeonetworkPwd(geonetworkPassword);
		metadataInserter.setGeonetworkUser(geonetworkUser);
		metadataInserter.setGeonetworkGroup(geonetworkGroup);
		
		metadataInserter.setTitle(layerTitle);
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		
		metadataInserter.setAbstractField(abstractField+" Hosted on the D4Science Thredds Catalog: "+threddsURL);
		metadataInserter.setCustomTopics(topics);
		metadataInserter.setAuthor(username);
		
		metadataInserter.setResolution(resolution);
		
		log.debug("Res:"+resolution);
		
		String [] urls = {threddsURL+threddsFileServerPath+filename};
		
		String [] protocols = {"HTTP"};
		
		log.debug("Publishing in group: "+metadataInserter.getGeonetworkGroup());
		log.debug("Inserting custom metadata ");
		metadataInserter.customMetaDataInsert(urls,protocols,isprivate);
	}
	
	
	private static void publishNetCDFMeta(String scope, String layerTitle,String abstractField, String filename, String netCDFLayerName, String threddsURL, String username, String [] topics, boolean isprivate) throws Exception{
			log.debug("Getting GeoNetwork Info");
			
			GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
			GeoNetworkInspector gninspector =new GeoNetworkInspector();
			gninspector.setScope(scope);
			String geonetworkURL = gninspector.getGeonetworkURLFromScope();
			String geonetworkUser = gninspector.getGeonetworkUserFromScope();
			String geonetworkPassword = gninspector.getGeonetworkPasswordFromScope();
			String geonetworkGroup = "";
			if (isprivate)
				geonetworkGroup = gninspector.getGeonetworkPrivateGroup();
			else
				geonetworkGroup = gninspector.getGeonetworkPublicGroup();
			
			log.debug("GeoNetwork Info: "+geonetworkURL+" "+geonetworkUser+" "+geonetworkGroup);
			
			metadataInserter.setGeonetworkUrl(geonetworkURL);
			metadataInserter.setGeonetworkPwd(geonetworkPassword);
			metadataInserter.setGeonetworkUser(geonetworkUser);
			metadataInserter.setGeonetworkGroup(geonetworkGroup);
			
			metadataInserter.setTitle(layerTitle);
			metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
			
			metadataInserter.setAbstractField(abstractField+" Hosted on the D4Science Thredds Catalog: "+threddsURL);
			metadataInserter.setCustomTopics(topics);
			metadataInserter.setAuthor(username);
			String threddscatalog = threddsURL+threddsCatalogPath;
			String url = OGCFormatter.getOpenDapURL(threddscatalog, filename);
			log.debug("OpenDAP URL: {} ",url);
			
			GridDatatype gdt = NetCDFDataExplorer.getGrid(netCDFLayerName, url);
			
			double minX = NetCDFDataExplorer.getMinX(gdt.getCoordinateSystem());
			double maxX = NetCDFDataExplorer.getMaxX(gdt.getCoordinateSystem());
			double minY = NetCDFDataExplorer.getMinY(gdt.getCoordinateSystem());
			double maxY = NetCDFDataExplorer.getMaxY(gdt.getCoordinateSystem());
			
			double resolutionY = NetCDFDataExplorer.getResolution(netCDFLayerName,url);
			
			metadataInserter.setResolution(resolutionY);
			
			log.debug("minX: "+minX+" minY: "+minY+" maxX:"+maxX+" maxY:"+maxY+" Res:"+resolutionY);
			
			String wms = OGCFormatter.getWmsNetCDFUrl(url, netCDFLayerName, OGCFormatter.buildBoundingBox(minX, minY, maxX, maxY)).replace("width=676", "width=640").replace("height=330", "height=480");
			
			log.debug("WMS URL: {}",wms);
			String wcs = OGCFormatter.getWcsNetCDFUrl(url, netCDFLayerName, OGCFormatter.buildBoundingBox(minX, minY, maxX, maxY)).replace("width=676", "width=640").replace("height=330", "height=480");
			log.debug("WCS URL: {}",wcs);
			String fileServerUrl = threddsURL+threddsFileServerPath+filename;
			log.debug("HTTP URL: {} ",fileServerUrl);
			String [] urls = {fileServerUrl,wms,wcs,url};
			
			String [] protocols = {"HTTP","WMS","WCS","OPeNDAP"};
			
			metadataInserter.setXLeftLow(minX);
			metadataInserter.setYLeftLow(minY);
			metadataInserter.setXRightUpper(maxX);
			metadataInserter.setYRightUpper(maxY);
			log.debug("Publishing in group: {} ",metadataInserter.getGeonetworkGroup());
			log.debug("Inserting metadata ");
			metadataInserter.customMetaDataInsert(urls,protocols,isprivate);
		}
	
	
}
