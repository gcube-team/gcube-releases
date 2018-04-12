package org.gcube.dataanalysis.ecoengine.test.checks;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.InterpolateTables.INTERPOLATIONFUNCTIONS;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class CheckOccurrences {
	
public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(testConfigLocal5());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
}

	
	private static AlgorithmConfiguration testConfigLocal() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("BIOCLIMATE_HSPEC");
		config.setParam("HSPEC_TABLE_LIST", "hspec_validation"+AlgorithmConfiguration.getListSeparator()+"hspec_validation2");
		config.setParam("HSPEC_TABLE_NAMES", "test"+AlgorithmConfiguration.getListSeparator()+"test");
		config.setParam("Threshold", "0.5");
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal2() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("BIOCLIMATE_HCAF");
		config.setParam("HCAF_TABLE_LIST","hcaf_d"+AlgorithmConfiguration.getListSeparator()+"hcaf_d_2016_linear_01332632269756"+AlgorithmConfiguration.getListSeparator()+"hcaf_d_2016_linear_01336062995861"+AlgorithmConfiguration.getListSeparator()+"hcaf_d_2050");
		config.setParam("HCAF_TABLE_NAMES", 	"test"+AlgorithmConfiguration.getListSeparator()+"test"+AlgorithmConfiguration.getListSeparator()+"test"+AlgorithmConfiguration.getListSeparator()+"test");

		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal3() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("BIOCLIMATE_HSPEN");
		config.setParam("HSPEN_TABLE_LIST","hspen"+AlgorithmConfiguration.getListSeparator()+"hspen_2016"+AlgorithmConfiguration.getListSeparator()+"hspen_2020"+AlgorithmConfiguration.getListSeparator()+"hspen_2050");
		config.setParam("HSPEN_TABLE_NAMES", 	"test"+AlgorithmConfiguration.getListSeparator()+"test"+AlgorithmConfiguration.getListSeparator()+"test"+AlgorithmConfiguration.getListSeparator()+"test");

		return config;
	}
	
	
	private static AlgorithmConfiguration testConfigLocal4() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("HCAF_INTERPOLATION");
		
		config.setParam("FirstHCAF","hcaf_d");
		config.setParam("SecondHCAF","hcaf_d_2050");
		config.setParam("YearStart","2012");
		config.setParam("YearEnd","2050");
		config.setParam("NumberOfInterpolations","2");
		config.setParam("InterpolationFunction",INTERPOLATIONFUNCTIONS.LINEAR.name());
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal5() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("OCCURRENCES_MERGER");
		
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		
		config.setParam("longitudeColumn", "decimallongitude");
		config.setParam("latitudeColumn", "decimallatitude");
		config.setParam("recordedByColumn", "recordedby");
		config.setParam("scientificNameColumn", "scientificname");
		config.setParam("eventDateColumn", "eventdate");
		config.setParam("lastModificationColumn", "modified");
		config.setParam("rightTableName", "occurrence_species_id72d89d3d_103d_4c78_a922_a0ed620b393d");
		config.setParam("leftTableName", "occurrence_species_id72d89d3d_103d_4c78_a922_a0ed620b393d");
		config.setParam("finalTableName", "occurrence_species_merged");
		config.setParam("spatialTolerance", "0.1");
		config.setParam("confidence", "80");
		
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal6() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("OCCURRENCES_INSEAS_ONEARTH");
		
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		
		config.setParam("longitudeColumn", "decimallongitude");
		config.setParam("latitudeColumn", "decimallatitude");
		config.setParam("OccurrencePointsTableName", "occurrence_species_id72d89d3d_103d_4c78_a922_a0ed620b393d");
		config.setParam("finalTableName", "occurrence_species_seas");
		config.setParam("FilterType", "IN_THE_WATER");
//		config.setParam("FilterType", "ON_EARTH");
		
		return config;
	}

	private static AlgorithmConfiguration testConfigLocal7() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");

		
		config.setAgent("OCCURRENCES_DUPLICATES_DELETER");
		
		config.setParam("longitudeColumn", "decimallongitude");
		config.setParam("latitudeColumn", "decimallatitude");
		config.setParam("recordedByColumn", "recordedby");
		config.setParam("scientificNameColumn", "scientificname");
		config.setParam("eventDateColumn", "eventdate");
		config.setParam("lastModificationColumn", "modified");
		config.setParam("OccurrencePointsTableName", "occurrence_species_id72d89d3d_103d_4c78_a922_a0ed620b393d");
		config.setParam("finalTableName", "occurrence_species_deleted");
		config.setParam("spatialTolerance", "0.5");
		config.setParam("confidence", "80");

		return config;
	}


}
