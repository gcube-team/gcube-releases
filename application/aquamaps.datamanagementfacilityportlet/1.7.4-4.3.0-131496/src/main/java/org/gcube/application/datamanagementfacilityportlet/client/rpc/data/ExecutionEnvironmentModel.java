package org.gcube.application.datamanagementfacilityportlet.client.rpc.data;

import java.util.HashMap;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ExecutionEnvironmentModel extends BaseModel implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -752435221491523377L;
	
	public static final String INFRASTRUCTURE="infrastructure";
	public static final String BACKEND="BACKEND";
	public static final String ENVIRONMENT_NAME="environment_name";
	public static final String MAX_PARTITIONS="max_partitions";
	public static final String MIN_PARTITIONS="min_partitions";
	public static final String DEFAULT_PARTITIONS="default_partitions";
	public static final String LOGO="logo";
	public static final String DESCRIPTION="description";
	public static final String POLICIES="policies";
	public static final String DISCLAIMER="disclaimer";
	public static final String CONFIGURATION="configuration";
	
	public ExecutionEnvironmentModel() {
	}
	
	public ExecutionEnvironmentModel(String infrastructure,String backend,String name,Integer maxPartitions,Integer minPartitions,Integer defaultPartitions,
			String logo,String description,String policies,String disclaimer,HashMap<String,String> configuration){
		set(INFRASTRUCTURE,infrastructure);
		set(BACKEND,backend);
		set(ENVIRONMENT_NAME,name);
		set(MAX_PARTITIONS,maxPartitions);
		set(MIN_PARTITIONS,minPartitions);
		set(DEFAULT_PARTITIONS,defaultPartitions);
		set(LOGO,logo);
		set(POLICIES,policies);
		set(DESCRIPTION,description);
		set(DISCLAIMER,disclaimer);
		set(CONFIGURATION,configuration);
	}
	
	
	public String getInfrastructure(){
		return get(INFRASTRUCTURE);
	}
	public String getBackend(){
		return get(BACKEND);
	}
	public String getName(){
		return get(ENVIRONMENT_NAME);
	}
	public Integer getMaxPartitions(){
		return get(MAX_PARTITIONS);
	}
	public Integer getMinPartitions(){
		return get(MIN_PARTITIONS);
	}
	public Integer getDefaultPartitions(){
		return get(DEFAULT_PARTITIONS);
	}
	public String getLogo(){
		return get(LOGO);
	}
	public String getPolicies(){
		return get(POLICIES);
	}
	public String getDescription(){
		return get(DESCRIPTION);
	}
	public String getDisclaimer(){
		return get(DISCLAIMER);
	}

	public HashMap<String, String> getConfiguration(){
		return get(CONFIGURATION);
	}
	
	@Override
	public String toString() {
		return "ExecutionEnvironmentModel [getInfrastructure()="
				+ getInfrastructure() + ", getBackend()=" + getBackend()
				+ ", getName()=" + getName() + ", getMaxPartitions()="
				+ getMaxPartitions() + ", getMinPartitions()="
				+ getMinPartitions() + ", getDefaultPartitions()="
				+ getDefaultPartitions() + ", getLogo()=" + getLogo()
				+ ", getPolicies()=" + getPolicies() + ", getDescription()="
				+ getDescription() + ", getDisclaimer()=" + getDisclaimer()
				+ "]";
	}
	
	
	
}
