package org.gcube.dataanalysis.geo.test.infra;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;

public class TestEstimateFishingActivity {

	static AlgorithmConfiguration[] configs = { testGridConversion()};
	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");

		for (int i = 0; i < configs.length; i++) {
			AnalysisLogger.getLogger().debug("Executing: "+configs[i].getAgent());
			List<ComputationalAgent> trans = null;
			trans = TransducerersFactory.getTransducerers(configs[i]);
			trans.get(0).init();
			Regressor.process(trans.get(0));
			StatisticalType st = trans.get(0).getOutput();
			AnalysisLogger.getLogger().debug("ST:" + st);
			trans = null;
		}
	}
	
	
	private static AlgorithmConfiguration testGridConversion() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("ESTIMATE_FISHING_ACTIVITY");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.org/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		config.setParam("InputTable", "generic_idb7822ad1_f66c_444c_9c07_2698b824bab3");
		config.setParam("VesselsIDColumn", "vessel_id");
		config.setParam("VesselsSpeedsColumn", "speed");
		config.setParam("VesselsTimestampsColumn", "datetime");
		config.setParam("VesselsLatitudesColumn", "y");
		config.setParam("VesselsLongitudesColumn", "x");
		
		return config;
	}

		
}
