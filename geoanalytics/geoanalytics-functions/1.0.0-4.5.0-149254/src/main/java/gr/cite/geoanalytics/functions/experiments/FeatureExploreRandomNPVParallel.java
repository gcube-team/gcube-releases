package gr.cite.geoanalytics.functions.experiments;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;

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
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.Rand;

import com.sun.media.jai.imageioimpl.ImageReadWriteSpi;

import gr.cite.gaap.datatransferobjects.plugin.FunctionResponse;
import gr.cite.geoanalytics.functions.common.ExecutionParameters;
import gr.cite.geoanalytics.functions.common.model.LayerConfig;
import gr.cite.geoanalytics.functions.common.model.functions.FunctionExecConfigI;
import gr.cite.geoanalytics.functions.common.model.functions.RandomEvaluator;
import gr.cite.geoanalytics.functions.configuration.FunctionsCompCfg;
import gr.cite.geoanalytics.functions.exploration.FeatureBasedAlgorithmParallel;


@Component
public class FeatureExploreRandomNPVParallel {

	
	@Autowired private FeatureBasedAlgorithmParallel featureBasedAlgorithmParallel;
	@Autowired private FunctionsCompCfg functionsCompCfg;
	
	private static final Logger logger = LoggerFactory.getLogger(FeatureExploreRandomNPVParallel.class);
	
	public static final String FEATURE_TYPE_CRS = "EPSG:4326";

	
	public FunctionResponse initialize(Map<String, Object> UIParameters) throws Exception {
		
		
		ExecutionParameters executionParameters = (ExecutionParameters)UIParameters.get("executionParameters");
		
		LayerConfig parakties = executionParameters.getFunctionExecConfig().getLayerConfigByRequiredName("Coastal areas");
		LayerConfig cities = executionParameters.getFunctionExecConfig().getLayerConfigByRequiredName("Cities locations");
		LayerConfig natura2000 = executionParameters.getFunctionExecConfig().getLayerConfigByRequiredName("Natura 2000 regions");
		
		String paraktiesID = parakties.getLayerID();
		String citiesID = cities.getLayerID();
		String natura2000ID = natura2000.getLayerID();
		String sst = "";
		
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
		
		ClassLoader loader = (ClassLoader)UIParameters.get("loader");
		GeoTools.addClassLoader(loader);
		prepareClassloader("application.xml", loader);
		
		SparkConf conf = new SparkConf()
				  .setMaster(functionsCompCfg.getSparkEndpoint())
				  .set("spark.executor.memory", functionsCompCfg.getSparkExecutorMemory())
//				  .set("spark.driver.memory", "4g") //this has no effect guys (because driver is this app which already has its memory limits set)
				  .setJars(jars)
				  .setAppName("FeatureExploreRandomNPVParallel minX:"+minimumX+" maxX:"+maximumX+" minY:"+minimumY+" maxY:"+maximumY);
		
		SparkSession sparkSession = SparkSession.builder().config(conf).getOrCreate();
		JavaSparkContext sparkContext = new JavaSparkContext(sparkSession.sparkContext());
		
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
		
		String newLayerID = featureBasedAlgorithmParallel.execute(sparkContext, layerName, tenantID, creatorID, DISTANCE_METERS, paraktiesID, citiesID, natura2000ID, sst, FEATURE_TYPE_CRS, minimumX, minimumY, maximumX, maximumY);
	
		logger.info("ANALYTICS RESULTS CAN BE FOUND WITHIN LAYER : "+newLayerID);
		
		sparkContext.stop();
		
		FunctionResponse fr = new FunctionResponse();
		if(newLayerID != null){
			fr.getLayerIDs().add(UUID.fromString(newLayerID));
		}
		
		return fr;
	}
	
	
	
	private void prepareClassloader(String appContextFilename, ClassLoader loader){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:**/"+appContextFilename);
		context.setClassLoader(loader);
		((ConfigurableApplicationContext)context).refresh();
		//for (String bean : context.getBeanDefinitionNames()) { System.out.println(bean); }
        featureBasedAlgorithmParallel = context.getBean(FeatureBasedAlgorithmParallel.class);
        functionsCompCfg = context.getBean(FunctionsCompCfg.class);
	}



	
	
//	public static void main(String[] args) throws Exception {
//		ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
//		
//		FeatureExploreRandomNPVParallel featureExploreRandomNPVParallel = context.getBean(FeatureExploreRandomNPVParallel.class);
//		featureExploreRandomNPVParallel.initialize(new HashMap<String, Object>()); //no parameters needed at this moment when run locally
//	}
}