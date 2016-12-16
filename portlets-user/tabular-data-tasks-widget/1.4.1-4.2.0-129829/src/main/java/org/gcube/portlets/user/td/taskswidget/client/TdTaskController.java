/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client;

import java.util.List;

import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.taskswidget.client.event.OpenResultEvent;
import org.gcube.portlets.user.td.taskswidget.client.event.OpenResultEventHandler;
import org.gcube.portlets.user.td.taskswidget.client.event.SeeMoreTask;
import org.gcube.portlets.user.td.taskswidget.client.event.SeeMoreTaskEvent;
import org.gcube.portlets.user.td.taskswidget.client.manager.TasksCentralManager;
import org.gcube.portlets.user.td.taskswidget.client.panel.TdTaskManagerMainPanel;
import org.gcube.portlets.user.td.taskswidget.client.rpc.TdTasksWidgetService;
import org.gcube.portlets.user.td.taskswidget.client.rpc.TdTasksWidgetServiceAsync;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.TasksMonitorEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.TaskType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 12, 2013
 * 
 * Singleton Controller
 * 
 */
public class TdTaskController {
	
	
	public static final TdTasksWidgetServiceAsync tdTaskService = (TdTasksWidgetServiceAsync) GWT.create(TdTasksWidgetService.class);

	private static TdTaskController instance = null;
	
	private static EventBus privateTaskBus = new SimpleEventBus();
	
	private static EventBus commonBus = null;
	
	private TdTaskManagerMainPanel mainPanel = new TdTaskManagerMainPanel(0, ConstantsTdTasks.PAGE_SIZE);
	
	private TasksCentralManager taskCentralManager;
	
	private Window win;
	
	private DialogBox dialogoBox;
	
	/**
	 * @param privateTaskBus
	 */
	private TdTaskController() {
		taskCentralManager = new TasksCentralManager(mainPanel);
		bindPrivateEvents();
	}


	public static synchronized TdTaskController getInstance() {
		if (instance == null)
			instance = new TdTaskController();
		return instance;
	}
	
	/**
	 * 
	 * @param bus
	 */
	public static void bindCommonBus(EventBus bus){
		commonBus = bus;
		bindCommonEvents();
	}
	

	
	private static void bindCommonEvents() {

//		commonBus.addHandler(UIStateEvent.TYPE, new UIStateEvent.UIStateHandler() {
//			
//			@Override
//			public void onUIState(UIStateEvent event) {
//			
//				if(event.getUIStateType().equals(UIStateType.TR_OPEN)){
//					getInstance().updateTasks(true);
//				}
//				
//			}
//		});
		
	}

	private void bindPrivateEvents(){
		
		privateTaskBus.addHandler(SeeMoreTaskEvent.TYPE, new SeeMoreTask() {
			
			@Override
			public void onSeeMoreTask(SeeMoreTaskEvent seeMoreTask) {
				
				if(seeMoreTask.getStart()>0)
					updateTasks(seeMoreTask.getStart(), ConstantsTdTasks.PAGE_SIZE, false);
				
			}
		});
		
		privateTaskBus.addHandler(OpenResultEvent.TYPE, new OpenResultEventHandler() {
			
			@Override
			public void onResultOpenSelect(OpenResultEvent openResultEvent) {
//				System.out.println("openResultEvent: "+openResultEvent);
				
				if(openResultEvent.getTrId()!=null){
					if(openResultEvent.equals(OpenResultEvent.ResultType.TABULARTABLE)){
						fireTaskMonitorEvent(TaskType.OPENTABLE, openResultEvent.getTrId());
					}
					else{
						fireTaskMonitorEvent(TaskType.OPENCOLLATERALTABLE, openResultEvent.getTrId());
					}
				}
			}
		});
	}
	
	
	
	private void fireTaskMonitorEvent(TaskType type, TRId trId){
		
		if(type!=null && commonBus!=null && trId.getId()!=null && trId.getTableId()!=null){
			
			if(trId.getId()!=null && trId.getTableId()!=null && !trId.getId().isEmpty() && !trId.getTableId().isEmpty()){
				TasksMonitorEvent event = new TasksMonitorEvent(type, trId);
				commonBus.fireEvent(event);
//				GWT.log("fired event TasksMonitorEvent with parameters: "+type + " TRId: "+trId);
			}
		}
		
	}
	
	
	/**
	 * 
	 */
	protected void go(final HasWidgets rootPanel) {

		updateTasks(0, ConstantsTdTasks.PAGE_SIZE, false);
		rootPanel.add(taskCentralManager.getFilledTaskMangerMainPanel());
	}
	
	public static TdTasksWidgetServiceAsync getTdTaskService() {
		return tdTaskService;
	}


	public static EventBus getInternalBus() {
		return privateTaskBus;
	}
	
	public void updateTasks(boolean resetTaskView){
		updateTasks(0, ConstantsTdTasks.PAGE_SIZE, resetTaskView);
	}
	
	//TODO
	protected void updateTasks(int start, int limit, boolean resetTasksView){
		
		taskCentralManager.maskMainPanel(true);
		
		if(resetTasksView)
			taskCentralManager.reset();
	

		tdTaskService.getTdTasks(start, limit, resetTasksView, new AsyncCallback<List<TdTaskModel>>() {
			
			@Override
			public void onSuccess(List<TdTaskModel> result) {
				
				if(result!=null){
					taskCentralManager.addListTask(result);
				}
				else
					Info.display("Sorry the current list of tasks is empty, try again later", null);
				
//				taskCentralManager.getFilledTaskMangerMainPanel().layout(true);
				
				taskCentralManager.maskMainPanel(false);
//				mainPanel.layout(true);
				
				if(win!=null)
					win.layout(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				taskCentralManager.maskMainPanel(false);
				if (caught instanceof TDGWTSessionExpiredException) {
					commonBus.fireEvent(new SessionExpiredEvent(
							SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						GWT.log(caught.getLocalizedMessage());
						MessageBox.alert("Error Locked",
								caught.getLocalizedMessage(),null);
					} else {
						if (caught instanceof TDGWTIsFinalException) {
							GWT.log(caught
									.getLocalizedMessage());
							MessageBox.alert("Error Final",
											caught.getLocalizedMessage(),null);
						} else {
							GWT.log("Error in getTdTasks : "
									+ caught.getLocalizedMessage());
							MessageBox.alert("Error",
											"Sorry an error occurred on recovering tasks from service, try again later!",null);
						}
					}
				}

//				MessageBox.alert("Error", "Sorry an error occurred on contacting the service, try again later", null);
				
			}
		});
	}


	public TdTaskManagerMainPanel getMainPanel(int width, int height) {
		mainPanel.setWidth(width);
		mainPanel.setHeight(height);
		return mainPanel;
	}
	
	public TdTaskManagerMainPanel getMainPanel(int height) {
		mainPanel.setHeight(height);
		return mainPanel;
	}
	
	public TdTaskManagerMainPanel getMainPanel() {
		return mainPanel;
	}
	
	/**
	 * Used for GWT
	 * @return
	 */
	public DialogBox getDialogBoxTaskMonitor() {
		dialogoBox = new DialogBox();
		int width = ConstantsTdTasks.MAINWIDTH+15;
		int height = ConstantsTdTasks.MAINHEIGHT+15;
		dialogoBox.setSize(width+"", height+"");
		
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(2);
		
		dialogContents.add(mainPanel);
		// Add a close button at the bottom of the dialog
	    Button closeButton = new Button("Close", new ClickHandler() {
          public void onClick(ClickEvent event) {
        	  dialogoBox.hide();
          }
	    });
	    dialogContents.add(closeButton);
	    
	    dialogoBox.add(dialogContents);
	    
		return dialogoBox;
	}
	
	/**
	 * Used for GXT 2.5
	 * @return
	 */
	public Window getWindowTaskMonitor() {
//		updateTasks(0, ConstantsTdTasks.PAGE_SIZE, true);
	
		win = new Window();
		win.setHeading("Timeline Task Monitor");
		int width = ConstantsTdTasks.MAINWIDTH+15;
		int height = ConstantsTdTasks.MAINHEIGHT+40;
		win.setSize(width, height);
	    win.add(mainPanel);
	    win.setBottomComponent(mainPanel.getPagingToolbar());
	    win.setResizable(false);
		return win;
	}


	public static EventBus getCommonBus() {
		return commonBus;
	}
	
	public void setDebug(final String tabularResourceId){
		
		if(tabularResourceId==null || tabularResourceId.isEmpty())
			return;
		
		tdTaskService.setDegubTabularResource(true, tabularResourceId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(Void arg0) {
				GWT.log("debugging: "+tabularResourceId);
				
			}
		});
	}
	
//	//TODO
//	public void updateResults(){
//		
//	}
}
