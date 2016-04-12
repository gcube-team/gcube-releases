package org.gcube.dataanalysis.geo.test.regression;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.signals.PeriodicityDetector;
import org.gcube.dataanalysis.geo.matrixmodel.PointsExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.TimeSeriesExtractor;

import com.vividsolutions.jts.geom.util.PointExtracter;

public class RegressionPointExtraction {

	static String cfg = "./cfg/";
	
	public static void main(String[] args) throws Exception{
		String  layertitle = "afd54b39-30f7-403a-815c-4f91c6c74c26";
		long t0 = System.currentTimeMillis();
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		config.setConfigPath(cfg);
		
		PointsExtractor pe = new PointsExtractor(config);
		
		double value = pe.extractXYZT(layertitle, 0,0,0,0, 0);
		
		System.out.println("Point value: "+value);
		
		
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
		
		
	}
	
}
