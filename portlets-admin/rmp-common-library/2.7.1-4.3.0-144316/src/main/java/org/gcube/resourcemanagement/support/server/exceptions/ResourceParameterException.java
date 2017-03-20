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
 * Filename: ResourceParameterException.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.exceptions;

/**
 * Wrong parameters provided by the user.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ResourceParameterException extends AbstractResourceException {
	private static final long serialVersionUID = -1117521050663690780L;

	public ResourceParameterException() {
		super();
	}

	public ResourceParameterException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ResourceParameterException(final String message) {
		super(message);
	}

	public ResourceParameterException(final Throwable cause) {
		super(cause);
	}

}
