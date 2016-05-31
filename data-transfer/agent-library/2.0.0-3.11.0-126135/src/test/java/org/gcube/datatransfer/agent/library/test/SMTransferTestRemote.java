package org.gcube.datatransfer.agent.library.test;

import java.net.URI;
import java.util.ArrayList;


import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageAccessType;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.agent.library.proxies.Proxies;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SMTransferTestRemote {
	

	AgentLibrary library = null;
	Logger logger = LoggerFactory.getLogger(this.getClass().toString());
	StorageManagerDetails smDetails = null;
	TransferOptions options = null;
	
	
	@Before
	public void setUp() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec/");
		
		library = Proxies.transferAgent().at("thredds.research-infrastructures.eu", 9090).build();
		
		smDetails = new StorageManagerDetails();
		smDetails.setAccessType(storageAccessType.SHARED);
		smDetails.setOwner("Andrea.Manzi");
		smDetails.setServiceClass("DataTransfer");
		smDetails.setServiceName("ServiceName");
		
		options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setStorageManagerDetails(smDetails);
		options.setType(storageType.StorageManager);
		options.setUnzipFile(false);
		
		
	}
	@Test
	public void testSMtoFile() throws Exception {
		
			ArrayList<URI> uris = new ArrayList<URI>(); 
		
			URI input = new URI("https://dl.dropbox.com/u/8704957/commons-pool-1.5.4.jar");
			uris.add(input);
			String outPath = "/temporary/andrea";
			
			ArrayList<FileTransferOutcome> outcomes = library.startTransferSync(uris, outPath, options);

			for (FileTransferOutcome outcome : outcomes){
				logger.debug("Exception: "+outcome.getException());
				logger.debug("Success?: "+ outcome.isSuccess());
				logger.debug("Failure?: "+ outcome.isFailure());
				logger.debug("sourceFile: "+ outcome.fileName());
				logger.debug("dest: "+ outcome.getDest());
			}
		
	}

}

