package org.gcube.data.analysis.statisticalmanager.test;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;

public class TestDataSpace {

	
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ScopeProvider.instance.set(TestCommon.SCOPE);
		System.out.println("Testing dataspace under scope "+TestCommon.SCOPE);
		System.out.println("Username "+TestCommon.USER);
		
		StatisticalManagerDataSpace ds=StatisticalManagerDSL.dataSpace().build();
		System.out.println("-------- GET Files");
		
		for(SMFile file:ds.getFiles(TestCommon.USER))
			System.out.println(file);
		
		System.out.println("-------- GET Tables ");
		for(SMTable table:ds.getTables(TestCommon.USER).list()){
			System.out.println();
			System.out.println("DB Parameters "+ds.getDBParameters(table.resourceId()));
			System.out.println("Similar tables "+ds.getTables(TestCommon.USER, table.template()).list().size() );
		}
		
		System.out.println(" IMPORT / EXPORT ");
		//TODO
		System.out.println("TODO ");

	}

}
