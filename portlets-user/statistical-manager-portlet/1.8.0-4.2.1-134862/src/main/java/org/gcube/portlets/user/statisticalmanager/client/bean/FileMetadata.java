package org.gcube.portlets.user.statisticalmanager.client.bean;

import java.io.Serializable;

public class FileMetadata implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileAbsolutePath, fileTaxaPath,fileVernaculaPath, fileName, description, type;
	
	
	public FileMetadata() {
		super();
	}
	public FileMetadata( String fileAbsolutePath,String type) {
		super();
		this.fileAbsolutePath = fileAbsolutePath;
		this.fileTaxaPath=null;
		this.fileVernaculaPath=null;
		this.type=type;
	}
	public FileMetadata( String fileAbsolutePath,String fileTaxaPath, String fileVernacularPath,String type) {
		super();
		this.fileAbsolutePath = fileAbsolutePath;
		this.fileTaxaPath=fileTaxaPath;
		this.fileVernaculaPath=fileVernacularPath;
		this.type=type;
	}
	
	
	
	public String getType()
	{
		return type;
		
	}
	public void setType(String type)
	{
		this.type=type;
	}
	
	
	

	/**
	 * @return the fileAbsolutePath
	 */
	public String getTaxaFileAbsolutePath() {
		return fileTaxaPath;
	}


	/**
	 * @param fileAbsolutePath the fileAbsolutePath to set
	 */
	public void setTaxaFileAbsolutePath(String fileTaxaPath) {
		this.fileTaxaPath = fileTaxaPath;
	}
	

	/**
	 * @return the fileAbsolutePath
	 */
	public String getVernacularFileAbsolutePath() {
		return fileVernaculaPath;
	}


	/**
	 * @param fileAbsolutePath the fileAbsolutePath to set
	 */
	public void setVernacularFileAbsolutePath(String fileVernaculaPath) {
		this.fileVernaculaPath = fileVernaculaPath;
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
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}


	/**
	 * @param fileName the tableName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
