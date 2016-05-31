package org.gcube.datatransfer.agent.library.test;

import java.net.URI;
import java.util.ArrayList;


import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.agent.library.proxies.Proxies;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.options.TransferOptions.ConversionType;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class FileConversionTestRemote {
	
	Logger logger = LoggerFactory.getLogger(this.getClass().toString());
	AgentLibrary library = null;
	private TransferOptions options;	
	
	static String transferId = "";
	
	
	@Before
	public void setUp() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		library = Proxies.transferAgent().at("geoserver-dev.d4science-ii.research-infrastructures.eu", 9000).build();
		
		options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setType(storageType.LocalGHN);
		options.setUnzipFile(false);
		options.setCovertFile(true);
		options.setConversionType(ConversionType.GEOTIFF);
		options.setDeleteOriginalFile(true);

	}
	
	@Test
	public void testTransferAndConversionc() throws Exception {
		
		try {
			ArrayList<URI> uris = new ArrayList<URI>();
			URI input = new URI("http://dedalo.i3m.upv.es/enm-results/315690ec-d358-42df-9af6-d0ecb97fc880/results/00001-PASSIFLORA_EDULIS-SVM_PROJECTION_MAP.img");
			uris.add(input);
			String outPath = "/test";
			
			transferId = library.startTransfer(uris, outPath, options);
			
			System.out.println("TransferID receveid "+ transferId);
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testMonitorTransfer() throws Exception {
		
		MonitorTransferReportMessage message = null;
		do{
			try {
				
				message = library.monitorTransferWithProgress(transferId);
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
		while (!( message.getTransferStatus()!= "DONE"));
			
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
				System.out.println("FileName: "+ outcome.fileName());
				System.out.println("TransferTime: "+ outcome.getTransferTime());


			}
			Thread.sleep(10000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
