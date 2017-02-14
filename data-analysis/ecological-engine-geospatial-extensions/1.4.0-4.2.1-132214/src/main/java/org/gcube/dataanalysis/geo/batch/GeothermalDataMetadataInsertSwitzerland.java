package org.gcube.dataanalysis.geo.batch;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class GeothermalDataMetadataInsertSwitzerland {
	
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
		"Industries",
		"Heat Flow Lines",
		"Heat Flow Units",
		"Licenses",
		"Temperature Units",
		"Temperature Lines",
		"Training Centers",
		//"Heatflow",
		//"Geothermal Map of Switzerland 1:500000"
		};
	static String[] abstracts = {
		"Industry data for EGIP",
		"Heat Flow Line data for EGIP",
		"Heat Flow Unit data for EGIP",
		"License data for EGIP",
		"Temperature Units data for EGIP",
		"Temperature Lines data for EGIP",
		"Training Center data for EGIP",
		//"The geothermal map shows the thermal energy that is produced in the subsurface and traverses the Earth's surface within an area of 1 m2. The heat itself is released in the Earth's crust (around 30 km thick) generally by radioactive decay processes. On average, the heat flow in Switzerland is around 90 mW/m2. The highest figures are found in northern Switzerland and the lowest in the Alps/Jura. Differences are caused by deep groundwater circulating in permeable rock strata. The heat flow data is calculated from the temperature gradient (average value approx. 30 K/km) and heat conductivity of the rock (average value approx. 3 W/m/K). Paper map: Ph. Bodmer, 1982. Vector map: 2001.",
		//"The geothermal map shows the thermal energy that is produced in the subsurface and traverses the Earth's surface within an area of 1 m2. The heat itself is released in the Earth's crust (around 30 km thick) generally by radioactive decay processes. On average, the heat flow in Switzerland is around 90 mW/m2. The highest figures are found in northern Switzerland and the lowest in the Alps/Jura. Differences are caused by deep groundwater circulating in permeable rock strata. The heat flow data is calculated from the temperature gradient (average value approx. 30 K/km) and heat conductivity of the rock (average value approx. 3 W/m/K). Paper map: Ph. Bodmer, 1982. Vector map: 2001."
	};
	static String[] customTopics = {
		"geothermal energy, Energy resources, Switzerland, EGIP",
		"geothermal energy, Energy resources, Switzerland, EGIP",
		"geothermal energy, Energy resources, Switzerland, EGIP",
		"geothermal energy, Energy resources, Switzerland, EGIP",
		"geothermal energy, Energy resources, Switzerland, EGIP",
		"geothermal energy, Energy resources, Switzerland, EGIP",
		"geothermal energy, Energy resources, Switzerland, EGIP",
		//"geophysics, geophysical map, geothermal energy, e-geo.ch geoportal, Geology, Energy resources, EGIP",
		//"geophysics, geophysical map, geothermal energy, e-geo.ch geoportal, Geology, Energy resources, EGIP"
		};
	static String[] categoryTypes = {
		"_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		"_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_",
		//"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		//"_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_",
		};
	static String[] layernames = {
		"swisstopo:industryTest",
		"swisstopo:heatFlowLineTest_WGS84",
		"swisstopo:heatFlowUnitTest_WGS84",
		"swisstopo:license",
		"swisstopo:tempDummyPoly",
		"swisstopo:tempDummyLine",
		"swisstopo:trainingCenter",
		//"Heatflow",
		//"Geothermal Map of Switzerland 1:500000"
	};
	static String[] wmsurls= {
		"http://swisstopo.geops.ch/geoserver/swisstopo/wms?service=WMS&version=1.1.0&request=GetMap&layers=swisstopo:industryTest&styles=&bbox=5.0,44.0,10.0,48.0&width=512&height=409&srs=EPSG:4326&format=application/openlayers",
		"http://swisstopo.geops.ch/geoserver/swisstopo/wms?service=WMS&version=1.1.0&request=GetMap&layers=swisstopo:heatFlowLineTest_WGS84&styles=&bbox=6.536429259689217,45.96452289074837,9.88179989473991,47.68507871081211&width=641&height=330&srs=EPSG:4326&format=application/openlayers",
		"http://swisstopo.geops.ch/geoserver/swisstopo/wms?service=WMS&version=1.1.0&request=GetMap&layers=swisstopo:heatFlowUnitTest_WGS84&styles=&bbox=6.495394739540089,45.92430483245075,9.923892462894338,47.67826328791616&width=645&height=330&srs=EPSG:4326&format=application/openlayers",
		"http://swisstopo.geops.ch/geoserver/swisstopo/wms?service=WMS&version=1.1.0&request=GetMap&layers=swisstopo:license&styles=&bbox=5.83103329189459,45.66406238270901,10.980959404235438,47.8584385534728&width=774&height=330&srs=EPSG:4326&format=application/openlayers",
		"http://swisstopo.geops.ch/geoserver/swisstopo/wms?service=WMS&version=1.1.0&request=GetMap&layers=swisstopo:tempDummyPoly&styles=&bbox=5.83103329189459,45.66406238270901,10.980959404235438,47.8584385534728&width=774&height=330&srs=EPSG:4326&format=application/openlayers",
		"http://swisstopo.geops.ch/geoserver/swisstopo/wms?service=WMS&version=1.1.0&request=GetMap&layers=swisstopo:tempDummyLine&styles=&bbox=5.83103329189459,45.66406238270901,10.980959404235438,47.8584385534728&width=774&height=330&srs=EPSG:4326&format=application/openlayers",
		"http://swisstopo.geops.ch/geoserver/swisstopo/wms?service=WMS&version=1.1.0&request=GetMap&layers=swisstopo:trainingCenter&styles=&bbox=5.83103329189459,45.66406238270901,10.980959404235438,47.8584385534728&width=774&height=330&srs=EPSG:4326&format=application/openlayers",
		//"Heatflow",
		//"Geothermal Map of Switzerland 1:500000"
	};
	static String[] wfsurls= {
		"http://swisstopo.geops.ch/geoserver/swisstopo/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=swisstopo:industryTest",
		"http://swisstopo.geops.ch/geoserver/swisstopo/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=swisstopo:heatFlowLineTest_WGS84",
		"http://swisstopo.geops.ch/geoserver/swisstopo/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=swisstopo:heatFlowUnitTest_WGS84",
		"http://swisstopo.geops.ch/geoserver/swisstopo/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=swisstopo:license",
		"http://swisstopo.geops.ch/geoserver/swisstopo/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=swisstopo:tempDummyPoly",
		"http://swisstopo.geops.ch/geoserver/swisstopo/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=swisstopo:tempDummyLine",
		"http://swisstopo.geops.ch/geoserver/swisstopo/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=swisstopo:trainingCenter",
		//"Heatflow",
		//"Geothermal Map of Switzerland 1:500000"
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
