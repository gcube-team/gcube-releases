package org.gcube.dataanalysis.geo.test.regression;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.signals.PeriodicityDetector;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;
import org.gcube.dataanalysis.geo.matrixmodel.TimeSeriesExtractor;

public class RegressionSignalFromTable {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam(TableMatrixRepresentation.tableNameParameter, "generic_id037d302d_2ba0_4e43_b6e4_1a797bb91728");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "y");

		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "datetime");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "speed");
		config.setParam(TableMatrixRepresentation.filterParameter, "speed<2");

		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		config.setConfigPath(cfg);

		TimeSeriesExtractor intersector = new TimeSeriesExtractor(config);
		long t0 = System.currentTimeMillis();

		double signal[] = intersector.extractT("table", -47.97,43.42, 0d, 0.5);
		
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
		System.out.println("Signal: "+signal.length);
		
		PeriodicityDetector pd = new PeriodicityDetector();
		double F = pd.detectFrequency(signal,true);
		
		System.out.println("Detected Frequency:"+F+" indecision ["+pd.lowermeanF+" , "+pd.uppermeanF+"]");
		System.out.println("Detected Period:"+pd.meanPeriod+" indecision ["+pd.lowermeanPeriod+" , "+pd.uppermeanPeriod+"]");
		System.out.println("Detected Periodicity Strength:"+pd.periodicityStrength);
	}
	
}
