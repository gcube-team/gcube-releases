/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local.Source;

public class ImportSession implements Serializable {

	private static final long serialVersionUID = 8335993560069738899L;
	protected Source source;
	protected String id;
	protected WizardState state;

	protected static String type;

	protected ArrayList<String> headers = new ArrayList<String>();

	protected boolean skipInvalidLines = false;

	protected boolean[] columnToImportMask;

	protected String localFileName;
	protected long fileLenght;
	protected String serverlFileName;

	protected String Title;
	protected String Description;
	protected String Source;
	protected String Rights;

	/**
	 * Creates a new import session.
	 */
	public ImportSession() {
		state = WizardState.SOURCE_SELECTION;
	}

	/**
	 * Returns the session id.
	 * 
	 * @return the session id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the session id.
	 * 
	 * @param id
	 *            the session id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the current {@link CSVSource}.
	 * 
	 * @return the Source.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the {@link CSVSource}.
	 * 
	 * @param source
	 *            the Source to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the headers
	 */
	public ArrayList<String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers
	 *            the headers to set
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
	 * @param columnToImportMask
	 *            the columnToImportMask to set
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
	 * @param skipInvalidLines
	 *            the skipInvalidLines to set
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
	 * @param localFileName
	 *            the fileName to set
	 */
	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}

	/**
	 * @return the Title
	 */
	public String getCsvTitle() {
		return Title;
	}

	/**
	 * @param Title
	 *            the Name to set
	 */
	public void setCsvTitle(String Title) {
		this.Title = Title;
	}

	/**
	 * @return the Description
	 */
	public String getCsvDescription() {
		return Description;
	}

	/**
	 * @param Description
	 *            the Description to set
	 */
	public void setCsvDescription(String Description) {
		this.Description = Description;
	}

	/**
	 * @return the serverlFileName
	 */
	public String getServerlFileName() {
		return serverlFileName;
	}

	/**
	 * @param serverlFileName
	 *            the serverlFileName to set
	 */
	public void setServerlFileName(String serverlFileName) {
		this.serverlFileName = serverlFileName;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Source getSource() {
		return source;
	}

}
