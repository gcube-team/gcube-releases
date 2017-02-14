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
 * Filename: StatusHandler.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.client.utils;




/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class StatusHandler {
	
	private static CurrentStatus status = new CurrentStatus();

	public static final synchronized void setStatus(final CurrentStatus status) {
		StatusHandler.status = status;
	}

	public static final synchronized CurrentStatus getStatus() {
		return StatusHandler.status;
	}
}
