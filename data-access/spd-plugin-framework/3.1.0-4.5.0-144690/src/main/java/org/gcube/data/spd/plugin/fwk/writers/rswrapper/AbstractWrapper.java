package org.gcube.data.spd.plugin.fwk.writers.rswrapper;

import org.gcube.data.spd.model.exceptions.InvalidRecordException;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.exceptions.WrapperAlreadyDisposedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWrapper<T> {

	Logger logger= LoggerFactory.getLogger(AbstractWrapper.class);
	
	protected int links; 
	
	public abstract String getLocator();
	
	public abstract boolean add(T result) throws InvalidRecordException, WrapperAlreadyDisposedException;
	
	public abstract boolean add(StreamException result) throws InvalidRecordException, WrapperAlreadyDisposedException;
		
	public  abstract void close();
	
	public synchronized void unregister(){
		logger.info(Thread.currentThread().getId()+" - closing wrapper");
		links--;
		if (links<=0){
			if (!this.isClosed()){
				this.close();
			}
			else throw new IllegalStateException("wrapper already closed");
		}
	}
	
	
	
	public synchronized void register(){
		this.links++;
	}
	
	public abstract boolean isClosed();
	
}
