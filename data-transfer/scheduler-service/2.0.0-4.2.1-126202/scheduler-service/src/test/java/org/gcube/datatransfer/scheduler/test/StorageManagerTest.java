package org.gcube.datatransfer.scheduler.test;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.transferAgent;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.agent.library.exceptions.GetTransferOutcomesException;
import org.gcube.datatransfer.agent.library.exceptions.MonitorTransferException;
import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageAccessType;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;


public class StorageManagerTest {

	static AgentLibrary agentLibrary = null;
	static String scope = "/gcube/devsec";
	static boolean overwrite = true;
	static boolean unzipFile = false;

	public static void main(String[] args) {
		process();
	}

	public static void process(){
		String result =null;
		try {
			System.out.println("StorageManagerTest -- scooooooooope="+scope);
			//GCUBEScopeManager.DEFAULT.setScope(GCUBEScope.getScope(scope));
			ScopeProvider.instance.set(scope); 
			agentLibrary =  transferAgent().at("pcitgt1012.cern.ch", 8081).withTimeout(10, TimeUnit.SECONDS).build();	
			//agentLibrary =  transferAgent().at("thredds.research-infrastructures.eu", 9090).withTimeout(10, TimeUnit.SECONDS).build();	

			// *** Remote Process *** //
			//first needed input for the agent method
			ArrayList<URI> inputUris= new ArrayList<URI>();	
		//	inputUris.add(new URI("file:///tmp/home/nick/Downloads/test/managTest.txt"));
			inputUris.add(new URI("http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png"));
			inputUris.add(new URI("http://upload.wikimedia.org/wikipedia/commons/c/c4/Kalamata%2C_Peloponnese%2C_Greece.jpg"));
			
			storageType storageType=null;
			StorageManagerDetails smDetails = new StorageManagerDetails();

			storageType=storageType.StorageManager;
			smDetails.setAccessType(storageAccessType.SHARED);
			smDetails.setOwner("Andrea.Manzi");
			smDetails.setServiceClass("DataTransfer");
			smDetails.setServiceName("Agent-Service");

			//second needed input for the agent method
			String outPath = "/temporary/andrea";

			//third input for the agent method
			TransferOptions transferOptions= new TransferOptions();				
			transferOptions.setType(storageType);
			transferOptions.setOverwriteFile(overwrite);
			transferOptions.setUnzipFile(unzipFile);
			transferOptions.setStorageManagerDetails(smDetails);

			result = agentLibrary.startTransfer(inputUris, outPath, transferOptions);
			System.out.println("StorageManagerTest - After the agentLibrary.startTransfer(...)");

			System.out.println("StorageManagerTest - Result="+result);
			if(result==null)return;
		} catch (Exception e) {
			System.out.println("StorageManagerTest - Exception when call agentLibrary.startTransfer(.....)");
			e.printStackTrace();
		}

		while(monitor(result).compareTo("STARTED")==0){
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				System.out.println("\nStorageManagerTest-- InterruptedException-Unable to sleep");
				e.printStackTrace();
			}
		}
		
		getOutcomes(result);

	}

	public static void getOutcomes(String result){
		//*** getTransferOutcomes *** //			
		StringBuilder outcomes=new StringBuilder();
		ArrayList<FileTransferOutcome> outcomesArray = null;
		try {
			outcomesArray = agentLibrary.getTransferOutcomes(result, FileTransferOutcome.class);
		} catch (GetTransferOutcomesException e) {
			System.out.println("StorageManagerTest - getOutcomes - GetTransferOutcomesException");
			e.printStackTrace();		
		}			
		catch (Exception e) {
			System.out.println("StorageManagerTest - getOutcomes - Exception");
			e.printStackTrace();	
		}			

		if(outcomesArray!=null){
			int numOfObj=0;
			for (FileTransferOutcome outcome : outcomesArray){
				outcomes.append("Outcome-"+numOfObj+"\n");
				outcomes.append("Exception: "+outcome.getException()+"\n");
				outcomes.append("FileName: "+ outcome.getFilename()+"\n");
				outcomes.append("Dest: "+ outcome.getDest()+"\n");
				outcomes.append("TransferTime: "+ outcome.getTransferTime()+"\n");
				outcomes.append("Success?: "+ outcome.isSuccess()+"\n");
				outcomes.append("Failure?: "+ outcome.isFailure()+"\n");
				numOfObj++;
			}
			System.out.println("StorageManagerTest - Outcomes="+outcomes.toString());
		}
		else{
			System.out.println("StorageManagerTest - Outcomes=null");
		}
	}


	public static String monitor(String result){
		//*** monitorTransfer *** //			
		String monitorResult = null;
		try {
			monitorResult = agentLibrary.monitorTransfer(result);
		} catch (MonitorTransferException e) {
			System.out.println("StorageManagerTest - monitor - Exception");

			e.printStackTrace();
		}			
		System.out.println("StorageManagerTest - monitorResult="+monitorResult);
		return monitorResult;
	}
}
