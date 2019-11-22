package gr.uoa.di.madgik.grs.record.exception;


public class GRS2ThrowableWrapperException extends Exception {
	private static final long serialVersionUID = 1L;

	public GRS2ThrowableWrapperException() {
		super();
	}

	public GRS2ThrowableWrapperException(String message) {
		super(message);
	}

	public GRS2ThrowableWrapperException(String message, Throwable cause) {
		super(message, cause);
	}

}