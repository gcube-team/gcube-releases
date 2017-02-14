package org.gcube.dataanalysis.ecoengine.test;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestSimpleSignal {


//	static AlgorithmConfiguration[] configs = {periodicSignalConfig(), russianSignalConfig(),simpleSignalConfig(), occurrencePointsSignalConfig(),hugeSignalConfig()};
//static AlgorithmConfiguration[] configs = {periodicSignalConfig(), russianSignalConfig(),simpleSignalConfig()};
//	static AlgorithmConfiguration[] configs = {NAFOSignalConfig()};
//	static AlgorithmConfiguration[] configs = {largeCustomSignalConfig()};
//	static AlgorithmConfiguration[] configs = {temperatureSignalConfig()};
//	static AlgorithmConfiguration[] configs = {periodicSignalConfig()};
//	static AlgorithmConfiguration[] configs = {simpleSignalConfig()};
//	static AlgorithmConfiguration[] configs = {sawSignalConfig()};
	static AlgorithmConfiguration[] configs = {temperatureSignalConfig()};
//	static AlgorithmConfiguration[] configs = {russianSignalConfig()};
//	static AlgorithmConfiguration[] configs = {largeCustomSignalConfig()};
//	static AlgorithmConfiguration[] configs = {occurrencePointsSignalConfig()};
//	static AlgorithmConfiguration[] configs = {hugeSignalConfig()};
	public static void main(String[] args) throws Exception {
		
		int wLength = (int) Math.pow(2, 1);
		System.out.println("L:"+wLength);
		for (int i = 0; i < configs.length; i++) {
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

	public static AlgorithmConfiguration simpleSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "timeseries_id4dd368bf_63fb_4d19_8e31_20ced63a477d");
		config.setParam("ValueColum", "quantity");
		
		config.setParam("SSA_Window_in_Samples", "30");
		config.setParam("SSA_EigenvaluesThreshold", "0.7");
		config.setParam("SSA_Points_to_Forecast", "10");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}
	
	public static AlgorithmConfiguration russianSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "generic_ideb9efbe0_61ad_4eea_b0ee_95e64ce11b28");
		config.setParam("ValueColum", "quantity");
		config.setParam("SSA_Window_in_Samples", "20");
		config.setParam("SSA_EigenvaluesThreshold", "0.7");
		config.setParam("SSA_Points_to_Forecast", "10");

		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}
	
	public static AlgorithmConfiguration occurrencePointsSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "generic_id037d302d_2ba0_4e43_b6e4_1a797bb91728");
		config.setParam("ValueColum", "speed");
		config.setParam("TimeColum", "datetime");
		config.setParam("AggregationFunction", "AVG");
		config.setParam("SSA_Window_in_Samples", "20");
		config.setParam("SSA_EigenvaluesThreshold", "0.7");
		config.setParam("SSA_Points_to_Forecast", "10");

		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}

	public static AlgorithmConfiguration periodicSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "timeseries_ide814eb07_c13b_41b3_a240_aa99446db831");
		config.setParam("ValueColum", "quantity");
		config.setParam("FrequencyResolution", "0.01");
		config.setParam("SSA_Window_in_Samples", "20");
		config.setParam("SSA_EigenvaluesThreshold", "0.7");
		config.setParam("SSA_Points_to_Forecast", "10");

		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}
	
	public static AlgorithmConfiguration hugeSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "generic_id634a660c_4d1a_410c_aa45_eb6e4c5afdf9");
		config.setParam("ValueColum", "quantity");
		config.setParam("TimeColum", "years");
		config.setParam("SSA_Window_in_Samples", "20");
		config.setParam("SSA_EigenvaluesThreshold", "0.7");
		config.setParam("SSA_Points_to_Forecast", "10");

		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}
	
	
	public static AlgorithmConfiguration NAFOSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "timeseries_id39c6c28f_2484_421c_8ffb_9c2cc2330c62");
		config.setParam("ValueColum", "speed");
		
		config.setParam("SSA_Window_in_Samples", "30");
		config.setParam("SSA_EigenvaluesThreshold", "0.7");
		config.setParam("SSA_Points_to_Forecast", "10");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}

	public static AlgorithmConfiguration sawSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "timeseries_ide814eb07_c13b_41b3_a240_aa99446db831");
		config.setParam("ValueColum", "quantity");
		
		
		config.setParam("FrequencyResolution", "0.01");
		config.setParam("SSA_Window_in_Samples", "20");
		config.setParam("SSA_EigenvaluesThreshold", "0.7");
		config.setParam("SSA_Points_to_Forecast", "10");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}

	public static AlgorithmConfiguration largeCustomSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "timeseries_idd3dd174e_242c_4f8b_920a_faa79691ca43");
		config.setParam("ValueColum", "quantity");
		
		
		config.setParam("FFT_Window_Samples", "14");
		config.setParam("SSA_Window_in_Samples", "20");
		config.setParam("SSA_EigenvaluesThreshold", "0.7");
		config.setParam("SSA_Points_to_Forecast", "10");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}
	
	
	public static AlgorithmConfiguration earthquakesSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "timeseries_id0f44b131_de55_4839_b07f_2721574e2b9d");
		config.setParam("ValueColum", "magnitude");
		
		
		config.setParam("FFT_Window_Samples", "14");
		config.setParam("SSA_Window_in_Samples", "20");
		config.setParam("SSA_EigenvaluesThreshold", "0.7");
		config.setParam("SSA_Points_to_Forecast", "10");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}
	
	
	
	public static AlgorithmConfiguration temperatureSignalConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam("TimeSeriesTable", "timeseries_id08b3abb9_c7b0_4b82_8117_64b69055416f");
		config.setParam("ValueColum", "fvalue");
		
		
		config.setParam("FFT_Window_Samples", "70");
		config.setParam("SSA_Window_in_Samples", "10");
		config.setParam("SSA_EigenvaluesThreshold", "0.7");
		config.setParam("SSA_Points_to_Forecast", "10");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setGcubeScope("/gcube");
		config.setConfigPath("./cfg");
		return config;

	}	
	
	
}
