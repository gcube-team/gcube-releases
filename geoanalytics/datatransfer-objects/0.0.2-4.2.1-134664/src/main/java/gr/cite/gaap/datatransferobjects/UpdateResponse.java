package gr.cite.gaap.datatransferobjects;

public class UpdateResponse {
	private boolean status = false;
	private String message = null;

	public UpdateResponse() {
	}

	public UpdateResponse(boolean status, String message) {
		this.status = status;
		this.message = message;
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
