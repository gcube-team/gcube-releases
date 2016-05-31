package org.gcube.dataanalysis.geo.batch;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class GeothermalDataMetadataInsertSlovenia {
	
	//static String geonetworkurl = "http://geonetwork.geothermaldata.d4science.org/geonetwork";
	static String geonetworkurl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork";
	static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
	static String user = "admin";
	//static String password = "d4science2014";
	static String password = "admin";
	public static void main(String[] args) throws Exception{
		
		for (int i=0;i<titles.length;i++){
			insertLayer(i);
		}
	}
	
	static String[] titles = {
		"Geothermal map - Temperature lines at 2000 m depth - Test",
		};
	static String[] abstracts = {
		"The underground geothermal conditions can be presented, irrespective of the aquifers' position, with the appropriate geothermal maps. This map represents the expected isotherms at a depth of 2000 m and is derived from Geothermal map - Expected temperatures at a depth of 2000 m, which is made with data from 240 boreholes. It is made on the basis of measured temperatures in accessible boreholes throughout the country. However, since the temperature field depends on the geological structure in the depths and tectonic characteristics, the course of the isotherms is a result of many influences, such as thermal conductivity of rocks, permeability and fracturing of rocks, all of which are reflected in the measured temperatures in boreholes. In this depth also a radiogenic heat production in the rocks has smaller influence. The distribution of boreholes, which were useful for the measurement of temperature, is very uneven and different as regard the depths. Following the expected temperatures at a depth of 2000 m a stronger positive anomaly is in the northeastern part of Slovenia, from the line Maribor-Rogatec to the east, while in the eastern part of the Krka basin the anomaly is not so much visible any more. In the northeastern part of the country the anomaly is the result of the thinning of the Earth's crust and greater conductive heat flow from the Earth's mantle.",
	};
	static String[] customTopics = {
		"Slovenia,EGIP, Geothermal map - Temperature lines at 2000 m depth",
		};
	static String[] categoryTypes = {
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_", 
		};
	static String[] layernames = {
		"Temperature line Lines at 2000m depth",
	};
	static String[] wmsurls= {
		"http://biotit.geo-zs.si/gis/services/EGIP/EGIP_geothermal_map/MapServer/WMSServer?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&LAYERS=Temperature%20line%20Lines%20at%202000m%20depth&SRS=EPSG%3A4326&BBOX=13.275317,45.321849,16.710686,46.976674&WIDTH=800&HEIGHT=400&FORMAT=image/png&STYLES="
		};
	static String[] wfsurls= {
		"http://biotit.geo-zs.si/gis/services/EGIP/EGIP_geothermal_map/MapServer/WFSServer?SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=EGIP_EGIP_geothermal_map%3ATemperature_line_Lines_at_2000m_depth"
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
