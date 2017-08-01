package gr.cite.geoanalytics.functions.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.cite.geoanalytics.functions.common.model.functions.FunctionLayerConfigI;

public class ExecutionParameters {

	
	private FunctionLayerConfigI functionLayerConfig;
	
	private double minX;    //minimum x of the bounding box
	private double maxX;	//maximum x of the bounding box
	private double minY;	//minumum y of the bounding box
	private double maxY;	//maximum y of the bounding box
	
	private int samplingMeters;   //sampling distance in meters
	
	private List<String> jars = new ArrayList<String>(); //filesystem filepaths of the jars to be broadcasted to spark.
	
	private String tenantName;	//name of the tenant e.g. "/gcube/devsec/devVRE";
	
	private String tenantID;	//ID of the tenant
	
	private String creatorID;   // the unique id by which Creator exists in geoanalytics domain
	
	private String resultingLayerName;  //the name for the new layer which will hold the results. The id is auto generated.
	
	private String pluginID;
	
	private String projectID;
	
	
	//zookeeper cluster endpoint (connection string)
	private String zookeeperConnStr;
	//spark config (for driver and executors)
	private String sparkEndpoint;
	private String sparkExecutorMemory;
	private String geoanalyticsEndpoint;
	private String geoanalyticsToken;
	private Integer sparkGeoSplitsX;
	private Integer sparkGeoSplitsY;
	
	

	public FunctionLayerConfigI getFunctionLayerConfig() {
		return functionLayerConfig;
	}

	public void setFunctionExecConfig(FunctionLayerConfigI functionLayerConfig) {
		this.functionLayerConfig = functionLayerConfig;
	} 

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public int getSamplingMeters() {
		return samplingMeters;
	}

	public void setSamplingMeters(int samplingMeters) {
		this.samplingMeters = samplingMeters;
	}

	public List<String> getJars() {
		return jars;
	}

	public void setJars(List<String> jars) {
		this.jars = jars;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantID() {
		return tenantID;
	}

	public void setTenantID(String tenantID) {
		this.tenantID = tenantID;
	}

	public String getCreatorID() {
		return creatorID;
	}

	public void setCreatorID(String creatorID) {
		this.creatorID = creatorID;
	}

	public String getPluginID() {
		return pluginID;
	}

	public void setPluginID(String pluginID) {
		this.pluginID = pluginID;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	
	public String getResultingLayerName() {
		return resultingLayerName;
	}

	public void setResultingLayerName(String resultingLayerName) {
		this.resultingLayerName = resultingLayerName;
	}

	public String getSparkEndpoint() {
		return sparkEndpoint;
	}

	public void setSparkEndpoint(String sparkEndpoint) {
		this.sparkEndpoint = sparkEndpoint;
	}

	public String getSparkExecutorMemory() {
		return sparkExecutorMemory;
	}

	public void setSparkExecutorMemory(String sparkExecutorMemory) {
		this.sparkExecutorMemory = sparkExecutorMemory;
	}

	public String getGeoanalyticsEndpoint() {
		return geoanalyticsEndpoint;
	}

	public void setGeoanalyticsEndpoint(String geoanalyticsEndpoint) {
		this.geoanalyticsEndpoint = geoanalyticsEndpoint;
	}

	public String getGeoanalyticsToken() {
		return geoanalyticsToken;
	}

	public void setGeoanalyticsToken(String geoanalyticsToken) {
		this.geoanalyticsToken = geoanalyticsToken;
	}

	public Integer getSparkGeoSplitsX() {
		return sparkGeoSplitsX;
	}

	public void setSparkGeoSplitsX(Integer sparkGeoSplitsX) {
		this.sparkGeoSplitsX = sparkGeoSplitsX;
	}

	public Integer getSparkGeoSplitsY() {
		return sparkGeoSplitsY;
	}

	public void setSparkGeoSplitsY(Integer sparkGeoSplitsY) {
		this.sparkGeoSplitsY = sparkGeoSplitsY;
	}

	public String getZookeeperConnStr() {
		return zookeeperConnStr;
	}

	public void setZookeeperConnStr(String zookeeperConnStr) {
		this.zookeeperConnStr = zookeeperConnStr;
	}

	
}
