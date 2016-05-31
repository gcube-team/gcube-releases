package gr.uoa.di.madgik.searchlibrary.operatorlibrary;

import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink.DataSink;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.DataSource;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Initializes the DataSource and DataSink classes.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
@SuppressWarnings("unchecked")
public class IOHandler {

	/**
	 * Logs operations performed by {@link IOHandler} class
	 */
	private static Logger log = LoggerFactory.getLogger(IOHandler.class.getName());

	/**
	 * Maps the {@link DataSource} type to its class.
	 */
	private static Map<String, Class<? extends DataSource>> inputTypeToDataSourceClass = new HashMap<String, Class<? extends DataSource>>();

	/**
	 * Maps the {@link DataSink} type to its class.
	 */
	private static Map<String, Class<? extends DataSink>> outputTypeToDataSinkClass = new HashMap<String, Class<? extends DataSink>>();

	/**
	 * The parameters which are passes in each {@link DataSource} constructor.
	 */
	private static final Class<?>[] dataSourceConstructorParameterTypes = { String.class, Map.class};

	/**
	 * The parameters which are passes in each {@link DataSink} constructor.
	 */
	private static final Class<?>[] dataSinkConstructorParameterTypes = { URI.class, String.class, Map.class, StatsContainer.class };

	private static boolean initialized = false;
	
	/**
	 * Initializes the {@link IOHandler} by reading the available data handlers
	 * from a configuration file.
	 * 
	 * @param dataHandlersConfigFile
	 *            The configuration file from which the {@link IOHandler} will
	 *            read or null for default config
	 * @throws Exception
	 *             If initialization fails
	 */
	public static void init(String dataHandlersConfigFile) throws Exception {
		if (initialized)
			return;
		initialized = true;
		
		try {
			InputStream is = dataHandlersConfigFile == null ? IOHandler.class.getResourceAsStream("/DataHandlersConfig.xml") : new FileInputStream(
					dataHandlersConfigFile);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);

			// Mapping the input types with the DataSource Handlers...
			NodeList dataSources = doc.getElementsByTagName("DataSource");
			for (int i = 0; i < dataSources.getLength(); i++) {
				Element dataSource = (Element) dataSources.item(i);
				String type = dataSource.getAttribute("type");
				String className = dataSource.getAttribute("class");
				log.debug("Data Source found " + type + ": " + className);
				try {
					Class<? extends DataSource> cls = (Class<? extends DataSource>) Class.forName(className);
					inputTypeToDataSourceClass.put(type, cls);
				} catch (Exception e) {
					log.error("Did not manage to find in classpath class with name " + className, e);
				} catch (java.lang.Error err) {
					log.error("Did not manage to find in classpath class with name " + className, err);
				}
			}

			// Mapping the output types with the DataSink Handlers...
			NodeList dataSinks = doc.getElementsByTagName("DataSink");
			for (int i = 0; i < dataSinks.getLength(); i++) {
				Element dataSink = (Element) dataSinks.item(i);
				String type = dataSink.getAttribute("type");
				String className = dataSink.getAttribute("class");
				log.debug("Data Sink found " + type + ": " + className);
				try {
					Class<? extends DataSink> cls = (Class<? extends DataSink>) Class.forName(className);
					outputTypeToDataSinkClass.put(type, cls);
				} catch (Exception e) {
					log.error("Did not manage to find in classpath class with name " + className, e);
				} catch (java.lang.Error err) {
					log.error("Did not manage to find in classpath class with name " + className, err);
				}
			}

		} catch (Exception e) {
			log.error("Could not parse configuration file for Data Handlers", e);
			throw new Exception("Could not parse configuration file for Data Handlers", e);
		}
	}

	/**
	 * Initializes and returns a {@link DataSource} object.
	 * 
	 * @param inputType
	 *            The input type
	 * @param inputValue
	 *            The input value
	 * @param inputParameters
	 *            The parameters of the input
	 * @return The {@link DataSource} object
	 * @throws Exception
	 *             If an error occurred in initializing the {@link DataSource}
	 */
	public static DataSource getDataSource(String inputType, String inputValue, Map<String, String> inputParameters) throws Exception {

		if (inputTypeToDataSourceClass == null) {
			log.error("IOHandler is not initialized");
			throw new Exception("IOHandler is not initialized");
		}

		if (inputType == null || inputValue == null) {
			log.error("Output is not set properly");
			throw new Exception("Output is not set properly");
		}

		Class<? extends DataSource> dataSourceClass = inputTypeToDataSourceClass.get(inputType);
		if (dataSourceClass == null) {
			log.error("Could not find data source with " + inputType + " IOType.");
			throw new Exception("Could not find data source with IOType " + inputType);
		}

		try {
			Constructor<DataSource> sourceConstructor = (Constructor<DataSource>) dataSourceClass.getConstructor(dataSourceConstructorParameterTypes);
			return sourceConstructor.newInstance(inputValue, inputParameters);
		} catch (Exception e) {
			log.error("Error when instanciating the data source.", e);
			throw new Exception("Error when instanciating the data source", e);
		}
	}

	/**
	 * Initializes and returns a {@link DataSink} object.
	 * 
	 * @param inLocator
	 *            The input locator
	 * @param outputType
	 *            The output type
	 * @param outputValue
	 *            The output value
	 * @param outputParameters
	 *            The parameters of the output
	 * @param stats
	 *            The statistics container
	 * @return The {@link DataSink} object
	 * @throws Exception
	 *             If an error occurred in initializing the {@link DataSink}
	 */
	public static DataSink getDataSink(URI inLocator, String outputType, String outputValue, Map<String, String> outputParameters, StatsContainer stats)
			throws Exception {

		if (outputTypeToDataSinkClass == null) {
			log.error("IOHandler is not initialized");
			throw new Exception("IOHandler is not initialized");
		}

		if (inLocator == null) {
			log.error("input locator is not set");
			throw new Exception("input locator is not set");
		}

		if (outputType == null || outputValue == null) {
			log.error("Output is not set properly");
			return null;
		}

		Class<? extends DataSink> dataSinkClass = outputTypeToDataSinkClass.get(outputType);
		if (dataSinkClass == null) {
			log.error("Could not find data sink with " + outputType + " IOType.");
			throw new Exception("Could not find data sink with IOType " + outputType);
		}

		try {
			Constructor<DataSink> sinkConstructor = (Constructor<DataSink>) dataSinkClass.getConstructor(dataSinkConstructorParameterTypes);
			return sinkConstructor.newInstance(inLocator, outputValue, outputParameters, stats);
		} catch (Exception e) {
			log.error("Error when instanciating the data sink.", e);
			throw new Exception("Error when instanciating the data sink", e);
		}
	}
}
