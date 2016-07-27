package org.gcube.dataanalysis.geo.test.maps;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.algorithms.PointsMapsCreator;
import org.gcube.dataanalysis.geo.algorithms.PolygonMapsCreator;

public class TestMapCreation {

	
	static String cfg = "./cfg/";
	public static void main1(String[] args) throws Exception{
		
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(cfg);
//		config.setGcubeScope("/gcube/devsec/statVRE");
		config.setGcubeScope("/gcube/devsec/devVRE");
		config.setPersistencePath("./");
		
		config.setParam("MapName","Test Polygonal Map Ph 6");
		/*
		config.setParam("InputTable","occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");	
		config.setParam("xDimension","decimallongitude");
		config.setParam("yDimension","decimallatitude");
		config.setParam("Info","recordedby")	;
		config.setParam("Resolution","0.5");
		*/
		config.setParam("InputTable","generic_idbc699da3_a4d5_40fb_80ff_666dbf1316d5");	
		config.setParam("xDimension","x");
		config.setParam("yDimension","y");
		config.setParam("Info","fvalue")	;
		
		

		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.org/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");

		config.setParam("Z","0");
		
		config.setParam("user", "postgres");
		config.setParam("password", "d4science2");
		config.setParam("STOREURL","jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
		config.setParam("driver", "org.postgresql.Driver");
		config.setParam("dialect", "org.hibernatespatial.postgis.PostgisDialect");
		/*
		PolygonMapsCreator mc = new PolygonMapsCreator();
		*/
		PointsMapsCreator mc = new PointsMapsCreator();
		mc.setConfiguration(config);
		mc.init();
		mc.compute();
		
	}
	
	
public static void main(String[] args) throws Exception{
		
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(cfg);
//		config.setGcubeScope("/gcube/devsec/statVRE");
		config.setGcubeScope("/gcube/devsec/devVRE");
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		
		config.setPersistencePath("./");
		
		config.setParam("MapName","Test Polygonal Map Ph 10");
		config.setParam("PublicationLevel","PRIVATE");
		/*
		config.setParam("InputTable","occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");	
		config.setParam("xDimension","decimallongitude");
		config.setParam("yDimension","decimallatitude");
		config.setParam("Info","recordedby")	;
		config.setParam("Resolution","0.5");
		*/
		/*
		config.setParam("InputTable","testextraction2");	
		config.setParam("xDimension","x");
		config.setParam("yDimension","y");
		config.setParam("Info","fvalue")	;
		*/
		
		config.setParam("InputTable","code_85e5d927f7094a3ca677a53f4433fed4");	
		config.setParam("xDimension","longitude");
		config.setParam("yDimension","latitude");
		config.setParam("Info","longitude")	;
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.org/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
/*
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
*/
		config.setParam("Z","0");
		
		config.setParam("user", "postgres");
		config.setParam("password", "d4science2");
		config.setParam("STOREURL","jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
		config.setParam("driver", "org.postgresql.Driver");
		config.setParam("dialect", "org.hibernatespatial.postgis.PostgisDialect");
		/*
		PolygonMapsCreator mc = new PolygonMapsCreator();
		*/
		PointsMapsCreator mc = new PointsMapsCreator();
		mc.setConfiguration(config);
		mc.init();
		mc.compute();
		
	}

}
