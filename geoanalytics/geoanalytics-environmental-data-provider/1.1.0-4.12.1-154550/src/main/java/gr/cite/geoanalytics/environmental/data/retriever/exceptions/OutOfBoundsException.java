package gr.cite.geoanalytics.environmental.data.retriever.exceptions;

public class OutOfBoundsException extends Exception {

	private static final long serialVersionUID = -2935028221473287302L;

	public OutOfBoundsException() {
		super();
	}

	public OutOfBoundsException(String message) {
		super(message);
	}

	public OutOfBoundsException(Throwable cause) {
		super(cause);
	}

	public OutOfBoundsException(String message, Throwable cause) {
		super(message, cause);
	}
}
