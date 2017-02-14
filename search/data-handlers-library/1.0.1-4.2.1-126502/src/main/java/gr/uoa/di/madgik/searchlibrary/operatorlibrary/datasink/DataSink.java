package gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink;

import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The DataSink abstract class is extended by all classes that will be used as a
 * data sink.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public abstract class DataSink extends Thread {

	/**
	 * Output value of the {@link DataSink}
	 */
	protected String output;

	/**
	 * Output parameters of the {@link DataSink}
	 */
	protected Map<String, String> outputParameters;

	/**
	 * The locator of the input
	 */
	protected URI inLocator = null;

	/**
	 * The field definition of the reader
	 */
	protected RecordDefinition[] fieldDefs;

	/**
	 * The default timeout used by the {@link IRecordReader}. Currently set to
	 * 60.
	 */
	private static final long TimeoutDef = 600;
	/**
	 * The default timeout unit used by the {@link IRecordReader}. The current
	 * default unit is seconds.
	 */
	private static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;

	protected long timeout = TimeoutDef;
	protected TimeUnit timeUnit = TimeUnitDef;

	/**
	 * Statistics
	 */
	protected StatsContainer stats = null;

	/**
	 * Constructor of the abstract class. Used for initialization.
	 * 
	 * @param inLocator
	 *            input locator of the consuming result set
	 * @param output
	 *            output value of the {@link DataSink}
	 * @param outputParameters
	 *            output parameters of the {@link DataSink}
	 * @param stats
	 *            statistics container
	 * @throws Exception
	 *             If the initialization of the {@link DataSink} fails
	 */
	public DataSink(URI inLocator, String output, Map<String, String> outputParameters, StatsContainer stats) throws Exception {
		this.inLocator = inLocator;
		this.output = output;
		this.outputParameters = outputParameters;
		this.stats = stats;
	}

	/**
	 * Abstract method used to retrieve output
	 * 
	 * @return An output description
	 */
	public abstract String getOutput();

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @param timeUnit
	 *            the timeUnit to set
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
}
