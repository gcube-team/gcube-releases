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
 * Filename: InvalidPermissionsException.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.shared.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class InvalidPermissionsException extends Exception implements IsSerializable {
	private static final long serialVersionUID = 1L;

	public InvalidPermissionsException() {
		super();
	}

	public InvalidPermissionsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPermissionsException(String message) {
		super(message);
	}

	public InvalidPermissionsException(Throwable cause) {
		super(cause);
	}

}
