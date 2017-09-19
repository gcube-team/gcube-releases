/**
 * 
 */
package org.gcube.data.trees.constraints;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Constrains {@link Double} values to be strictly smaller than a given one.
 * 
 * @author Fabio Simeoni
 */
@XmlRootElement 
public class Less extends BaseConstraint<Double> {

	private static final long serialVersionUID = 1L;
	
	@XmlElement private double than;
	Less(){} //here for deserialisation
	
	/**
	 * Creates an instance from a given value.
	 * @param d the value.
	 */
	public Less(double d) {than=d;}
	
	public double than() {
		return than;
	}
	
	/**{@inheritDoc}*/
	public boolean accepts(Double t) {return t<than;}
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		return super.toString()+" than "+than;
	}
	
	/**{@inheritDoc}*/
	@Override public boolean equals(Object obj) {
		if (!(obj instanceof Less)) return false;
		else return than==((Less) obj).than;
	}
	
	/**{@inheritDoc}*/
	@Override public int hashCode() {
		long l = Double.doubleToLongBits(than);
		return (int)(l^(l>>>32))+31*17;
	}
}
