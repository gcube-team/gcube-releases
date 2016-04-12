package org.gcube.datatransfer.agent.library.test.tree;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;


import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.common.outcome.TreeTransferOutcome;
import org.gcube.datatransfer.agent.library.proxies.Proxies;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//@RunWith(MyContainerTestRunner.class)
public class FirstTreeBasedTest {

	static AgentLibrary library = null;
	static String scope = "/gcube/devsec";
	static String transferId = "";

	//@Deployment
	//static Gar gar = new Gar(new File("src/test/resources/agent-service.gar"));
	static String agentAddress ="geoserver-dev.d4science-ii.research-infrastructures.eu";
	static int agentPort=8081;
	static String sourceID="from_portlet_27-5";
	static String destID="from_portlet_27-5";
	@Test
	public void process(){
		try{
			setUp();
			testTransferSR1toSR2();
			testMonitorTransfer();
			testGetOutcome();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void setUp() throws Exception{		
		ScopeProvider.instance.set(scope);
		//library = Proxies.transferAgent().at("localhost", 9999).build();
		library = Proxies.transferAgent().at(agentAddress, agentPort).build();
	}

	public static void testTransferSR1toSR2() throws Exception {	
		try {
			
			Pattern pattern = Patterns.any(); 

			ScopeProvider.instance.set(scope);
			transferId = library.startTransfer(pattern,sourceID,destID);

			System.out.println("TransferID received "+ transferId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void testMonitorTransfer() throws Exception {
		String transferStatus= null ;
		do{
			try {
				transferStatus=library.monitorTransfer(transferId);
				System.out.println("Status: "+ transferStatus);
				Thread.sleep(5000);
			}  catch (Exception e) {
				e.printStackTrace();
			} 
			if(transferStatus==null){
				System.out.println("Status==null");
				return;
			}

		}while (transferStatus.compareTo(TransferStatus.QUEUED.name())==0
				|| transferStatus.compareTo(TransferStatus.STARTED.name())==0);
		System.out.println("Status: "+ transferStatus);
	}


	public static void testGetOutcome() throws Exception {

		try {
			System.out.println("The TransferID is :"+transferId);
			ArrayList<TreeTransferOutcome> outcomes = library.getTransferOutcomes(transferId, TreeTransferOutcome.class);
			for (TreeTransferOutcome outcome : outcomes){
				System.out.println("SourceID: "+ outcome.getSourceID());
				System.out.println("DestID: "+ outcome.getDestID());
				System.out.println("TotalTrees(read): "+ outcome.getTotalReadTrees());
				System.out.println("SuccessfullyTransferredTrees(written): "+ outcome.getTotalWrittenTrees());
				System.out.println("Success?: "+ outcome.isSuccess());
				System.out.println("Failure?: "+ outcome.isFailure());
				System.out.println("Exception: "+outcome.getException());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
