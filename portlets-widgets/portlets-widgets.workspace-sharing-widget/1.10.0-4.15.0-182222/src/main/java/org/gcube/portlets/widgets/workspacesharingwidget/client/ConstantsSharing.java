/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client;

/**
 * @author Francesco Mangiacrapa  Feb 25, 2014
 *
 */
public interface ConstantsSharing {

	public static final String LOADINGSTYLE = "x-mask-loading";
	public static final String SERVER_ERROR = "Sorry, an error has occurred on the server when";
	public static final String TRY_AGAIN = "Try again";

	public static int WIDTH_DIALOG = 530;
	public static int HEIGHT_DIALOG = 430;

	public static final String PATH_SEPARATOR = "/";

	public static enum LOAD_CONTACTS_AS {
		SHARED_USER, ADMINISTRATOR
	}
}
