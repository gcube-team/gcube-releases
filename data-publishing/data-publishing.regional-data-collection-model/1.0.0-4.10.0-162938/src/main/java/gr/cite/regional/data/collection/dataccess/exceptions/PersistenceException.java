package gr.cite.regional.data.collection.dataccess.exceptions;

public class PersistenceException extends Exception {
	public PersistenceException() {
		super();
	}
	
	public PersistenceException(String message) {
		super(message);
	}
	
	public PersistenceException(Throwable cause) {
		super(cause);
	}
	
	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
