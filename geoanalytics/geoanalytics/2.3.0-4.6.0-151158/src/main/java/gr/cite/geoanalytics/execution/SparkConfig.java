package gr.cite.geoanalytics.execution;

import org.springframework.beans.factory.annotation.Value;

public class SparkConfig {

	private String sparkEndpoint;
	private String sparkExecutorMemory;
	private String geoanalyticsEndpoint;
	private String geoanalyticsToken;
	private Integer sparkGeoSplitsX;
	private Integer sparkGeoSplitsY;
	private String zkConnStr;
	
	
	public String getSparkEndpoint() {
		return sparkEndpoint;
	}
	@Value("${gr.cite.geoanalytics.functions.spark.endpoint}")
	public void setSparkEndpoint(String sparkEndpoint) {
		this.sparkEndpoint = sparkEndpoint;
	}
	
	public String getSparkExecutorMemory() {
		return sparkExecutorMemory;
	}
	@Value("${gr.cite.geoanalytics.functions.spark.executor.memory}")
	public void setSparkExecutorMemory(String sparkExecutorMemory) {
		this.sparkExecutorMemory = sparkExecutorMemory;
	}
	
	public String getGeoanalyticsEndpoint() {
		return geoanalyticsEndpoint;
	}
	@Value("${gr.cite.geoanalytics.functions.geoanalytics.endpoint}")
	public void setGeoanalyticsEndpoint(String geoanalyticsEndpoint) {
		this.geoanalyticsEndpoint = geoanalyticsEndpoint;
	}
	
	public String getGeoanalyticsToken() {
		return geoanalyticsToken;
	}
	@Value("${gr.cite.geoanalytics.plugins.basepath}")
	public void setGeoanalyticsToken(String geoanalyticsToken) {
		this.geoanalyticsToken = geoanalyticsToken;
	}
	
	public Integer getSparkGeoSplitsX() {
		return sparkGeoSplitsX;
	}
	@Value("${gr.cite.geoanalytics.functions.spark.geo.splits.x}")
	public void setSparkGeoSplitsX(Integer sparkGeoSplitsX) {
		this.sparkGeoSplitsX = sparkGeoSplitsX;
	}
	
	public Integer getSparkGeoSplitsY() {
		return sparkGeoSplitsY;
	}
	@Value("${gr.cite.geoanalytics.functions.spark.geo.splits.y}")
	public void setSparkGeoSplitsY(Integer sparkGeoSplitsY) {
		this.sparkGeoSplitsY = sparkGeoSplitsY;
	}
	
	public String getZkConnStr() {
		return zkConnStr;
	}
	@Value("${gr.cite.clustermanager.connectionString}")
	public void setZkConnStr(String zkConnStr) {
		this.zkConnStr = zkConnStr;
	}
	
	
	
}
