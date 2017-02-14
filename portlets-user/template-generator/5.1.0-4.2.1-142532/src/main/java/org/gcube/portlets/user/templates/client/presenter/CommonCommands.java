package org.gcube.portlets.user.templates.client.presenter;

import java.util.Arrays;

import org.gcube.portlets.d4sreporting.common.client.CommonConstants;
import org.gcube.portlets.user.templates.client.TGenConstants;
import org.gcube.portlets.user.templates.client.dialogs.ImageUploaderDialog;
import org.gcube.portlets.user.templates.client.dialogs.ImporterDialog;
import org.gcube.portlets.user.templates.client.model.TemplateModel;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;



/**
 * * 
 * /**
 * <code> CommonCommands </code> class contains the menu commands for the UI
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class CommonCommands {

	public Command openTemplate;
	public Command importTemplateCommand;
	public Command insertImage;
	public Command saveTemplate;
	public Command saveTemplateAs;
	/**
	 * 
	 */
	public Command pickColor;
	private Presenter controller;


	/**
	 * 
	 */
	private void showSaveTemplateDialog() {
		
		ItemType[] types = {ItemType.FOLDER};
		final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Save Template, choose folder please:", "", Arrays.asList(types));
		WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){
			 
			@Override
			public void onSaving(Item parent, String fileName) {
				//checking user input
				String inputUser = fileName;
				String newTemplateName = inputUser;
				if (controller.getModel().getTemplateName().compareTo(newTemplateName) != 0) {
					newTemplateName = newTemplateName.trim();
					controller.getModel().setTemplateName(newTemplateName);
				}
				controller.changeTemplateName(newTemplateName);
				controller.saveTemplate(parent.getId());
				navigator.hide();
			}
	 
			@Override
			public void onAborted() {
				GWT.log("onAborted");
			}
	 
			@Override
			public void onFailed(Throwable throwable) {
				GWT.log("onFailed");
			}
		};
		navigator.addWorkspaceExplorerSaveNotificationListener(listener);
	    navigator.show();				
		
	}


	public CommonCommands(final Presenter controller) {	
		this.controller = controller;
		openTemplate = new Command() {	
			public void execute() {				;
			ItemType[] types = {ItemType.REPORT_TEMPLATE};
			final WorkspaceExplorerSelectDialog wpTreepopup = new WorkspaceExplorerSelectDialog("Select a Template to open", Arrays.asList(types), Arrays.asList(types));

			WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {

				@Override
				public void onSelectedItem(Item item) {
					controller.openTemplate(item.getName(), item.getId());					
					wpTreepopup.hide();
				}
				@Override
				public void onFailed(Throwable throwable) {
					Window.alert("There are networks problem, please check your connection.");            
				}				 
				@Override
				public void onAborted() {}
				@Override
				public void onNotValidSelection() {				
				}
			};
			wpTreepopup.addWorkspaceExplorerSelectNotificationListener(listener);
			wpTreepopup.show();					

			}			
		};

		insertImage = new Command() {	
			public void execute() {				
				int left = controller.getHeader().getMainLayout().getAbsoluteLeft() + 50;
				int top = controller.getHeader().getMainLayout().getAbsoluteTop() + 25;
				ImageUploaderDialog dlg = new ImageUploaderDialog(controller);
				dlg.setAnimationEnabled(true);
				dlg.setPopupPosition(left, top);
				dlg.show();
			}
		};
		pickColor = new Command() {	
			public void execute() {				
				MessageBox.alert("Warning", "It is not possible to choose font colors at template definition time.", null);
			}
		};


		importTemplateCommand  = new Command() {			
			public void execute() {
				if (! TGenConstants.isDeployed) {
					int left = controller.getHeader().getMainLayout().getAbsoluteLeft() + 50;
					int top = controller.getHeader().getMainLayout().getAbsoluteTop() + 25;
					ImporterDialog dlg = new ImporterDialog(null, controller);
					dlg.setPopupPosition(left, top);
					dlg.setAnimationEnabled(true);
					dlg.show();
				}
				else {

					ItemType[] types = {ItemType.REPORT_TEMPLATE};
					final WorkspaceExplorerSelectDialog wpTreepopup = new WorkspaceExplorerSelectDialog("Pick the item you want to import from", Arrays.asList(types), Arrays.asList(types));

					WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {

						@Override
						public void onSelectedItem(Item item) {
							int left = controller.getHeader().getMainLayout().getAbsoluteLeft() + 50;
							int top = controller.getHeader().getMainLayout().getAbsoluteTop() + 25;
							ImporterDialog dlg = new ImporterDialog(item, controller);
							dlg.setPopupPosition(left, top);
							dlg.setAnimationEnabled(true);
							dlg.show();				
							wpTreepopup.hide();
						}
						@Override
						public void onFailed(Throwable throwable) {
							Window.alert("There are networks problem, please check your connection.");            
						}				 
						@Override
						public void onAborted() {}
						@Override
						public void onNotValidSelection() {				
						}
					};
					wpTreepopup.addWorkspaceExplorerSelectNotificationListener(listener);
					wpTreepopup.show();					
				}
			}	
		};

		saveTemplate = new Command() {			
			public void execute() {
				if (TGenConstants.isDeployed) {
					GWT.log("saveTemplate");
					if (! controller.getModel().getTemplateName().equals(TemplateModel.DEFAULT_NAME)) {
						controller.saveTemplate(null);
					}
					else {
						showSaveTemplateDialog();
					}
				}
				else
					controller.saveTemplate("PINO");
			}
		};

		saveTemplateAs = new Command() {			
			public void execute() {
				if (TGenConstants.isDeployed) {
					GWT.log("saveTemplateAs");
					showSaveTemplateDialog();
				}
				else
					controller.saveTemplate("PINO");
			}
		};

	} //end constructor


	/**
	 * Inner class for save popup
	 * @author 
	 */
	protected class SaveReportPopUp extends DialogBox {
		private TextBox templNameTextBox = new TextBox();
		private Button saveButton = new Button("Save");


		public SaveReportPopUp(final String basketidToSaveIn, boolean autoHide, String currTemplateName) {

			super(autoHide);

			// Create a panel to hold all of the form widgets.
			VerticalPanel panel = new VerticalPanel();
			Label theLabel = null;
			if (currTemplateName.compareTo("") == 0) {
				this.setText("Save As ...");
				theLabel = new Label("New Template name");
			}
			else {
				this.setText("Save");
				theLabel = new Label("Current Template name");
			}
			panel.add(theLabel);
			panel.setSpacing(4);
			templNameTextBox.setMaxLength(27);	
			templNameTextBox.setSize("180", "24");
			templNameTextBox.setText(currTemplateName);

			panel.add(templNameTextBox);

			HorizontalPanel buttonsPanel = new HorizontalPanel();
			HorizontalPanel buttonsContainer = new HorizontalPanel();
			buttonsPanel.setWidth("100%");
			buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			buttonsContainer.setSpacing(8);
			buttonsPanel.add(buttonsContainer);
			buttonsContainer.add(new Button("Cancel", new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
				}
			}
			));

			buttonsContainer.add(saveButton);
			panel.add(buttonsPanel);
			panel.setPixelSize(220, 120);
			setWidget(panel);

			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					//checking user input
					String inputUser = templNameTextBox.getText();
					if (inputUser.compareTo(templNameTextBox.getText().replaceAll(CommonConstants.ACCEPTED_CHARS_REG_EX, "")) != 0) {
						Window.alert("Template name contains illegal characters detected, System will remove them");
						templNameTextBox.setText(templNameTextBox.getText().replaceAll(CommonConstants.ACCEPTED_CHARS_REG_EX, ""));
					}
					else if (inputUser.compareTo("") == 0) {
						Window.alert("Template Name cannot be empty");
					}
					else if (inputUser.compareTo(TemplateModel.DEFAULT_NAME) == 0) {
						Window.alert("Please choose a different name, " + TemplateModel.DEFAULT_NAME + " is the default one");
						templNameTextBox.selectAll();
						templNameTextBox.setFocus(true);
					}
					else {
						String newTemplateName = inputUser;
						if (controller.getModel().getTemplateName().compareTo(newTemplateName) != 0) {
							newTemplateName = newTemplateName.trim();
							controller.getModel().setTemplateName(newTemplateName);
						}
						controller.changeTemplateName(newTemplateName);
						controller.saveTemplate(basketidToSaveIn);
						hide();
					}

				}
			});
		}
		/*
		 * selectAll method works only when the widget is attacched to the DOM,
		 * indeed I neede to use this timer 
		 */ 

		Timer t = new Timer() {
			@Override
			public void run() {
				templNameTextBox.selectAll();
			}
		};

		protected void setFocus() {
			templNameTextBox.setFocus(true);
			t.schedule(300);
		}
	} //end inner class
}
