package gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink;

import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.IOHandler;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to used to retrieve content from a result set and store
 * output to various data sinks
 * 
 * @author john.gerbesiotis - DI NKUA
 */
public class DataSinkOp {
	/**
	 * The logger used by the class
	 */
	private Logger log = LoggerFactory.getLogger(DataSinkOp.class.getName());

	/**
	 * The default timeout used by the {@link IRecordReader}. Currently set to
	 * 60.
	 */
	private static final long TimeoutDef = 60;
	/**
	 * The default timeout unit used by the {@link IRecordReader}. The current
	 * default unit is seconds.
	 */
	private static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;

	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;

	/**
	 * The locator of the input
	 */
	protected URI inLocator = null;

	/**
	 * Predefined type of the output data sink
	 */
	private String outputType;

	/**
	 * Value for the output for the specific outputType
	 */
	private String outputValue;

	/**
	 * Parameters for the specific output
	 */
	private Map<String, String> outputParameters;

	/**
	 * Statistics
	 */
	private StatsContainer stats = null;

	/**
	 * Creates a new {@link DataSinkOp} with the default timeout for the writer
	 * 
	 * @param uri
	 *            The input locator of the result set that will be consumed
	 * @param outputType
	 *            The predefined type of the output data sink
	 * @param outputValue
	 *            The value for the output of the specific outputType
	 * @param outputParameters
	 *            The parameters for the specific output
	 * @param stats
	 *            Statistics
	 */
	public DataSinkOp(URI uri, String outputType, String outputValue, Map<String, String> outputParameters, StatsContainer stats) {
		this.inLocator = uri;
		this.outputType = outputType;
		this.outputValue = outputValue;
		this.outputParameters = outputParameters;
		this.stats = stats;
	}
	
	public DataSinkOp(URI uri, String outputType, String outputValue, HashMap<String, String> outputParameters, StatsContainer stats) {
		this(uri, outputType, outputValue, (Map<String, String>)outputParameters, stats);
	}

	
	/**
	 * Creates a new {@link DataSinkOp} with configurable timeout for the writer
	 * 
	 * @param uri
	 *            The input locator of the result set that will be consumed
	 * @param outputType
	 *            The predefined type of the output data sink
	 * @param outputValue
	 *            The value for the output of the specific outputType
	 * @param outputParameters
	 *            The parameters for the specific output
	 * @param timeout
	 *            The timeout to be used for the writer
	 * @param timeUnit
	 *            The time unit of the timeout used
	 * @param stats
	 *            Statistics
	 */
	public DataSinkOp(URI uri, String outputType, String outputValue, Map<String, String> outputParameters, long timeout, TimeUnit timeUnit,
			StatsContainer stats) {
		this(uri, outputType, outputValue, outputParameters, stats);

		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	public DataSinkOp(URI uri, String outputType, String outputValue, HashMap<String, String> outputParameters, long timeout, TimeUnit timeUnit,
			StatsContainer stats) {
			this(uri, outputType, outputValue, (Map<String, String>)outputParameters, timeout, timeUnit, stats);
	}

	public String compute() throws Exception {
		try {
			long start = Calendar.getInstance().getTimeInMillis();

			IOHandler.init(null);

			DataSink dataSink = IOHandler.getDataSink(inLocator, outputType, outputValue, outputParameters, stats);
			dataSink.setTimeout(timeout);
			dataSink.setTimeUnit(timeUnit);
			dataSink.start();

			stats.timeToInitialize(Calendar.getInstance().getTimeInMillis() - start);
			return dataSink.getOutput();
		} catch (Exception e) {
			log.error("Could not initialize datasink operation. Throwing Exception", e);
			throw new Exception("Could not initialize datasink operation", e);
		}
	}
}
