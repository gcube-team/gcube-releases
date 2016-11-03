package org.gcube.data.spd.plugin.fwk.writers;


public interface ClosableWriter<T> extends ObjectWriter<T> {

	
	public void close();
}
