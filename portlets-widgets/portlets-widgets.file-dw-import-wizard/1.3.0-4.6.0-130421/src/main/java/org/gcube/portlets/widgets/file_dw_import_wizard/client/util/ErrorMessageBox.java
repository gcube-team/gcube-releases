/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.util;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ErrorMessageBox {


	private static final String DETAILS = "Details";

	public static void showError(String title, String failureReason, final String failureDetails, final Listener<MessageBoxEvent> callback)
	{
		final MessageBox box = new MessageBox();
		box.setTitle(title);
		box.setMessage(failureReason);
		box.addCallback(new Listener<MessageBoxEvent>() {

			@Override
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getText().equals(DETAILS)){
				    box.close();
				    showErrorDetails("Error details", failureDetails);
				} else callback.handleEvent(be);
			}
		});
		box.setIcon(MessageBox.ERROR);
		box.getDialog().cancelText = DETAILS;
		box.getDialog().setButtons(MessageBox.OKCANCEL);
		box.show();
	}
	
	public static void showErrorDetails(String title, String failureDetails)
	{
	    final Dialog simple = new Dialog();  
	    simple.setHeading(title);  
	    simple.setButtons(Dialog.OK);  
	    simple.setBodyStyleName("pad-text");  
	    simple.addText("<PRE>"+failureDetails+"</PRE>");  
	    simple.getItem(0).getFocusSupport().setIgnore(true);  
	    simple.setScrollMode(Scroll.AUTO);  
	    simple.setHideOnButtonClick(true);
	    simple.setWidth(400);
	    simple.setHeight(400);
	    simple.show();
	}

}
