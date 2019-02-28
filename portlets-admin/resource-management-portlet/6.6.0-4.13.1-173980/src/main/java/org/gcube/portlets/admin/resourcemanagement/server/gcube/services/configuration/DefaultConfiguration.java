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
 * Filename: DefaultConfiguration.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.server.gcube.services.configuration;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class DefaultConfiguration {

	/**
	 * The user and its credentials
	 * Possible values for credentials:
	 * [USER,ADMIN,DEBUG]
	 * USER_CREDENTIALS = DEBUG
	 * Defines the modality in which the portlet is running
	 * possible values [STANDALONE,PORTAL,NOTDEFINED]
	 */
	public static final String USER_CREDENTIALS = "USER";
	public static final String RUNNING_MODE = "STANDALONE";
	public static final String DEFAULT_USER = "daniele.strollo";

	/**
	 * The scope that will be used as default
	 */
	public static final String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu";

	/*
	 * The target of update notification (if in singleton mode).
	 * Mails MUST be separated by ";"
	 */
	public static final String USERMAIL_TO = "daniele.strollo@gmail.com";
	// USERMAIL_CC = Not supported due to old implementation of mail in liferay


	/*****************************************
	 * PROPERTIES FOR THE SWEEPER
	 *****************************************/
	/*
	 * The max number of minutes from last profile update to consider a GHN alive.
	 */
	public static final String LIVE_GHN_MAX_MINUTES = "40";
	
	/*
	 * The max number of minutes from last profile update to consider a GHN alive.
	 */
	public static final String LIST_GHN_STARTUP = "YES";
}
