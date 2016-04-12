package org.gcube.datatransfer.agent.library.test;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;


import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageAccessType;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.*;

public class SMPtoSMP {
	static String transferId = null;
	AgentLibrary library = null;
	StorageManagerDetails smDetails  = null;
	TransferOptions options = null;
	
	@Test
	public void testSmptoSmp(){
		Logger logger = LoggerFactory.getLogger(this.getClass().toString());
		AgentLibrary library = null;
		ScopeProvider.instance.set("/gcube/devsec");
		try {
			library =  transferAgent().at("geoserver-dev.d4science-ii.research-infrastructures.eu", 8081).build();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		smDetails = new StorageManagerDetails();
		
		smDetails.setServiceClass("data-transfer");
		smDetails.setServiceName("scheduler-portlet");
		smDetails.setOwner("testing");
		smDetails.setAccessType(storageAccessType.PRIVATE);
		
		options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setStorageManagerDetails(smDetails);
		options.setType(storageType.StorageManager);
		options.setUnzipFile(false);
		
		try{
			ArrayList<URI> uris = new ArrayList<URI>(); 
			//remote image size 393642
			URI  input = new URI("smp://devTest/Desert.jpg?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeSxqJkp9OeWKkznDnXYgDz7F/ELBV1lV8qTh/bosrhjOzQb50+GI/1q29zuVdj9M0F9HSDO7kdVA==");
			uris.add(input);
			
			String output=new String("/devTest2/");
			
			transferId=library.startTransfer(uris, output, options);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug("\ntransferId="+transferId+"\n");
		
	}
}
