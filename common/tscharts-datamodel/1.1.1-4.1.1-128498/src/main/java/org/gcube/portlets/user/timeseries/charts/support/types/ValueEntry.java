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
 * Filename: ValueEntry.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.timeseries.charts.support.types;

import java.io.Serializable;

import org.gcube.portlets.user.timeseries.charts.support.assertions.Assertion;
import org.gcube.portlets.user.timeseries.charts.support.exceptions.InvalidParameterException;


/**
 * Each value entry in a graph is identified by its numerical value and
 * possibly its label.
 * <p>
 * <b>Note:</b> the type <b>T</b> must be numerical (int, double, etc.).
 * The label is not mandatory.
 * </p>
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ValueEntry<T extends Number> implements Serializable {
	private static final long serialVersionUID = -6765421417425929840L;
	private String label = null;
	private T value = null;

	@SuppressWarnings("unused")
	private ValueEntry() {
		// for serialization only
	}

	public ValueEntry(final T value) throws InvalidParameterException {
		this.setValue(value);
	}

	public ValueEntry(final String label, final T value) throws InvalidParameterException {
		this(value);
		this.setLabel(label);
	}

	public final void setLabel(final String label) {
		if (label != null) {
			this.label = label.trim();
		}
	}

	public final void setValue(final T value) throws InvalidParameterException {
		Assertion<InvalidParameterException> checker = new Assertion<InvalidParameterException>();
		checker.validate(
				value != null,
				new InvalidParameterException("Invalid parameter value. Null not allowed."));
		this.value = value;
	}

	public final String getLabel() {
		return this.label;
	}

	public final T getValue() {
		return this.value;
	}

	@Override
	public final String toString() {
		return (this.label != null ? this.label : "N/D") + " -> " + this.getValue();
	}
}
