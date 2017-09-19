package org.gcube.data.spd.model.service.types;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubmitJob {

	@XmlElement
	private String input;
	
	@XmlElement
	private JobType job;
	
	public SubmitJob(String input, JobType job) {
		super();
		this.input = input;
		this.job = job;
	}

	protected SubmitJob() {
		super();
	}
	
	
	public String getInput() {
		return input;
	}
	public JobType getJob() {
		return job;
	}
	
	
	
}
