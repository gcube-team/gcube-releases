package org.gcube.data.spd.plugin.fwk.writers.rswrapper;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;


public abstract class AbstractLocalWrapper<T> extends AbstractWrapper<T> {

	private String locator;
	protected boolean closed = false;
	
	public AbstractLocalWrapper() {
		super();
		this.locator = UUID.randomUUID().toString();
	}

	public AbstractLocalWrapper(int queueSize) {
		super();
		this.locator = UUID.randomUUID().toString();
	}
	
	public abstract BlockingQueue<T> getQueue();
	
	@Override
	public String getLocator() {
		return this.locator;
	}
	
	@Override
	public boolean isClosed() {
		return closed;
	}
	
	public abstract void disposeBuffer();
	
}
