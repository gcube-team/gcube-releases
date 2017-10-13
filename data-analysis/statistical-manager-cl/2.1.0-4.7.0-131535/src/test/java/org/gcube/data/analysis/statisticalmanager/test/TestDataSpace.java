package org.gcube.data.analysis.statisticalmanager.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMImport;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;

import marytts.util.io.FileUtils;

public class TestDataSpace {

	
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TokenSetter.set(TestCommon.SCOPE);
		System.out.println("Testing dataspace under scope "+TestCommon.SCOPE);
		System.out.println("Username "+TestCommon.USER);
		
		StatisticalManagerDataSpace ds=StatisticalManagerDSL.dataSpace().build();
//		System.out.println("-------- GET Files");
//		
//		for(SMFile file:ds.getFiles(TestCommon.USER))
//			System.out.println(file);
		
		
//		System.out.println("-------- GET Tables ");
//		for(SMTable table:ds.getTables(TestCommon.USER).list()){
//			System.out.println();
//			System.out.println("DB Parameters "+ds.getDBParameters(table.resourceId()));
//			System.out.println("Similar tables "+ds.getTables(TestCommon.USER, table.template()).list().size() );
//		}
		
		System.out.println(" IMPORT / EXPORT ");
		
		String id=importTable("http://goo.gl/VDzpch", "GENERIC", ds, TestCommon.USER);
		
		
		System.out.println("TODO ");

	}

	
	private static String importTable(String url,String template,StatisticalManagerDataSpace dataSpace, String user) throws Exception{
		File tempFile=null;
		try{
			tempFile=File.createTempFile("SM_", ".csv");
			IOUtils.copy(new InputStreamReader(new URL(url).openStream()), new FileOutputStream(tempFile));
			String importerId=dataSpace.createTableFromCSV(tempFile, true, ",", "#", "tableTest", TableTemplates.valueOf(template), "Imported from "+url, user);
			boolean complete=false;
			while(!complete){
				SMImport smImport=dataSpace.getImporter(importerId);
				SMOperationStatus status=SMOperationStatus.values()[smImport.operationStatus()];
				switch(status){
				case FAILED : 
					throw new Exception("Unable to send table to data space");
				case COMPLETED : 
					complete=true;
					return smImport.abstractResource().resource().resourceId();					
				default :
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
			throw new Exception("Unable to send table to data space");
		}finally{
			if(tempFile!=null) FileUtils.delete(tempFile.getAbsolutePath());
		}		
	}
	
}
