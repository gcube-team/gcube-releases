package gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource;

import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.IOHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to used to retrieve content from various data sources and
 * produce a new result set output holding the records
 * 
 * @author john.gerbesiotis - DI NKUA
 */
public class DataSourceOp {
	/**
	 * The logger used by the class
	 */
	private Logger log = LoggerFactory.getLogger(DataSourceOp.class.getName());

	/**
	 * The default timeout used by the {@link IRecordWriter} and all
	 * {@link IRecordReader}s. Currently set to 60.
	 */
	private static final long TimeoutDef = 60;
	/**
	 * The default timeout unit used by the {@link RecordWriter} and all
	 * {@link IRecordReader}s. The current default unit is seconds.
	 */
	private static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;

	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;

	/**
	 * Predefined type of the input data source
	 */
	private String inputType;

	/**
	 * Value for the input for the specific inputType
	 */
	private String inputValue;

	/**
	 * Parameters for the specific input
	 */
	private Map<String, String> inputParameters;

	/**
	 * Creates a new {@link DataSourceOp} with the default timeout for the
	 * writer
	 * 
	 * @param inputType
	 *            The predefined type of the input data source
	 * @param inputValue
	 *            The value for the input of the specific inputType
	 * @param inputParameters
	 *            The parameters for the specific input
	 */
	public DataSourceOp(String inputType, String inputValue, Map<String, String> inputParameters) {
		this.inputType = inputType;
		this.inputValue = inputValue;
		this.inputParameters = inputParameters;
	}
	
	public DataSourceOp(String inputType, String inputValue, HashMap<String, String> inputParameters) {
		this(inputType, inputValue, (Map<String, String>)inputParameters);
	}

	/**
	 * Creates a new {@link DataSourceOp} with configurable timeout for the
	 * writer
	 * 
	 * @param inputType
	 *            The predefined type of the input data source
	 * @param inputValue
	 *            The value for the input of the specific inputType
	 * @param inputParameters
	 *            The parameters for the specific input
	 * @param timeout
	 *            The timeout to be used for the writer
	 * @param timeUnit
	 *            The time unit of the timeout used
	 */
	public DataSourceOp(String inputType, String inputValue, Map<String, String> inputParameters, long timeout, TimeUnit timeUnit) {
		this(inputType, inputValue, inputParameters);

		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}
	
	public DataSourceOp(String inputType, String inputValue, HashMap<String, String> inputParameters, long timeout, TimeUnit timeUnit) {
	this(inputType, inputValue, (Map<String, String>)inputParameters, timeout, timeUnit);
	}

	public URI compute() throws Exception {
		try {
			IOHandler.init(null);

			DataSource dataSource = IOHandler.getDataSource(inputType, inputValue, inputParameters);
			dataSource.setTimeout(timeout);
			dataSource.setTimeUnit(timeUnit);
			dataSource.start();

			return dataSource.getLocator();
		} catch (Exception e) {
			log.error("Could not initialize datasource operation. Throwing Exception", e);
			throw new Exception("Could not initialize datasource operation");
		}
	}
}
