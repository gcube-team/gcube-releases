/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.csv;

import java.io.Serializable;


/**
 * Errors for row
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class CSVRowError implements Serializable {
	
	private static final long serialVersionUID = 4680781887858741657L;
	
	protected static int seed;
	
	protected int id;
	protected int lineNumber;
	protected String lineValue;
	protected String errorDescription;
		
	public CSVRowError(){}
	
	/**
	 * @param lineNumber the row number.
	 * @param lineValue the line value.
	 * @param errorDescription the error description.
	 */
	public CSVRowError(int lineNumber, String lineValue, String errorDescription) {
		this.id = seed++;
		this.lineNumber = lineNumber;
		this.lineValue = lineValue;
		this.errorDescription = errorDescription;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @return the line value
	 */
	public String getLineValue() {
		return lineValue;
	}

	/**
	 * @return the error description.
	 */
	public String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CSVRowError [lineNumber=");
		builder.append(lineNumber);
		builder.append(", lineValue=");
		builder.append(lineValue);
		builder.append(", errorDescription=");
		builder.append(errorDescription);
		builder.append("]");
		return builder.toString();
	}
}
