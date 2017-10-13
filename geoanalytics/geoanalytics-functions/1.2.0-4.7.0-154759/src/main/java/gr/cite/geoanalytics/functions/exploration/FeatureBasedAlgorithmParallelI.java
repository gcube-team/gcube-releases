package gr.cite.geoanalytics.functions.exploration;

import java.util.Map;

import org.apache.spark.api.java.JavaSparkContext;

import gr.cite.geoanalytics.functions.common.model.functions.FunctionLayerConfigI;

public interface FeatureBasedAlgorithmParallelI {

	
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
			String sst,
			String crsCode,
			double minimumX, double minimumY, double maximumX, double maximumY, Map<String, String> credentials) throws Exception;
	
	
	
}
