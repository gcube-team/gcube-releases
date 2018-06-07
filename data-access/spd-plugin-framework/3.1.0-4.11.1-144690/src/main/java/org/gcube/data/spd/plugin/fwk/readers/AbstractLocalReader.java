package org.gcube.data.spd.plugin.fwk.readers;

import java.util.concurrent.BlockingQueue;

import org.gcube.data.spd.plugin.fwk.writers.rswrapper.AbstractLocalWrapper;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractLocalReader<T> implements Stream<T>{

	protected static final Logger logger = LoggerFactory.getLogger(AbstractLocalReader.class);
	
	protected BlockingQueue<T> queue;
	
	protected T element = null;
	
	protected int timeoutInSeconds= 2; 
	
	AbstractLocalWrapper<T> wrapper ;
	
	public AbstractLocalReader(AbstractLocalWrapper<T> wrapper) {
		queue = wrapper.getQueue();
		this.wrapper = wrapper;
	}


	
	public void setTimeoutInSeconds(int timeoutInSeconds) {
		this.timeoutInSeconds = timeoutInSeconds;
	}



	@Override
	public T next() {
		return element;
	}

	@Override
	public void remove() {}

	
}
