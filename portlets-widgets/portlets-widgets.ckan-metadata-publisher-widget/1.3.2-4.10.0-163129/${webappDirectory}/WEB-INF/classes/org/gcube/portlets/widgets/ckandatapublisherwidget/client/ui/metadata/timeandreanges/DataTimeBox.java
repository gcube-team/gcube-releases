package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.metadata.timeandreanges;

import java.util.Date;

import com.github.gwtbootstrap.datepicker.client.ui.DateBox;
import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget for handling date-like fields.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class DataTimeBox extends Composite{

	private static DataTimeBoxUiBinder uiBinder = GWT
			.create(DataTimeBoxUiBinder.class);

	interface DataTimeBoxUiBinder extends UiBinder<Widget, DataTimeBox> {
	}

	public static final String RANGE_SEPARATOR_START_END = "/";
	public static final String MISSING_RANGE_VALUE_START = "MISSING_RANGE_PART_START";
	public static final String MISSING_RANGE_VALUE_END = "MISSING_RANGE_PART_END";
	private static final String COLON = ":";
	private static final String INSERT_DATE_INSTANT_LABEL = "Insert a date";
	private static final String INSERT_TIME_INSTANT_LABEL = "Hour and minutes";
	private static final String INSERT_DATE_START_LABEL = "Insert a start date"; 
	private static final String INSERT_DATE_END_LABEL = "Insert an end date"; 
	private static final DateTimeFormat formatDate = DateTimeFormat.getFormat("yyyy-MM-dd");
	private static final DateTimeFormat formatTime = DateTimeFormat.getFormat("HH:mm");

	private boolean isRange;

	@UiField
	DateBox startRangeDate;
	@UiField
	DateTimeBox startRangeTime;
	@UiField
	DateBox endRangeDate;
	@UiField
	DateTimeBox endRangeTime;
	@UiField
	FlowPanel singleDataEnd;
	@UiField
	FlowPanel singleDataStart;

	public DataTimeBox(boolean isRange) {
		initWidget(uiBinder.createAndBindUi(this));
		this.isRange = isRange;
		startRangeDate.setPlaceholder(INSERT_DATE_INSTANT_LABEL);
		startRangeTime.setPlaceholder(INSERT_TIME_INSTANT_LABEL);
		if(isRange){
			singleDataEnd.setVisible(true);
			singleDataEnd.setWidth("50%");
			singleDataStart.setWidth("50%");

			startRangeTime.setWidth("30%");
			endRangeTime.setWidth("30%");
			startRangeDate.setWidth("60%");
			endRangeDate.setWidth("60%");

			startRangeDate.setPlaceholder(INSERT_DATE_START_LABEL);
			endRangeDate.setPlaceholder(INSERT_DATE_END_LABEL);
			endRangeTime.setPlaceholder(INSERT_TIME_INSTANT_LABEL);
		}
		startRangeDate.setValue(null);
		startRangeTime.setValue(null);
		endRangeDate.setValue(null);
		endRangeTime.setValue(null);
	}

	public boolean getIsRange(){
		return isRange;
	}

	public void setStartDate(String date, String time){
		GWT.log("Date is " + date + " and time is " + time);
		startRangeDate.setValue(new Date(date));
		if(time != null && !time.isEmpty()){
			Date completeDate = new Date();
			completeDate.setHours(Integer.parseInt(time.split(COLON)[0]));
			completeDate.setMinutes(Integer.parseInt(time.split(COLON)[1]));
			startRangeTime.setValue(completeDate);
		}
	}

	public void setEndDate(String date, String time){
		GWT.log("Date is " + date + " and time is " + time);
		endRangeDate.setValue(new Date(date));
		if(time != null && !time.isEmpty()){
			Date completeDate = new Date();
			completeDate.setHours(Integer.parseInt(time.split(COLON)[0]));
			completeDate.setMinutes(Integer.parseInt(time.split(COLON)[1]));
			endRangeTime.setValue(completeDate);
		}
	}

	/**
	 * Return the current value, with MISSING_RANGE_VALUE in case of missing entry
	 * @return
	 */
	public String getCurrentValue(){

		String firstRange = MISSING_RANGE_VALUE_START;
		String secondRange = MISSING_RANGE_VALUE_END;

		if(startRangeDate.getValue() != null){
			firstRange = formatDate.format(startRangeDate.getValue()) + " " + (startRangeTime.getValue() != null ? formatTime.format(startRangeTime.getValue()) : "");;
		}
		if(isRange && endRangeDate.getValue() != null){
			secondRange = formatDate.format(endRangeDate.getValue()) + " " + (endRangeTime.getValue() != null ? formatTime.format(endRangeTime.getValue()) : "");
		}

		if(isRange){
			GWT.log("Returning " + firstRange + RANGE_SEPARATOR_START_END + secondRange);
			return firstRange + RANGE_SEPARATOR_START_END + secondRange;
		}else {
			GWT.log("Returning " + firstRange);
			return firstRange;
		}
	}

	/**
	 * Freeze the inputs
	 */
	public void freeze(){
		startRangeDate.setEnabled(false);
		startRangeTime.setEnabled(false);
		endRangeDate.setEnabled(false);
		endRangeTime.setEnabled(false);
	}
}
