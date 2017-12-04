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
 * Filename: InvalidParameterException.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.timeseries.charts.support.exceptions;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class InvalidParameterException extends Exception {
	private static final long serialVersionUID = -9108485633158199338L;
	public InvalidParameterException() {
		super();
	}
	public InvalidParameterException(final String message) {
		super(message);
	}
	public InvalidParameterException(final Throwable cause) {
		super(cause);
	}
	public InvalidParameterException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
