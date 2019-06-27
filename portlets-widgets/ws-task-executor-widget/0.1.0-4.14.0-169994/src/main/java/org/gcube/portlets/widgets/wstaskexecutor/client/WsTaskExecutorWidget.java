
package org.gcube.portlets.widgets.wstaskexecutor.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.workspacetaskexecutor.shared.TaskOutput;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameter;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskExecutionStatus;
import org.gcube.common.workspacetaskexecutor.shared.exception.ItemNotConfiguredException;
import org.gcube.common.workspacetaskexecutor.shared.exception.WorkspaceFolderLocked;
import org.gcube.portlets.widgets.wstaskexecutor.client.TaskCompletedNotification.TaskCompletedNotificationListner;
import org.gcube.portlets.widgets.wstaskexecutor.client.dialog.DialogConfirm;
import org.gcube.portlets.widgets.wstaskexecutor.client.dialog.DialogResult;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.CreatedTaskConfigurationEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.CreatedTaskConfigurationEventHandler;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.DeleteConfigurationEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.DeleteConfigurationEventHandler;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.PerformRunTaskEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.PerformRunTaskEventHandler;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.ShowCreateTaskConfigurationDialogEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.ShowCreateTaskConfigurationDialogEvent.Operation;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.ShowCreateTaskConfigurationDialogEventHandler;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.ShowListOfTaskConfigurationsEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.ShowListOfTaskConfigurationsEventHandler;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.TaskComputationFinishedEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.TaskComputationFinishedEventHandler;
import org.gcube.portlets.widgets.wstaskexecutor.client.rpc.WsTaskExecutorWidgetServiceAsync;
import org.gcube.portlets.widgets.wstaskexecutor.client.view.LoaderIcon;
import org.gcube.portlets.widgets.wstaskexecutor.client.view.WsTaskExecutorWidgetViewManager;
import org.gcube.portlets.widgets.wstaskexecutor.client.view.binder.MonitorFolderTaskExecutionStatusView;
import org.gcube.portlets.widgets.wstaskexecutor.shared.SelectableOperator;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public class WsTaskExecutorWidget {

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
		+ "attempting to contact the server. Please check your network "
		+ "connection and try again.";
	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	public static final WsTaskExecutorWidgetServiceAsync wsTaskService = WsTaskExecutorWidgetServiceAsync.Util.getInstance();

	/** The Constant eventBus. */
	public final static HandlerManager eventBus = new HandlerManager(null);

	private WsTaskExecutorWidgetViewManager viewManager = new WsTaskExecutorWidgetViewManager();

	private final List<TaskCompletedNotificationListner> taskEventsListeners = new ArrayList<TaskCompletedNotificationListner>();

	private SelectableOperator selectableOperators = null;

	/**
	 * Instantiates a new ws task executor widget.
	 */
	public WsTaskExecutorWidget() {
		bindEvents();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Bind events.
	 */
	private void bindEvents() {

		eventBus.addHandler(PerformRunTaskEvent.TYPE, new PerformRunTaskEventHandler() {

			@Override
			public void onPerformRunTask(final PerformRunTaskEvent perforRunTaskEvent) {

				try {
					if(perforRunTaskEvent.getWsItem()!=null && perforRunTaskEvent.getConfiguration()!=null){
						String msg = "<div style='font-size:14px; font-weight:bold;'>Executing the task with configuration:</div>";
						msg+="<br/>";
						msg+="Operator Id: <br/>"+perforRunTaskEvent.getConfiguration().getTaskId();
						int cParam = perforRunTaskEvent.getConfiguration().getListParameters().size();
						msg+="<br/><br/>";
						if(cParam>0){
							msg+="With Input ";
							msg+=cParam>1?"Parameters:":"Parameter:";
							msg+="<br/>";
							int i = 0;
							for (TaskParameter param : perforRunTaskEvent.getConfiguration().getListParameters()) {
								msg+=++i+". Type: "+param.getType().getType() +" having "+param.getKey()+" = " +param.getValue();
								msg+="<br/>";
							}
						}
						msg+="<br/>";
						msg+="<br/>";
						msg+="<div style='font-size:14px; font-weight:bold;'>Confirm?</div>";
						final DialogConfirm confirm = new DialogConfirm(null, "Run the task?", msg);

						confirm.getYesButton().addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {

								try {
									performRunTask(perforRunTaskEvent.getWsItem(), perforRunTaskEvent.getConfiguration());
									confirm.hide();
								}
								catch (Exception e) {
									Window.alert(e.getMessage());
								}
							}
						});

						confirm.center();
					}


				}
				catch (Exception e) {
					Window.alert(e.getMessage());
				}
			}
		});

		eventBus.addHandler(ShowListOfTaskConfigurationsEvent.TYPE, new ShowListOfTaskConfigurationsEventHandler() {

			@Override
			public void onShowListOfTaskConfigurations(ShowListOfTaskConfigurationsEvent showListOfTaskConfigurationsEvent) {

				if(showListOfTaskConfigurationsEvent.getWsItem()!=null)
					try {
						showTaskConfigurations(showListOfTaskConfigurationsEvent.getWsItem(), selectableOperators);
					}
					catch (Exception e) {
						Window.alert(e.getMessage());
					}
			}
		});

		eventBus.addHandler(DeleteConfigurationEvent.TYPE, new DeleteConfigurationEventHandler() {

			@Override
			public void onRemoveConfiguration(final DeleteConfigurationEvent dcEvent) {

				//GWT.log("qui remove");

				if(dcEvent.getTaskConf()!=null && dcEvent.getWsItem()!=null){

					WsTaskExecutorWidget.wsTaskService.removeTaskConfiguration(dcEvent.getTaskConf(), new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {

							new DialogResult(null, "Delete Error!", caught.getMessage()).center();

						}

						@Override
						public void onSuccess(Boolean result) {

							try {
								if(result){
									String msg = "Task configuration for Algorithm: "+dcEvent.getTaskConf().getTaskName() +"<br/><br/>removed correctly";
									new DialogResult(null, "Delete performed!",msg).center();
								}
								showTaskConfigurations(dcEvent.getWsItem(), selectableOperators);
							}
							catch (Exception e) {
								Window.alert(e.getMessage());
							}

						}
					});
				}
			}
		});

		eventBus.addHandler(ShowCreateTaskConfigurationDialogEvent.TYPE, new ShowCreateTaskConfigurationDialogEventHandler() {

			@Override
			public void onShowCreateConfiguration(
				ShowCreateTaskConfigurationDialogEvent event) {

				if(event.getWsItem()!=null){

					if(event.getOperation().equals(Operation.CREATE_NEW)){
						viewManager.showCreateTaskConfigurationForFolder(event.getWsItem(), null, selectableOperators);
					}else {
						viewManager.showCreateTaskConfigurationForFolder(event.getWsItem(), event.getTaskConfiguration(), selectableOperators);
					}
				}
			}
		});



		eventBus.addHandler(CreatedTaskConfigurationEvent.TYPE, new CreatedTaskConfigurationEventHandler() {

			@Override
			public void onCreatedConfiguration(
				final CreatedTaskConfigurationEvent createTCE) {

				if(createTCE.getWsItem()!=null && createTCE.getConf()!=null){
					GWT.log("Creating the configuration: "+createTCE.getConf());

					WsTaskExecutorWidget.wsTaskService.createTaskConfiguration(createTCE.getWsItem().getItemId(), createTCE.getConf(), createTCE.isUpdate(), new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {

							Window.alert(caught.getMessage());

						}

						@Override
						public void onSuccess(Boolean result) {

							try {
								showTaskConfigurations(createTCE.getWsItem(), selectableOperators);
							}
							catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
				}
			}
		});

		eventBus.addHandler(TaskComputationFinishedEvent.TYPE, new TaskComputationFinishedEventHandler() {

			@Override
			public void onTaskFinished(
				TaskComputationFinishedEvent taskComputationTerminatedEvent) {

				if(taskComputationTerminatedEvent.getWsItem()!=null){

					GWT.log("Task finished with status "+taskComputationTerminatedEvent.getTaskExecutionStatus()+" cancelling the polling");
					viewManager.cancelMonitor(taskComputationTerminatedEvent.getWsItem());

					if(taskComputationTerminatedEvent.getError()!=null){
						//Exception occurred server-side no output to displaying
						Window.alert(taskComputationTerminatedEvent.getError().getMessage());
						return;
					}


					//CALLING METHOD ON SYNC COMPLETED TO THE LISTENERS
					for (TaskCompletedNotificationListner listener : taskEventsListeners) {
						listener.onTaskComputationCompleted(taskComputationTerminatedEvent.getWsItem());
					}

					//RETRIEVES A VALID STATUS IF THE OUTPUT IS COMPLETED BUT IT IS CALLED ON FAILED AND CANCELLED STATUS IN ORDER TO REMOVE SERVER SIDE CACHED COMPUATION
					WsTaskExecutorWidget.wsTaskService.getOutput(taskComputationTerminatedEvent.getTaskExecutionStatus().getTaskConfiguration(), taskComputationTerminatedEvent.getTaskExecutionStatus().getTaskComputation(), new AsyncCallback<TaskOutput>() {

						@Override
						public void onFailure(Throwable caught) {

							//Window.alert(caught.getMessage());

						}

						@Override
						public void onSuccess(TaskOutput result) {

							if(result==null)
								return;

							List<String> listMessages = result.getOutputMessages();
							String outMsg = "<ul style=\"margin:5px; word-break: break-all;\">";
							for (String msg : listMessages) {
								outMsg+="<li>"+msg+"</li><br/>";
							}
							outMsg += "</ul>";

							final DialogResult dResult = new DialogResult(null, "Computation results are:", outMsg);
							dResult.center();

						}
					});

				}

			}
		});

	}

//	private String addHtmlLink(String text){
//
//		if(text==null || text.isEmpty())
//			return text;
//
//		MatchResult matcher = urlPattern.exec(text);
//		boolean matchFound = matcher != null; // equivalent to regExp.test(inputStr);
//
//		String msgWithHref = text;
//		if (matchFound) {
//		    // Get all groups for this match
//		    for (int i = 0; i < matcher.getGroupCount(); i++) {
//		        String groupStr = matcher.getGroup(i);
//		        System.out.println(groupStr);
//		        msgWithHref= msgWithHref.replace(groupStr, "<a href='"+groupStr+"'>");
//
//		    }
//		}
//
//		GWT.log("Replace html: "+msgWithHref);
//
//		return msgWithHref;
//	}

	// Pattern for recognizing a URL, based off RFC 3986
	private static final RegExp urlPattern = RegExp.compile(
	        "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
	                + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
	                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)");


	/**
	 * Perform run task.
	 *
	 * @param wsItem the folder
	 * @param conf the conf
	 * @throws Exception the exception
	 */
	public void performRunTask(final WSItem wsItem, final TaskConfiguration conf) throws Exception {

		if(wsItem==null || wsItem.getItemId()==null)
			throw new Exception("Invalid parameter workpace item or its id is null");

		if(conf==null)
			throw new Exception("Invalid parameter the configuration is null");

		final Modal box = new Modal(true);
		box.setTitle("Executing task...");

		String algName = conf.getTaskId().substring(conf.getTaskId().lastIndexOf(".")+1, conf.getTaskId().length());
		LoaderIcon loader = new LoaderIcon("Inizializing new run for: "+algName);
		box.add(loader);

		WsTaskExecutorWidget.wsTaskService.executeTheTask(conf, new AsyncCallback<TaskExecutionStatus>() {

			@Override
			public void onFailure(Throwable caught) {
				box.hide();
				new DialogResult(null, "Error on show Task Status for algorithm id:"+conf.getTaskId(), caught.getMessage()).center();
				viewManager.cancelMonitor(wsItem);
			}

			@Override
			public void onSuccess(TaskExecutionStatus result) {
				box.hide();
				viewManager.showMonitorTaskStatusFor(wsItem, result.getTaskConfiguration(), result.getTaskComputation());
			}
		});

		box.show();
	}


	/**
	 * Show task configurations.
	 *
	 * @param wsItem the ws item
	 * @throws Exception the exception
	 */
	public void showTaskConfigurations(final WSItem wsItem, SelectableOperator selectableOperators) throws Exception {
		this.selectableOperators = selectableOperators;

		if(wsItem==null || wsItem.getItemId()==null)
			throw new Exception("Invalid input parameter "+WSItem.class.getSimpleName()+". Its id or itself is null");

		final Modal box = new Modal(true);
		box.setTitle("Checking item configurations...");

		String suffix = wsItem.getItemName()!=null || !wsItem.getItemName().isEmpty()?wsItem.getItemName():wsItem.getItemId();
		LoaderIcon loader = new LoaderIcon("Checking task configurations for item: "+suffix);
		box.add(loader);

		WsTaskExecutorWidget.wsTaskService.loadItem(wsItem.getItemId(), new AsyncCallback<WSItem>() {

			@Override
			public void onFailure(Throwable caught) {
				box.hide();
				Window.alert(caught.getMessage());

			}

			@Override
			public void onSuccess(WSItem result) {
				//no hide monitor
				handleShowMonitor(result,box);

			}
		});

		box.show();

	}


	/**
	 * Handle show monitor.
	 *
	 * @param wsItem the ws item
	 */
	private void handleShowMonitor(final WSItem wsItem, final Modal box){

		MonitorFolderTaskExecutionStatusView monitor = viewManager.getMonitor(wsItem);

		//SHOWING CURRENT ACTIVE MONITOR
		if(monitor!=null) {
			GWT.log("Monitor for workpace item: "+wsItem.getItemId() +" exists showing it..");
			viewManager.showMonitorTaskStatusFor(wsItem, monitor.getTaskConfiguration(), monitor.getTaskComputation());
			return;
		}

//		box.clear();
//		box.setTitle("Checking configurations...");
//
//		String suffix = wsItem.getItemName()!=null || !wsItem.getItemName().isEmpty()?wsItem.getItemName():wsItem.getItemId();
//		LoaderIcon loader = new LoaderIcon("Checking task configurations for item: "+suffix);
//		box.add(loader);

		GWT.log("Performing checkItemTaskConfigurations: "+wsItem.getItemId());

		WsTaskExecutorWidget.wsTaskService.checkItemTaskConfigurations(wsItem.getItemId(), new AsyncCallback<List<TaskConfiguration>>() {

			@Override
			public void onSuccess(List<TaskConfiguration> result) {
				box.hide();

				viewManager.showTaskConfigurationsFolderInfo(wsItem, result, taskEventsListeners);
			}

			@Override
			public void onFailure(Throwable caught) {


				if(caught instanceof ItemNotConfiguredException){
					box.hide();
					viewManager.showCreateTaskConfigurationForFolder(wsItem, null, selectableOperators);

				}else if(caught instanceof WorkspaceFolderLocked){
					VerticalPanel v = new VerticalPanel();
					Alert alert = new Alert("Current Folder is locked by another proccess. Do you want see Task status?");
					alert.setClose(true);
					alert.setType(AlertType.INFO);

					Button butt = new Button("Show Status");
					butt.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							//WsThreddsWidget.eventBus.fireEvent(new ShowMonitorSyncStatusEvent(folder));
						}
					});

					v.add(alert);
					v.add(butt);
					box.add(v);
				}

			}
		});

		//box.show();
	}
}
