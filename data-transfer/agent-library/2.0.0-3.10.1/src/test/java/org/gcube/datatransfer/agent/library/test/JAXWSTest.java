package org.gcube.datatransfer.agent.library.test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainer;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.mycontainer.Scope;

import org.gcube.datatransfer.agent.library.fws.AgentServiceJAXWSStubs;
import org.gcube.datatransfer.common.agent.Types.*;
import org.gcube.datatransfer.common.outcome.TransferStatus;

import static org.gcube.datatransfer.agent.library.Constants.*;

import static org.gcube.datatransfer.agent.library.fws.Constants.*;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;

@RunWith(MyContainerTestRunner.class)  
@Scope("/gcube/devsec")
public class JAXWSTest {
	
	
	@Deployment
	static Gar gar = new Gar(new File("src/test/resources/agent-service.gar"));
		
	static AgentServiceJAXWSStubs stub ;
	
	@Inject
	static MyContainer container;
	
	@Named(PORT_TYPE_NAME)
	static URI agentAddress;
	
	static String id;
	
	@BeforeClass
	public static void setup() {
	
		//setProxy("localhost",8080); //comment after on-the-wire analysis
		
		stub = stubFor(agent).at(agentAddress);
	}
	
	
	@Test
	public void testStartTransfer() throws URISyntaxException{

		
		StartTransferMessage message = new StartTransferMessage();
		message.syncOp=false;
		
		SourceData data = new SourceData();
		InputURIs uris = new InputURIs();
		
		data.scope = "/gcube/devsec";
		data.type = transferType.FileBasedTransfer;
		
		ArrayList<String> urLists = new ArrayList<String>();
		urLists.add("https://dl.dropbox.com/u/8704957/00001-PASSIFLORA_EDULIS-SVM_PROJECTION_MAP.img");
		
		data.inputURIs = urLists;
		
		message.source = data;
		
		DestData destData = new DestData();
		
		OutUriData outUri = new OutUriData();
		
		ArrayList<String> outLists = new ArrayList<String>();
		
		outLists.add("/Users/andrea");
		outUri.OutUris= outLists;
		
		TransferOptions options = new TransferOptions();
		options.overwrite= true;
		options.transferTimeout= 10000000;
		options.storageType= storageType.LocalGHN;
		outUri.options= options;
		
		destData.type = transferType.FileBasedTransfer;
		destData.scope = "/gcube/devsec";
		destData.outUri = outUri;
		destData.outSourceId = null;
		
		message.dest = destData;
	
		id  = stub.startTransfer(message);
		System.out.println(id);
				 
		String response = stub.monitorTransfer(id);
		
		System.out.println(response);
		
	}
	
	@Test
	public void testMonitorTransfer() throws Exception {
		
		MonitorTransferReportMessage message = null;
		TransferStatus transferStatus= null ;
		do{
			try {
				
				message = stub.monitorTransferWithProgress(id);
				transferStatus = TransferStatus.valueOf(message.transferStatus);
				System.out.println("Status: "+ message.transferStatus);
				System.out.println("TotalBytes: "+ message.totalBytes);
				System.out.println("transferedBytes: "+ message.bytesTransferred);
				System.out.println("totalTransfers: "+ message.totalTransfers);
				System.out.println("transfersCompleted: "+ message.transferCompleted);
				Thread.sleep(5000);
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
		while (!( transferStatus.hasCompleted()));
			
	}
}
