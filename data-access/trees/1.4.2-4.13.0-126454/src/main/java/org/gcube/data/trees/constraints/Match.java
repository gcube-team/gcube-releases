/**
 * 
 */
package org.gcube.data.trees.constraints;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Accepts values that match a given regular expression when converted to {@link String}.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement 
public class Match extends BaseConstraint<Object> {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement 
	String regex;
	
	Match(){} //here for deserialisation
	
	/**
	 * Creates an instance from a regular expression.
	 * @param regex the regular expression
	 */
	public Match(String regex) {
		this.regex=regex;
	}
	
	/**{@inheritDoc}*/
	public boolean accepts(Object o) {
		return o.toString().matches(regex);
	}
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		return super.toString()+" matches "+regex;
	}
	
	/**{@inheritDoc}*/
	@Override public boolean equals(Object obj) {
		if (!(obj instanceof Match)) return false;
		else return regex.equals(((Match) obj).regex);
	}
	
	/**{@inheritDoc}*/
	@Override public int hashCode() {
		return regex.hashCode()+31*17;
	}
}
