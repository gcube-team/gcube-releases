package org.gcube.dataanalysis.geo.test.infra;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;

public class TestOccurrenceEnrichment {

//	static AlgorithmConfiguration[] configs = { testOccEnrichment(), testOccEnrichmentWFS()};
	static AlgorithmConfiguration[] configs = { testOccEnrichmentWFSFAO()};
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
	

	private static AlgorithmConfiguration testOccEnrichment() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("OCCURRENCE_ENRICHMENT");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		
		config.setParam("OccurrenceTable","occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		config.setParam("LongitudeColumn","decimallongitude");
		config.setParam("LatitudeColumn","decimallatitude");
		config.setParam("ScientificNameColumn","scientificname");
		config.setParam("TimeColumn","eventdate");
		config.setParam("OptionalFilter","");
		config.setParam("Resolution","0.5");
		config.setParam("OutputTableDBName","testenrichment");
		config.setParam("OutputTableName","testenrichment");
		String sep=AlgorithmConfiguration.getListSeparator();
//		config.setParam("Layers","http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/global-reanalysis-phys-001-004-b-ref-fr-mjm95-gridt__ENVIRONMENT_OCEANS_ELEVATION_1366210702774.nc"+sep+"4d597da9-dbfa-4a65-9de6-9bbff69eac19"+sep+"2c2304d1-681a-4f3a-8409-e8cdb5ed447f");
		//ASC file
		config.setParam("Layers","http://goo.gl/s6fOfS");
		config.setParam("Layers","https://dl.dropboxusercontent.com/u/12809149/test1.tiff");
		
		
//		config.setParam("Layers","https://dl.dropboxusercontent.com/u/12809149/geoserver-GetCoverage.image.asc");
		//NETCDF http - cannot work
//		config.setParam("Layers","http://goo.gl/qXtqiY");
//		config.setParam("Layers","https://dl.dropboxusercontent.com/u/12809149/geoserver-GetCoverage.image.tiff");
//		config.setParam("Layers","https://dl.dropboxusercontent.com/u/12809149/TrueMarble.tif");
//		config.setParam("Layers","http://goo.gl/l4tEmd");
		
			
//		config.setParam("Layers","8f5d883f-95bf-4b7c-8252-aaf0b2e6fd81"+sep+"4d597da9-dbfa-4a65-9de6-9bbff69eac19"+sep+"2c2304d1-681a-4f3a-8409-e8cdb5ed447f");
		config.setParam("FeaturesNames","temperature"+sep+"chlorophyll"+sep+"ph");
//		config.setParam("Layers","4d597da9-dbfa-4a65-9de6-9bbff69eac19"+sep+"2c2304d1-681a-4f3a-8409-e8cdb5ed447f");
//		config.setParam("FeaturesNames","chlorophyll"+sep+"ph");
		return config;
	}
	
	private static AlgorithmConfiguration testOccEnrichmentWFSFAO() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("OCCURRENCE_ENRICHMENT");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		config.setParam("OccurrenceTable","occurrence_carch");
		config.setParam("LongitudeColumn","decimallongitude");
		config.setParam("LatitudeColumn","decimallatitude");
		config.setParam("ScientificNameColumn","scientificname");
		config.setParam("TimeColumn","eventdate");
		config.setParam("OptionalFilter","");
		config.setParam("Resolution","0.5");
		config.setParam("OutputTableDBName","testenrichmentwpsfao");
		config.setParam("OutputTableName","testenrichmentwpsfao");
		String sep=AlgorithmConfiguration.getListSeparator();

		//WFS: carcharodon
		config.setParam("Layers","	fao-species-map-wsh");
		
		
		return config;
	}
	
	private static AlgorithmConfiguration testOccEnrichmentWFS() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("OCCURRENCE_ENRICHMENT");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		config.setParam("OccurrenceTable","occurrence_carch");
		config.setParam("LongitudeColumn","decimallongitude");
		config.setParam("LatitudeColumn","decimallatitude");
		config.setParam("ScientificNameColumn","scientificname");
		config.setParam("TimeColumn","eventdate");
		config.setParam("OptionalFilter","");
		config.setParam("Resolution","0.5");
		config.setParam("OutputTableDBName","testenrichmentwps");
		config.setParam("OutputTableName","testenrichmentwps");
		String sep=AlgorithmConfiguration.getListSeparator();

		//WFS: carcharodon
		config.setParam("Layers","b8a17d86-c62f-4e73-b5c9-bdb3366015c9");
		
		
		return config;
	}
	
}
