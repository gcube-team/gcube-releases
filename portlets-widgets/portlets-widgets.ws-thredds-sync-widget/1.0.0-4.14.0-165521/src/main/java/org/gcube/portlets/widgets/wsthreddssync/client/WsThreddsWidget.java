package org.gcube.portlets.widgets.wsthreddssync.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portal.wssynclibrary.shared.WorkspaceFolderLocked;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncStatus;
import org.gcube.portlets.widgets.wsthreddssync.client.SyncCompletedNotification.HasWsSyncNotificationListner;
import org.gcube.portlets.widgets.wsthreddssync.client.SyncCompletedNotification.SyncCompletedNotificationListner;
import org.gcube.portlets.widgets.wsthreddssync.client.dialog.DialogConfirm;
import org.gcube.portlets.widgets.wsthreddssync.client.dialog.DialogResult;
import org.gcube.portlets.widgets.wsthreddssync.client.event.PerformDoSyncEvent;
import org.gcube.portlets.widgets.wsthreddssync.client.event.PerformDoSyncEventHandler;
import org.gcube.portlets.widgets.wsthreddssync.client.event.PerformDoUnSyncEvent;
import org.gcube.portlets.widgets.wsthreddssync.client.event.PerformDoUnSyncEventHandler;
import org.gcube.portlets.widgets.wsthreddssync.client.event.ShowMonitorSyncStatusEvent;
import org.gcube.portlets.widgets.wsthreddssync.client.event.ShowMonitorSyncStatusEventHandler;
import org.gcube.portlets.widgets.wsthreddssync.client.rpc.ThreddsWorkspaceSyncServiceAsync;
import org.gcube.portlets.widgets.wsthreddssync.client.view.LoaderIcon;
import org.gcube.portlets.widgets.wsthreddssync.client.view.WsThreddsWidgetViewManager;
import org.gcube.portlets.widgets.wsthreddssync.client.view.binder.MonitorFolderSyncStatusView;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsFolder;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderConfiguration;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderDescriptor;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


// TODO: Auto-generated Javadoc
/**
 * The Class WsThreddsWidget.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 14, 2018
 */
public class WsThreddsWidget implements HasWsSyncNotificationListner {

	/** The ws thredds sync service. */
	public static final ThreddsWorkspaceSyncServiceAsync wsThreddsSyncService = ThreddsWorkspaceSyncServiceAsync.Util.getInstance();

	/** The Constant eventBus. */
	public final static HandlerManager eventBus = new HandlerManager(null);

	private WsThreddsWidgetViewManager viewManager = new WsThreddsWidgetViewManager();

	private final List<SyncCompletedNotificationListner> syncEventsListeners = new ArrayList<SyncCompletedNotificationListner>();

	/**
	 * This is the entry point method.
	 */
	public WsThreddsWidget() {
		bindEvents();

	}

	/**
	 * Bind events.
	 */
	private void bindEvents() {

		eventBus.addHandler(PerformDoSyncEvent.TYPE, new PerformDoSyncEventHandler() {

			@Override
			public void onPerformDoSync(PerformDoSyncEvent performDoSyncEvent) {

				//GWT.log("One PerformDoSyncEvent "+Random.nextDouble());

				if(performDoSyncEvent.getFolder()!=null)

					performFolderSync(performDoSyncEvent.getFolder(), performDoSyncEvent.getConf());
			}
		});

		eventBus.addHandler(ShowMonitorSyncStatusEvent.TYPE, new ShowMonitorSyncStatusEventHandler() {

			@Override
			public void onShowMonitorSyncStatus(ShowMonitorSyncStatusEvent showSyncStatusEvent) {

				if(showSyncStatusEvent.getFolder()!=null)

					viewManager.showMonitorSyncToFolder(showSyncStatusEvent.getFolder(), syncEventsListeners);

			}
		});

		eventBus.addHandler(PerformDoUnSyncEvent.TYPE, new PerformDoUnSyncEventHandler() {

			@Override
			public void onPerformDoUnSync(final PerformDoUnSyncEvent performDoUnSyncEvent) {

				GWT.log("DO UnSync performed on: "+performDoUnSyncEvent.getFolder());

				if(performDoUnSyncEvent.getFolder()!=null){

					final DialogConfirm confirm = new DialogConfirm(null, "Unsync confirm?", "Deleting sync configurations to the folder '"+performDoUnSyncEvent.getFolder().getFoderName()+"'</b><br>Confirm?");

					confirm.getYesButton().addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							confirm.hide();
							performFolderUnSync(performDoUnSyncEvent.getFolder());
						}
					});

					confirm.getElement().getStyle().setZIndex(Integer.MAX_VALUE-1000);
					confirm.center();

				}
			}

		});
	}


	/**
	 * Show sync folder info.
	 *
	 * @param folder the folder
	 * @throws Exception the exception
	 */
	public void showSyncFolderInfo(final WsFolder folder) throws Exception {

		if(folder==null || folder.getFolderId()==null)
			throw new Exception("Invalid parameter folder null");


		MonitorFolderSyncStatusView monitor = viewManager.getMonitor(folder);

		//SHOWING CURRENT ACTIVE MONITOR
		if(monitor!=null) {
			GWT.log("Monitor for folder: "+folder.getFolderId() +" exists showing it..");
			viewManager.showMonitorSyncToFolder(folder, syncEventsListeners);
			return;
		}

		final Modal box = new Modal(true);
		box.setTitle("Checking configurations...");
		LoaderIcon loader = new LoaderIcon("Checking folder configurations...");
		box.add(loader);

		GWT.log("Performing isItemSynched: "+folder.getFolderId());
		WsThreddsWidget.wsThreddsSyncService.isItemSynched(folder.getFolderId(), new AsyncCallback<WsThreddsSynchFolderDescriptor>() {

			@Override
			public void onSuccess(WsThreddsSynchFolderDescriptor result) {
				box.hide();
				//GWT.log("WsThreddsSynchFolderDescriptor result: "+result);
				viewManager.showThreddsFolderInfo(folder, result);

			}

			@Override
			public void onFailure(Throwable caught) {
				box.hide();

				if(caught instanceof WorkspaceFolderLocked){
					viewManager.showMonitorSyncToFolder(folder,syncEventsListeners);
					return;
				}

				viewManager.cancelMonitor(folder);
				// TODO Auto-generated method stub
				Window.alert(caught.getMessage());
			}
		});

		box.show();

	}

	private void performFolderUnSync(final WsFolder folder) {
		GWT.log("Performing doSyncFolder on: "+folder);
		final Modal box = new Modal(true);
		box.setTitle("Deleting...");
		box.hide(false);
		LoaderIcon loader = new LoaderIcon("Deleting sync configurations to the folder: "+folder.getFoderName());
		box.add(loader);

		wsThreddsSyncService.doUnSyncFolder(folder.getFolderId(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				if(box!=null)
					box.hide();

				//CALLING METHOD ON SYNC ERROR TO THE LISTENERS
				for (SyncCompletedNotificationListner myListener : syncEventsListeners) {
					myListener.onSyncError(folder);
				}

			}

			@Override
			public void onSuccess(Boolean result) {
				box.hide();

				if(result){
					DialogResult dlg = new DialogResult(null, "Unsync performed", "Unsync was performed correctly");
					dlg.center();

					//CALLING METHOD UNSYNC PERFORMED COMPLETED TO THE LISTENERS
					for (SyncCompletedNotificationListner myListener : syncEventsListeners) {
						myListener.onUnSyncPerformed(folder);
					}
				}

			}
		});

		box.show();

	}


	/**
	 * Perform folder sync.
	 *
	 * @param folder the folder
	 * @param config the config
	 */
	private void performFolderSync(final WsFolder folder, WsThreddsSynchFolderConfiguration config) {
		GWT.log("Performing doSyncFolder on: "+folder);
		final Modal box = new Modal(true);
		box.setTitle("Starting synchronization...");
		box.hide(false);
		LoaderIcon loader = new LoaderIcon("Inizializiting synchronization to the folder: "+folder.getFoderName());
		box.add(loader);

		wsThreddsSyncService.doSyncFolder(folder.getFolderId(), config, new AsyncCallback<ThSyncStatus>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				viewManager.cancelMonitor(folder);
				if(box!=null)
					box.hide();

			}

			@Override
			public void onSuccess(ThSyncStatus result) {
				if(box!=null)
					box.hide();

				GWT.log("doSyncFolder Updating sync status: "+result);
				viewManager.showMonitorSyncToFolder(folder, syncEventsListeners);

			}
		});
		box.show();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsthreddssync.client.SyncCompletedNotification.HasWsSyncNotificationListner#addSyncCompletedListner(org.gcube.portlets.widgets.wsthreddssync.client.SyncCompletedNotification.SyncCompletedNotificationListner)
	 */
	@Override
	public void addSyncCompletedListner(SyncCompletedNotificationListner listner) {

		syncEventsListeners.add(listner);

	}

}
