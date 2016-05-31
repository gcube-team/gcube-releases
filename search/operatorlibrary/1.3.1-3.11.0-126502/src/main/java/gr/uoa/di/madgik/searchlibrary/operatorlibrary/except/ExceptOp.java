package gr.uoa.di.madgik.searchlibrary.operatorlibrary.except;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.reader.decorators.keepalive.KeepAliveReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to perform a set difference on its input and produces an output holding the produced results.
 * The set produced contains all records in the left input whose key is not present in the records of the right input.
 * No duplicate elimination is performed, therefore a caller wishing to obtain standard SQL EXCEPT semantics should
 * perform duplicate elimination as a subsequent step.
 * 
 * @author UoA
 */
public class ExceptOp {
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(ExceptOp.class.getName());

	/**
	 * The locator of the left input
	 */
	private URI leftLocator = null;
	
	/**
	 * The locator of the right input
	 */
	private URI rightLocator = null;

	/**
	 * The default timeout
	 */
	private static long TimeoutDef = 60;
	/**
	 * The default time unit
	 */
	private static TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	/**
	 * The timeout that will be used both by the two {@link IRecordReader}s and the {@link IRecordWriter} involved in the join operation
	 */
	private long timeout = TimeoutDef;
	/**
	 * The time unit of the timeout that will be used
	 */
	private TimeUnit timeUnit = TimeUnitDef;
	
	/**
	 * Container of statistics
	 */
	private StatsContainer stats;
	
	/**
	 * Creates a new {@link ExceptOp} with the default timeout
	 * 
	 * @param leftLocator The locator of the left input
	 * @param rightLocator The locator of the right input
	 * @param stats statistics
	 */
	public ExceptOp(URI leftLocator, URI rightLocator,StatsContainer stats){
		this.leftLocator = leftLocator;
		this.rightLocator = rightLocator;
		this.stats=stats;
	}
	
	/**
	 * Creates a new {@link ExceptOp} with configurable timeout
	 * 
	 * @param leftLocator The locator of the left input
	 * @param rightLocator The locator of the right input
	 * @param timeout The timeout which will be used by the two {@link IRecordReader}s and the {@link RecordWriter}
	 * @param timeUnit The unit of the timeout
	 * @param stats Statistics
	 */
	public ExceptOp(URI leftLocator, URI rightLocator, long timeout, TimeUnit timeUnit, StatsContainer stats){
		this.leftLocator = leftLocator;
		this.rightLocator = rightLocator;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.stats=stats;
	}
	
	/**
	 * Performs the join operation
	 * 
	 * @param type The type of resource to create
	 * @param leftKeyFieldName The name of the {@link Field} of the join key originating from the left locator
	 * @param rightKeyFieldName The name of the {@link Field} of the join key origiating from the right locator
	 * @return The join result
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI compute(/*RSResourceType type,*/String leftKeyFieldName, String rightKeyFieldName) throws Exception{
		try{
			long start=Calendar.getInstance().getTimeInMillis();
			
			IRecordReader<Record> reader1 = new KeepAliveReader<Record>(new ForwardReader<Record>(leftLocator), 1, TimeUnit.MINUTES);
			IRecordReader<Record> reader2 = new ForwardReader<Record>(rightLocator);
			IRecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), reader1, 
					100, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);			
		
			stats.timeToInitialize(Calendar.getInstance().getTimeInMillis()-start);
	
			ExceptWorker worker=new ExceptWorker(writer, reader1, reader2, leftKeyFieldName, rightKeyFieldName, timeout, timeUnit,stats);
			worker.start();
			return writer.getLocator();
		}catch(Exception e){
			logger.error("Could not initialize join operation. Throwing Exception", e);
			throw new Exception("Could not initialize join operation");
		}
	}
}
