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
 * Filename: ConsoleMessageBroker.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.console;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.WidgetsRegistry;

import com.google.gwt.core.client.GWT;

public class ConsoleMessageBroker {

	private static void printMessage(final ConsoleLogSeverity severity, final Object caller, final String message) {
		ConsolePanel console = WidgetsRegistry.getConsole();
		if (console != null) {
			console.handle(new ConsoleMessage(caller, severity, message));
		} else {
			GWT.log(message);
		}
	}

	private static void printMessage(final ConsoleLogSeverity severity, final Class<?> caller, final String message) {
		ConsolePanel console = WidgetsRegistry.getConsole();
		if (console != null) {
			console.handle(new ConsoleMessage(caller.getName(), severity, message));
		} else {
			GWT.log(message);
		}
	}
	
	public static void log(final Object caller, final String message) {
		printMessage(ConsoleLogSeverity.LOG, caller, message);
	}
	public static void info(final Object caller, final String message) {
		printMessage(ConsoleLogSeverity.INFO, caller, message);
	}
	public static void trace(final Object caller, final String message) {
		printMessage(ConsoleLogSeverity.TRACE, caller, message);
	}
	public static void debug(final Object caller, final String message) {
		printMessage(ConsoleLogSeverity.DEBUG, caller, message);
	}
	public static void warning(final Object caller, final String message) {
		printMessage(ConsoleLogSeverity.WARNING, caller, message);
	}
	public static void error(final Object caller, final String message) {
		printMessage(ConsoleLogSeverity.ERROR, caller, message);
	}
	
	
	public static void log(final Class<?> caller, final String message) {
		printMessage(ConsoleLogSeverity.LOG, caller, message);
	}
	public static void info(final Class<?> caller, final String message) {
		printMessage(ConsoleLogSeverity.INFO, caller, message);
	}
	public static void trace(final Class<?> caller, final String message) {
		printMessage(ConsoleLogSeverity.TRACE, caller, message);
	}
	public static void debug(final Class<?> caller, final String message) {
		printMessage(ConsoleLogSeverity.DEBUG, caller, message);
	}
	public static void warning(final Class<?> caller, final String message) {
		printMessage(ConsoleLogSeverity.WARNING, caller, message);
	}
	public static void error(final Class<?> caller, final String message) {
		printMessage(ConsoleLogSeverity.ERROR, caller, message);
	}
}
