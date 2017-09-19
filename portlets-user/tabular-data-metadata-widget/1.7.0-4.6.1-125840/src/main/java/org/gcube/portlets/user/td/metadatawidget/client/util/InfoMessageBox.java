package org.gcube.portlets.user.td.metadatawidget.client.util;

import com.sencha.gxt.widget.core.client.box.MessageBox;

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