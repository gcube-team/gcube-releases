package org.gcube.dataanalysis.geo.test.regression;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.signals.SignalProcessing;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;
import org.gcube.dataanalysis.geo.matrixmodel.ZExtractor;

public class RegressionWaterColumnTemperature {

	public static void main(String[] args) throws Exception{
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		
		String  layertitle = "6411b110-7572-457a-a662-a16e4ff09e4e";
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		ZExtractor extractor = new ZExtractor(config);
		long t0 = System.currentTimeMillis();

		double watercolumn[] = extractor.extractZ(layertitle, 0,0, 0, 100);
		
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
		System.out.println("Signal: "+watercolumn.length);
		SignalProcessing.displaySignalWithGenericTime(watercolumn, 0, 1, "signal");
		
	}
	
}
