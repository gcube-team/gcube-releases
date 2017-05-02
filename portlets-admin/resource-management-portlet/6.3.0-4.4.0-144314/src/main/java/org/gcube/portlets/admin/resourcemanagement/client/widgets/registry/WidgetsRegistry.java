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
 * Filename: WidgetsRegistry.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.registry;

import java.util.HashMap;

import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsolePanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.MainPanel;
import org.gcube.portlets.admin.resourcemanagement.shared.exceptions.WidgetNotRegistered;
import org.gcube.resourcemanagement.support.shared.util.Assertion;

import com.google.gwt.user.client.ui.Widget;

public class WidgetsRegistry {
	private static final HashMap<String, Object> registeredWidgets = new HashMap<String, Object>();

	public static synchronized void registerWidget(final String ID, final Widget widget) {
		registeredWidgets.put(ID, widget);
	}

	public static synchronized void registerPanel(final String ID, final MainPanel widget) {
		registeredWidgets.put(ID, widget);
	}

	public static synchronized MainPanel getPanel(final String ID) {
		if (!registeredWidgets.containsKey(ID) || !(registeredWidgets.get(ID) instanceof MainPanel)) {
			return null;
		}
		return (MainPanel) registeredWidgets.get(ID);
	}

	public static synchronized Widget getWidget(final String ID) {
		if (!registeredWidgets.containsKey(ID) || !(registeredWidgets.get(ID) instanceof Widget)) {
			return null;
		}
		return (Widget) registeredWidgets.get(ID);
	}
	public static synchronized boolean containsElem(final String ID) {
		return registeredWidgets.containsKey(ID);
	}

	public static synchronized Object getElem(final String key){
		return registeredWidgets.get(key);
	}

	public static synchronized void unregisterWidget(final String key) {
		if (registeredWidgets.containsKey(key)) {
			registeredWidgets.remove(key);
		}
	}

	public static synchronized void registerElem(final String ID, final Object widget) {
		registeredWidgets.put(ID, widget);
	}

	@SuppressWarnings("unchecked")
	public static final synchronized <T> Object getElem(final String key, final Class<T> elemType)
	throws WidgetNotRegistered {
		Assertion<WidgetNotRegistered> checker = new Assertion<WidgetNotRegistered>();
		checker.validate(registeredWidgets.containsKey(key), new WidgetNotRegistered("The widget " + key + " has not be registered"));
		Object retval = registeredWidgets.get(key);

		try {
			return (T) retval;
		} catch (ClassCastException e) {
			throw new WidgetNotRegistered("The registered element is not of declared type");
		}
	}

	/**
	 * Utility that returns the singleton instance of console panel.
	 * @return null if the console has not been registered
	 */
	public static ConsolePanel getConsole() {
		try {
			return (ConsolePanel) WidgetsRegistry.getElem(UIIdentifiers.CONSOLE_WIDGET_ID, ConsolePanel.class);
		} catch (WidgetNotRegistered e) {
			return null;
		}
	}

}
