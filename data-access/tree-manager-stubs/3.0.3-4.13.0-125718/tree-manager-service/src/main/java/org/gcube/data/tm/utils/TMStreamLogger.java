package org.gcube.data.tm.utils;

import static java.lang.Math.*;
import static java.lang.System.*;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.streams.delegates.StreamListener;
import org.gcube.data.streams.generators.Processor;
import org.gcube.data.trees.data.Node;

public class TMStreamLogger<N extends Node> extends Processor<N> implements StreamListener {
	
	private static GCUBELog logger = new GCUBELog(TMStreamLogger.class);

	static long count=0;
	static long starttime=0;
	static long size=0;

	static private String rformat = "processed %1d elements of avg size %2dKb in %3d ms (%4d/sec)";
	
	
	/**{@inheritDoc}*/
	@Override
	public void onStart() {
		logger.info("started processing");
		starttime=currentTimeMillis();
	}
		
	
	/**{@inheritDoc}*/
	@Override
	public void process(N node) {

		count++;
		size=size+node.size();
	}
	
	/**{@inheritDoc}*/
	@Override
	public void onEnd() {
		
		String report;
		if (starttime==0) //we may have iterated over no elements at all
			report="processed 0 elements";
		else {
			long time= currentTimeMillis()-starttime;
			long ratio = round(((double)count/time)*1000);
			long avgSize = round(((double)size/count)/1000);
			report = String.format(rformat,count,avgSize,time,ratio);	
		}
		
		logger.info(report);
	}
	
	@Override
	public void onClose() {
		
	}
	
}