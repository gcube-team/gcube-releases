package org.gcube.portlets.widgets.wsthreddssync.client.view.binder;


import org.gcube.portal.wssynclibrary.shared.thredds.Status;
import org.gcube.portal.wssynclibrary.shared.thredds.ThProcessStatus;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncStatus;
import org.gcube.portlets.widgets.wsthreddssync.client.view.LoaderIcon;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsFolder;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Pager;
import com.github.gwtbootstrap.client.ui.ProgressBar;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ResizeType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;



// TODO: Auto-generated Javadoc
/**
 * The Class ShowThreddsFolderInfoView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 15, 2018
 */
public abstract class MonitorFolderSyncStatusView extends Composite {

	//private static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


	public static final String TRANSFERRING_STATE = "Transferring state: ";

	private static final String UNKNOWN = "UNKNOWN";

	/** The ui binder. */
	private static MonitorFolderSyncStatusViewUiBinder uiBinder =
		GWT.create(MonitorFolderSyncStatusViewUiBinder.class);

	/**
	 * The Interface ShowThreddsFolderInfoViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 15, 2018
	 */
	interface MonitorFolderSyncStatusViewUiBinder
		extends UiBinder<Widget, MonitorFolderSyncStatusView> {
	}


	@UiField
	Pager pager;

	@UiField
	ProgressBar progress_percentage;


	@UiField
	TextArea field_current_message;

	@UiField
	TextBox field_queued_items;


	@UiField
	TextBox field_transferred_items;


	@UiField
	TextBox field_number_error;

	@UiField
	TextArea field_history_messages;

	@UiField
	HTMLPanel form_monitor_thredds_transfer;

	@UiField
	HTMLPanel field_loader;

	@UiField
	Form field_form;

	private WsFolder folder;


	/** The scheduler time. */
	private Timer schedulerTime;


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
	public MonitorFolderSyncStatusView() {

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

		field_history_messages.setHeight("200px");
		field_history_messages.setResize(ResizeType.BOTH);
		field_current_message.setHeight("80px");
		field_current_message.setResize(ResizeType.BOTH);

//		field_queued_items.addStyleName("myLittleWidth");
//		field_number_error.addStyleName("myLittleWidth");
//		field_transferred_items.addStyleName("myLittleWidth");

//		field_current_message.addStyleName("textAreaWidth");
//		field_history_messages.addStyleName("textAreaWidth");

//		field_form.addStyleName("myFormWidth");
	}


	/**
	 * Update status view.
	 *
	 * @param folder the folder
	 * @param syncStatus the sync status
	 */
	public void updateStatusView(WsFolder folder, ThSyncStatus syncStatus) {

		field_loader.clear();
		LoaderIcon loader = new LoaderIcon("Waiting...");
		field_loader.add(loader);

		this.folder = folder;
		this.field_current_message.setValue(UNKNOWN);
		setFieldValue(this.field_number_error, UNKNOWN);
		setFieldValue(this.field_queued_items, UNKNOWN);
		setFieldValue(this.field_transferred_items, UNKNOWN);
		this.field_history_messages.setValue(UNKNOWN);

		if(folder==null || syncStatus==null || syncStatus.getProcessStatus()==null) {
			//setError(true, "Sync status error: either folder does not exist or the status is null");
			GWT.log("Sync status error: either folder does not exist or the status is null");
			return;
		}

		ThProcessStatus process = syncStatus.getProcessStatus();

		if(process.getPercentCompleted()>=0){
			float perc = process.getPercentCompleted()*100;
			progress_percentage.setPercent((int)perc);
		}

		if(process.getStatus()!=null)

			if(syncStatus.getProcessStatus().getStatus()!=null) {
				loader.setText(syncStatus.getProcessStatus().getStatus().toString());

				switch (syncStatus.getProcessStatus().getStatus()) {
				case INITIALIZING:
					field_loader.clear();
					field_loader.add(new LoaderIcon(TRANSFERRING_STATE+Status.INITIALIZING.toString()));
					break;

				case ONGOING:
					field_loader.clear();
					field_loader.add(new LoaderIcon(TRANSFERRING_STATE+Status.ONGOING.toString()));
					break;
				case WARNINGS:
					field_loader.clear();
					field_loader.add(new LoaderIcon(TRANSFERRING_STATE+Status.WARNINGS.toString()));
					break;
				case COMPLETED:
					field_loader.clear();
					Alert alert = new Alert("Transferring " +Status.COMPLETED.toString());
					alert.setClose(false);
					alert.setType(AlertType.SUCCESS);
					field_loader.add(alert);
					break;

				case STOPPED:
					field_loader.clear();
					Alert alert1 = new Alert("Transferring " +Status.STOPPED.toString());
					alert1.setClose(false);
					alert1.setType(AlertType.WARNING);
					field_loader.add(alert1);
					//progress_percentage.setVisible(false);
					break;

				default:
					break;
				}
			}

		if(process.getCurrentMessage()!=null)
			this.field_current_message.setValue(process.getCurrentMessage());

		if(process.getErrorCount()!=null)
			this.field_number_error.setValue(process.getErrorCount().toString());


		if(process.getQueuedTransfers()!=null)
			this.field_queued_items.setValue(process.getQueuedTransfers().toString());

		if(process.getServedTransfers()!=null)
			this.field_transferred_items.setValue(process.getServedTransfers().toString());

		if(process.getLogBuilder()!=null)
			this.field_history_messages.setValue(process.getLogBuilder().toString());

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
