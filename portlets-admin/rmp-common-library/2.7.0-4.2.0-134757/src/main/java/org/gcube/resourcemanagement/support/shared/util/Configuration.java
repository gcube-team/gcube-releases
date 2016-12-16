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
 * Filename: Configuration.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.shared.util;

/**
 * The client side UI configuration.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class Configuration {
	// Delay of popups in mills
	public static final int popupDelay = 4000;
	// Configuration parameters that can be changed through options
	// menu.
	public static boolean openProfileOnLoad = false;
	public static boolean allowMultipleProfiles = false;
	//public static String scopeFile = null;

	public static final String SUPER_USER_CODE = "gcube2017";
	/*
	 * Forces to use flash based charts also in portal mode.
	 * Due to conflicts in the portal with the flash based
	 * charts this functionality usually is avoided.
	 */
	public static final boolean DISABLE_FLASH = false;
}
