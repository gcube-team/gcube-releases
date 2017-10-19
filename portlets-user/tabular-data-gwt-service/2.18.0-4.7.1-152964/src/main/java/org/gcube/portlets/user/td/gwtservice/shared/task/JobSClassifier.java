package org.gcube.portlets.user.td.gwtservice.shared.task;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public enum JobSClassifier implements Serializable{
	PREPROCESSING("Preprocessing"),
	PROCESSING("Processing"),
	POSTPROCESSING("Postprocessing"),
	DATAVALIDATION("Data Validation"),
	UNKNOWN("Unknown");
	
	
	/**
	 * @param text
	 */
	private JobSClassifier(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}
	
	
	public static List<JobSClassifier> getList(){
		return Arrays.asList(values());
		
	}
	

	public static JobSClassifier getJobClassifierFromId(
			String id) {
		for(JobSClassifier jobClassifier:values()){
			if (id.compareTo(jobClassifier.id) == 0) {
				return jobClassifier;
			}
		}
		return null;
	}
	
	public String getLabel(){
		return id;
	}
	
	
	
}
