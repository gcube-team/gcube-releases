package org.gcube.dataanalysis.geo.test.maps;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.MapsComparator;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;

public class TestMapsComparisonTemperatureUUID {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
//		String  layertitle = "Temperature in [12-15-09 01:00] (3D) {Native grid ORCA025.L75 monthly average: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-reanalysis-phys-001-004-b-ref-fr-mjm95-gridt}";
//		String  layertitle = "Standard Deviation from Statistical Mean from [01-16-01 01:00] to [12-16-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - monthly: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_monthly_1deg_ENVIRONMENT_OCEANS_.nc}";
//		String  layertitle = "86a7ac79-866a-49c6-b5d5-602fc2d87ddd";
		String  layertitle = "35412bde-aa6e-49f6-b9ad-e99458882f92";
		
//		String  layertitle2 = "Statistical Mean in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_annual_1deg_ENVIRONMENT_OCEANS_.nc}";
//		String  layertitle2 = "0e03d0fa-9c44-4a0c-a7e3-9f6d48710d00";
//		String  layertitle2 = layertitle;
		String  layertitle2 = "625ba45b-c19f-4a9d-b3f7-be13e8f73101";
//		{MEAN=224.49, VARIANCE=10337.11, NUMBER_OF_ERRORS=47054, NUMBER_OF_COMPARISONS=65522, ACCURACY=28.19, MAXIMUM_ERROR=303.6, MAXIMUM_ERROR_POINT=5006:104, Resolution=0.9972222222222222}
		
		
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
		config.setGcubeScope(null);
		
		MapsComparator mc = new MapsComparator();
		mc.setConfiguration(config);
		mc.init();
		mc.compute();
		mc.getOutput();
	}
}
