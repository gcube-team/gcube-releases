/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.data;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * An error for a row in the CSV.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class CSVRowError extends BaseModel implements Serializable {
	
	private static final long serialVersionUID = 4680781887858741657L;

	public static final String ERROR_DESCRIPTION = "errorDescription";
	public static final String LINE_VALUE = "lineValue";
	public static final String LINE_NUMBER = "lineNumber";
		
	public CSVRowError(){}
	
	/**
	 * @param lineNumber the row number.
	 * @param lineValue the line value.
	 * @param errorDescription the error description.
	 */
	public CSVRowError(int lineNumber, String lineValue, String errorDescription) {
		set(LINE_NUMBER, lineNumber);
		set(LINE_VALUE, lineValue);
		set(ERROR_DESCRIPTION, errorDescription);
	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return (Integer)get(LINE_NUMBER);
	}

	/**
	 * @return the line value
	 */
	public String getLineValue() {
		return get(LINE_VALUE);
	}

	/**
	 * @return the error description.
	 */
	public String getErrorDescription() {
		return get(ERROR_DESCRIPTION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CSVRowError [lineNumber=");
		builder.append(getLineNumber());
		builder.append(", lineValue=");
		builder.append(getLineValue());
		builder.append(", errorDescription=");
		builder.append(getErrorDescription());
		builder.append("]");
		return builder.toString();
	}
}
