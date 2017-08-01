package gr.cite.geoanalytics.security;

public class SecurityException extends Exception {
	
	private static final long serialVersionUID = 4803304617965670731L;

	public SecurityException() {
		super();
	}
	
	public SecurityException(String message) {
		super(message);
	}
	
	public SecurityException(Throwable cause) {
		super(cause);
	}
	
	public SecurityException(String message, Throwable cause) {
		super(message, cause);
	}
}
