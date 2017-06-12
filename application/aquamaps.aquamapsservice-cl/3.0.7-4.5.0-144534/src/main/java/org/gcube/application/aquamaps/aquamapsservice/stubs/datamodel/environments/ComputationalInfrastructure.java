package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.DataModel;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("ComputationalInfrastructure")
public class ComputationalInfrastructure extends DataModel{

	private String groupLabel;
	private String submissionBackend;
	private ArrayList<ExecutionEnvironment> environments=new ArrayList<ExecutionEnvironment>();
	public String getGroupLabel() {
		return groupLabel;
	}
	public void setGroupLabel(String groupLabel) {
		this.groupLabel = groupLabel;
	}
	public String getSubmissionBackend() {
		return submissionBackend;
	}
	public void setSubmissionBackend(String submissionBackend) {
		this.submissionBackend = submissionBackend;
	}
	public ArrayList<ExecutionEnvironment> getEnvironments() {
		return environments;
	}
	public void setEnvironments(ArrayList<ExecutionEnvironment> environments) {
		this.environments = environments;
	}
	
	
}
