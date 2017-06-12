package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
@XmlSeeAlso({SMCreateTableFromCSVRequest.class, SMCreateTableFromDataStreamRequest.class, SMimportFileRequest.class})
@XmlRootElement(namespace = TYPES_WSDL_NAMESPACE)
public abstract class SMCreateTableRequest {

	@XmlElement()
	private String user;
	@XmlElement()
	private String fileName;
	@XmlElement()
	private String tableName;
	@XmlElement()
	private String tableType;
	@XmlElement()
	private String description;
	@XmlElement()
	private String rsLocator;

	public SMCreateTableRequest() {
	}

	public SMCreateTableRequest(String description, String fileName,
			String rsLocator, String tableName, String tableType, String user) {
		this.user = user;
		this.fileName = fileName;
		this.tableName = tableName;
		this.tableType = tableType;
		this.description = description;
		this.rsLocator = rsLocator;
	}

	public void user(String user) {
		this.user = user;
	}

	public String user() {
		return user;
	}

	public void fileName(String fileName) {
		this.fileName = fileName;
	}

	public String fileName() {
		return fileName;
	}

	public void tableName(String tableName) {
		this.tableName = tableName;
	}

	public String tableName() {
		return tableName;
	}

	public void tableType(String tableType) {
		this.tableType = tableType;
	}

	public String tableType() {
		return tableType;
	}

	public void description(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public void rsLocator(String rsLocator) {
		this.rsLocator = rsLocator;
	}

	public String rsLocator() {
		return rsLocator;
	}
}
