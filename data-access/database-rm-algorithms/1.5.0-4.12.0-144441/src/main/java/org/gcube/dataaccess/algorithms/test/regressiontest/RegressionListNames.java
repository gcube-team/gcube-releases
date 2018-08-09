package org.gcube.dataaccess.algorithms.test.regressiontest;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class RegressionListNames {

	static String[] algorithms = { "Postgres1", "Postgres2", "Postgis",
			"Mysql", "NullInputValue" };

	static AlgorithmConfiguration config;

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");

//		for (int i = 0; i < algorithms.length; i++) {
			AnalysisLogger.getLogger().debug("Executing:" + "test");
			
			AlgorithmConfiguration config = Regressor.getConfig();
			
			config.setAgent("LISTDBNAMES");
			config.setGcubeScope("/gcube");	

			
			List<ComputationalAgent> trans = null;
			trans = TransducerersFactory.getTransducerers(config);
			trans.get(0).init();

			// trans.setConfiguration(configs[i]);
			// trans.init();
			Regressor.process(trans.get(0));
			StatisticalType st = trans.get(0).getOutput();
			AnalysisLogger.getLogger().debug("ST:" + st);
			
			
//			// Print Result
//			PrimitiveType obj= (PrimitiveType)st;
//			Object result=(Object) (obj.getContent());
//			LinkedHashMap map=new LinkedHashMap<String, String>();
//			
//			map= (LinkedHashMap) result;
//			
//			for(int j=0;j<map.size();j++){
//				
//				AnalysisLogger.getLogger().debug("value:" + map.get(j));
//				
//			}
			
			trans = null;
//		}

	}
	
//	private static AlgorithmConfiguration test() {
//		
//		AlgorithmConfiguration config = Regressor.getConfig();
//		
//		config.setAgent("LISTDBNAMES");
//		config.setGcubeScope("/gcube");	
//		return config;
//		
//	}
	
	

}
