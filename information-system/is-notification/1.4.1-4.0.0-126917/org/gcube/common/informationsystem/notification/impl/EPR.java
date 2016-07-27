package org.gcube.common.informationsystem.notification.impl;

import org.apache.axis.message.addressing.EndpointReferenceType;

/**
 * This class is simply an extension to the Globus EndpointReferenceType class that
 * implements the Comparable interface. 
 * @author Christoph Langguth
 *
 */
public class EPR extends EndpointReferenceType implements Comparable<EndpointReferenceType> {
	
	/** required by Serializable interface */
	public static final long serialVersionUID = 1;
	
	/**
	 * Constructor taking an EndpointReferenceType this object will represent.
	 * @param epr the EndpointReferenceType this object represents.
	 */
	public EPR(EndpointReferenceType epr) {
		super(epr);
	}

	/**
	 * Method required for implementing the Comparable interface
	 * @param o o
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * @return an integer, as specified in Comparable.
	 */
	public int compareTo(EndpointReferenceType o) {
		return this.toString().compareTo(o.toString());
	}
	
	/**
	 * Method overriding the equals() method
	 * @param o o
	 * @return a boolean indicating equality
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof EndpointReferenceType)) return false;
		return compareTo((EndpointReferenceType)o) == 0;
	}
	
	/**
	 * Method overriding the hashCode() method.
	 * @return the hash code of the object
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		
		return this.toString().hashCode();
	}
}
