package gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource;

import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The DataSource abstract class is extended by all classes that will be used as
 * a data source.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public abstract class DataSource extends Thread {

	/**
	 * Input value of the {@link DataSource}
	 */
	protected String input;

	/**
	 * Input parameters of the {@link DataSource}
	 */
	protected Map<String, String> inputParameters;

	/**
	 * The writer to use
	 */
	protected IRecordWriter<Record> writer;

	/**
	 * The field definition of the writer
	 */
	protected FieldDefinition[] fieldDefs;

	
	/**
	 * The filter mask that will be applied. If null all available fields will be used.
	 */
	protected String filterMask;

	/**
	 * The default timeout used by the {@link IRecordWriter}. Currently set to
	 * 60.
	 */
	protected static final long TimeoutDef = 60;
	/**
	 * The default timeout unit used by the {@link RecordWriter}. The current
	 * default unit is seconds.
	 */
	protected static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;

	protected long timeout = TimeoutDef;
	protected TimeUnit timeUnit = TimeUnitDef;

	/**
	 * Constructor of the abstract class. Used for initialization.
	 * 
	 * @param input
	 *            input value of the {@link DataSource}
	 * @param inputParameters
	 *            input parameters of the {@link DataSource}
	 * @throws Exception
	 *             If the initialization of the {@link DataSource} fails
	 */
	public DataSource(String input, Map<String, String> inputParameters) throws Exception {
		this.input = input;
		this.inputParameters = inputParameters;
	}

	/**
	 * Abstract method used to retrieve URI locator of the gRS2
	 * 
	 * @return A URI locator of the gRS2
	 */
	public abstract URI getLocator();

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
