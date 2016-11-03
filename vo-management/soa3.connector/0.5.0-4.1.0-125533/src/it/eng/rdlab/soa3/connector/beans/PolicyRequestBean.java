package it.eng.rdlab.soa3.connector.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PolicyRequestBean 
{
	private List<String> attributes;
	private String action;
	private String resource;
	
	public PolicyRequestBean() 
	{
		attributes = new ArrayList<String>();
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
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
	
	

}
