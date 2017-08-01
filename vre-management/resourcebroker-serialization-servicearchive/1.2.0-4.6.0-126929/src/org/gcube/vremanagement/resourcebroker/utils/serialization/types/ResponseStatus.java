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
 * Filename: ResponseStatus.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ResponseStatus {
	@XStreamAlias("value")
	@XStreamAsAttribute
	private String status = null;
	@XStreamAlias("ErrorMsg")
	private String msg = null;
	@XStreamAlias("FailedGroup")
	private PackageGroup position = null;

	public ResponseStatus() {
		super();
	}
	public ResponseStatus(final String status) {
		this.status = status;
	}
	public ResponseStatus(final String status, final String message, PackageGroup position) {
		this.status = status;
		this.msg = ((position != null) ? "PkgGroup ID: " + position.getID() + ", PkgGroup Service: " + position.getServiceName() + ". " : "") + message;
		this.position = position;
	}
	public ResponseStatus(final String status, final String message) {
		this(status);
		this.msg = message;
	}
	public final String getStatus() {
		return status;
	}
	public final void setStatus(final String status) {
		this.status = status;
	}
	public final String getMsg() {
		return msg;
	}
	public final void setMsg(final String msg) {
		this.msg = msg;
	}
	public PackageGroup getPosition() {
		return this.position;
	}
	public void setPosition(PackageGroup position) {
		this.position = position;
	}
}