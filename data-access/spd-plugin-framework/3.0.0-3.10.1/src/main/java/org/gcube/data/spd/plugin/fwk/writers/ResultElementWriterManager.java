package org.gcube.data.spd.plugin.fwk.writers;

import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ResultElementWriterManager<T extends ResultElement> extends WriterManager<T> {

	private static final Logger logger = LoggerFactory.getLogger(ResultElementWriterManager.class);
	
	protected String provider;

	public ResultElementWriterManager(String provider) {
		super();
		this.provider = provider;
	}

	@Override
	public T enrich(T obj) {
		try{
			return _enrich(Util.<T>copy(obj));
		}catch (Exception e) {
			logger.error("error enriching object",e);
			return null;
		}
	}
	
	protected abstract T _enrich(T obj);
	
	
	
	
}
