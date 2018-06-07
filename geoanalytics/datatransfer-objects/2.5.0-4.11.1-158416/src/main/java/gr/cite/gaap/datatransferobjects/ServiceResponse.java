package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceResponse {
	private static Logger logger = LoggerFactory.getLogger(ServiceResponse.class);

	private boolean status = false;
	private Object data = null;
	private String message = null;
	
	public ServiceResponse() {
		logger.trace("Initialized default contructor for ServiceResponse");

	}
	
	public ServiceResponse(boolean status, Object data, String message) {
		logger.trace("Initializing ServiceResponse...");

		this.status = status;
		this.data = data;
		this.message = message;
		logger.trace("Initialized ServiceResponse");
	}

	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}