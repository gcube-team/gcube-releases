/**
 * 
 */
package org.gcube.data.trees.constraints;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.trees.patterns.DatePattern;

/**
 * Accepts {@link Calendar} values that precede a given one.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement 
public class Before extends BaseConstraint<java.util.Calendar> {

	private static final long serialVersionUID = 1L;
	
	@XmlElement 
	private java.util.Calendar than;
	
	Before(){} //here for deserialisation
	
	/**
	 * Creates an instance with a given calendar.
	 * @param c the calendar
	 */
	public Before(java.util.Calendar c) {
		than=c;
	}
	
	/**{@inheritDoc}*/
	public boolean accepts(java.util.Calendar d) {
		return d.before(than);
	}
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		return super.toString()+" "+DatePattern.ISO8601formatter.format(than.getTime());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((than == null) ? 0 : than.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Before))
			return false;
		Before other = (Before) obj;
		if (than == null) {
			if (other.than != null)
				return false;
		} else if (!than.equals(other.than))
			return false;
		return true;
	}
	
}
