package gr.cite.geoanalytics.functions.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.cite.geoanalytics.functions.common.model.functions.FunctionExecConfigI;

public class ExecutionParameters {

	
	private FunctionExecConfigI functionExecConfig;
	
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

	
	public FunctionExecConfigI getFunctionExecConfig() {
		return functionExecConfig;
	}

	public void setFunctionExecConfig(FunctionExecConfigI functionExecConfig) {
		this.functionExecConfig = functionExecConfig;
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

	public String getResultingLayerName() {
		return resultingLayerName;
	}

	public void setResultingLayerName(String resultingLayerName) {
		this.resultingLayerName = resultingLayerName;
	}

	
}
