package org.gcube.datatransfer.agent.impl.event;

import java.util.concurrent.FutureTask;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.db.DataTransferDBManager;
import org.gcube.datatransfer.agent.impl.utils.Utils;
import org.gcube.datatransfer.agent.impl.worker.Worker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.InputPattern;
import org.gcube.datatransfer.agent.stubs.datatransferagent.OutUriData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.StorageType;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferFault;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;
import org.gcube.datatransfer.common.agent.Types.StartTransferMessage;
import org.gcube.datatransfer.common.agent.Types.transferType;
import org.gcube.datatransfer.common.messaging.MessageChecker;
import org.gcube.datatransfer.common.messaging.messages.TransferRequestMessage;

import com.thoughtworks.xstream.XStream;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferRequestChecker extends MessageChecker<TransferRequestMessage>{
	public GCUBELog logger = new GCUBELog(TransferRequestChecker.class);
	//private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();   //not need anymore, I use the same id that i have in the scheduler
	private static DataTransferDBManager dbManager =  ServiceContext.getContext().getDbManager();

	public TransferRequestChecker (GCUBEScope scope, String subscriberEndpoint){
		super(scope,subscriberEndpoint);
	}

	public void check(TransferRequestMessage message){
		// checking for the right subscriber
		String destEndpoint=message.getDestEndpoint();
		if(destEndpoint==null)return;
		else if(destEndpoint.compareTo(subscriberEndpoint)==0){
			handleMessage(message);
		}
	}


	public void handleMessage(TransferRequestMessage message){
		logger.debug("TransferRequestChecker - Start Transfer invoked in scope " + message.getScope());
		String id =  message.getTransferId();
		AgentFunctions agentFunctions=new AgentFunctions();
		//optionsFromCommon.getType()
		try {			
			DestData destData;
			SourceData sourceData;
			logger.debug("treesource="+message.getTreeSourceID()+" -- destId="+message.getTreeDestID()+" -- pattern="+message.getTreePattern());
			if(message.getTreeSourceID()==null){//filebased
				//setting source data
				sourceData=new SourceData();
				String[] arrayUris = new String[message.getInputUris().size()];
				for(int i=0;i<arrayUris.length;i++){
					arrayUris[i]=message.getInputUris().get(i).toString();
				}
				sourceData.setInputURIs(arrayUris);
				sourceData.setScope(message.getScope());
				sourceData.setType(TransferType.FileBasedTransfer);

				//mapping dest data
				destData=new DestData();
				destData.setScope(message.getScope());
				destData.setType(TransferType.FileBasedTransfer.getValue());
				OutUriData outUri=new OutUriData();				
				//set options
				outUri.setOptions(AgentFunctions.getMappedTransferOptions(message.getTransferOptions()));
				//set outputUris (or many uris or just one when dest folder)
				if(outUri.getOptions().getStorageType().equals(StorageType.DataStorage)){
					String[] arrayOutputUris = new String[message.getOutputUris().size()];
					for(int i=0;i<arrayOutputUris.length;i++){
						arrayOutputUris[i]=message.getOutputUris().get(i).toString();
					}
					outUri.setOutUris(arrayOutputUris);
				}else{
					String outputFolder=message.getDestination();
					if (outputFolder.startsWith("/"))
						outputFolder = outputFolder.substring(1);
					outUri.setOutUris(new String[] {outputFolder});
				}			
				destData.setOutUri(outUri);
			}
			else{//tree based
				destData = new DestData();
				destData.setOutSourceId(message.getTreeDestID());
				destData.setScope(ScopeProvider.instance.get());

				sourceData = new SourceData();
				InputPattern input =  new InputPattern();
				XStream xstreamForPattern = new XStream();
				Pattern patternInput = 	(Pattern) xstreamForPattern.fromXML(message.getTreePattern());
				try {
					input.setPattern(Utils.toHolder(patternInput));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				input.setSourceId(message.getTreeSourceID());
				sourceData.setInputSource(input);
				sourceData.setScope(ScopeProvider.instance.get());
				sourceData.setType(TransferType.TreeBasedTransfer);
			}

			FutureTask<Worker> task = agentFunctions.startAsyncTask(id, sourceData, destData);
			//CHANGED - Now we set the workerMap inside the startAsyncTask operation..
			//agentFunctions.getResource().getWorkerMap().put(id, task);		

		} catch (Exception e) {
			logger.error("TransferRequestChecker - Unable to perform the transfer", e);
			try {
				throw Utils.newFault(new TransferFault(), e);
			} catch (TransferFault e1) {
				e1.printStackTrace();
			}
			return;
		}

		logger.info("TransferRequestChecker - transfer id : "+ id +" - submitterEndpoint : "+message.getSourceEndpoint());

		//update the submitter
		try {
			dbManager.updateTransferSubmitterEndpoint(id, message.getSourceEndpoint());
		}catch(Exception e){
			e.printStackTrace();
			logger.error("exception when call dbManager.updateTransferSubmitterEndpoint ... ");
		}
		//update the LastNotificationMsgSent field
		try {
			dbManager.updateLastNotificationMsgSent(id, false);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exception when call dbManager.updateLastNotificationMsgSent ... ");
		}
	}


}
