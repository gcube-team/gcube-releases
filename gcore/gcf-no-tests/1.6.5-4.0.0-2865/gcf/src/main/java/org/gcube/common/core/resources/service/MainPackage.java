package org.gcube.common.core.resources.service;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.resources.service.PortType.Function;

public class MainPackage extends Package {

	protected boolean deployable = true;
	protected String garArchive;
	protected List<Function> serviceEquivalenceFunctions = new ArrayList<Function>();
	protected List<PortType> porttypes = new ArrayList<PortType>();
	
	public boolean isDeployable() {return deployable;}
	public void setDeployable(boolean deployable) {this.deployable = deployable;}
	public String getGarArchive() {return garArchive;}
	public void setGarArchive(String garArchive) {this.garArchive = garArchive;}
	public List<Function> getServiceEquivalenceFunctions() {return serviceEquivalenceFunctions;}
	public void setServiceEquivalenceFunctions(List<Function> serviceEquivalenceFunctions) {this.serviceEquivalenceFunctions = serviceEquivalenceFunctions;}

	public List<PortType> getPorttypes() {
		return this.porttypes;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		final MainPackage other = (MainPackage) obj;
		
		if (deployable != other.deployable) return false;
		
		if (garArchive == null) {
			if (other.garArchive != null)
				return false;
		} else if (! garArchive.equals(other.garArchive))
			return false;
		
		if (serviceEquivalenceFunctions == null) {
			if (other.serviceEquivalenceFunctions != null)
				return false;
		} else if (! serviceEquivalenceFunctions.equals(other.serviceEquivalenceFunctions))
			return false;
		
		if (porttypes == null) {
			if (other.porttypes != null)
				return false;
		} else if (! porttypes.equals(other.porttypes))
			return false;

		return super.equals(obj);
	}

}
