package org.gcube.dataanalysis.geo.test.infra;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;

public class TestMaxEnt {

	static AlgorithmConfiguration[] configs = { testMaxentTemperature()};

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
	
	private static AlgorithmConfiguration testMaxentTemperature() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("MAX_ENT_NICHE_MODELLING");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		config.setParam("OutputTableName","maxenttest");
		config.setParam("OutputTableLabel","maxenttest");
		config.setParam("SpeciesName","testsspecies");

		config.setParam("OccurrencesTable","occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		config.setParam("LongitudeColumn","decimallongitude");
		config.setParam("LatitudeColumn","decimallatitude");
		config.setParam("ScientificNameColumn","scientificname");

		String sep=AlgorithmConfiguration.getListSeparator();
//		config.setParam("Layers","dfd1bad2-ab00-42ac-8bb2-46a17162f509"+sep+"23646f93-23a8-4be4-974e-aee6bebe1707");
		//config.setParam("Layers","94ea5767-ae76-41dc-be87-f9a0bdc96419");//temperature 99-09 2D
//		config.setParam("Layers","23646f93-23a8-4be4-974e-aee6bebe1707");//ph
//		config.setParam("Layers","fc9ac2f4-a2bd-43d1-a361-ac67c5ceac31");//temperature
		
		config.setParam("Layers","http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/ows?service=wfs&version=1.0.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&TYPENAME=aquamaps:worldborders");
		
		config.setParam("MaxIterations","10000");
		config.setParam("DefaultPrevalence","1");
		
		config.setParam("Z","0");
		config.setParam("TimeIndex","0");
		
		config.setParam("BBox_LowerLeftLong","-180");
		config.setParam("BBox_UpperRightLong","180");
		config.setParam("BBox_LowerLeftLat","-90");
		config.setParam("BBox_UpperRightLat","90");
		
		/*
		config.setParam("BBox_LowerLeftLong","-60");
		config.setParam("BBox_UpperRightLong","60");
		config.setParam("BBox_LowerLeftLat","-10");
		config.setParam("BBox_UpperRightLat","10");
		*/
		
//		config.setParam("XResolution","0.5");
//		config.setParam("YResolution","0.5");

		config.setParam("XResolution","1");
		config.setParam("YResolution","1");

		
		return config;
	}
	
	
}
