package org.gcube.application.datamanagementfacilityportlet.client.rpc.data;


import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientLogicType;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GroupGenerationRequest implements IsSerializable {

	
	public static final String FIRST_HCAF_ID="FIRST_HCAF_ID";
	public static final String SECOND_HCAF_ID="SECOND_HCAF_ID";
	public static final String NUM_INTERPOLATIONS="NUM_INTERPOLATIONS";
	public static final String FIRST_HCAF_TIME="FIRST_HCAF_TIME";
	public static final String SECOND_HCAF_TIME="SECOND_HCAF_TIME";
	
	//********************** Execution Parameters
	public static final String COMBINE_MATCHING="COMBINE_MATCHING";
	public static final String FORCE_MAPS_REGENERATION="FORCE_MAPS_REGENERATION";
	public static final String GENERATE_MAPS="GENERATE_MAPS";
	public static final String GIS_ENABLED="GIS_ENABLED";
	
	
	private String generationname;
	private String description;
	private ArrayList<Integer> sourceIds= new ArrayList<Integer>();	
	private ClientLogicType logic;
	private ArrayList<String> algorithms=new ArrayList<String>(); 
	
	private HashMap<String,Object> additionalParameters=new HashMap<String, Object>(); 
	
	private ExecutionEnvironmentModel executionEnvironment=null;
	
	
	
	public HashMap<String, Object> getAdditionalParameters() {
		return additionalParameters;
	}
	
	public GroupGenerationRequest() {
		// TODO Auto-generated constructor stub
	}


	public String getGenerationname() {
		return generationname;
	}


	public void setGenerationname(String generationname) {
		this.generationname = generationname;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public ArrayList<Integer> getSourceIds() {
		return sourceIds;
	}


	public void setSourceIds(ArrayList<Integer> sourceIds) {
		this.sourceIds = sourceIds;
	}


	public ClientLogicType getLogic() {
		return logic;
	}


	public void setLogic(ClientLogicType logic) {
		this.logic = logic;
	}


	public ArrayList<String> getAlgorithms() {
		return algorithms;
	}


	public void setAlgorithms(ArrayList<String> algorithms) {
		this.algorithms = algorithms;
	}




	public ExecutionEnvironmentModel getExecutionEnvironment() {
		return executionEnvironment;
	}


	public void setExecutionEnvironment(
			ExecutionEnvironmentModel executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}
	
	
	
	
}
