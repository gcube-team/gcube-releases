package org.gcube.dataanalysis.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.fin.taxamatch.TaxaMatchListTransducer.operators;

public class TestFinTaxaMatchList {

	
public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(testConfig());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		StatisticalType st = trans.get(0).getOutput();
		trans = null;
}
	
	private static AlgorithmConfiguration testConfig() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		
		String databaseURL = "//biodiversity.db.i-marine.research-infrastructures.eu/fishbase";
		String databaseUser = "postgres";
		String databasePwd = "0b1s@d4sc13nc3";
		
		config.setParam("FishBase", databaseURL);
		config.setParam("user", databaseUser);
		config.setParam("password", databasePwd);
		
		
		config.setAgent("FIN_TAXA_MATCH_LIST");
		
		config.setParam("ComparisonOperatorforGenus", "EQUAL");
		config.setParam("ComparisonOperatorforSpecies", "EQUAL");
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		config.setParam("TaxaTable","generic_id1098fa80_aa83_4441_8ff1_28c4b8e09630");
		config.setParam("TaxaColumns","genus"+AlgorithmConfiguration.listSeparator+"species");
		String tablename = "testtaxa"+(UUID.randomUUID());
		
		config.setParam("OutputTableName","Test Casey");
		config.setParam("OutputTable", tablename.replace("-", ""));
		
		return config;
	}

	
}
