package org.gcube.datatransformation.datatransformationlibrary.datahandlers;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.utils.XMLUtils;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeEvaluator;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Output;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Input;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Dimitris Katris, NKUA
 *
 * Initializes the DataSource and DataSink classes.
 */
@SuppressWarnings("unchecked")
public class IOHandler {

	/**
	 * Logs operations performed by {@link IOHandler} class
	 */
	private static Logger log = LoggerFactory.getLogger(IOHandler.class);
	
	/**
	 * Maps the {@link DataSource} type to its class. 
	 */
	private static Map<String, Class> inputTypeToDataSourceClass = new HashMap<String, Class>();
	/**
	 * Maps the {@link DataSink} type to its class.
	 */
	private static Map<String, Class> outputTypeToDataSinkClass = new HashMap<String, Class>();
	
	/**
	 * Maps the {@link ContentTypeEvaluator} type to its class.
	 */
	private static Map<String, Class> evaluatorTypeToEvaluatorClass = new HashMap<String, Class>();

	/**
	 * The parameters which are passes in each {@link DataHandler} constructor. 
	 */
	private static final Class[] dataHandlerConstructorParameterTypes = {String.class, org.gcube.datatransformation.datatransformationlibrary.model.Parameter[].class};
	
	/**
	 * Initializes the {@link IOHandler} by reading the available data handlers from a configuration file.
	 * 
	 * @param dataHandlersConfigFile The configuration file from which the {@link IOHandler} will read 
	 * @throws Exception
	 */
	public static void init(String dataHandlersConfigFile) throws Exception {
		try {
			Document doc = null;
			if (dataHandlersConfigFile == null) {
				InputStream is = IOHandler.class.getResourceAsStream("/DataHandlersConfig.xml");
				doc = XMLUtils.newDocument(is);
				is.close();
			} else {
				new FileInputStream(dataHandlersConfigFile);
			}
			
			//Mapping the input types with the DataSource Handlers...
			NodeList dataSources = doc.getElementsByTagName("DataSource");
			for(int i=0;i<dataSources.getLength();i++){
				Element dataSource = (Element)dataSources.item(i);
				String type = dataSource.getAttribute("type");
				String className = dataSource.getAttribute("class");
				log.debug("Data Source found "+type+": "+className);
				try {
					Class cls = Class.forName(className);
					inputTypeToDataSourceClass.put(type, cls);
				} catch (Exception e) {
					log.error("Did not manage to find in classpath class with name "+className, e);
				} catch (java.lang.Error err){
					log.error("Did not manage to find in classpath class with name "+className, err);
				}
			}
			
			//Mapping the output types with the DataSink Handlers...
			NodeList dataSinks = doc.getElementsByTagName("DataSink");
			for(int i=0;i<dataSinks.getLength();i++){
				Element dataSink = (Element)dataSinks.item(i);
				String type = dataSink.getAttribute("type");
				String className = dataSink.getAttribute("class");
				log.debug("Data Sink found "+type+": "+className);
				try {
					Class cls = Class.forName(className);
					outputTypeToDataSinkClass.put(type, cls);
				} catch (Exception e) {
					log.error("Did not manage to find in classpath class with name "+className, e);
				} catch (java.lang.Error err){
					log.error("Did not manage to find in classpath class with name "+className, err);
				}
			}
			
			//Mapping the output types with the DataSink Handlers...
			NodeList contentTypeEvaluators = doc.getElementsByTagName("ContentTypeEvaluator");
			for(int i=0;i<contentTypeEvaluators.getLength();i++){
				Element contentTypeEvaluator = (Element)contentTypeEvaluators.item(i);
				String type = contentTypeEvaluator.getAttribute("type");
				String className = contentTypeEvaluator.getAttribute("class");
				log.debug("Data Sink found "+type+": "+className);
				try {
					Class cls = Class.forName(className);
					evaluatorTypeToEvaluatorClass.put(type, cls);
				} catch (Exception e) {
					log.error("Did not manage to find in classpath class with name "+className, e);
				} catch (java.lang.Error err){
					log.error("Did not manage to find in classpath class with name "+className, err);
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
	 * @param input The input type, value and parameters 
	 * @return The {@link DataSource} object.
	 * @throws Exception If an error occurred in initializing the {@link DataSource}.
	 */
	public static DataSource getDataSource(Input input) throws Exception {
		
		if(inputTypeToDataSourceClass==null){log.error("IOHandler is not initialized");throw new Exception("IOHandler is not initialized");}
		
		if(input==null){log.error("Input is not set");throw new Exception("Input is not set");}
		
		String inputType = input.getInputType();
		String inputValue = input.getInputValue();
		Parameter[] inputParameters = input.getInputParameters();
		
		if(inputType==null || inputType==null){	log.error("Input type is not set");throw new Exception("Input type is not set");}
		
		if(inputValue==null){log.error("Input value is not set");throw new Exception("Input value is not set");}
		
		Class dataSourceClass = inputTypeToDataSourceClass.get(inputType);
		if(dataSourceClass==null){log.error("Could not find data source with "+inputType+" IOType.");throw new Exception("Could not find data source with IOType "+inputType);}
		
		try {
			Constructor<DataSource> sourceConstructor = (Constructor<DataSource>)dataSourceClass.getConstructor(dataHandlerConstructorParameterTypes);
			return sourceConstructor.newInstance(inputValue, inputParameters);
		} catch (Exception e) {
			log.error("Error when instantiating the data source.", e);
			throw new Exception("Error when instantiating the data source", e);
		}
	}
	
	/**
	 * Initializes and returns a {@link ContentTypeEvaluator} object.
	 * 
	 * @param evaluatorType The evaluator type.
	 * @return The {@link ContentTypeEvaluator} object.
	 * @throws Exception If an error occurred in initializing the {@link ContentTypeEvaluator}.
	 */
	public static ContentTypeEvaluator getContentTypeEvaluator(String evaluatorType) throws Exception {
		
		if(evaluatorTypeToEvaluatorClass==null){log.error("IOHandler is not initialized");throw new Exception("IOHandler is not initialized");}
		
		if(evaluatorType==null || evaluatorType.trim().length()==0){log.error("Evaluator type is not set");throw new Exception("Evaluator type is not set");}
		
		Class evaluatorClass = evaluatorTypeToEvaluatorClass.get(evaluatorType);
		if(evaluatorClass==null){log.error("Could not find evaluator with type: "+evaluatorType);throw new Exception("Could not find evaluator with type: "+evaluatorType);}
		
		try {
			return (ContentTypeEvaluator)evaluatorClass.newInstance();
		} catch (Exception e) {
			log.error("Error when instanciating the Content Type Evaluator", e);
			throw new Exception("Error when instanciating the Content Type Evaluator");
		}
	}
	
	/**
	 * Initializes and returns a {@link DataSink} object. 
	 * 
	 * @param output The output type, value and parameters 
	 * @return The {@link DataSinks object.
	 * @throws Exception If an error occurred in initializing the {@link DataSink}.
	 */
	public static DataSink getDataSink(Output output) throws Exception {
		
		if(outputTypeToDataSinkClass==null){log.error("IOHandler is not initialized");throw new Exception("IOHandler is not initialized");}
		
		if(output==null){log.error("Output is not set");throw new Exception("Output is not set");}
		
		String outputType = output.getOutputType();
		String outputValue = output.getOutputValue();
		Parameter[] outputParameters = output.getOutputparameters();
		
		if(outputType==null || outputType==null){log.error("Output type is not set properly");return null;}
		
		Class dataSinkClass = outputTypeToDataSinkClass.get(outputType);
		if(dataSinkClass==null){log.error("Could not find data sink with "+outputType+" IOType.");throw new Exception("Could not find data sink with IOType "+outputType);}
		
		try {
			Constructor<DataSink> sinkConstructor = (Constructor<DataSink>)dataSinkClass.getConstructor(dataHandlerConstructorParameterTypes);
			return sinkConstructor.newInstance(outputValue, outputParameters);
		} catch (Exception e) {
			log.error("Error when instanciating the data sink.", e);
			throw new Exception("Error when instanciating the data sink", e);
		}
	}
}
