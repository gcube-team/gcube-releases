package org.gcube.data.spd.executor.jobs;

import java.io.Serializable;
import java.util.Calendar;

public interface SpeciesJob extends Serializable, Runnable {

	public JobStatus getStatus() ;
	
	public void setStatus(JobStatus status) ;

	public String getId();
	
	public boolean validateInput(String input);
	
	public int getCompletedEntries();
	
	public Calendar getStartDate();
	
	public Calendar getEndDate();
	
}
