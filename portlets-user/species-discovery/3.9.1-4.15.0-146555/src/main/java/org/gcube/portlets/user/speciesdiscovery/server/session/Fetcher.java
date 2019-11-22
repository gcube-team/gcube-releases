/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.server.session;

import java.io.Closeable;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CloseableIterator;
import org.gcube.portlets.user.speciesdiscovery.shared.FetchingElement;

/**
 * The Class Fetcher.
 *
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 * updated by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @param <T> the generic type
 */
public class Fetcher<T extends FetchingElement> implements Runnable, Closeable {

	protected Logger logger = Logger.getLogger(Fetcher.class);
	protected final int MAX_CONSECUTIVE_ATTEMPTS_ON_NULL = 2;
	protected FetchingBuffer<T> buffer;
	protected CloseableIterator<T> source;
	protected boolean complete = false;

	/**
	 * Instantiates a new fetcher.
	 *
	 * @param source the source
	 * @param buffer the buffer
	 */
	public Fetcher(CloseableIterator<T> source, FetchingBuffer<T> buffer) {
		this.source = source;
		this.complete = false;
		this.buffer = buffer;
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public CloseableIterator<T> getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 *
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
		int countNullItems = 0;
		try {

			while(source!=null && source.hasNext() && !complete) {

				try {
					next = source.next();

					if(next!=null){
						logger.debug("item "+count++ +" fetch new row: "+next.getId());
						buffer.add(next);
						countNullItems = 0;
					}
					else{
						countNullItems++;
						logger.warn("fetch new row is null!! Number of null value/s: "+countNullItems);
						if(MAX_CONSECUTIVE_ATTEMPTS_ON_NULL==countNullItems){
							logger.warn("Fetched "+MAX_CONSECUTIVE_ATTEMPTS_ON_NULL+ " null rows, MAX ATTEMPTS reached, complete fetch true and closing stream!!");
							silentClose();
						}
					}

				} catch (Exception e) {
					logger.error("Error in source.next() " + e.getMessage(), e);
					silentClose();
				}
			}

		} catch (Exception e) {
			logger.error("Error in add row " + e.getMessage(), e);
			silentClose();
		}

		if(source==null)
			logger.warn("exit because source iterator is null");

		logger.trace("exit fetch run - complete true");
		complete = true;
	}

	/**
	 * Silent close.
	 */
	protected void silentClose()
	{
		try {
			close();
		} catch (IOException e) {
			logger.error("Error during silent close", e);
		}
	}

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException
	{
		logger.info("Fetcher closing iterator!!");
		complete = true;
		source.close();
	}

	/**
	 * Checks if is complete.
	 *
	 * @return true, if is complete
	 */
	public boolean isComplete()
	{
		return complete;
	}
}
