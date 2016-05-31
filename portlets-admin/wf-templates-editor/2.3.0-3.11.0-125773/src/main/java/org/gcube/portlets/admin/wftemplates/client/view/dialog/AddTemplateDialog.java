package org.gcube.portlets.admin.wftemplates.client.view.dialog;

import org.gcube.portlets.admin.wftemplates.client.event.AddTemplateEvent;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.shared.HandlerManager;

/**
 * <code> AddTemplateDialog </code> class is is the Dialog for input the saving template name
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */

public class AddTemplateDialog extends Dialog {
	final TextField<String> stepName = new TextField<String>();  

	/**
	 * 
	 * @param controller
	 */
	public AddTemplateDialog(final HandlerManager eventBus) {

		setHeading("Save Template");

		FormPanel simple = new FormPanel();  
		simple.setHeaderVisible(false);
		simple.setFrame(true);  

	
		stepName.setFieldLabel("Name");  
		stepName.setAllowBlank(false);
		stepName.setWidth("100px");
		simple.add(stepName);

		simple.add(new Label("Note: Workflow template can not be edited after saving."));
		add(simple);		


		setButtons(Dialog.OKCANCEL);
		ButtonBar buttons = this.getButtonBar();

		setHideOnButtonClick(false);
		Button okbutton = (Button) buttons.getItem(0);

		okbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				eventBus.fireEvent(new AddTemplateEvent(stepName.getValue()));
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

