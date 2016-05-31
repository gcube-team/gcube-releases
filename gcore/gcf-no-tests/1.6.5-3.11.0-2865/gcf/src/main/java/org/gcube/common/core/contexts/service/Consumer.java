package org.gcube.common.core.contexts.service;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEServiceContext.RILifetimeEvent;
import org.gcube.common.core.contexts.GCUBEServiceContext.RILifetimeTopic;
import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Partial implemention of {@link GCUBEConsumer} of RI lifetime events.
 * @author Fabio Simeoni (University of Strathclyde)
 */
public class Consumer implements GCUBEConsumer<RILifetimeTopic,GCUBEServiceContext> {
	/**Object logger.*/
	protected GCUBELog consumerLogger = new GCUBELog(this);
	
	/**Sets the instance logger.
	 * @param logger the logger;*/
	public void setLogger(GCUBELog logger) {this.consumerLogger = logger;}
	
	/** Receives RI lifetime events and dispatches them to topic-specific callbacks.*/
	synchronized public <T1 extends RILifetimeTopic, P1 extends GCUBEServiceContext> void onEvent(GCUBEEvent<T1, P1>... events) {
		if (events==null || events.length==0) return;
		for (GCUBEEvent<T1,P1> e : events) {
			try {
				RILifetimeEvent event = (RILifetimeEvent) e;
				switch (event.getTopic()) {
					case DEPLOYED : this.onRIDeployed(event);break;
					case INITIALISED : this.onRIInitialised(event);break;
					case READY : this.onRIReady(event);break;
					case FAILED : this.onRIFailed(event);break;
					case UPDATED : this.onRIUpdated(event);break;
					case STATECHANGE : this.onRIStateChange(event);break;
					case DOWN : this.onGHNShutdown(event);break;
				}
			}
			catch(Throwable trouble) {//fatal log + a FAILED event if not already failed
				consumerLogger.setContext(e.getPayload());
				String errMsg="Could not process RI event";
				if (e.getTopic()!=RILifetimeTopic.FAILED) {
					consumerLogger.fatal(errMsg,trouble);
					events[0].getPayload().setStatus(Status.FAILED);//NOTE: will generate runtime error if consumer not a core component
				}
				else consumerLogger.warn(errMsg,trouble);
			}
		}	
	}
		
	/**Returns the list of topics handled by the consumer.
	 * @return the topics.*/
	public RILifetimeTopic[] getTopics() {return RILifetimeTopic.values();}

	/**gHN shutdown even. callback
	 * @param event the event.
	 * @throws Exception if the callback could not complete successfully.*/
	synchronized protected void onGHNShutdown(RILifetimeEvent event) throws Exception {}
	/** Deployment event callback.
	 * @param event the event.
	 * @throws Exception if the callback could not complete successfully.*/
	synchronized protected void onRIDeployed(RILifetimeEvent event) throws Exception {}
	/** Initialisation event callback.
	 * @param event the event.
	 * @throws Exception if the callback could not complete successfully.*/
	synchronized protected void onRIInitialised(RILifetimeEvent event) throws Exception {}
	/** Ready event callback.
	 * @param event the event.
	 * @throws Exception if the callback could not complete successfully.*/
	synchronized protected void onRIReady(RILifetimeEvent event) throws Exception {}
	/** Failure event callback.
	 * @param event the event.
	 * @throws Exception if the callback could not complete successfully.*/
	synchronized protected void onRIFailed(RILifetimeEvent event) throws Exception {} 
	/** Update event callback.
	 * @param event the event.
	 * @throws Exception if the callback could not complete successfully.*/
	synchronized protected void onRIUpdated(RILifetimeEvent event) throws Exception {} 
	/** State change event callback.
	 * @param event the event.
	 * @throws Exception if the callback could not complete successfully.*/
	synchronized protected void onRIStateChange(RILifetimeEvent event) throws Exception {} 
}
	
