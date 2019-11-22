package org.gcube.portlets.user.speciesdiscovery.client.window;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class MessageBoxAlert {
	
	private MessageBox box = null;
	
	public MessageBoxAlert(String headerTitle, String msg, Listener<MessageBoxEvent> listener){
		
		if(listener==null)
			listener = new Listener<MessageBoxEvent>() {

				@Override
				public void handleEvent(MessageBoxEvent be) {
					
					
				}
			};
			
		  box = MessageBox.alert(headerTitle, msg, listener);  
		  box.show();
		  
	}
	
	public MessageBox getMessageBoxInsert() {
		return box;
	}

}
