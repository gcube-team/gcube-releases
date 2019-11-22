package org.gcube.portlets.user.workspace.client.view.windows;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it Jun 25, 2013
 *
 */
public class InfoDisplayMessage extends InfoDisplay {

	/**
	 * @param title
	 *            message title
	 * @param text
	 *            message text
	 */
	public InfoDisplayMessage(String title, String text) {
		super(title, text);
	}

	/**
	 * @param title
	 *            message title
	 * @param text
	 *            message text
	 * @param milliseconds
	 *            milliseconds
	 */
	public InfoDisplayMessage(String title, String text, int milliseconds) {
		super(title, text, milliseconds);
	}

}
