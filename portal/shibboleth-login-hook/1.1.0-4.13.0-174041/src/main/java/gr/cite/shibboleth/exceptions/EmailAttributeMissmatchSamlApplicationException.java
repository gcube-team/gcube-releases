package gr.cite.shibboleth.exceptions;

public class EmailAttributeMissmatchSamlApplicationException extends Exception {

	private static final long serialVersionUID = 501461658221737797L;

	public EmailAttributeMissmatchSamlApplicationException() {
	}

	public EmailAttributeMissmatchSamlApplicationException(String message) {
		super(message);
	}

	public EmailAttributeMissmatchSamlApplicationException(Throwable cause) {
		super(cause);
	}

	public EmailAttributeMissmatchSamlApplicationException(String message, Throwable cause) {
		super(message, cause);
	}
}
