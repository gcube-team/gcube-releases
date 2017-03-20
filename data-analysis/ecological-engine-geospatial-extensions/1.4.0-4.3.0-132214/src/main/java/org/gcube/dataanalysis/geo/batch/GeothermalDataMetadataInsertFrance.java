package org.gcube.dataanalysis.geo.batch;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class GeothermalDataMetadataInsertFrance {
	
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
		"HeatFlowLine",
		"License",
		"TemperatureLine",
		"TemperatureUnit",
		"HeatFlowUnit",
		"Industry",
		"TrainingCenter",
		};
	static String[] abstracts = {
		"The class HeatFlowLine presents a heat flow map. HeatFlowLine is qualified by isolines, featured as discrete heat flow values regularly spaced. These data are based on measurements operated since 1970 and already processed.",
		"The class License was created to present a list of exploration and production permits. They are the official permits that allow an organism to start a study or a production on a defined area. License is qualified by the name and type of permit and its area and limits date. For any further information, a link was added.",
		"The class TemperatureLine presents a temperature map. TemperatureLine is qualified by isolines, featured as discrete temperature values regularly spaced. These temperature data come from drill holes measurements, mostly from petroleum wells between the years 1978 and 2007, and were processed in 2007.",
		"The class TemperatureUnit presents a temperature map. TemperatureUnit is qualified by polygons, featured as intervals between a lower and upper temperature value. These temperature data come from drill holes measurements, mostly from petroleum wells between the years 1978 and 2007, and were processed in 2007.",
		"The class HeatFlowUnit presents a heat flow map. HeatFlowUnit is qualified by polygons, featured as intervals between a lower and upper heat flow value. These data are based on measurements operated since 1970 and already processed.",
		"The class Industry was created to present a list of industries, which invest in geothermal energy or participate into geothermal projects. Industry is qualified by the name and type of the company and its location. For any further information, a link to the company's website was added.",
		"The class TrainingCenter was created to present a list of Education and research institutes, as well as training centers, which offer geothermal education. Education is qualified by the name and type of the institute and its location. For any further information, a link to the institute's website was added."
	};
	static String[] customTopics = {
		"EGIP, HeatFlowLine, France",
		"EGIP, License, permits, France",
		"EGIP, TemperatureLine, France",
		"EGIP, TemperatureUnit, France",
		"EGIP, HeatFlowUnit, France",
		"EGIP, Industry, France",
		"EGIP, TrainingCenter, Education, France"
		};
	static String[] categoryTypes = {
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_", 
		"_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_",
		"_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_"
		};
	static String[] layernames = {
		"HeatFlowLine",
		"License",
		"TemperatureLine",
		"TemperatureUnit",
		"HeatFlowUnit",
		"Industry",
		"TrainingCenter"
	};
	static String[] wmsurls= {
		"http://egip.brgm-rec.fr/wxs/?service=WMS&layers=HeatFlowLine",
		"http://egip.brgm-rec.fr/wxs/?service=WMS&layers=License",
		"http://egip.brgm-rec.fr/wxs/?service=WMS&layers=TemperatureLine",
		"http://egip.brgm-rec.fr/wxs/?service=WMS&layers=TemperatureUnit",
		"http://egip.brgm-rec.fr/wxs/?service=WMS&layers=HeatFlowUnit",
		"http://egip.brgm-rec.fr/wxs/?service=WMS&layers=Industry",
		"http://egip.brgm-rec.fr/wxs/?service=WMS&layers=TrainingCenter"
		};
	static String[] wfsurls= {
		"http://egip.brgm-rec.fr/wxs/?service=WFS&version=1.1.0&request=GetFeature&typeName=HeatFlowLine&srsName=EPSG:4326",
		"http://egip.brgm-rec.fr/wxs/?service=WFS&version=1.1.0&request=GetFeature&typeName=License&srsName=EPSG:4326",
		"http://egip.brgm-rec.fr/wxs/?service=WFS&version=1.1.0&request=GetFeature&typeName=TemperatureLine&srsName=EPSG:4326",
		"http://egip.brgm-rec.fr/wxs/?service=WFS&version=1.1.0&request=GetFeature&typeName=TemperatureUnit&srsName=EPSG:4326",
		"http://egip.brgm-rec.fr/wxs/?service=WFS&version=1.1.0&request=GetFeature&typeName=HeatFlowUnit&srsName=EPSG:4326",
		"http://egip.brgm-rec.fr/wxs/?service=WFS&version=1.1.0&request=GetFeature&typeName=Industry&srsName=EPSG:4326",
		"http://egip.brgm-rec.fr/wxs/?service=WFS&version=1.1.0&request=GetFeature&typeName=TrainingCenter&srsName=EPSG:4326"
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
