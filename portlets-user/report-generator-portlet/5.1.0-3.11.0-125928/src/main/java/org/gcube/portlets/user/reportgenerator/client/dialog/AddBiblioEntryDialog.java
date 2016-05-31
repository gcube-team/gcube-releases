package org.gcube.portlets.user.reportgenerator.client.dialog;

import org.gcube.portlets.user.reportgenerator.client.events.AddBiblioEvent;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.shared.HandlerManager;
/**
 * The <code> AddBiblioEntryDialog </code> class 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version July 2011 (3.0) 
 */
public class AddBiblioEntryDialog extends Window {

	public AddBiblioEntryDialog(final HandlerManager eventBus) {
		setHeading("Add Citation");  
		setClosable(true);  
		setWidth(400);  
		setHeight(250);  
		setPlain(true);  
		setLayout(new FitLayout());  
	
		final TextField<String> citekey = new TextField<String>();
		citekey.setFieldLabel("Cite key");
		citekey.setAllowBlank(false);
		final TextArea textArea = new TextArea();  
		textArea.setFieldLabel("Citation");
		textArea.setAllowBlank(false);
		textArea.setHeight(130);
		Button addButton = new Button("Add") {
			@Override
			protected void onClick(final ComponentEvent ce) {
				if (citekey.isValid()) {
					eventBus.fireEvent(new AddBiblioEvent(citekey.getValue(), textArea.getValue()));
					close();
				}
			}
		};
		
		Button cancelButton = new Button("Cancel") {
			@Override
			protected void onClick(final ComponentEvent ce) {
				close();
			}
		};

		addButton(addButton);  
		addButton(cancelButton); 
		
		FormData formData = new FormData("-10");
		FormPanel formPanel = new FormPanel();  
		formPanel.setAutoWidth(true);
		formPanel.getHeader().setStyleName("x-hide-panel-header");
		formPanel.setHeaderVisible(false);
		formPanel.setLabelWidth(55);  
		formPanel.setWidth(400);  
		formPanel.setHeight(250);  
		formPanel.add(citekey, formData);  
		formPanel.add(textArea, formData);  
		add(formPanel);  
	}
}
