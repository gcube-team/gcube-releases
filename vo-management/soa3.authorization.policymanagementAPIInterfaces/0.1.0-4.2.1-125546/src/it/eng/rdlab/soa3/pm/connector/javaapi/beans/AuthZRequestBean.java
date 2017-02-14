package it.eng.rdlab.soa3.pm.connector.javaapi.beans;

import java.util.ArrayList;
import java.util.List;

public class AuthZRequestBean 
{
	private List<Attribute> attributes;
	private String action;
	private String resource;
	
	public AuthZRequestBean() 
	{
		this.attributes = new ArrayList<Attribute>();
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
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
