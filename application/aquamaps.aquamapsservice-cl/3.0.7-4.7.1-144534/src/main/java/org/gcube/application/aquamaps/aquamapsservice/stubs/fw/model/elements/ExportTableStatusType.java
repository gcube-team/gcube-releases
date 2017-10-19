package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.ExportCSVSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportStatus;

@XmlRootElement
public class ExportTableStatusType {

	@XmlElement
	private String tableName;
	
	@XmlElement
	private ExportCSVSettings csvSettings;	
	
	@XmlElement
	private String rsLocator;
	
	@XmlElement
	private ExportStatus status;
	
	@XmlElement
	private String errors;
	
	
	public ExportTableStatusType() {
		// TODO Auto-generated constructor stub
	}


	
	
	public ExportTableStatusType(String tableName, ExportCSVSettings csvSettings,
			String rsLocator, ExportStatus status, String errors) {
		super();
		this.tableName = tableName;
		this.csvSettings = csvSettings;
		this.rsLocator = rsLocator;
		this.status = status;
		this.errors = errors;
	}




	/**
	 * @return the tableName
	 */
	public String tableName() {
		return tableName;
	}


	/**
	 * @param tableName the tableName to set
	 */
	public void tableName(String tableName) {
		this.tableName = tableName;
	}


	/**
	 * @return the csvSettings
	 */
	public ExportCSVSettings csvSettings() {
		return csvSettings;
	}


	/**
	 * @param csvSettings the csvSettings to set
	 */
	public void csvSettings(ExportCSVSettings csvSettings) {
		this.csvSettings = csvSettings;
	}


	/**
	 * @return the rsLocator
	 */
	public String rsLocator() {
		return rsLocator;
	}


	/**
	 * @param rsLocator the rsLocator to set
	 */
	public void rsLocator(String rsLocator) {
		this.rsLocator = rsLocator;
	}


	/**
	 * @return the status
	 */
	public ExportStatus status() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void status(ExportStatus status) {
		this.status = status;
	}


	/**
	 * @return the errors
	 */
	public String errors() {
		return errors;
	}


	/**
	 * @param errors the errors to set
	 */
	public void errors(String errors) {
		this.errors = errors;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExportTableStatusType [tableName=");
		builder.append(tableName);
		builder.append(", csvSettings=");
		builder.append(csvSettings);
		builder.append(", rsLocator=");
		builder.append(rsLocator);
		builder.append(", status=");
		builder.append(status);
		builder.append(", errors=");
		builder.append(errors);
		builder.append("]");
		return builder.toString();
	}
	
	
}
