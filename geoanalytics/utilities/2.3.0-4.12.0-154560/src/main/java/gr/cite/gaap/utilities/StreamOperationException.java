package gr.cite.gaap.utilities;

public class StreamOperationException extends RuntimeException {
	
	private static final long serialVersionUID = 5638148986689461843L;

	public StreamOperationException() {
		super();
	}
	
	public StreamOperationException(String message) {
		super(message);
	}
	
	public StreamOperationException(Throwable cause) {
		super(cause);
	}
	
	public StreamOperationException(String message, Throwable cause) {
		super(message, cause);
	}
}
