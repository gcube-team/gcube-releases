package org.gcube.portlets.admin.policydefinition.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

public class TimeFormatHelper {
	
	static Logger logger = LoggerFactory.getLogger(TimeFormatHelper.class); 

	/**
	 * the value is {@value #TIME_FORMAT}
	 */
	public static final String TIME_FORMAT = "hhmm";

	/**
	 * the value is {@value #DATE_FORMAT}
	 */
	public static final String DATE_FORMAT = "yyyyMMdd";
	
	/**
	 * the value is {@value #VIEW_DATE_FORMAT}
	 */
	public static final String VIEW_DATE_FORMAT = "dd/MM/yyyy";
	
	/**
	 * the value is {@value #TIME_REGEX}
	 */
	public static final String TIME_REGEX = "([01][0-9]|2[0-3]):([012345][0-9])";
	
	/**
	 * 
	 * @param startTimeTextField
	 * @param endTimeTextField
	 * @return the {@link String} of policy management timeRange parameter or null
	 */
	public static String getTimeRange(TextField startTimeTextField, TextField endTimeTextField){
		logger.debug("Time range: "+startTimeTextField.getValue() + " - "+ endTimeTextField.getValue());
		if(startTimeTextField.getValue() == null || endTimeTextField.getValue() == null
				|| startTimeTextField.getValue().equals("") || endTimeTextField.getValue().equals("")) return null;
		String[] start = startTimeTextField.getValue().toString().split(":");
		String[] end = endTimeTextField.getValue().toString().split(":");
		if(start == null || start.length == 0 || end == null || end.length == 0) return null;
		return start[0] + start[1] + "-" + end[0] + end[1];
	}
	
	/**
	 * Return the {@link String} of policy management dateRange parameter. The format is [{@value #DATE_FORMAT}-{@value #DATE_FORMAT}]
	 * @param startPopupDateField
	 * @param endPopupDateField
	 * @return the {@link String} of policy management dateRange parameter or null
	 */
	public static String getDateRange(PopupDateField startPopupDateField, PopupDateField endPopupDateField){
		logger.debug("Date range: "+startPopupDateField.getValue() + " - "+ endPopupDateField.getValue());
		if(startPopupDateField.getValue() == null || endPopupDateField.getValue() == null) return null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		return dateFormat.format((Date)startPopupDateField.getValue())+"-"+dateFormat.format((Date)endPopupDateField.getValue());
	}
}
