package org.gcube.data.harmonization.occurrence;

import org.gcube.data.harmonization.occurrence.impl.readers.StreamProgress;


public interface OccurrenceStreamer {

	public void streamData();
	public StreamProgress getProgress();
	public String getLocator() throws Exception;
}
