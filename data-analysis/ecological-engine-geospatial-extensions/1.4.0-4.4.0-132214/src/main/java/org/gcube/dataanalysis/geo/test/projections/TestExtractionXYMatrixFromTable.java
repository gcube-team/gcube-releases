package org.gcube.dataanalysis.geo.test.projections;

import java.io.File;
import java.io.FileWriter;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;
import org.gcube.dataanalysis.geo.utils.MapUtils;

public class TestExtractionXYMatrixFromTable {

	public static void sliceTableAquaMaps(AlgorithmConfiguration config) throws Exception {
		// latimeria chalumnae
		config.setParam(TableMatrixRepresentation.tableNameParameter, "testextractionaquamaps");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "approx_x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "approx_y");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "time");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "f_probability");
		config.setParam(TableMatrixRepresentation.filterParameter, "");
	}

	public static void sliceTablePhImported(AlgorithmConfiguration config) throws Exception {
		// ph
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.org/testdb");
		config.setParam(TableMatrixRepresentation.tableNameParameter, "generic_idbc699da3_a4d5_40fb_80ff_666dbf1316d5");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "y");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "time");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "fvalue");
		config.setParam(TableMatrixRepresentation.filterParameter, "");
	}

	
	public static void sliceTablePh(AlgorithmConfiguration config) throws Exception {
		// ph
		config.setParam(TableMatrixRepresentation.tableNameParameter, "testextraction");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "y");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "time");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "fvalue");
		config.setParam(TableMatrixRepresentation.filterParameter, "");
	}
	
	public static void sliceMapCreated(AlgorithmConfiguration config) throws Exception {

		config.setParam(TableMatrixRepresentation.tableNameParameter, "testextraction2");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "y");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "time");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "fvalue");
		config.setParam(TableMatrixRepresentation.filterParameter, "");
	}
	
	public static void sliceMapCreated2(AlgorithmConfiguration config) throws Exception {
		
		config.setParam(TableMatrixRepresentation.tableNameParameter, "testextraction2");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "approx_x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "approx_y");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "time");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "f_temp");
		config.setParam(TableMatrixRepresentation.filterParameter, "");
	}
	
	public static void sliceMaxEnt(AlgorithmConfiguration config) throws Exception {
		
		config.setParam(TableMatrixRepresentation.tableNameParameter, "rstrf31af9ff13de42e583327e4ca51c38ef");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "y");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "time");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "fvalue");
		config.setParam(TableMatrixRepresentation.filterParameter, "");
	}

	public static void sliceTableMapServer(AlgorithmConfiguration config) throws Exception {
		
		config.setParam(TableMatrixRepresentation.tableNameParameter, "testextraction3");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "approx_x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "approx_y");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "time");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "f_depth");
		config.setParam(TableMatrixRepresentation.filterParameter, "");
	}
	
	
	public static void main(String[] args) throws Exception {
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "gcube");
		config.setParam("DatabasePassword", "d4science2");
		config.setParam("DatabaseURL", "jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec/devVRE");
		sliceTableMapServer(config);

		double resolution = 1;
		FileWriter fw = new FileWriter(new File("maps.txt"));

		XYExtractor extractor = new XYExtractor(config);
		double[][] matrix = extractor.extractXYGrid(null, 0, -180, 180, -90, 90, 0, resolution, resolution);
		String map = MapUtils.globalASCIIMap(matrix);
		fw.write(map);

		fw.close();
		System.out.println("DONE!");
	}

}
