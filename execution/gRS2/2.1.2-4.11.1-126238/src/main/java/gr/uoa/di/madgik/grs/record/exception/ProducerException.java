package gr.uoa.di.madgik.grs.record.exception;

public class ProducerException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProducerException() {
		super();
	}

	public ProducerException(String message) {
		super(message);
	}

	public ProducerException(String message, Throwable cause) {
		super(message, cause);
	}
}
