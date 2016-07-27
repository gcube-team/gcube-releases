package gr.uoa.di.madgik.searchlibrary.operatorlibrary.keeptop;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Operator class used to perform a keep top operation on an input ResultSet
 * and produce a new output ResultSet with the top
 * records
 * 
 * @author UoA
 */
public class KeepTopOp {
	/**
	 * Logger used by this class
	 */
	private Logger logger = LoggerFactory.getLogger(KeepTopOp.class.getName());

	/**
	 * The locator of the input
	 */
	private URI inLocator = null;
	/**
	 * stats
	 */
	private StatsContainer stats=null;
	
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
	 * Creates a new {@link KeepTopOp} with the default timeout for the reader and the writer
	 * 
	 * @param locator The locator of the input
	 * @param stats Statistics
	 */
	public KeepTopOp(URI locator,StatsContainer stats){
		this.inLocator=locator;
		this.stats=stats;
	}
	
	/**
	 * Creates a new {@link KeepTopOp} with configurable timeout for the reader and the writer
	 * 
	 * @param locator The locator of the input
	 * @param stats Statistics
	 * @param timeout The timeout to be used both by the reader and the writer
	 * @param timeUnit The time unit of the timeout used
	 */
	public KeepTopOp(URI locator, StatsContainer stats, long timeout, TimeUnit timeUnit) {
		this(locator, stats);
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	/**
	 * Performs the Keep Top operation
	 * 
	 * @param count The number of records to keep

	 * @return The locator of the produced output
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI compute(int count/*,String ssid*/) throws Exception{
		try{
			long start=Calendar.getInstance().getTimeInMillis();
			IRecordReader<Record> reader = new ForwardReader<Record>(inLocator);
			IRecordWriter<Record> writer = new RecordWriter<Record>(new LocalWriterProxy(), reader);
			
			KeepTopWorker<Record> worker = new KeepTopWorker<Record>(reader, writer, count, stats, timeout, timeUnit);
			worker.start();
			long readerstop = Calendar.getInstance().getTimeInMillis();
			stats.timeToInitialize(readerstop-start);
	
			return writer.getLocator();
		}catch(Exception e){
			logger.error("Could not start background process of keep top operator. Throwing Exception", e);
			throw new Exception("Could not start background process of keep top operator");
		}
	}
}
