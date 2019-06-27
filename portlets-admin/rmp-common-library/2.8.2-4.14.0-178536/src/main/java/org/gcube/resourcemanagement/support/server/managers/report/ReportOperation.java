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
 * Filename: ReportOperation.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.report;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public enum ReportOperation {
	AddToScope("Add To Scope");

	private String label = null;
	private ReportOperation(final String label) {
		this.label = label;
	}
	public final String getLabel() {
		return this.label;
	}
}
