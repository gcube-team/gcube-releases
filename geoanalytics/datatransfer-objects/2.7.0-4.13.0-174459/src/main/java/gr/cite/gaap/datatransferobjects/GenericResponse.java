package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericResponse {
	private static Logger logger = LoggerFactory.getLogger(GenericResponse.class);
	public enum Status {
		Success, Failure, Existing,
		NotFound, ProjectNotFound, TaskNotFound,
		DocumentNotFound, Unauthorized, TermsExceedLimit,
		InvalidDate, ValidationError
	}

	private Status status = Status.Failure;
	private String message = null;
	private Object response = null;

	public GenericResponse() {
		logger.trace("Initialized default contructor for GenericResponse");
	}

	public GenericResponse(Status status, Object response, String message) {
		logger.trace("Initializing GenericResponse...");
		this.status = status;
		this.response = response;
		this.message = message;
		logger.trace("Initialized GenericResponse");
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}
}
