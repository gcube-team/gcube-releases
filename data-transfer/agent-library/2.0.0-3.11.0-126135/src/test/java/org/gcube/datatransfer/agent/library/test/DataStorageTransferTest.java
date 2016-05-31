package org.gcube.datatransfer.agent.library.test;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;


import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.agent.library.proxies.Proxies;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MyContainerTestRunner.class)
public class DataStorageTransferTest {
	
	Logger logger = LoggerFactory.getLogger(this.getClass().toString());
	static String transferId = null;
	AgentLibrary library = null;
	
	
	TransferOptions options = null;
	@Deployment
	static Gar gar = new Gar(new File("src/test/resources/agent-service.gar"));
	
	@Before
	public void setUp() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		library = Proxies.transferAgent().at("localhost", 9999).build();
	
		options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setType(storageType.DataStorage);
		options.setUnzipFile(false);
		

	}
	
	@Test
	public void testDataStorageTransferFromTOFTP() throws Exception {
		
		
		try {
			ArrayList<URI> inputUris = new ArrayList<URI>(); 
			ArrayList<URI> outputUris = new ArrayList<URI>(); 
			//URI  input = new URI("s3://simple-bucket-andrea/test5");
			
			//URI input = new URI("http://grids16.eng.it/BuildReport/download/Recent_Builds/org.gcube.content-management.time-series-geo-tools.1.6.0/BUILD_1/dist/org.gcube/gcf-no-tests/1.5.1/sl5_x86_64_gcc412/gcf-no-tests-1.5.1-SNAPSHOT.centos6.x86_64.tar.gz");
			
			URI  input = new URI("ftp://andrea:bilico1980@pcd4science3.cern.ch/cernts");
			inputUris.add(input);
			
			URI  output = new URI("ftp://andrea:bilico1980@pcd4science3.cern.ch/testandrea");
			
			//URI  output = new URI("s3://simple-bucket-andrea/test7");
			
			outputUris.add(output);
			
			
			transferId = library.startTransfer(inputUris,outputUris, options);
			logger.debug("Transfer id"+ transferId);
	
			Thread.sleep(20000);
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

