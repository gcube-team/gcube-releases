package org.gcube.data.streams.generators;

import static java.lang.Math.*;
import static java.lang.System.*;

import org.gcube.data.streams.delegates.StreamListener;
import org.gcube.data.streams.delegates.StreamListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A pass-through {@link Generator} that acts as a {@link StreamListener} for throughput logging purposes.
 * 
 * @author Fabio Simeoni
 *
 * @param <E> the type of stream elements
 */
public class LoggingListener<E> extends StreamListenerAdapter implements Generator<E,E> {

	static Logger log = LoggerFactory.getLogger(LoggingListener.class);
	
	static long count=0;
	static long starttime=0;

	static private String rformat = "streamed %1d elements in %2d ms (%3d/sec)";
	
	
	/**{@inheritDoc}*/
	@Override
	public void onStart() {
		log.info("started processing");
		starttime=currentTimeMillis();
	}
		
	
	/**{@inheritDoc}*/
	@Override
	public E yield(E element) {

		count++;
		return element;
	}
	
	/**{@inheritDoc}*/
	@Override
	public void onClose() {
		
		String report;
		if (starttime==0) //we may have iterated over no elements at all
			report="processed 0 elements";
		else {
			long time= currentTimeMillis()-starttime;
			long ratio = round(((double)count/time)*1000);
			report = String.format(rformat,count,time,ratio);	
		}
		
		log.info(report);
	}
}
