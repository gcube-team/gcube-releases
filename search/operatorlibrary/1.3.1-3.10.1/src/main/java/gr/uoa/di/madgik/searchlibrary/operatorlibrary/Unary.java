package gr.uoa.di.madgik.searchlibrary.operatorlibrary;

import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class Unary {
	/**
	 * The locator of the input
	 */
	protected URI inLocator;
	protected Map<String, String> operatorParameters;
	protected StatsContainer stats;
	
	/**
	 * The default timeout used by the {@link IRecordWriter} and all
	 * {@link IRecordReader}s. Currently set to 600.
	 */
	public static final long TimeoutDef = 600;
	/**
	 * The default timeout unit used by the {@link RecordWriter} and all
	 * {@link IRecordReader}s. The current default unit is seconds.
	 */
	public static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;

	protected long timeout = TimeoutDef;
	protected TimeUnit timeUnit = TimeUnitDef;

	/**
	 * Constructor of the abstract class. Used for initialization.
	 * @param inLocator 
	 *            input value of the {@link Unary}
	 * @param operatorParameters
	 *            input parameters of the {@link Unary}
	 * @param stats Statistics
	 * @throws Exception
	 *             If the initialization of the {@link Unary} fails
	 */
	public Unary(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats) throws Exception {
		this(inLocator, operatorParameters, stats, TimeoutDef, TimeUnitDef);
	}
	
	public Unary(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats, long timeout, TimeUnit timeUnit) throws Exception {
		this.inLocator = inLocator;
		this.operatorParameters = operatorParameters;
		this.stats = stats;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	abstract public URI compute() throws Exception;
}
