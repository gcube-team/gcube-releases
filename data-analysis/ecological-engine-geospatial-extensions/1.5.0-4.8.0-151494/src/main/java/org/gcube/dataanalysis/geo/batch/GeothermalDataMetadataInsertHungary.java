package org.gcube.dataanalysis.geo.batch;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class GeothermalDataMetadataInsertHungary {
	
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
		//"HeatFlowLine",
		"Temperature at 1 km depth, Hungary",
		"Temperature at 2 km depth, Hungary",
		"Temperature at 3 km depth, Hungary",
		"Surface heat flow, Hungary"
		};
	static String[] abstracts = {
		//"The class HeatFlowLine presents a heat flow map. HeatFlowLine is qualified by isolines, featured as discrete heat flow values regularly spaced.",
		"Map of temperature at 1 km depth (below ground level), Hungary",
		"Map of temperature at 2 km depth (below ground level), Hungary",
		"Map of temperature at 3 km depth (below ground level), Hungary",
		"Geothermal surface heat flow, Hungary"
	};
	static String[] customTopics = {
		//"",
		"geothermal energy, map, Hungary, Energy resources, EGIP, MFGI",
		"geothermal energy, map, Hungary, Energy resources, EGIP, MFGI",
		"geothermal energy, map, Hungary, Energy resources, EGIP, MFGI",
		"geothermal energy, map, Hungary, Energy resources, EGIP, MFGI",
		"geothermal energy, map, Hungary, Energy resources, EGIP, MFGI",
		};
	static String[] categoryTypes = {
		//"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_", 
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		};
	static String[] layernames = {
		//"HeatFlowUnit",
		"temp1000",
		"temp2000",
		"temp3000",
		"heatflow"
	};
	static String[] wmsurls= {
		//"http://egip.brgm-rec.fr/wxs/?service=WMS&layers=HeatFlowUnit",
		"http://geonetwork.mfgi.hu:8080/geoserver/egip/ows?service=WMS&version=1.1.1&bbox=16.18993,45.71316,22.93481,48.54140&request=GetMap&layers=temp1000&styles=&width=512&height=453&srs=EPSG:4326&format=application/openlayers",
		"http://geonetwork.mfgi.hu:8080/geoserver/egip/ows?service=WMS&version=1.1.1&bbox=16.18993,45.71316,22.93481,48.54140&request=GetMap&layers=temp2000&styles=&width=512&height=453&srs=EPSG:4326&format=application/openlayers",
		"http://geonetwork.mfgi.hu:8080/geoserver/egip/ows?service=WMS&version=1.1.1&bbox=16.18993,45.71316,22.93481,48.54140&request=GetMap&layers=temp3000&styles=&width=512&height=453&srs=EPSG:4326&format=application/openlayers",
		"http://geonetwork.mfgi.hu:8080/geoserver/egip/ows?service=WMS&version=1.1.1&bbox=16.18993,45.71316,22.93481,48.54140&request=GetMap&layers=heatflow&styles=&width=512&height=453&srs=EPSG:4326&format=application/openlayers",
		};
	static String[] wfsurls= {
		//"http://egip.brgm-rec.fr/wxs/?service=WFS&version=1.1.0&request=GetFeature&typeName=HeatFlowUnit&srsName=EPSG:4326",
		"http://geonetwork.mfgi.hu:8080/geoserver/egip/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=temp1000",
		"http://geonetwork.mfgi.hu:8080/geoserver/egip/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=temp2000",
		"http://geonetwork.mfgi.hu:8080/geoserver/egip/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=temp3000",
		"http://geonetwork.mfgi.hu:8080/geoserver/egip/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=heatflow"
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
