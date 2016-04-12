package gr.uoa.di.madgik.searchlibrary.operatorlibrary.select;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.Unary;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to perform selection operation on its input
 * {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet} and produce
 * a new {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
 * output holding the passing records
 * 
 * @author john.gerbesiotis - DI NKUA
 */
public class SelectOp extends Unary{
	/**
	 * The Logger used by the class
	 */
	private Logger logger = LoggerFactory.getLogger(SelectOp.class.getName());

	/**
	 * The logical expression
	 */
	private String logicalExpressions = null;

	/**
	 * A filterMask to be applied for column filter/rearrange
	 */
	private String filterMask;

	
	/**
	 * @see Unary
	 */
	public SelectOp(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats) throws Exception {
		super(inLocator, operatorParameters, stats);
		
		this.logicalExpressions = operatorParameters.get("logicalExpressions");
		this.filterMask = operatorParameters.get("filterMask");
		
		if (logicalExpressions == null && filterMask == null) {
			throw new Exception("Initialization failed because both logicalExpression and filterMask parameters are not set");
		}
		
		logger.info("Initialized Select operator with logical Expression: " + logicalExpressions + " and filter mask: " + filterMask);
	}

	/**
	 * Creates a new {@link SelectOp} with configurable timeout for the reader
	 * and the writer
	 * 
	 * @param inLocator
	 *            The locator of the input
	 * @param logicalExpressions
	 *            The logical expression that selection will be based upon
	 * @param filterMask
	 *            The filter mask to be applied
	 * @param stats
	 *            Statistics
	 * @param timeout
	 *            The timeout to be used both by the reader and the writer
	 * @param timeUnit
	 *            The time unit of the timeout used
	 */
//	public SelectOp(URI inLocator, String logicalExpressions, String filterMask, StatsContainer stats, long timeout, TimeUnit timeUnit) {
//		this(inLocator, logicalExpressions, filterMask, stats);
//		this.timeout = timeout;
//		this.timeUnit = timeUnit;
//	}

	/**
	 * Performs the selection operation
	 * 
	 * @return The locator of the produced output
	 * @throws Exception
	 *             An unrecoverable error for the operation occured
	 */
	public URI compute() throws Exception {
		try {
			long start = Calendar.getInstance().getTimeInMillis();
			IRecordReader<Record> reader = new ForwardReader<Record>(inLocator);
			RecordDefinition[] readerDefs = reader.getRecordDefinitions();
			FieldDefinition[] writerFieldDefs = null;

			Integer mask[] = null;
			if (filterMask != null) {
				mask = initialiseMask(filterMask);
				writerFieldDefs = new FieldDefinition[mask.length];
				for (int i = 0; i < mask.length; i++) {
					writerFieldDefs[i] = readerDefs[0].getDefinition(mask[i]);
				}
			}

			IRecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), writerFieldDefs == null ? readerDefs
					: new RecordDefinition[] { new GenericRecordDefinition(writerFieldDefs) }, RecordWriter.DefaultBufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);

			SelectWorker<Record> worker = new SelectWorker<Record>(reader, writer, logicalExpressions, mask, stats, timeout, timeUnit);
			worker.start();
			stats.timeToInitialize(Calendar.getInstance().getTimeInMillis() - start);
			return writer.getLocator();
		} catch (Exception e) {
			logger.error("Could not initialize selection operation. Throwing Exception", e);
			throw new Exception("Could not initialize selection operation", e);
		}
	}

	/**
	 * Takes a string filter mask as an input (e.g. [2, 3, 1]) and return an
	 * array of Integers
	 * 
	 * @param filterMask
	 *            the string filter mask to be applied
	 * @return an array of integers
	 */
	private Integer[] initialiseMask(String filterMask) {
		List<Integer> schemaList = new ArrayList<Integer>();

		for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
			int index = Integer.parseInt(ref);

			schemaList.add(index);
		}

		return schemaList.toArray(new Integer[schemaList.size()]);
	}

}
