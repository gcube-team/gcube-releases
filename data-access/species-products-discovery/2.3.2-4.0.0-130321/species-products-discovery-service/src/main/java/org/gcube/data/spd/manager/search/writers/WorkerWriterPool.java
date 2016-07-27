package org.gcube.data.spd.manager.search.writers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WorkerWriterPool<O> implements ConsumerEventHandler<O>{

	Logger logger = LoggerFactory.getLogger(WorkerWriterPool.class);
	
	private int createdWriters = 0;
	
	List<ConsumerEventHandler<O>> consumers;
	
	
	public WorkerWriterPool(ConsumerEventHandler<O> ... consumers ){
		this.consumers = new ArrayList<ConsumerEventHandler<O>>();
		Collections.addAll(this.consumers, consumers);
		writers= new ArrayList<WorkerWriter<O>>();
	}
	
	
	List<WorkerWriter<O>> writers;
	
	public WorkerWriter<O> get(){
		WorkerWriter<O> writer = new WorkerWriter<O>(this);
		this.createdWriters++;
		writers.add(writer);
		return writer;
	}

	@Override
	public void onClose() {
		this.createdWriters--;		
		if (this.createdWriters == 0){
			for (ConsumerEventHandler<O> consumer : consumers){
				logger.trace("sending close to the consumer ("+consumer.getClass().getSimpleName()+")");
				consumer.onClose();
			}
			for (WorkerWriter<O> writer:writers)
				if (writer.isAlive())
						writer.close();
		}
	}

	@Override
	public synchronized boolean onElementReady(O element) {
		Iterator<ConsumerEventHandler<O>> it = consumers.iterator();
		for(;it.hasNext();){
			ConsumerEventHandler<O> consumer = it.next();
			boolean onElementWorked = consumer.onElementReady(element);
			//logger.trace("onElementReady called on "+consumer.getClass().getSimpleName()+" returned "+onElementWorked );
			if (!onElementWorked)
				it.remove();
		}
		//logger.trace("retained consumers are "+consumers.size());
		return consumers.size()>0;
	}

	@Override
	public void onError(StreamException exception) {
		for (ConsumerEventHandler<O> consumer : consumers)
			consumer.onError(exception);
	}

	@Override
	public boolean isConsumerAlive() {
		Iterator<ConsumerEventHandler<O>> consumerIt = consumers.iterator();
		boolean allDead= true;
		while (consumerIt.hasNext() && allDead){ 
			ConsumerEventHandler<O> consumer = consumerIt.next(); 
			boolean isAlive = consumer.isConsumerAlive();
			allDead = !isAlive && allDead;
		}
		return !allDead;
	}


	
}
