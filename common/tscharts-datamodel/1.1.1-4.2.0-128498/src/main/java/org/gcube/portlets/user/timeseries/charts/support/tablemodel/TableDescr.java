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
 * Filename: TableDescr.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.timeseries.charts.support.tablemodel;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import org.gcube.portlets.user.timeseries.charts.support.assertions.Assertion;
import org.gcube.portlets.user.timeseries.charts.support.exceptions.InvalidParameterException;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public final class TableDescr implements Serializable {
	private static final long serialVersionUID = -4850971456321479627L;
	private String name = null;
	private String alias = null;
	private List<FieldDescr> fields = new Vector<FieldDescr>();

	private TableDescr() {
		// for serialization only
	}
	public TableDescr(final String name) throws InvalidParameterException {
		this();
		this.setName(name);
	}
	public TableDescr(final String name, final String alias) throws InvalidParameterException {
		this(name);
		this.setAlias(alias);
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) throws InvalidParameterException {
		Assertion<InvalidParameterException> checker = new Assertion<InvalidParameterException>();
		checker.validate(name != null && name.trim().length() > 0, new InvalidParameterException("The table name cannot be null or empty."));
		this.name = name.trim();
	}
	public void setAlias(final String alias) {
		if (alias != null && alias.trim().length() > 0) {
			this.alias = alias.trim();
		}
	}
	public String getAlias() {
		if (alias != null) {
			return alias;
		}
		return name;
	}

	public List<FieldDescr> getFields() {
		return this.fields;
	}

	public void addFields(final FieldDescr... fields) {
		if (fields == null) {
			return;
		}
		for (FieldDescr field : fields) {
			this.fields.add(field);
		}
	}
}
