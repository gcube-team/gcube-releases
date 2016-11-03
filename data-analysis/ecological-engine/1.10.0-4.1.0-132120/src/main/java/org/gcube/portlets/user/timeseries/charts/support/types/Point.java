/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: Point.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.timeseries.charts.support.types;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.gcube.portlets.user.timeseries.charts.support.assertions.Assertion;
import org.gcube.portlets.user.timeseries.charts.support.exceptions.InvalidParameterException;


/**
 * The Point consists of a single entry in the graph system.
 * Since to each point multiple values can be related,
 * a point essentially consists of a String (its label),
 * and a list of {@link ValueEntry} couples (String, Value).
 * <p>
 * The type <b>T</b> declares the number format of entries (e.g. int, double...).
 * </p>
 * <p><b>Usage:</b><br/>
 * <pre>
 * <i>// Here a point is represented by integer values on the
 * // X axis and floats on the relative Y axis values.</i>
 * new <b>Point</b>&lt;Integer, Float&gt;("Avg 2010",
 *  2010, <i>// The value of column entry</i>
 *  <i>// The rows associated to this column</i>
 *  new <b>ValueEntry</b>&lt;Float&gt;("Entry1", 500.34f),
 *  new <b>ValueEntry</b>&lt;Float&gt;("Entry2", 230.56f));
 * </pre>
 * </p>
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class Point<S extends Number, T extends Number> implements Serializable {
	private static final long serialVersionUID = 6164075295272357264L;
	private String label = null;
	private S value = null;
	private List<ValueEntry<T>> entries = new Vector<ValueEntry<T>>();

	@SuppressWarnings("unused")
	public Point() {
		// for serialization only
	}

	public Point(final S value)
	throws InvalidParameterException {
		this.setValue(value);
	}

	public Point(final String label, final S value)
	throws InvalidParameterException {
		this(value);
		this.setLabel(label);
	}

	public Point(final S value, final ValueEntry<T>... entries)
	throws InvalidParameterException {
		this(value);
		this.setEntries(entries);
	}

	public Point(final String label, final S value, final ValueEntry<T>... entries)
	throws InvalidParameterException {
		this(label, value);
		this.setEntries(entries);
	}

	public final void setLabel(final String label) {
		if (label != null) {
			this.label = label.trim();
		}
	}

	public final void setValue(final S value) throws InvalidParameterException {
		Assertion<InvalidParameterException> checker = new Assertion<InvalidParameterException>();
		checker.validate(
				value != null,
				new InvalidParameterException("Invalid parameter value. Null not allowed."));
		this.value = value;
	}

	public final S getValue() {
		return this.value;
	}

	public final void addEntry(final ValueEntry<T> entry) {
		this.entries.add(entry);
	}

	public final void setEntries(final ValueEntry<T>... entries) {
		if (entries != null) {
			for (ValueEntry<T> t : entries) {
				this.entries.add(t);
			}
		}
	}

	public final void sortEntries() {
		Collections.sort(entries, new Comparator<ValueEntry<T>>() {
			public int compare(final ValueEntry<T> o1, final ValueEntry<T> o2) {
				o1.getValue().doubleValue();
				return 0;
			};
		});
	}

	public final List<ValueEntry<T>> getEntries() {
		return this.entries;
	}

	public final String getLabel() {
		return this.label;
	}

	@Override
	public final String toString() {
		StringBuilder retval = new StringBuilder("[" + (this.label != null ? this.label : "N/D") + "] {");
		for (ValueEntry<T> yValue : this.getEntries()) {
			retval.append(yValue.toString() + " ");
		}
		retval.append("}");
		return retval.toString();
	}
}
