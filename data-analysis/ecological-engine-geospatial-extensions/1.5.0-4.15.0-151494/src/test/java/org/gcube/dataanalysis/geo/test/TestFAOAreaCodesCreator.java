package org.gcube.dataanalysis.geo.test;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.FAOOceanAreaCreator;

public class TestFAOAreaCodesCreator {

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
		config.setParam("Longitude_Column", "x_coord");
		config.setParam("Latitude_Column", "y_coord");
//		config.setParam("InputTable", "interp_2024_linear_01355325354899");
//		config.setParam("InputTable", "interp_2036_linear_11384851795640");
//		config.setParam("InputTable", "generic_id6ef3e4fa_6a06_4df1_9445_553f2e918102");
		config.setParam("InputTable", "generic_id25433bf4_c58c_4907_ac87_23919a3af0dc");
		
		config.setParam("OutputTableName", "csqout");
		config.setParam("Resolution", "5");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		FAOOceanAreaCreator cscreator = new FAOOceanAreaCreator();
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
}
