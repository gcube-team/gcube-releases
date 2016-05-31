package org.gcube.portlets.user.reportgenerator.client.Presenter;

import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.BasicSection;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.d4sreporting.common.shared.ReportReferences;
import org.gcube.portlets.d4sreporting.common.shared.Tuple;
import org.gcube.portlets.user.reportgenerator.client.Headerbar;
import org.gcube.portlets.user.reportgenerator.client.ReportGenerator;
import org.gcube.portlets.user.reportgenerator.client.ReportService;
import org.gcube.portlets.user.reportgenerator.client.ReportServiceAsync;
import org.gcube.portlets.user.reportgenerator.client.ReportStructurePanel;
import org.gcube.portlets.user.reportgenerator.client.TitleBar;
import org.gcube.portlets.user.reportgenerator.client.ToolboxPanel;
import org.gcube.portlets.user.reportgenerator.client.WorkspacePanel;
import org.gcube.portlets.user.reportgenerator.client.dialog.AddBiblioEntryDialog;
import org.gcube.portlets.user.reportgenerator.client.dialog.DeleteCitationsDialog;
import org.gcube.portlets.user.reportgenerator.client.dialog.SelectVMEReportDialog;
import org.gcube.portlets.user.reportgenerator.client.dialog.SelectVMEReportDialog.Action;
import org.gcube.portlets.user.reportgenerator.client.dialog.WaitingOperationDialog;
import org.gcube.portlets.user.reportgenerator.client.events.AddBiblioEvent;
import org.gcube.portlets.user.reportgenerator.client.events.AddBiblioEventHandler;
import org.gcube.portlets.user.reportgenerator.client.events.AddCommentEvent;
import org.gcube.portlets.user.reportgenerator.client.events.AddCommentEventHandler;
import org.gcube.portlets.user.reportgenerator.client.events.ItemSelectionEvent;
import org.gcube.portlets.user.reportgenerator.client.events.ItemSelectionEventHandler;
import org.gcube.portlets.user.reportgenerator.client.events.RemovedCitationEvent;
import org.gcube.portlets.user.reportgenerator.client.events.RemovedCitationEventHandler;
import org.gcube.portlets.user.reportgenerator.client.events.RemovedUserCommentEvent;
import org.gcube.portlets.user.reportgenerator.client.events.RemovedUserCommentEventHandler;
import org.gcube.portlets.user.reportgenerator.client.events.SelectedReportEvent;
import org.gcube.portlets.user.reportgenerator.client.events.SelectedReportEventHandler;
import org.gcube.portlets.user.reportgenerator.client.model.ExportManifestationType;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateComponent;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateModel;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateSection;
import org.gcube.portlets.user.reportgenerator.client.targets.AttributeMultiSelection;
import org.gcube.portlets.user.reportgenerator.client.targets.AttributeSingleSelection;
import org.gcube.portlets.user.reportgenerator.client.targets.BasicTextArea;
import org.gcube.portlets.user.reportgenerator.client.targets.ClientImage;
import org.gcube.portlets.user.reportgenerator.client.targets.ClientRepeatableSequence;
import org.gcube.portlets.user.reportgenerator.client.targets.ClientReportReference;
import org.gcube.portlets.user.reportgenerator.client.targets.ClientSequence;
import org.gcube.portlets.user.reportgenerator.client.targets.Coords;
import org.gcube.portlets.user.reportgenerator.client.targets.D4sRichTextarea;
import org.gcube.portlets.user.reportgenerator.client.targets.GenericTable;
import org.gcube.portlets.user.reportgenerator.client.targets.GroupingDelimiterArea;
import org.gcube.portlets.user.reportgenerator.client.targets.HeadingTextArea;
import org.gcube.portlets.user.reportgenerator.client.targets.ReportTextArea;
import org.gcube.portlets.user.reportgenerator.client.targets.TextTableImage;
import org.gcube.portlets.user.reportgenerator.client.toolbar.RichTextToolbar;
import org.gcube.portlets.user.reportgenerator.client.uibinder.ExportOptions;
import org.gcube.portlets.user.reportgenerator.shared.ReportImage;
import org.gcube.portlets.user.reportgenerator.shared.SessionInfo;
import org.gcube.portlets.user.reportgenerator.shared.UserBean;
import org.gcube.portlets.user.reportgenerator.shared.VMETypeIdentifier;
import org.gcube.portlets.user.reportgenerator.shared.VmeExportResponse;
import org.gcube.portlets.user.reportgenerator.shared.VmeResponseEntry;
import org.gcube.portlets.widgets.exporter.client.ReportExporterPopup;
import org.gcube.portlets.widgets.exporter.client.event.ExportingCompletedEvent;
import org.gcube.portlets.widgets.exporter.client.event.ExportingCompletedEventHandler;
import org.gcube.portlets.widgets.exporter.client.event.ReportExporterEvent;
import org.gcube.portlets.widgets.exporter.client.event.ReportExporterEventHandler;
import org.gcube.portlets.widgets.exporter.shared.TypeExporter;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadCompleteEvent;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadCompleteEventHandler;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadSelectedEvent;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadSelectedEventHandler;
import org.gcube.portlets.widgets.fileupload.client.view.UploadProgressDialog;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;



/**
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * 
 */

public class Presenter {
	private ReportServiceAsync reportService = (ReportServiceAsync) GWT.create(ReportService.class);
	/**
	 *  View part
	 */
	private WorkspacePanel wp;

	private Headerbar header;

	private ToolboxPanel toolBoxPanel;

	private TitleBar titleBar;

	private HorizontalPanel exportsPanel;

	private final WaitingOperationDialog dlg = new WaitingOperationDialog();

	private UploadProgressDialog uploadDlg;

	private UserBean currentUser;
	private String currentScope;
	private String currentReportsStoreGatewayURL;

	private ClientImage selectedImage;



	/**
	 *  Model
	 */
	private TemplateModel model;

	private String location;

	private int currFocus;
	/**
	 * this is needed to know on which client sequence the user is operating
	 */
	private ClientSequence clientSeqSelected = null;

	private boolean isShowingStructure = false;

	RichTextToolbar currentSelectedToolbar;

	RichTextArea areaForBiblio;

	/**
	 * 
	 */
	private CommonCommands commonCommands;

	private boolean menuForWorkflowDocument = false;

	private boolean isVME = false;
	/**
	 * by default is set to Type VME, because refs can only be imported and not opened from WorkSpace
	 */
	private VMETypeIdentifier currentVmeType = VMETypeIdentifier.Vme;
	/**
	 * eventbus events handler
	 */
	static HandlerManager eventBus = new HandlerManager(null);

	public static HandlerManager getEventBus() {
		return eventBus;
	}


	private void handleEvents() {
		/**
		 * get the uploaded selected file name before submitting it
		 */
		eventBus.addHandler(FileUploadSelectedEvent.TYPE, new FileUploadSelectedEventHandler() {
			@Override
			public void onFileSelected(FileUploadSelectedEvent event) {
				String fileName = event.getSelectedFileName();
				GWT.log("selected file name: " + fileName);
				if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") 
						|| fileName.endsWith(".gif") || fileName.endsWith(".bmp") || fileName.endsWith(".d4sR")) {
					try {
						uploadDlg.submitForm();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else {
					Window.alert("File type not allowed");
					uploadDlg.hide();
				}
			}
		});
		/**
		 * get the uploaded file result
		 */
		eventBus.addHandler(FileUploadCompleteEvent.TYPE, new FileUploadCompleteEventHandler() {

			@Override
			public void onUploadComplete(FileUploadCompleteEvent event) {

				//the filename and its path on server are returned to the client
				String fileName = event.getUploadedFileInfo().getFilename();
				String absolutePathOnServer = event.getUploadedFileInfo().getAbsolutePath();
				GWT.log(fileName + " uploaded on Server here: " + absolutePathOnServer);

				reportService.getUploadedImageUrlById(fileName, absolutePathOnServer, new AsyncCallback<ReportImage>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("An error occurred in the server: " + caught.getMessage());
					}
					@Override
					public void onSuccess(ReportImage result) {
						uploadDlg.showRegisteringResult(true); //or false if an error occurred				
						selectedImage.dropImage(result.getUrl(), result.getId(), result.getWidth(), result.getHeight());
					}
				});		
			}
		});

		eventBus.addHandler(SelectedReportEvent.TYPE, new SelectedReportEventHandler() {			
			@Override
			public void onReportSelected(SelectedReportEvent event) {
				switch (event.getAction()) {
				case SELECT:
					importVMEReport(event.getId(), event.getName(), event.getType());			
					break;
				case ASSOCIATE:
					try {
						associateVMEReportRef(event.getType(), event.getId());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case DELETE:
					deleteVMEReport(event.getId(), event.getName(), event.getType());	
					break;
				}				
			}			
		});

		eventBus.addHandler(ExportingCompletedEvent.TYPE, new ExportingCompletedEventHandler() {			
			@Override
			public void onExportFinished(ExportingCompletedEvent event) {
				int top = 0;
				ReportGenerator.get().getScrollerPanel().setVerticalScrollPosition(top);
				showExportSaveOptions(event.getFilePath(), event.getItemName(), event.getType());
			}
		});

		eventBus.addHandler(AddBiblioEvent.TYPE, new AddBiblioEventHandler() {
			public void onAddCitation(AddBiblioEvent event) {
				addCitation(event.getCitekey(), event.getCitetext());
				String keyToAdd = "&nbsp;(" + event.getCitekey() +")&nbsp;";
				String currHTML = areaForBiblio.getHTML();
				if (currHTML.endsWith("<br>"))
					currHTML = currHTML.substring(0, currHTML.length()-4);
				areaForBiblio.setHTML(currHTML+keyToAdd);
			}
		});

		eventBus.addHandler(RemovedCitationEvent.TYPE, new RemovedCitationEventHandler() {			
			public void onRemovedCitation(RemovedCitationEvent event) {
				removeCitation(event.getCitekey());
			}
		});

		eventBus.addHandler(ReportExporterEvent.TYPE, new ReportExporterEventHandler() {

			@Override
			public void onCompletedExport(ReportExporterEvent event) {

				switch (event.getOperationResult()) {
				case SAVED:
					//refreshWorkspace();
					break;
				case SAVED_OPEN:
					//	refreshWorkspace();
					toolBoxPanel.showExportedVersion(event.getItemId(), event.getItemId());
					break;
				default:
					break;
				}

			}
		});

		eventBus.addHandler(AddCommentEvent.TYPE, new AddCommentEventHandler() {			
			public void onAddComment(AddCommentEvent event) {
				model.addCommentToComponent(event.getSourceComponent(), event.getComment(), event.getAreaHeight());
			}
		});

		eventBus.addHandler(RemovedUserCommentEvent.TYPE, new RemovedUserCommentEventHandler() {
			public void onRemovedComment(RemovedUserCommentEvent event) {
				model.removeComment(event.getSourceComponent());				
			}						
		});

		eventBus.addHandler(ItemSelectionEvent.TYPE, new ItemSelectionEventHandler() {
			@SuppressWarnings("unchecked")
			public void onItemSelected(ItemSelectionEvent event) {
				HashMap<String,Object> map =  event.getItemSelected();
				int sectionIndex = 0, compIndex = 0;
				if (map != null) {
					if (map.get("item").equals("Section")) {
						sectionIndex = Integer.parseInt((String) map.get("index"));
						seekSection(sectionIndex+1);
					} else {
						compIndex = Integer.parseInt((String) map.get("index"));
						sectionIndex = Integer.parseInt((String)  ((HashMap<String, Object>) map.get("parent")).get("index"));
						seekSection(sectionIndex+1);
						int top = getModel().getSectionComponent(sectionIndex+1).get(compIndex).getContent().getAbsoluteTop();
						ReportGenerator.get().getScrollerPanel().setVerticalScrollPosition(top);
					}
				}

			}
		});
		}


		/**
		 * constructor
		 */
		public Presenter() {
			model = new TemplateModel(this);

			titleBar = ReportGenerator.get().getTitleHeader();
			dlg.center();
			dlg.show();
			handleEvents();
			AsyncCallback<SessionInfo> callback = new AsyncCallback<SessionInfo>() {
				public void onFailure(Throwable caught) {}

				public void onSuccess(final SessionInfo sessionInfo) {
					currentUser = sessionInfo.getUserName();
					currentScope = sessionInfo.getScope();
					isVME = sessionInfo.isVME();
					currentReportsStoreGatewayURL = sessionInfo.getRsgEndpoint();
					addTextToolBar(true);
					if  (sessionInfo.isWorkflowDocument()) {
						model.getModelService().getWorkflowDocumentFromDocumentLibrary(new AsyncCallback<Model>() {

							public void onFailure(Throwable caught) {	
								dlg.hide();
							}

							public void onSuccess(Model wfReport) {
								dlg.hide();
								loadModel(wfReport, true);
								header.setMenuForWorkflowDocument(sessionInfo.isEditable());
								titleBar.addWorkflowButtons(true);
								pollServiceForLockRenewal();
							}
						});
					}
					else { //not in workflow mode check if a report was open
						model.getModelService().readTemplateFromSession(new AsyncCallback<Model>() {
							public void onFailure(Throwable caught) {
								dlg.hide();
							}
							public void onSuccess(Model result) {	
								if (isVME) {
									header.setMenuForVME();
									header.enableExports();
								}
								dlg.hide();							
								if (result != null) {  //there was one report open in session
									loadModel(result, true);
									header.enableExports();
								} else{	
									commonCommands.newTemplate.execute();
								}
							}
						});
					}
				}
			};

			model.getModelService().getSessionInfo(getHost(), callback);
			commonCommands = new CommonCommands(this);
			//importDlg = new FimesFileUploadWindow(eventBus);
		}	

		public void setClientSequenceSelected(ClientSequence toSet) {
			this.clientSeqSelected = toSet;
		}

		public void showOpenOptions() {
			wp.showOpenOptions(isVME, currentReportsStoreGatewayURL);
		}

		public void showLoading() {
			wp.showLoading();
		}

		/**
		 * load the template to edit in the MODEL and in the VIEW
		 * @param templateToOpen the name of the template to open without extension nor path
		 * @param templateObjectID the id in the folder of the template to open
		 * @param isTemplate true if you are opening a template false if you are opening a report
		 */
		public void openTemplate(String templateToOpen, String templateObjectID, final boolean isTemplate) {
			showLoading();
			//will asyncrously return a Serializable Model instance read from disk
			model.getModelService().readModel(templateToOpen, templateObjectID, isTemplate, false, new AsyncCallback<Model>() {
				@Override
				public void onSuccess(Model toLoad) {
					if (toLoad != null)
						loadModel(toLoad, true);
					else
						Window.alert("Could not Load template, error on server: ");
				}
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Could not Load template, please try again later: " + caught.getMessage());

				}
			});
		}

		/**
		 * called when a citation is added
		 * @param citekey .
		 * @param text .
		 */
		public void addCitation(String citekey, String text) {
			if (! hasBibliography()) {
				MessageBox.alert("Warning", "Bibliography will be added as last section of this report", null);			
				model.insertBiblioSection();			
			}
			model.addCitation(citekey, text);
			titleBar.getSectionSwitchPanel().setPageDisplayer(model.getCurrentPage(), model.getTotalPages());
		}

		public boolean removeCitation(String citekey) {
			boolean toReturn = model.removeCitation(citekey);
			if (toReturn) { //if has been removed from the model 
				if (model.getCurrentPage() == model.getTotalPages()) { //if the view is displaying the bibliography, need to refresh it
					seekSection(model.getTotalPages());
				}
			}
			return toReturn;
		}

		/**
		 * look if a section with a specific metadata (that indicate sit is a biblio section)
		 * exists in the current report model: 		
		 * check whether the last section has Metadata "isBiblio" = "true" or false
		 * @return true if bibliography is present yet false otherwise
		 */
		public boolean hasBibliography() {
			for (Metadata metadata : model.getSection(model.getTotalPages()).getAllMetadata()) 
				if (metadata.getAttribute().equals(TemplateModel.BIBLIO_SECTION)) return true;
			return false;
		}
		/**
		 * look if a comment with a specific metadata (that indicate sit is a comment)
		 * exists in the current report model: 		
		 * @return true if comment is present yet false otherwise
		 */
		public boolean hasComments(Widget toCheck) {
			TemplateSection currSection = model.getSection(model.getCurrentPage());
			return currSection.hasComments(toCheck);
		}
		/**
		 * look if a comment with a specific metadata (that indicate sit is a comment)
		 * exists in the current report model: 		
		 * @return true if comment is present yet false otherwise
		 */
		public AddCommentEvent getComponentComments(Widget toCheck) {
			TemplateSection currSection = model.getSection(model.getCurrentPage());
			return currSection.getComponentComments(toCheck);
		}
		/**
		 * 
		 */
		private void pollServiceForLockRenewal() {
			final int fourteenMinutes = 840000;
			final Timer t = new Timer() {
				public void run() {
					model.getModelService().renewLock(new AsyncCallback<Void>() {
						public void onFailure(Throwable caught) {
						}
						public void onSuccess(Void result) {
							schedule(fourteenMinutes);
						}
					});
				}
			};
			t.schedule(fourteenMinutes);
		}
		/**
		 * put the commands in the hashmap
		 */
		private HashMap<String, Command> getCommands() {
			/**
			 * commands to pass to the toolbar
			 */
			HashMap<String, Command> toReturn = new HashMap<String, Command>();

			toReturn.put("structureView", commonCommands.structureView);
			toReturn.put("save", commonCommands.saveTemplate);
			toReturn.put("exportRSG", commonCommands.exportToRSG);
			toReturn.put("newdoc", commonCommands.newTemplate);
			toReturn.put("open_report", commonCommands.openReport);
			toReturn.put("open_template", commonCommands.openTemplate);
			toReturn.put("importing", commonCommands.importTemplateCommand);
			toReturn.put("insertImage", commonCommands.insertImage);
			toReturn.put("pickColor", commonCommands.pickColor);

			return toReturn;

		}

		/**
		 * to remove the current displayed section
		 */
		public void discardCurrentSection() {
			if (model.getTotalPages() == 1)
				Window.alert("Cannot discard section, need ad least 2");
			else {
				boolean result = Window.confirm("Are you sure you want to discard section number " + model.getCurrentPage() + "?");
				if (result) {
					TemplateSection removed = model.discardSection(model.getCurrentPage());
					if (removed == null)
						GWT.log("REMOVED NOTHING", null);
					else
						GWT.log("REMOVED " + removed.getAllComponents().size(), null);
					loadFirstSection();
				}
			}
		}
		/**
		 * 
		 * @param titleBar ,
		 */
		public void setTitleBar(TitleBar titleBar) {
			this.titleBar = titleBar;
		}
		/**
		 * 
		 * @return .
		 */
		public String getHost() {
			return GWT.getHostPageBaseURL() + "../../";
		}	


		/**
		 * remove the user-added components from the workspace, and from the model
		 *
		 */
		public void cleanAll() {
			//		reset the model
			model = new TemplateModel(this);

			//reset the UI

			//give the new model instance 
			header.setModel(model);
			wp.setModel(model);

			cleanWorkspace();
			titleBar.getSectionSwitchPanel().hideNextButton();
			titleBar.getSectionSwitchPanel().hidePrevButton();
			titleBar.setTemplateName(model.getTemplateName());
			titleBar.getSectionSwitchPanel().setPageDisplayer(model.getCurrentPage(), model.getTotalPages());
			titleBar.getSectionSwitchPanel().setVisible(false);
			resizeWorkingArea(model.getPageWidth(), model.getPageHeight());

			toolBoxPanel.clear();
			toolBoxPanel.collapse();

		}
		/**
		 * remove the user-added components from the workspace (in the current page) but not from the model 
		 *
		 */
		public void cleanWorkspace() {
			wp.getMainLayout().clear();
			ReportGenerator.get().getScrollerPanel().setScrollPosition(0);
		}


		/**
		 * Save the current report 
		 * @param folderid the id where to save the report
		 * 
		 */
		public void saveReport(String folderid, String name) {
			dlg.center();
			dlg.show();
			Model toSave = model.getSerializableModel();
			reportService.saveReport(toSave, folderid, name, new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					dlg.hide();
					MessageBox.alert("Warning","Report Not Saved: " + caught.getMessage(), null);
				}
				public void onSuccess(Void result) {
					dlg.hide();
					MessageBox.info("Saving Operation","Report Saved Successfully", null);
					refreshWorkspace();
				}
			});

		}
		/**
		 * Save the current report in a given folder 
		 * 
		 */
		public void saveReport() {
			dlg.center();
			dlg.show();
			Model toSave = model.getSerializableModel();
			reportService.saveReport(toSave, new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					dlg.hide();
					MessageBox.alert("Warning","Report Not Saved: " + caught.getMessage(), null);
				}
				public void onSuccess(Void result) {
					dlg.hide();
					MessageBox.info("Saving Operation","Report Saved Successfully", null);
					refreshWorkspace();
				}
			});
		}

		/**
		 * Save the current report in a given folder 
		 * 
		 */
		public void updateWorkflowDocument(boolean update) {
			dlg.center();
			dlg.show();
			Model toSave = model.getSerializableModel();
			reportService.updateWorkflowDocument(toSave, update, new AsyncCallback<Void>() {

				public void onFailure(Throwable caught) {
					dlg.hide();
					Window.alert("failed to update workflow document");
				}
				public void onSuccess(Void result) {	
					dlg.hide();
					loadWorkflowLibraryApp();
				}			
			});
		}

		/**
		 * 
		 *@param templateName .
		 */
		public void changeTemplateName(String templateName) {
			//initialize the template
			titleBar.setTemplateName(templateName);
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
			this.@org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter::location = $wnd.location.href;
	}-*/;

		/**
		 * Import a Section in the View and in the Model
		 * @param toLoad the SerializableModel instance where toget the section
		 * @param sectionNoToimport section to import 0 -> n-1
		 * @param beforeSection say where to import this section (before)
		 * @param asLastSection say to import this section as last section in the curren template / report 
		 */
		public void importSection(Model toLoad, int sectionNoToimport, int beforeSection, boolean asLastSection) {
			model.importSectionInModel(toLoad, sectionNoToimport, beforeSection, asLastSection);
			if (asLastSection) 		
				seekLastPage();
			else 
				seekSection(beforeSection);
			Window.alert("Importing Complete");
		}

		/**
		 * in case someone imported a new section 
		 */
		public void seekLastPage() {
			while (! (model.getCurrentPage() == model.getTotalPages()) )
				nextPageButtonClicked();
		}

		/**
		 * in case someone imported a new section 
		 * @param sect2Seek .
		 */
		public void seekSection(int sect2Seek) {
			loadFirstSection();
			while (! ( model.getCurrentPage() == sect2Seek) )
				nextPageButtonClicked();
		}
		/**
		 * 
		 */
		public void addTextToolBar(boolean enableCommands) {
			RichTextToolbar rtbar = new RichTextToolbar(new RichTextArea(), getCommands(), isVME);
			rtbar.enableCommands(enableCommands);

			currentSelectedToolbar = rtbar;
			SimplePanel deco = new SimplePanel();
			rtbar.setEnabled(false);
			deco.add(rtbar);
			deco.setSize("100%", "25");
			rtbar.setWidth("100%");	
			ReportGenerator.get().getToolbarPanel().clear();
			ReportGenerator.get().getToolbarPanel().add(deco);		
		}
		/**
		 * enable the format text toolbar for the given Rich Textarea passed as argument
		 * @param d4sArea the enabled text area
		 */
		public void enableTextToolBar(RichTextArea d4sArea) {		

			RichTextToolbar rtbar = new RichTextToolbar(d4sArea, getCommands(), isVME);
			if (menuForWorkflowDocument) //disable open and save buttons from the toolbar
				rtbar.enableCommands(false);
			currentSelectedToolbar = rtbar;
			rtbar.setEnabled(true);
			ReportGenerator.get().getToolbarPanel().clear();
			SimplePanel deco = new SimplePanel();
			deco.add(rtbar);
			deco.setSize("100%", "25");
			rtbar.setWidth("100%");	
			ReportGenerator.get().getToolbarPanel().add(deco);
		}

		/**
		 * enable the format text toolbar for the given Rich Textarea passed as argument
		 * @param d4sArea the enabled text area
		 */
		public void enableBiblioEntry(RichTextArea d4sArea) {		
			ReportGenerator.get().getHeader().enableBiblioEntry(d4sArea);
		}

		/**
		 * generate the docx to be passed to the fimesExporter 
		 * @param model
		 */
		public void generateFiMES(final TemplateModel model) {
		}
		/**
		 * 
		 * @param model .
		 * @param type .
		 */
		public void generateManifestation(final TemplateModel model, final ExportManifestationType type) {
			GWT.runAsync(ReportExporterPopup.class, new RunAsyncCallback() {

				@SuppressWarnings("incomplete-switch")
				@Override
				public void onSuccess() {
					ReportExporterPopup popup = new ReportExporterPopup(eventBus);
					Model reportModel = model.getSerializableModel();				
					switch (type) {
					case DOCX:
						popup.export(reportModel, TypeExporter.DOCX);
						break;
					case HTML:
						popup.export(reportModel, TypeExporter.HTML);
						break;
					case PDF:
						popup.export(reportModel, TypeExporter.PDF);
						break;
					case XML:
						popup.export(reportModel, TypeExporter.XML);
						break;
					}				
				}

				@Override
				public void onFailure(Throwable reason) {
				}
			});

		}



		public void openAddCitationDialog() {
			AddBiblioEntryDialog dlg = new AddBiblioEntryDialog(eventBus);
			dlg.show();
		}

		public void openManageCitationsDialog() {
			DeleteCitationsDialog dlg = new DeleteCitationsDialog(eventBus, model.getSection(model.getTotalPages()));
			dlg.show();
		}

		/**
		 * 
		 * @return .
		 */
		public Headerbar getHeader() {
			return header;
		}


		/**
		 * 
		 * @return .
		 */
		public TemplateModel getModel() {
			return model;
		}

		/**
		 * 
		 * @return .
		 */
		public ToolboxPanel getToolBoxPanel() {
			return toolBoxPanel;
		}

		/**
		 * 
		 * @return .
		 */
		public WorkspacePanel getWp() {
			return wp;
		}

		/**
		 * called when nextPage Button is Clicked
		 */
		public void nextPageButtonClicked() {
			cleanWorkspace();
			//refresh the current page in the model
			model.setCurrentPage(model.getCurrentPage() + 1);

			//refresh the current page in the UI
			titleBar.getSectionSwitchPanel().setPageDisplayer(model.getCurrentPage(), model.getTotalPages());

			//read the previous user added elements to the template page from the model and place them back in the UI
			placeTemplatePageElements(model.getCurrentPage());

			if (model.getCurrentPage() == model.getTotalPages()) 
				titleBar.getSectionSwitchPanel().hideNextButton();
			else
				titleBar.getSectionSwitchPanel().showNextButton();

			if (model.getCurrentPage() == 1)
				titleBar.getSectionSwitchPanel().hidePrevButton();
			else
				titleBar.getSectionSwitchPanel().showPrevButton();
		}

		/**
		 * 
		 * @param toLoad
		 */
		private void loadModel(Model toLoad, boolean savingEnabled) {
			//reset the UI
			cleanAllNotSession();

			//load the serializable model in my Model 
			model.loadModel(toLoad, this);

			wp.setModel(model);

			titleBar.setTemplateName(model.getTemplateName());
			titleBar.getSectionSwitchPanel().setPageDisplayer(model.getCurrentPage(), model.getTotalPages());
			titleBar.getSectionSwitchPanel().setVisible(true);
			resizeWorkingArea(model.getPageWidth(), model.getPageHeight());

			addTextToolBar(savingEnabled);
			int currPage = model.getCurrentPage();
			//load the UI components of the current page
			GWT.log("READ CURR PAGE"+currPage, null);
			placeTemplatePageElements(currPage);

			//if there is more than one page place in the UI the next page button 
			if (currPage < model.getTotalPages()) {
				titleBar.getSectionSwitchPanel().showNextButton();
			}
			if (currPage > 1)
				titleBar.getSectionSwitchPanel().showPrevButton();

			if (isShowingStructure)
				showStructure();

		}

		/**
		 * just clean the page
		 */
		public void cleanAllNotSession() {
			//		reset the model
			model = new TemplateModel(this);

			//reset the UI

			//give the new model instance 
			header.setModel(model);
			wp.setModel(model);

			cleanWorkspace();
			titleBar.getSectionSwitchPanel().hideNextButton();
			titleBar.getSectionSwitchPanel().hidePrevButton();
			titleBar.setTemplateName(model.getTemplateName());
			titleBar.getSectionSwitchPanel().setPageDisplayer(model.getCurrentPage(), model.getTotalPages());
			titleBar.getSectionSwitchPanel().setVisible(false);
			resizeWorkingArea(model.getPageWidth(), model.getPageHeight());
		}

		private void loadFirstSection() {
			//reset the UI
			cleanWorkspace();
			titleBar.getSectionSwitchPanel().hideNextButton();
			titleBar.getSectionSwitchPanel().hidePrevButton();
			model.setCurrentPage(1);


			titleBar.setTemplateName(model.getTemplateName());
			titleBar.getSectionSwitchPanel().setPageDisplayer(model.getCurrentPage(), model.getTotalPages());
			titleBar.getSectionSwitchPanel().setVisible(true);
			addTextToolBar(true);
			int currPage = model.getCurrentPage();
			//load the UI components of the current page
			GWT.log("READ CURR PAGE"+currPage, null);
			placeTemplatePageElements(currPage);

			//if there is more than one page place in the UI the next page button 
			if (currPage < model.getTotalPages()) {
				titleBar.getSectionSwitchPanel().showNextButton();
			}
			if (currPage > 1)
				titleBar.getSectionSwitchPanel().showPrevButton();

		}

		/**
		 * It places back the user added widgets (TemplateComponents) in the page
		 * 
		 * @param section . the section number of the wanted TemplateComponent(s)
		 */

		public void placeTemplatePageElements(int section) {
			if (! (model.getSectionComponent(section) == null)) {
				List<TemplateComponent> pageElems = model.getSectionComponent(section);

				for (TemplateComponent component : pageElems) {
					int uiX = component.getX();
					int uiY= component.getY();		
					switch (component.getType()) {	
					case HEADING_1:				
					case HEADING_2:					
					case HEADING_3:		
					case HEADING_4:	
					case HEADING_5:	
					case TITLE:	
						if (component.isLocked()) {
							HTML text = (HTML) component.getContent();
							wp.addComponentToLayout(text, component.isDoubleColLayout());
						}
						else {
							HeadingTextArea textArea = (HeadingTextArea) component.getContent();
							textArea.getMyInstance().setTop(uiY);
							textArea.getMyInstance().setLeft(uiX);
							wp.addComponentToLayout(textArea, component.isDoubleColLayout());
						}

						break;
					case BODY_NOT_FORMATTED:
						if (component.isLocked()) {
							HTML text = (HTML) component.getContent();
							text.addStyleName("readOnlyText");
							wp.addComponentToLayout(text, component.isDoubleColLayout());
						}
						else {
							BasicTextArea textArea = (BasicTextArea) component.getContent();
							wp.addComponentToLayout(textArea, component.isDoubleColLayout());
						}

						break;
					case BODY:		
						if (component.isLocked()) {
							HTML text = (HTML) component.getContent();
							wp.addComponentToLayout(text, component.isDoubleColLayout());
						}
						else {
							D4sRichTextarea textArea = (D4sRichTextarea) component.getContent();
							textArea.getMyInstance().setTop(uiY);
							textArea.getMyInstance().setLeft(uiX);
							wp.addComponentToLayout(textArea, component.isDoubleColLayout());
						}

						break;
					case DYNA_IMAGE: 
						ClientImage imageDropping = (ClientImage) component.getContent();
						wp.addComponentToLayout(imageDropping, component.isDoubleColLayout());
						break;
					case FAKE_TEXTAREA:
						break;
					case TOC:
						ReportTextArea dp = (ReportTextArea) component.getContent();
						wp.addComponentToLayout(dp, component.isDoubleColLayout());
						setCurrCursorPos(uiY);
						break;
					case BIBLIO:
						ReportTextArea dp2 = (ReportTextArea) component.getContent();
						wp.addComponentToLayout(dp2, component.isDoubleColLayout());
						setCurrCursorPos(uiY);
						break;
					case PAGEBREAK:
						ReportTextArea dp3 = (ReportTextArea) component.getContent();
						wp.addComponentToLayout(dp3, component.isDoubleColLayout());
						setCurrCursorPos(uiY);
						break;
					case TIME_SERIES:
						break;
					case FLEX_TABLE:
						GenericTable gt = (GenericTable) component.getContent();
						GWT.log("Reading TABLE rows: " + gt.getRowsNo()  + " cols: " + gt.getCols());
						wp.addComponentToLayout(gt,  component.isDoubleColLayout());
						break;
					case ATTRIBUTE_MULTI:
						AttributeMultiSelection at = (AttributeMultiSelection) component.getContent();
						wp.addComponentToLayout(at, component.isDoubleColLayout());
						setCurrCursorPos(uiY);
						break;
					case ATTRIBUTE_UNIQUE:
						AttributeSingleSelection atu = (AttributeSingleSelection) component.getContent();
						wp.addComponentToLayout(atu, component.isDoubleColLayout());
						setCurrCursorPos(uiY);
						break;
					case COMMENT:
						HTML text = (HTML) component.getContent();
						wp.addComponentToLayout(text, component.isDoubleColLayout());
						break;
					case INSTRUCTION:
						HTML instr = (HTML) component.getContent();
						wp.addComponentToLayout(instr, component.isDoubleColLayout());
						break;
					case REPEAT_SEQUENCE_DELIMITER:
						GroupingDelimiterArea gpa = (GroupingDelimiterArea) component.getContent();
						wp.addComponentToLayout(gpa,  component.isDoubleColLayout());
						break;
					case REPEAT_SEQUENCE:
						ClientRepeatableSequence rps = (ClientRepeatableSequence) component.getContent();
						wp.addComponentToLayout(rps,  component.isDoubleColLayout());
						break;
					case REPORT_REFERENCE:
						ClientReportReference cmSeq = (ClientReportReference) component.getContent();
						wp.addComponentToLayout(cmSeq,  component.isDoubleColLayout());
						break;
					case BODY_TABLE_IMAGE:
						TextTableImage tti = (TextTableImage) component.getContent();
						wp.addComponentToLayout(tti,  component.isDoubleColLayout());
						break;
					}
				}
			}
		}

		/**
		 * 
		 * @param y .
		 */
		public void setCurrCursorPos(int y) {
			this.currFocus = y;
		}
		public Coords getInsertionPoint() {
			int y = getSelectedIndex();
			return new Coords(25, y);
		}
		public int getSelectedIndex() {
			return currFocus;
		}
		/**
		 * called when prevPage Button is Clicked
		 */

		public void prevPageButtonClicked() {
			cleanWorkspace();
			//refresh the current page in the model
			model.setCurrentPage(model.getCurrentPage() - 1);

			//refresh the current page in the UI
			titleBar.getSectionSwitchPanel().setPageDisplayer(model.getCurrentPage(), model.getTotalPages());

			//read the previous user added elements to the template page from the model and place them back in the UI
			placeTemplatePageElements(model.getCurrentPage());

			if (model.getCurrentPage() == model.getTotalPages()) 
				titleBar.getSectionSwitchPanel().hideNextButton();
			else
				titleBar.getSectionSwitchPanel().showNextButton();

			if (model.getCurrentPage() == 1)
				titleBar.getSectionSwitchPanel().hidePrevButton();
			else
				titleBar.getSectionSwitchPanel().showPrevButton();
		}

		/**
		 * Resize the template componet just the model 
		 * 
		 * @param toResize .
		 * @param newWidth .
		 * @param newHeight .
		 */
		public void resizeTemplateComponentInModel(Widget toResize, int newWidth, int newHeight) {
			model.resizeModelComponent(toResize, newWidth, newHeight);
		}

		/**
		 * @param width . 
		 * @param height . 
		 */
		public void resizeWorkingArea(int width, int height) {
			//save the new state ---> TO MODEL
			model.setPageWidth(width);
			model.setPageHeight(height);

			//apply the change   ---> TO VIEW
			wp.resizeWorkspace(width, height);
		}
		/**
		 * 
		 * @param header .
		 */
		public void setHeader(Headerbar header) {
			this.header = header;
		}
		/**
		 * 
		 * @param toolBoxPanel It's the tool box panel
		 */
		public void setToolBoxPanel(ToolboxPanel toolBoxPanel) {
			this.toolBoxPanel = toolBoxPanel;
		}
		/**
		 * 
		 * @param wp .
		 */
		public void setWp(WorkspacePanel wp) {
			this.wp = wp;
		}
		/**
		 * 
		 * @return the scope in which the application is running on
		 */
		public String getCurrentScope() {
			return currentScope;
		}

		/**
		 * 
		 * @return the user username who is using the application 
		 */
		public UserBean getCurrentUser() {
			return currentUser;
		}

		/**
		 * 
		 * @return .
		 */
		public TitleBar getTitleBar() {
			return titleBar;
		}
		/**
		 * refresh the root in the workspace
		 */
		public void refreshWorkspace() {
			toolBoxPanel.refreshRoot();
		}
		/**
		 * show the upload file popup
		 */
		public void showUploadImagePopup(ClientImage selectedImage) {
			uploadDlg = new UploadProgressDialog("Upload Image",  eventBus, true);
			uploadDlg.center();
			uploadDlg.show();		
			this.selectedImage = selectedImage;
		}
		/**
		 * show the upload file popup
		 */
		public void showUploadFilePopup() {
			uploadDlg = new UploadProgressDialog("Upload Report",  eventBus);
			uploadDlg.center();
			uploadDlg.show();		
		}
		/**
		 * 
		 * @return the current selected rich text area
		 */
		public RichTextToolbar getCurrentSelected() {
			return currentSelectedToolbar;
		}

		public void setMenuForWorkflowDocument(boolean enable) {
			menuForWorkflowDocument = enable;
		}

		public boolean getMenuForWorkflowDocument() {
			return menuForWorkflowDocument;
		}

		public void setAreaForBiblio(RichTextArea d4sArea) {
			areaForBiblio = d4sArea;
		}

		/**
		 * show/hide the structure view
		 */

		public void toggleReportStructure() {
			if (!isShowingStructure) {
				showStructure();
				isShowingStructure = true;
			}
			else {
				toolBoxPanel.collapse();
				isShowingStructure = false;
			}
		}

		private void showStructure() {
			toolBoxPanel.showStructure(new ReportStructurePanel(eventBus, model.getSerializableModel(), ToolboxPanel.TOOLBOX_WIDTH+"px", ToolboxPanel.TOOLBOX_HEIGHT+"px"));

		}

		public HorizontalPanel getExportsPanel() {
			return exportsPanel;
		}


		public void setExportsPanel(HorizontalPanel exportsPanel) {
			this.exportsPanel = exportsPanel;
		}

		/**
		 * when export is done this method is called
		 * @param filePath
		 * @param itemName
		 * @param type
		 */
		public void showExportSaveOptions(final String filePath, final String itemName, final TypeExporter type)  {
			clearExportPanel();
			/*
			 * I need to save a temp file, in case the user uses the Save & Open. 
			 * Smart popup blockers will allow a popup if it is directly associated to a user’s action. 
			 * If it’s delayed in anyway, there’s a good chance it’s going to get blocked. The exported File needs to be there when clicking open.
			 */
			reportService.save(filePath, null, "system.tmp", type, true, new AsyncCallback<String>() {			
				@Override
				public void onSuccess(String createdItemId) {
					//here i pass the temp createdItemId
					showExportPanel(filePath, itemName, type, createdItemId);
				}

				@Override
				public void onFailure(Throwable caught) {		
					Window.alert("Error while trying exporting this report: " + caught.getMessage());
				}
			});

		}

		public void showExportPanel(final String filePath, final String itemName, final TypeExporter type, String tempFileId) {
			final ExportOptions exo = new ExportOptions(this, toolBoxPanel, filePath, itemName, type, reportService, tempFileId);
			exportsPanel.add(exo);
			//needed for applying the css3 transition effect
			final Timer t = new Timer() {
				@Override
				public void run() {
					exo.getMainPanel().addStyleName("exportPanel-show");
				}
			};
			t.schedule(10);
		}

		public void clearExportPanel() {
			exportsPanel.clear();
		}

		public void newDoc() {
			changeTemplateName(TemplateModel.DEFAULT_NAME);
			cleanAll();
			showOpenOptions();
		}

		/**
		 * 
		 */
		public void showVMEImportDialog() {
			SelectVMEReportDialog dlg = new SelectVMEReportDialog(getEventBus(), VMETypeIdentifier.Vme, Action.SELECT);
			dlg.show();
		}

		/**
		 * 
		 */
		public void showVMEReportRefImportDialog(VMETypeIdentifier refType) {
			SelectVMEReportDialog dlg = new SelectVMEReportDialog(getEventBus(), refType, Action.SELECT);
			dlg.show();
		}
		/**
		 * 
		 */
		public void showVMERefAssociateDialog(VMETypeIdentifier refType) {
			SelectVMEReportDialog dlg = new SelectVMEReportDialog(getEventBus(), refType, Action.ASSOCIATE);
			dlg.show();
		}
		/**
		 * 
		 */
		public void showVMEDeleteDialog(VMETypeIdentifier refType) {
			SelectVMEReportDialog dlg = new SelectVMEReportDialog(getEventBus(), refType, Action.DELETE);
			dlg.show();
		}

		public void importVMETemplate(final VMETypeIdentifier type) {
			showLoading();
			reportService.importVMETemplate(type, new AsyncCallback<Model>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Could not Load Template Model,  error on server.: " + caught.getMessage());				
				}

				@Override
				public void onSuccess(Model toLoad) {
					if (toLoad != null) { 
						loadModel(toLoad, type == VMETypeIdentifier.Vme);
						currentVmeType = type;
					}
					else
						Window.alert("Could not Load Template Model, error on server.");				
				}
			});

		}
		/**
		 * Export To the Reports Store Gateway
		 * 
		 */
		public void exportReportToRSG() {
			MessageBox.confirm("Commit to VME-DB", "Are you sure you want to commit the " + model.getTemplateName() + " into the VME Database?", new Listener<MessageBoxEvent>() {				
				@Override
				public void handleEvent(MessageBoxEvent be) {        
	                 if(Dialog.YES.equalsIgnoreCase(be.getButtonClicked().getItemId())) {
	     				dlg.center();
	     				Model toSave = model.getSerializableModel();
	     				reportService.exportReportToRSG(currentVmeType, toSave, new AsyncCallback<VmeExportResponse>() {
	     					public void onFailure(Throwable caught) {
	     						dlg.hide();
	     						MessageBox.alert("Warning","Report Not Exported: " + caught.getMessage(), null);
	     					}
	     					public void onSuccess(VmeExportResponse response) {
	     						dlg.hide();
	     						//write ok
	     						if (response.isGloballySucceded()) {
	     							MessageBox.info("Exporting to RSG Operation","Report Exported Successfully", null);
	     							newDoc();
	     						}
	     						//runtime exception
	     						else if (response.getResponseMessageList().size() == 1 && response.getResponseMessageList().get(0).getResponseEntryCode().equals("RUNTIME_EXCEPTION")) {
	     							MessageBox.alert("Exporting to RSG Operation Failed","Report Exporting has failed for the following reason: <br/> " + response.getResponseMessageList().get(0).getResponseMessage(), null);
	     						}
	     						//user report compile exception
	     						else {
	     							String failReasons = "";
	     							for (VmeResponseEntry entry : response.getResponseMessageList()) {
	     								failReasons += entry.getResponseMessage() + "<br/>";
	     							}
	     							MessageBox.alert("Exporting to RSG Operation Failed","Report Exporting has failed for the following reasons: <br/> " + failReasons, null);
	     						}
	     					}
	     				});
	                 }
					
				}
			});
		}

		private void deleteVMEReport(String reportId, String name, final VMETypeIdentifier type) {
			if (Window.confirm("Are you sure you want to delete " + name + " from the VME Database? (This action is Undoable)")) {
				showLoading();
				reportService.deleteReportFromRSG(type, reportId, new AsyncCallback<VmeExportResponse>() {
					@Override
					public void onFailure(Throwable caught) {
						dlg.hide();
						Window.alert("Could not Delete,  error on server.: " + caught.getMessage());		
					}

					@Override
					public void onSuccess(VmeExportResponse response) {
						dlg.hide();
						//delete ok
						if (response.getResponseMessageList().size() == 1 && response.getResponseMessageList().get(0).getResponseEntryCode().equalsIgnoreCase("SUCCEEDED"))
							MessageBox.info("Delete VME Report Operation","Report Deleted Successfully", null);
						//runtime exception
						else if (response.getResponseMessageList().size() == 1 && response.getResponseMessageList().get(0).getResponseEntryCode().equals("RUNTIME_EXCEPTION")) {
							MessageBox.alert("Delete VME Report Operation Failed","Report Delete has failed for the following reason: <br/> " + response.getResponseMessageList().get(0).getResponseMessage(), null);
						}
						//user report compile exception
						else {
							String failReasons = "";
							for (VmeResponseEntry entry : response.getResponseMessageList()) {
								failReasons += entry.getResponseMessage() + "<br/>";
							}
							MessageBox.alert("Deleting Operation Failed","Report Deletion has failed for the following reasons: <br/> " + failReasons, null);
						}
						newDoc();
					}
				});
			}
		}

		private void importVMEReport(String reportId, String name, final VMETypeIdentifier type) {
			showLoading();
			reportService.importVMEReport(reportId, name, type, new AsyncCallback<Model>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Could not Load Report Model,  error on server.: " + caught.getMessage());				
				}

				@Override
				public void onSuccess(Model toLoad) {
					if (toLoad != null) { 
						loadModel(toLoad, type == VMETypeIdentifier.Vme);
						currentVmeType = type;
					}
					else
						Window.alert("Could not Load Report Model, error on server.");				
				}
			});
		}

		private void associateVMEReportRef(VMETypeIdentifier type, String reportId) throws Exception {
			GWT.log("Type="+type);
			GWT.log("Id"+reportId);
			reportService.getVMEReportRef2Associate(reportId, type, new AsyncCallback<Model>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Could not Load Report Model,  error on server.: " + caught.getMessage());								
				}

				@Override
				public void onSuccess(Model reportRef) {
					if (reportRef != null && reportRef.getSections().size() > 0) {
						//the Repeatable Seq must be in the 1st Section, second component. (because ReportsModeler put it there)
						BasicSection firstSection = reportRef.getSections().get(0);
						if (firstSection.getComponents() != null && firstSection.getComponents().size()==2) {
							ReportReferences toPass = (ReportReferences) firstSection.getComponents().get(1).getPossibleContent();
							GWT.log("Singola?" + toPass.isSingleRelation());
							String refKey = toPass.getTuples().get(0).getKey();
							Tuple ref = toPass.getTuples().get(0);
							clientSeqSelected.add(refKey, ref, toPass.isSingleRelation());
						} 
						else {
							Window.alert("Sorry, we could not locate the ReportRef correctly in the model instance");				
						}
					}
					else
						Window.alert("Could not Load Report Model, error on server.");				
				}
			});
		}

		/**
		 * this method translate the VME ReportRef String in an ENUM
		 * @param theType
		 * @return
		 * @throws Exception
		 */
		public VMETypeIdentifier getTypeIdFromString(String theType) throws Exception {
			if (theType.equals("GeneralMeasure"))
				return VMETypeIdentifier.GeneralMeasure;
			else if (theType.equals("InformationSource"))
				return VMETypeIdentifier.InformationSource;
			else if (theType.equals("FisheryAreasHistory"))
				return VMETypeIdentifier.FisheryAreasHistory;
			else if (theType.equals("VMEsHistory"))
				return VMETypeIdentifier.VMEsHistory;
			else if (theType.equals("Rfmo"))
				return VMETypeIdentifier.Rfmo;
			throw new Exception("Could not find any valid Report Ref, got " + theType + " should be any of " + VMETypeIdentifier.values().toString());
		}
	}
