package org.gcube.data_catalogue.grsf_publish_ws.json.output;

/**
 * Response bean to be used by the service.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ResponseBean{

	private boolean success;
	private String message;
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
