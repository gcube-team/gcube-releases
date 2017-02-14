package org.gcube.portlets.user.speciesdiscovery.client.window;

import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class MessageDialog {

	private final MessageBox info;

	public MessageDialog(String headingTxt, String msgTitle, String msgTxt) {

		info = MessageBox.confirm(msgTitle, msgTxt, null);
		info.show();
	}

	public MessageBox getMessageBoxConfirm(){
		return info;
	}

}