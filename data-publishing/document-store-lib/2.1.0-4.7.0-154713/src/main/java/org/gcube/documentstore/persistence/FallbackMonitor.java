/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used as scheduled thread to check if some records 
 * where persisted in fallback file to retry 
 * to persist them with the discovered PersistenceBackend
 * @author Luca Frosini (ISTI - CNR)
 */
public class FallbackMonitor implements Runnable {
	
	private final static Logger logger = LoggerFactory.getLogger(FallbackMonitor.class);
	
	private final static String ELABORATION_FILE_SUFFIX = ".ELABORATION";
	private final static String ELABORATION_FILE_NOT_DELETED_SUFFIX = ".ELABORATION.NOT-DELETED";

	protected final PersistenceBackend persistenceBackend;
	
	public final static int INITIAL_DELAY = 1;
	public final static int DELAY = 10;
	public final static TimeUnit TIME_UNIT = TimeUnit.MINUTES;
	
	public FallbackMonitor(PersistenceBackend persistenceBackend){
		this(persistenceBackend,true);
	}
	
	public FallbackMonitor(PersistenceBackend persistenceBackend, boolean schedule){
		this.persistenceBackend = persistenceBackend;
		if(schedule){
			ExecutorUtils.scheduler.scheduleAtFixedRate(this, INITIAL_DELAY, DELAY, TimeUnit.MINUTES);
		}
	}
	
	protected void elaborateFile(File elaborationFile){
		try(BufferedReader br = new BufferedReader(new FileReader(elaborationFile))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	try {
		    		Record record = RecordUtility.getRecord(line);		    		
		    		persistenceBackend.accountWithFallback(record);
		    	} catch(Exception e){
		    		logger.error("Was not possible parse line {} to obtain a valid Record. Going to writing back this line as string fallback file.", line, e);
		    		FallbackPersistenceBackend fallbackPersistenceBackend = persistenceBackend.getFallbackPersistence();
		    		try {
						fallbackPersistenceBackend.printLine(line);
					} catch (Exception e1) {
						logger.error("Line {} will be lost", line, e1);
					}
		    	}
		    }
		} catch (FileNotFoundException e) {
			logger.error("File non trovato", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
	}
	
	@Deprecated
	protected void manageOldAccountingFile(){
		FallbackPersistenceBackend fallbackPersistenceBackend = persistenceBackend.getFallbackPersistence();
		File newFile = fallbackPersistenceBackend.getFallbackFile();
		
		String oldAccountingFileName = newFile.getName();
		int lastIndexOf_ = oldAccountingFileName.lastIndexOf("_");
		
		oldAccountingFileName = oldAccountingFileName.substring(lastIndexOf_+1);
		oldAccountingFileName = oldAccountingFileName.replace(
				PersistenceBackendFactory.FALLBACK_FILENAME, "accountingFallback.log");
		
		
		File oldAccountingFile = new File(newFile.getParentFile(), oldAccountingFileName);
		elaborateFallbackFile(oldAccountingFile);
		
	}
	
	protected synchronized void elaborateFallbackFile(File file){
		logger.trace("Trying to persist {}s which failed and were persisted using fallback on file {}", 
				Record.class.getSimpleName(), file.getAbsoluteFile());
		File elaborationFile = null;

		if(file.exists()){
			Long timestamp = Calendar.getInstance().getTimeInMillis();
			elaborationFile = new File(file.getAbsolutePath() + ELABORATION_FILE_SUFFIX + "." + timestamp.toString());
			logger.trace("Going to move fallaback file ({}) to elaboration file ({})", 
					file.getAbsolutePath(), elaborationFile.getAbsolutePath());
			file.renameTo(elaborationFile);
			
		}
		
		if(elaborationFile!=null){
			elaborateFile(elaborationFile);
			boolean deleted = elaborationFile.delete();
			if(!deleted){
				logger.trace("Failed to delete file {}", elaborationFile.getAbsolutePath());
				File elaborationFileNotDeleted = new File(elaborationFile.getAbsolutePath()+ELABORATION_FILE_NOT_DELETED_SUFFIX);
				elaborationFile.renameTo(elaborationFileNotDeleted);
			}
		}
			
	}
	
	@Override
	public void run() {
		//logger.trace("TestingThread PersistenceBackendMonitor run");
		FallbackPersistenceBackend fallbackPersistenceBackend = persistenceBackend.getFallbackPersistence();
		File file = new File(fallbackPersistenceBackend.getFallbackFile().getAbsolutePath());
		elaborateFallbackFile(file);
		manageOldAccountingFile();
	}
	
}
