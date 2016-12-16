/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.datafetcher;

import java.io.Closeable;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.converter.CloseableIterator;
import org.gcube.portlets.user.geoexplorer.shared.FetchingElement;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class Fetcher<T extends FetchingElement> implements Runnable, Closeable {

	protected Logger logger = Logger.getLogger(Fetcher.class);
	
	protected FetchingBuffer<T> buffer;
	protected CloseableIterator<T> source;
	protected boolean complete = false;

	public Fetcher(CloseableIterator<T> source, FetchingBuffer<T> buffer) {
		this.source = source;
		this.complete = false;
		this.buffer = buffer;
	}

	/**
	 * @return the source
	 */
	public CloseableIterator<T> getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(CloseableIterator<T> source) {
		this.source = source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {

		T next = null;
		int count = 0;
		try {

			while(source!=null && source.hasNext() && !complete) {
				
				try {
					next = source.next();
	
					if(next!=null){
//						logger.trace("item "+count++ +" fetch new row " +next.getId());
						count++;
						buffer.add(next);
					}
					else{
						logger.trace("fetch new row: null");
					}
					
//					if(count == 10){
//						logger.trace("EXIT EXIT MANUALE");
//						silentClose();
//					}
				
				} catch (Exception e) {
					logger.error("Error in source.next() " + e.getMessage(), e);
					e.printStackTrace();
					silentClose();
				}
				
			
			}
			
		} catch (Exception e) {
			logger.error("Error in add row " + e.getMessage());
//			System.out.println("Error in add row " + e.getMessage());
//			e.printStackTrace();
			silentClose();
		}
		
		if(source==null)
			logger.warn("exit because source iterator is null");
		
		logger.trace("fetching completed, added "+count+ " item to buffer");
		complete = true;
		
//		System.out.println("#################################TOTAL " + count);
	}
	
	protected void silentClose()
	{
		try {
			close();
		} catch (IOException e) {
			logger.error("Error during silent close", e);
		}
	}

	public void close() throws IOException
	{
		complete = true;
		source.close();
	}

	public boolean isComplete()
	{
		return complete;
	}	
}
