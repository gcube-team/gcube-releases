/**
 * 
 */
package org.gcube.data.trees.patterns;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.trees.constraints.Constraint;
import org.gcube.data.trees.data.Leaf;


/**
 * A {@link Pattern} that matches the value of a {@link Leaf} against a given Calendar {@link Constraint}.
 * 
 * @author Federico De Faveri (defaveri@isti.cnr.it)
 *
 */
@XmlRootElement(name=CalendarPattern.NAME) 
public final class CalendarPattern extends LeafPattern<java.util.Calendar, Constraint<? super java.util.Calendar>> {
	
	private static final long serialVersionUID = 1L;
	
	static final String NAME="calendar";
	
	/**Formatter for ISO8601.*/
	public static DateFormat ISO8601formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
	/**Formatter for ISO8601 without time zones.*/
	public static DateFormat ISO8601shortFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
	/**Formatter for ISO8601 without time.*/
	public static DateFormat ISO8601minFormatter = new SimpleDateFormat("yyyy-MM-dd");
	
	CalendarPattern(){} //here for deserialisation
	
	/**
	 * Creates an instance with a given constraint.
	 * @param c the constraint
	 */
	public CalendarPattern(Constraint<? super java.util.Calendar> c) {super(c);}
	
	/**{@inheritDoc}*/
	@Override protected java.util.Calendar valueOf(String s) throws Exception {
		java.util.Calendar d = java.util.Calendar.getInstance();

		try {
			d.setTime(ISO8601formatter.parse(s));
		}
		catch (ParseException e1) {
			try{
				d.setTime(ISO8601shortFormatter.parse(s));
			}
			catch(ParseException e2) {
				d.setTime(ISO8601minFormatter.parse(s));
			}
		}
		return d;
	}
	
	@Override
	/**{@inheritDoc}*/
	public String name() {
		return NAME;
	}
}