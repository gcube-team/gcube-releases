package org.gcube.portlets.admin.wftemplates.client.view.dialog;
import org.gcube.portlets.admin.wftemplates.client.event.AddStepEvent;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.shared.HandlerManager;


/**
 * <code> AddStepDialog </code> class is is the Dialog for input the step name
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version April 2011 (0.1) 
 */


public class AddStepDialog extends Dialog {
	final TextField<String> stepName = new TextField<String>();  
	final TextArea stepDescription = new TextArea();  

	/**
	 * 
	 * @param controller
	 */
	public AddStepDialog(final HandlerManager eventBus) {

		setHeading("Add Step");
		FormPanel simple = new FormPanel();  
		simple.setHeaderVisible(false);
		simple.setFrame(true);  

	
		stepName.setFieldLabel("Name");  
		stepName.setAllowBlank(false);
		stepName.setMaxLength(16);
		stepDescription.setFieldLabel("Description");  
		stepDescription.setAllowBlank(false);
		simple.add(stepName);
		simple.add(stepDescription);
		simple.add(new Label("* Description will be show by holding your mouse pointer over the desired step."));
		add(simple);		


		setButtons(Dialog.OKCANCEL);
		ButtonBar buttons = this.getButtonBar();

		setHideOnButtonClick(false);
		Button okbutton = (Button) buttons.getItem(0);

		okbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				eventBus.fireEvent(new AddStepEvent(stepName.getValue(), stepDescription.getValue()));
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
		stepName.focus();
	}

}

