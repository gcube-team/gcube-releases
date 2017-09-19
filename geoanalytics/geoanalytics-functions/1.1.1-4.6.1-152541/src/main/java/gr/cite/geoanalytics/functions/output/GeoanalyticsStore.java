package gr.cite.geoanalytics.functions.output;


import java.io.Serializable;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.client.GeoanalyticsImportManagement;

@Component
public class GeoanalyticsStore implements Serializable {

	private static final long serialVersionUID = 4905879552607674013L;

	private final String geoanalyticsEndpoint;
	
	@Autowired private GeoanalyticsImportManagement geoanalyticsImportManagement;
	
	public GeoanalyticsStore(String geoanalyticsEndpoint) throws Exception {
		this.geoanalyticsEndpoint = geoanalyticsEndpoint;
	}
	
	
	public void storeToGeoanalytics(String execID, String layerName, String tenantID, String creatorID, String projectID, GosDefinition gosDefinition, JavaRDD<List<ShapeMessenger>> featuresRDD, String srid) throws Exception{
		geoanalyticsImportManagement.importLayerFromRDD(execID, geoanalyticsEndpoint, gosDefinition, layerName, tenantID, creatorID, projectID, featuresRDD, srid);
	}
	
}
