package org.gcube.portlets.user.templates.client.dialogs;

import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.user.templates.client.TGenConstants;
import org.gcube.portlets.user.templates.client.presenter.Presenter;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <code> ImporterDialog </code> class is is the Dialog for importing template or report sections
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2009 (1.4) 
 */
public class ImporterDialog extends DialogBox {

	/**
	 * this layout panel
	 */
	private VerticalPanel dialogPanel = new VerticalPanel();
	Image loading = new Image(TGenConstants.LOADING_BAR);

	VerticalPanel toReplace = new VerticalPanel();
	Model toimportFrom = null;
	ListBox listbox = new ListBox();

	ListBox listboxCurr = new ListBox();
	CheckBox lastSection =  new CheckBox();
	
	Presenter controller;
	
	/**
	 * 
	 * @param item the item to import
	 * @param controller the c
	 */
	public ImporterDialog(Item item, final Presenter controller) {
		this.controller = controller;

		dialogPanel.setSpacing(4);
		setText("Import from Template or Report");
		toReplace.setWidth("100%");

		HTML templateNameLabel = new HTML("Importing Template, please hold ...");
		toReplace.add(templateNameLabel);
		toReplace.add(loading);


		HorizontalPanel buttonsPanel = new HorizontalPanel();
		HorizontalPanel buttonsContainer = new HorizontalPanel();

		
		// Add a cancel button at the bottom of the dialog
		Button cancelButton = new Button("Close");
		cancelButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				hide(); 				
			}
		});
		
		
		Button applyButton = new Button("Import");
		applyButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				boolean result = false;
				if (lastSection.getValue())
					result = Window.confirm("You are about to import section " + (listbox.getSelectedIndex()+1) + " as last section in the current document");
				else
					result = Window.confirm("You are about to import section " + (listbox.getSelectedIndex()+1) + " before section " + (listboxCurr.getSelectedIndex()+1) + " of the current document");
				
				if (result)
					controller.importSection(toimportFrom, listbox.getSelectedIndex()+1, listboxCurr.getSelectedIndex()+1, lastSection.getValue());
				hide();				
			}
		});
		
		
	

		buttonsPanel.setWidth("100%");
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsContainer.setSpacing(10);
		buttonsContainer.add(applyButton);
		buttonsContainer.add(cancelButton);
		buttonsPanel.add(buttonsContainer);

		dialogPanel.add(toReplace);
		
		
		dialogPanel.add(buttonsPanel);
		dialogPanel.setPixelSize(350, 275);
		setWidget(dialogPanel);


		AsyncCallback<Model> callback = new AsyncCallback<Model>() {

			public void onFailure(Throwable caught) {
				Window.alert("Could not Import template, please try again later: " + caught.getMessage());
			}
			public void onSuccess(Model toLoad) {
				importFinished(toLoad);
			}
		};

		//then is not deployed mode
		if (item == null) {
			controller.getModel().getModelService().readModel("", "", true, callback);
		}
		else {
			//will asyncrously return a SerializableModel instance read from disk
			controller.getModel().getModelService().readModel(item.getName(), item.getId(), true, callback);
		}
	}


	private void importFinished(Model toLoad) {
		HTML label = new HTML("Importing Complete...");
		
		Grid grid = new Grid(4, 2);
		grid.setWidget(0, 0, new HTML("<b>Name: </b>"));
		grid.setWidget(0, 1, new HTML(toLoad.getTemplateName()));
		grid.setWidget(1, 0, new HTML("<b>Author: </b>"));
		grid.setWidget(1, 1, new HTML(toLoad.getAuthor()));
		grid.setWidget(2, 0, new HTML("<b>Last Edit: </b>"));
		grid.setWidget(2, 1, new HTML(""+toLoad.getLastEdit()));
		grid.setWidget(3, 0, new HTML("<b>Total Sections: </b>"));
		grid.setWidget(3, 1, new HTML(""+toLoad.getTotalPages()));

		toReplace.clear();
		toReplace.add(label);
		toReplace.add(grid);
		toReplace.add(new HTML("<hr color=\"#DDDDDD\" height=\"1px\" width=\"100%\" />"));

		
		for (int i = 1; i <= toLoad.getTotalPages(); i++) {
			listbox.addItem("Section " + i, ""+(i-1));
		}
		Grid grid2 = new Grid(1, 2);
		grid2.setWidget(0, 0, new HTML("<b>Choose the section to import: </b>&nbsp;&nbsp;"));
		grid2.setWidget(0, 1, listbox);
		grid2.setCellPadding(5);
		
		
		for (int i = 1; i <= controller.getModel().getTotalPages(); i++) {
			listboxCurr.addItem("Section " + i, ""+(i-1));
		}
		
		lastSection.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CheckBox cb = (CheckBox) event.getSource();
				listboxCurr.setEnabled(! cb.getValue());
				
			}
		});
		
				
		Grid grid3 = new Grid(2, 2);
		grid3.setWidget(0, 0, new HTML("<b>Import Section before: </b>&nbsp;&nbsp;"));
		grid3.setWidget(0, 1, listboxCurr);
		grid3.setWidget(1, 0, new HTML("<b>As last section: </b>&nbsp;&nbsp;"));
		grid3.setWidget(1, 1, lastSection);
		grid3.setCellPadding(5);

		toReplace.add(grid2);
		toReplace.add(new HTML("<hr color=\"#DDDDDD\" height=\"1px\" width=\"100%\" />"));
		toReplace.add(grid3);
		toimportFrom = toLoad;

	}
}


