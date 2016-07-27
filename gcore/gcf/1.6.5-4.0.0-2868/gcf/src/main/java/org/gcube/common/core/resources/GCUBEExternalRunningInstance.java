package org.gcube.common.core.resources;

import java.util.ArrayList;

/**
 *@author Andrea Manzi (ISTI-CNR)
 */
public abstract class GCUBEExternalRunningInstance extends GCUBEResource{
	
	/**
	 * The type of the resource.
	 */
	public static final String TYPE="ExternalRunningInstance";
	

	
	public GCUBEExternalRunningInstance() {
		this.type = TYPE;
	}

	private String description;
	
	
	private ArrayList<RunningInstanceInterface> accesspoint = new ArrayList<RunningInstanceInterface>();
	
	
	private String specificData;



	public ArrayList<RunningInstanceInterface> getAccesspoint() {
		return accesspoint;
	}


	public void setAccesspoint(ArrayList<RunningInstanceInterface> accesspoint) {
		this.accesspoint = accesspoint;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getSpecificData() {
		return specificData;
	}


	public void setSpecificData(String specificData) {
		this.specificData = specificData;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		if (!super.equals(obj)) return false;
		
		final GCUBEExternalRunningInstance other = (GCUBEExternalRunningInstance) obj;
		
		if (accesspoint == null) {
			if (other.accesspoint != null)
				return false;
		} else if (! accesspoint.equals(other.accesspoint))
			return false;
		
		if (specificData == null) {
			if (other.specificData != null)
				return false;
		} else if (! specificData.equals(other.specificData))
			return false;
		
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (! description.equals(other.description))
			return false;
		
		
		return true;
	}
	
	public static class RunningInstanceInterface {
		
		private String Endpoint;
		
		private String WSDL;

		public String getEndpoint() {
			return Endpoint;
		}

		public void setEndpoint(String endpoint) {
			Endpoint = endpoint;
		}

		public String getWSDL() {
			return WSDL;
		}

		public void setWSDL(String wsdl) {
			WSDL = wsdl;
		}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final RunningInstanceInterface other = (RunningInstanceInterface) obj;
			
			if (WSDL == null) {
				if (other.WSDL != null)
					return false;
			} else if (! WSDL.equals(other.WSDL))
				return false;
			
			if (Endpoint == null) {
				if (other.Endpoint != null)
					return false;
			} else if (! Endpoint.equals(other.Endpoint))
				return false;
			
			
			return true;
		}
		
	}
	
	
}
