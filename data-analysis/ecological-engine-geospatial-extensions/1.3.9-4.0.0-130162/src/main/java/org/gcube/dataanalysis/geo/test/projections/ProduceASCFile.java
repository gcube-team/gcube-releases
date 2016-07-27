package org.gcube.dataanalysis.geo.test.projections;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.asc.AscRasterWriter;
import org.gcube.dataanalysis.geo.connectors.table.Table;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;
import org.gcube.dataanalysis.geo.utils.MapUtils;
import org.gcube.dataanalysis.geo.utils.VectorOperations;

public class ProduceASCFile {

	static String layer = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/ows?service=wfs&version=1.0.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&TYPENAME=aquamaps:worldborders";
	
//	static String layer = "ed8f77bd-2423-4036-b34d-2f1cb5fcaffc";
//	static String layer = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/ows?service=wfs&version=1.0.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&TYPENAME=aquamaps:eezall";
//	static String layer = "http://geo.vliz.be/geoserver/MarineRegions/ows?service=wfs&version=1.0.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&typename=MarineRegions:eez";
//	static String layer = "aeabfdb5-9ddb-495e-b628-5b7d2cf1d8a2";
	
	static String field = "f_cat";
//	static String field = "eez_id";
//	static String field = "f_eezall";
//	static String field = "f_eez_id";
//	static String field = "f_zone";
	
	static double res = 0.3;
	static String table = "testextraction4";
	static String scope = "/gcube/devsec/devVRE";
	static String databaseUser = "gcube";
	static String databasePwd = "d4science2";
	static String databaseURL = "jdbc:postgresql://localhost/testdb";
	static String databaseDriver = "org.postgresql.Driver";
	static double xll = -180;
	static double yll=-90;
	static double xur=180;
	static double yur=90;
	
	static String outASCIIMAP = "producedmap.txt";
	static String outASCFile = "produced.asc";
	
	private static AlgorithmConfiguration XYExtractionConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setAgent("XYEXTRACTOR");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", databaseUser);
		config.setParam("DatabasePassword", databasePwd);
		config.setParam("DatabaseURL", databaseURL);
		config.setParam("DatabaseDriver", databaseDriver);
		config.setGcubeScope(scope);

		config.setParam("Layer", layer);

		config.setParam("Z", "0");
		config.setParam("TimeIndex", "0");
		config.setParam("BBox_LowerLeftLat", ""+yll);
		config.setParam("BBox_LowerLeftLong", ""+xll);
		config.setParam("BBox_UpperRightLat", ""+yur);
		config.setParam("BBox_UpperRightLong", ""+xur);
		config.setParam("XResolution", ""+res);
		config.setParam("YResolution", ""+res);
		config.setParam("OutputTableName", table);
		config.setParam("OutputTableLabel", table);

		return config;
	}

	private static AlgorithmConfiguration TableExtractionConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", databaseUser);
		config.setParam("DatabasePassword", databasePwd);
		config.setParam("DatabaseURL", databaseURL);
		config.setParam("DatabaseDriver", databaseDriver);
		config.setGcubeScope(scope);
		config.setParam("BBox_LowerLeftLat", ""+yll);
		config.setParam("BBox_LowerLeftLong", ""+xll);
		config.setParam("BBox_UpperRightLat", ""+xur);
		config.setParam("BBox_UpperRightLong", ""+yur);
		config.setParam("XResolution", ""+res);
		config.setParam("YResolution", ""+res);
		config.setParam("OutputTableName", table);
		config.setParam("OutputTableLabel", table);
		
		config.setParam(TableMatrixRepresentation.tableNameParameter, table);
		config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "approx_x");
		config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "approx_y");
		config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "time");
		config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, field);
		config.setParam(TableMatrixRepresentation.filterParameter, " ");
		
		return config;
	}

	public static void main(String[] args) throws Exception{
		
//		produce(XYExtractionConfig());
		AnalysisLogger.setLogger("./cfg/"+AlgorithmConfiguration.defaultLoggerFile);
		List<Tuple<Double>> tuples = VectorOperations.generateCoordinateTripletsInBoundingBox(xll,xur,yll,yur, 0, res, res);
		Table connector = new Table(TableExtractionConfig(), res);
		List<Double> values  = connector.getFeaturesInTimeInstantAndArea(null, null, 0, tuples, xll,xur,yll,yur);
		double[][] matrix = VectorOperations.vectorToMatix(values, xll,xur,yll,yur,res, res);
		System.out.println(MapUtils.globalASCIIMap(matrix));
		FileWriter fw = new FileWriter(new File(outASCIIMAP));
		fw.write(MapUtils.globalASCIIMap(matrix));
		fw.close();
		
		AscRasterWriter writer = new AscRasterWriter();
		writer.writeRasterInvertYAxis(outASCFile, matrix, xll,yll, res, "-9999");
	}

	
	public static void produce(AlgorithmConfiguration config) throws Exception {

		System.out.println("TEST 1");

		
			AnalysisLogger.getLogger().debug("Executing: "+config.getAgent());
			List<ComputationalAgent> trans = null;
			trans = TransducerersFactory.getTransducerers(config);
			trans.get(0).init();
			Regressor.process(trans.get(0));
			StatisticalType st = trans.get(0).getOutput();
			AnalysisLogger.getLogger().debug("ST:" + st);
			trans = null;
		
	}
	
}
