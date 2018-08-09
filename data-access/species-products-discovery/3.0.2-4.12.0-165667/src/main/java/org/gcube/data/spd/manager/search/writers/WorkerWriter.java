package org.gcube.data.spd.manager.search.writers;

import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerWriter<O> implements ClosableWriter<O>{

	Logger logger = LoggerFactory.getLogger(WorkerWriter.class);
	
	boolean closed;
	
	private ConsumerEventHandler<O> consumer;
	
	protected WorkerWriter(ConsumerEventHandler<O> consumer ){
		this.consumer = consumer;
	}
		
	@Override
	public boolean write(O t) {
		if (!consumer.onElementReady(t)){
			this.close();
			return false;
		}
		return true;
	}

	@Override
	public boolean write(StreamException error) {
		consumer.onError(error);
		if (error instanceof StreamBlockingException){
			this.close();
			return false;
		}
		else return true;
	}

	@Override
	public boolean isAlive() {
		return (!closed && consumer.isConsumerAlive());
	}

	@Override
	public void close() {
		closed = true;
		if (consumer!=null)
			consumer.onClose();
		else logger.trace("found null consumer");
	}

}