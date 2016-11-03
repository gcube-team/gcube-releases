/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Utility class for convert an input into a different kind of output.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class CSVGenerator<I> implements CloseableIterator<List<String>> {
	
	protected Logger logger = Logger.getLogger(CSVGenerator.class);

	protected CloseableIterator<I> source;
	protected Converter<I, List<String>> converter;
	protected List<String> header;
	protected boolean writerHeader = true;

	public CSVGenerator(CloseableIterator<I> source, Converter<I, List<String>> converter, List<String> header) {
		this.source = source;
		this.converter = converter;
		this.header = header;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return writerHeader || source.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> next() {
		
		assert hasNext();
		
		if (writerHeader) {
			writerHeader = false;
			return header;
		}
		
		try {
			I input = source.next();
			List<String> output = converter.convert(input);
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
