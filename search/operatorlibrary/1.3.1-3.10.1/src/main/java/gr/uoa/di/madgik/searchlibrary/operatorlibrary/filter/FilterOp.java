package gr.uoa.di.madgik.searchlibrary.operatorlibrary.filter;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
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
 * Operator class used to perform an xPath based filtering operation on its input {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
 * and produce a new {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet} output holding the passing
 * records
 * 
 * @author UoA
 */
public class FilterOp {
	/**
	 * The Logger used by the class
	 */
	private Logger logger = LoggerFactory.getLogger(FilterOp.class.getName());

	/**
	 * The default timeout used by the {@link IRecordWriter} and all {@link IRecordReader}s. Currently set to 60.
	 */
	private static final long TimeoutDef = 60;
	/**
	 * The default timeout unit used by the {@link RecordWriter} and all {@link IRecordReader}s. The current default unit is seconds.
	 */
	private static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;

	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;
	
	/**
	 * The locator of the input
	 */
	private URI inLocator = null;
	
	/**
	 * The name of the payload field
	 */
	private String payloadFieldName = null;
	
	/**
	 * Statistics
	 */
	private StatsContainer stats=null;
	
	/**
	 * Creates a new {@link FilterOp} with the default timeout for the reader and the writer
	 * 
	 * @param locator The locator of the input
	 * @param payloadFieldName The name of the {@link Field} which contains the payload to apply the filtering operation on
	 * @param stats Statistics
	 */
	public FilterOp(URI inLocator, String payloadFieldName, StatsContainer stats){
		this.inLocator = inLocator;
		this.payloadFieldName = payloadFieldName;
		this.stats=stats;
	}
	
	/**
	 * Creates a new {@link FilterOp} with configurable timeout for the reader and the writer
	 * 
	 * @param locator The locator of the input
	 * @param payloadFieldName The name of the {@link Field} which contains the payload to apply the filtering operation on
	 * @param stats Statistics
	 * @param timeout The timeout to be used both by the reader and the writer
	 * @param timeUnit The time unit of the timeout used
	 */
	public FilterOp(URI inLocator, String payloadFieldName, StatsContainer stats, long timeout, TimeUnit timeUnit){
		this.inLocator = inLocator;
		this.payloadFieldName = payloadFieldName;
		this.stats=stats;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	/**
	 * Performs the filtering operation
	 * 
	 * @param xPath The xpath expression to use
	 * @return The locator of the produced output
	 * @throws Exception An unrecoverable error for the operation occured
	 */
	public URI compute(String xPath) throws Exception{
		try{
			long start=Calendar.getInstance().getTimeInMillis();
			IRecordReader<Record> reader = new ForwardReader<Record>(inLocator);
			IRecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), reader);
			
			FilterWorker<Record> worker = new FilterWorker<Record>(reader, writer, payloadFieldName, xPath, stats, timeout, timeUnit);
			worker.start();
			stats.timeToInitialize(Calendar.getInstance().getTimeInMillis()-start);
			return writer.getLocator();
		}catch(Exception e){
			logger.error("Could not initialize filter operation. Throwing Exception",e);
			throw new Exception("Could not initialize filter operation");
		}
	}
	
}
