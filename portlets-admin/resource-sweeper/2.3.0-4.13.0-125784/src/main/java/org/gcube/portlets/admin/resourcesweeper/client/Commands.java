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
 * Filename: Commands.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcesweeper.client;

import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;

/**
 * Here is a set of commands executed inside callbacks that involve
 * interactions with client-side widgets.
 * Additionally other commands are provided for general purpose functionalities
 * (e.g. refresh components, main components lookup, ...).m
 * @author Daniele Strollo (ISTI-CNR)
 */
public class Commands {
	public static void showPopup(final String title, final String text) {
		showPopup(title, text, 4000);
	}

	public static void showPopup(final String title, final String text, final int delayMills) {
		InfoConfig cfg = new InfoConfig(title, text);
		cfg.display = delayMills;
		Info.display(cfg);
	}

}
