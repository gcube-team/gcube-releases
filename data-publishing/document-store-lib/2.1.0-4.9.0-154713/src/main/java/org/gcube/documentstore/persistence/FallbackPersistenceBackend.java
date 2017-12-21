/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class FallbackPersistenceBackend extends PersistenceBackend {
	
	
	private static final Logger logger = LoggerFactory.getLogger(FallbackPersistenceBackend.class);
	private File fallbackFile; 
	
	/**
	 * @return the fallbackFile
	 */
	protected File getFallbackFile() {
		return fallbackFile;
	}
	
	protected FallbackPersistenceBackend(File fallbackFile) {
		super(null);
		this.fallbackFile = fallbackFile;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareConnection(PersistenceBackendConfiguration configuration) {
		// Nothing TO DO
	}

	@Override
	protected void openConnection() throws Exception {
		// Nothing TO DO
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		// Nothing TO DO
	}

	

	/**
	 * {@inheritDoc}
	 * This method is synchronized on {@link File} used, so any actions which 
	 * has to modify, rename or delete the file must be synchronized on this 
	 * file. To retrieve it use {@link #getFallbackFile()} method.  
	 * This is intended for internal library usage only so that is protected
	 */
	@Override
	protected void reallyAccount(Record record) throws Exception {
		String marshalled = DSMapper.marshal(record);
		logger.debug("reallyAccount:{}",marshalled);
		printLine(marshalled);
	}

	public void printLine(String line) throws Exception {
		synchronized (fallbackFile) {
			try(FileWriter fw = new FileWriter(fallbackFile, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)){
					out.println(line);
					out.flush();
			} catch( IOException e ){
			   throw e;
			}
		}
	}

	@Override
	protected void closeConnection() throws Exception {
		// Nothing TO DO
		
	}
	
	@Override
	public boolean isConnectionActive() throws Exception{
		return true;
	}

	@Override
	protected void clean() throws Exception {
		// Nothing TO DO
	}
	
	
}
