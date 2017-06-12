package org.gcube.dataanalysis.geo.test.regression;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;

public class RegressionXYSlice {

	static String cfg = "./cfg/";
	static String layertitle = "120313e1-c0cb-4b3c-9779-ed651c490cdb";
	static AlgorithmConfiguration config = new AlgorithmConfiguration(); 
	public static void main(String[] args) throws Exception {
		config.setConfigPath(cfg);
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		AnalysisLogger.setLogger(cfg + AlgorithmConfiguration.defaultLoggerFile);
		config.setPersistencePath("./");
		
//		sliceWFS();
//		sliceNetCDF();
		sliceASC();
//		sliceTable();
	}

	public static void sliceWFS() throws Exception{
		AnalysisLogger.getLogger().debug("WFS");
		// latimeria chalumnae
		layertitle = "120313e1-c0cb-4b3c-9779-ed651c490cdb";
		execute();
	}

	public static void sliceNetCDF() throws Exception{
		AnalysisLogger.getLogger().debug("NetCDF");
		// Chlorophyll
		layertitle = "c565e32c-c5b3-4964-b44f-06dc620563e9";
		execute();
	}
	
	public static void sliceASC() throws Exception{
		AnalysisLogger.getLogger().debug("ASC");
		//
		layertitle = "2c2304d1-681a-4f3a-8409-e8cdb5ed447f";
		execute();
	}
	
	public static void sliceTable() throws Exception{
		AnalysisLogger.getLogger().debug("Table");
		// latimeria chalumnae
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam(TableMatrixRepresentation.tableNameParameter, "generic_id037d302d_2ba0_4e43_b6e4_1a797bb91728");
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "y");
//		config.setParam(TableMatrixRepresentation.zDimensionColumnParameter, "x");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "datetime");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "speed");
		config.setParam(TableMatrixRepresentation.filterParameter, "speed<2");
		
		execute();
	}

	public static void execute() throws Exception{
		long t0 = System.currentTimeMillis();
		XYExtractor intersector = new XYExtractor(config);
		intersector.extractXYGrid(layertitle, 0, -180, 180, -90, 90, 0, 0.5, 0.5);
		System.out.println("ELAPSED TIME: " + (System.currentTimeMillis() - t0));
	}
}
