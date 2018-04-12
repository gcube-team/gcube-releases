package gr.cite.geoanalytics.functions.experiments.production;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.geotools.data.*;
import org.opengis.feature.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import gr.cite.clustermanager.exceptions.NoAvailableGos;
import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.functions.common.model.functions.FunctionLayerConfigI;
import gr.cite.geoanalytics.functions.discovery.PathFinder;
import gr.cite.geoanalytics.functions.experiments.AquacultureAlgorithm;
import gr.cite.geoanalytics.functions.experiments.AquacultureAlgorithmExecutor;
import gr.cite.geoanalytics.functions.filters.CoordinateFilter;
import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.output.GeoanalyticsStore;
import gr.cite.geoanalytics.functions.production.kpis.FoodConsumptionSeaBassFunction;
import scala.Tuple5;

@Configurable
//@PropertySource("classpath*:**/runtime.properties")
public class FoodConsumptionSeaBassAlgorithmParallel implements AquacultureAlgorithm, Serializable{

	private static final long serialVersionUID = 3033238757748897716L;

	private static final Logger logger = LoggerFactory.getLogger(FoodConsumptionSeaBassAlgorithmParallel.class);
	
	@Autowired private GeoanalyticsStore geoanalyticsStore; 
	
	@Autowired private TrafficShaper trafficShaper;
	
	@Autowired private PathFinder pathFinder;
	
	
	public FoodConsumptionSeaBassAlgorithmParallel(int hPartitions, int vPartitions) {
		this.hPartitions = hPartitions;
		this.vPartitions = vPartitions;
	}
	
	private int hPartitions;

	private int vPartitions;

	@SuppressWarnings("unchecked")
	public void execute(
			String execID,
			JavaSparkContext sparkContext,
			String layerName,
			String styleName,
			String projectID,
			String tenantName,
			String tenantID,
			String creatorID,
			int scanStepMeters,
			FunctionLayerConfigI functionLayerConfig,
			String crsCode,
			double minimumX, double minimumY, double maximumX, double maximumY, Map<String, String> credentials) throws Exception {

		
		long timeout = 40000, current = 0;
		long stepping = 10;
		GosDefinition gosDefinition = null;
		try{gosDefinition = trafficShaper.getGosForNewLayer();} catch(NoAvailableGos nag){}
		while(gosDefinition == null && current<timeout){
			Thread.sleep(stepping);
			try{gosDefinition = trafficShaper.getGosForNewLayer();} catch(NoAvailableGos nag){}
			current += stepping;
		}
		
		if(gosDefinition==null || gosDefinition.getGosEndpoint()==null || gosDefinition.getGosEndpoint().isEmpty()) //will not be able to store shapes
			throw new NoAvailableGos("There are no available GOS to store the layer");
		else 
			logger.info("Found an endpoint after "+current+ " milliseconds");
		
		
		double xDistance = Math.abs(maximumX - minimumX) / vPartitions;
		double yDistance = Math.abs(maximumY - minimumY) / hPartitions;

		List<Tuple5<Double, Double, Double, Double, Integer>> list = new ArrayList<Tuple5<Double, Double, Double, Double, Integer>>();

		int cnt = 0;
		for (int i = 0; i < hPartitions; i++) {
			for (int j = 0; j < vPartitions; j++) {
				list.add(new Tuple5<Double, Double, Double, Double, Integer>(
						minimumX + i * xDistance, 
						minimumY + j * yDistance, 
						minimumX + (i+1) * xDistance, 
						minimumY + (j+1) * yDistance, 
						cnt++));
			}
		}
		
		final int PARALLELIZATION_FACTOR = hPartitions * vPartitions;

		JavaRDD<Tuple5<Double, Double, Double, Double, Integer>> partitions = sparkContext.parallelize(list, PARALLELIZATION_FACTOR);
		
		Broadcast<PathFinder> pathfinderBC = sparkContext.broadcast(pathFinder);
		
		final String paraktiesLayerID = functionLayerConfig.getLayerConfigByObjectID("0").getLayerID();
		
		JavaRDD<List<ShapeMessenger>> simpleFeaturesListRDD = partitions.map(s -> {
			
			Function function = new FoodConsumptionSeaBassFunction(tenantName, credentials);
			
			FeatureSource<SimpleFeatureType, SimpleFeature> paraktiesSource = pathfinderBC.getValue().getFeatureSourceFor(paraktiesLayerID);
			
			ArrayList<CoordinateFilter> filters = new ArrayList<CoordinateFilter>();
			
			try {
				return new AquacultureAlgorithmExecutor(scanStepMeters).executeForPartition(
					s._1(),	s._2(), s._3(), s._4(), 
					filters, 
					function,
					crsCode,
					paraktiesSource);
			}
			catch(Exception ex){
				ex.printStackTrace();
				System.out.println("Could not execute for a partition! Error: " + ex.getMessage());
				logger.error("Could not execute for a partition! Error: " + ex.getMessage(), ex);
				return new ArrayList<ShapeMessenger>();
			}
			
		});
		
		List<Map.Entry<String, Class>> schema = new ArrayList<Map.Entry<String, Class>>();
		schema.add(new AbstractMap.SimpleEntry<String, Class>("feed", Double.class));
		geoanalyticsStore.storeToGeoanalytics(execID, layerName, styleName, tenantID, creatorID, projectID, gosDefinition, simpleFeaturesListRDD, crsCode, schema);
		
	}
}
