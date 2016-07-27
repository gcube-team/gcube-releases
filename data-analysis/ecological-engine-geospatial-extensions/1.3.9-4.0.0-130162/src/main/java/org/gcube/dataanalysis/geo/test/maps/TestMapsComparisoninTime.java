package org.gcube.dataanalysis.geo.test.maps;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.MapsComparator;

public class TestMapsComparisoninTime {

	static String cfg = "./cfg/";
	
	public static void main(String[] args) throws Exception{
		
		int[] timeIdx = {0,12,24,36,48,60,72,84,96,108,120}; 
		
		for (int i=1;i<timeIdx.length;i++){
			compare(timeIdx[i-1], timeIdx[i]);
		}
		
	}
	
	
	public static void compare(int t1, int t2) throws Exception{
		String  layertitle = "Temperature from [12-15-99 01:00] to [12-15-09 01:00] (2D) {Native grid ORCA025.L75 monthly average: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-reanalysis-phys-001-004-b-ref-fr-mjm95-gridt}";
		
		//1vs0: {MEAN=6.23, VARIANCE=30.58, NUMBER_OF_ERRORS=39650, NUMBER_OF_COMPARISONS=522242, ACCURACY=92.41, MAXIMUM_ERROR=45.35, MAXIMUM_ERROR_POINT=3215:143, Resolution=0.3525954946131244}

/*
**********(12->0) {MEAN=0.53, VARIANCE=0.28, NUMBER_OF_ERRORS=36075, NUMBER_OF_COMPARISONS=522242, ACCURACY=93.09, MAXIMUM_ERROR=6.0, MAXIMUM_ERROR_POINT=7309:456, TREND=EXPANSION, Resolution=0.3525954946131244} ELAPSED: 1370363639187
**********(24->12) {MEAN=0.56, VARIANCE=0.3, NUMBER_OF_ERRORS=36053, NUMBER_OF_COMPARISONS=522242, ACCURACY=93.1, MAXIMUM_ERROR=6.95, MAXIMUM_ERROR_POINT=1313:143, TREND=EXPANSION, Resolution=0.3525954946131244} ELAPSED: 1370363722843 Japan
**********(36->24) {MEAN=0.62, VARIANCE=0.33, NUMBER_OF_ERRORS=35744, NUMBER_OF_COMPARISONS=522242, ACCURACY=93.16, MAXIMUM_ERROR=6.87, MAXIMUM_ERROR_POINT=1314:465, TREND=EXPANSION, Resolution=0.3525954946131244} ELAPSED: 1370363798387
**********(48->36) {MEAN=0.49, VARIANCE=0.22, NUMBER_OF_ERRORS=35664, NUMBER_OF_COMPARISONS=522242, ACCURACY=93.17, MAXIMUM_ERROR=7.54, MAXIMUM_ERROR_POINT=7307:456, TREND=CONTRACTION, Resolution=0.3525954946131244} ELAPSED: 1370363875063 North Carolina
**********(60->48) {MEAN=0.46, VARIANCE=0.23, NUMBER_OF_ERRORS=36133, NUMBER_OF_COMPARISONS=522242, ACCURACY=93.08, MAXIMUM_ERROR=5.42, MAXIMUM_ERROR_POINT=7307:456, TREND=EXPANSION, Resolution=0.3525954946131244} ELAPSED: 1370363953390
**********(72->60) {MEAN=0.56, VARIANCE=0.31, NUMBER_OF_ERRORS=35970, NUMBER_OF_COMPARISONS=522242, ACCURACY=93.11, MAXIMUM_ERROR=7.01, MAXIMUM_ERROR_POINT=7307:236, TREND=EXPANSION, Resolution=0.3525954946131244} ELAPSED: 1370364033154
**********(84->72) {MEAN=0.57, VARIANCE=0.31, NUMBER_OF_ERRORS=36148, NUMBER_OF_COMPARISONS=522242, ACCURACY=93.08, MAXIMUM_ERROR=7.02, MAXIMUM_ERROR_POINT=7307:247, TREND=CONTRACTION, Resolution=0.3525954946131244} ELAPSED: 1370364110444
**********(96->84) {MEAN=0.59, VARIANCE=0.32, NUMBER_OF_ERRORS=35873, NUMBER_OF_COMPARISONS=522242, ACCURACY=93.13, MAXIMUM_ERROR=5.23, MAXIMUM_ERROR_POINT=7306:249, TREND=CONTRACTION, Resolution=0.3525954946131244} ELAPSED: 1370364190900
**********(108->96) {MEAN=0.53, VARIANCE=0.27, NUMBER_OF_ERRORS=35789, NUMBER_OF_COMPARISONS=522242, ACCURACY=93.15, MAXIMUM_ERROR=4.96, MAXIMUM_ERROR_POINT=7306:249, TREND=EXPANSION, Resolution=0.3525954946131244} ELAPSED: 1370364272133
**********(120->108) {MEAN=0.62, VARIANCE=0.37, NUMBER_OF_ERRORS=36194, NUMBER_OF_COMPARISONS=522242, ACCURACY=93.07, MAXIMUM_ERROR=5.51, MAXIMUM_ERROR_POINT=1316:352, TREND=EXPANSION, Resolution=0.3525954946131244} pacific ocean
  
 */
		long t0=0;
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(cfg);
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setParam("Layer_1",layertitle);
		config.setParam("Layer_2",layertitle);
		config.setParam("TimeIndex_1",""+t1);
		config.setParam("TimeIndex_2",""+t2);
		config.setParam("ValuesComparisonThreshold","0.01");
		config.setParam("Z","0");
		config.setGcubeScope(null);
		
		MapsComparator mc = new MapsComparator();
		mc.setConfiguration(config);
		mc.init();
		mc.compute();
		mc.getOutput();
		System.out.println("*********("+t2+"->"+t1+") "+mc.outputParameters +" ELAPSED: "+(System.currentTimeMillis()-t0));
		
	}
}
