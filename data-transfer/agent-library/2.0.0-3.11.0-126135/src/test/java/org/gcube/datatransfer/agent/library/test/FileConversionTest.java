package org.gcube.datatransfer.agent.library.test;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.options.TransferOptions.ConversionType;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.agent.library.proxies.Proxies;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(MyContainerTestRunner.class)
public class FileConversionTest {
	
	public static Logger log = LoggerFactory.getLogger("test");
	AgentLibrary library = null;
	private TransferOptions options;
	static String transferId = "";
	
	@Deployment
	static Gar gar = new Gar(new File("src/test/resources/agent-service.gar"));
	
	@Before
	public void setUp() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		library = Proxies.transferAgent().at("localhost", 9999).build();
		
		options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setType(storageType.LocalGHN);
		options.setUnzipFile(false);
		options.setCovertFile(false);
		options.setConversionType(ConversionType.GEOTIFF);
		options.setTransferTimeout(2, TimeUnit.HOURS);

	}
	
	@Test
	public void testLocalTransferFromFTPAsync() throws Exception {
		
		try {
			ArrayList<URI> uris = new ArrayList<URI>();
			for (int i = 0; i<1; i++){
				URI input = new URI ("https://dl.dropbox.com/u/12809149/p_edulis_map.img");
				uris.add(input);
			}
			
			String outPath = "/tmp";
			
			transferId = library.startTransfer(uris, outPath, options);
			
			System.out.println("TransferID receveid "+ transferId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMonitorTransfer() throws Exception {
		
		MonitorTransferReportMessage message = null;
		TransferStatus transferStatus= null ;
		do{
			try {
				
				message = library.monitorTransferWithProgress(transferId);
				transferStatus = TransferStatus.valueOf(message.getTransferStatus());
				System.out.println("Status: "+ message.getTransferStatus());
				System.out.println("TotalBytes: "+ message.getTotalBytes());
				System.out.println("transferedBytes: "+ message.getBytesTransferred());
				System.out.println("totalTransfers: "+ message.getTotalTransfers());
				System.out.println("transfersCompleted: "+ message.getTransferCompleted());
				Thread.sleep(5000);
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
		while (!( transferStatus.hasCompleted()));
	}
	
	@Test
	public void testGetResult() throws Exception {
		
		try {
			System.out.println("The TransferID is :"+transferId);
			ArrayList<FileTransferOutcome> outcomes = library.getTransferOutcomes(transferId, FileTransferOutcome.class);
			for (FileTransferOutcome outcome : outcomes){
				System.out.println("Exception: "+outcome.getException());
				System.out.println("Success?: "+ outcome.isSuccess());
				System.out.println("Failure?: "+ outcome.isFailure());
				
				if (outcome.isFailure())
					System.err.println("ERRRRRRRORRRRRR");
				System.out.println("FileName: "+ outcome.fileName());
				System.out.println("TransferTime: "+ outcome.getTransferTime());


			}
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
