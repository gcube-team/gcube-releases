package gr.uoa.di.madgik.searchlibrary.operatorlibrary.partition;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.Unary;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to perform partitioning on its input
 * {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet} based on a
 * specified field and produce a new
 * {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet} output
 * holding the locators of the produced
 * {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}s, one for
 * each different value of that field.
 * 
 * @author john.gerbesiotis - DI NKUA
 */
public class PartitionOp extends Unary {
	/**
	 * The Logger used by the class
	 */
	private Logger logger = LoggerFactory.getLogger(PartitionOp.class.getName());

	/**
	 * The field that will be partitioned
	 */
	private String clusterBy = null;

	/**
	 * The name field of the output
	 */
	private final static String outputFieldName = "locator";

	private boolean cluterByColumn = true;
	
	/**
	 * Creates a new {@link PartitionOp} with the default timeout for the reader
	 * and the writer
	 * 
	 * @param inLocator
	 *            The locator of the input
	 * @param operatorParameters
	 *            The field that the partition will be applied upon ("clusterBy")
	 * @param stats
	 *            Statistics
	 * @throws Exception 
	 */
	public PartitionOp(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats) throws Exception {
		super(inLocator, operatorParameters, stats);
		
		init();
	}

	/**
	 * Creates a new {@link PartitionOp} with configurable timeout for the
	 * reader and the writer
	 * 
	 * @param inLocator
	 *            The locator of the input
	 * @param operatorParameters
	 *            The field that the partition will be applied upon ("clusterBy")
	 * @param stats
	 *            Statistics
	 * @param timeout
	 *            The timeout to be used both by the reader and the writer
	 * @param timeUnit
	 *            The time unit of the timeout used
	 * @throws Exception 
	 */
	public PartitionOp(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats, long timeout, TimeUnit timeUnit) throws Exception {
		super(inLocator, operatorParameters, stats, timeout, timeUnit);
		
		init();
	}
	
	private void init() throws Exception {
		this.clusterBy = operatorParameters.get("clusterBy");
		
		if (operatorParameters.containsKey("partitionBy")) {
			this.clusterBy = operatorParameters.get("partitionBy");
			this.cluterByColumn = false;
		}
		
		if (clusterBy == null)
			throw new Exception("partition fields unspecified");

	}

	/**
	 * Performs the partitioning operation
	 * 
	 * @return The locator of the produced output
	 * @throws Exception
	 *             An unrecoverable error for the operation occurred
	 */
	public URI compute() throws Exception {
		try {
			int fieldIndex;
			long start = Calendar.getInstance().getTimeInMillis();
			IRecordReader<Record> reader = new ForwardReader<Record>(inLocator);
			if (clusterBy.matches("\\[.*\\]"))
				clusterBy = clusterBy.substring(1, clusterBy.length() - 1);
			if (clusterBy.matches("\\d*"))
				fieldIndex = Integer.parseInt(clusterBy);
			else
				fieldIndex = reader.getRecordDefinitions()[0].getDefinition(clusterBy);

			final RecordDefinition[] defs = new RecordDefinition[]{new GenericRecordDefinition(new FieldDefinition[]{new StringFieldDefinition(outputFieldName)})};
	    	IRecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), defs);

			PartitionWorker<Record> worker = new PartitionWorker<Record>(reader, writer, cluterByColumn, fieldIndex, stats, timeout, timeUnit);
			worker.start();
			stats.timeToInitialize(Calendar.getInstance().getTimeInMillis() - start);
			return writer.getLocator();
		} catch (Exception e) {
			logger.error("Could not initialize selection operation. Throwing Exception", e);
			throw new Exception("Could not initialize selection operation");
		}
	}

}
