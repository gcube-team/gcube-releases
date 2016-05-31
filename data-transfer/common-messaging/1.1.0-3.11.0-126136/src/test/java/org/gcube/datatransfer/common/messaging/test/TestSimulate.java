package org.gcube.datatransfer.common.messaging.test;

import java.net.URI;
import java.util.ArrayList;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.common.agent.Types.TransferOptions;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.messaging.MSGClient;
import org.gcube.datatransfer.common.messaging.MSGClientFactory;
import org.gcube.datatransfer.common.messaging.messages.TransferRequestMessage;
import org.gcube.datatransfer.common.messaging.subscriptions.TransferRequestSubscriptions;
import org.gcube.datatransfer.common.messaging.subscriptions.TransferResponseSubscriptions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestSimulate {

	//use fake hostnames for testing
	String schedulerEndpoint="pcitgt1012.cern.ch:8080";
	String agentEndpoint="pcitgt1012.cern.ch:8081";
	GCUBEScope scope =GCUBEScope.getScope("/gcube/devsec");
	public Logger logger = LoggerFactory.getLogger(TestSimulate.class);

	@Test
	public void process(){


		schedulerSubscribes();
		agentSubscribes();

		schedulerSends();
		
		try {// 2min
			Thread.sleep(120000);
		} catch (InterruptedException e) {
			logger.debug("\nTestSimulate (sleep 2 min)-- InterruptedException-Unable to sleep");
			e.printStackTrace();
		}
	}

	public void schedulerSubscribes(){
		ScopeProvider.instance.set("/gcube/devsec");
		//subscribe to the transfer response q
		String subscriberEndpoint=schedulerEndpoint;
		TransferResponseSubscriptions subscriber=new TransferResponseSubscriptions(subscriberEndpoint);
		subscriber.setScope(scope);
		try {
			subscriber.subscribe();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void agentSubscribes(){
		//subscribe to the transfer request q
		ScopeProvider.instance.set("/gcube/devsec");
		String subscriberEndpoint=agentEndpoint;
		TransferRequestSubscriptions subscriber=new TransferRequestSubscriptions(subscriberEndpoint);
		subscriber.setScope(scope);
		try {
			subscriber.subscribe();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public void schedulerSends(){
		//CREATE THE MSGClient
		ScopeProvider.instance.set("/gcube/devsec");
		MSGClient client=null;
		try {
			client = MSGClientFactory.getMSGClientInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//CREATE THE Request Message
		TransferRequestMessage msg = new TransferRequestMessage();
		msg.setSourceEndpoint(schedulerEndpoint);
		msg.setDestEndpoint(agentEndpoint);
		msg.setScope(scope.toString());
		
		//fill with fake parameters for testing
		ArrayList<URI> inputUris = null;
		ArrayList<URI> outputUris = null;
		TransferOptions transferOptions=new TransferOptions();
		transferOptions.setStorageType(storageType.DataStorage);
		
		msg.setInputUris(inputUris);
		msg.setOutputUris(outputUris);
		msg.setTransferOptions(transferOptions);
		
		if(client==null){logger.error("\nmsg client is null");return;}
		
			try {
				client.sendRequestMessage(null, msg, scope);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

	}

}
