package gr.cite.geoanalytics.functions.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;

import gr.cite.geoanalytics.functions.configuration.Constants;
import org.apache.commons.jxpath.ri.compiler.Constant;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.geotools.factory.GeoTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.Rand;

import com.sun.media.jai.imageioimpl.ImageReadWriteSpi;

import gr.cite.clustermanager.actuators.functions.ExecutionNotifier;
import gr.cite.clustermanager.model.functions.ExecutionDetails;
import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.gaap.datatransferobjects.plugin.FunctionResponse;
import gr.cite.geoanalytics.functions.common.ExecutionParameters;
import gr.cite.geoanalytics.functions.common.model.functions.FunctionLayerConfigI;
import gr.cite.geoanalytics.functions.common.model.functions.LayerConfig;
import gr.cite.geoanalytics.functions.common.model.functions.RandomEvaluator;
//import gr.cite.geoanalytics.functions.configuration.FunctionsCompCfg;
import gr.cite.geoanalytics.functions.exploration.FeatureBasedAlgorithmParallel;
import gr.cite.geoanalytics.functions.exploration.FeatureBasedAlgorithmParallelSimAn;


@Component
public class FeatureExploreRandomNPVParallel {

	
//	@Autowired private FeatureBasedAlgorithmParallelSimAn featureBasedAlgorithmParallelSimAn;
	@Autowired private FeatureBasedAlgorithmParallel featureBasedAlgorithmParallel;
	
	private ExecutionNotifier executionNotifier;

	
	private static final Logger logger = LoggerFactory.getLogger(FeatureExploreRandomNPVParallel.class);
	
	public static final String FEATURE_TYPE_CRS = "EPSG:4326";

	public FunctionResponse initialize(Map<String, Object> UIParameters) {
		
		String executionID = "exec-"+UUID.randomUUID().toString();
		
		try{
			
			ExecutionParameters executionParameters = (ExecutionParameters)UIParameters.get("executionParameters");
			
			
			String sst = "http://dionysus.di.uoa.gr:3000/geoserver/wcs?request=GetCoverage&version=2.0.1&coverageid=geoanalytics:adbb5464-fae0-447c-b082-0f13c471f56d&format=geotiff";
			
			int DISTANCE_METERS = executionParameters.getSamplingMeters();
			
			double minimumX = executionParameters.getMinX();
			double maximumX = executionParameters.getMaxX();
			double minimumY = executionParameters.getMinY();
			double maximumY = executionParameters.getMaxY();
			
			String [] jars = executionParameters.getJars().stream().toArray(String[]::new);
			
	//		String tenantName = executionParameters.getTenantName();
			
			String layerName = executionParameters.getResultingLayerName();
			String creatorID = executionParameters.getCreatorID();
			
			String tenantID = executionParameters.getTenantID();
			String tenantName = executionParameters.getTenantName();
			
			String userID = executionParameters.getCreatorID();
			String pluginID = executionParameters.getPluginID();
			String projectID = executionParameters.getProjectID();
			
			
			URLClassLoader loader = (URLClassLoader)UIParameters.get("loader");
			addPropsToClasspath("runtime.properties", executionParameters, loader);
			GeoTools.addClassLoader(loader);
			prepareClassloader(Constants.CONTEXT_FILENAME, loader);
			
			OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
			if(registry == null){
				logger.error("Error with JAI initialization (needed for GeoTools).");
			}else {
				try {
	                new ImageReadWriteSpi().updateRegistry(registry);
	            } catch(IllegalArgumentException e) {
	            	logger.info("JAI instance is probably already registered");
	            }
			}
			
			executionNotifier.notifyAbout(new ExecutionDetails(executionID, executionParameters.getGeoanalyticsEndpoint(), executionParameters.getTenantName(), System.currentTimeMillis(), userID, pluginID, projectID));
			
			new Thread(){
				public void run(){
					logger.info("Async execution on cluster has been triggered");
					try{
						SparkConf conf = new SparkConf()
								  .setMaster(executionParameters.getSparkEndpoint())
								  .set("spark.executor.memory", executionParameters.getSparkExecutorMemory())
				//				  .set("spark.driver.memory", "4g") 
								  .setJars(jars)
								  .setAppName("FeatureExploreRandomNPVParallel minX:"+minimumX+" maxX:"+maximumX+" minY:"+minimumY+" maxY:"+maximumY);
						
						SparkSession sparkSession = SparkSession.builder().config(conf).getOrCreate();
						JavaSparkContext sparkContext = new JavaSparkContext(sparkSession.sparkContext());
						
						featureBasedAlgorithmParallel.execute(executionID, sparkContext, layerName, tenantName, tenantID, creatorID, DISTANCE_METERS, executionParameters.getFunctionLayerConfig(), sst, FEATURE_TYPE_CRS, minimumX, minimumY, maximumX, maximumY);
						sparkContext.stop();
					}
					catch(Exception ex){
						ex.printStackTrace();
						logger.error("There was an exception while executing the function on the computation cluster: Details: "+ex.getMessage());
					}
				}
			}.start();
			
		}
		catch(Exception ex){
			logger.error("There was an exception while trying to submit the function to computation cluster: Details: "+ex.getMessage());
		}
		
		return new FunctionResponse(executionID);
	}
	
	
	/**
	 * This function "injects" in the classpath the fat jar file which contains the algorithms + their deps for running on spark
	 */
	private void prepareClassloader(String appContextFilename, ClassLoader loader) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:**/"+appContextFilename);
		context.setClassLoader(loader);
		((ConfigurableApplicationContext)context).refresh();
		featureBasedAlgorithmParallel = context.getBean(FeatureBasedAlgorithmParallel.class);
        executionNotifier = context.getBean(ExecutionNotifier.class);
	}

	
	/**
	 * This function "injects" in the classpath the .properties file which holds the required runtime parameters for spring's injection
	 */
	private void addPropsToClasspath(String propsFileName, ExecutionParameters executionParameters, URLClassLoader loader) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		//add the Constants.CONTEXT_FILENAME needed parameters in the properties file (will be named after Constants.GENERATED_PROPS_FILENAME).
		Properties properties = new Properties();
		properties.put("gr.cite.clustermanager.connectionString", executionParameters.getZookeeperConnStr());
		properties.put("gr.cite.geoanalytics.functions.spark.endpoint", executionParameters.getSparkEndpoint());
		properties.put("gr.cite.geoanalytics.functions.spark.executor.memory", executionParameters.getSparkExecutorMemory());
		properties.put("gr.cite.geoanalytics.token", executionParameters.getGeoanalyticsToken());
		properties.put("gr.cite.geoanalytics.functions.geoanalytics.endpoint", executionParameters.getGeoanalyticsEndpoint());
		properties.put("gr.cite.geoanalytics.functions.spark.geo.splits.x", executionParameters.getSparkGeoSplitsX().toString());
		properties.put("gr.cite.geoanalytics.functions.spark.geo.splits.y", executionParameters.getSparkGeoSplitsY().toString());
		//create temp conf file for spring
		File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "analytics-configs" + File.separator + UUID.randomUUID().toString() + File.separator + propsFileName);
		if(file.exists()) file.delete();
		file.getParentFile().mkdirs();
		FileOutputStream fileOut = new FileOutputStream(file);
		properties.store(fileOut, "Temp functions lib config");
		fileOut.close();
		//add config file on the given classloader
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(loader, new Object[]{file.getParentFile().toURI().toURL()});
	}

	
	
//	public static void main(String[] args) throws Exception {
//		ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
//		
//		FeatureExploreRandomNPVParallel featureExploreRandomNPVParallel = context.getBean(FeatureExploreRandomNPVParallel.class);
//		featureExploreRandomNPVParallel.initialize(new HashMap<String, Object>()); //no parameters needed at this moment when run locally
//	}
}