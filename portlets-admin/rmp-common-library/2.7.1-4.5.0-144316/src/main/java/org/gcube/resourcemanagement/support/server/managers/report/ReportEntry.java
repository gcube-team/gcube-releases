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
 * Filename: ReportEntry.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.managers.report;

import org.gcube.resourcemanagement.support.server.exceptions.AbstractResourceException;
import org.gcube.resourcemanagement.support.server.managers.resources.AbstractResourceManager;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ReportEntry {
	private ReportOperation operation = null;
	private AbstractResourceManager resource = null;
	private boolean success = false;
	private String message = null;

	public ReportEntry(
			final ReportOperation operation,
			final AbstractResourceManager resource
	) throws AbstractResourceException {
		this(operation, resource, null, false);
	}

	public ReportEntry(
			final ReportOperation operation,
			final AbstractResourceManager resource,
			final boolean success
	) throws AbstractResourceException {
		this(operation, resource, null, success);
	}

	public ReportEntry(
			final ReportOperation operation,
			final AbstractResourceManager resource,
			final String message,
			final boolean success
	) throws AbstractResourceException {
		this.operation = operation;
		this.resource = resource;
		this.success = success;
		this.message = message;
	}
	public final ReportOperation getOperation() {
		return operation;
	}
	public final AbstractResourceManager getResource() {
		return resource;
	}
	public final boolean isSuccess() {
		return success;
	}
	public final void setSuccess(final boolean success) {
		this.success = success;
	}
	public final void setMessage(final String message) {
		this.message = message;
	}
	public final void setMessage(final Throwable ex) {
		this.message = ex.getMessage();
	}

	@Override
	public final String toString() {
		return this.toXML();
	}

	public final String toXML() {
		return
		"\t<Operation type=\"" + this.getOperation().getLabel() + "\">\n" +
		"\t\t<Status>" + (this.isSuccess() ? "SUCCESS" : "FAILURE") + "</Status>\n" +
		"\t\t<Resource>\n" +
		"\t\t\t<ID>" + this.getResource().getID() + "</ID>\n" +
		// If the resource has a name
		(this.getResource().getName() != null ?
				"\t\t\t<Name>" + this.getResource().getName() + "</Name>\n" : "") +

				"\t\t\t<Type>" + this.getResource().getType() + "</Type>\n" +
				// If a message is present
				(this.message != null ?
						"\t\t\t<Message>\n\t\t\t\t" + this.message.trim() + "\n\t\t\t</Message>\n" : "") +

						"\t\t</Resource>\n" +
						"\t</Operation>\n";
	}
}
