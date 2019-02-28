package org.gcube.portlets.user.reportgenerator.client;


import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.user.reportgenerator.client.Presenter.CommonCommands;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.dialog.ImporterDialog;
import org.gcube.portlets.user.reportgenerator.client.dialog.PagePropertiesDialog;
import org.gcube.portlets.user.reportgenerator.client.model.ExportManifestationType;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateModel;
import org.gcube.portlets.user.reportgenerator.shared.VMETypeIdentifier;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.RichTextArea;



/**
 * <code> Headerbar </code> class is the top bar component of the UI 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class Headerbar extends Composite{

	private static final String ADD_BIBLIO_ENTRY = "Add citation";
	private static final String VIEW_BIBLIO = "View Bibliography";
	private static final String MANAGE_BIBLIO = "Delete citation(s)";
	private static final String IMPORT_VME = "Edit VME";
	//	private static final String EXPORT_ENCRYPTED_REPORT = "Save an Encrypted Version to Desktop";

	private static final String VIEW_USER_COMMENTS = "View user comments";

	String location;

	private Presenter presenter; 

	/**
	 * the template Model
	 */
	private TemplateModel templateModel;

	/**
	 * mainLayout Panel
	 */
	private CellPanel mainLayout = new HorizontalPanel();


	//private MenuItem optionPDF;

	private MenuItem importModel;

	private MenuItem addBiblioEntry;

	private MenuItem viewBiblio;

	private MenuItem manageBiblio;

	private MenuItem optionHTML;

	private MenuItem optionDOCX;

	private MenuItem optionPDF;

	private MenuItem optionFimes;

	private MenuItem optionXML;

	private MenuItem optionEncryptedModel;

	private MenuItem viewMetadata;

	private MenuItem viewComments;

	private MenuItem discardSection;

	private MenuItem importVME;

	private MenuItem importVMERef;



	MenuBar menuBar = new MenuBar();

	MenuItem fileMenu;
	MenuItem viewMenu;
	MenuItem sectionsMenu;
	MenuItem biblioMenu;
	MenuItem exportMenu;
	MenuItem vmeMenu;
	MenuItemSeparator separator1;
	MenuItemSeparator separator2;
	MenuItemSeparator separator3;
	MenuItemSeparator separator4;

	/**
	 * Constructor
	 * @param c the controller instance for this UI component
	 */
	public Headerbar(Presenter c) {
		this.presenter = c;
		this.templateModel = presenter.getModel();

		menuBar.setAutoOpen(false);
		//	menuBar.setWidth("100px");
		menuBar.setAnimationEnabled(true);
		fileMenu = getFileMenu();
		menuBar.addItem(fileMenu);
		separator1 = menuBar.addSeparator();
		viewMenu = getViewMenu();
		menuBar.addItem(viewMenu);
		separator2 = menuBar.addSeparator();
		sectionsMenu = getSectionMenu();
		menuBar.addItem(sectionsMenu);
		separator3 = menuBar.addSeparator();
		biblioMenu = getBiblioMenu();
		menuBar.addItem(biblioMenu);

		mainLayout.setSize("100%", "24px");
		mainLayout.setStyleName("menubar");

		mainLayout.add(menuBar);

		//design the part for the template name and the pages handling

		HorizontalPanel captionPanel = new HorizontalPanel();
		captionPanel.setWidth("100%");

		HorizontalPanel pageHandlerPanel = new HorizontalPanel();
		pageHandlerPanel.setHeight("24px");
		pageHandlerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		captionPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainLayout.add(captionPanel);
		mainLayout.add(pageHandlerPanel);
		mainLayout.setCellHorizontalAlignment(menuBar, HasHorizontalAlignment.ALIGN_LEFT);
		mainLayout.setCellHorizontalAlignment(captionPanel, HasHorizontalAlignment.ALIGN_LEFT);
		mainLayout.setCellWidth(menuBar, "200px");
//		mainLayout.setCellWidth(pageHandlerPanel, "200");
		initWidget(mainLayout);
	}


	public void setMenuForVME() {
		menuBar.removeItem(viewMenu);
		menuBar.removeItem(sectionsMenu);
		menuBar.removeItem(biblioMenu);
		menuBar.removeSeparator(separator1);
		menuBar.removeSeparator(separator2);
		menuBar.removeSeparator(separator3);
		menuBar.removeItem(exportMenu);

		vmeMenu = getVMEMenuEdit();
		menuBar.addItem(vmeMenu);

		menuBar.addSeparator();	

		MenuItem vmeMenuCreate = getVMEMenuCreate();
		menuBar.addItem(vmeMenuCreate);

		separator4 = menuBar.addSeparator();

		MenuItem vmeMenuDelete = getVMEMenuDelete();
		menuBar.addItem(vmeMenuDelete);

	}

	public void setMenuForWorkflowDocument(boolean canUpdate) {
		presenter.setMenuForWorkflowDocument(true);
		menuBar.removeItem(fileMenu);
		menuBar.removeItem(viewMenu);
		menuBar.removeItem(sectionsMenu);
		menuBar.removeItem(biblioMenu);
		menuBar.removeSeparator(separator1);
		menuBar.removeSeparator(separator2);
		menuBar.removeItem(exportMenu);

		MenuBar workflowMenu = new MenuBar(true);
		workflowMenu.setAnimationEnabled(true);

		separator1 = menuBar.addSeparator();
		viewMenu = getViewMenu();
		menuBar.addItem(viewMenu);
		separator2 = menuBar.addSeparator();
		sectionsMenu = getSectionMenu();
		menuBar.addItem(sectionsMenu);
		separator3 = menuBar.addSeparator();
		biblioMenu = getBiblioMenu();
		menuBar.addItem(biblioMenu);

		ReportGenerator.get().getToolbarPanel().clear();
		ReportGenerator.get().getToolbarPanel().add(new HTML("&nbsp;&nbsp;", true));
	}
	/**
	 * Redirect to VRE Deployer Portlet
	 */
	private void loadWorkflowLibraryApp(){
		getUrl();
		location += "/../my-workflow-documents";
		Window.open(location, "_self", "");		
	}
	/**
	 * Get URL from browser
	 */
	public native void getUrl()/*-{
			this.@org.gcube.portlets.user.reportgenerator.client.Headerbar::location = $wnd.location.href;
	}-*/;

	/**
	 * temporary command 
	 * @return the command instance
	 */
	public Command getNullCommand() {
		Command openNothing = new Command() {	

			public void execute() {
			}
		};
		return openNothing;
	}

	public Command getDisabledExportMenuItemCmd() {

		Command disabledExportMenuItemCmd = new Command() {	
			public void execute() {

				MessageBox.alert("Alert",
						"Export is disabled for templates, please save this template as a Report and retry." +
								" If you just saved this Template as a Report please save it and reopen the Report", null);
			}
		};
		return disabledExportMenuItemCmd;
	}
	/**
	 * temporary command 
	 * @return the command instance
	 */
	public Command getBiblioCommand() {
		Command openNothing = new Command() {	
			public void execute() {	
				MessageBox.alert("Warning ","A textarea must be selected to add an entry", null);
			}
		};
		return openNothing;
	}


	Command addCitationCmd = new Command() {	
		public void execute() {
			presenter.openAddCitationDialog();
		}
	};

	Command viewBiblioCmd = new Command() {	
		public void execute() {
			if (presenter.hasBibliography()) {
				presenter.seekLastPage();
			}
			else {
				MessageBox.alert("Warning", "No bibliography found, to add bibliography start adding citations:<br />Bibliography > Add citation (from within a text area)", null);
			}
		}
	};

	Command manageBiblioCmd = new Command() {	
		public void execute() {
			if (presenter.hasBibliography()) {
				presenter.openManageCitationsDialog();
			}
			else {
				MessageBox.alert("Warning", "No bibliography found, to add bibliography start adding citations:<br />Bibliography > Add citation (from within a text area)", null);
			}
		}
	};

	private MenuItem getVMEMenuDelete() {
		MenuBar createMenu = new MenuBar(true);

		createMenu.setAnimationEnabled(true);
		MenuItem toReturn = new MenuItem("Delete VME-DB", createMenu);
		toReturn.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);

		MenuItem item = new MenuItem("Delete VME", true, new Command() {			
			public void execute() {				
				presenter.showVMEDeleteDialog(VMETypeIdentifier.Vme);
			}
		});		
		createMenu.addItem(item);
		createMenu.addSeparator();

		item = new MenuItem("Delete VME General Measure", true, new Command() {			
			public void execute() {				
				presenter.showVMEDeleteDialog(VMETypeIdentifier.GeneralMeasure);
			}
		});		
		createMenu.addItem(item);
		item = new MenuItem("Delete Information Source", true, new Command() {			
			public void execute() {				
				presenter.showVMEDeleteDialog(VMETypeIdentifier.InformationSource);
			}
		});		
		createMenu.addItem(item);
		item = new MenuItem("Delete Bottom fishing areas", true, new Command() {			
			public void execute() {				
				presenter.showVMEDeleteDialog(VMETypeIdentifier.FisheryAreasHistory);
			}
		});		
		createMenu.addItem(item);
		item = new MenuItem("Delete Regional overview of VMEs", true,  new Command() {			
			public void execute() {				
				presenter.showVMEDeleteDialog(VMETypeIdentifier.VMEsHistory);
			}
		});		
		createMenu.addItem(item);
		return toReturn;
	}

	private MenuItem getVMEMenuEdit() {
		MenuBar importMenu = new MenuBar(true);

		importMenu.setAnimationEnabled(true);
		MenuItem toReturn = new MenuItem("Edit VME-DB", importMenu);
		toReturn.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);

		Command importVMEReport = new Command() {			
			public void execute() {				
				presenter.showVMEImportDialog();
			}
		};

		importVME = new MenuItem(IMPORT_VME, true, importVMEReport);

		importMenu.addItem(importVME);
		importMenu.addSeparator();

		MenuItem item = new MenuItem("Edit VME General Measures", true, new Command() {			
			public void execute() {				
				presenter.showVMEReportRefImportDialog(VMETypeIdentifier.GeneralMeasure);
			}
		});		
		importMenu.addItem(item);

		item = new MenuItem("Edit Information Sources", true, new Command() {			
			public void execute() {				
				presenter.showVMEReportRefImportDialog(VMETypeIdentifier.InformationSource);
			}
		});		
		importMenu.addItem(item);

		item = new MenuItem("Edit Bottom fishing areas", true, new Command() {			
			public void execute() {				
				presenter.showVMEReportRefImportDialog(VMETypeIdentifier.FisheryAreasHistory);
			}
		});		
		importMenu.addItem(item);

		item = new MenuItem("Edit Regional overview of VMEs", true, new Command() {			
			public void execute() {				
				presenter.showVMEReportRefImportDialog(VMETypeIdentifier.VMEsHistory);
			}
		});		
		importMenu.addItem(item);

		//		item = new MenuItem("Edit Rfmo", true, new Command() {			
		//			public void execute() {				
		//				presenter.showVMEReportRefImportDialog(VMETypeIdentifier.Rfmo);
		//			}
		//		});		
		//		importMenu.addItem(item);

		//		
		//		importMenu.addSeparator();
		//		MenuItem exportVME = new MenuItem("Export Current to VME-DB", true, importVMEReport);
		//		importMenu.addItem(exportVME);

		return toReturn;
	}


	private MenuItem getVMEMenuCreate() {
		MenuBar importMenu = new MenuBar(true);

		importMenu.setAnimationEnabled(true);
		MenuItem toReturn = new MenuItem("Create VME-DB", importMenu);
		toReturn.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);

		MenuItem item = new MenuItem("Create new VME", true, new Command() {			
			public void execute() {				
				presenter.importVMETemplate(VMETypeIdentifier.Vme);
			}
		});		
		importMenu.addItem(item);
		importMenu.addSeparator();

		item = new MenuItem("Create new General Measure", true, new Command() {			
			public void execute() {				
				presenter.importVMETemplate(VMETypeIdentifier.GeneralMeasure);
			}
		});		
		importMenu.addItem(item);

		item = new MenuItem("Create new Information Source", true, new Command() {			
			public void execute() {				
				presenter.importVMETemplate(VMETypeIdentifier.InformationSource);
			}
		});		
		importMenu.addItem(item);

		item = new MenuItem("Create new Bottom fishing areas", true, new Command() {			
			public void execute() {				
				presenter.importVMETemplate(VMETypeIdentifier.FisheryAreasHistory);
			}
		});		
		importMenu.addItem(item);

		item = new MenuItem("Create new Regional overview of VMEs", true, new Command() {			
			public void execute() {				
				presenter.importVMETemplate(VMETypeIdentifier.VMEsHistory);
			}
		});		
		importMenu.addItem(item);
		return toReturn;
	}

	/**
	 * 
	 * @return
	 */
	private MenuItem getBiblioMenu() {
		//		Create the Options menu
		MenuBar biblioMenu = new MenuBar(true);
		MenuItem toReturn = new MenuItem("Bibliography", biblioMenu);
		biblioMenu.setAnimationEnabled(true);
		addBiblioEntry = new MenuItem("<font color=\"gray\">"+ ADD_BIBLIO_ENTRY +"</font>", true, getBiblioCommand());
		biblioMenu.addItem(addBiblioEntry);
		viewBiblio = new MenuItem(VIEW_BIBLIO, true, viewBiblioCmd);
		manageBiblio = new MenuItem(MANAGE_BIBLIO, true, manageBiblioCmd);
		biblioMenu.addSeparator();
		biblioMenu.addItem(manageBiblio);
		biblioMenu.addItem(viewBiblio);		
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
		insertsMenu.setAnimationEnabled(true);
		importModel = new MenuItem("<font color=\"gray\">Import from Template or Report</font>", true, getNullCommand());
		insertsMenu.addItem(importModel);
		viewMetadata = new MenuItem("<font color=\"gray\">View Metadata</font>", true, getNullCommand());
		discardSection = new MenuItem("<font color=\"gray\">Discard current</font>", true, getNullCommand());
		viewComments = new MenuItem("<font color=\"gray\">"+ VIEW_USER_COMMENTS+"</font>", true, getNullCommand());
		insertsMenu.addItem(discardSection);
		insertsMenu.addSeparator();
		insertsMenu.addItem(viewMetadata);
		insertsMenu.addItem(viewComments);
		return toReturn;
	}

	private MenuItem getViewMenu() {
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

		Command showReportStructure = new Command() {			
			public void execute() {				
				presenter.toggleReportStructure();
			}
		};



		//		Create the Options menu
		MenuBar optionsMenu = new MenuBar(true);

		optionsMenu.setAnimationEnabled(true);
		MenuItem toReturn = new MenuItem("View", optionsMenu);

		optionsMenu.addItem("View Properties", openPageProperties);
		optionsMenu.addItem("View/Hide Structure", showReportStructure);
		optionsMenu.addSeparator();

		return toReturn;
	}



	/**
	 * rewrite with setHTML to remove the gray color
	 * @param d4sArea 
	 */
	public void enableBiblioEntry(RichTextArea d4sArea) {
		addBiblioEntry.setHTML(ADD_BIBLIO_ENTRY);
		addBiblioEntry.setScheduledCommand(addBiblioEntryCommand);
		presenter.setAreaForBiblio(d4sArea);
	}

	/**
	 * build the File Menu
	 * @return
	 */

	private MenuItem getFileMenu() {
		CommonCommands cmd = new CommonCommands(presenter);


		Command openHelp = new Command() {

			public void execute() {
				if (! ReportConstants.isDeployed) {
					presenter.openTemplate("", "", true);
				}
				else {
					String url = "https://gcube.wiki.gcube-system.org/gcube/index.php/Common_Functionality#Report_Management";
					int width = Window.getClientWidth();
					int height = Window.getClientHeight();
					int winWidth = (int) (Window.getClientWidth() * 0.8);
					int winHeight = (int) (Window.getClientHeight() * 0.7);
					int left = (width - winWidth) / 2;
					int top = (height - winHeight) / 2;
					Window.open(url, null,"left=" + left + "top" + top + ", width=" + winWidth + ", height=" + winHeight + ", resizable=yes, scrollbars=yes, status=yes");			
				}

			}

		};



		Command saveReportAs = new Command() {			
			public void execute() {
				CommonCommands cmd = new CommonCommands(presenter);
				cmd.saveReportAsDialog();
			}			
		};



		Command saveReport = new Command() {			
			public void execute() {
				if (templateModel.getTemplateName().endsWith("d4sR"))
					presenter.saveReport();
				else {
					CommonCommands cmd = new CommonCommands(presenter);
					cmd.saveReportAsDialog();
				}
			}
		};




		//		Create the file menu
		MenuBar fileMenu = new MenuBar(true);
		fileMenu.setAnimationEnabled(true);

		MenuItem toReturn = new MenuItem("File", fileMenu);
		fileMenu.addItem("Open Report from Workspace", cmd.openReport);
		//fileMenu.addItem("Open template...", cmd.openTemplate);
		fileMenu.addSeparator();		
		fileMenu.addItem("Save", saveReport);
		fileMenu.addItem("Save As ..", saveReportAs);
		fileMenu.addSeparator();
		//		fileMenu.addItem("Import from FiMES XML", importFimes);
		fileMenu.addItem("Close Report", cmd.newTemplate);
		fileMenu.addSeparator();
		//		fileMenu.addItem("? Open User's Guide", openHelp);		
		return toReturn;
	}

	/**
	 * 
	 * @param model .
	 */
	public void setModel(TemplateModel model ) {
		this.templateModel = model;
	}

	//************** COMMANDS ********************************////

	ScheduledCommand generateFimes = new ScheduledCommand() {	
		public void execute() {
			presenter.generateFiMES(templateModel);
		}
	};

	ScheduledCommand generateEncryptedModel = new ScheduledCommand() {		
		@Override
		public void execute() {					
			GWT.runAsync(WorkspaceExplorerSelectDialog.class, new RunAsyncCallback() {
				public void onSuccess() {

					ItemType[] types = {ItemType.REPORT_TEMPLATE, ItemType.REPORT};

					final WorkspaceExplorerSelectDialog wpTreepopup = new WorkspaceExplorerSelectDialog("Select the Report (or Template) to encrypt", Arrays.asList(types), Arrays.asList(types));

					WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {

						@Override
						public void onSelectedItem(Item item) {
							final String itemId = item.getId();
							final String url = GWT.getModuleBaseURL() + "downloadEncryptedReport?itemId=" + itemId; 
							Window.open(url, "_blank", "");			
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

				public void onFailure(Throwable reason) {
					Window.alert("There are networks problem, please check your connection.");              
				}
			});
		}
	};



	//************** COMMANDS ********************************////
	Command addBiblioEntryCommand = new Command() {	
		public void execute() {
			GWT.runAsync(new RunAsyncCallback() {

				@Override
				public void onSuccess() {
					presenter.openAddCitationDialog();					
				}

				@Override
				public void onFailure(Throwable reason) {		
				}
			});

		}
	};

	/**
	 * 
	 */
	Command openMetadata =  new Command() {			
		public void execute() {	
			GWT.runAsync(new RunAsyncCallback() {

				@Override
				public void onSuccess() {
					int left = mainLayout.getAbsoluteLeft() + 50;
					int top = mainLayout.getAbsoluteTop() + 25;
					GCubeDialog dlg = new GCubeDialog(true);
					dlg.setText("Report Metadata:");
					int pageNo = presenter.getModel().getCurrentPage();
					List<Metadata> metadatas =  presenter.getModel().getSection(pageNo).getAllMetadata();
					int nRows = metadatas.size();
					Grid metadataGrid = new Grid(nRows, 2);
					int i = 0;
					for (Metadata md : metadatas) {
						metadataGrid.setWidget(i, 0, new HTML("<b>" + md.getAttribute() + ": </b>"));
						metadataGrid.setWidget(i, 1, new HTML(md.getValue()));
						i++;
					}

					dlg.setWidget(metadataGrid);
					dlg.setPopupPosition(left, top);
					dlg.setAnimationEnabled(true);
					dlg.show();					
				}

				@Override
				public void onFailure(Throwable reason) {}
			});


		}
	};

	Command importModelOrReport = new Command() {			
		public void execute() {
			if (! ReportConstants.isDeployed) {
				int left = mainLayout.getAbsoluteLeft() + 50;
				int top = mainLayout.getAbsoluteTop() + 25;
				ImporterDialog dlg = new ImporterDialog(null, presenter);
				dlg.setPopupPosition(left, top);
				dlg.setAnimationEnabled(true);
				dlg.show();
			}
			else {
				GWT.runAsync(new RunAsyncCallback() {
					@Override
					public void onSuccess() {
						ItemType[] types = {ItemType.REPORT_TEMPLATE, ItemType.REPORT};
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

				@Override
				public void onFailure(Throwable reason) {

				}
			});

		}
	}	
};
/**
 * 
 * @return .
 */
public CellPanel getMainLayout() {
	return mainLayout;
}
}
