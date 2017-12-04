package org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils;

import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.ClientResource;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientResourceType;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchRequest.Config;
import it.geosolutions.geonetwork.util.GNSearchRequest.Param;
import it.geosolutions.geonetwork.util.GNSearchResponse;


public class ModelTranslation {

	private static final Logger logger = LoggerFactory.getLogger(ModelTranslation.class);
	
	
	public static final ClientResource toClient(Resource res){
		ClientResource toReturn=new ClientResource();
		toReturn.setAlgorithm(AlgorithmType.valueOf(res.getAlgorithm()+""));
		toReturn.setAuthor(res.getAuthor());
		toReturn.setDescription(res.getDescription());
		toReturn.setDisclaimer(res.getDisclaimer());
		
		toReturn.setHcafIds(CSVUtils.listToCSV(res.getSourceHCAFIds()));
		toReturn.setHcafNames(CSVUtils.listToCSV(res.getSourceHCAFTables()));
		
		toReturn.setHspenIds(CSVUtils.listToCSV(res.getSourceHSPENIds()));
		toReturn.setHspenNames(CSVUtils.listToCSV(res.getSourceHSPENTables()));
		
		toReturn.setHspecIds(CSVUtils.listToCSV(res.getSourceHSPECIds()));
		toReturn.setHspecNames(CSVUtils.listToCSV(res.getSourceHSPECTables()));
		
		toReturn.setOccurrIds(CSVUtils.listToCSV(res.getSourceOccurrenceCellsIds()));
		toReturn.setOccurNames(CSVUtils.listToCSV(res.getSourceOccurrenceCellsTables()));
		//
		toReturn.setIsDefault(res.getDefaultSource());
		try{
			toReturn.setParameters(Field.toJSONArray(res.getParameters()).toString());
		}catch(JSONException e){
			logger.error("Unable to parse resource parameters, resource is  "+res, e);
		}
		toReturn.setProvenance(res.getProvenance());
		toReturn.setRowCount(res.getRowCount());
		toReturn.setSearchId(res.getSearchId());
		toReturn.setStatus(res.getStatus()+"");
		toReturn.setTableName(res.getTableName());
		toReturn.setTime(new Time(res.getGenerationTime()));
		toReturn.setTitle(res.getTitle());
		toReturn.setType(ClientResourceType.valueOf(res.getType()+""));
		return toReturn;
	}

	public static final Resource toServer(ClientResource res){
		Resource toReturn=new Resource(ResourceType.valueOf(res.getType()+""), res.getSearchId());
		toReturn.setAlgorithm(org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType.valueOf(res.getAlgorithm()+""));
		toReturn.setAuthor(res.getAuthor());
		toReturn.setDefaultSource(res.getIsDefault());
		toReturn.setDescription(res.getDescription());
		toReturn.setDisclaimer(res.getDisclaimer());
		toReturn.setGenerationTime(res.getTime().getTime());
		try {
			toReturn.setParameters(Field.fromJSONArray(new JSONArray(res.getParameters())));
		} catch (JSONException e) {
			logger.error("Unable to parse resource parameters, client resource is "+res, e);
		}
		toReturn.setProvenance(res.getProvenance());
		toReturn.setRowCount(res.getRowCount());
		
		toReturn.setSourceHCAFIds(CSVUtils.CSVTOIntegerList(res.getHcafIds()));
		toReturn.setSourceHCAFTables(CSVUtils.CSVToStringList(res.getHcafNames()));
		toReturn.setSourceHSPENIds(CSVUtils.CSVTOIntegerList(res.getHspenIds()));
		toReturn.setSourceHSPENTables(CSVUtils.CSVToStringList(res.getHspenNames()));
		toReturn.setSourceHSPECIds(CSVUtils.CSVTOIntegerList(res.getHspecIds()));
		toReturn.setSourceHSPECTables(CSVUtils.CSVToStringList(res.getHspecNames()));
		toReturn.setSourceOccurrenceCellsIds(CSVUtils.CSVTOIntegerList(res.getOccurrIds()));
		toReturn.setSourceOccurrenceCellsTables(CSVUtils.CSVToStringList(res.getOccurNames()));
		toReturn.setStatus(ResourceStatus.valueOf(res.getStatus()));
		toReturn.setTableName(res.getTableName());
		toReturn.setTitle(res.getTitle());
		
		return toReturn;
	}
	public static CompoundMapItem toClient(AquaMap map){
		CompoundMapItem toReturn=new CompoundMapItem();
		String thumbnail=null;
		try{toReturn.setAlgorithm(map.getResource().getAlgorithm()+"");}catch(Exception e){}
		try{toReturn.setAuthor(map.getAuthor());}catch(Exception e){}
		try{toReturn.setCoverage(map.getCoverage());}catch(Exception e){}
		try{toReturn.setCreationDate(map.getCreationDate());}catch(Exception e){}
		try{toReturn.setDataGenerationTime(map.getResource().getGenerationTime());}catch(Exception e){}
		try{toReturn.setFileSetId(map.getFileSetId());}catch(Exception e){}
		try{toReturn.setGis(map.isGis());}catch(Exception e){}
		try{toReturn.setCustom(map.isCustom());}catch(Exception e){}
		try{toReturn.setImageCount(map.getFiles().size());}catch(Exception e){}
		
		try{
			List<String> urls=new ArrayList<String>();
			for(File f:map.getFiles()){
				urls.add(f.getUuri());
				if(f.getName().equals("Earth"))thumbnail=f.getUuri();
			}
		toReturn.setImageList(CSVUtils.listToCSV(urls));
		}catch(Exception e){}
		
		try{
			if(thumbnail==null){
				if(map.getFiles().size()>0) thumbnail=map.getFiles().get(0).getUuri();
				else thumbnail="";
			}
			toReturn.setImageThumbNail(thumbnail);
		}catch(Exception e){}
			
//		try{toReturn.setLayerId(map.getLayerId());}catch(Exception e){}
		try{
			LayerInfo layer=map.getLayer();
			String layerID=getUUIDbyGSId(layer.getName());
			String layerUrl=getPublicLink(layerID);
			toReturn.setLayerUrl(layerUrl);
			toReturn.setLayerId(layerID);
//			http://geoserver.d4science-ii.research-infrastructures.eu/geoserver/wms?" +
////				"service=WMS&version=1.1.0&request=GetMap&layers=TrueMarble.16km.2700x1350," +
////				"aquamaps:depthMean&styles=&bbox=-180.0,-85.5,180.0,90.0&width=676&height=330&srs=EPSG:4326&format=image%2Fgif
			
			URL url=new URL(layer.getUrl());
			
			String previewUrl="http://"+url.getHost()+"/geoserver/wms?service=WMS&version=1.1.0&request=GetMap&layers=aquamaps:TrueMarble.16km.2700x1350," +
				"aquamaps:"+layer.getName()+"&styles=&bbox=-180.0,-85.5,180.0,90.0&width=676&height=330&srs=EPSG:4326&format=image%2Fgif";
			toReturn.setLayerPreview(previewUrl);
			}catch(Exception e){}
		try{toReturn.setResourceId(map.getResource().getSearchId());}catch(Exception e){}
		try{toReturn.setSpeciesList(map.getSpeciesCsvList());}catch(Exception e){}
		try{toReturn.setTitle(map.getTitle());}catch(Exception e){}
		try{toReturn.setType(map.getMapType()+"");}catch(Exception e){}		
		return toReturn;
	}
	
	
	private static String getPublicLink(String uuid) throws UriResolverMapException, IllegalArgumentException{
		UriResolverManager resolver = new UriResolverManager("GIS");
		
		
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("gis-UUID", uuid);
		params.put("scope", ScopeProvider.instance.get());
		return resolver.getLink(params, true);
	}
	
	
	private static String getUUIDbyGSId(String gsID) throws Exception{
		GeoNetworkReader reader=GeoNetwork.get();
		reader.login(LoginLevel.ADMIN);


		GNSearchRequest req=new GNSearchRequest();
		req.addParam(Param.any, gsID);
		req.addConfig(Config.similarity, "1");
		GNSearchResponse resp=reader.query(req);		
		return resp.getMetadata(0).getUUID();
	}
}
