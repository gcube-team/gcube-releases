package org.gcube.dataanalysis.geo.test;

import java.io.File;
import java.io.FileWriter;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.CSquaresCreator;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;
import org.gcube.dataanalysis.geo.utils.MapUtils;

public class TestCSquareCodesCreator {

	public static void main(String[] args) throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		
//		config.setParam("Longitude_Column", "centerlong");
//		config.setParam("Latitude_Column", "centerlat");
//		config.setParam("InputTable", "interp_2024_linear_01355325354899");
//		config.setParam("InputTable", "interp_2036_linear_11384851795640");

		config.setParam("Longitude_Column", "center_long");
		config.setParam("Latitude_Column", "center_lat");
		config.setParam("InputTable", "generic_ide4573b63_b955_4bbc_b83f_579ff3f0858f");

		
		config.setParam("OutputTableName", "csqout");
		config.setParam("CSquare_Resolution", "0.5");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		CSquaresCreator cscreator = new CSquaresCreator();
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
}
