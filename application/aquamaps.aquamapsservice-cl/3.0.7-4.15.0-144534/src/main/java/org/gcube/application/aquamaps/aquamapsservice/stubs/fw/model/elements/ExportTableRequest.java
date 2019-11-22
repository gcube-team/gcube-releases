package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.ExportCSVSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportOperation;

@XmlRootElement(namespace=DM_target_namespace,name="exportTableRequestType")
public class ExportTableRequest {

	@XmlElement(namespace=DM_target_namespace)
	private String tableName;
	@XmlElement(namespace=DM_target_namespace)
	private ExportOperation operationType;
	@XmlElement(namespace=DM_target_namespace)
	private String user;
	@XmlElement(namespace=DM_target_namespace)
	private String basketId;
	@XmlElement(namespace=DM_target_namespace)
	private String toSaveName;
	@XmlElement(namespace=DM_target_namespace)
	private ExportCSVSettings csvSettings;
	
	
	public ExportTableRequest() {
		// TODO Auto-generated constructor stub
	}


	public ExportTableRequest(String tableName, ExportOperation operationType,
			String user, String basketId, String toSaveName,
			ExportCSVSettings csvSettings) {
		super();
		this.tableName = tableName;
		this.operationType = operationType;
		this.user = user;
		this.basketId = basketId;
		this.toSaveName = toSaveName;
		this.csvSettings = csvSettings;
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
	 * @return the operationType
	 */
	public ExportOperation operationType() {
		return operationType;
	}


	/**
	 * @param operationType the operationType to set
	 */
	public void operationType(ExportOperation operationType) {
		this.operationType = operationType;
	}


	/**
	 * @return the user
	 */
	public String user() {
		return user;
	}


	/**
	 * @param user the user to set
	 */
	public void user(String user) {
		this.user = user;
	}


	/**
	 * @return the basketId
	 */
	public String basketId() {
		return basketId;
	}


	/**
	 * @param basketId the basketId to set
	 */
	public void basketId(String basketId) {
		this.basketId = basketId;
	}


	/**
	 * @return the toSaveName
	 */
	public String toSaveName() {
		return toSaveName;
	}


	/**
	 * @param toSaveName the toSaveName to set
	 */
	public void toSaveName(String toSaveName) {
		this.toSaveName = toSaveName;
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


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExportTableRequest [tableName=");
		builder.append(tableName);
		builder.append(", operationType=");
		builder.append(operationType);
		builder.append(", user=");
		builder.append(user);
		builder.append(", basketId=");
		builder.append(basketId);
		builder.append(", toSaveName=");
		builder.append(toSaveName);
		builder.append(", csvSettings=");
		builder.append(csvSettings);
		builder.append("]");
		return builder.toString();
	}
	
	
}
