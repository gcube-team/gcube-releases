package org.gcube.data.spd.plugin.fwk.writers;

import org.gcube.data.spd.model.exceptions.StreamException;




public interface ObjectWriter<T> {

	public boolean write(T t);	
	
	public boolean write(StreamException error);
	
	public boolean isAlive();
	
	
}
