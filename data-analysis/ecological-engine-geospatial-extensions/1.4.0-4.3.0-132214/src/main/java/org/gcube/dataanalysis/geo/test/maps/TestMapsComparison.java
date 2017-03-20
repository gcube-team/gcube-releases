package org.gcube.dataanalysis.geo.test.maps;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.MapsComparator;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;

public class TestMapsComparison {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
//		String  layertitle = "Ice velocity u from [12-15-02 01:00] to [12-15-09 01:00] (2D) {Native grid ORCA025.L75 monthly average: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-reanalysis-phys-001-004-b-ref-fr-mjm95-icemod}";
//		String  layertitle2 = "Ice velocity v from [12-15-02 01:00] to [12-15-09 01:00] (2D) {Native grid ORCA025.L75 monthly average: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-reanalysis-phys-001-004-b-ref-fr-mjm95-icemod}";
//		String  layertitle = "Number of Observations in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Apparent Oxygen Utilization - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/apparent_oxygen_utilization_annual_1deg_ENVIRONMENT_BIOTA_.nc}";
//		String  layertitle2 = "Seasonal or Monthly Climatology minus Annual Climatology in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Apparent Oxygen Utilization - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/apparent_oxygen_utilization_annual_1deg_ENVIRONMENT_BIOTA_.nc}";
//		String  layertitle2 = "Number of Mean Values within Radius of Influence in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Apparent Oxygen Utilization - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/apparent_oxygen_utilization_annual_1deg_ENVIRONMENT_BIOTA_.nc}";
//		String  layertitle = "Ice velocity u from [12-15-02 01:00] to [12-15-09 01:00] (2D) {Native grid ORCA025.L75 monthly average: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-reanalysis-phys-001-004-b-ref-fr-mjm95-icemod}";
//		String  layertitle2 = "Ice velocity v from [12-15-02 01:00] to [12-15-09 01:00] (2D) {Native grid ORCA025.L75 monthly average: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-reanalysis-phys-001-004-b-ref-fr-mjm95-icemod}";
		
		//String  layertitle = "wind stress from [05-01-07 14:00] to [04-01-12 14:00] (2D) {Monthly ASCAT global wind field: Data extracted from dataset http://tds0.ifremer.fr/thredds/dodsC/CERSAT-GLO-CLIM_WIND_L4-OBS_FULL_TIME_SERIE}";
		//String  layertitle2 = "wind speed from [05-01-07 14:00] to [04-01-12 14:00] (2D) {Monthly ASCAT global wind field: Data extracted from dataset http://tds0.ifremer.fr/thredds/dodsC/CERSAT-GLO-CLIM_WIND_L4-OBS_FULL_TIME_SERIE}";
		
		//String  layertitle = "Objectively Analyzed Climatology from [02-16-01 01:00] to [11-16-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Salinity - seasonal: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/salinity_seasonal_1deg_ENVIRONMENT_OCEANS_.nc}";
		//String  layertitle2 = "Objectively Analyzed Climatology from [01-16-01 01:00] to [12-16-01 01:00] (3D) {World Ocean Atlas 09: Apparent Oxygen Utilization - monthly: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/apparent_oxygen_utilization_monthly_1deg_ENVIRONMENT_BIOTA_.nc}"; 
		

//		String  layertitle = "FAO aquatic species distribution map of Istiophorus platypterus";
//		String  layertitle2 = "FAO aquatic species distribution map of Teuthowenia megalops";
		//{MEAN=1.0, VARIANCE=0.0, NUMBER_OF_ERRORS=38596, NUMBER_OF_COMPARISONS=260281, ACCURACY=85.17, MAXIMUM_ERROR=1.0, MAXIMUM_ERROR_POINT=3207:219:1, TREND=CONTRACTION, Resolution=0.5}

		//String  layertitle = "Sarda orientalis";
		//String  layertitle2 = "FAO aquatic species distribution map of Sarda chiliensis";
		
//		String  layertitle2 = "4e5c1bbf-f5ce-4b66-a67c-14d7d9920aa0";
//		String  layertitle = "38b2eb74-1c07-4569-8a81-36ac2f973146";

		//String  layertitle = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=lluidiamaculata20121218223748535cet&format=json&maxfeatures=1";
		//String  layertitle2 = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=lluidiamaculata20121218223748535cet&format=json&maxfeatures=1";
		
		String  layertitle = "http://geoserver-dev4.d4science.org/geoserver/aquamaps/ows?service=wfs&version=1.1.0&REQUEST=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&TYPENAME=aquamaps:llatimeriachalumnae20130717140243002cest&format=json&maxfeatures=1";
		String  layertitle2 = "http://geoserver-dev4.d4science.org/geoserver/aquamaps/ows?service=wfs&version=1.1.0&REQUEST=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&TYPENAME=aquamaps:llatimeriachalumnae20130717140243002cest&format=json&maxfeatures=1";
		
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(cfg);
		config.setGcubeScope("/gcube/devsec");
		config.setPersistencePath("./");
		
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setParam("Layer_1",layertitle);
		config.setParam("Layer_2",layertitle2);
		config.setParam("ValuesComparisonThreshold","0.1");
		config.setParam("Z","0");
		
		
		MapsComparator mc = new MapsComparator();
		mc.setConfiguration(config);
		mc.init();
		mc.compute();
		mc.getOutput();
	}
}
