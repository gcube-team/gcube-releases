package gr.cite.additionalemailaddresses.models;

/**
 * @author mnikolopoulos
 *
 * @param <T>
 */
public class ResponseMessage<T> {

	private final T entity;
	private final String message;
	private final Boolean status;
	
	public ResponseMessage(final T entity, final String message, final Boolean status) {
		this.entity = entity;
		this.message = message;
		this.status = status;
	}

	public T getEntity() {
		return entity;
	}

	public String getMessage() {
		return message;
	}

	public Boolean getStatus() {
		return status;
	}
}
