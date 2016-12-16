package org.gcube.portlets.user.templates.client;


import java.util.Arrays;

import org.gcube.portlets.d4sreporting.common.client.CommonConstants;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.user.templates.client.dialogs.ImporterDialog;
import org.gcube.portlets.user.templates.client.dialogs.PagePropertiesDialog;
import org.gcube.portlets.user.templates.client.model.TemplateModel;
import org.gcube.portlets.user.templates.client.presenter.CommonCommands;
import org.gcube.portlets.user.templates.client.presenter.Presenter;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * <code> HeaderBar </code> class is the top bar component of the UI 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * 
 * @version March 2013 
 */
public class HeaderBar extends Composite{


	private Presenter presenter;

	/**
	 * the template Model
	 */
	private TemplateModel templateModel;
	

	/**
	 * mainLayout Panel
	 */
	private CellPanel mainLayout = new HorizontalPanel();

	/**
	 *  contains the current template name
	 */
	private HTML templateNameBox = new HTML();

	private Command openTemplate;
	private CommonCommands commonCommands;
	private Command newTemplate;
	/**
	 * Constructor
	 * @param c the controller instance for this UI component
	 */
	public HeaderBar(Presenter c) {
		this.presenter = c;
		this.templateModel = presenter.getModel();
		commonCommands = new CommonCommands(presenter);
		// Create the File menu bar
		MenuBar menuBar = new MenuBar();
		menuBar.setAutoOpen(false);
		//menuBar.setWidth("100px");
		menuBar.setAnimationEnabled(false);
		menuBar.addItem(getFileMenu());

//		menuBar.addSeparator();
//		menuBar.addItem(getInsertMenu());
		menuBar.addSeparator();
		menuBar.addItem(getOptionsMenu());
		menuBar.addSeparator();
		menuBar.addItem(getTableMenu());
		menuBar.addSeparator();
		menuBar.addItem(getSectionMenu());


		mainLayout.setSize("100%", "24px");
		mainLayout.setStyleName("menubar");

		mainLayout.add(menuBar);

		//design the part for the template name and the pages handling

		HorizontalPanel captionPanel = new HorizontalPanel();
		captionPanel.setWidth("100%");



		HorizontalPanel pageHandlerPanel = new HorizontalPanel();
		pageHandlerPanel.setHeight("24");
		pageHandlerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		pageHandlerPanel.setWidth("100%");

		captionPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		captionPanel.add(templateNameBox);
		mainLayout.add(captionPanel);
		mainLayout.add(pageHandlerPanel);
		mainLayout.setCellHorizontalAlignment(menuBar, HasHorizontalAlignment.ALIGN_LEFT);
		mainLayout.setCellHorizontalAlignment(captionPanel, HasHorizontalAlignment.ALIGN_LEFT);
		mainLayout.setCellWidth(menuBar, "200");
		mainLayout.setCellWidth(pageHandlerPanel, "200");
		initWidget(mainLayout);

	}

	/**
	 * temporary command 
	 * @return the command instance
	 */
	public Command getNullCommand() {
		Command openNothing = new Command() {	

			public void execute() {
				Window.alert("Feature not supported yet");				
			}
		};

		return openNothing;
	}


	

	/**
	 * build the File Menu
	 * @return
	 */

	private MenuItem getFileMenu() {

		newTemplate = new Command() {

			public void execute() {
				presenter.getModel().resetModelInSession();
			}
		};

		openTemplate = new Command() {	

			public void execute() {
				
				ItemType[] types = {ItemType.REPORT_TEMPLATE};
				final WorkspaceExplorerSelectDialog wpTreepopup = new WorkspaceExplorerSelectDialog("Select a Template to open", Arrays.asList(types), Arrays.asList(types));

				WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {

					@Override
					public void onSelectedItem(Item item) {
						presenter.openTemplate(item.getName(), item.getId());	
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


		Command saveTemplate = commonCommands.saveTemplate;

		Command saveTemplateAs = commonCommands.saveTemplateAs;


		//		Create the file menu
		MenuBar fileMenu = new MenuBar(true);
		fileMenu.setAnimationEnabled(true);

		MenuItem toReturn = new MenuItem("File", fileMenu);

		fileMenu.addItem("New empty template", newTemplate);
		fileMenu.addSeparator();
		fileMenu.addItem("Open template...", openTemplate);
		fileMenu.addSeparator();
		fileMenu.addItem("Save ", saveTemplate);
		fileMenu.addItem("Save As...", saveTemplateAs);

		return toReturn;
	}

	/**
	 * build the Options Menu
	 * @return
	 */

	private MenuItem getOptionsMenu() {
		Command openPageProperties = new Command() {	
			public void execute() {
				int left = mainLayout.getAbsoluteLeft() + 50;
				int top = mainLayout.getAbsoluteTop() + 25;
				PagePropertiesDialog dlg = new PagePropertiesDialog(templateModel, presenter);
				dlg.setPopupPosition(left, top);
				dlg.setAnimationEnabled(true);
				dlg.show();
			}
		};
		Command showToolbox = new Command() {	
			public void execute() {
				presenter.showToolbox();
			}
		};


		//		Create the Options menu
		MenuBar optionsMenu = new MenuBar(true);

		optionsMenu.setAnimationEnabled(true);
		MenuItem toReturn = new MenuItem("Options", optionsMenu);
		optionsMenu.addItem("Template Properties ", openPageProperties);
		optionsMenu.addItem("Show Toolbox Panel", showToolbox);
		optionsMenu.addSeparator();
		optionsMenu.addItem("Help", new Command() {			
			public void execute() {	
				if (!TGenConstants.isDeployed) {
					presenter.openTemplate("", "");
				}
				else {
					String url = "https://gcube.wiki.gcube-system.org/gcube/index.php/Common_Functionality#Template_Creation";
					int width = Window.getClientWidth();
					int height = Window.getClientHeight();
					int winWidth = (int) (Window.getClientWidth() * 0.8);
					int winHeight = (int) (Window.getClientHeight() * 0.7);
					int left = (width - winWidth) / 2;
					int top = (height - winHeight) / 2;
					Window.open(url, null,"left=" + left + "top" + top + ", width=" + winWidth + ", height=" + winHeight + ", resizable=yes, scrollbars=yes, status=yes");			
				}
			}
		});

		return toReturn;
	}


	/**
	 * 
	 * @return
	 */
	public MenuItem getInsertMenu() {
		//		Create the Options menu
		MenuBar insertsMenu = new MenuBar(true);

		insertsMenu.setAnimationEnabled(true);
		MenuItem toReturn = new MenuItem("Insert", insertsMenu);
		insertsMenu.addItem("Single Column...", getSingleColumnMenu());
		//insertsMenu.addItem("Double Column...", getDoubleColumnMenu());
		insertsMenu.addSeparator();
		insertsMenu.addItem("Table Of Contents",  new Command() {			
			public void execute() {	
				presenter.insertPlaceHolder(ComponentType.TOC, TemplateModel.TEMPLATE_WIDTH - 50, 50);
			}
		});
		return toReturn;
	}

	/**
	 * 
	 * @return
	 */
	private MenuItem getSectionMenu() {
		//		Create the Options menu
		MenuBar insertsMenu = new MenuBar(true);
		MenuItem toReturn = new MenuItem("Section", insertsMenu);


		insertsMenu.addItem("Add New", new Command() {			
			public void execute() {	presenter.addNewPage(); }
		});

		insertsMenu.addItem("Discard Current", new Command() {			
			public void execute() {	presenter.discardCurrentSection(); }
		});

		insertsMenu.addItem("Import from Template", importTemplateCommand);
		
		return toReturn;
	}

	/**
	 * 
	 * @return
	 */
	private MenuItem getTableMenu() {
		//		Create the Options menu
		MenuBar insertsMenu = new MenuBar(true);
		MenuItem toReturn = new MenuItem("Table", insertsMenu);


		insertsMenu.addItem("Add Table", new Command() {			
			public void execute() {	
				presenter.showTablePopup(ComponentType.FLEX_TABLE, TemplateModel.TEMPLATE_WIDTH - 50, 50);
			}
		});
		insertsMenu.addSeparator();
		insertsMenu.addItem("Add Time Series spot",  new Command() {			
			public void execute() {	
				presenter.insertPlaceHolder(ComponentType.TIME_SERIES, TemplateModel.TEMPLATE_WIDTH - 50, 60);
			}
		});
		return toReturn;
	}
	/**
	 * 
	 * @return
	 */
	private MenuBar getSingleColumnMenu() {


		//		Create the Options menu
		MenuBar insertsMenu = new MenuBar(true);

		insertsMenu.setAnimationEnabled(true);
		insertsMenu.addItem("Title", new Command() {			
			public void execute() {	
				presenter.insertStaticTextArea(ComponentType.TITLE, TemplateModel.TEMPLATE_WIDTH - 50, 40);
			}
		});
		insertsMenu.addItem("Heading 1", new Command() {			
			public void execute() {	
				presenter.insertStaticTextArea(ComponentType.HEADING_1, TemplateModel.TEMPLATE_WIDTH - 50, 35);
			}
		});
		insertsMenu.addItem("Heading 2", new Command() {			
			public void execute() {	
				presenter.insertStaticTextArea(ComponentType.HEADING_2, TemplateModel.TEMPLATE_WIDTH - 50, 35);
			}
		});
		insertsMenu.addItem("Heading 3", new Command() {			
			public void execute() {	
				presenter.insertStaticTextArea(ComponentType.HEADING_3, TemplateModel.TEMPLATE_WIDTH - 50, 35);
			}
		});
		insertsMenu.addSeparator();
		insertsMenu.addItem("Image", new Command() {			
			public void execute() {	
				presenter.insertDroppingArea(ComponentType.DYNA_IMAGE, TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT);
			}
		});
		insertsMenu.addItem("Text",  new Command() {			
			public void execute() {	
				presenter.insertStaticTextArea(ComponentType.BODY, TemplateModel.TEMPLATE_WIDTH - 50, 75);
			}
		});
		return insertsMenu;
	}

	private Command importTemplateCommand = new Command() {			
		public void execute() {
			if (! TGenConstants.isDeployed) {
				int left = mainLayout.getAbsoluteLeft() + 50;
				int top = mainLayout.getAbsoluteTop() + 25;
				ImporterDialog dlg = new ImporterDialog(null, presenter);
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
						int left = mainLayout.getAbsoluteLeft() + 50;
						int top = mainLayout.getAbsoluteTop() + 25;
						ImporterDialog dlg = new ImporterDialog(item, presenter);
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


	public Command getImportTemplateCommand() {
		return importTemplateCommand;
	}

	/**
	 * changes the template name label in the UI
	 * @param name .
	 */
	public void setTemplateName(String name) {
		templateNameBox.setHTML("&nbsp;&nbsp;" + name);
	}

	/**
	 * Shows the previous botton in the UI
	 */

	/**
	 * 
	 * @param model .
	 */
	public void setModel(TemplateModel model ) {
		this.templateModel = model;
	}

	/**
	 * 
	 * @return .
	 */
	public Command getOpenTemplate() {
		return openTemplate;
	}

	/**
	 * 
	 * @return .
	 */
	public Command getNewTemplate() {
		return newTemplate;
	}

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
				}}));

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
						if (templateModel.getTemplateName().compareTo(newTemplateName) != 0) {
							newTemplateName = newTemplateName.trim();
							templateModel.setTemplateName(newTemplateName);
						}
						presenter.changeTemplateName(newTemplateName);
						presenter.saveTemplate(basketidToSaveIn);
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

	public CellPanel getMainLayout() {
		return mainLayout;
	}
}
