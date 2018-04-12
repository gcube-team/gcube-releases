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
 * Filename: ScopeTest.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.tests;

import java.io.File;

import org.gcube.resourcemanagement.support.server.managers.scope.ScopeManager;

/**
 * Makes tests on ScopeManager.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ScopeTest {

	public static final void testLoadScopes() {
		ScopeManager.setScopeConfigFile("test-suite" + File.separator + "scopes" + File.separator + "scopedata_admin.xml");
		try {
			ScopeManager.update();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Scopes retrievial [ERR]");
			return;
		}
		System.out.println("Scopes retrievial [DONE]");
	}


	public static void main(final String[] args) {
		testLoadScopes();
	}
}
