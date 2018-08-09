package org.gcube.accounting.aggregator.recover;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.accounting.aggregator.aggregation.AggregatorBuffer;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.document.json.JsonObject;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class RecoverOriginalRecords {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected static final String ID = Record.ID;
	
	static {
		/// One Record per package is enough
		RecordUtility.addRecordPackage(ServiceUsageRecord.class.getPackage());
		RecordUtility.addRecordPackage(AggregatedServiceUsageRecord.class.getPackage());
	}
	
	protected final File srcFile;
	protected final File duplicatedFile;
	protected final File cleanDstFile;
	protected final File aggregatedFile;
	
	protected final Map<String, Record> uniqueRecords;
	protected final AggregatorBuffer aggregatorBuffer;
	
	protected int elaborated;
	protected int duplicated;
	protected int aggregated;
	protected int unique;
	
	protected RecoverOriginalRecords(File srcFile){
		this.srcFile = srcFile;
		this.duplicatedFile = new File(srcFile.getParentFile(), srcFile.getName().replaceAll(".bad", ".duplicated"));
		this.cleanDstFile = new File(srcFile.getParentFile(), srcFile.getName().replaceAll(".bad", ""));
		this.aggregatedFile = new File(srcFile.getParentFile(), cleanDstFile.getName().replaceAll("original", "aggregated"));
		this.uniqueRecords= new HashMap<>();
		this.aggregatorBuffer = new AggregatorBuffer();
		this.elaborated = 0;
		this.duplicated = 0;
		this.aggregated = 0;
		this.unique = 0;
	}
	
	protected void readFile() throws Exception {
		try {
			// Open the file that is the first // command line parameter
			FileInputStream fstream = new FileInputStream(srcFile);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String line;
			// Read File Line By Line
			while ((line = br.readLine()) != null) {
				elaborateLine(line);
				++elaborated;
			}
			
			br.close();
			in.close();
			fstream.close();
			
		} catch (Exception e) {
			logger.error("Error while elaborating file {}", srcFile.getAbsolutePath(), e);
			throw e;
		}

	}

	public void elaborate() throws Exception{
		logger.info("Going to elaborate {}", srcFile.getAbsolutePath());
		readFile();
		afterElaboration();
	}
	
	protected void elaborateLine(String line) throws Exception {
		JsonObject jsonObject = JsonObject.fromJson(line);
		String id = jsonObject.getString(ID);
		if(uniqueRecords.containsKey(id)){
			logger.trace("Duplicated Original Record with ID {}", id);
			Utility.printLine(duplicatedFile, line);
			duplicated++;
		}else{
			Record record = RecordUtility.getRecord(line);
			uniqueRecords.put(id, record);
			Utility.printLine(cleanDstFile, line);
			@SuppressWarnings("rawtypes")
			AggregatedRecord aggregatedRecord = AggregatorBuffer.getAggregatedRecord(record);
			aggregatorBuffer.aggregate(aggregatedRecord);
			unique++;
		}
				
	}
	
	/**
	 * Perform actions at the end of line by line elaboration
	 * @throws Exception 
	 */
	protected void afterElaboration() throws Exception {
		List<AggregatedRecord<?, ?>> aggregatedRecords = aggregatorBuffer.getAggregatedRecords();
		for (AggregatedRecord<?, ?> aggregatedRecord : aggregatedRecords) {
			String marshalled = DSMapper.marshal(aggregatedRecord);
			JsonObject jsonObject = JsonObject.fromJson(marshalled);
			Utility.printLine(aggregatedFile, jsonObject.toString());
			aggregated++;
		}
	}
	
}
