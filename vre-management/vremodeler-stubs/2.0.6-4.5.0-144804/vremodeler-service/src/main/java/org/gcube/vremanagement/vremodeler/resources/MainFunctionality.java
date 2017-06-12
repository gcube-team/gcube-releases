package org.gcube.vremanagement.vremodeler.resources;

import java.util.ArrayList;
import java.util.List;

public class MainFunctionality {

	private List<Functionality> functionalities= new ArrayList<Functionality>();
	private String description;
	private String name;
	
	private boolean mandatory;
	
	public List<Functionality> getFunctionalities() {
		return functionalities;
	}

	public void setFunctionalities(List<Functionality> functionalities) {
		this.functionalities = functionalities;
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

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	} 
		
}
