package org.gcube.dataanalysis.geo.batch;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class GeothermalDataMetadataInsertItaly {
	
	static String geonetworkurl = "http://geonetwork.geothermaldata.d4science.org/geonetwork";
	static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
	static String user = "admin";
	static String password = "d4science2014";
	
	public static void main(String[] args) throws Exception{
		
		for (int i=0;i<titles.length;i++){
			insertLayer(i);
		}
	}
	
	static String[] titles = {
		"HeatFlowUnit",
		"HeatFlowLine",
		"TemperatureLine3km",
		"TemperatureLine2km",
		"TemperatureUnit1km",
		"TemperatureUnit3km",
		"TemperatureUnit2km",
		"TemperatureLine1km",
		"Industry",
		"TrainingCenter",
		"Licences",
		};
	static String[] abstracts = {
		"Surface Heat Flow Map of Italy",
		"Surface Heat Flow Contour Map of Italy",
		"Temperature Isoline at 3 km depth (below ground level) of Italy",
		"Temperature Isoline at 2 km depth (below ground level) of Italy",
		"Temperature map at 1km depth (below ground level) of Italy",
		"The temperature map at 3 km depth of Italy has been obtained digitizing the map from scientific paper Cataldi et al. 1995",
		"Temperature map at 2km depth (below ground level) of Italy",
		"Temperature Isoline at 1 km depth",
		"Industries involved in geothermal activities refer to all companies that produce components both for power production and the direct use of heat",
		"List of education and research centres with geothermal courses and lectures: The list is in a table format and includes the type, the name, the location and the URL",
		"Exploration and production licenses and (projected) power production",
		};
	static String[] customTopics = {
		"geothermal energy, map, Italy, Energy resources, EGIP, IRENA, CNR, IGG",
		"geothermal energy, map, Italy, Energy resources, EGIP, IRENA, CNR, IGG",
		"geothermal energy, map, Italy, Energy resources, EGIP, IRENA, CNR, IGG",
		"geothermal energy, map, Italy, Energy resources, EGIP, IRENA, CNR, IGG",
		"geothermal energy, map, Italy, Energy resources, EGIP, IRENA, CNR, IGG",
		"geothermal energy, map, Italy, Energy resources, EGIP, IRENA, CNR, IGG",
		"geothermal energy, map, Italy, Energy resources, EGIP, IRENA, CNR, IGG",
		"geothermal energy, map, Italy, Energy resources, EGIP, IRENA, CNR, IGG",
		"geothermal energy, map, Italy, Energy resources, EGIP, IGG",
		"geothermal energy, map, Italy, Energy resources, EGIP, IGG",
		"geothermal energy, map, Italy, Energy resources, resources management, land management and planning, EGIP, IGG",
		
		};
	static String[] categoryTypes = {
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_",
		"_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_",
		"_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_",
		};
	static String[] layernames = {
		"EGIP:HeatFlowUnit",
		"EGIP:HeatFlowLine",
		"EGIP:TemperatureLine3km",
		"EGIP:TemperatureLine2km",
		"EGIP:TemperatureUnit1km",
		"EGIP:TemperatureUnit3km",
		"EGIP:TemperatureUnit2km",
		"EGIP:TemperatureLine1km",
		"EGIP:Industry",
		"EGIP:TrainingCenter",
		"EGIP:licence",
	};
	static String[] wmsurls= {
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:HeatFlowUnit&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:HeatFlowLine&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:TemperatureLine3km&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:TemperatureLine2km&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:TemperatureUnit1km&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:TemperatureUnit3km&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:TemperatureUnit2km&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:TemperatureLine1km&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:Industry&styles=&bbox=9.189578001171471,41.909917999980756,12.480876999984194,45.52478199898418&width=512&height=451&srs=EPSG:4326&format=application/openlayers",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:TrainingCenter&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/wms?service=WMS&version=1.1.1&request=GetMap&layers=EGIP:licence&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
		};
	static String[] wfsurls= {
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:HeatFlowUnit",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:HeatFlowLine",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:TemperatureLine3km",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:TemperatureLine2km",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:TemperatureUnit1km",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:TemperatureUnit3km",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:TemperatureUnit2km",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:TemperatureLine1km",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:Industry",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:TrainingCenter",
		"http://repoigg.services.iit.cnr.it/geoserver/EGIP/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=EGIP:licence"
		};
	
	private static void insertLayer(int i) throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(-180);
		metadataInserter.setYLeftLow(-90);
		metadataInserter.setXRightUpper(180);
		metadataInserter.setYRightUpper(90);
		
		metadataInserter.setTitle(titles[i]);
		metadataInserter.setAbstractField(abstracts[i]);
		metadataInserter.setCustomTopics(customTopics[i].split(","));
		metadataInserter.setCategoryTypes(categoryTypes[i]);
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName(layernames[i]);
		
		String [] urls = {
				wmsurls[i],
				wfsurls[i]
		};
		String [] protocols = {"WMS","WFS"};
		
		if (titles[i].length()>0)
			metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
}
