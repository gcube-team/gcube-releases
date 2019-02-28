package org.gcube.portlets.widgets.wsthreddssync.client.view;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portal.wssynclibrary.shared.thredds.Status;
import org.gcube.portal.wssynclibrary.shared.thredds.ThCatalogueBean;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncStatus;
import org.gcube.portlets.widgets.wsthreddssync.client.SyncCompletedNotification.SyncCompletedNotificationListner;
import org.gcube.portlets.widgets.wsthreddssync.client.WsThreddsWidget;
import org.gcube.portlets.widgets.wsthreddssync.client.event.PerformDoSyncEvent;
import org.gcube.portlets.widgets.wsthreddssync.client.event.ShowMonitorSyncStatusEvent;
import org.gcube.portlets.widgets.wsthreddssync.client.view.binder.AbstractViewDialogBox;
import org.gcube.portlets.widgets.wsthreddssync.client.view.binder.CreateThreddsConfigurationView;
import org.gcube.portlets.widgets.wsthreddssync.client.view.binder.MonitorFolderSyncStatusView;
import org.gcube.portlets.widgets.wsthreddssync.client.view.binder.ShowThreddsFolderInfoView;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsFolder;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderConfiguration;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderDescriptor;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;


// TODO: Auto-generated Javadoc
/**
 * The Class WsThreddsWidgetViewManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2018
 */
public class WsThreddsWidgetViewManager {

	private Map<String, MonitorFolderSyncStatusView> mapMonitor = new HashMap<String, MonitorFolderSyncStatusView>();


	/**
	 * Instantiates a new ws thredds widget view manager.
	 */
	public WsThreddsWidgetViewManager() {
	}


	/**
	 * Cancel monitor.
	 *
	 * @param folder the folder
	 */
	public void cancelMonitor(WsFolder folder) {
		try {
			MonitorFolderSyncStatusView monitor = cancelScheduler(folder);
			if(monitor!=null) {
				GWT.log("Removed monitor for folder: "+folder.getFolderId());
				mapMonitor.remove(folder.getFolderId());
			}
		}catch (Exception e) {
			GWT.log("Error on removing map monitor for id: "+folder.getFolderId());
		}
	}


	/**
	 * Gets the monitor.
	 *
	 * @param folder the folder
	 * @return the monitor
	 */
	public MonitorFolderSyncStatusView getMonitor(WsFolder folder){

		if(folder==null)
			return null;

		return mapMonitor.get(folder.getFolderId());
	}


	/**
	 * Save monitor.
	 *
	 * @param folder the folder
	 * @param monitor the monitor
	 */
	private void saveMonitor(WsFolder folder, MonitorFolderSyncStatusView monitor) {
		GWT.log("Saving monitor for folder: "+folder.getFolderId());
		mapMonitor.put(folder.getFolderId(), monitor);
	}


	/**
	 * Cancel scheduler.
	 *
	 * @param folder the folder
	 * @return
	 */
	private MonitorFolderSyncStatusView cancelScheduler(final WsFolder folder) {

		MonitorFolderSyncStatusView monitor = getMonitor(folder);
		if(monitor.getSchedulerTime()!=null) {
			GWT.log("Cancelling scheduler time on folder: "+folder.getFolderId());
			monitor.getSchedulerTime().cancel();
		}

		return monitor;

	}

	/**
	 * Show monitor sync to folder.
	 *
	 * @param folder the folder
	 * @param syncEventsListeners
	 */
	public void showMonitorSyncToFolder(final WsFolder folder, final List<SyncCompletedNotificationListner> syncEventsListeners) {
		GWT.log("showMonitorSyncToFolder for folder: "+folder.getFolderId());

		final Modal box = new Modal(true);
		box.addStyleName("ws-thredds-modal-body");
		box.setTitle("Monitor transferring of: "+FormatUtil.getFolderTitle(folder.getFoderName(), 20));
		box.setWidth(800);
		box.hide(false);

		MonitorFolderSyncStatusView monitorView = getMonitor(folder);
		GWT.log("monitorView is: "+monitorView);

		final AbstractViewDialogBox panelView = new AbstractViewDialogBox() {

			@Override
			public void closeHandler() {
				//cancelScheduler(folder);
				box.hide();

			}

			@Override
			public void confirmHanlder(CONFIRM_VALUE confirm, Command command) {

				// TODO Auto-generated method stub

			}
		};

		if(monitorView==null) {

			monitorView = new MonitorFolderSyncStatusView() {

				@Override
				public void closetHandler() {
					//TODO CLIENT SIDE?
					//cancelMonitor(folder);
					panelView.closeHandler();
				}

				@Override
				public void setError(boolean visible, String error) {
					panelView.setError(visible, error);
				}
			};

		}

		if(monitorView.getSchedulerTime()==null) {

			//Removing old monitor
			//cancelMonitor(folder);
			//Creating new one
			final MonitorFolderSyncStatusView currentMonitor = monitorView;

			Timer schedulingTimer = new Timer() {

				@Override
				public void run() {

					WsThreddsWidget.wsThreddsSyncService.monitorSyncStatus(folder.getFolderId(), new AsyncCallback<ThSyncStatus>() {

						@Override
						public void onFailure(Throwable caught) {
							cancelMonitor(folder);
							Window.alert(caught.getMessage());

							//CALLING METHOD ON SYNC ERROR TO THE LISTENERS
							for (SyncCompletedNotificationListner listener : syncEventsListeners) {
								listener.onSyncError(folder);
							}
						}

						@Override
						public void onSuccess(ThSyncStatus status) {
							GWT.log("monitorSyncStatus: "+status);
							if(status==null) {
								GWT.log("The status is null server-side, cancelling polling");
								cancelMonitor(folder);
							}

							currentMonitor.updateStatusView(folder, status);

							if(status!=null && status.getProcessStatus()!=null) {

								if(status.getProcessStatus().getStatus().equals(Status.COMPLETED)) {
									GWT.log("Sync completed cancel the polling: "+status);
									cancelMonitor(folder);

									//CALLING METHOD ON SYNC COMPLETED TO THE LISTENERS
									for (SyncCompletedNotificationListner listener : syncEventsListeners) {
										listener.onSyncCompleted(folder);
									}
								}

							}
						}
					});
				}
			};

			schedulingTimer.scheduleRepeating(2000);
			currentMonitor.setScheduler(schedulingTimer);
			saveMonitor(folder, currentMonitor);

		}

		panelView.addViewAsWidget(monitorView);
		box.add(panelView);
		box.show();

	}


	/**
	 * Show create configuration folder.
	 *
	 * @param folder the folder
	 * @param conf the conf
	 */
	public void showCreateConfigurationFolder(final WsFolder folder, WsThreddsSynchFolderDescriptor conf){

		final Modal box = new Modal(true);
		box.setTitle("Create Thredds Sync Configuration for: "+FormatUtil.getFolderTitle(folder.getFoderName(), 20));
		//box.getElement().getStyle().setZIndex(10000);

		final AbstractViewDialogBox panelView = new AbstractViewDialogBox() {

			@Override
			public void closeHandler() {
				box.hide();

			}

			@Override
			public void confirmHanlder(CONFIRM_VALUE confirm, Command command) {

				if(confirm.equals(CONFIRM_VALUE.YES)){
					box.hide();
				}

				if(command!=null)
					command.execute();

			}
		};

		CreateThreddsConfigurationView createThreddsConfiguration = new CreateThreddsConfigurationView(folder.getFolderId()) {

			@Override
			public void submitHandler() {
				panelView.closeHandler();
				WsThreddsSynchFolderConfiguration conf = new WsThreddsSynchFolderConfiguration();
				conf.setFilter(null);

				ThCatalogueBean catalogueSelected = getSelectedCatalogue();
				String remotePath = catalogueSelected.getPath()!=null?catalogueSelected.getPath():"";
				remotePath = remotePath.isEmpty()?getFolderName():remotePath+"/"+getFolderName();
				conf.setRemotePath(remotePath);
				conf.setCatalogName(catalogueSelected.getName());
				conf.setSelectedScope(getSelectedScope());
				conf.setRootFolderId(folder.getFolderId());
				WsThreddsWidget.eventBus.fireEvent(new PerformDoSyncEvent(folder, conf));
			}

			@Override
			public void setError(boolean visible, String error) {
				panelView.setError(visible, error);

			}

			@Override
			public void setConfirm(boolean visible, String msg) {

				Command yes = new Command() {

					@Override
					public void execute() {

						submitHandler();
					}
				};

				Command no = new Command() {

					@Override
					public void execute() {

						panelView.setConfirm(false, "", null, null);
					}
				};

				panelView.setConfirm(visible, msg, yes, no);

			}
		};

		panelView.addViewAsWidget(createThreddsConfiguration);
		box.add(panelView);
		box.show();

	}


	/**
	 * Show thredds folder info.
	 *
	 * @param folder the folder
	 * @param folderDescriptor the folder descriptor
	 */
	public void showThreddsFolderInfo(final WsFolder folder, final WsThreddsSynchFolderDescriptor folderDescriptor){
		GWT.log("ShowThreddsFolderInfo folder: "+folder);
		//GWT.log("WsThreddsSynchFolderDescriptor is: "+folderDescriptor);

		final Modal box = new Modal(true);
		box.hide(false);
		//box.setWidth(WIDHT_DIALOG+"px");
		box.setTitle("Thredds Sync Information for: "+FormatUtil.getFolderTitle(folder.getFoderName(), 20));
		//box.getElement().getStyle().setZIndex(10000);

		final AbstractViewDialogBox panelView = new AbstractViewDialogBox() {

			@Override
			public void closeHandler() {
				box.hide();
			}

			@Override
			public void confirmHanlder(CONFIRM_VALUE confirm, Command command) {

				// TODO Auto-generated method stub

			}
		};

		boolean isCreateConfiguration = folderDescriptor==null?true:false;


		ShowThreddsFolderInfoView folderInfo = new ShowThreddsFolderInfoView(folder.getFolderId(), isCreateConfiguration) {

			@Override
			public void submitHandler(SUBMIT_ACTION action) {
				panelView.closeHandler();

				if(action==null)
					return;

				switch (action) {

				case CREATE_UPDATE_CONFIGURATION:

					if(folderDescriptor==null) {
						GWT.log("Performing a create configuration");
						//PERFORM A CREATE CONFIGURATION (AT FIRST TIME), THE CONFIGURATION DOES NOT EXITS
						showCreateConfigurationFolder(folder, null);

					}
//					else {
//						GWT.log("Performing an updated configuration");
//						//PERFORM AN UPDATE CONFIGURATION. THE CONFIGURATION EXIST
//						//BeanConverter.toWsThreddsFolderConfig(t, vre);
//						showCreateConfigurationFolder(folder, result);
//					}
//

					break;

				case DO_SYNC:
					GWT.log("Performing a do sync using server folder configuration");
					//PERFORM A DO SYNC BUT NOT AT FIRST TIME
					WsThreddsWidget.eventBus.fireEvent(new PerformDoSyncEvent(folder, null));

					break;

				default:
					break;
				}

			}
			@Override
			public void setError(boolean visible, String error) {
				//panelView.setError(visible, error);
				panelView.setInfo(visible, error);
			}
		};

		folderInfo.updateViewToResult(folder, folderDescriptor);

		if(isCreateConfiguration) {
			folderInfo.getMainPanel().setVisible(false);
			folderInfo.setError(true, "This Folder is not configured. Do you want create a configuration?");
			folderInfo.getPager().getLeft().setText("Create Configuration");
		}else {
			//USER CAN PERFORM DO SYNC
			//MOREOVER, HE/SHE COULD UPDATE THE CONFIGURATION BUT IT IS NOT SUPPORTED SERVER-SIDE
			folderInfo.getPager().getLeft().setVisible(false);

			if(folderDescriptor.getServerFolderDescriptor().isLocked()) {
				VerticalPanel v = new VerticalPanel();
				Alert alert = new Alert("Current Folder synchronization is locked by another proccess. Do you want see synchronization status?");
				alert.setClose(true);
				alert.setType(AlertType.INFO);

				Button butt = new Button("Show Status");
				butt.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						WsThreddsWidget.eventBus.fireEvent(new ShowMonitorSyncStatusEvent(folder));
					}
				});

				v.add(alert);
				v.add(butt);
				box.add(v);
			}

		}

		panelView.addViewAsWidget(folderInfo);
		box.add(panelView);
		box.show();

	}

}
