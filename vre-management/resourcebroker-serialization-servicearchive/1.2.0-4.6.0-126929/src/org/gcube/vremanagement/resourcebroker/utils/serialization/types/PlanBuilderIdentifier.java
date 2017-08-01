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
 * Filename: PlanBuilderIdentifier.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * <p>
 * A structure used to uniquely identify a workflow session.
 * It is used to retrieve the list of reserved GHN descriptors
 * inside the same workflow session.
 * </p>
 * <p>
 * This identifier will be later on used as an unique key
 * inside the response to keep track of the workflow
 * that has generated it.
 * </p>
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class PlanBuilderIdentifier {
	private String id = null;

	/**
	 * Builds a new identifier for the workflow session
	 * and assigns to it a fresh random string.
	 */
	public PlanBuilderIdentifier() {
		this.id = new BigInteger(130, new SecureRandom()).toString(32);
	}

	/**
	 * This constructor is used if the unique identifier
	 * of the workflow session is already known.
	 * Typically this happens only once a feedback is received
	 * and from the previous
	 * {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanResponse}
	 * the requester has retrieved the id.
	 * For this reason it is avoided any external usage.
	 *
	 * @deprecated use {@link PlanBuilderIdentifier#PlanBuilderIdentifier()}
	 * instead.
	 * @param id the identifier to use.
	 */
	public PlanBuilderIdentifier(final String id) {
		if (id != null) {
			this.id = id.trim();
		}
	}

	/**
	 *
	 * @return the string representation of a workflow
	 * identifier.
	 */
	public final String getID() {
		return this.id;
	}

	@Override
	public final String toString() {
		return this.id;
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof PlanBuilderIdentifier) {
			return this.id.equals(((PlanBuilderIdentifier) obj).getID());
		}
		return super.equals(obj);
	}

	@Override
	public final int hashCode() {
		return this.id.hashCode();
	}
}
