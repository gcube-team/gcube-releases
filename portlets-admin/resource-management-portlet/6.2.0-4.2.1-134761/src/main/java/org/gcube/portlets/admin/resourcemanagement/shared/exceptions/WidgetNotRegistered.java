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
 * Filename: WidgetNotRegistered.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.shared.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class WidgetNotRegistered extends Exception implements IsSerializable {
	private static final long serialVersionUID = -4492836891522593176L;

	public WidgetNotRegistered() {
		super();
	}

	public WidgetNotRegistered(String message, Throwable cause) {
		super(message, cause);
	}

	public WidgetNotRegistered(String message) {
		super(message);
	}

	public WidgetNotRegistered(Throwable cause) {
		super(cause);
	}
}
