package org.gcube.datatransfer.agent.library.test;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.transferAgent;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;


import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.agent.library.proxies.Proxies;
import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageAccessType;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(MyContainerTestRunner.class)
public class SMPtoFTP {


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
		/*smDetails.setAccessType(storageAccessType.SHARED);
		smDetails.setOwner("Nick");
		smDetails.setServiceClass("DataTransfer");
		smDetails.setServiceName("Agent-Service");*/

		options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setStorageManagerDetails(smDetails);
		options.setType(storageType.DataStorage);
		options.setUnzipFile(false);
	}


	@Test
	public void testFromSMPtoFTP() throws Exception {
		try{
			ArrayList<URI> uris = new ArrayList<URI>(); 
			URI  input = new URI("smp://temporary/testFile?5ezvFfBOLqYJ+M0vmF5az+aJSmGtP6VXVMReFhgdGEZifATnvOQ3QmBUhER21xAlAOF+gORZy71SbB0T8Oz7PJM9M3uFaAke1SXl7QQHOpVCbP5YCTXMwhpmz+fhygs3");
			uris.add(input);
			
			ArrayList<URI> urisOutput = new ArrayList<URI>(); 
			URI output=new URI("ftp://d4science:fourD_314@ftp.d4science.org/nickTest/testFile");
			urisOutput.add(output);
			
			transferId=library.startTransfer(uris, urisOutput, options);
			while(true);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\ntransferId="+transferId+"\n");
	}

}

