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
 * Filename: PlanExceptionMessages.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders;

import org.gcube.vremanagement.resourcebroker.impl.services.BrokerService;

/**
 * All the possible error massages that can be generated
 * during plan decision making.
 * @author Daniele Strollo (ISTI-CNR)
 */
public enum PlanExceptionMessages {
	RETRY_LATER ("The " + BrokerService.class.getSimpleName() + " is busy. Please try Later!!!"),
	INVALID_REQUEST ("The received plan request is invalid or not well formed."),
	INVALID_REQUEST_SCOPE ("The scope of received plan request is invalid or not allowed."),
	INVALID_RESPONSE ("The response plan built is not correct or partially defined."),
	NO_GHNS_AVAILABLE ("Sorry, no GHNs satisfying your request have been found."),
	REQUIRED_GHN_LOCKED ("The required GHN is already locked by another plan."),
	RESERVATION_EXPIRED_TIME ("The reservation has expired."),
	IS_ACCESS_ERROR ("Sorry, the Information System cannot be contacted."),
	INVALID_GHNS ("The GHNs required in the plan request are not available."),
	GENERIC_ERROR ("A generic exception has been thrown during plan computation."),
	REQUIREMENTS_NOT_SATISFIED ("The requirements expressed on package group cannot be satisfied.");


	private String msg = null;
	PlanExceptionMessages(final String msg) {
		this.msg = msg;
	}
	public final String getMessage() {
		return this.msg;
	}
}
