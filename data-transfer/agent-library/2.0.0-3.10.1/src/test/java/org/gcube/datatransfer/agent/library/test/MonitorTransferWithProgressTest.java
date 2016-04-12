package org.gcube.datatransfer.agent.library.test;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.agent.library.proxies.Proxies;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorTransferWithProgressTest {

	Logger logger = LoggerFactory.getLogger(this.getClass().toString());
	static String transferId = null;
	AgentLibrary library = null;	
	TransferOptions options = null;

	@Before
	public void setUp() throws Exception{
		ScopeProvider.instance.set("/gcube");
		//library = Proxies.transferAgent().at("geoserver-dev.d4science-ii.research-infrastructures.eu", 8081).build();
		library = Proxies.transferAgent().at("pcitgt1012.cern.ch", 8081).build();

		options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setUnzipFile(false);
	}

	@Test
	public void testTransferFromUrlToLocal() throws Exception {	
		options.setType(storageType.LocalGHN);
		try {
			ArrayList<URI> inputUris = new ArrayList<URI>(); 
			//ArrayList<URI> outputUris = new ArrayList<URI>(); 

			URI  input = new URI("http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png");
			inputUris.add(input);

			String  output = "/testFile";

			transferId = library.startTransfer(inputUris,output, options);
			logger.debug("Transfer id"+ transferId);

			if(transferId==null){
				Thread.sleep(1000);
			}
			testMonitorTransferWithProgress();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	//@Test
	public void testTransferFromUrlToFTP() throws Exception {	
		options.setType(storageType.DataStorage);
		try {
			ArrayList<URI> inputUris = new ArrayList<URI>(); 
			ArrayList<URI> outputUris = new ArrayList<URI>(); 

			URI  input = new URI("http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png");
			inputUris.add(input);
			inputUris.add(input);

			String outputString = "ftp://d4science:fourD_314@ftp.d4science.org/testFile";
			outputUris.add(new URI(outputString));
			outputUris.add(new URI(outputString+"2"));

			transferId = library.startTransfer(inputUris,outputUris, options);
			logger.debug("Transfer id"+ transferId);

			if(transferId==null){
				Thread.sleep(1000);
			}
			//testMonitorTransferWithProgress();
			testMonitorTransferWithProgress();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testMonitorTransferWithProgress() throws Exception {		
		MonitorTransferReportMessage msg = null;
		long transferredBytes=-1,totalBytes=0 ;
		while(transferredBytes<totalBytes){
			try {
				System.out.println("The TransferID is :"+transferId);
				msg = library.monitorTransferWithProgress(transferId);
				System.out.println("MonitorTransferReportMessage:\n"+
						"getTransferStatus="+msg.getTransferStatus()+"\n"+
						"getBytesTransferred="+msg.getBytesTransferred()+"\n"+
						"getTotalBytes="+msg.getTotalBytes()+"\n"+
						"getTotalTransfers="+msg.getTotalTransfers()+"\n");

				transferredBytes=msg.getBytesTransferred();
				totalBytes = msg.getTotalBytes();
			}catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public void testMonitorTransfer() throws Exception {		
		String result;
		try{
				System.out.println("The TransferID is :"+transferId);
				result = library.monitorTransfer(transferId);
				System.out.println("status=\n"+
						result);

			}catch (Exception e) {
				e.printStackTrace();
				
			}
		
	}
}

