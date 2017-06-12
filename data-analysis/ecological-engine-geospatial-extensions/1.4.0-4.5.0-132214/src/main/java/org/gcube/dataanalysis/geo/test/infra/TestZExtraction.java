package org.gcube.dataanalysis.geo.test.infra;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;

public class TestZExtraction {

//	static AlgorithmConfiguration[] configs = { testTimeExtractionTable(), testZExtractionLongNetCDF(),testZExtractionNetCDF(),testTimeExtractionTable2()};
	static AlgorithmConfiguration[] configs = { testZExtractionAquamaps()};
	
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
	
	private static AlgorithmConfiguration testZExtractionAquamaps() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("ZEXTRACTION");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		config.setParam("Layer","40198411-9ceb-420f-8f39-a7e1b8128d6b");
		
		config.setParam("OutputTableName","testzextractionaquamaps");
		config.setParam("OutputTableLabel","testzextractionaquamaps");
		
		config.setParam("TimeIndex","0");
		config.setParam("X","121");
		config.setParam("Y","-4");
		config.setParam("Resolution","0.5");
		
		return config;
	}
	
	private static AlgorithmConfiguration testZExtractionLongNetCDF() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("ZEXTRACTION");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		config.setParam("Layer","6411b110-7572-457a-a662-a16e4ff09e4e");
//		config.setParam("Layer","dffa504b-dbc8-4553-896e-002549f8f5d3");
		
		config.setParam("OutputTableName","testzextractionlong");
		config.setParam("OutputTableLabel","testzextractionlong");
		
		config.setParam("TimeIndex","0");
		config.setParam("X","0");
		config.setParam("Y","0");
		config.setParam("Resolution","0.5");
		
		return config;
	}

	private static AlgorithmConfiguration testZExtractionNetCDF() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("ZEXTRACTION");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec");
		
		config.setParam("Layer","7f90e153-0c5c-4d45-a498-a6374593e68d");
		
		config.setParam("OutputTableName","testzextractionstandard");
		config.setParam("OutputTableLabel","testzextractionstandard");
		
		config.setParam("TimeIndex","0");
		config.setParam("X","0");
		config.setParam("Y","0");
		config.setParam("Resolution","100");
		
		return config;
	}
	
	private static AlgorithmConfiguration testTimeExtractionTable() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("TIMEEXTRACTION");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		config.setParam("Layer","c565e32c-c5b3-4964-b44f-06dc620563e9");
		
		config.setParam("OutputTableName","testtimeextraction");
		config.setParam("OutputTableLabel","testtimeextraction");
		
		config.setParam("Z","0");
		config.setParam("X","-47.97"); 
		config.setParam("Y","43.42");
		config.setParam("Resolution","0.5");
		config.setParam("SamplingFreq","-1");
		config.setParam("MinFrequency","-1");
		config.setParam("MaxFrequency","-1");
		config.setParam("FrequencyError","-1");
		
		config.setParam(TableMatrixRepresentation.tableNameParameter, "generic_id037d302d_2ba0_4e43_b6e4_1a797bb91728");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "y");

		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "datetime");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "speed");
		config.setParam(TableMatrixRepresentation.filterParameter, "speed<2");
		
		return config;
	}

	
	private static AlgorithmConfiguration testTimeExtractionTable2() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("TIMEEXTRACTION");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		config.setParam("OutputTableName","testtimeextraction2");
		config.setParam("OutputTableLabel","testtimeextraction2");
		
		config.setParam("Z","0");
		config.setParam("X","18.61669921875"); 
		config.setParam("Y","-34.1833000183105");
		config.setParam("Resolution","10");
		config.setParam("SamplingFreq","-1");
		config.setParam("MinFrequency","-1");
		config.setParam("MaxFrequency","-1");
		config.setParam("FrequencyError","-1");
		
		config.setParam(TableMatrixRepresentation.tableNameParameter, "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "decimallongitude");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "decimallatitude");

		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "eventdate");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "decimallongitude");
		config.setParam(TableMatrixRepresentation.filterParameter, " ");
		
		return config;
	}
	

}
