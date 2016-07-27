package org.gcube.dataanalysis.geo.test.maps;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.MapsComparator;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;

public class TestMapsComparisonAquaMapsvsFAO {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
		String  layertitle = "FAO aquatic species distribution map of Eleutheronema tetradactylum";
		String  layertitle2 = "Eleutheronema tetradactylum";
		
		/*
		 * {MEAN=0.81, VARIANCE=0.02, NUMBER_OF_ERRORS=6691, NUMBER_OF_COMPARISONS=259200, 
		 * ACCURACY=97.42, 
		 * MAXIMUM_ERROR=1.0, MAXIMUM_ERROR_POINT=3005:363:1, 
		 * COHENS_KAPPA=0.218, 
		 * COHENS_KAPPA_CLASSIFICATION_LANDIS_KOCH=Fair, 
		 * COHENS_KAPPA_CLASSIFICATION_FLEISS=Marginal, 
		 * TREND=EXPANSION, 
		 * Resolution=0.5}
		 */
		
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
