package org.gcube.portlets.widgets.wstaskexecutor.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.workspacetaskexecutor.shared.TaskStatus;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskComputation;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskExecutionStatus;
import org.gcube.portlets.widgets.wstaskexecutor.client.TaskCompletedNotification.TaskCompletedNotificationListner;
import org.gcube.portlets.widgets.wstaskexecutor.client.WsTaskExecutorWidget;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.CreatedTaskConfigurationEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.DeleteConfigurationEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.TaskComputationFinishedEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.view.binder.AbstractViewDialogBox;
import org.gcube.portlets.widgets.wstaskexecutor.client.view.binder.CreateTaskConfigurationView;
import org.gcube.portlets.widgets.wstaskexecutor.client.view.binder.MonitorFolderTaskExecutionStatusView;
import org.gcube.portlets.widgets.wstaskexecutor.client.view.binder.ShowTaskConfigurationsView;
import org.gcube.portlets.widgets.wstaskexecutor.shared.SelectableOperator;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class WsTaskExecutorWidgetViewManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public class WsTaskExecutorWidgetViewManager {

	private Map<String, MonitorFolderTaskExecutionStatusView> mapMonitor = new HashMap<String, MonitorFolderTaskExecutionStatusView>();

	/**
	 * Cancel scheduler.
	 *
	 * @param wsItem the ws item
	 * @return the monitor folder task execution status view
	 */
	private MonitorFolderTaskExecutionStatusView cancelScheduler(final WSItem wsItem) {

		MonitorFolderTaskExecutionStatusView monitor = getMonitor(wsItem);
		if(monitor.getSchedulerTime()!=null) {
			GWT.log("Cancelling scheduler time on item: "+wsItem.getItemId());
			monitor.getSchedulerTime().cancel();
		}

		return monitor;

	}


	/**
	 * Cancel monitor.
	 *
	 * @param wsItem the ws item
	 */
	public void cancelMonitor(WSItem wsItem) {
		try {
			MonitorFolderTaskExecutionStatusView monitor = cancelScheduler(wsItem);
			if(monitor!=null) {
				GWT.log("Removed monitor for item: "+wsItem.getItemId());
				mapMonitor.remove(wsItem.getItemId());
			}
		}catch (Exception e) {
			GWT.log("Error on removing map monitor for id: "+wsItem.getItemId());
		}
	}


	/**
	 * Gets the monitor.
	 *
	 * @param wsItem the ws item
	 * @return the monitor
	 */
	public MonitorFolderTaskExecutionStatusView getMonitor(WSItem wsItem){

		if(wsItem==null)
			return null;

		return mapMonitor.get(wsItem.getItemId());
	}


	/**
	 * Save monitor.
	 *
	 * @param wsItem the ws item
	 * @param monitor the monitor
	 */
	private void saveMonitor(WSItem wsItem, MonitorFolderTaskExecutionStatusView monitor) {
		GWT.log("Saving monitor for folder: "+wsItem.getItemId());
		mapMonitor.put(wsItem.getItemId(), monitor);
	}


	/**
	 * Show monitor task status for.
	 *
	 * @param wsItem the ws item
	 * @param configuration the configuration
	 * @param taskComputation the task computation
	 */
	public void showMonitorTaskStatusFor(
		final WSItem wsItem,
		final TaskConfiguration configuration, final TaskComputation taskComputation) {

		GWT.log("Show Monitor TaskStatus for itemId: "+wsItem.getItemId());

		final Modal box = new Modal(true);
		box.addStyleName("ws-task-modal-body");
		box.setTitle("Monitor Task Execution on: "+FormatUtil.getFolderTitle(wsItem.getItemName(), 20));
		box.setWidth(800);
		box.hide(false);

		MonitorFolderTaskExecutionStatusView monitorView = getMonitor(wsItem);
		GWT.log("monitorView is: "+monitorView);

		final AbstractViewDialogBox panelView = new AbstractViewDialogBox() {

			@Override
			public void closeHandler() {
				//cancelScheduler(folder);
				box.hide();

			}

			@Override
			public void confirmHandler(CONFIRM_VALUE confirm, Command command) {

				// TODO Auto-generated method stub

			}
		};

		if(monitorView==null) {

			monitorView = new MonitorFolderTaskExecutionStatusView(configuration, taskComputation) {

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
			final MonitorFolderTaskExecutionStatusView currentMonitor = monitorView;

			Timer schedulingTimer = new Timer() {

				@Override
				public void run() {

					WsTaskExecutorWidget.wsTaskService.monitorTaskExecutionStatus(configuration, taskComputation, new AsyncCallback<TaskExecutionStatus>() {

						@Override
						public void onFailure(Throwable caught) {
							//cancelMonitor(wsItem);
							WsTaskExecutorWidget.eventBus.fireEvent(new TaskComputationFinishedEvent(wsItem, null, caught));

//							//CALLING METHOD ON SYNC ERROR TO THE LISTENERS
//							for (TaskCompletedNotificationListner listener : taskEventsListeners) {
//								listener.onTaskComputationError(wsItem);
//							}
						}

						@Override
						public void onSuccess(TaskExecutionStatus status) {
							GWT.log("monitorSyncStatus: "+status);
							if(status==null) {
								GWT.log("The status is null server-side, cancelling polling");
								cancelMonitor(wsItem);
							}

							currentMonitor.updateStatusView(wsItem, status);

							if(status!=null) {

								if(status.getStatus().equals(TaskStatus.CANCELLED) ||
												status.getStatus().equals(TaskStatus.COMPLETED) ||
												status.getStatus().equals(TaskStatus.FAILED)){

									WsTaskExecutorWidget.eventBus.fireEvent(new TaskComputationFinishedEvent(wsItem, status, null));


//									GWT.log("Sync completed cancel the polling: "+status);
//									cancelMonitor(wsItem);
//
//									//CALLING METHOD ON SYNC COMPLETED TO THE LISTENERS
//									for (TaskCompletedNotificationListner listener : taskEventsListeners) {
//										listener.onTaskComputationCompleted(wsItem);
//									}
								}

							}
						}
					});
				}
			};

			schedulingTimer.scheduleRepeating(2000);
			currentMonitor.setScheduler(schedulingTimer);
			saveMonitor(wsItem, currentMonitor);

		}

		panelView.addViewAsWidget(monitorView);
		box.add(panelView);
		box.show();

	}


	/**
	 * Show task configurations folder info.
	 *
	 * @param wsItem the ws item
	 * @param listTaskConfigurations the list task configurations
	 * @param taskEventsListeners the task events listeners
	 */
	public void showTaskConfigurationsFolderInfo(WSItem wsItem, final List<TaskConfiguration> listTaskConfigurations,  final List<TaskCompletedNotificationListner> taskEventsListeners) {
		final Modal box = new Modal(true);
		//box.setWidth(AbstractViewDialogBox.DEFAULT_WIDTH+20+"px");
		box.setTitle("Task Configurations created for: "+FormatUtil.getFolderTitle(wsItem.getItemName(), 20));
		box.addStyleName("ws-task-modal-body");
		box.setWidth(AbstractViewDialogBox.DEFAULT_WIDTH+50);

		final AbstractViewDialogBox panelView = new AbstractViewDialogBox() {

			@Override
			public void closeHandler() {
				box.hide();

			}

			@Override
			public void confirmHandler(CONFIRM_VALUE confirm, Command command) {

				if(confirm.equals(CONFIRM_VALUE.YES)){
					box.hide();
				}

				if(command!=null)
					command.execute();

			}
		};


		ShowTaskConfigurationsView panelConfigs = new ShowTaskConfigurationsView(wsItem, listTaskConfigurations){

			@Override
			public void submitHandler() {

				//Fired by Command Yes
				WsTaskExecutorWidget.eventBus.fireEvent(new DeleteConfigurationEvent(getWsItem(), getSelectedConfiguration()));

			}

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


		panelView.addViewAsWidget(panelConfigs);
		box.add(panelView);
		box.show();

	}


	/**
	 * Show create task configuration for folder.
	 *
	 * @param wsItem the ws item
	 * @param conf the conf
	 */
	public void showCreateTaskConfigurationForFolder(final WSItem wsItem, TaskConfiguration conf, SelectableOperator selectableOperator){

		final Modal box = new Modal(true);
		//box.setWidth(AbstractViewDialogBox.DEFAULT_WIDTH+20+"px");
		box.setTitle("Create Task Configuration for: "+FormatUtil.getFolderTitle(wsItem.getItemName(), 20));
		box.addStyleName("ws-task-modal-body");
		box.setWidth(AbstractViewDialogBox.DEFAULT_WIDTH+50);
		//box.getElement().getStyle().setZIndex(10000);

		final AbstractViewDialogBox panelView = new AbstractViewDialogBox() {

			@Override
			public void closeHandler() {
				box.hide();

			}

			@Override
			public void confirmHandler(CONFIRM_VALUE confirm, Command command) {

				if(confirm.equals(CONFIRM_VALUE.YES)){
					box.hide();
				}

				if(command!=null)
					command.execute();

			}
		};

		CreateTaskConfigurationView createTaskConfiguration = new CreateTaskConfigurationView(wsItem, conf, selectableOperator) {

			@Override
			public void submitHandler() {
				panelView.closeHandler();
				TaskConfiguration conf = new TaskConfiguration();
				conf.setScope(getSelectedScope().getScopeName());
				conf.setTaskId(getTaskId());
				conf.setTaskName(getTaskName());
				conf.setTaskDescription(getDescription());
				conf.setWorkspaceItemId(wsItem.getItemId());
				conf.setListParameters(getParameters());

				boolean isUpdate = this.isEditConfiguration() && this.getEditConfiguration()!=null;
				if(isUpdate){
					conf.setConfigurationKey(this.getEditConfiguration().getConfigurationKey());
				}

				WsTaskExecutorWidget.eventBus.fireEvent(new CreatedTaskConfigurationEvent(wsItem, conf, isUpdate));
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

		box.addHideHandler(new HideHandler() {

			@Override
			public void onHide(HideEvent hideEvent) {

				//WsTaskExecutorWidget.eventBus.fireEvent(new ShowListOfTaskConfigurationsEvent(wsItem));
			}
		});

		panelView.addViewAsWidget(createTaskConfiguration);
		box.add(panelView);
		box.show();



	}



}
