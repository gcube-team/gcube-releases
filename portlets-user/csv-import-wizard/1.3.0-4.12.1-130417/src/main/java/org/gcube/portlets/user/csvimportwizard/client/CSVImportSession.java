/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.csvimportwizard.client.source.CSVSource;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVImportSession implements Serializable {
	
	private static final long serialVersionUID = 8335993560069738899L;

	protected String id;
	protected WizardState state;

	protected CSVSource source;
	
	protected ArrayList<String> headers = new ArrayList<String>();
	
	protected boolean skipInvalidLines = false;
	
	protected boolean[] columnToImportMask;
	
	
	
	protected String localFileName;
	protected long fileLenght;
	protected String serverlFileName;
	
	protected String csvTitle;
	protected String csvDescription;
	protected String csvSource;
	protected String csvRights;
	
	
	
	
	
	
	
	
	/**
	 * Creates a new import session.
	 */
	public CSVImportSession()
	{
		state = WizardState.SOURCE_SELECTION;
	}
	
	/**
	 * Returns the session id.
	 * @return the session id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the session id.
	 * @param id the session id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the current {@link CSVSource}.
	 * @return the csvSource.
	 */
	public CSVSource getSource() {
		return source;
	}

	/**
	 * Sets the {@link CSVSource}.
	 * @param source the csvSource to set
	 */
	public void setSource(CSVSource source) {
		this.source = source;
	}
	
	/**
	 * @return the headers
	 */
	public ArrayList<String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}

	/**
	 * @return the columnToImportMask
	 */
	public boolean[] getColumnToImportMask() {
		return columnToImportMask;
	}

	/**
	 * @param columnToImportMask the columnToImportMask to set
	 */
	public void setColumnToImportMask(boolean[] columnToImportMask) {
		this.columnToImportMask = columnToImportMask;
	}

	/**
	 * @return the skipInvalidLines
	 */
	public boolean isSkipInvalidLines() {
		return skipInvalidLines;
	}


	/**
	 * @param skipInvalidLines the skipInvalidLines to set
	 */
	public void setSkipInvalidLines(boolean skipInvalidLines) {
		this.skipInvalidLines = skipInvalidLines;
	}
	

	
	/**
	 * @return the fileName
	 */
	public String getLocalFileName() {
		return localFileName;
	}


	/**
	 * @param localFileName the fileName to set
	 */
	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}


	/**
	 * @return the csvTitle
	 */
	public String getCsvTitle() {
		return csvTitle;
	}


	/**
	 * @param csvTitle the csvName to set
	 */
	public void setCsvTitle(String csvTitle) {
		this.csvTitle = csvTitle;
	}


	/**
	 * @return the csvDescription
	 */
	public String getCsvDescription() {
		return csvDescription;
	}


	/**
	 * @param csvDescription the csvDescription to set
	 */
	public void setCsvDescription(String csvDescription) {
		this.csvDescription = csvDescription;
	}

	/**
	 * @return the serverlFileName
	 */
	public String getServerlFileName() {
		return serverlFileName;
	}


	/**
	 * @param serverlFileName the serverlFileName to set
	 */
	public void setServerlFileName(String serverlFileName) {
		this.serverlFileName = serverlFileName;
	}

}
