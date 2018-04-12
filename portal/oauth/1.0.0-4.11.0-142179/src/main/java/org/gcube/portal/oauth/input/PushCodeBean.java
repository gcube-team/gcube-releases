package org.gcube.portal.oauth.input;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The code to be pushed into the cache of codes (plus some other informations)
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class PushCodeBean {

	@JsonProperty("code") 
	private String code;

	@JsonProperty("redirect_uri") 
	private String redirectUri;

	@JsonProperty("client_id") 
	private String clientId;

	public PushCodeBean() {
		super();
	}

	/**
	 * @param code
	 * @param redirectUri
	 * @param clientId
	 */
	public PushCodeBean(String code, String redirectUri, String clientId) {
		super();
		this.code = code;
		this.redirectUri = redirectUri;
		this.clientId = clientId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String toString() {
		return "PushCodeBean [code=" + code + ", redirectUri=" + redirectUri
				+ ", clientId=" + clientId + "]";
	}

}
