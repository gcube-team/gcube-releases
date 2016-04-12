package org.gcube.common.core.resources.runninginstance;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;


/***
 * 
 * @author   Andrea Manzi (CNR)
 *
 */
public class AccessPoint {


	protected RunningInstanceInterfaces runningInstanceInterfaces;

	protected String factoryURI;
	/**
	 * Gets the value of the runningInstanceInterfaces property.
	 *
	 * @return
	 *     possible object is
	 *     {@link RunningInstanceInterfaces }
	 *
	 */
	public RunningInstanceInterfaces getRunningInstanceInterfaces() {
		return runningInstanceInterfaces;
	}

	/**
	 * Sets the value of the runningInstanceInterfaces property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link RunningInstanceInterfaces }
	 *
	 */
	public void setRunningInstanceInterfaces(RunningInstanceInterfaces value) {
		this.runningInstanceInterfaces = value;
	}


	/**
	 * Gets the value of the factoryURI property.
	 *
	 * @return
	 *     possible object is
	 *     {@link String }
	 *
	 */
	@Deprecated
	public String getFactoryURI() {
		return factoryURI;
	}

	/**
	 * Sets the value of the factoryURI property.
	 *
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *
	 */
	@Deprecated
	public void setFactoryURI(String value) {
		this.factoryURI = value;
	}
	
	/**
	 * Returns the asbsolute endpoint of a port-type from its relative endpoint.
	 * @param entryName the relative endpoint.
	 * @return the absolute endpoint.
	 */
	public EndpointReferenceType getEndpoint(String entryName) {
		try {
			for (Endpoint epr : this.getRunningInstanceInterfaces().getEndpoint())
				if (epr.getEntryName().equals(entryName)) 
					return new EndpointReferenceType(new AttributedURI(epr.getValue()));
		}
		catch(Exception e) {}
		return null;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		final AccessPoint other = (AccessPoint) obj;
		
		if (runningInstanceInterfaces == null) {
			if (other.runningInstanceInterfaces != null)
				return false;
		} else if (! runningInstanceInterfaces.equals(other.runningInstanceInterfaces))
			return false;
		
		if (factoryURI == null) {
			if (other.factoryURI != null)
				return false;
		} else if (! factoryURI.equals(other.factoryURI))
			return false;
		
		
		return true;
	}

}
