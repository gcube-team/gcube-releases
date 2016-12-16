/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.gcube.documentstore.records.Record;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class FallbackPersistenceBackend extends PersistenceBackend {
	
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

	/**
	 * {@inheritDoc}
	 * This method is synchronized on {@link File} used, so any actions which 
	 * has to modify, rename or delete the file must be synchronized on this 
	 * file. To retrieve it use {@link #getFallbackFile()} method.  
	 * This is intended for internal library usage only so that is protected
	 */
	@Override
	protected void reallyAccount(Record record) throws Exception {
		printLine(String.valueOf(record));
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		// Nothing TO DO
	}
	
}
