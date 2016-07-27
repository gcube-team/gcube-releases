package org.gcube.portlets.user.statisticalalgorithmsimporter.client.utils;

import com.sencha.gxt.widget.core.client.box.MessageBox;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class InfoMessageBox extends MessageBox {

	/**
	 * Creates a message box with an info icon and the specified title and
	 * message.
	 * 
	 * @param title
	 *            the message box title
	 * @param message
	 *            the message displayed in the message box
	 */
	public InfoMessageBox(String title, String message) {
		super(title, message);

		setIcon(ICONS.info());
	}

}