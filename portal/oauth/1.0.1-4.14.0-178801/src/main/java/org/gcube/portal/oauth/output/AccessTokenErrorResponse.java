package org.gcube.portal.oauth.output;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean used on failed request
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AccessTokenErrorResponse {

	@JsonProperty("error")
	private String error;

	@JsonProperty("error_description")
	private String errorDescription;

	public AccessTokenErrorResponse() {
		super();
	}

	/**
	 * @param error
	 * @param errorDescription
	 * @param errorUri
	 */
	public AccessTokenErrorResponse(String error, String errorDescription) {
		super();
		this.error = error;
		this.errorDescription = errorDescription;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	@Override
	public String toString() {
		return "AccessTokenErrorResponse ["
				+ (error != null ? "error=" + error + ", " : "")
				+ (errorDescription != null ? "errorDescription="
						+ errorDescription : "") + "]";
	}
}
