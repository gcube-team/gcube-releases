/**
 * 
 */
package org.gcube.data.trees.patterns;

import static org.gcube.data.trees.data.Nodes.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.trees.constraints.Constraint;
import org.gcube.data.trees.data.Leaf;


/**
 * A {@link Pattern} that matches the value of {@link Leaf}s against given {@link Date} {@link Constraint}.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name=DatePattern.NAME) 

public final class DatePattern extends LeafPattern<java.util.Date, Constraint<? super java.util.Date>> {
	
	private static final long serialVersionUID = 1L;
	static final String NAME="date";
	
	/**Formatter for ISO8601.*/
	public static DateFormat ISO8601formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
	
	DatePattern(){} //here for deserialisation
	
	/**
	 * Creates an instance with a given constraint.
	 * @param c the constraint
	 */
	public DatePattern(Constraint<? super java.util.Date> c) {
		super(c);
	}
	
	/**{@inheritDoc}*/
	@Override protected java.util.Date valueOf(String s) throws Exception {
		return toDate(s);
	}
	
	@Override
	/**{@inheritDoc}*/
	public String name() {
		return NAME;
	}
}