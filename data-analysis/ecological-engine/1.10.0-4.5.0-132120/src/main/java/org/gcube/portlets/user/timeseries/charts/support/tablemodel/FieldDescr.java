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
 * Filename: ColumnDescr.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.timeseries.charts.support.tablemodel;

import java.io.Serializable;

import org.gcube.portlets.user.timeseries.charts.support.assertions.Assertion;
import org.gcube.portlets.user.timeseries.charts.support.exceptions.InvalidParameterException;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public final class FieldDescr implements Serializable {
	private static final long serialVersionUID = 532149221570962514L;
	private String alias = null;
	private String rawname = null;

	private FieldDescr() {
		// for serialization only
	}

	public FieldDescr(final String rawname) throws InvalidParameterException {
		this(null, rawname, FieldType.ND);
	}

	public FieldDescr(final String rawname, final FieldType type) throws InvalidParameterException {
		this(null, rawname, type);
	}

	public FieldDescr(final String alias, final String rawname) throws InvalidParameterException {
		this(alias, rawname, FieldType.ND);
		this.setAlias(alias);
		this.setRawName(rawname);
	}

	public FieldDescr(final String alias, final String rawname, final FieldType type) throws InvalidParameterException {
		this();
		this.setAlias(alias);
		this.setRawName(rawname);
	}

	/**
	 * The alias name is the label used to identify the column.
	 * It can be null and in this case the getAlias will return the rawName.
	 * @param alias
	 * @throws InvalidParameterException
	 */
	public void setAlias(final String alias) {
		if (alias != null && alias.trim().length() > 0) {
			this.alias = alias.trim();
		}
	}

	/**
	 * The rawName identifies the name of column on the table.
	 * It cannot be null.
	 * @param rawName
	 */
	public void setRawName(final String rawName) throws InvalidParameterException {
		Assertion<InvalidParameterException> checker = new Assertion<InvalidParameterException>();
		checker.validate(rawName != null && rawName.trim().length() > 0, new InvalidParameterException("The raw name cannot be null or empty."));

		this.rawname = rawName.trim();
	}

	public String getRawName() {
		return this.rawname;
	}

	public String getAlias() {
		if (this.alias != null) {
			return this.alias;
		}
		return this.rawname;
	}
}
