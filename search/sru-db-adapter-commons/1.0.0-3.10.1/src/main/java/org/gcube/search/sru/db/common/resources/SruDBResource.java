package org.gcube.search.sru.db.common.resources;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;

@XmlRootElement
public class SruDBResource extends StatefulResource {

	private static final long serialVersionUID = 1L;

	private String hostname;
	private Integer port;
	private String dbName;
	private String dbType;

	private String dbTitle;
	private String dbDescription;

	private String username;
	private String password;

	private String scope;
	
	private Map<String, String> fieldsMapping;
	private ExplainInfo explainInfo;

	@XmlElement
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	@XmlElement
	public Map<String, String> getFieldsMapping() {
		return fieldsMapping;
	}

	public void setFieldsMapping(Map<String, String> fieldsMapping) {
		this.fieldsMapping = fieldsMapping;
	}

	@XmlElement
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@XmlElement
	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@XmlElement
	public String getDbTitle() {
		return dbTitle;
	}

	public void setDbTitle(String dbTitle) {
		this.dbTitle = dbTitle;
	}

	@XmlElement
	public String getDbDescription() {
		return dbDescription;
	}

	public void setDbDescription(String dbDescription) {
		this.dbDescription = dbDescription;
	}

	@XmlElement
	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	@XmlElement
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@XmlElement
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@XmlElement
	public ExplainInfo getExplainInfo() {
		return explainInfo;
	}

	public void setExplainInfo(ExplainInfo explainInfo) {
		this.explainInfo = explainInfo;
	}

	@Override
	public void onLoad() throws StatefulResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClose() throws StatefulResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() throws StatefulResourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public String toXML() throws javax.xml.bind.JAXBException {
		String xml = super.toXML();

		String replace = "GUESS";

		xml = xml.replace(">" + this.password + "<", ">" + replace + "<")
				.replace(">" + this.username + "<", ">" + replace + "<");

		return xml;
	}

	@Override
	public String toString() {
		return "SRUDatabaseResource [hostname=" + hostname + ", port=" + port
				+ ", dbName=" + dbName + ", dbType=" + dbType + ", username="
				+ username + ", password=" + password + "]";
	}

}
