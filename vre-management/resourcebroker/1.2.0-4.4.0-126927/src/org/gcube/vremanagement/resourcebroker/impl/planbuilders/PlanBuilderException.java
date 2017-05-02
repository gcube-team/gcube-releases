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
 * Filename: PlanBuilderException.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;

/**
 * The only kind of exceptions that can be thrown during
 * decision planning.
 * Internally contains the message to return encapsulated
 * inside the
 * {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanResponse}
 * returned to the requester.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class PlanBuilderException extends GCUBEFault {
	private static final long serialVersionUID = -3572721717863067815L;
	private PlanExceptionMessages msg = null;
	private String reason = null;
	private PackageGroup position = null;

	public PlanBuilderException(final PlanExceptionMessages errCode) {
		super(errCode.getMessage());
		this.msg = errCode;
	}

	public PlanBuilderException(final PlanExceptionMessages errCode, final String message) {
		super(errCode.getMessage() + ". " + message);
		this.reason = message;
		this.msg = errCode;
	}

	/**
	 * If during the deployment phase the exception is thrown in a well precise
	 * PackageGroup node, it is passed here as parameter so that the error message
	 * of the returned {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanResponse}
	 * gives a detailed information about the node where the failure has arisen.
	 * @param errCode a pre-built error message {@link PlanExceptionMessages} to return to the requester inside the response
	 * @param position the {@link PackageGroup} node where the failure happened.
	 */
	public PlanBuilderException(final PlanExceptionMessages errCode, final PackageGroup position) {
		this(errCode);
		this.position = position;
	}

	/**
	 * Similar to the {@link PlanBuilderException#PlanBuilderException(PlanExceptionMessages, PackageGroup)}
	 * with an additional parameter giving additional information about the failure.
	 */
	public PlanBuilderException(final PlanExceptionMessages errCode, final PackageGroup position, final String message) {
		this(errCode, message);
		this.reason = message;
		this.msg = errCode;
		this.position = position;
	}

	public final PackageGroup getPosition() {
		return this.position;
	}

	@Override
	public final String getMessage() {
		return this.msg.getMessage() + (this.reason != null ? " " + this.reason : "");
	}

	@Override
	public final synchronized boolean equals(final Object obj) {
		return super.equals(obj);
	}

	@Override
	public final synchronized int hashCode() {
		return super.hashCode();
	}
}
