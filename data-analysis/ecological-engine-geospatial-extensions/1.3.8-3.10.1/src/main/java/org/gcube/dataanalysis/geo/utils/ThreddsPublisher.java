package org.gcube.dataanalysis.geo.utils;

import java.io.File;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.util.DataTransferer;
import org.gcube.dataanalysis.executor.util.InfraRetrieval;
import org.gcube.dataanalysis.geo.connectors.netcdf.NetCDFDataExplorer;
import org.gcube.dataanalysis.geo.infrastructure.GeoNetworkInspector;
import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.dataanalysis.geo.meta.OGCFormatter;
import org.opengis.metadata.identification.TopicCategory;

import ucar.nc2.dt.GridDatatype;

public class ThreddsPublisher {
	
	public static void main (String[] args) throws Exception{
//		String scope = "/d4science.research-infrastructures.eu/gCubeApps";
		String scope = "/gcube/devsec";
		String username = "gianpaolo.coro";
		//String fileAbsolutePath = "C:/Users/coro/Dropbox/Public/wind1.tif";
		String fileAbsolutePath = "C:/Users/coro/Downloads/adux_pres_portale_test.nc";
		
		String layerTitle = "architeuthis dux distribution file - test";
		String layerName = "adux_pres_2";
		String abstractField = "abstract architeuthis dux distribution file - test";
		String[] topics = {"adux","D4Science"};
		double resolution = -1;
		AnalysisLogger.setLogger("./cfg/"+AlgorithmConfiguration.defaultLoggerFile);
		publishOnThredds(scope, username, fileAbsolutePath, layerTitle, layerName, abstractField, topics, resolution);
	}
	
	public static boolean publishOnThredds(String scope,String username, String fileAbsolutePath, String layerTitle, String layerName, String abstractField, String[] topics, double resolution) throws Exception{
			//TODO manage faults
			String remoteFolder = "/data/content/thredds/public/netcdf/";
			List<String> threddsAddress = InfraRetrieval.retrieveServiceAddress("Gis", "THREDDS", scope, "Geoserver");
			if (threddsAddress.size()==0) 
				threddsAddress = InfraRetrieval.retrieveServiceAddress("Gis", "Thredds", scope, "Geoserver");
			
			if (threddsAddress.size()==0)
				throw new Exception("Thredds resource is not available in scope "+scope);
			
			String threddServiceAddress = threddsAddress.get(0);
			threddServiceAddress = threddServiceAddress.substring(threddServiceAddress.indexOf("http://")+7);
			threddServiceAddress = threddServiceAddress.substring(0,threddServiceAddress.indexOf("/"));
			
			AnalysisLogger.getLogger().debug("Found "+threddsAddress.size()+" thredds services");
			AnalysisLogger.getLogger().debug("THREDDS: "+threddServiceAddress);
			List<String> dataTransferAddress = InfraRetrieval.retrieveService("agent-service", scope);
			
			if (dataTransferAddress.size()==0)
				throw new Exception("Data Transfer services are not available in scope "+scope);
			
			AnalysisLogger.getLogger().debug("Found "+dataTransferAddress.size()+" transfer services");
			String threddsDTService = threddServiceAddress;
			int threddsDTPort = 9090; 
			boolean found = false;
			for (String datatransferservice:dataTransferAddress){
				AnalysisLogger.getLogger().debug("Transfer service found");
				datatransferservice = datatransferservice.substring(datatransferservice.indexOf("http://")+7);
				String servicehost = datatransferservice.substring(0,datatransferservice.indexOf(":"));
				String serviceport = datatransferservice.substring(datatransferservice.indexOf(":")+1,datatransferservice.indexOf("/"));
				AnalysisLogger.getLogger().debug("Transfer service: "+servicehost+":"+serviceport);
				if (threddServiceAddress.equals(servicehost)){
					threddsDTPort = Integer.parseInt(serviceport);
					found = true;
					break;
				}
			}
			
			if (!found)
				throw new Exception("Thredds data transfer has not been found in the same scope of the catalog: "+scope);
			boolean gridded=true;
			if (fileAbsolutePath.endsWith(".nc")){
				AnalysisLogger.getLogger().debug("checking NetCDF file coherence"+fileAbsolutePath);
				//let's publish also if the netCDF is not gridded
				try{
					NetCDFDataExplorer.getGrid(layerName, fileAbsolutePath);
				}catch(Exception e){
					gridded=false;
					AnalysisLogger.getLogger().debug("NetCDF is not gridded"+fileAbsolutePath);
				}
			}
			AnalysisLogger.getLogger().debug("Transferring via DT to "+threddServiceAddress);
			DataTransferer.transferFileToService(scope, username, threddsDTService, threddsDTPort, fileAbsolutePath, remoteFolder);
			
			AnalysisLogger.getLogger().debug("Adding metadata on GeoNetwork");
			
			if (fileAbsolutePath.endsWith(".nc") && gridded)
				publishNetCDFMeta(scope, layerTitle, abstractField, new File(fileAbsolutePath).getName(),layerName,threddServiceAddress,username,topics);
			else{
				if (resolution==-1 && gridded)
					throw new Exception ("Specify valid resolution parameter for non-NetCDF raster datasets");
				publishOtherFileMeta(scope, layerTitle, resolution, abstractField, new File(fileAbsolutePath).getName(), threddServiceAddress,username,topics);
			}
			
			
			AnalysisLogger.getLogger().debug("Finished");
			return true;
	}
	
	private static void publishOtherFileMeta(String scope, String layerTitle, double resolution, String abstractField, String filename, String threddsURL, String username, String [] topics) throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		
		GeoNetworkInspector gninspector =new GeoNetworkInspector();
		gninspector.setScope(scope);
		String geonetworkURL = gninspector.getGeonetworkURLFromScope();
		String geonetworkUser = gninspector.getGeonetworkUserFromScope();
		String geonetworkPassword = gninspector.getGeonetworkPasswordFromScope();
		
		AnalysisLogger.getLogger().debug("GeoNetwork Info: "+geonetworkURL+" "+geonetworkUser);
		
		metadataInserter.setGeonetworkUrl(geonetworkURL);
		metadataInserter.setGeonetworkPwd(geonetworkPassword);
		metadataInserter.setGeonetworkUser(geonetworkUser);
		
		metadataInserter.setTitle(layerTitle);
		metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
		
		metadataInserter.setAbstractField(abstractField+" Hosted on the D4Science Thredds Catalog: "+threddsURL);
		metadataInserter.setCustomTopics(topics);
		metadataInserter.setAuthor(username);
		
		metadataInserter.setResolution(resolution);
		
		AnalysisLogger.getLogger().debug("Res:"+resolution);
		
		String [] urls = {"http://"+threddsURL+"/thredds/fileServer/public/netcdf/"+filename};
		
		String [] protocols = {"HTTP"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	
		private static void publishNetCDFMeta(String scope, String layerTitle,String abstractField, String filename, String netCDFLayerName, String threddsURL, String username, String [] topics) throws Exception{
			AnalysisLogger.getLogger().debug("Getting GeoNetwork Info");
			
			GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
			GeoNetworkInspector gninspector =new GeoNetworkInspector();
			gninspector.setScope(scope);
			String geonetworkURL = gninspector.getGeonetworkURLFromScope();
			String geonetworkUser = gninspector.getGeonetworkUserFromScope();
			String geonetworkPassword = gninspector.getGeonetworkPasswordFromScope();
			
			AnalysisLogger.getLogger().debug("GeoNetwork Info: "+geonetworkURL+" "+geonetworkUser);
			
			metadataInserter.setGeonetworkUrl(geonetworkURL);
			metadataInserter.setGeonetworkPwd(geonetworkPassword);
			metadataInserter.setGeonetworkUser(geonetworkUser);
			
			metadataInserter.setTitle(layerTitle);
			metadataInserter.setCategoryTypes("_"+TopicCategory.ENVIRONMENT.name()+"_");
			
			metadataInserter.setAbstractField(abstractField+" Hosted on the D4Science Thredds Catalog: "+threddsURL);
			metadataInserter.setCustomTopics(topics);
			metadataInserter.setAuthor(username);
			String Threddscatalog = "http://"+threddsURL+"/thredds/catalog/public/netcdf/catalog.xml";
			String url = OGCFormatter.getOpenDapURL(Threddscatalog, filename);
			AnalysisLogger.getLogger().debug("OpenDAP URL: "+url);
			
			GridDatatype gdt = NetCDFDataExplorer.getGrid(netCDFLayerName, url);
			
			double minX = NetCDFDataExplorer.getMinX(gdt.getCoordinateSystem());
			double maxX = NetCDFDataExplorer.getMaxX(gdt.getCoordinateSystem());
			double minY = NetCDFDataExplorer.getMinY(gdt.getCoordinateSystem());
			double maxY = NetCDFDataExplorer.getMaxY(gdt.getCoordinateSystem());
			
			double resolutionY = NetCDFDataExplorer.getResolution(netCDFLayerName,url);
			
			metadataInserter.setResolution(resolutionY);
			
			AnalysisLogger.getLogger().debug("minX: "+minX+" minY: "+minY+" maxX:"+maxX+" maxY:"+maxY+" Res:"+resolutionY);
			
			String wms = OGCFormatter.getWmsNetCDFUrl(url, netCDFLayerName, OGCFormatter.buildBoundingBox(minX, minY, maxX, maxY)).replace("width=676", "width=640").replace("height=330", "height=480");
			
			AnalysisLogger.getLogger().debug("WMS URL: "+wms);
			String wcs = OGCFormatter.getWcsNetCDFUrl(url, netCDFLayerName, OGCFormatter.buildBoundingBox(minX, minY, maxX, maxY)).replace("width=676", "width=640").replace("height=330", "height=480");
			AnalysisLogger.getLogger().debug("WCS URL: "+wcs);
			AnalysisLogger.getLogger().debug("HTTP URL: "+"http://"+threddsURL+"/thredds/fileServer/public/netcdf/"+filename);
			String [] urls = {"http://"+threddsURL+"/thredds/fileServer/public/netcdf/"+filename,wms,wcs,url};
			
			String [] protocols = {"HTTP","WMS","WCS","OPeNDAP"};
			
			metadataInserter.setXLeftLow(minX);
			metadataInserter.setYLeftLow(minY);
			metadataInserter.setXRightUpper(maxX);
			metadataInserter.setYRightUpper(maxY);
			
			AnalysisLogger.getLogger().debug("Inserting metadata ");
			metadataInserter.customMetaDataInsert(urls,protocols);
		}
	
	
}
