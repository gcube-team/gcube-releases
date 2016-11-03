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
 * Filename: PlanBuilderElem.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.planbuilders;

import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanBuilderIdentifier;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanResponse;

/**
 *<p>
 * A class containing all the information needed to a
 * {@link PlanBuilderTask} to receive the request to make a new
 * plan and for representing the corresponding decision.
 *</p>
 *<p>
 * Encapsulates a {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest}, a {@link PlanResponse} and
 * a list of gHN ids (strings).
 * At each stage of decision planning, implemented through
 * {@link PlanBuilderTask} elements, the elements can be removed
 * from the request (once they are supposed to have reached a satisfying
 * condition to deploy) and the response is incrementally built.
 * The list of selected gHNs is used to avoid multiple usage of the same
 * gHN when overloaded.
 *</p>
 *
 * @see PlanBuilderTask
 * @author Daniele Strollo (ISTI-CNR)
 */

public class PlanBuilderElem {
	/** Identifies the current workflow session. */
	private PlanBuilderIdentifier identifier = null;

	/** The {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} to handle. */
	private PlanRequest request = null;

	/**
	 * The {@link PlanResponse} that incrementally is build and finally returned to the
	 * caller.
	 */
	private PlanResponse response = null;

	/**
	 * Creates a new {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem}.
	 * @param request the {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} to handle.
	 */
	public PlanBuilderElem(
			final PlanRequest request) {
		this (request, null);
	}

	/**
	 * Creates a new {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem}.
	 * @param request the {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} to handle.
	 * @param response if null a new one will be assigned.
	 */
	public PlanBuilderElem(
			final PlanRequest request,
			final PlanResponse response) {
		this.request = request;
		this.identifier = new PlanBuilderIdentifier();
		if (response != null) {
			this.response = response;
		} else {
			this.response = new PlanResponse(this.identifier);
		}
	}

	public final PlanBuilderIdentifier getID() {
		return this.identifier;
	}

	public final PlanRequest getRequest() {
		return request;
	}

	public final PlanResponse getResponse() {
		return this.response;
	}
}
