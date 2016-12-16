package org.gcube.portlets.admin.policydefinition.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.vaadin.ui.Table;

public class PresentationHelper {

	static final String SEPARATOR = ":";
	
	/**
	 * Return the word "policy" in plural form or not.
	 * @param table
	 * @return
	 */
	public static String getPolicyNoun(Table table){
		if(table.getValue() != null && ((Collection<?>)table.getValue()).size() > 1)
			return "policies";
		else return "policy";
	}

	/**
	 * Retrieve serviceName and serviceClass from a given back-end service name.
	 * @param serviceNameAndClass
	 * @return String[0] is serviceClass and String[1] is serviceName
	 */
	public static String[] splitNameHepler(String serviceNameAndClass){
		return serviceNameAndClass.split(PresentationHelper.SEPARATOR);
	}

	/**
	 * Build the name of service used for visualization.
	 * @param serviceName
	 * @return {@link String} representing the service name for presentation
	 */
	public static String viewName(String serviceName){
		if(serviceName.indexOf(PresentationHelper.SEPARATOR) >= 0){
			String[] split = serviceName.split(PresentationHelper.SEPARATOR);
			return split[0]+" "+split[1];
		}
		return serviceName;
	}

	/**
	 * Build the name of service for policy management back-end.
	 * @param serviceName
	 * @param serviceClass
	 * @return {@link String} representing the service name of back-end
	 */
	public static String buildNameHelper(String serviceName, String serviceClass){
		StringBuilder result = new StringBuilder();
		result.append(serviceClass).append(PresentationHelper.SEPARATOR).append(serviceName);
		return result.toString();
	}

	/**
	 * Return an array of {@link String} for the timeRange visualization. At index 0 the start and at index 1 the end. The format is HH:mm.
	 * @param policyTmeRange
	 * @return an array of {@link String} for the timeRange visualization or null
	 */
	public static String[] viewTimeRange(String policyTmeRange){
		if(policyTmeRange == null || policyTmeRange.equals("") || policyTmeRange.indexOf("-") < 0) return null;
		String[] timeRange = policyTmeRange.split("-");
		if(timeRange == null || timeRange.length == 0) return null;
		return new String[]{timeRange[0].subSequence(0, 2) + ":" + timeRange[0].subSequence(2, 4), timeRange[1].subSequence(0, 2) + ":" + timeRange[1].subSequence(2, 4)};
	}

	/**
	 * Return an array of {@link Date} for the dateRange visualization. At index 0 the start and at index 1 the end. The format is {@value TimeFormatHelper#DATE_FORMAT}.
	 * @param policyDateRange
	 * @return an array of {@link Date} for the dateRange visualization or null
	 */
	public static Date[] viewDateRange(String policyDateRange){
		if(policyDateRange == null || policyDateRange.equals("")) return null;
		String[] dateRange = policyDateRange.split("-");
		if(dateRange == null || dateRange.length != 2)return null;
		SimpleDateFormat format = new SimpleDateFormat(TimeFormatHelper.DATE_FORMAT);
		try {
			return new Date[]{format.parse(dateRange[0]), format.parse(dateRange[1])};
		} catch (ParseException e) {
			TimeFormatHelper.logger.error("Error parsing Date Range ["+policyDateRange+"]", e);
		}
		return null;
	}

	/**
	 * Return the {@link String} representing the dateRange for visualization. At index 0 the start and at index 1 the end.
	 * @param policyDateRange
	 * @return {@link String} representing the dateRange for visualization or null
	 */
	public static String viewDateRangeString(String policyDateRange){
		if(policyDateRange == null || policyDateRange.equals("")) return null;
		String[] dateRange = policyDateRange.split("-");
		if(dateRange == null || dateRange.length != 2)return null;
		SimpleDateFormat format = new SimpleDateFormat(TimeFormatHelper.DATE_FORMAT);
		SimpleDateFormat viewFormat = new SimpleDateFormat(TimeFormatHelper.VIEW_DATE_FORMAT);
		try {
			return viewFormat.format(format.parse(dateRange[0]))+" - "+viewFormat.format(format.parse(dateRange[1]));
		} catch (ParseException e) {
			TimeFormatHelper.logger.error("Error parsing Date Range ["+policyDateRange+"]", e);
		}
		return null;
	}

	/**
	 * Return an array of {@link String} for the timeRange visualization. At index 0 the start and at index 1 the end. The format is HH:mm.
	 * @param policyTmeRange
	 * @return an array of {@link String} for the timeRange visualization or null
	 */
	public static String viewTimeRangeString(String policyTmeRange){
		if(policyTmeRange == null || policyTmeRange.equals("") || policyTmeRange.indexOf("-") < 0) return null;
		String[] timeRange = policyTmeRange.split("-");
		if(timeRange == null || timeRange.length == 0) return null;
		return timeRange[0].subSequence(0, 2) + ":" + timeRange[0].subSequence(2, 4)+" - "+ timeRange[1].subSequence(0, 2) + ":" + timeRange[1].subSequence(2, 4);
	}

}
