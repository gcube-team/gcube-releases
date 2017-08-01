package org.gcube.dataanalysis.geo.test;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;

public class TestChunkization {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
		String  layertitle = "Statistical Mean in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_annual_1deg_ENVIRONMENT_OCEANS_.nc}";
//		String  layertitle = "Mass Concentration of Chlorophyll in Sea Water in [03-30-13 01:00] (3D) {Mercator Ocean BIOMER1V1R1: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-analysis-bio-001-008-a}";
//		String  layertitle = "Objectively Analyzed Climatology in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_annual_1deg_ENVIRONMENT_OCEANS_.nc}";
		long t0 = System.currentTimeMillis();
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(cfg);
		XYExtractor intersector = new XYExtractor (config);
//		intersector.takeTimeSlice(layertitle, 0, -180, 180, -10, 10, 0, 1, 1);
//		intersector.takeTimeSlice(layertitle, 0, -10, 10, -10, 10, 0,1, 1);
		intersector.extractXYGrid(layertitle, 0, -180, 180, -90, 90, 0, 0.5, 0.5);
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
	}
}
