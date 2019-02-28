package org.gcube.data.spd.utils;

import java.io.IOException;

import org.gcube.data.spd.plugin.fwk.writers.RecordWriter;
import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JerseyWriter<T,K> implements RecordWriter<T> {

	Logger logger = LoggerFactory.getLogger(JerseyWriter.class);

	private ChunkedOutput<K> output;

	private boolean isFirst = true;

	public JerseyWriter(ChunkedOutput<K> out) {
		this.output = out;
	}

	@Override
	public boolean put(T element) {
		try {
			K convertedElement = convert(element);
			if (isFirst){
				output.write(header());
				isFirst = false;
			}
			output.write(convertedElement);
			return true;
		} catch (IOException e) {
			logger.warn("error writing element",e);
			return false;
		}
	}

	@Override
	public boolean put(Exception error) {
		return true;
	}

	@Override
	public void close() {
		if (!this.isClosed()){
			logger.info("closing the writer");
			try {
				if (isFirst) output.write(header());
				this.output.write(footer());
				this.output.close();
				this.output = null;
			} catch (IOException e) {
				logger.warn("error closing output",e);
			}
		}
	}

	@Override
	public boolean isClosed() {
		return this.output==null || output.isClosed();
	}

	public abstract K convert(T input);
	public K header(){return null;}
	public K footer(){return null;}

}
