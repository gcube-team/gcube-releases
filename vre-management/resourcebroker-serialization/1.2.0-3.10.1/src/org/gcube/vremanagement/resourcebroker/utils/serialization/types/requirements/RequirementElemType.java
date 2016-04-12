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
 * Filename: RequirementElemType.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements;

/**
 * A support enum that is used to express how the constraint value
 * should be accessed. Namely if in a String mode the character "<b>'</b>"
 * must be appended to the XPath transformed query.
 * @author Daniele Strollo (ISTI-CNR)
 */
public enum RequirementElemType {
	STRING,
	NUMBER;
}
