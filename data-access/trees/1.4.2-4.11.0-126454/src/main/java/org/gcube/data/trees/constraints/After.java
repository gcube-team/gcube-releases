/**
 * 
 */
package org.gcube.data.trees.constraints;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.trees.patterns.DatePattern;

/**
 * Accepts {@link Calendar} values that follow a given one.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement 
public class After extends BaseConstraint<java.util.Calendar> {

	private static final long serialVersionUID = 1L;
	
	@XmlElement 
	private Calendar than;
	
	After(){} //here for deserialisation
	
	/**
	 * Creates an instance with a given calendar.
	 * @param c the calendar
	 */
	public After(Calendar c) {
		than=c;
	}
	
	/**{@inheritDoc}*/
	public boolean accepts(Calendar d) {
		return d.after(than);
	}
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		return super.toString()+" "+DatePattern.ISO8601formatter.format(than.getTime());
	}
	
	/**{@inheritDoc}*/
	@Override public boolean equals(Object obj) {
		if (!(obj instanceof After)) return false;
		else return than.equals(((After) obj).than);
	}
	
	/**{@inheritDoc}*/
	@Override public int hashCode() {
		return than.hashCode()+31*17;
	}
}
