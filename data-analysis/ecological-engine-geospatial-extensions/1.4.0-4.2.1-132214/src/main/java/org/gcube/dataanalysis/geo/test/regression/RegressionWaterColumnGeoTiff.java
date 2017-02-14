package org.gcube.dataanalysis.geo.test.regression;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.signals.SignalProcessing;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;
import org.gcube.dataanalysis.geo.matrixmodel.ZExtractor;

public class RegressionWaterColumnGeoTiff {

	public static void main(String[] args) throws Exception{
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		
		String  layertitle = "WorldClimBioGeoTiffTest2";
//		layertitle = "WorldClimBioWCS2";
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		ZExtractor extractor = new ZExtractor(config);
		long t0 = System.currentTimeMillis();

		double watercolumn[] = extractor.extractZ(layertitle, 18.620429d,20.836419d,0, 0);
		
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
		System.out.println("Signal: "+watercolumn.length);
		System.out.println("Signal first element: "+watercolumn[0]);
		SignalProcessing.displaySignalWithGenericTime(watercolumn, 0, 1, "signal");

		layertitle = "WorldClimBioWCS2";

		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		extractor = new ZExtractor(config);
		t0 = System.currentTimeMillis();

		watercolumn = extractor.extractZ(layertitle, 18.620429d,20.836419d,0, 0);
		
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
		System.out.println("Signal: "+watercolumn.length);
		System.out.println("Signal first element: "+watercolumn[0]);
		SignalProcessing.displaySignalWithGenericTime(watercolumn, 0, 1, "signal");

		
	}
	
}
