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
 * Filename: DeployNode.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback;

import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Artifact for PackageGroup elements.
 * Here only the information relevant to handling the
 * feedback are took into account regardless the packages
 * defined inside it, and so on.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class DeployNode {
	public static final String NODE_TAG = "DeployNode";

	@XStreamAlias("PackageGroup")
	private PackageGroup packageGroup = null;

	public DeployNode(final PackageGroup pg) {
		this.setPackageGroup(pg);
	}

	private int evaluateScore() {
		if (this.getPackageGroup() == null || this.getPackageGroup().getPackages() == null) {
			return 0;
		}
		float total = this.getPackageGroup().getPackages().size();
		float partialScore = 0;
		for (org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageElem p : this.getPackageGroup().getPackages()) {
			// FIXME the null status is considered success
			if (p.getStatus() != null) {
				partialScore += p.getStatus().getWeight();
			} else {
				partialScore += FeedbackStatus.SUCCESS.getWeight();
			}
		}

		float percentage = (100 / total) * partialScore;
		return Math.round(percentage);
	}

	public final int getScore() {
		return this.evaluateScore();
	}

	public final void setPackageGroup(final PackageGroup pg) {
		this.packageGroup = pg;
	}

	public final PackageGroup getPackageGroup() {
		return this.packageGroup;
	}
}
