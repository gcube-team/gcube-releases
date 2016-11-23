/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.google.gwt.core.shared.GWT;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 11, 2014
 * 
 */
public class ExpressionDialogConvertUtil {

	public static ColumnTypeCode toColumnTypeCode(String type) {

		GWT.log("Converting toColumnTypeCode: "+type);
		if (type != null && !type.isEmpty()) {
			type = type.toUpperCase();
			GWT.log("Converting toUpperCase: "+type);
			return ColumnTypeCode.valueOf(type);
		}
		
		return null;
	}

	public static ColumnDataType toColumnDataType(String dataType) {

		GWT.log("Converting toColumnDataType: "+dataType);
//		System.out.println("Converting toColumnDataType: "+dataType);
		if (dataType != null && !dataType.isEmpty()) {
			dataType = lowerCaseUpFirstChar(dataType);
			GWT.log("Converting lowerCaseUpFirstChar: "+dataType);
//			System.out.println("Converting lowerCaseUpFirstChar: "+dataType);
			return ColumnDataType.valueOf(dataType);
		}
		
		return null;
	}

	public static String lowerCaseUpFirstChar(String value) {

		// logger.trace("Normalize...:   "+value);

		if (value == null || value.length() == 0)
			return "";

		value = value.trim();

		String firstChar = value.substring(0, 1);
		String lastChars = value.substring(1, value.length());

		if (lastChars != null)
			lastChars = lastChars.toLowerCase();

		// logger.trace("Normalized in:   "+firstChar.toUpperCase() +
		// lastChars);

		return firstChar.toUpperCase() + lastChars;

	}

}
