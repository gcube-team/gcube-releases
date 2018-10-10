package org.gcube.vremanagement.vremodeler.resources;

import java.util.ArrayList;
import java.util.List;


public class Functionality {

	private String description;
	private String name;
	private List<Service> services= new ArrayList<Service>();
	private ArrayList<String> portlets= new ArrayList<String>();
	private List<ResourceDefinition<?>> mandatoryResources = new ArrayList<ResourceDefinition<?>>();
	private List<ResourceDefinition<?>> selectableResources = new ArrayList<ResourceDefinition<?>>();
	private boolean mandatory = false;
	
	
	
	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}
	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	public List<ResourceDefinition<?>> getMandatoryResources() {
		return mandatoryResources;
	}
	public List<ResourceDefinition<?>> getSelectableResources() {
		return selectableResources;
	}
	public List<Service> getServices() {
		return services;
	}
	public void setServices(List<Service> services) {
		this.services = services;
	}
	public ArrayList<String> getPortlets() {
		return portlets;
	}
	public void setPortlets(ArrayList<String> portlets) {
		this.portlets = portlets;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void setMandatoryResources(List<ResourceDefinition<?>> mandatoryResources) {
		this.mandatoryResources = mandatoryResources;
	}
	
	public void setSelectableResources(List<ResourceDefinition<?>> selectableResources) {
		this.selectableResources = selectableResources;
	}
	
	
}
