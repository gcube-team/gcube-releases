package org.gcube.data.harmonization.occurrence.impl.readers;

import java.io.File;

import org.gcube.data.harmonization.occurrence.OccurrenceStreamer;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OccurrenceReader implements OccurrenceStreamer{

	protected static final Logger logger = LoggerFactory.getLogger(OccurrenceReader.class);
	
	protected StreamProgress progress=new StreamProgress();
	protected ResultWrapper<OccurrencePoint> wrapper;
	protected File toRead;
	protected ParserConfiguration configuration;
	
	public OccurrenceReader(File toRead,
			ParserConfiguration configuration) {
		super();		
		this.toRead = toRead;
		this.configuration = configuration;
	}
	
	@Override
	public StreamProgress getProgress() {
		return progress;
	}
	public void setWrapper(ResultWrapper<OccurrencePoint> wrapper) {
		this.wrapper = wrapper;
	}
	@Override
	public String getLocator() throws Exception {
		return wrapper.getLocator();
	}
}
