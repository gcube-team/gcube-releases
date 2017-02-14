package org.gcube.dataanalysis.geo.test.infra;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;

public class TestESRIGRIDExtraction {

//	static AlgorithmConfiguration[] configs = { testXYExtractionNetCDF(),testXYExtractionAquaMaps(),testXYExtractionTable(),testXYExtractionTable2(),testDirectExtraction()};
//	static AlgorithmConfiguration[] configs = { testXYExtractionTable2()};
//	static AlgorithmConfiguration[] configs = { testDirectExtraction()};
//	static AlgorithmConfiguration[] configs = { testXYExtractionAquaMaps()};
//	static AlgorithmConfiguration[] configs = { testXYExtractionGeotermia()};
//	static AlgorithmConfiguration[] configs = { testXYExtractionFAO()};
	static AlgorithmConfiguration[] configs = { testXYExtractionNetCDF()};
	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");

		for (int i = 0; i < configs.length; i++) {
			AnalysisLogger.getLogger().debug("Executing: "+configs[i].getAgent());
			List<ComputationalAgent> trans = null;
			trans = TransducerersFactory.getTransducerers(configs[i]);
			trans.get(0).init();
			Regressor.process(trans.get(0));
			StatisticalType st = trans.get(0).getOutput();
			AnalysisLogger.getLogger().debug("ST:" + st);
			trans = null;
		}
	}
	
	
	
	private static AlgorithmConfiguration testXYExtractionNetCDF() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("ESRI_GRID_EXTRACTION");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
//		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		config.setParam("Layer","fc9ac2f4-a2bd-43d1-a361-ac67c5ceac31");
		config.setParam("Z","0");
		config.setParam("TimeIndex","0");
		config.setParam("BBox_LowerLeftLat","-60");
		config.setParam("BBox_LowerLeftLong","-50");
		config.setParam("BBox_UpperRightLat","60");
		config.setParam("BBox_UpperRightLong","50");
		config.setParam("XResolution","1");
		config.setParam("YResolution","1");
				
		return config;
	}


	
}
