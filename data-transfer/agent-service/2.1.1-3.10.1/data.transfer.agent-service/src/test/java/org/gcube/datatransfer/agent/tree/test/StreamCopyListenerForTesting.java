package org.gcube.datatransfer.agent.tree.test;


import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.streams.delegates.StreamListenerAdapter;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.trees.data.Tree;
import org.gcube.datatransfer.agent.impl.event.Events.TransferTopics;
import org.gcube.datatransfer.agent.impl.event.TransferOutcome;
import org.gcube.datatransfer.agent.impl.streams.Counter;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class StreamCopyListenerForTesting extends StreamListenerAdapter{

	protected GCUBELog logger = new GCUBELog(this.getClass());
	Counter counter;
	
	GCUBEEvent<TransferTopics,TransferOutcome> event = null;

	public StreamCopyListenerForTesting(Counter counter){
		this.counter= counter;
	}
	public StreamCopyListenerForTesting(){
	}
	@Override
	public void onEnd() {
		sendEvent(TransferTopics.TRANSFER_END,"Transfer Completed");

	}
	
	@Override
	public void onStart() {
		sendEvent(TransferTopics.TRANSFER_START,"Transfer Started");
	}


	public void sendEvent(TransferTopics topic ,String message){
		try{
			if(topic==null)System.out.println("transfer topic == null");
			if(topic!=null){
				if(counter==null){
					System.out.println("transfer topic = "+topic.name()+" - message="+message);
				}else System.out.println("transfer topic = "+topic.name()+" - message="+message+" -  detected trees = "+counter.total);
			}
			}catch(Exception e){
			e.printStackTrace();
		}
	}
	

}