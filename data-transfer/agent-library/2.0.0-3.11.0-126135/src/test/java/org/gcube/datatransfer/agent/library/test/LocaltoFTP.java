package org.gcube.datatransfer.agent.library.test;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.*;

public class LocaltoFTP {
	Logger logger = LoggerFactory.getLogger(this.getClass().toString());	static String transferId = null;
	AgentLibrary library = null;
	StorageManagerDetails smDetails  = null;
	TransferOptions options = null;
	
	@Test
	public void testLocaltoFTP(){
		AgentLibrary library = null;
		ScopeProvider.instance.set("/gcube/devsec");
		try {
			 library =  transferAgent().at("geoserver-dev.d4science-ii.research-infrastructures.eu", 8081).build();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		smDetails = new StorageManagerDetails();
		/*smDetails.setAccessType(storageAccessType.SHARED);
		smDetails.setOwner("Nick");
		smDetails.setServiceClass("DataTransfer");
		smDetails.setServiceName("Agent-Service");*/

		options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setStorageManagerDetails(smDetails);
		options.setType(storageType.DataStorage);
		options.setUnzipFile(false);
		
		try{
			ArrayList<URI> uris = new ArrayList<URI>(); 
			//remote image size 393642
			URI  input = new URI("file:///tmp/data-transfer-tmp");
			uris.add(input);
			
			ArrayList<URI> urisOutput = new ArrayList<URI>(); 
			URI output=new URI("ftp://d4science:fourD_314@ftp.d4science.org/nickTest/data-transfer-tmp");
			urisOutput.add(output);
			
			transferId=library.startTransfer(uris, urisOutput, options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug("\ntransferId="+transferId+"\n");
		
	}
}
