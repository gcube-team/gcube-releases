package org.gcube.dataanalysis.geo.batch;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.opengis.metadata.identification.TopicCategory;

public class GeothermalDataMetadataInsertDev {
	
	static String geonetworkurl = "http://geonetwork.geothermaldata.d4science.org/geonetwork";
	static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
	/*
	static String geonetworkurl = "http://geonetwork.d4science.org/geonetwork/";
	static String geoserverurl = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver";
	*/
	//static String geoserverurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";

	static String user = "admin";
	static String password = "d4science2014";
	//	static String workspace= "timeseriesgisdb";
 	//Temperature Isoline at 3 km depth
	//Surface Heat Flow Map of Italy
	//Temperature Isoline at 2 km depth
	//	Temperature Isoline at 1 km depth
	//Temperature map at 1 km depth
	//Surface Heat Flow Contour Map of Italy
	//Temperature map at 2 km depth
	//Temperature map at 3 km depth
	
	public static void main(String[] args) throws Exception{
		/*
		TemperatureIsolineat3kmdepth();
		SurfaceHeatFlowMapofItaly();
		TemperatureIsolineat2kmdepth();
		TemperatureIsolineat1kmdepth();
		Temperaturemapat1kmdepth();
		SurfaceHeatFlowContourMapofItaly();
		Temperaturemapat2kmdepth();
		Temperaturemapat3kmdepth();
		Energy();
		Licenses();
		
		*/
		Industry();
		TrainingCenter();
	}
	
	
	private static void TemperatureIsolineat3kmdepth() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(6.62397);
		metadataInserter.setYLeftLow(36.64916);
		metadataInserter.setXRightUpper(18.51444);
		metadataInserter.setYRightUpper(47.09458);
		
		metadataInserter.setTitle("Temperature Isoline at 3 km depth");
		metadataInserter.setAbstractField("Temperature map at 3km depth (below ground level) of Italy");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Energy resources","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:iso_3000");
		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.3.0&request=GetMap&layers=IGG:iso_3000&styles=&bbox=7.59535694122314,36.5945739746094,17.2513008117676,46.1617622375488&width=512&height=507&crs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=IGG:iso_3000&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};
		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void SurfaceHeatFlowMapofItaly() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(6.62397);
		metadataInserter.setYLeftLow(36.64916);
		metadataInserter.setXRightUpper(18.51444);
		metadataInserter.setYRightUpper(47.09458);
		
		metadataInserter.setTitle("Surface Heat Flow Map of Italy");
		metadataInserter.setAbstractField("Surface Heat Flow Contour Map of Italy");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Energy resources","EGIP","IRENA","D4Science");		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:hf_1");
		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.3.0&request=GetMap&layers=IGG:hf_1&styles=&bbox=6.66010808944702,36.571231842041,18.6017723083496,47.099250793457&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=IGG:hf_1&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};
		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	
	private static void TemperatureIsolineat2kmdepth() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(6.62397);
		metadataInserter.setYLeftLow(36.64916);
		metadataInserter.setXRightUpper(18.51444);
		metadataInserter.setYRightUpper(47.09458);
		
		metadataInserter.setTitle("Temperature Isoline at 2 km depth");
		metadataInserter.setAbstractField("Temperature Isoline at 2 km depth (below ground level) of Italy");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Energy resources","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:iso_2000");
		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.1.0&request=GetMap&layers=IGG:iso_2000&styles=&bbox=7.53796720504761,36.6590042114258,17.1645240783691,46.0757904052734&width=512&height=500&crs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=IGG:iso_2000&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};
		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}

	private static void TemperatureIsolineat1kmdepth() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(6.62397);
		metadataInserter.setYLeftLow(36.64916);
		metadataInserter.setXRightUpper(18.51444);
		metadataInserter.setYRightUpper(47.09458);
		
		metadataInserter.setTitle("Temperature Isoline at 1 km depth");
		metadataInserter.setAbstractField("Temperature Isoline at 1 km depth (below ground level) of Italy");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Energy resources","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:iso_1000");
		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.3.0&request=GetMap&layers=IGG:iso_1000&styles=&bbox=7.40797662734985,36.7031669616699,17.1524467468262,46.1305541992188&width=512&height=495&crs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=IGG:iso_1000&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};
		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void Temperaturemapat1kmdepth() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(6.62397);
		metadataInserter.setYLeftLow(36.64916);
		metadataInserter.setXRightUpper(18.51444);
		metadataInserter.setYRightUpper(47.09458);
		
		metadataInserter.setTitle("Temperature map at 1 km depth");
		metadataInserter.setAbstractField("Temperature map at 1km depth (below ground level) of Italy");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Energy resources","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:area_temp_1000");
		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.3.0&request=GetMap&layers=IGG:area_temp_1000&styles=&bbox=6.62688943789748,36.6438921370804,18.5206117399977,47.0919540445501&width=512&height=449&crs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=IGG:area_temp_1000&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};
		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void SurfaceHeatFlowContourMapofItaly() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(6.62397);
		metadataInserter.setYLeftLow(36.64916);
		metadataInserter.setXRightUpper(18.51444);
		metadataInserter.setYRightUpper(47.09458);
		
		metadataInserter.setTitle("Surface Heat Flow Contour Map of Italy");
		metadataInserter.setAbstractField("Surface Heat Flow Contour Map of Italy");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Energy resources","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:heat_flow_1");
		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.3.0&request=GetMap&layers=IGG:heat_flow_1&styles=&bbox=6.699791431427,36.5742835998535,18.6017723083496,47.0844573974609&width=512&height=452&crs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=IGG:heat_flow_1&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};
		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	private static void Temperaturemapat2kmdepth() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(6.62397);
		metadataInserter.setYLeftLow(36.64916);
		metadataInserter.setXRightUpper(18.51444);
		metadataInserter.setYRightUpper(47.09458);
		
		metadataInserter.setTitle("Temperature map at 2 km depth");
		metadataInserter.setAbstractField("Temperature map at 2km depth (below ground level) of Italy");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Energy resources","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:area_temp_2000");
		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.3.0&request=GetMap&layers=IGG:area_temp_2000&styles=&bbox=6.6268892288208,36.6438903808594,18.5206127166748,47.0919570922852&width=512&height=449&crs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=IGG:area_temp_2000&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};
		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void Temperaturemapat3kmdepth() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(6.62397);
		metadataInserter.setYLeftLow(36.64916);
		metadataInserter.setXRightUpper(18.51444);
		metadataInserter.setYRightUpper(47.09458);
		
		metadataInserter.setTitle("Temperature map at 3 km depth");
		metadataInserter.setAbstractField("Temperature map at 3km depth (below ground level) of Italy");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Energy resources","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE.name()+"_"+"_"+TopicCategory.ENVIRONMENT.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:area_temp_3000");
		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.3.0&request=GetMap&layers=IGG:area_temp_3000&styles=&bbox=6.6268892288208,36.5945739746094,18.5206127166748,47.0919570922852&width=512&height=451&crs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&typeName=IGG:area_temp_3000&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};
		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void Energy() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(-180);
		metadataInserter.setYLeftLow(-90);
		metadataInserter.setXRightUpper(180);
		metadataInserter.setYRightUpper(90);
		
		metadataInserter.setTitle("GeothermalManagementArea_ERANET");
		metadataInserter.setAbstractField("GeothermalManagementArea_ERANET");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Energy resources","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:GeothermalManagementArea_ERANET");

	/*
		String [] urls = {
				"http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/timeseriesws/wms?service=WMS&version=1.1.0&request=GetMap&layers=timeseriesws:GeothermalManagementArea2&styles=&bbox=-24.5465240478516,35.8154258728027,44.8349914550781,66.5346374511719&width=745&height=330&srs=EPSG:4326",
				"http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/timeseriesws/wfs?service=wfs&version=1.1.0&REQUEST=GetFeature&TYPENAME=timeseriesws:GeothermalManagementArea2&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};
	*/

		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.1.0&request=GetMap&layers=IGG:GeothermalManagementArea_ERANET&styles=&bbox=-24.546524000000005,35.49220699999999,44.83498800000001,66.563774&width=736&height=330&srs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&TYPENAME=IGG:GeothermalManagementArea_ERANET&srsName=urn:x-ogc:def:crs:EPSG:4326"
				};

		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void Licenses() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(-180);
		metadataInserter.setYLeftLow(-90);
		metadataInserter.setXRightUpper(180);
		metadataInserter.setYRightUpper(90);
		
		metadataInserter.setTitle("Licences");
		metadataInserter.setAbstractField("Exploration and production licenses and (projected) power production");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Licenses","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:licence");

		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.1.1&request=GetMap&layers=IGG:licence&styles=&bbox=8.519806711445952,36.75219999995809,15.243165,45.612201456761284&width=388&height=512&srs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=IGG:licence"
				};

		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	
	private static void Industry() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(-180);
		metadataInserter.setYLeftLow(-90);
		metadataInserter.setXRightUpper(180);
		metadataInserter.setYRightUpper(90);
		
		metadataInserter.setTitle("Industry");
		metadataInserter.setAbstractField("Industries involved in geothermal activities refer to all companies that produce components both for power production and the direct use of heat");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","Industry","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:Industry");

		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.1.1&request=GetMap&layers=IGG:Industry&styles=&bbox=9.189578001171471,41.909917999980756,12.480876999984194,45.52478199898418&width=466&height=512&srs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=IGG:Industry"
				};

		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
	private static void TrainingCenter() throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		metadataInserter.setGeonetworkUrl(geonetworkurl);
		metadataInserter.setGeonetworkUser(user);
		metadataInserter.setGeonetworkPwd(password);
		metadataInserter.setResolution(0);
		metadataInserter.setXLeftLow(-180);
		metadataInserter.setYLeftLow(-90);
		metadataInserter.setXRightUpper(180);
		metadataInserter.setYRightUpper(90);
		
		metadataInserter.setTitle("TrainingCenter");
		metadataInserter.setAbstractField("List of education and research centres with geothermal courses and lectures: The list is in a table format and includes the type, the name, the location and the URL");
		metadataInserter.setCustomTopics("geothermal energy","map","Italy","TrainingCenter","EGIP","D4Science");
		metadataInserter.setCategoryTypes("_"+TopicCategory.GEOSCIENTIFIC_INFORMATION.name()+"_"+"_"+TopicCategory.UTILITIES_COMMUNICATION.name()+"_");
		metadataInserter.setResolution(0);
		
		metadataInserter.setLayerName("IGG:TrainingCenter");

		
		String [] urls = {
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/wms?service=WMS&version=1.1.1&request=GetMap&layers=IGG:TrainingCenter&styles=&bbox=7.673140015606858,37.50289999999999,16.861828000003374,45.635315999999726&width=512&height=453&srs=EPSG:4326&format=application/openlayers",
				"http://repoigg.services.iit.cnr.it/geoserver/IGG/ows?service=WFS&version=1.1.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typeName=IGG:TrainingCenter"
				};

		String [] protocols = {"WMS","WFS"};
		
		metadataInserter.customMetaDataInsert(urls,protocols);
	}
	
}
