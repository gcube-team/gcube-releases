/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.session;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.speciesdiscovery.server.stream.Aggregator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.AggregatorIterator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CloseableIterator;
import org.gcube.portlets.user.speciesdiscovery.shared.FetchingElement;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class FetchingSession<T extends FetchingElement> implements Closeable {

	protected Logger logger = Logger.getLogger(FetchingSession.class);

	protected FetchingBuffer<T> buffer;
	protected Fetcher<T> fetcher;
	protected Thread fetcherThread;
	protected CloseableIterator<T> source;
	protected AggregatorIterator<T> aggregatorIterator;

	public FetchingSession(CloseableIterator<T> source, FetchingBuffer<T> buffer) {
		this.source = source;
		this.buffer = buffer;
		this.fetcher = new Fetcher<T>(source,buffer);
	}

	public void addAggregator(Aggregator<T, ?> aggregator)
	{
		if (aggregatorIterator == null) setupAggregatorIterator();
		aggregatorIterator.addAggregator(aggregator);
	}

	protected void setupAggregatorIterator()
	{
		aggregatorIterator = new AggregatorIterator<T>(source);
		fetcher.setSource(aggregatorIterator);
	}

	public Aggregator<T, ?> getAggregator(String name)
	{
		if (aggregatorIterator!=null) return aggregatorIterator.getAggregator(name);
		return null;
	}

	public void startFetching()
	{
		fetcherThread = new Thread(fetcher);
		logger.info("fetcherThread: "+fetcherThread.getId()+" start...");
//		System.out.println("#######fetcherThread start");
		fetcherThread.start();
	}

	public void close() throws IOException
	{
		new Thread(){
			@Override
			public void run() {
				try {
					Long startTime =  System.currentTimeMillis();
					logger.trace("##new thread run for closing old fetcher... time: "+startTime);
					fetcher.close();
					Long endTime = System.currentTimeMillis() - startTime;
					String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
					logger.trace("##old fetcher has been closed.. in " + time);
					logger.info("fetcherThread: "+fetcherThread.getId()+" closed!");
				} catch (IOException e) {
					logger.error("An error occurred in fetcher Thread close ",e);
				}
			};
		}.start();

	}

	public boolean isComplete(){
		return fetcher.isComplete();
	}

	public FetchingBuffer<T> getBuffer() throws Exception {
		logger.trace("fetcherThread is alive: " + fetcherThread.isAlive());
		try {
			logger.trace("buffer size: " + buffer.size());
		} catch (SQLException e) {
			logger.error("error in getBuffer: " +e);
		}
		return buffer;
	}

	public int getBufferSize() throws Exception {
		return buffer.size();
	}
}
