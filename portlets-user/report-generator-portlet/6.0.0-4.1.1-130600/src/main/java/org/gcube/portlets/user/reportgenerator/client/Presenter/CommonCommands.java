package org.gcube.portlets.user.reportgenerator.client.Presenter;

import java.util.Arrays;

import org.gcube.portlets.user.reportgenerator.client.ReportConstants;
import org.gcube.portlets.user.reportgenerator.client.dialog.ImporterDialog;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.extjs.gxt.ui.client.event.ColorPaletteEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ColorPalette;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;



/**
 * * 
 * /**
 * <code> CommonCommands </code> class contains the menu commands for the UI
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class CommonCommands {
	/**
	 * 
	 */
	public Command exportToRSG;
	/**
	 * 
	 */
	public Command openTemplate;
	/**
	 * 
	 */
	public Command openReport;
	
	
	public Command newTemplate;
	/**
	 * 
	 */
	public Command importTemplateCommand;
	/**
	 * 
	 */
	public Command insertImage;
	/**
	 * 
	 */
	public Command saveTemplate;	
	/**
	 * 
	 */
	public Command structureView;
	/**
	 * 
	 */
	public Command pickColor;

	private Presenter presenter;

	/**
	 * 
	 * @param presenter .
	 */
	public CommonCommands(final Presenter presenter) {	
		this.presenter = presenter;
		structureView = new Command() {
			public void execute() {
				presenter.toggleReportStructure();
			}
		};
		newTemplate = new Command() {
			public void execute() {
				presenter.newDoc();
			}
		};
	
		openTemplate = new Command() {	
			public void execute() {			
				GWT.runAsync(WorkspaceExplorerSelectDialog.class, new RunAsyncCallback() {
					public void onSuccess() {
						
						ItemType[] types = {ItemType.REPORT_TEMPLATE};
						final WorkspaceExplorerSelectDialog wpTreepopup = new WorkspaceExplorerSelectDialog("Select a Template to open", Arrays.asList(types), Arrays.asList(types));

						WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {

							@Override
							public void onSelectedItem(Item item) {
								presenter.openTemplate(item.getName(), item.getId(), true);	
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

					public void onFailure(Throwable reason) {
						Window.alert("There are networks problem, please check your connection.");              
					}
				});
			}
		};

		openReport = new Command() {	
			public void execute() {			
				GWT.runAsync(WorkspaceExplorerSelectDialog.class, new RunAsyncCallback() {
					public void onSuccess() {
						
						ItemType[] types = {ItemType.REPORT};
						final WorkspaceExplorerSelectDialog wpTreepopup = new WorkspaceExplorerSelectDialog("Select a Report to open", Arrays.asList(types), Arrays.asList(types));

						WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {

							@Override
							public void onSelectedItem(Item item) {
								presenter.openTemplate(item.getName(), item.getId(), true);			
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

					public void onFailure(Throwable reason) {
						Window.alert("There are networks problem, please check your connection.");              
					}
				});
			}
		};

		pickColor = new Command() {	
			public void execute() {				
				int left = presenter.getHeader().getMainLayout().getAbsoluteLeft() + 600;
				int top = presenter.getHeader().getMainLayout().getAbsoluteTop() + 50;
				final PopupPanel pp = new PopupPanel(true);
		
				ColorPalette colorPalette = new ColorPalette(); 
				colorPalette.addListener(Events.Select, new Listener<ColorPaletteEvent>() {
					@SuppressWarnings("deprecation")
					public void handleEvent(ColorPaletteEvent be) {
						presenter.getCurrentSelected().getExtendedFormatter().setForeColor(be.getColor());
						pp.hide();
					}
				});
				pp.add(colorPalette);
				pp.setAnimationEnabled(false);
				pp.setPopupPosition(left, top);
				pp.show();
			}
		};



		importTemplateCommand  = new Command() {			
			public void execute() {
				if (! ReportConstants.isDeployed) {
					int left = presenter.getHeader().getMainLayout().getAbsoluteLeft() + 50;
					int top = presenter.getHeader().getMainLayout().getAbsoluteTop() + 25;
					ImporterDialog dlg = new ImporterDialog(null, presenter);
					dlg.setPopupPosition(left, top);
					dlg.setAnimationEnabled(true);
					dlg.show();
				}
				else {					
					GWT.runAsync(WorkspaceExplorerSelectDialog.class, new RunAsyncCallback() {
						public void onSuccess() {
							
							ItemType[] types = {ItemType.REPORT_TEMPLATE};
							final WorkspaceExplorerSelectDialog wpTreepopup = new WorkspaceExplorerSelectDialog("Pick the item you want to import from", Arrays.asList(types), Arrays.asList(types));

							WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {

								@Override
								public void onSelectedItem(Item item) {
									int left = presenter.getHeader().getMainLayout().getAbsoluteLeft() + 50;
									int top = presenter.getHeader().getMainLayout().getAbsoluteTop() + 25;
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

						public void onFailure(Throwable reason) {
							Window.alert("There are networks problem, please check your connection.");              
						}
					});					
				}
			}	
		};

		saveTemplate = new Command() {			
			public void execute() {
				if (presenter.getModel().getTemplateName().endsWith("d4sR"))
					presenter.saveReport();
				else
					saveReportAsDialog();
			}
		};
		
		exportToRSG = new Command() {			
			public void execute() {
				presenter.exportReportToRSG();				
			}
		};

	} //end constructor

	public void saveReportAsDialog() {
		
		GWT.runAsync(WorskpaceExplorerSaveNotificationListener.class, new RunAsyncCallback() {
			public void onSuccess() {
				ItemType[] types = {ItemType.FOLDER};
				final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Save Report, choose folder please:", Arrays.asList(types));
				WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){
					 
					@Override
					public void onSaving(Item parent, String fileName) {
						//checking user input
						String inputUser = fileName;
						String newTemplateName = inputUser;
						if (presenter.getModel().getTemplateName().compareTo(newTemplateName) != 0) {
							newTemplateName = newTemplateName.trim();
							presenter.getModel().setTemplateName(newTemplateName+".d4sR");
						}
						presenter.changeTemplateName(newTemplateName);
						presenter.saveReport(parent.getId(), newTemplateName);
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

			public void onFailure(Throwable reason) {
				Window.alert("There are networks problem, please check your connection.");              
			}
		});


	}
}
