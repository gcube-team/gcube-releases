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
 * Filename: ResourceOperationException.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.exceptions;

/**
 * If an operation of the library internally fails.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ResourceOperationException extends AbstractResourceException {
	private static final long serialVersionUID = -8748539948441128210L;

	/**
	 *
	 */
	public ResourceOperationException() {
		super();
	}

	/**
	 * @param message
	 */
	public ResourceOperationException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ResourceOperationException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ResourceOperationException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
