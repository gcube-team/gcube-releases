package org.gcube.dataanalysis.geo.test;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.FAOOceanAreaCreator;
import org.gcube.dataanalysis.geo.algorithms.FAOOceanAreaCreatorQuadrant;

public class TestFAOAreaCodesQuadrantCreator {

	public static void main(String[] args) throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		
		config.setParam("Quadrant_Column", "quadrant");
		config.setParam("Longitude_Column", "long");
		config.setParam("Latitude_Column", "lat");
		config.setParam("InputTable", "generic_id6ef3e4fa_6a06_4df1_9445_553f2e918102");
//		config.setParam("InputTable", "interp_2036_linear_11384851795640");
//		config.setParam("InputTable", "generic_id35e6ded3_4adc_48ba_a575_35a02b67514a");
		
		
		config.setParam("OutputTableName", "csqout");
		config.setParam("Resolution", "5");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		FAOOceanAreaCreatorQuadrant cscreator = new FAOOceanAreaCreatorQuadrant();
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
}
