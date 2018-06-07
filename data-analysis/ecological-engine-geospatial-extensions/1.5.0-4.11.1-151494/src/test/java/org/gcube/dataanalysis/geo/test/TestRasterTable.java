package org.gcube.dataanalysis.geo.test;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;

public class TestRasterTable {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
//		String  layertitle = "MyDistributionMap";
//		String  layertitle = "Mass Concentration of Chlorophyll in Sea Water in [03-30-13 01:00] (3D) {Mercator Ocean BIOMER1V1R1: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-analysis-bio-001-008-a}";
//		String  layertitle = "Objectively Analyzed Climatology in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_annual_1deg_ENVIRONMENT_OCEANS_.nc}";
		String  layertitle = "FAO AQUATIC SPECIES DISTRIBUTION MAP OF MEGALASPIS CORDYLA";
		long t0 = System.currentTimeMillis();
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(cfg);
		config.setPersistencePath("./");

		/*
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.next.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		*/
		
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		XYExtractor  intersector = new XYExtractor (config);
		int t = 0;
		double x1 = -180;
		double x2 = 180;
		double y1 = -90;
		double y2 = 90;
		double z = 0;
		double xResolution = 0.5;
		double yResolution = 0.5;
		
		double[][] slice = intersector.extractXYGrid(layertitle, t, x1, x2, y1,y2,z,xResolution,yResolution);
		
		RasterTable raster = new RasterTable(x1, x2, y1, y2, z, xResolution, yResolution, slice, config);
		raster.dumpGeoTable();
		
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
	}
}
