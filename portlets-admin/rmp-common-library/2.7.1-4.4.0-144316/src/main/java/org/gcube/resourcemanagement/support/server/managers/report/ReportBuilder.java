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
 * Filename: ReportBuilder.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.report;

import java.util.List;
import java.util.Vector;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ReportBuilder {
	private List<ReportEntry> entries = null;
	public ReportBuilder() {
		entries = new Vector<ReportEntry>();
	}

	public final void addEntry(final ReportEntry entry) {
		this.entries.add(entry);
	}

	public final int size() {
		return this.entries.size();
	}

	public final String getXML() {
		StringBuilder builder = new StringBuilder();

		builder.append("<Report>\n");
		for (ReportEntry entry : this.entries) {
			builder.append(entry.toXML());
		}
		builder.append("</Report>\n");
		return builder.toString();
	}

	@Override
	public final String toString() {
		return this.getXML();
	}

}
