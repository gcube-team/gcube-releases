package org.gcube.dataanalysis.geo.test.infra;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;

public class TestExtraction {

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
	
	private static AlgorithmConfiguration testXYExtractionProd() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("XYEXTRACTOR");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		config.setParam("Layer","0aac424b-5f5b-4fa6-97d6-4b4deee62b97");
		config.setParam("Z","0");
		config.setParam("TimeIndex","0");
		config.setParam("BBox_LowerLeftLat","-60");
		config.setParam("BBox_LowerLeftLong","-50");
		config.setParam("BBox_UpperRightLat","60");
		config.setParam("BBox_UpperRightLong","50");
		config.setParam("XResolution","0.5");
		config.setParam("YResolution","0.5");
		config.setParam("OutputTableName","testextractionprod");
		config.setParam("OutputTableLabel","testextractionprod");
				
		return config;
	}
	
	private static AlgorithmConfiguration testXYExtractionGeotermia() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("XYEXTRACTOR");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		config.setParam("Layer","http://repoigg.services.iit.cnr.it:8080/geoserver/IGG/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=IGG:area_temp_1000&maxFeatures=50");
		config.setParam("Z","-1000");
		config.setParam("TimeIndex","0");
		config.setParam("BBox_LowerLeftLat","34.46");
		config.setParam("BBox_LowerLeftLong","5.85");
		config.setParam("BBox_UpperRightLat","49");
		config.setParam("BBox_UpperRightLong","21.41");
		config.setParam("XResolution","0.01");
		config.setParam("YResolution","0.01");
		config.setParam("OutputTableName","testextractiongeotermia");
		config.setParam("OutputTableLabel","testextractiongeotermia");
				
		return config;
	}
	
	private static AlgorithmConfiguration testXYExtractionFAO() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("XYEXTRACTOR");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		config.setParam("Layer","20c06241-f00f-4cb0-82a3-4e5ec97a0d0a");
		config.setParam("Z","0");
		config.setParam("TimeIndex","0");
		config.setParam("BBox_LowerLeftLat","-90");
		config.setParam("BBox_LowerLeftLong","-180");
		config.setParam("BBox_UpperRightLat","90");
		config.setParam("BBox_UpperRightLong","180");
		config.setParam("XResolution","0.2");
		config.setParam("YResolution","0.2");
		config.setParam("OutputTableName","testextractionfao");
		config.setParam("OutputTableLabel","testextractionfao");
				
		return config;
	}
	
	private static AlgorithmConfiguration testXYExtractionNetCDF() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("XYEXTRACTOR");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		config.setParam("Layer","c565e32c-c5b3-4964-b44f-06dc620563e9");
		config.setParam("Z","0");
		config.setParam("TimeIndex","0");
		config.setParam("BBox_LowerLeftLat","-60");
		config.setParam("BBox_LowerLeftLong","-50");
		config.setParam("BBox_UpperRightLat","60");
		config.setParam("BBox_UpperRightLong","50");
		config.setParam("XResolution","0.5");
		config.setParam("YResolution","0.5");
		config.setParam("OutputTableName","testextraction2");
		config.setParam("OutputTableLabel","testextraction2");
				
		return config;
	}

	private static AlgorithmConfiguration testDirectExtraction() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("XYEXTRACTOR");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		config.setParam("Layer","https://dl.dropboxusercontent.com/u/12809149/geoserver-GetCoverage.image.asc");
		config.setParam("Z","0");
		config.setParam("TimeIndex","0");
		config.setParam("BBox_LowerLeftLat","-60");
		config.setParam("BBox_LowerLeftLong","-50");
		config.setParam("BBox_UpperRightLat","60");
		config.setParam("BBox_UpperRightLong","50");
		config.setParam("XResolution","0.5");
		config.setParam("YResolution","0.5");
		config.setParam("OutputTableName","testextractiondirect");
		config.setParam("OutputTableLabel","testextractiondirect");
				
		return config;
	}
	

	private static AlgorithmConfiguration testXYExtractionAquaMaps() {

		AlgorithmConfiguration config = testXYExtractionNetCDF();
		config.setParam("Layer","04e61cb8-3c32-47fe-823c-80ac3d417a0b");
		config.setParam("OutputTableName","testextractionaquamaps");
		
		return config;
	}
	
	
	private static AlgorithmConfiguration testXYExtractionTable() {

		AlgorithmConfiguration config = testXYExtractionNetCDF();
		config.setAgent("XYEXTRACTOR_TABLE");
		
		config.setParam("OutputTableName","testextractiontable");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		/*
		config.setParam(TableMatrixRepresentation.tableNameParameter, "generic_id037d302d_2ba0_4e43_b6e4_1a797bb91728");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "y");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "datetime");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "speed");
		config.setParam(TableMatrixRepresentation.filterParameter, "speed<2");
		*/
		config.setParam(TableMatrixRepresentation.tableNameParameter, "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "decimallongitude");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "decimallatitude");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, " ");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "decimallatitude");
		config.setParam(TableMatrixRepresentation.filterParameter, " ");
		
		return config;
	}
	
	private static AlgorithmConfiguration testXYExtractionTable2() {

		AlgorithmConfiguration config = testXYExtractionNetCDF();
		config.setAgent("XYEXTRACTOR_TABLE");
		
		config.setParam("OutputTableName","testextractiontable2");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		/*
		config.setParam(TableMatrixRepresentation.tableNameParameter, "generic_id037d302d_2ba0_4e43_b6e4_1a797bb91728");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "y");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "datetime");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "speed");
		config.setParam(TableMatrixRepresentation.filterParameter, "speed<2");
		*/
		config.setParam(TableMatrixRepresentation.tableNameParameter, "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "decimallongitude");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "decimallatitude");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "modified");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "decimallatitude");
		config.setParam(TableMatrixRepresentation.filterParameter, " ");
		config.setParam("Z","0");
		config.setParam("TimeIndex","1");
		
		return config;
	}	
	
}
