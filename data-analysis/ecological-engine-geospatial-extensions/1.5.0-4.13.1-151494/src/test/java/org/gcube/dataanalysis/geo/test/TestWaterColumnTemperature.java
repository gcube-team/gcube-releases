package org.gcube.dataanalysis.geo.test;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.signals.SignalProcessing;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;
import org.gcube.dataanalysis.geo.matrixmodel.ZExtractor;

public class TestWaterColumnTemperature {

	public static void main(String[] args) throws Exception{
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		
		String  layertitle = "6411b110-7572-457a-a662-a16e4ff09e4e";
		/*
		layertitle = "be24800d-7583-4efa-b925-e0d8760e0fd3";
		layertitle = "320652c8-e986-4428-9306-619d9014822a";
		layertitle = "0aac424b-5f5b-4fa6-97d6-4b4deee62b97";
		*/
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
