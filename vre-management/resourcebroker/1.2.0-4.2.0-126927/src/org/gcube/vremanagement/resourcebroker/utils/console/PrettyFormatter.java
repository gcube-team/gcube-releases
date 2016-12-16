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
 * Filename: PrettyFormatter.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.console;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class PrettyFormatter {
	/**
	 * Returns the ANSI escaped string for printing it underlined.
	 * @param msg
	 * @return
	 */
	public static String underlined(final String msg) {
		return "\033[4m" + msg + "\033[0m";
	}

	/**
	 * Returns the ANSI escaped string for printing it underlined.
	 * @param msg
	 * @return
	 */
	public static String bold(final String msg) {
		return "\033[1m" + msg + "\033[0m";
	}
}
