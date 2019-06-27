package org.gcube.portlets.user.geoexplorer.server.datafetcher;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.converter.CloseableIterator;
import org.gcube.portlets.user.geoexplorer.server.service.dao.MetadataPersistence;
import org.gcube.portlets.user.geoexplorer.shared.FetchingElement;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class FetchingSession<T extends FetchingElement> implements Closeable {

	protected Logger logger = Logger.getLogger(FetchingSession.class);

	protected FetchingBuffer<T> buffer;
	protected Fetcher<T> fetcher;
	protected Thread fetcherThread;
	protected CloseableIterator<T> source;

	private int totalMetadata;

	private MetadataPersistence persistence;

	public FetchingSession() {} //FOR SERIALIZATION

	public FetchingSession(CloseableIterator<T> source, FetchingBuffer<T> buffer, int totalMetadata, MetadataPersistence persistence) {
		this.source = source;
		this.buffer = buffer;
		this.fetcher = new Fetcher<T>(source,buffer);
		this.totalMetadata = totalMetadata;
		this.persistence = persistence;
	}
	
	public void startFetching()
	{
		fetcherThread = new Thread(fetcher);
		logger.trace("###fetcherThread start");
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
					//logger.trace("###fetcherThread close - OK");

					Long endTime = System.currentTimeMillis() - startTime;
					String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
					
					logger.trace("##old fetcher has been closed.. in " + time);
					
				} catch (IOException e) {
					logger.error("An error occurred in fetcher Thread close ",e);
					e.printStackTrace();
				}
			};
		}.start();

	}

	public boolean isComplete()
	{
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

	public int getTotalMetadata() {
		return totalMetadata;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FetchingSession [totalMetadata=");
		builder.append(totalMetadata);
		builder.append("]");
		return builder.toString();
	}

	public MetadataPersistence getPersistence() {
		return persistence;
	}

	public void setPersistence(MetadataPersistence persistence) {
		this.persistence = persistence;
	}
}
