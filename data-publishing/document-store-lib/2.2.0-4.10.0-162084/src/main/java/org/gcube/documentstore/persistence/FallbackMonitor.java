/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	
	protected void elaborateFile(File elaborationFile) {
		try(BufferedReader br = new BufferedReader(new FileReader(elaborationFile))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	try {
		    		Record record = RecordUtility.getRecord(line);		    		
		    		persistenceBackend.accountWithFallback(record);
		    	} catch(Throwable e){
		    		logger.error("Was not possible parse line {} to obtain a valid Record. Going to writing back this line as string fallback file.", line, e);
		    		FallbackPersistenceBackend fallbackPersistenceBackend = persistenceBackend.getFallbackPersistence();
		    		try {
						fallbackPersistenceBackend.printLine(line);
					} catch (Throwable e1) {
						logger.error("Line {} will be lost", line, e1);
					}
		    	}
		    }
		} catch (Throwable e) {
			logger.error("Error elaborating {}", elaborationFile.getAbsoluteFile(), e);
		}
	}
	
	@Deprecated
	protected void manageOldAccountingFile(){
		FallbackPersistenceBackend fallbackPersistenceBackend = persistenceBackend.getFallbackPersistence();
		elaborateFallbackFile(fallbackPersistenceBackend.getOldFallbackFile());
		
	}
	
	protected void elaborateFallbackFile(File elaborationFile){
		if(elaborationFile!=null && elaborationFile.exists()){
			try {
				elaborateFile(elaborationFile);
			}finally {
				if(elaborationFile.exists()) {
					boolean deleted = elaborationFile.delete();
					if(!deleted){
						logger.error("Failed to delete file {}", elaborationFile.getAbsolutePath());
						File elaborationFileNotDeleted = new File(elaborationFile.getAbsolutePath()+ELABORATION_FILE_NOT_DELETED_SUFFIX);
						elaborationFile.renameTo(elaborationFileNotDeleted);
					}
				}else {
					logger.error("File {} does not exists. This is really starge and should not occur. Please contact the administrator.", elaborationFile.getAbsolutePath());
				}
				
			}
		}
	}
	
	protected void elaborateFallbackFile(){
		FallbackPersistenceBackend fallbackPersistenceBackend = persistenceBackend.getFallbackPersistence();
		File file = fallbackPersistenceBackend.getFallbackFile();
		
		logger.trace("Trying to persist {}s which were persisted using fallback on file {}", 
				Record.class.getSimpleName(), file.getAbsoluteFile());

		Long timestamp = Calendar.getInstance().getTimeInMillis();
		File elaborationFile = fallbackPersistenceBackend.moveFallbackFile(ELABORATION_FILE_SUFFIX + "." + timestamp.toString());
		elaborateFallbackFile(elaborationFile);
	}
	
	@Override
	public void run() {
		elaborateFallbackFile();
		manageOldAccountingFile();
	}
	
}
