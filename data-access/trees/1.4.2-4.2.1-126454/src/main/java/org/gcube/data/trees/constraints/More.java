/**
 * 
 */
package org.gcube.data.trees.constraints;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Constraints values of type {@link Double} to be strictly greater than a given one.
 * @author Fabio Simeoni
 *
 */
@XmlRootElement 
public class More extends BaseConstraint<Double> {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement private double than;
	
	More(){} //here for deserialisation
	
	/**
	 * Creates a new instance with a double value.
	 * @param n value.
	 */
	public More(double n) {than=n;}
	
	public Double than() {
		return than;
	}
	
	/**{@inheritDoc}*/
	public boolean accepts(Double t) {return t>than;}
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		return super.toString()+" than "+than;
	}
	
	/**{@inheritDoc}*/
	@Override public boolean equals(Object obj) {
		if (!(obj instanceof More)) return false;
		else return than==((More) obj).than;
	}
	
	/**{@inheritDoc}*/
	@Override public int hashCode() {
		long l = Double.doubleToLongBits(than);
		return (int)(l^(l>>>32))+31*17;
	}
}
