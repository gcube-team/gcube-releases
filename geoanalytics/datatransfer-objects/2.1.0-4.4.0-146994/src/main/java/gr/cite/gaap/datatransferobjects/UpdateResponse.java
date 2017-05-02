package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateResponse {
	private static Logger logger = LoggerFactory.getLogger(UpdateResponse.class);

	private boolean status = false;
	private String message = null;

	public UpdateResponse() {
		logger.trace("Initialized default contructor for UpdateResponse");

	}

	public UpdateResponse(boolean status, String message) {
		logger.trace("Initializing UpdateResponse...");

		this.status = status;
		this.message = message;
		logger.trace("Initialized UpdateResponse");

	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
