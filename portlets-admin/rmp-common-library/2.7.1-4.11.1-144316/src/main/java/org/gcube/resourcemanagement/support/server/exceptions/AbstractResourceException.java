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
 * Filename: AbstractResourceException.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.exceptions;

/**
 * Represents the basic type of exception thrown by functionalities exposed
 * in this library.
 *
 * The reason is to provide an access point for serialization issues
 * (e.g. for GWT based portlets that require additional serialization
 * annotations).
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class AbstractResourceException extends Exception {
	private static final long serialVersionUID = 4851998460190622583L;

	public AbstractResourceException() {
		super();
	}

	public AbstractResourceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public AbstractResourceException(final String message) {
		super(message);
	}

	public AbstractResourceException(final Throwable cause) {
		super(cause);
	}

}
