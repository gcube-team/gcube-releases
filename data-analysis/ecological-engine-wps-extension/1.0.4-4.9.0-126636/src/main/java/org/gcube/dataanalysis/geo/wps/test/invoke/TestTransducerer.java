package org.gcube.dataanalysis.geo.wps.test.invoke;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.InterpolateTables.INTERPOLATIONFUNCTIONS;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestTransducerer {
	
public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(testConfigLocal());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
}

	private static AlgorithmConfiguration testConfigLocal() {
		
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
		config.setParam("OccurrencePointsTableName", "whitesharkoccurrences2");
		config.setParam("finalTableName", "whitesharkoccurrencesnoduplicates");
		config.setParam("spatialTolerance", "0.5");
		config.setParam("confidence", "80");

		return config;
	}

	private static AlgorithmConfiguration testConfigWPS() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setGcubeScope("/gcube");
		
		config.setAgent("com.terradue.wps_hadoop.processes.examples.async.Async");
		config.setParam("secondsDelay", "1");
		return config;
	}

	
	private static AlgorithmConfiguration testSpread() {
//		dataInputs=geoColumn=field0;quantityColumn=field4;sourceAreaLayerName=FAO_AREAS;targetAreaLayerName=EEZ_HIGHSEAS;dataUrls=https://dl.dropboxusercontent.com/u/24368142/timeseries_100.json;&ResponseDocument=result
			
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setGcubeScope("/gcube");
		config.setAgent("com.terradue.wps_hadoop.processes.fao.spread.Spread");
		config.setParam("geoColumn", "field0");
		config.setParam("quantityColumn", "field4");
		config.setParam("sourceAreaLayerName", "FAO_AREAS");
		config.setParam("targetAreaLayerName", "EEZ_HIGHSEAS");
		config.setParam("dataUrls", "https://dl.dropboxusercontent.com/u/24368142/timeseries_100.json");

		return config;
	}
	
	private static AlgorithmConfiguration testTunaAtlas1() {
		/*
		"YFT,Thunnus albacares,Albacore,Rabil,Yellowfin tuna",
		  "SKJ,Katsuwonus pelamis,Listao,Listado,Ocean skipjack",
		  "BET,Thunnus obesus,Thon obese,Patudo,Bigeye tuna",
		  "ALB,Thunnus alalunga,Germon,Atun blanco,Albacore",
		  "BFT,Thunnus thynnus thynnus,Thon rouge,Atun rojo,Bluefin tuna",
		  "SBF,Thunnus maccoyii,Thon rouge du sud,Atun rojo del sur,Southern bluefin tuna",
		  "SFA,Istiophorus platypterus,Voilier Indo-Pacifique,Pez vela del Indo-Pacifico,Indo-Pacific sailfish",
		  "BLM,Makaira indica,Makaire noir,Aguja negra,Black marlin",
		  "BUM,Makaira nigricans,Makaire bleu Atlantique,Aguja azul,Atlantic blue marlin",
		  "MLS,Tetrapturus audax,Marlin raye,Marlin rayado,Striped marlin",
		  "BIL,Istiophoridae spp.,Poissons a rostre non classes,,Unclassified marlin",
		  "SWO,Xiphias gladius,Espadon,Pez espada,Broadbill swordfish",
		  "SSP,Tetrapturus angustirostris,Makaire a rostre court,Marlin trompa corta,short-billed spearfish",
		  */
			
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setGcubeScope("/gcube");
		config.setAgent("com.terradue.wps_hadoop.processes.ird.indicator.IndicatorI1");
		config.setParam("species", "YFT");
//		http://mdst-macroes.ird.fr:8080/constellation/WS/wfs/tuna_atlas?service=wfs&request=getcapabilities
		return config;
	}
	
}
