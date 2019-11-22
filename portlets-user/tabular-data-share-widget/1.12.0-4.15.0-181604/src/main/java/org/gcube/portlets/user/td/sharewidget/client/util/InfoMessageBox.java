package org.gcube.portlets.user.td.sharewidget.client.util;

import com.sencha.gxt.widget.core.client.box.MessageBox;

/**
 * 
 * @author "Giancarlo Panichi" 
 *  
 *
 */
public class InfoMessageBox extends MessageBox {

	public InfoMessageBox(String title, String message) {
		super(title, message);

		setIcon(ICONS.info());
	}

}