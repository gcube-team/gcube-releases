package org.gcube.dataanalysis.ecoengine.test.signalprocessing;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.ecoengine.transducers.TimeSeriesAnalysis;

public class IOTCAnalyse {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		System.out.println("*******START TEST******");
		List<ComputationalAgent> agent = TransducerersFactory.getTransducerers(configAlgorithm());
		agent.get(0).init();
		Regressor.process(agent.get(0));
		StatisticalType st = agent.get(0).getOutput();
		System.out.println("Output:"+st);
		agent = null;
		System.out.println("*******END TEST******");
	}
	
	
	public static AlgorithmConfiguration configAlgorithm(){
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		AnalysisLogger.setLogger("./cfg/"+AlgorithmConfiguration.defaultLoggerFile);
		config.setAgent("TIME_SERIES_PROCESSING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		
		config.setParam("TimeSeriesTable", "timeseries_id9ac52133_3d3b_418e_8d70_c61844623e81");
		config.setParam("ValueColum", "Effort");
		config.setParam("FFT_Window_Samples", "128");
		config.setParam("SSA_Window_in_Samples", "36");
		config.setParam("SSA_EigenvaluesThreshold", "0.07");
		config.setParam("SSA_Points_to_Forecast", "12");
		config.setParam("AggregationFunction", "SUM");
		config.setParam("Sensitivity", "LOW");
		
		config.setGcubeScope("/gcube");
		
		TimeSeriesAnalysis.display=true;
		return config;
	}
}
