package it.eng.rdlab.soa3.pm.connector.service.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RuleJaxBean 
{
	//public static final String ATTRIBUTE_SEPARATOR = "=";
	
	private Map<String,String> attributes;
	private String action;
	private String ruleId;
	private String resource;
	private String dateRange;
	private String timeRange;
	private boolean permitted;
	
	public RuleJaxBean ()
	{
		this.attributes = new HashMap<String,String>();
	}

	
	
	public String getRuleId() {
		return ruleId;
	}



	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}



	public Map<String,String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String,String> attributes) {
		this.attributes = attributes;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}

	public String getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

	public boolean isPermitted() {
		return permitted;
	}

	public void setPermitted(boolean permitted) {
		this.permitted = permitted;
	}
	

	
	
}
