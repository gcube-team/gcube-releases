package org.gcube.accounting.aggregator.persist;

import java.io.File;
import java.util.Calendar;

import org.gcube.accounting.aggregator.directory.WorkSpaceDirectoryStructure;
import org.gcube.accounting.aggregator.elaboration.Elaborator;
import org.gcube.accounting.aggregator.status.AggregationState;
import org.gcube.accounting.aggregator.status.AggregationStatus;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.accounting.aggregator.workspace.WorkSpaceManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Persist {

	private static Logger logger = LoggerFactory.getLogger(Persist.class);

	protected final AggregationStatus aggregationStatus;
	
	protected final Bucket originalRecordBucket;
	protected final Bucket aggregatedRecordBucket;
	
	protected final File originalRecordsbackupFile; 
	protected final File aggregateRecordsBackupFile;
	
	public Persist(AggregationStatus aggregationStatus, 
			Bucket originalRecordBucket, Bucket aggregatedRecordBucket, 
			File originalRecordsbackupFile, File aggregateRecordsBackupFile) {
		
		super();
		this.aggregationStatus = aggregationStatus;
		
		this.originalRecordBucket = originalRecordBucket;
		this.aggregatedRecordBucket = aggregatedRecordBucket;
		
		this.originalRecordsbackupFile = originalRecordsbackupFile;
		this.aggregateRecordsBackupFile = aggregateRecordsBackupFile;
	} 
	
	private void setAggregationStateToCompleted(Calendar now) throws Exception {
		originalRecordsbackupFile.delete();
		aggregateRecordsBackupFile.delete();
		File malformedRecords = Utility.getMalformatedFile(aggregateRecordsBackupFile);
		if(malformedRecords.exists()){
			malformedRecords.delete();
		}
		aggregationStatus.setAggregationState(AggregationState.COMPLETED, now, true);
	}
	
	public void recover() throws Exception{
		
		if(aggregationStatus.getAggregatedRecordsNumber()==aggregationStatus.getOriginalRecordsNumber()){
			if(originalRecordBucket.name().compareTo(aggregatedRecordBucket.name())==0 || aggregationStatus.getAggregatedRecordsNumber()==0){
				Calendar now = Utility.getUTCCalendarInstance();
				logger.info("{} - OriginalRecords are {}. AggregatedRecords are {} ({}=={}). All records were already aggregated. The aggregation didn't had any effects and the Source and Destination Bucket are the same ({}) or the record number is 0. Setting {} to {}", 
						aggregationStatus.getAggregationInfo(),
						aggregationStatus.getOriginalRecordsNumber(), 
						aggregationStatus.getAggregatedRecordsNumber(),
						aggregationStatus.getOriginalRecordsNumber(), 
						aggregationStatus.getAggregatedRecordsNumber(),
						originalRecordBucket.name(),
						AggregationState.class.getSimpleName(), AggregationState.COMPLETED);
				setAggregationStateToCompleted(now);
				return;
			}
		}
		
		
		if(AggregationState.canContinue(aggregationStatus.getAggregationState(),AggregationState.AGGREGATED)){
			// For Each original row stored on file it remove them from Bucket.   
			// At the end of elaboration set AgrgegationStatus to DELETED
			// Then save the file in Workspace and set AgrgegationStatus to COMPLETED
			DeleteDocument deleteDocument = new DeleteDocument(aggregationStatus, originalRecordsbackupFile, originalRecordBucket);
			deleteDocument.elaborate();
		}
		
		if(AggregationState.canContinue(aggregationStatus.getAggregationState(),AggregationState.DELETED)){
			// For Each aggregated row stored on file it add them to Bucket. At the end of elaboration set AggregationStatus to ADDED
			InsertDocument insertDocument = new InsertDocument(aggregationStatus, aggregateRecordsBackupFile, aggregatedRecordBucket);
			insertDocument.elaborate();
		}
			
		if(AggregationState.canContinue(aggregationStatus.getAggregationState(),AggregationState.ADDED)){
			Calendar now = Utility.getUTCCalendarInstance();
			WorkSpaceDirectoryStructure workspaceDirectoryStructure = new WorkSpaceDirectoryStructure();
			String targetFolder = workspaceDirectoryStructure.getTargetFolder(aggregationStatus.getAggregationInfo().getAggregationType(), aggregationStatus.getAggregationInfo().getAggregationStartDate());
			
			File malformedRecords = Utility.getMalformatedFile(aggregateRecordsBackupFile);
			if(malformedRecords.exists()){
				WorkSpaceManagement.zipAndBackupFiles(targetFolder, 
					originalRecordsbackupFile.getName().replace(Elaborator.ORIGINAL_SUFFIX, "-with-malformed"), originalRecordsbackupFile, aggregateRecordsBackupFile, malformedRecords);
			}else{
				WorkSpaceManagement.zipAndBackupFiles(targetFolder, 
					originalRecordsbackupFile.getName().replace(Elaborator.ORIGINAL_SUFFIX, ""), originalRecordsbackupFile, aggregateRecordsBackupFile);
			}
			
			setAggregationStateToCompleted(now);
		}
		
	}
	
}
