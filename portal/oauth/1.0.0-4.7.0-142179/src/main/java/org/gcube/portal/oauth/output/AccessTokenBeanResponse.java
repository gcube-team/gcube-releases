package org.gcube.portal.oauth.output;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response to a request token.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AccessTokenBeanResponse {

	@JsonProperty("access_token") 
	private String accessToken;

	@JsonProperty("expires_in") 
	private static final Long expiresIn = Long.MAX_VALUE; // the number of seconds remaining (max long value)

	@JsonProperty("scope")
	private String scope;
	
	@JsonProperty("token_type")
	private static final String tokenType = "Bearer";

	/**
	 * @param accessToken
	 * @param scope
	 */
	public AccessTokenBeanResponse(String accessToken, String scope) {
		super();
		this.accessToken = accessToken;
		this.scope = scope;
	}

	public static Long getExpiresin() {
		return expiresIn;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public static String getTokentype() {
		return tokenType;
	}

	@Override
	public String toString() {
		return "AccessTokenBeanResponse [accessToken=" + accessToken
				+ ", scope=" + scope + "]";
	}
}
