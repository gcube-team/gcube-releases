package org.gcube.portlets.user.templates.client.dialogs;

import org.gcube.portlets.user.templates.client.model.TemplateModel;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <code> OpenTemplateDialog </code> class is is the Dialog for opening templates
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2008 (0.2) 
 */
public class OpenTemplateDialog extends DialogBox {

	/**
	 * this layout panel
	 */
	private VerticalPanel dialogPanel = new VerticalPanel();
	private ListBox listbox = new ListBox();

	/**
	 * 
	 * @param myController .
	 */
	public OpenTemplateDialog(final Presenter myController) {
		this.
		dialogPanel.setPixelSize(250, 160);
		dialogPanel.setSpacing(4);
		HTML spacer = new HTML("<hr width=\"100%\" />");
		spacer.setHeight("30");

		listbox.setMultipleSelect(true);
		listbox.setHeight("100");
		listbox.setWidth("100%");
		
		
		//buttons
		Button openButton = new Button("Open");		
		openButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				if (listbox.getSelectedIndex() > -1) {
					String selectedItem = listbox.getItemText(listbox.getSelectedIndex());
					myController.openTemplate(selectedItem, "");
					hide();
				}				
			}
		});
		
//		Add a cancel button at the bottom of the dialog
		Button cancelButton = new Button("Close");
		
		cancelButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		HorizontalPanel buttonsContainer = new HorizontalPanel();
		buttonsPanel.setWidth("100%");
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsContainer.setSpacing(10);
		buttonsContainer.add(cancelButton);
		buttonsContainer.add(openButton);
		buttonsPanel.add(buttonsContainer);
		
		dialogPanel.add(listbox);
		dialogPanel.add(buttonsPanel);
		setWidget(dialogPanel);
	}
	
	/**
	 * 
	 * @param templateNames .
	 */
	public void fillListBox(String[] templateNames) {
		for (String tName : templateNames) {
			if (tName.compareTo(TemplateModel.DEFAULT_NAME) != 0)
				listbox.addItem(tName);
		}
	}


}
