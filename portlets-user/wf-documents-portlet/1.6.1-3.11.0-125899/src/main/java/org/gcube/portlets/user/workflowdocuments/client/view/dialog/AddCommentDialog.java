package org.gcube.portlets.user.workflowdocuments.client.view.dialog;
import org.gcube.portlets.user.workflowdocuments.client.event.AddCommentEvent;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.HandlerManager;


/**
 * <code> AddCommentDialog </code> class is is the Dialog for input the new comment from user
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version June 2011 (0.1) 
 */


public class AddCommentDialog extends Dialog {
	final TextArea comment = new TextArea();  

	/**
	 * 
	 * @param controller
	 */
	public AddCommentDialog(final HandlerManager eventBus, final String workflowname, final String workflowid) {
		super.setWidth(450);
		setHeading("Add new comment to "+ workflowname);
		
		FormPanel simple = new FormPanel();  
		simple.setHeaderVisible(false);
		simple.setFrame(true);  
		simple.setLayout(new FitLayout());
		comment.setAllowBlank(false);
		comment.setAutoWidth(true);
		
		simple.add(comment);
		add(simple);		


		setButtons(Dialog.OKCANCEL);
		ButtonBar buttons = this.getButtonBar();

		setHideOnButtonClick(false);
		Button okbutton = (Button) buttons.getItem(0);

		okbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				eventBus.fireEvent(new AddCommentEvent(workflowid, comment.getValue()));
				hide();
			}  
		});  

		Button cancelbutton = (Button) buttons.getItem(1);

		cancelbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				hide();
			}  
		});  
	}
	
	public void focus() {
		comment.focus();
	}

}

