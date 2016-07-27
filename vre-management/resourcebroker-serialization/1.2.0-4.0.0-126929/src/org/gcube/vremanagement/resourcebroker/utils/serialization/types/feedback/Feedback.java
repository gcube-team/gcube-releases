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
 * Filename: Feedback.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Vector;

import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanBuilderIdentifier;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Daniele Strollo (ISTI-CNR)
 */
public class Feedback {
	public static final String NODE_TAG = "Feedback";
	@XStreamAsAttribute
	private String planID = null;

	@XStreamAlias("Scope")
	private String scope = null;

	private List<DeployNode> deployNodes = new Vector<DeployNode>();

	/**
	 * @deprecated for internal use only.
	 */
	public Feedback() {
		this.planID = new BigInteger(130, new SecureRandom()).toString(32);
	}

	public Feedback(final PlanBuilderIdentifier workflowSession, final String scope) {
		this.planID = workflowSession.getID();
		this.scope = scope;
	}

	public final String getPlanID() {
		return planID;
	}

	public final void setPlanID(final String planID) {
		this.planID = planID;
	}

	/**
	 * If the Feedback does not have a status
	 * the score is evaluated.
	 */
	public final int getScore() {
		if (this.getDeployNodes() == null || this.getDeployNodes().size() == 0) {
			return Math.round(FeedbackStatus.SUCCESS.getWeight());
		}
		float partial = 0;
		for (DeployNode dn : this.getDeployNodes()) {
			partial += dn.getScore();
		}
		float percentage = (partial / this.getDeployNodes().size());
		return Math.round(percentage);
	}

	public final List<DeployNode> getDeployNodes() {
		return deployNodes;
	}

	public final void addDeployNode(final DeployNode deployNode) {
		this.deployNodes.add(deployNode);
	}

	public final String getScope() {
		return this.scope;
	}

	public final void setScope(final String scope) {
		this.scope = scope;
	}
}
