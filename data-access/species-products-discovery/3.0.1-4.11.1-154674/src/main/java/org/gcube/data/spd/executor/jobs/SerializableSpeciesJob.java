package org.gcube.data.spd.executor.jobs;

import java.io.Serializable;
import java.util.Calendar;

import org.gcube.data.spd.model.service.types.JobStatus;

public class SerializableSpeciesJob extends SpeciesJob implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JobStatus status;
	private String id;
	private int completedEntries;
	private Calendar startDate;
	private Calendar endDate;
	
	public SerializableSpeciesJob(JobStatus status, String id,
			int completedEntries, Calendar startDate, Calendar endDate) {
		super();
		this.status = status!=JobStatus.COMPLETED?JobStatus.FAILED:JobStatus.COMPLETED;
		this.id = id;
		this.completedEntries = completedEntries;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	
	
	@Override
	public void execute() {}



	@Override
	public boolean isResubmitPermitted() {
		return false;
	}



	@Override
	public JobStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(JobStatus status) {
		this.status= status;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean validateInput(String input) {
		return false;
	}

	@Override
	public int getCompletedEntries() {
		return completedEntries;
	}

	@Override
	public Calendar getStartDate() {
		return startDate;
	}

	@Override
	public Calendar getEndDate() {
		return endDate;
	}

}
