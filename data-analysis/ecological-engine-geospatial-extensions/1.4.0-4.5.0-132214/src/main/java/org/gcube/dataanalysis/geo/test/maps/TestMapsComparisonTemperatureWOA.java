package org.gcube.dataanalysis.geo.test.maps;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.MapsComparator;

public class TestMapsComparisonTemperatureWOA {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
		String  layertitle = "Temperature in [07-01-01 13:00] (3D) {World Ocean Atlas 2005: Tcl version: 8.4.13, NAP version: 6.2.2}";
		String  layertitle2 = "Statistical Mean in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_annual_1deg_ENVIRONMENT_OCEANS_.nc}";
		//{MEAN=0.0, VARIANCE=0.0, NUMBER_OF_ERRORS=0, NUMBER_OF_COMPARISONS=65522, ACCURACY=100.0, MAXIMUM_ERROR=0.0, MAXIMUM_ERROR_POINT=null, TREND=STATIONARY, Resolution=0.9972222222222222}
		
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(cfg);
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setParam("Layer_1",layertitle);
		config.setParam("Layer_2",layertitle2);
		config.setParam("ValuesComparisonThreshold",""+0.1);
		config.setParam("Z","0");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		MapsComparator mc = new MapsComparator();
		mc.setConfiguration(config);
		mc.init();
		mc.compute();
		mc.getOutput();
	}
}
