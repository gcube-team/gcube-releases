package org.gcube.data.spd.caching;

import javax.xml.bind.JAXBException;

import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheWriter<T extends ResultElement> implements ObjectWriter<T>, ClosableWriter<T> {

	Logger logger= LoggerFactory.getLogger(CacheWriter.class);
	
	ObjectWriter<T> writer;
	
	QueryCache<T> cache;
			
	boolean error = false;
	
	boolean closed = false;
	
	public CacheWriter(ObjectWriter<T> writer, QueryCache<T> cache) {
		super();
		this.writer = writer;
		this.cache = cache;
	}

	@Override
	public void close() {
		logger.trace("closing cachewriter with error "+error);
		closed= true;
		if (!error) cache.closeStore();
		else cache.setValid(false);
	}


	@Override
	public boolean write(T t) {
		T copyObj = null;
		try{
			copyObj = Util.copy(t);
			boolean external = writer.write(t);
			if (!writer.isAlive()) error= true;		
			if (!error) cache.store(copyObj);
			return external;
		}catch (JAXBException e) {
			logger.warn("error copying element "+t.getId()+" in "+t.getProvider(), e);
			return false;
		}
		
	}

	@Override
	public boolean write(StreamException error) {
		if (error instanceof StreamBlockingException )
			this.error= true;
		return true;
	}

	@Override
	public boolean isAlive() {
		if (!closed && !writer.isAlive())
			error = true;
		return writer.isAlive();
	}

}
