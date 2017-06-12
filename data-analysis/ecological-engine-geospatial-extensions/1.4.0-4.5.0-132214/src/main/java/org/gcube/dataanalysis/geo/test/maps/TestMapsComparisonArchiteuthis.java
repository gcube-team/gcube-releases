package org.gcube.dataanalysis.geo.test.maps;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.MapsComparator;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;

public class TestMapsComparisonArchiteuthis {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
		String  layertitle = "96575f38-583c-4b6c-926f-e804156c7baa";
		String  layertitle2 = "f9a831c0-143f-4792-a5d9-896d72db73f6";
		
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(cfg);
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.org/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setParam("Layer_1",layertitle);
		config.setParam("Layer_2",layertitle2);
		config.setParam("ValuesComparisonThreshold","0.5");
		config.setParam("KThreshold","0.5");
		
		config.setParam("Z","0");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		
		MapsComparator mc = new MapsComparator();
		mc.setConfiguration(config);
		mc.init();
		mc.compute();
		mc.getOutput();
	}
}
