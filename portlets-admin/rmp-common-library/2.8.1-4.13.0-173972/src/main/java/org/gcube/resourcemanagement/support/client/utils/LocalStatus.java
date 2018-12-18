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
 * Filename: LocalStatus.java
 ****************************************************************************
 * @author <a href="mailto:massimiliano,assante@isti.cnr.it">Massimiliano Assante</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.client.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Here represented the local status of the client side application.
 * @author Massimilianio Assante (ISTI-CNR)
 */
public class LocalStatus {
	private final ArrayList<String> availableScopes = new ArrayList<String>();
	private final ArrayList<String> deployReports = new ArrayList<String>();
	private static final LocalStatus INSTANCE = new LocalStatus();

	public static synchronized LocalStatus getInstance() {
		return INSTANCE;
	}

	public final ArrayList<String> getAvailableScopes() {
		return this.availableScopes;
	}

	public final List<String> getDeployReports() {
		return this.deployReports;
	}
}
