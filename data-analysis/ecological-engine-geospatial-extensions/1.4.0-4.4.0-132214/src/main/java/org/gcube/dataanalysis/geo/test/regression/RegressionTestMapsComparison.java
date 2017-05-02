package org.gcube.dataanalysis.geo.test.regression;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Evaluator;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class RegressionTestMapsComparison {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

	public static void main(String[] args) throws Exception {

		List<ComputationalAgent>  evaluators = EvaluatorsFactory.getEvaluators(testConfig1());
		evaluators.get(0).init();
		Regressor.process(evaluators.get(0));
		evaluators = null;
	}

	private static AlgorithmConfiguration testConfig1() {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setConfigPath("./cfg");
		config.setPersistencePath("./");
		config.setAgent("MAPS_COMPARISON");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
//		config.setParam("Layer_1","86a7ac79-866a-49c6-b5d5-602fc2d87ddd");
//		config.setParam("Layer_2","86a7ac79-866a-49c6-b5d5-602fc2d87ddd");
		
		//World seas : IHO vs Marine regions
//		config.setParam("Layer_2","70a6d757-e607-46f7-b643-e21749f45a42");
//		config.setParam("Layer_1","a2a8c130-124f-45b5-973f-c9358028a2a6");
		
		//FAO vs FAO:
//		config.setParam("Layer_1","b040894b-c5db-47fc-ba9c-d4fafcdcf620"); //goblin shark
//		config.setParam("Layer_2","c9a31223-cc00-4acd-bc5b-a0c76a7f79c7"); //humbolt squid
		
		//FAO vs AquaMaps
//		config.setParam("Layer_1","b040894b-c5db-47fc-ba9c-d4fafcdcf620");
//		config.setParam("Layer_2","c9a31223-cc00-4acd-bc5b-a0c76a7f79c7");
		
		//NetCDF vs NETCDF WOA
//		config.setParam("Layer_1","e0dbbcc0-8364-4087-8bcb-c7d95b2f55c8"); //statistical mean oxygen
//		config.setParam("Layer_2","49f5a5a1-80ff-4a00-8c84-dac29bda1a23");//statistical mean phosphate

		//Eleutheronema tetradactylum
		config.setParam("Layer_1","fao-species-map-fot");
		config.setParam("Layer_2","c492f5d3-1cfc-44e3-b8d2-8530fec3e7e7");
		
		
		
		//NetCDF vs NetCDF MyOcean
//		config.setParam("Layer_1","e0dbbcc0-8364-4087-8bcb-c7d95b2f55c8"); //statistical mean oxygen
//		config.setParam("Layer_2","fc9ac2f4-a2bd-43d1-a361-ac67c5ceac31");//temperature

		//NetCDF vs IHO
//		config.setParam("Layer_1","70a6d757-e607-46f7-b643-e21749f45a42");//IHO
//		config.setParam("Layer_2","fc9ac2f4-a2bd-43d1-a361-ac67c5ceac31");//temperature
		
		//NetCDF vs NetCDF MyOcean only
//		config.setParam("Layer_1","fc9ac2f4-a2bd-43d1-a361-ac67c5ceac31"); //statistical mean oxygen
//		config.setParam("Layer_2","fc9ac2f4-a2bd-43d1-a361-ac67c5ceac31");//temperature
		
		//NetCDF vs NetCDF Envri
//		config.setParam("Layer_1","Etna Volcano SAR Analysis 1"); //
//		config.setParam("Layer_2","Etna Volcano SAR Analysis 7");//
		
		
		
		config.setParam("ValuesComparisonThreshold",""+0.1);
		config.setParam("Z","0");
		config.setGcubeScope("/gcube");
//		config.setGcubeScope(null);
//		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps");
		
		return config;
	}
}
