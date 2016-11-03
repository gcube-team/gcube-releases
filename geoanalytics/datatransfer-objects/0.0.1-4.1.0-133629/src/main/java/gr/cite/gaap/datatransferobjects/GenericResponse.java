package gr.cite.gaap.datatransferobjects;

public class GenericResponse {
	public enum Status {
		Success, Failure, Existing, NotFound, ProjectNotFound, TaskNotFound, DocumentNotFound, Unauthorized, TermsExceedLimit, InvalidDate
	}

	private Status status = Status.Failure;
	private String message = null;
	private Object response = null;

	public GenericResponse() {
	}

	public GenericResponse(Status status, Object response, String message) {
		this.status = status;
		this.response = response;
		this.message = message;
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
