package org.gcube.portlets.widgets.wstaskexecutor.client.view.binder;


import org.gcube.common.workspacetaskexecutor.shared.TaskStatus;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskComputation;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskExecutionStatus;
import org.gcube.portlets.widgets.wstaskexecutor.client.DateFormatterUtil;
import org.gcube.portlets.widgets.wstaskexecutor.client.HTML5StorageUtil;
import org.gcube.portlets.widgets.wstaskexecutor.client.view.LoaderIcon;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Pager;
import com.github.gwtbootstrap.client.ui.ProgressBar;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.github.gwtbootstrap.client.ui.constants.ResizeType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;



// TODO: Auto-generated Javadoc
/**
 * The Class ShowThreddsFolderInfoView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 15, 2018
 */
public abstract class MonitorFolderTaskExecutionStatusView extends Composite {

	//private static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	/**
	 *
	 */
	private static final String KEY_LOCAL_STORAGE_COMP_MSG_HISTORY = "comp_msg_history";

	public static final String PROCESSING_STATE = "Processing state: ";

	private static final String EMPTY = "EMPTY";

	private static final String NEW_STATUS_CHARS = "******* ";

	private static final String NEW_LINE = "\n";

	private static final String NEW_LOG = NEW_STATUS_CHARS;

	private static final String DASH = "-";

	private static final String START_SECTION = DASH+DASH;

	private static final String NEW_SECTION = NEW_LINE+NEW_LINE+DASH+DASH;

	public static int maxLogSize = 10000;

	/** The ui binder. */
	private static MonitorFolderTaskExecutionStatusViewUiBinder uiBinder =
		GWT.create(MonitorFolderTaskExecutionStatusViewUiBinder.class);

	/**
	 * The Interface ShowThreddsFolderInfoViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 15, 2018
	 */
	interface MonitorFolderTaskExecutionStatusViewUiBinder
		extends UiBinder<Widget, MonitorFolderTaskExecutionStatusView> {
	}


	@UiField
	Pager pager;

	@UiField
	ProgressBar progress_percentage;

	@UiField
	TextArea field_current_message;

	@UiField
	TextArea field_history_messages;

	@UiField
	TextArea field_computation_info;

	@UiField
	HTMLPanel field_loader;

	@UiField
	Form field_form;

	@UiField
	HorizontalPanel field_times;

	private WSItem wsItem;


	/** The scheduler time. */
	private Timer schedulerTime;

	private TaskConfiguration taskConfiguration;

	private TaskComputation taskComputation;

	private HTML5StorageUtil storageUtil = new HTML5StorageUtil();


	/**
	 * Submit handler.
	 */
	public abstract void closetHandler();

	/**
	 * Sets the error.
	 *
	 * @param visible the visible
	 * @param error the error
	 */
	public abstract void setError(boolean visible, String error);

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public MonitorFolderTaskExecutionStatusView(TaskConfiguration conf, TaskComputation comp) {
		this.taskConfiguration = conf;
		this.taskComputation = comp;

		initWidget(uiBinder.createAndBindUi(this));

		pager.getLeft().setVisible(false);

		pager.getRight().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GWT.log("Close Monitor Click");
				setError(false, "");
				closetHandler();

			}
		});

		pager.getRight().setVisible(false);

		field_history_messages.setHeight("165px");
		field_history_messages.setResize(ResizeType.BOTH);

		field_computation_info.setHeight("165px");
		field_computation_info.setResize(ResizeType.BOTH);

		field_current_message.setResize(ResizeType.BOTH);
	}


	/**
	 * Update status view.
	 *
	 * @param wsItem the folder
	 * @param taskExecutionStatus the sync status
	 */
	public void updateStatusView(WSItem wsItem, TaskExecutionStatus taskExecutionStatus) {
		this.wsItem = wsItem;

		field_loader.clear();
		LoaderIcon loader = new LoaderIcon("Waiting...");
		field_loader.add(loader);

		this.wsItem = wsItem;
		this.field_current_message.setValue(EMPTY);
		//setFieldValue(this.field_number_error, UNKNOWN);
//		setFieldValue(this.field_queued_items, UNKNOWN);
//		setFieldValue(this.field_transferred_items, UNKNOWN);
		this.field_history_messages.setValue(EMPTY);

		if(wsItem==null || taskExecutionStatus==null || taskExecutionStatus.getStatus()==null) {
			//setError(true, "Sync status error: either folder does not exist or the status is null");
			GWT.log("Sync status error: either folder does not exist or the status is null");
			return;
		}

		String startTime = DateFormatterUtil.getDateTimeToString(taskExecutionStatus.getTaskComputation().getStartTime());
		field_times.clear();
		field_times.getElement().getStyle().setMarginTop(5, Unit.PX);
		field_times.getElement().getStyle().setMarginBottom(5, Unit.PX);
		field_times.setWidth("100%");
		Label l1 = new Label("Start: "+startTime);
		l1.setType(LabelType.INFO);
		field_times.add(l1);

		Label endTimeLabel =  new Label();
		if(taskExecutionStatus.getTaskComputation().getEndTime()!=null){
			String endTime = DateFormatterUtil.getDateTimeToString(taskExecutionStatus.getTaskComputation().getEndTime());
			//field_end_time.clear();
			endTimeLabel.setText("End: "+endTime);
			endTimeLabel.setType(LabelType.INFO);
			endTimeLabel.getElement().getStyle().setFloat(Float.RIGHT);
			field_times.add(endTimeLabel);
		}

		if(taskExecutionStatus.getPercentCompleted()>=0){
			float perc = taskExecutionStatus.getPercentCompleted();
			progress_percentage.setPercent((int)perc);
		}

		if(taskExecutionStatus.getStatus()!=null)

			if(taskExecutionStatus.getStatus()!=null) {
				loader.setText(taskExecutionStatus.getStatus().toString());

				switch (taskExecutionStatus.getStatus()) {
				case INITIALIZING:
					field_loader.clear();
					field_loader.add(new LoaderIcon(PROCESSING_STATE+TaskStatus.INITIALIZING.toString()));
					break;
				case ONGOING:
					field_loader.clear();
					field_loader.add(new LoaderIcon(PROCESSING_STATE+TaskStatus.ONGOING.toString()));
					break;
				case ACCEPTED:
					field_loader.clear();
					field_loader.add(new LoaderIcon(PROCESSING_STATE+TaskStatus.ACCEPTED.toString()));
					break;
				case COMPLETED:
					field_loader.clear();
					Alert alert = new Alert("Task " +TaskStatus.COMPLETED.toString());
					alert.setClose(false);
					alert.setType(AlertType.SUCCESS);
					field_loader.add(alert);
					if(endTimeLabel!=null)
					endTimeLabel.setType(LabelType.SUCCESS);
					break;
				case CANCELLED:
					field_loader.clear();
					Alert alert1 = new Alert("Transferring " +TaskStatus.CANCELLED.toString());
					alert1.setClose(false);
					alert1.setType(AlertType.WARNING);
					field_loader.add(alert1);
					endTimeLabel.setType(LabelType.WARNING);
					//progress_percentage.setVisible(false);
					break;
				case FAILED:
					field_loader.clear();
					Alert alert2 = new Alert(PROCESSING_STATE +TaskStatus.FAILED.toString());
					alert2.setClose(false);
					alert2.setType(AlertType.ERROR);
					field_loader.add(alert2);
					endTimeLabel.setType(LabelType.WARNING);
					break;

				default:
					break;
				}
			}
		//Duration.currentTimeMillis()
		//first time adding the configurations
		String confs=START_SECTION+"Operator Id: "+NEW_LINE+taskExecutionStatus.getTaskComputation().getOperatorId();
		confs+=NEW_SECTION+"Operator Name: "+NEW_LINE+taskExecutionStatus.getTaskComputation().getOperatorName();
		confs+=NEW_SECTION+"EquivalentRequest: "+NEW_LINE+taskExecutionStatus.getTaskComputation().getEquivalentRequest();
		confs+=NEW_SECTION+"URL ID: "+NEW_LINE+taskExecutionStatus.getTaskComputation().getUrlId();
		this.field_computation_info.setValue(confs);

		String msgHistory = NEW_LINE+NEW_LOG+DateFormatterUtil.getDateTimeToString(null)+NEW_LINE;
		msgHistory += taskExecutionStatus.getMessage();
		msgHistory += NEW_LINE;
		msgHistory += storageUtil.getItem(KEY_LOCAL_STORAGE_COMP_MSG_HISTORY); //adding also history
		this.field_history_messages.setValue(msgHistory);
		storageUtil.setItem(KEY_LOCAL_STORAGE_COMP_MSG_HISTORY, msgHistory);
	}



	/**
	 * @return the taskConfiguration
	 */
	public TaskConfiguration getTaskConfiguration() {

		return taskConfiguration;
	}


	/**
	 * @return the taskComputation
	 */
	public TaskComputation getTaskComputation() {

		return taskComputation;
	}


	/**
	 * Sets the field value.
	 *
	 * @param box the box
	 * @param value the value
	 */
	private void setFieldValue(TextBox box, String value) {
		box.setValue(value);
	}


	/**
	 * Validate submit.
	 *
	 * @return true, if successful
	 */
	protected boolean validateSubmit() {

		return true;
	}


	/**
	 * Sets the scheduler.
	 *
	 * @param schedulingTimer the new scheduler
	 */
	public void setScheduler(Timer schedulingTimer) {
		this.schedulerTime = schedulingTimer;
	}

	/**
	 * Gets the scheduling time.
	 *
	 * @return the scheduling time
	 */
	public Timer getSchedulerTime() {
		return schedulerTime;
	}
}
