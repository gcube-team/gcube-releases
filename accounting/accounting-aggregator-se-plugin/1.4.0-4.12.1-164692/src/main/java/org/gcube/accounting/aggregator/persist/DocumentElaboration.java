package org.gcube.accounting.aggregator.persist;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.aggregator.status.AggregationState;
import org.gcube.accounting.aggregator.status.AggregationStatus;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.documentstore.records.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class DocumentElaboration {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected static final String ID = Record.ID;
	
	protected static final int THRESHOLD_FOR_FIVE_PERCENT = 100000;
	protected static final int THRESHOLD_FOR_ONE_PERCENT = 1000000;
	
	public static final int MAX_RETRY = 7;
	
	protected final AggregationStatus aggregationStatus;
	protected final File file;
	protected final Bucket bucket;
	protected final AggregationState finalAggregationState;
	
	protected final int rowToBeElaborated;
	
	protected Calendar startTime;
	
	protected DocumentElaboration(AggregationStatus statusManager, AggregationState finalAggregationState, File file,
			Bucket bucket, int rowToBeElaborated) {
		this.aggregationStatus = statusManager;
		this.finalAggregationState = finalAggregationState;
		this.file = file;
		this.bucket = bucket;
		this.rowToBeElaborated = rowToBeElaborated;
	}
	
	protected void readFile() throws Exception {
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		try {
			// Open the file that is the first // command line parameter
			fstream = new FileInputStream(file);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			
			logger.info("{} - Going to elaborate {} rows", aggregationStatus.getAggregationInfo(), rowToBeElaborated);
			
			int percentOfNumberOfRows = (rowToBeElaborated / 10) + 1;
			if(rowToBeElaborated >= THRESHOLD_FOR_FIVE_PERCENT) {
				percentOfNumberOfRows = percentOfNumberOfRows / 2;
			}
			
			if(rowToBeElaborated >= THRESHOLD_FOR_ONE_PERCENT) {
				percentOfNumberOfRows = percentOfNumberOfRows / 5;
			}
			
			int elaborated = 0;
			String line;
			// Read File Line By Line
			while((line = br.readLine()) != null) {
				boolean elaborate = true;
				long delay = TimeUnit.MILLISECONDS.toMillis(100);
				int i = 0;
				while(elaborate) {
					++i;
					try {
						elaborateLine(line);
						elaborate = false;
					} catch(Exception e) {
						if(i != 1) {
							logger.debug("Elaboration of line {} failed due to {}. Retrying {}{} time in {} {}", line,
									e.getMessage(), i, i == 2 ? "nd" : i == 3 ? "rd" : "th", delay,
									TimeUnit.MILLISECONDS.name().toLowerCase());
						} else {
							logger.warn("Elaboration of line {} failed due to {}. Retrying in {} {}", line,
									e.getMessage(), delay, TimeUnit.MILLISECONDS.name().toLowerCase());
						}
						if(i < MAX_RETRY) {
							TimeUnit.MILLISECONDS.sleep(delay);
							delay = delay * 2;
						} else {
							// elaborate = false; // This is not needed but it is added to improve code readability
							throw e;
						}
					}
				}
				
				++elaborated;
				if(elaborated % percentOfNumberOfRows == 0) {
					int elaboratedPercentage = elaborated * 100 / rowToBeElaborated;
					logger.info("{} - Elaborated {} rows of {} (about {}%)", aggregationStatus.getAggregationInfo(),
							elaborated, rowToBeElaborated, elaboratedPercentage);
				}
				if(elaborated > rowToBeElaborated) {
					throw new Exception("Elaborated file line is number " + elaborated + " > " + rowToBeElaborated
							+ " (total number of rows to elaborate). This is really strange and should not occur. Stopping execution");
				}
			}
			if(elaborated != rowToBeElaborated) {
				throw new Exception("Elaborated file line is number " + elaborated + " != " + rowToBeElaborated
						+ "(total number of rows to elaborate). This is really strange and should not occur. Stopping execution");
			}
			
			logger.info("{} - Elaborated {} rows of {} ({}%)", aggregationStatus.getAggregationInfo(), elaborated,
					rowToBeElaborated, 100);
			
		} catch(Exception e) {
			logger.error("Error while elaborating file {}", file.getAbsolutePath(), e);
			throw e;
		} finally {
			if(br != null) {
				br.close();
			}
			if(in != null) {
				in.close();
			}
			if(fstream != null) {
				fstream.close();
			}
			
		}
		
	}
	
	public void elaborate() throws Exception {
		startTime = Utility.getUTCCalendarInstance();
		readFile();
		aggregationStatus.setAggregationState(finalAggregationState, startTime, true);
		afterElaboration();
	}
	
	protected abstract void elaborateLine(String line) throws Exception;
	
	/**
	 * Perform actions at the end of line by line elaboration
	 * @throws Exception 
	 */
	protected abstract void afterElaboration() throws Exception;
	
}
