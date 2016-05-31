package org.gcube.dataanalysis.geo.test.regression;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.signals.PeriodicityDetector;
import org.gcube.dataanalysis.geo.matrixmodel.TimeSeriesExtractor;

public class RegressionLowPeriodicity {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
		String  layertitle = "c565e32c-c5b3-4964-b44f-06dc620563e9";
		long t0 = System.currentTimeMillis();
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		config.setConfigPath(cfg);
		TimeSeriesExtractor intersector = new TimeSeriesExtractor(config);
		double signal[] = intersector.extractT(layertitle, 0d, 0d,0d,0.5);
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
		
		System.out.println("Signal: "+signal.length);

		PeriodicityDetector pd = new PeriodicityDetector();
		double F = pd.detectFrequency(signal,true);
		
		System.out.println("Detected Frequency:"+F+" indecision ["+pd.lowermeanF+" , "+pd.uppermeanF+"]");
		System.out.println("Detected Period:"+pd.meanPeriod+" indecision ["+pd.lowermeanPeriod+" , "+pd.uppermeanPeriod+"]");
		System.out.println("Detected Periodicity Strength:"+pd.periodicityStrength+":"+pd.getPeriodicityStregthInterpretation());
	}
	
}
