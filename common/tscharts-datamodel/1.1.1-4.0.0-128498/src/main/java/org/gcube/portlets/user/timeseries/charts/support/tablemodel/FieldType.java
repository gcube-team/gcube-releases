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
 * Filename: ColumnType.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.timeseries.charts.support.tablemodel;

import java.io.Serializable;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public enum FieldType implements Serializable {
	ND("not defined"),
	STRING("string"),
	NUMERIC("int"),
	QUANTITY("quantity"),
	DATE("date");

	private String typeName = null;

	private FieldType(final String typeName) {
		this.typeName = typeName;
	}

	public final String getTypeName() {
		return this.typeName;
	}
}
