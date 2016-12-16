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
 * Filename: FeedbackStatus.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback;

/**
 * The feedback status represents the score that each node
 * in a deployment phase has reached.
 * Associated to the name itself here we find a weight
 * that represents the percentage of success of that status.
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public enum FeedbackStatus {
	SUCCESS(1),
	PARTIAL(0.8f),
	FAILED(0);

	private float weight = 0;

	private FeedbackStatus(final float w) {
		this.weight = w;
	}

	public float getWeight() {
		return this.weight;
	}
}
