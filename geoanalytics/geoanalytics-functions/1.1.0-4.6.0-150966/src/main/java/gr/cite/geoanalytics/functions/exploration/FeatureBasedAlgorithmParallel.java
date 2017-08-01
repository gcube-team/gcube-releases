package gr.cite.geoanalytics.functions.exploration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;
import org.geotools.data.*;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import gr.cite.clustermanager.exceptions.NoAvailableGos;
import gr.cite.clustermanager.model.functions.ExecutionDetails;
import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.clustermanager.trafficshaping.SimpleTrafficShaper;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.functions.common.model.functions.FunctionLayerConfigI;
import gr.cite.geoanalytics.functions.common.model.functions.LayerConfig;
import gr.cite.geoanalytics.functions.discovery.PathFinder;
import gr.cite.geoanalytics.functions.filters.CityDistanceFilter;
import gr.cite.geoanalytics.functions.filters.CoordinateFilter;
import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.functions.NPVFunction;
import gr.cite.geoanalytics.functions.functions.RandomNPV;
import gr.cite.geoanalytics.functions.output.GeoJsonStore;
import gr.cite.geoanalytics.functions.output.GeoanalyticsStore;
import gr.cite.geoanalytics.functions.output.ShapefileStore;
import scala.Tuple5;

@Configurable
//@PropertySource("classpath*:**/runtime.properties")
public class FeatureBasedAlgorithmParallel implements FeatureBasedAlgorithmParallelI, Serializable{

	private static final long serialVersionUID = 3033238757748897716L;

	private static final Logger logger = LoggerFactory.getLogger(FeatureBasedAlgorithmParallel.class);
	
	@Autowired private GeoanalyticsStore geoanalyticsStore; 
	
	@Autowired private TrafficShaper trafficShaper;
	
	@Autowired private PathFinder pathFinder;
	
	
	public FeatureBasedAlgorithmParallel(int hPartitions, int vPartitions) {
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
			String tenantName,
			String tenantID,
			String creatorID,
			int scanStepMeters,
			FunctionLayerConfigI functionLayerConfig,
			String sst,
			String crsCode,
			double minimumX, double minimumY, double maximumX, double maximumY) throws Exception {

		
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
		
		
//		Broadcast<String> layerIdBC = sc.broadcast(layerID);
		
		final int PARALLELIZATION_FACTOR = hPartitions * vPartitions;

		JavaRDD<Tuple5<Double, Double, Double, Double, Integer>> partitions = sparkContext.parallelize(list, PARALLELIZATION_FACTOR);
		
		
		Broadcast<PathFinder> pathfinderBC = sparkContext.broadcast(pathFinder);
		
		
		final String paraktiesLayerID = functionLayerConfig.getLayerConfigByObjectID("0").getLayerID();
		final String citiesLayerID = functionLayerConfig.getLayerConfigByObjectID("1").getLayerID();
		final String natura2000LayerID = functionLayerConfig.getLayerConfigByObjectID("2").getLayerID();
		
		
		JavaRDD<List<ShapeMessenger>> simpleFeaturesListRDD = partitions.map(s -> {
			
			String f = String.format("Partition %d %f %f %f %f", s._5(), s._1(), s._2(), s._3(), s._4());
			
			FeatureSource<SimpleFeatureType, SimpleFeature> paraktiesSource = pathfinderBC.getValue().getFeatureSourceFor(paraktiesLayerID);
			FeatureSource<SimpleFeatureType, SimpleFeature> natura2000Source = pathfinderBC.getValue().getFeatureSourceFor(natura2000LayerID);
			FeatureSource<SimpleFeatureType, SimpleFeature> citiesSource = pathfinderBC.getValue().getFeatureSourceFor(citiesLayerID);
			
			CoordinateReferenceSystem crs = CRS.decode(crsCode);
			
			ArrayList<CoordinateFilter> filters = new ArrayList<CoordinateFilter>();
			CityDistanceFilter cityDistanceFilter = new CityDistanceFilter();
			cityDistanceFilter.setCrs(crs);
			cityDistanceFilter.setCitiesSource(citiesSource);
			filters.add(cityDistanceFilter);
			
			Function function = 
//					new RandomNPV();
					new NPVFunction(tenantName);
			
			try {
				return new FeatureBasedAlgorithmParallelExecutor(scanStepMeters).executeForPartition(
					s._1(),	s._2(), s._3(), s._4(), 
					filters, 
					function,
					JTSFactoryFinder.getGeometryFactory(),
					crsCode,
					paraktiesSource,
					natura2000Source);
			}
			catch(Exception ex){
				ex.printStackTrace();
				logger.error("Could not execute for a partition! Error: "+ex.getMessage());
				return new ArrayList<ShapeMessenger>();
			}
			
		});
		
		geoanalyticsStore.storeToGeoanalytics(execID, layerName, tenantID, creatorID, gosDefinition, simpleFeaturesListRDD, crsCode);
		
	}
}
