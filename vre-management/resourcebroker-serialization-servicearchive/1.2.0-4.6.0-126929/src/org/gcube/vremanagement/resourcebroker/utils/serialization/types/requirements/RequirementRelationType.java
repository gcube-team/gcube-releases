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
 * Filename: RequirementRelationType.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements;

/**
 * How the looked up requirement nodes are in relation with the required
 * value.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public enum RequirementRelationType {
	EQUAL("="),
	LESS_OR_EQUAL("<="),
	GREATER_OR_EQUAL(">="),
	GREATER(">"),
	LESS("<"),
	CONTAINS("contains"),
	NOT_EQUAL("!=");

	private String relType = null;
	private RequirementRelationType(final String relType) {
		this.relType = relType;
	}
	public String getRelType() {
		return this.relType;
	}
}
