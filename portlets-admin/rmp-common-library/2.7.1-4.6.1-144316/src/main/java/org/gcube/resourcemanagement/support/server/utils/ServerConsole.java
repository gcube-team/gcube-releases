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
 * Filename: ServerConsole.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.utils;

import org.gcube.resourcemanagement.support.server.managers.resources.AbstractResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ServerConsole {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResourceManager.class);

	private static final String LOG_PREFIX = "*** [RMP] ";

	public static void error(final String prefix, final String msg) {
		LOGGER.error(LOG_PREFIX + ((prefix != null) ? prefix + " " : "") + msg);
	}
	public static void error(final String prefix, final Throwable exc) {
		LOGGER.error(LOG_PREFIX + ((prefix != null) ? prefix : ""), exc);
	}
	public static void error(final String prefix, final String msg, final Throwable exc) {
		LOGGER.error(LOG_PREFIX + ((prefix != null) ? prefix + " " : "") + msg, exc);
	}
	public static void warn(final String prefix, final String msg) {
		LOGGER.warn(LOG_PREFIX + ((prefix != null) ? prefix + " " : "") + msg);
	}
	public static void warn(final String prefix, final String msg, final Throwable exc) {
		LOGGER.warn(LOG_PREFIX + ((prefix != null) ? prefix + " " : "") + msg, exc);
	}
	public static void info(final String prefix, final String msg) {
		LOGGER.info(LOG_PREFIX + ((prefix != null) ? prefix + " " : "") + msg);
	}
	public static void trace(final String prefix, final String msg) {
		LOGGER.trace(LOG_PREFIX + ((prefix != null) ? prefix + " " : "") + msg);
	}
	public static void trace(final String msg) {
		LOGGER.trace(msg);
	}
	public static void debug(final String msg) {
		LOGGER.debug(msg);
	}
	public static void warn(final String msg) {
		LOGGER.warn(msg);
	}
	public static void debug(final String prefix, final String msg) {
		LOGGER.debug(LOG_PREFIX + ((prefix != null) ? prefix + " " : "") + msg);
	}	
}
