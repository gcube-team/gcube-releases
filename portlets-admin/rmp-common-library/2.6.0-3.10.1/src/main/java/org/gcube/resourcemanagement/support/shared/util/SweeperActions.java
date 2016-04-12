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
 * Filename: Actions.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.shared.util;

import java.io.Serializable;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public enum SweeperActions implements Serializable {
	// Declarations for getting resources to cleanup
	GET_GHN_MOVE_TO_UNREACHABLE(
			"Expired GHNs",
			"GHNs no more updated for a long time",
			"Changes the status of GHNs to unreachable"),
	GET_GHN_DELETE("Dead GHNs",
			"GHNs having status down or unreachable",
			"Removes the GHNs from the IS"),
	GET_RI_DELETE("Orphan RIs",
			"RIs related to no more available GHNs",
			"Removes the RIs from the IS"),

	// Be sure the names are reported in xq files.
	// Declarations for applying resources cleanup
	APPLY_GHN_MOVE_TO_UNREACHABLE("Change GHN Status"),
	APPLY_GHN_DELETE("Remove Dead GHN"),
	APPLY_RI_DELETE("Remove Orphan RI");

	private String opDescription = null;
	private String label = null;
	private String tooltip = null;

	private SweeperActions(final String label) {
		this.label = label;
	}

	private SweeperActions(final String label, final String opDescription) {
		this(label);
		this.opDescription = opDescription;
	}

	private SweeperActions(final String label, final String tooltip, final String opDescription) {
		this(label, opDescription);
		this.tooltip = tooltip;
	}

	public String getTooltip() {
		return this.tooltip;
	}

	public String getLabel() {
		return label;
	}
	public String getOperationDescription() {
		return opDescription;
	}
}
