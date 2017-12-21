package org.gcube.portal.social.networking.ws.inputs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Application id object
 * @author Costantino Perciante at ISTI-CNR
 */
@ApiModel(description="An object containing the app_id field", value="Application")
public class ApplicationId {

	@JsonProperty("app_id")
	@NotNull(message="app_id cannot be null")
	@Size(message="app_id cannot be empty", min=1)
	@ApiModelProperty( 
			example="appX",
			name="app_id",
			required=true, 
			value="The application identifier"
			)
	private String appId;

	public ApplicationId() {
		super();
	}

	public ApplicationId(String appId) {
		super();
		this.appId = appId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	public String toString() {
		return "ApplicationId [appId=" + appId + "]";
	}
}
