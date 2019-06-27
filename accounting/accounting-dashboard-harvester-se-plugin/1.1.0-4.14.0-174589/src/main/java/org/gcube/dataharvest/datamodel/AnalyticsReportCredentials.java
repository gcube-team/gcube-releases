package org.gcube.dataharvest.datamodel;

import java.util.List;

/**
 * 
 * @author massi
 *
 */
public class AnalyticsReportCredentials {
	
	private List<String> viewIds;
	private String projectId;
	private String clientId;
	private String clientEmail;
	private String privateKeyPem;
	private String privateKeyId;
	private String tokenUri;
	
	public AnalyticsReportCredentials() {
		super();
	}

	public List<String> getViewIds() {
		return viewIds;
	}

	public void setViewIds(List<String> viewIds) {
		this.viewIds = viewIds;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public String getPrivateKeyPem() {
		return privateKeyPem;
	}
	/**
	 * Please note:
	 * The key is stored in the resource with blanks " " instead of "\n" as it causes issues and 
	 * without the BEGIN and END Delimiters (e.g. -----END PRIVATE KEY-----) which myst be readded
	 * @param privateKeyPem
	 */
	public void setPrivateKeyPem(String privateKeyPem) {
		privateKeyPem = privateKeyPem.replace(" ", "\n");
		this.privateKeyPem = "-----BEGIN PRIVATE KEY-----\n"+privateKeyPem+"\n-----END PRIVATE KEY-----";
	}

	public String getPrivateKeyId() {
		return privateKeyId;
	}

	public void setPrivateKeyId(String privateKeyId) {
		this.privateKeyId = privateKeyId;
	}

	public String getTokenUri() {
		return tokenUri;
	}

	public void setTokenUri(String tokenUri) {
		this.tokenUri = tokenUri;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AnalyticsReportCredentials [viewIds=");
		builder.append(viewIds);
		builder.append(", projectId=");
		builder.append(projectId);
		builder.append(", clientId=");
		builder.append(clientId);
		builder.append(", clientEmail=");
		builder.append(clientEmail);
		builder.append(", privateKeyPem=\n");
		builder.append(privateKeyPem);
		builder.append("\n, privateKeyId=");
		builder.append(privateKeyId);
		builder.append(", tokenUri=");
		builder.append(tokenUri);
		builder.append("]");
		return builder.toString();
	}


}
