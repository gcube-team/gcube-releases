package org.gcube.portal.social.networking.ws.outputs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Response bean
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
@ApiModel(description="A response object", value="Response")
public class ResponseBean implements Serializable {

	private static final long serialVersionUID = -2725238162673879658L;
	@ApiModelProperty(value="The result of the request: true if it succeeded, false otherwise")
	private boolean success;

	@ApiModelProperty(value="An error message if something wrong happened, null/empty otherwise")
	private String message;

	@ApiModelProperty(value="The result object of the request")
	private Object result;

	public ResponseBean() {
		super();
	}

	/**
	 * @param success
	 * @param message
	 * @param result
	 * @param help
	 */
	public ResponseBean(boolean success, String message, Object result) {
		super();
		this.success = success;
		this.message = message;
		this.result = result;
	}


	public boolean isSuccess() {
		return success;
	}


	public void setSuccess(boolean success) {
		this.success = success;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public Object getResult() {
		return result;
	}


	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ResponseBean [success=" + success
				+ ", message=" + message + ", result=" + result + "]";
	}
}
