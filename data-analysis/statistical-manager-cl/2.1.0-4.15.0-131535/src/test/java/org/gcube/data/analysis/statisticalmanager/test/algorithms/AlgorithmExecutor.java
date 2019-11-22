package org.gcube.data.analysis.statisticalmanager.test.algorithms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import marytts.util.io.FileUtils;

import org.apache.commons.io.IOUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationConfig;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMImport;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMInputEntry;
import org.gcube.data.analysis.statisticalmanager.test.TestCommon;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;

public class AlgorithmExecutor {

	
	
	
	
	
	public static void main(String[] args) throws Exception{
		ScopeProvider.instance.set(TestCommon.SCOPE);
		System.out.println("Importing registered urls");
		StatisticalManagerDataSpace dataspace=StatisticalManagerDSL.dataSpace().build();
		HashMap<String,String> importedResources=new HashMap<String,String>();
		
		int toImport=TestExecutionProfile.REFERENCED_FILES.size()+TestExecutionProfile.REFERENCED_TABLES.size();
		
		System.out.println(" *****************IMPORTING REFERENCED TABLES******************");
		
		for(Entry<String,String> entry : TestExecutionProfile.REFERENCED_TABLES.entrySet()){
			String id=importTable(entry.getKey(), entry.getValue(), dataspace, TestCommon.USER);
			importedResources.put(entry.getKey(), id);
			System.out.println("IMPORTED "+importedResources.size()+" out of "+toImport);
		}
		
		System.out.println(" *****************IMPORTING REFERENCED FILES******************");

		
		for(Entry<String,String> entry : TestExecutionProfile.REFERENCED_FILES.entrySet()){
			String id=importFile(entry.getKey(), dataspace, TestCommon.USER);
			importedResources.put(entry.getKey(), id);
			System.out.println("IMPORTED "+importedResources.size()+" out of "+toImport);
		}
		
		System.out.println(" *****************Launching experiments******************");
		
		StatisticalManagerFactory factory=StatisticalManagerDSL.createStateful().build();
		
		HashMap<String,String> computationIds=new HashMap<String,String>();
		
		for(TestExecutionProfile profile:TestExecutionProfile.PROFILES){
			try{
				System.out.println("Executing "+profile.getName());			
			profile.updateReferences(importedResources);
			SMComputationRequest request=new SMComputationRequest(profile.getConfig(),
					"Execution of "+profile.getName(),
					profile.getName()+" TEST",
					TestCommon.USER);
			System.out.println("Request IS : "+print(request.config()));
			computationIds.put(factory.executeComputation(request),profile.getName());
			}catch(Exception e){
				System.out.println("UNABLE TO EXECUTE "+profile.getName());
				e.printStackTrace();
			}
		}
		System.out.println("Submitted "+computationIds.size()+" computations");
		
		HashMap<String,SMOperationStatus> status=new HashMap<>();
		
		boolean stop=false;
		while(!stop){
			for(String id:computationIds.keySet()){
				//if not checked or ongoing get status
				if(!status.containsKey(id)||
						!status.get(id).equals(SMOperationStatus.COMPLETED)||
						!status.get(id).equals(SMOperationStatus.FAILED))
					status.put(id, 
							SMOperationStatus.values()[factory.getComputation(id).operationStatus()]);
			}
			int completedCount=0;
			int errorCount=0;
			for(Entry<String,SMOperationStatus> entry:status.entrySet()){
				if(entry.getValue().equals(SMOperationStatus.COMPLETED))completedCount++;
				if(entry.getValue().equals(SMOperationStatus.FAILED))errorCount++;
			}
			stop=computationIds.size()==completedCount+errorCount;
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
			}
			System.out.println(String.format("Completed %s, Failed %s out of %s executions", completedCount, errorCount,computationIds.size()));
			if(errorCount>0){
				System.out.println("FAILED EXPERIMENTS :");
				for(Entry<String,SMOperationStatus> entry:status.entrySet())
					if(entry.getValue().equals(SMOperationStatus.FAILED)) 
						System.out.println(entry.getKey()+" : "+computationIds.get(entry.getKey()));
			}
		}	
		
		
		
		System.out.println("Clearing DataSpace...");
		
		for(String id:importedResources.keySet())
			dataspace.removeTable(id);		
		
		System.out.println("DONE");
		
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
	
	private static String importFile(String url,StatisticalManagerDataSpace dataSpace, String user) throws Exception{
		File tempFile=null;
		try{
			tempFile=File.createTempFile("SM_", ".gen");
			IOUtils.copy(new InputStreamReader(new URL(url).openStream()), new FileOutputStream(tempFile));
			String importerId=dataSpace.importFile(tempFile.getName(), tempFile, null, null, "Imported for test purposes", user, "");
			boolean complete=false;
			while(!complete){
				SMImport smImport=dataSpace.getImporter(importerId);
				SMOperationStatus status=SMOperationStatus.values()[smImport.operationStatus()];
				switch(status){
				case FAILED : 
					throw new Exception("Unable to send file to data space");
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
	
	private static final String print(SMComputationConfig toPrint){
		StringBuilder toReturn=new StringBuilder("ALGORITHM : "+toPrint.algorithm()+",");
		toReturn.append("Parameters {");
		for(SMInputEntry entry:toPrint.parameters().list())
			toReturn.append(entry.key()+" = "+entry.value()+";");
		toReturn.append("}");
		return toReturn.toString();
	}
}
