/**
 * 
 */
package org.gcube.common.geoserverinterface;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.transferAgent;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import org.gcube.common.geoserverinterface.bean.iso.EnvironmentConfiguration;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.agent.library.exceptions.ConfigurationException;
import org.gcube.datatransfer.agent.library.exceptions.GetTransferOutcomesException;
import org.gcube.datatransfer.agent.library.exceptions.TransferException;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.options.TransferOptions.ConversionType;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ceras
 *
 */
public class DataTransferUtl {

	private static final Logger logger = LoggerFactory.getLogger(DataTransferUtl.class);
	
	private static final String TRANSFER_STATE_DONE = "DONE";
	private static final long INTERVAL_MONITOR = 5000;

	/**
	 * @param geoTiffUrl
	 * @param destinationUrl
	 * @param scope
	 * @param b 
	 * @return 
	 * @throws URISyntaxException 
	 * @throws ConfigurationException 
	 * @throws TransferException 
	 * @throws GetTransferOutcomesException 
	 */
	public static String transferFromUrl(String geoTiffUrl, String destinationUrl, String dataSubFolder, String scope, boolean convert) throws URISyntaxException, TransferException, ConfigurationException, GetTransferOutcomesException {
		URI destURI = new URI(destinationUrl);
		String host = destURI.getHost();
		ScopeProvider.instance.set(scope);
		int port=getAgentPortOnHost(host);
		
		
		logger.info("[DataTransfer] Start Transfer");
		logger.info("[DataTransfer] source: "+geoTiffUrl+"; destination host: "+host+"; destination Port : "+port+"scope: "+scope);

		AgentLibrary library = transferAgent().at(host, port).build();

		ArrayList<URI> inputs = new ArrayList<URI>();
		inputs.add(new URI(geoTiffUrl));

		String outPath = "./data/"+dataSubFolder+"/";

		TransferOptions options = new TransferOptions();
		options.setOverwriteFile(true);
		options.setType(storageType.LocalGHN);
		options.setUnzipFile(false);
		if (convert) {
			options.setCovertFile(true);
			options.setConversionType(ConversionType.GEOTIFF);
			options.setDeleteOriginalFile(true);
		}

		String transferId = library.startTransfer(inputs, outPath, options);
		logger.info("Transfer started "+transferId);
		
		MonitorTransferReportMessage message = null;
		TransferStatus ts = null;
		
		do {
			try {
				message = library.monitorTransferWithProgress(transferId);
				ts = TransferStatus.valueOf(message.getTransferStatus());
				double percentage = (double)message.getBytesTransferred() / (double)message.getTotalBytes() * 100d; 
				logger.info("["+Math.round(percentage)+"%]"
						+"\tStatus: "+message.getTransferStatus()
						+"\tTotBytes: "+message.getTotalBytes()
						+"\tTransferedBytes: "+message.getBytesTransferred()
						+"\tTotalTransfers: "+message.getTotalTransfers()
						+"\tTransfersCompleted: "+message.getTransferCompleted());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(INTERVAL_MONITOR);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!ts.hasCompleted());

//		String transferState="";
//		while (!transferState.contentEquals(TRANSFER_STATE_DONE)) {
//			try {
//				
//				transferState = library.monitorTransfer(transferId);
//				System.out.print(".");
//				
//			} catch (MonitorTransferException e) {
//				e.printStackTrace();
//			}
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		logger.info("done!");
		FileTransferOutcome outcome = library.getTransferOutcomes(transferId, FileTransferOutcome.class).get(0);

//		for (FileTransferOutcome outcome : outcomes)
		logger.info("file: "+outcome.getDest()+"; "+ (outcome.isSuccess() ? "SUCCESS" : "FAILURE"));
		String fileName = outcome.isSuccess() ? new File(outcome.getDest()).getName() : null;
		return fileName;
	}

	
	
	public static final int getAgentPortOnHost(String host) {
		SimpleQuery query=queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq 'DataTransfer'").
				addCondition("$resource/Profile/ServiceName/text() eq 'agent-service'");
		DiscoveryClient<GCoreEndpoint> client=clientFor(GCoreEndpoint.class);
		for(GCoreEndpoint epr:client.submit(query)){
			URI uri=epr.profile().endpointMap().get("gcube/datatransfer/agent/DataTransferAgent").uri();
			if(uri.getHost().equalsIgnoreCase(host))return uri.getPort();
		}
		throw new RuntimeException("No Agent service found");
	}
	
}
