package org.gcube.data.spd.manager.search;

import org.gcube.data.spd.manager.search.writers.ConsumerEventHandler;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventDispatcher<I> implements ConsumerEventHandler<I>{

	protected Logger logger = LoggerFactory.getLogger(EventDispatcher.class);
	
	private ConsumerEventHandler<I> standardWorker;
	private ConsumerEventHandler<I> alternativeWorker;
	
	boolean alternativeClosed= false, standardClosed = false;
	
	public EventDispatcher(ConsumerEventHandler<I> standardWorker,
			ConsumerEventHandler<I> alternativeWorker) {
		super();
		this.standardWorker = standardWorker;
		this.alternativeWorker = alternativeWorker;
	}

	@Override
	public boolean onElementReady(I element) {
		boolean sendToStandardWriter = sendToStandardWriter(element);
		if (sendToStandardWriter){
			if (!standardClosed){
				standardClosed = !standardWorker.onElementReady(element);
				return !standardClosed;
			}
		}else{
			if (!alternativeClosed){
				alternativeClosed= !alternativeWorker.onElementReady(element);
				return !alternativeClosed; 
			}
		}
		return (!standardClosed && !alternativeClosed);
	}

	@Override
	public void onClose() {
		logger.trace("on close called in "+this.getClass().getSimpleName());
		standardWorker.onClose();
		alternativeWorker.onClose();		
	}
		
	@Override
	public void onError(StreamException exception) {
		standardWorker.onError(exception);
		alternativeWorker.onError(exception);
	}

	public abstract boolean sendToStandardWriter(I input);

	@Override
	public boolean isConsumerAlive() {
		return (!standardClosed && !alternativeClosed);
	}
	
}
