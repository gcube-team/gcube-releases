/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
		synchronized(fallbackFile) {
			return fallbackFile;
		}
	}
	
	@Deprecated
	protected File getOldFallbackFile() {
		synchronized(fallbackFile) {
			String oldAccountingFileName = fallbackFile.getName();
			int lastIndexOf_ = oldAccountingFileName.lastIndexOf("_");
			
			oldAccountingFileName = oldAccountingFileName.substring(lastIndexOf_+1);
			oldAccountingFileName = oldAccountingFileName.replace(
					PersistenceBackendFactory.FALLBACK_FILENAME, "accountingFallback.log");
			
			return  new File(fallbackFile.getParentFile(), oldAccountingFileName);
		}
	}
	
	/**
	 * Move the fallbackFile to a new file with the same name by appending a suffix
	 * @param suffix
	 * @return the moved file if any, null otherwise
	 */
	protected File moveFallbackFile(String suffix) {
		synchronized(fallbackFile) {
			try {
				Path source = fallbackFile.toPath();
				if(!fallbackFile.exists()) {
					logger.trace("No fallback file {} found. Nothing to recover", source.toAbsolutePath().toString());
					return null;
				}
				Path target = source.resolveSibling(fallbackFile.getName()+suffix);
				logger.trace("Going to move fallback file {} to {}", source.toAbsolutePath().toString(), target.toAbsolutePath().toString());
				target = Files.move(source, source.resolveSibling(fallbackFile.getName()+suffix), StandardCopyOption.ATOMIC_MOVE);
				return target.toFile();
			} catch (Exception e) {
				String absPath = fallbackFile.getAbsolutePath();
				String error = String.format("It was not possibile to move %s to %s%s.", absPath, absPath, suffix);
				logger.error(error, e);
				return null;
			}
		}
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

	@Override
	protected void accountWithFallback(Record... records) throws Exception {
		for (Record record : records) {
			try {
				reallyAccount(record);
				logger.trace("{} accounted succesfully from {}", record.toString(), FallbackPersistenceBackend.class.getSimpleName());
			} catch (Throwable th) {
				logger.error("{} was not accounted at all", record.toString(), th);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reallyAccount(Record record) {
		String marshalled = null;
		try {
			marshalled = DSMapper.marshal(record);
			printLine(marshalled);
			logger.trace("{} accounted succesfully from {}", marshalled, this.getClass().getSimpleName());
		} catch(Throwable th) {
			logger.error("{} was not accounted at all", marshalled!=null ? marshalled : record.toString(), th);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * This method is synchronized on fallbackFile. Any actions which 
	 * has to modify, rename or delete the file must be synchronized on this 
	 * file. To retrieve it use {@link #getFallbackFile()} method.  
	 * This is intended for internal library usage only so that is protected.
	 * It 
	 */
	protected void printLine(String line) throws Exception {
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
