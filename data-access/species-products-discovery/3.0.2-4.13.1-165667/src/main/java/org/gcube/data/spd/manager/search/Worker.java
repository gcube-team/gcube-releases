package org.gcube.data.spd.manager.search;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.gcube.data.spd.manager.search.writers.ConsumerEventHandler;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Worker<I,O> implements Runnable, ConsumerEventHandler<I> {
	
	protected Logger logger = LoggerFactory.getLogger(Worker.class); 
	
	private LinkedBlockingQueue<I> queue = new LinkedBlockingQueue<I>();
	
	protected boolean stop = false;
		
	boolean producerClosed= false;
	
	boolean alive = true;
	
	private ClosableWriter<O> writer;
	
	public Worker(ClosableWriter<O> writer) {
		super();
		this.writer = writer;
	}

	public void run(){
		logger.trace(this.getClass().getSimpleName()+" - worker started");
		
		try{
			while(!stop && (!producerClosed || !queue.isEmpty()) && writer.isAlive() ){
				I element = null;
				try {
					element = queue.poll(2, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					logger.warn("interrupt exception worker ", e);
				}
				if (element!=null)
					execute(element, writer);
			}
		}catch (Throwable e) {
			logger.warn("strange error on worker ",e);
		}
		writer.close();
		this.alive = false; 
		logger.trace(this.getClass().getSimpleName()+" - worker stopped");
	}
			
	protected abstract void execute(I input, ObjectWriter<O> outputWriter);
		
	
	@Override
	public synchronized boolean onElementReady(I element) {
		if (!stop && writer.isAlive()){
			try {
				return queue.offer(element, 1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				logger.warn("error in event onReadyElement",e);
			}
		}
		return false;
	}
		
	@Override
	public void onClose(){
		this.producerClosed = true;
	}
	

	public ClosableWriter<O> getWriter() {
		return writer;
	}

	@Override
	public synchronized void onError(StreamException exception) {
		logger.warn("error on stream ",exception);
		if (exception instanceof StreamBlockingException ){
			this.stop=true;
		}
	}

	@Override
	public boolean isConsumerAlive() {
		return alive ;
	}
	
	public String descriptor(){
		return this.getClass().getSimpleName();
	}
	
}
