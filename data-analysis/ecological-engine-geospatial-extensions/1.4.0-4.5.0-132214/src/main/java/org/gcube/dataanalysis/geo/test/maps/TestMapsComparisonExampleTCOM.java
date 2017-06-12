package org.gcube.dataanalysis.geo.test.maps;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.MapsComparator;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;

public class TestMapsComparisonExampleTCOM {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
		String  layertitle2 = "FAO aquatic species distribution map of Eleutheronema tetradactylum";
		String  layertitle = "FAO aquatic species distribution map of Leptomelanosoma indicum";
		
		//{MEAN=1.0, VARIANCE=0.0, NUMBER_OF_ERRORS=1823, NUMBER_OF_COMPARISONS=260281, ACCURACY=99.3, MAXIMUM_ERROR=1.0, MAXIMUM_ERROR_POINT=1008:390:1, TREND=EXPANSION, Resolution=0.5}
		
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(cfg);
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setParam("Layer_1",layertitle);
		config.setParam("Layer_2",layertitle2);
		config.setParam("Z","0");
		config.setGcubeScope(null);
		
		MapsComparator mc = new MapsComparator();
		mc.setConfiguration(config);
		mc.init();
		mc.compute();
	}
}
