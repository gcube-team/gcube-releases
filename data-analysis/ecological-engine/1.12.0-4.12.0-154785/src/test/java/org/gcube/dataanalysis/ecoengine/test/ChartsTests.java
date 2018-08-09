package org.gcube.dataanalysis.ecoengine.test;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.transducers.charts.QuantitiesAttributesChartsTransducerer;
import org.gcube.dataanalysis.ecoengine.transducers.charts.TimeSeriesChartsTransducerer;
import org.junit.Test;

public class ChartsTests {
	
	@Test
	public void testSmallGeneric() throws Exception{
		
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "hspen_mini");
		config.setParam("Attributes", "speciesid#lifestage#faoareas");
		config.setParam("Quantities", "depthmax#speccode");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		QuantitiesAttributesChartsTransducerer cscreator = new QuantitiesAttributesChartsTransducerer();
		cscreator.displaycharts=true;
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
	@Test
	public void testLargeGeneric() throws Exception{
		
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "generic_id6ef3e4fa_6a06_4df1_9445_553f2e918102");
		config.setParam("Attributes", "long#lat");
		config.setParam("Quantities", "long");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		QuantitiesAttributesChartsTransducerer cscreator = new QuantitiesAttributesChartsTransducerer();
		cscreator.displaycharts=true;
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
	@Test
	public void testStrangeGeneric() throws Exception{
		
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "generic_id0746f5ab_fb3e_4848_97cd_43f46ae57ac1");
		config.setParam("Attributes", "time#quantity");
		config.setParam("Quantities", "quantity");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		QuantitiesAttributesChartsTransducerer cscreator = new QuantitiesAttributesChartsTransducerer();
		cscreator.displaycharts=true;
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
		@Test
		public void testLonLatDataset() throws Exception{
			
			AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
			AlgorithmConfiguration config = new AlgorithmConfiguration();

			config.setConfigPath("./cfg/");
			config.setPersistencePath("./");
			config.setParam("DatabaseUserName", "utente");
			config.setParam("DatabasePassword", "d4science");
			config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
			config.setParam("DatabaseDriver", "org.postgresql.Driver");

			config.setParam("InputTable", "csq_84e9302c161243a3b29f3eff9c392d3e");
			config.setParam("Attributes", "field1");
			config.setParam("Quantities", "field2");
			
			config.setGcubeScope("/gcube/devsec/devVRE");
			
			QuantitiesAttributesChartsTransducerer cscreator = new QuantitiesAttributesChartsTransducerer();
			cscreator.displaycharts=true;
			cscreator.setConfiguration(config);
			cscreator.compute();
			
			System.out.println("DONE! "+cscreator.getOutput());
		}
	 
public static void main(String[] args) throws Exception{
		
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "generic_id0db6e87b_abd6_4dfc_aa05_208eab3df212");
		config.setParam("Attributes", "decimallongitude#decimallatitude");
		config.setParam("Quantities", "chlorophyll");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		QuantitiesAttributesChartsTransducerer cscreator = new QuantitiesAttributesChartsTransducerer();
		cscreator.displaycharts=true;
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}

	public static void mainTimeSeriesMedium(String[] args) throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "timeseries_id4dd368bf_63fb_4d19_8e31_20ced63a477d");
		config.setParam("Attributes", "country#area");
//		config.setParam("Attributes", "");
		config.setParam("Quantities", "quantity");
		config.setParam("Time", "time");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		TimeSeriesChartsTransducerer cscreator = new TimeSeriesChartsTransducerer();
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
	public static void main1(String[] args) throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "generic_idc3f49110_995b_45cd_9846_240f25c136be");
//		config.setParam("Attributes", "decimallatitude#decimallongitude#basisofrecord");
//		config.setParam("Attributes", "");
		config.setParam("Quantities", "maxdepth");
		config.setParam("Time", "eventdate");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		TimeSeriesChartsTransducerer cscreator = new TimeSeriesChartsTransducerer();
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
	@Test
	public void testTimeSeriesSuperposed() throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "generic_id3249137c_1559_438c_857b_29942cb13118");
		config.setParam("Attributes", "latdecdeg#longdecdeg");
		config.setParam("Quantities", "specific_a");
		config.setParam("Time", "begperiod");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		TimeSeriesChartsTransducerer cscreator = new TimeSeriesChartsTransducerer();
		cscreator.displaycharts=true;
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}

	@Test
	public void testTimeSeriesLongFAO() throws Exception{
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		config.setParam("InputTable", "generic_id0746f5ab_fb3e_4848_97cd_43f46ae57ac1");
		config.setParam("Attributes", "catch#country#suggested_country");
		config.setParam("Quantities", "quantity");
		config.setParam("Time", "time");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		TimeSeriesChartsTransducerer cscreator = new TimeSeriesChartsTransducerer();
		cscreator.displaycharts=true;
		cscreator.setConfiguration(config);
		cscreator.compute();
		
		System.out.println("DONE! "+cscreator.getOutput());
	}
	
	
}
