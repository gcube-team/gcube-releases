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
 * Filename: ResourceAccessException.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.exceptions;

/**
 * Thrown when is required an operation the user is not allowed to execute.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
@SuppressWarnings("serial")
public class ResourceAccessException extends AbstractResourceException {

	public ResourceAccessException() {
		super();
	}

	public ResourceAccessException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ResourceAccessException(final String message) {
		super(message);
	}

	public ResourceAccessException(final Throwable cause) {
		super(cause);
	}

}
