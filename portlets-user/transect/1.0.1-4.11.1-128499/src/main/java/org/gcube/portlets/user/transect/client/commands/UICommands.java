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
 * Filename: UICommands.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.transect.client.commands;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class UICommands {

	public static void showPopup(final String title, final String text) {
		showPopup(title, text, 6000);
	}

	public static void showPopup(final String title, final String text, final int delayMills) {
		InfoConfig cfg = new InfoConfig(title, text);
		cfg.display = delayMills;
		Info.display(cfg);
	}

	public static void showAlert(final String title, final String text) {
		MessageBox.alert(title, text, null);
	}

	public static void log(final String message) {
		GWT.log(message);
	}

	public static void log(final String message, final Throwable e) {
		GWT.log(message, e);
	}

	public static void log(final Throwable e) {
		GWT.log("Exception thrown: ", e);
	}

	/**
	 * When masking the component show the rotating gear.
	 * @param component the element to mask
	 * @param message the message to show during loading
	 */
	public static void mask(final String message, final Component component) {
		if (component != null) {
			if (message != null) {
				component.mask(message, "loading-indicator");
			} else {
				component.mask();
			}
		}
	}

	public static void unmask(final Component component) {
		if (component != null) {
			component.unmask();
		}
	}
}
