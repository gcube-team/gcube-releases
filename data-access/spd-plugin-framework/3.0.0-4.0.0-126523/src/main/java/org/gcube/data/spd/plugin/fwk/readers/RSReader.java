package org.gcube.data.spd.plugin.fwk.readers;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.StringField;
import java.net.URI;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RSReader<T> implements Iterator<T>{
	
	private static final Logger logger = LoggerFactory.getLogger(RSReader.class);
	private Iterator<GenericRecord> it;
	private ForwardReader<GenericRecord> reader;

	public RSReader(String locator) throws Exception{
		reader=new ForwardReader<GenericRecord>(new URI(locator));
		reader.setIteratorTimeout(3000);
		it =reader.iterator();
	}

	@Override
	public boolean hasNext() {
		if (it.hasNext()) return true;
		else {
			try {
				reader.close();
			} catch (GRS2ReaderException e) {
				logger.error("error closing reader",e);
			}
			return false;
		}
	}

	@Override
	public T next() {
		try {
			return transform(((StringField)it.next().getField("result")).getPayload());
		} catch (Exception e) {
			logger.error("error getting tree",e);
			return null;
		}
	}

	public abstract T transform(String serializedItem) throws Exception;
	
	@Override
	public void remove() {}
}
