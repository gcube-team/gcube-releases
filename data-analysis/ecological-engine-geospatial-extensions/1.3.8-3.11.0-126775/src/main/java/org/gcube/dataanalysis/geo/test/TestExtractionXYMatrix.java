package org.gcube.dataanalysis.geo.test;

import java.io.File;
import java.io.FileWriter;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;
import org.gcube.dataanalysis.geo.utils.MapUtils;

public class TestExtractionXYMatrix {

	static String[] layers = {
			"94ea5767-ae76-41dc-be87-f9a0bdc96419",//temp
			"9196c9cf-47c9-413e-8a34-04fadde48e63",//salinity 3d
			"23646f93-23a8-4be4-974e-aee6bebe1707",//ph
			"46b16749-88c1-4d35-a60a-8ad328cc320c",//oxygen
			"229c135f-2379-4712-bdd6-89baa8637a27",//nitrate
			"3fb7fd88-33d4-492d-b241-4e61299c44bb",//latimeria
			"4aa10e73-5bda-4eac-a059-792b240ef759",//cloud fraction
			"fao-rfb-map-ccsbt",//tuna
			"889d67b4-32f5-4159-b01f-9c9662176434"//carcharodon
			
	};
	
	public static void main(String[] args) throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "gcube");
		config.setParam("DatabasePassword", "d4science2");
		config.setParam("DatabaseURL", "jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec/devVRE");
		double resolution = 1;
		FileWriter fw = new FileWriter(new File("maps.txt"));
		for (String layer:layers){
			XYExtractor extractor = new XYExtractor(config);
			double[][] matrix = extractor.extractXYGrid(layer, 0, -180, 180, -90, 90, 0, resolution,resolution);
			String map = MapUtils.globalASCIIMap(matrix);
			fw.write(map);
		}
		
		fw.close();
		System.out.println("DONE!");
	}
	
}
