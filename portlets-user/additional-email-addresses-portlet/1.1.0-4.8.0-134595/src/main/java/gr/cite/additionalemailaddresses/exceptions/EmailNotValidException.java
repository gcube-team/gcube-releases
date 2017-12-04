package gr.cite.additionalemailaddresses.exceptions;

/**
 * @author mnikolopoulos
 *
 */
public class EmailNotValidException extends Exception {

	private static final long serialVersionUID = -8834110453282292767L;

		public EmailNotValidException() {
		}

		public EmailNotValidException(String message) {
			super(message);
		}

		public EmailNotValidException(Throwable cause) {
			super(cause);
		}

		public EmailNotValidException(String message, Throwable cause) {
			super(message, cause);
		}
}
