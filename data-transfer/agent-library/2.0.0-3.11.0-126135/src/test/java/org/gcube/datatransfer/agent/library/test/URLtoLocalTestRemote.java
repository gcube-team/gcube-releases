package org.gcube.datatransfer.agent.library.test;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.Future;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.agent.library.proxies.Proxies;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class URLtoLocalTestRemote {
	
	Logger logger = null;
	AgentLibrary library = null;
	private TransferOptions options;
	
	static Future<String> tranferidFuture = null;
	static String transferId = "";
	
	static String scope="/d4science.research-infrastructures.eu/EUBrazilOpenBIO";
	static String agentAddress="geoserver.d4science-ii.research-infrastructures.eu";
	static int port = 9000;
	
	@Before
	public void setUp() throws Exception{
		
		logger = LoggerFactory.getLogger(this.getClass().toString());
		ScopeProvider.instance.set(scope);
		
		library = Proxies.transferAgent().at(agentAddress,port).build();
		
		options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setType(storageType.LocalGHN);
		options.setUnzipFile(false);
	}
	
	@Test
	public void testLocalTransferFromUriAsync() throws Exception {
		
		try {
			
			ArrayList<URI> uris = new ArrayList<URI>();
			URI input = new URI("http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png");
			uris.add(input);
			String outPath = "/tmp";
			transferId = library.startTransfer(uris, outPath, options);
	
			System.out.println("TransferID receveid "+ transferId);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//@Test
	public void testMonitorTransfer() throws Exception {
		transferId ="ba76fe30-37f2-11e2-98c1-beb04c06c02b";
		
		try {
			System.out.println("The TransferID is :"+transferId);
			System.out.println(library.monitorTransfer(transferId));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testGetResulr() throws Exception {
		
		try {
			System.out.println("The TransferID is :"+transferId);
			ArrayList<FileTransferOutcome> outcomes = library.getTransferOutcomes(transferId, FileTransferOutcome.class);
			for (FileTransferOutcome outcome : outcomes){
				logger.debug("Exception: "+outcome.getException());
				logger.debug("Success?: "+ outcome.isSuccess());
				logger.debug("Failure?: "+ outcome.isFailure());
				logger.debug("FileName: "+ outcome.fileName());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

