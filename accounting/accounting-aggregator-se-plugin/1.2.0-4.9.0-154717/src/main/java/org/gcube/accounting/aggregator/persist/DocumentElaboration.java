package org.gcube.accounting.aggregator.persist;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

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
	
	protected final AggregationStatus aggregationStatus;
	protected final File file;
	protected final Bucket bucket;
	protected final AggregationState finalAggregationState;
	
	protected final int rowToBeElaborated;
	
	protected Calendar startTime;
	
	protected DocumentElaboration(AggregationStatus statusManager, AggregationState finalAggregationState, File file, Bucket bucket, int rowToBeElaborated){
		this.aggregationStatus = statusManager;
		this.finalAggregationState = finalAggregationState;
		this.file = file;
		this.bucket = bucket;
		this.rowToBeElaborated = rowToBeElaborated;
	}
	
	protected void readFile() throws Exception {
		try {
			// Open the file that is the first // command line parameter
			FileInputStream fstream = new FileInputStream(file);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			logger.info("{} - Going to elaborate {} rows", aggregationStatus.getAggregationInfo(), rowToBeElaborated);
			
			int tenPercentOfNumberOfRows = (rowToBeElaborated/10)+1;
			
			int elaborated = 0;
			String line;
			// Read File Line By Line
			while ((line = br.readLine()) != null) {
				elaborateLine(line);
				++elaborated;
				if(elaborated % tenPercentOfNumberOfRows == 0){
					int elaboratedPercentage = elaborated*100/rowToBeElaborated;
					logger.info("{} - Elaborated {} rows of {} (about {}%)", aggregationStatus.getAggregationInfo(), elaborated, rowToBeElaborated, elaboratedPercentage);
				}
				if(elaborated>rowToBeElaborated){
					br.close();
					in.close();
					fstream.close();
					throw new Exception("Elaborated file line is number " + elaborated + " > " + rowToBeElaborated + " (total number of rows to elaborate). This is really strange and should not occur. Stopping execution");
				}
			}
			if(elaborated!=rowToBeElaborated){
				br.close();
				in.close();
				fstream.close();
				throw new Exception("Elaborated file line is number " + elaborated + " != " + rowToBeElaborated + "(total number of rows to elaborate). This is really strange and should not occur. Stopping execution");
			}
			
			logger.info("{} - Elaborated {} rows of {} ({}%)", aggregationStatus.getAggregationInfo(), elaborated, rowToBeElaborated, 100);
			
			br.close();
			in.close();
			fstream.close();
			
		} catch (Exception e) {
			logger.error("Error while elaborating file {}", file.getAbsolutePath(), e);
			throw e;
		}

	}

	public void elaborate() throws Exception{
		startTime = Utility.getUTCCalendarInstance();
		readFile();
		aggregationStatus.setState(finalAggregationState, startTime, true);
		afterElaboration();
	}
	
	protected abstract void elaborateLine(String line) throws Exception;
	
	/**
	 * Perform actions at the end of line by line elaboration
	 * @throws Exception 
	 */
	protected abstract void afterElaboration() throws Exception;
	
}
