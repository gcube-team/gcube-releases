package org.gcube.vremanagement.softwaregateway.answer;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.Access;
/**
 * Class that contains all the fields of an record of report xml
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class ReportObject {
	
	public String groupId;
	public String artifactId;
	public String version;
	public String ID;
	public String timestamp;
	public String url;
	public String javadocUrl;
	public String status;
	public String operation;
	public String path;
	protected final GCUBELog logger = new GCUBELog(ReportObject.class);
	
	public ReportObject(String groupId, String artifactId, String version, String timestamp, String url, String javadocUrl, String status, String operation, String id){
		logger.trace("ReportObject method: build report object with param: "+groupId+" "+artifactId+" "+version+" "+timestamp+" "+url+" "+status+" "+operation+" "+id);
		setGroupId(groupId);
		setArtifactId(artifactId);
		setID(id);
		setOperation(operation);
		setStatus(status);
		setTimestamp(timestamp);
		setUrl(url);
		setVersion(version);
		setJavadocUrl(javadocUrl);
	}
	
	public ReportObject(String filePath){
		setPath(filePath);
	}
	
	public String getPath() {
		return path;
	}



	public void setPath(String path) {
		this.path = path;
	}



	public String getID() {
		return ID;
	}



	public void setID(String iD) {
		ID = iD;
	}



	public String getJavadocUrl() {
		return javadocUrl;
	}



	public void setJavadocUrl(String javadocUrl) {
		this.javadocUrl = javadocUrl;
	}



	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}

	
	
	
}
