package gr.cite.gaap.datatransferobjects;

import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;

public class ImportResponse {
	public enum Status {
		Success, Failure, Existing
	}

	private Status status = Status.Failure;
	private String message = null;
	private Bounds bounds = null;

	public ImportResponse() {
	}

	public ImportResponse(Status status, Bounds bounds, String message) {
		this.status = status;
		this.bounds = bounds;
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

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
}
