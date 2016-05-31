package org.gcube.datatransfer.agent.library.test;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;


import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.*;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.agent.library.proxies.Proxies;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MyContainerTestRunner.class)
public class SMTransferTest {
	
	Logger logger = LoggerFactory.getLogger(this.getClass().toString());
	static String transferId = null;
	AgentLibrary library = null;
	
	StorageManagerDetails smDetails  = null;
	
	TransferOptions options = null;
	
	@Deployment
	static Gar gar = new Gar(new File("src/test/resources/agent-service.gar"));
	
	@Before
	public void setUp() throws  Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		library = Proxies.transferAgent().at("localhost", 9999).build();
		
		smDetails = new StorageManagerDetails();
		smDetails.setAccessType(storageAccessType.SHARED);
		smDetails.setOwner("Andrea.Manzi");
		smDetails.setServiceClass("DataTransfer");
		smDetails.setServiceName("Agent-Service");
		
		options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setStorageManagerDetails(smDetails);
		options.setType(storageType.StorageManager);
		options.setUnzipFile(false);
		

	}


	//@Test
	public void testSMTransferFromLocalFile() throws Exception {
		
		try{
			ArrayList<URI> uris = new ArrayList<URI>(); 
			URI  input = new URI("file:///Users/andrea/Downloads/selFAO.csv");
			uris.add(input);
			
			String outPath = "/temporary/andrea";
			
			library.startTransfer(uris, outPath, options);
			while(true);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testSMTransferFromTorrent() throws Exception {
	
		try {

		ArrayList<URI> uris = new ArrayList<URI>(); 
		URI  input = new URI("bittorrent://file:///Users/andrea/Fedora-17-Beta-i686-Live-Desktop.torrent[Fedora-17-Beta-i686-Live-Desktop.iso]");
		uris.add(input);
		
		String outPath = "/temporary/andrea";
		library.startTransfer(uris, outPath, options);
		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testSMTransferFromFTP() throws Exception {
		
		
		try {
			ArrayList<URI> uris = new ArrayList<URI>(); 
			
			//URI  input = new URI("ftp://andrea:bilico1980@pcd4science3.cern.ch/cernts");
			URI input = new URI("http://grids16.eng.it/BuildReport/download/Recent_Builds/org.gcube.content-management.time-series-geo-tools.1.6.0/BUILD_1/dist/org.gcube/gcf-no-tests/1.5.1/sl5_x86_64_gcc412/gcf-no-tests-1.5.1-SNAPSHOT.centos6.x86_64.tar.gz");
			
			uris.add(input);
			
			String outPath = "/temporary/andrea";
			
			transferId = library.startTransfer(uris, outPath, options);
			logger.debug("Transfer id"+ transferId);
	
			Thread.sleep(10000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMonitorTransfer() throws Exception {
		
		try {
			System.out.println("The TransferID is :"+transferId);
			System.out.println(library.monitorTransfer(transferId));
			Thread.sleep(1000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetResult() throws Exception {
		
		try {
			System.out.println("The TransferID is :"+transferId);
			ArrayList<FileTransferOutcome> outcomes = library.getTransferOutcomes(transferId, FileTransferOutcome.class);
			for (FileTransferOutcome outcome : outcomes){
				logger.debug("Exception: "+outcome.getException());
				logger.debug("Success?: "+ outcome.isSuccess());
				logger.debug("Failure?: "+ outcome.isFailure());
				logger.debug("FileName: "+ outcome.fileName());
				logger.debug("Dest: "+ outcome.getDest());
				logger.debug("TransferTime: "+ outcome.getTransferTime());


			}
			Thread.sleep(10000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

