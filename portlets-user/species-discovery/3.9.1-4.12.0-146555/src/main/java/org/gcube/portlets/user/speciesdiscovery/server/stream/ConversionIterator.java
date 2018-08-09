/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Utility class for convert an input into a different kind of output.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ConversionIterator<I,O> implements CloseableIterator<O> {
	
	protected Logger logger = Logger.getLogger(ConversionIterator.class);

	protected CloseableIterator<I> source;
	protected Converter<I, O> converter;

	public ConversionIterator( CloseableIterator<I> source, Converter<I, O> converter) {
		this.source = source;
		this.converter = converter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public O next() {
		
		assert hasNext();
		try {
			I input = source.next();
			
			////Another check
			if(input==null){
				logger.warn("Skipping conversion source.next() is Null!!");
				return null;
			}
			
			O output = converter.convert(input);
			return output;
		} catch (Exception e) {
			logger.error("Error converting element", e);
		}
		return null;
	}
	

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		source.close();
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() {}
}
