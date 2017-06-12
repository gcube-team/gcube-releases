package org.gcube.dataanalysis.geo.test.infra;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;

public class TestSHAPEFILEPUBLISHER {

	static AlgorithmConfiguration[] configs = { testSFImporter()};
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
	
	
	
	private static AlgorithmConfiguration testSFImporter() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("SHAPEFILE_PUBLISHER");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DBUser","postgres");
		config.setParam("DBPassword","d4science2");
		config.setParam("DBUrl","jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu:5432/timeseriesgisdb");
		config.setParam("driver","org.postgresql.Driver");
//		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		config.setParam("MapTitle","Test local shapefile");
		config.setParam("MapAbstract","A local test");
		config.setParam("ShapeFileZip","shapefiletest.zip");
		config.setParam("ShapeFileName","shapefile2.shp");
		config.setParam("Topics","test|shapefile");
		config.setParam("PublicationLevel","PUBLIC");
		config.setParam("ServiceUserName","gianpaolo.coro");
		
		
		return config;
	}


	
}
