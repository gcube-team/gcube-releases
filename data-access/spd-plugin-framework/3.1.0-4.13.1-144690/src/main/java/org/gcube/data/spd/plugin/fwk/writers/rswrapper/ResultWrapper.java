package org.gcube.data.spd.plugin.fwk.writers.rswrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.gcube.data.spd.model.exceptions.InvalidRecordException;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.exceptions.WrapperAlreadyDisposedException;
import org.gcube.data.spd.plugin.fwk.writers.RecordWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResultWrapper<T> extends AbstractWrapper<T>{ 

	
	private static final Logger logger = LoggerFactory.getLogger(ResultWrapper.class);
	
	private static Map<String, ResultWrapper<?>> wrapperLocatorMap = new HashMap<String, ResultWrapper<?>>();
	
	public static ResultWrapper<?> getWrapper(String locator){
		return wrapperLocatorMap.get(locator);
	}
	
	private String locator;
	
	private RecordWriter<T> writer=null;
	
	
	public ResultWrapper(RecordWriter<T> rw) {
		this.writer = rw;
		this.locator = UUID.randomUUID().toString();
	}
	
	
	
	public synchronized boolean add(T input) throws InvalidRecordException, WrapperAlreadyDisposedException{
				
		try {
			return writer.put(input);
		}catch (Exception e) {
			logger.trace("the writer is already disposed (trying to write something when it is closed)");
			throw new WrapperAlreadyDisposedException(e);
		}
	}
	
	
	public void close(){
		this.writer.close();
	}

	
	@Override
	public boolean isClosed() {
		return this.writer.isClosed();
	}

	@Override
	public boolean add(StreamException result) throws InvalidRecordException,
			WrapperAlreadyDisposedException {
		try {
			return writer.put(result);
		}catch (Exception e) {
			logger.trace("the writer is already disposed (trying to write something when it is closed)");
			throw new WrapperAlreadyDisposedException(e);
		}
	}

	@Override
	public String getLocator() {
		return this.locator;
	}
	
}
