package org.gcube.dataanalysis.ecoengine.test.signalprocessing;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.ecoengine.transducers.TimeSeriesAnalysis;

public class TestIOTCSignals {



	static AlgorithmConfiguration[] configs = {IOTClongitudeConfig()};

	public static void main(String[] args) throws Exception {
		
		int wLength = (int) Math.pow(2, 1);
		System.out.println("L:"+wLength);
		for (int i = 0; i < configs.length; i++) {
			TimeSeriesAnalysis.display=true;
			System.out.println("*****************TEST "+i+" *****************");

			List<ComputationalAgent> trans = null;
			trans = TransducerersFactory.getTransducerers(configs[i]);
			trans.get(0).init();
			Regressor.process(trans.get(0));
			StatisticalType st = trans.get(0).getOutput();
			AnalysisLogger.getLogger().debug("ST:" + st);
			trans = null;
			System.out.println("*****************END TEST*****************");
		}
	}
	
	public static AlgorithmConfiguration IOTCSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "timeseries_idb73029b9_226e_4d0f_b828_24854d0b7b44	");
		config.setParam("ValueColum", "fishing_hours");
		
		
		config.setParam("FFT_Window_Samples", "128");
		config.setParam("SSA_Window_in_Samples", "80");
		config.setParam("SSA_EigenvaluesThreshold", "0.07");
		config.setParam("SSA_Points_to_Forecast", "24");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}
	
	public static AlgorithmConfiguration IOTCLatitudeConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "timeseries_idb73029b9_226e_4d0f_b828_24854d0b7b44	");
		config.setParam("ValueColum", "latitude");
		
		
		config.setParam("FFT_Window_Samples", "256");
		config.setParam("SSA_Window_in_Samples", "200");
		config.setParam("SSA_EigenvaluesThreshold", "0.07");
		config.setParam("SSA_Points_to_Forecast", "12");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}
	
	public static AlgorithmConfiguration IOTClongitudeConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "timeseries_idb73029b9_226e_4d0f_b828_24854d0b7b44	");
		config.setParam("ValueColum", "longitude");
		
		
		config.setParam("FFT_Window_Samples", "256");
		config.setParam("SSA_Window_in_Samples", "200");
		config.setParam("SSA_EigenvaluesThreshold", "0.07");
		config.setParam("SSA_Points_to_Forecast", "12");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}
}
