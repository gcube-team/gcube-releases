package org.gcube.datatransfer.agent.impl.event;

import java.util.ArrayList;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.db.DataTransferDBManager;
import org.gcube.datatransfer.agent.impl.jdo.Transfer;
import org.gcube.datatransfer.agent.stubs.datatransferagent.GetTransferOutcomesFault;
import org.gcube.datatransfer.agent.stubs.datatransferagent.MonitorTransferFault;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.messaging.MSGClient;
import org.gcube.datatransfer.common.messaging.messages.TransferResponseMessage;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.common.outcome.TreeTransferOutcome;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class ProduceResponse {

	private static String transferId;
	private static MSGClient msgClient = ServiceContext.getContext().getMsgClient();
	private static String scope = ScopeProvider.instance.get();
	private static DataTransferDBManager dbManager =  ServiceContext.getContext().getDbManager();
	private static final GCUBELog logger = new GCUBELog(ProduceResponse.class);



	public static void notify(String id){	
		ScopeProvider.instance.set(scope.toString());

		AgentFunctions agentFunctions = new AgentFunctions();
		transferId=id;


		// sourceEndpoint such as "pcitgt1012:8080" for example;			
		EndpointReferenceType endpoint = ServiceContext.getContext().getInstance().getAccessPoint().getEndpoint("gcube/datatransfer/agent/DataTransferAgent");	
		String address = endpoint.getAddress().toString();
		String sourceEndpoint=address;
		//we keep only the host name and the port
		String[] parts = address.split("/");
		if(parts.length>=3){
			sourceEndpoint = parts[0]+"//"+parts[2];
		}

		Transfer transfer = dbManager.getPersistenceManager().getObjectById(Transfer.class,transferId);

		boolean isItForTrees=false;
		if(transfer.getSourceID()!=null && transfer.getDestID()!=null)isItForTrees=true;


		String status = transfer.getStatus();
		//destPoint		
		String destEndpoint = transfer.getSubmitterEndpoint();

		if(destEndpoint==null){
			logger.error("submitterEndpoint is null for the transfer with id: "+transferId+" ..return..");
			return;
		}

		logger.debug("ProduceResponse -- Sending response to.. destEndpoint="+destEndpoint+" for the transfer with id="+transferId);

		TransferResponseMessage message = new TransferResponseMessage();
		message.setTransferId(transferId);
		message.setSourceEndpoint(sourceEndpoint);
		message.setDestEndpoint(destEndpoint);
		message.setTransferStatus(status);

		if(status.compareTo(TransferStatus.STARTED.toString())==0 ||
				status.compareTo(TransferStatus.QUEUED.toString())==0 ){
			//we send a MonitorTransferReportMessage
			MonitorTransferReportMessage monitorRes;
			try {
				//in case of trees it will not have -live-in-progress info .. 
				monitorRes=agentFunctions.monitorTransferWithProgress(transferId);
				message.setMonitorResponse(monitorRes);
			} catch (MonitorTransferFault e) {
				logger.error("ProduceTransferRes - MonitorTransferFault");
				e.printStackTrace();
				return;
			}
		}
		else{
			if(isItForTrees){
				TreeTransferOutcome treeOutcome=null;
				try {
					treeOutcome=dbManager.getTransferTreeOutCome(transferId);
					logger.debug("(it was a tree-based transfer) readTrees="+treeOutcome.getTotalReadTrees()+" - writtenTrees="+treeOutcome.getTotalWrittenTrees());
				} catch (Exception e) {
					logger.error("ProduceTransferRes(for trees) - Exception");
					e.printStackTrace();
				}
				message.setTreeOutcomeResponse(treeOutcome);
			}else{
				//we send an ArrayList<FileTransferOutcome>
				ArrayList<FileTransferOutcome> outcomesResponse = null;
				try {
					outcomesResponse = agentFunctions.getTransferOutcomes(transferId);
					logger.debug("(it was a file-based transfer) ...");
					message.setOutcomesResponse(outcomesResponse);
				} catch (GetTransferOutcomesFault e) {
					logger.error("ProduceTransferRes - GetTransferOutcomesFault");
					e.printStackTrace();
				} catch (Exception e) {
					logger.error("ProduceTransferRes - Exception");
					e.printStackTrace();
				}
				message.setOutcomesResponse(outcomesResponse);
			}
			//update the LastNotificationMsgSent field
			try {
				dbManager.updateLastNotificationMsgSent(transferId, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			msgClient.sendResponseMessage(ServiceContext.getContext(), message, GCUBEScope.getScope(scope));
		} catch (MalformedScopeExpressionException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
