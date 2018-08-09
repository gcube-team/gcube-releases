package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.test;

import java.io.File;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure.DatabaseInfo;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure.InfrastructureDialoguer;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.InputsManager;

public class DumpTestTables {

	public static void main(String[] args) throws Exception{
		Regressor regressor = new Regressor();
		AlgorithmConfiguration config = regressor.getConfig();
		
		String tableName = "spread_test";
		File tableFile = new File("C:\\Users\\coro\\Desktop\\DATABASE e NOTE\\spread_input_dataset .csv");
		String inputTableTemplate = TableTemplates.GENERIC.name();
		String scope = "/gcube/devsec/devVRE";
		System.out.println("Asking the infra for database in scope: "+scope);
		InfrastructureDialoguer dialoguer = new InfrastructureDialoguer(scope);
		DatabaseInfo supportDatabaseInfo = dialoguer.getDatabaseInfo("StatisticalManagerDataBase");
		InputsManager manager = new InputsManager(null, config, "test");
		manager.configSupportDatabaseParameters(supportDatabaseInfo);
		System.out.println("Database retrieved: "+supportDatabaseInfo.url);
		
		System.out.println("Creating: "+tableName);
		
		manager.createTable(tableName, tableFile,manager.getConfig(), supportDatabaseInfo, inputTableTemplate);
		
		System.out.println("DB coordinates: \nURL "+supportDatabaseInfo.url+"\n user "+supportDatabaseInfo.username+"\n password "+supportDatabaseInfo.password+"\n driver "+supportDatabaseInfo.driver+"\n dialect "+supportDatabaseInfo.dialect);
		System.out.println("All done");
	}
}
