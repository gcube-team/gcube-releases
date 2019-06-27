package org.gcube.portlets.user.workspace.client.view.windows;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class MessageBoxConfirm {
	
	private MessageBox box = null;
	
	public MessageBoxConfirm(String title, String msg) {	
		box =   MessageBox.confirm(title, msg, null);
	}
	
	public MessageBoxConfirm(String title, String msg, Listener<MessageBoxEvent> listener){
		box =   MessageBox.confirm(title, msg, listener);
	}
	
	public MessageBox getMessageBoxConfirm(){
		return box;
	}
}
