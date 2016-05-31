package org.gcube.dataanalysis.geo.test;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.StaticGeoChartProducer;
import org.gcube.dataanalysis.geo.algorithms.TimeGeoChartProducer;
import org.junit.Test;

public class TestGeoCharts {

	@Test
	public void testStaticChart() throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./chartstmp/");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "timeseries_id08b3abb9_c7b0_4b82_8117_64b69055416f");
		config.setParam("Longitude", "x");
		config.setParam("Latitude", "y");
		config.setParam("Quantities", "fvalue");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		StaticGeoChartProducer cscreator = new StaticGeoChartProducer();
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}

	@Test
	public void testStaticChartNoQuantities() throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./chartstmp/");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "timeseries_id08b3abb9_c7b0_4b82_8117_64b69055416f");
		config.setParam("Longitude", "x");
		config.setParam("Latitude", "y");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		StaticGeoChartProducer cscreator = new StaticGeoChartProducer();
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
	@Test
	public void testTimeChart() throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./chartstmp/");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "timeseries_idf1ae1dbe_a2b2_41d9_8e8b_30c739a47903");
		config.setParam("Longitude", "decimallongitude");
		config.setParam("Latitude", "decimallatitude");
		config.setParam("Quantities", "maxdepth");
		config.setParam("Time", "time");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		TimeGeoChartProducer cscreator = new TimeGeoChartProducer();
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
	@Test
	public void testSmallTimeChart() throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./chartstmp/");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "generic_idc3f49110_995b_45cd_9846_240f25c136be");
		config.setParam("Longitude", "decimallongitude");
		config.setParam("Latitude", "decimallatitude");
		config.setParam("Quantities", "maxdepth");
		config.setParam("Time", "eventdate");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		TimeGeoChartProducer cscreator = new TimeGeoChartProducer();
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
}
