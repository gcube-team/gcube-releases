package org.gcube.data.analysis.statisticalmanager.test;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;

public class DataSpaceTest {
	
	
	public static void main(String[] args) throws Exception{
		ScopeProvider.instance.set(TestCommon.SCOPE);
		StatisticalManagerDataSpace dataSpace = StatisticalManagerDSL.dataSpace().build();
		System.out.println(dataSpace.getImports(TestCommon.USER, null));
		System.out.println("DONE");
		
		
		
//		StatisticalManagerDataSpace dataSpace = StatisticalManagerDSL.dataSpace().build();
//		dataSpace.getImports(user, null);
//		SMTables tablesList = dataSpace.getTables(user);
//		for (SMTable table : tablesList.getList()) {
//			System.out.println("Table id " + table.getResourceId());
//		}
//		
//		tablesList = dataSpace.getTables(user,TableTemplates.OCCURRENCE_SPECIES.toString());
//		for (SMTable table : tablesList.getList()) {
//			
//			System.out.println("table id " + table.getResourceId());
//		}
				
//		dataSpace.createTableFromCSV(new File("/home/gioia/Desktop/HCAF"), true, "", TableTemplates.HCAF, "test import", user);
		
//		SMImporters importers =  dataSpace.getImporters(user, null);
//		System.out.println("imp" + importers.getList());
//    	
//		SMImporters importersList=dataSpace.getImporters(user, TableTemplates.OCCURRENCE_SPECIES.toString());
//		System.out.println("Size" + importersList.getList().length);
//		
//		List<SMTableMetadata> tablesList = dataSpace.getTablesMetadata("gianpaolo.coro", null);
//		for (SMTableMetadata table : tablesList) {
//			System.out.println("Table id " + table.getTableId());
//		}
		
//		File file = dataSpace.exportTable("hspen_mini_anto");
//		System.out.println("file " + file.getAbsolutePath());
		


    	
	}
	
	
	
}
