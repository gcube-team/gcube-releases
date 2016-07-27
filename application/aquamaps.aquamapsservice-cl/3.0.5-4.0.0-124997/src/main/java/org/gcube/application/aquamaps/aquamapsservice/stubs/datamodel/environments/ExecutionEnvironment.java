package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.DataModel;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ExecutionEnvironment")
public class ExecutionEnvironment extends DataModel{
	

	
	
	
	private String name;
	private String logo;
	private String description;
	private String disclaimer;
	private String policies;
	
	private ArrayList<LogicType> logics= new ArrayList<LogicType>();
	
	private Parameters parameters;
	

	private HashMap<String,String> conf= new HashMap<String, String>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
	public String getPolicies() {
		return policies;
	}
	public void setPolicies(String policies) {
		this.policies = policies;
	}
	public ArrayList<LogicType> getLogics() {
		return logics;
	}
	public void setLogics(ArrayList<LogicType> logics) {
		this.logics = logics;
	}
	public HashMap<String, String> getConf() {
		return conf;
	}
	public void setConf(HashMap<String, String> conf) {
		this.conf = conf;
	}
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
	public Parameters getParameters() {
		return parameters;
	}
	
}
