package org.gcube.portlet.user.my_vres.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AuthorizationBean implements Serializable {
	private String oAuth2TemporaryCode;
	private String state;
	private boolean success;
	private String errorDescription;
	
	public AuthorizationBean() {
		super();
	}

	public AuthorizationBean(String oAuth2TemporaryCode, String state, boolean success, String errorDescription) {
		super();
		this.oAuth2TemporaryCode = oAuth2TemporaryCode;
		this.state = state;
		this.success = success;
		this.errorDescription = errorDescription;
	}

	public String getOAuth2TemporaryCode() {
		return oAuth2TemporaryCode;
	}

	public void seOAuth2TemporaryCode(String oAuth2TemporaryCode) {
		this.oAuth2TemporaryCode = oAuth2TemporaryCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	@Override
	public String toString() {
		return "AuthorizationBean [oAuth2TemporaryCode=" + oAuth2TemporaryCode + ", state=" + state + ", success=" + success + ", errorDescription="
				+ errorDescription + "]";
	}
	
}
