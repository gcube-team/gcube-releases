package org.gcube.data.spd.plugin.fwk.writers;

public interface RecordWriter<T> {
	
	boolean put(T element);
	
	boolean put(Exception error);
	
	void close();
	
	boolean isClosed();
	
}
