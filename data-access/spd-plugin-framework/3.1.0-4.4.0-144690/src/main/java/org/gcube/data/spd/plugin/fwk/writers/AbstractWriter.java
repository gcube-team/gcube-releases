package org.gcube.data.spd.plugin.fwk.writers;

import org.gcube.data.spd.plugin.fwk.writers.rswrapper.AbstractWrapper;



public abstract class AbstractWriter<T> implements ClosableWriter<T>{
		
	//private Logger logger= LoggerFactory.getLogger(AbstractWriter.class);
	
	protected boolean closed;
	
	private int links =0;
	
	private AbstractWrapper<T> wrapper;
	
	protected int wrote;
	
	protected AbstractWriter(AbstractWrapper<T> wrapper) {
		this.wrapper = wrapper;
		this.wrapper.register();
	}

	protected AbstractWrapper<T> getWrapper() {
		return wrapper;
	}
	
	public synchronized void register(){
		links++;
	}
	
	public synchronized void register(int links){
		this.links+=links;
	}
	
	public synchronized void close() {
		links--;
		if (links<=0){
			if (!closed){
				if (!this.wrapper.isClosed())this.wrapper.unregister();
				closed=true;
			}  else throw new IllegalStateException("writer already closed");
		}
	}
	
}
