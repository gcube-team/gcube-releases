package gr.cite.datamonitor;


import java.util.UUID;

import gr.cite.clustermanager.actuators.functions.ExecutionMonitor;
import gr.cite.clustermanager.actuators.functions.ExecutionNotifier;
import gr.cite.clustermanager.model.functions.ExecutionDetails;
import gr.cite.clustermanager.model.functions.ExecutionStatus;

public class Test {

	private static String zkConnStr = "localhost:2181";
	
	
	public static void main (String [] args) throws Exception{
		
		ExecutionNotifier execNotifier = new ExecutionNotifier(zkConnStr);
		ExecutionMonitor execMonitor = new ExecutionMonitor(zkConnStr);

		ExecutionDetails execDetails = new ExecutionDetails("012345", "localhost", "/gcube/dummyVO/dummyVRE", System.currentTimeMillis(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
		
		execNotifier.notifyAbout(execDetails);
		Thread.sleep(2000);
		System.out.println(execMonitor.getAllLatestExecutionDetails());
		
		
		execDetails.setId("4466");
		execNotifier.notifyAbout(execDetails);
		Thread.sleep(10000);
		System.out.println(execMonitor.getAllLatestExecutionDetails());
		
		execDetails = execMonitor.getLatestExecutionDetailsOf("12345");
		execDetails.setProgress(20);
		execNotifier.notifyAbout(execDetails);
		Thread.sleep(10000);
		System.out.println(execMonitor.getAllLatestExecutionDetails());
		
		
	}
	
	
}
