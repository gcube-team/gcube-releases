/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.bean;

import java.io.Serializable;

/**
 * @author ceras
 *
 */
public class CsvMetadata implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6513418575213232121L;

	private boolean hasHeader;
	private String fileAbsolutePath, template, tableName, description, delimiterChar, commentChar;
	

	public CsvMetadata() {
		super();
	}
	


	/**
	 * @param hasHeader
	 * @param fileAbsolutePath
	 * @param tableName
	 * @param delimiterChar
	 * @param commentChar
	 */
	public CsvMetadata(boolean hasHeader, String fileAbsolutePath, String delimiterChar, String commentChar) {
		super();
		this.hasHeader = hasHeader;
		this.fileAbsolutePath = fileAbsolutePath;
		this.delimiterChar = delimiterChar;
		this.commentChar = commentChar;
	}


	/**
	 * @return the hasHeader
	 */
	public boolean isHasHeader() {
		return hasHeader;
	}


	/**
	 * @param hasHeader the hasHeader to set
	 */
	public void setHasHeader(boolean hasHeader) {
		this.hasHeader = hasHeader;
	}


	/**
	 * @return the fileAbsolutePath
	 */
	public String getFileAbsolutePath() {
		return fileAbsolutePath;
	}


	/**
	 * @param fileAbsolutePath the fileAbsolutePath to set
	 */
	public void setFileAbsolutePath(String fileAbsolutePath) {
		this.fileAbsolutePath = fileAbsolutePath;
	}


	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}


	/**
	 * @param template the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}


	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}


	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the delimiterChar
	 */
	public String getDelimiterChar() {
		return delimiterChar;
	}


	/**
	 * @param delimiterChar the delimiterChar to set
	 */
	public void setDelimiterChar(String delimiterChar) {
		this.delimiterChar = delimiterChar;
	}


	/**
	 * @return the commentChar
	 */
	public String getCommentChar() {
		return commentChar;
	}


	/**
	 * @param commentChar the commentChar to set
	 */
	public void setCommentChar(String commentChar) {
		this.commentChar = commentChar;
	}


	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
