package gr.cite.datamonitor;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

import gr.cite.clustermanager.actuators.functions.ExecutionMonitor;
import gr.cite.clustermanager.actuators.functions.ExecutionNotifier;
import gr.cite.clustermanager.actuators.layers.DataCreatorGos;
import gr.cite.clustermanager.actuators.layers.DataMonitor;
import gr.cite.clustermanager.model.functions.ExecutionDetails;
import gr.cite.clustermanager.model.layers.GosDefinition;

public class Test {

	private static String zkConnStr = "host:2181";
	private static String gosIdentifier = "host:8080";
	private static String geoserverUrl = "fadfasdfasdf";
	private static String gosHost = "localhost";
	private static String gosPort = "8080";
	private static String geoserverWorkspace = "sdfasdg";
	private static String datastoreName = "dfgsdfhgsdfg";
	
	
	
	public static void main (String [] args) throws Exception {
		
		List<String> layers = new ArrayList<String>();
		for(int i=0;i<10;i++)
			layers.add(UUID.randomUUID().toString());
		
		
		new Thread(new Runnable() {
		    public void run() {
		    	DataCreatorGos dataCreatorGos = DataCreatorGos.getInstance(zkConnStr, gosIdentifier, geoserverUrl, gosHost, gosPort, geoserverWorkspace, datastoreName);
				try {
					dataCreatorGos.create(layers);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		}).start();
		
		
		DataMonitor.getInstance(zkConnStr);
		Thread.sleep(3000);
		System.out.println( DataMonitor.getInstance(zkConnStr).getAllGosEndpoints());
		
        Thread.sleep(Integer.MAX_VALUE);
		
	}
	
	
	
	
	public static void main1 (String [] args) throws Exception{
		
		ExecutionNotifier execNotifier = new ExecutionNotifier(zkConnStr);
		ExecutionMonitor execMonitor = new ExecutionMonitor(zkConnStr);

		ExecutionDetails execDetails = new ExecutionDetails("012345", "localhost", "/gcube/dummyVO/dummyVRE", System.currentTimeMillis(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),"LayerTestName");
		
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
