/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.shared;

import java.io.Serializable;


/**
 * The Class HandlerResultMessage.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 10, 2016
 */
public class HandlerResultMessage implements Serializable{


	/**
	 * 
	 */
	private static final String SEPARATOR = ":";
	protected Status status;
	protected String message;

	/**
	 *
	 */
	private static final long serialVersionUID = -7652344136772252755L;

	/**
	 * The Enum Status.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Aug 3, 2015
	 */
	public enum Status {
		/**
		 * If an error occurs.
		 */
		ERROR,
		/**
		 * If no error occurs.
		 */
		OK,
		/**
		 * If there was some problems.
		 */
		WARN,

		/**
		 * If the result is unknown.
		 */
		UNKNOWN,

		/**
		 * If the session is expired.
		 */
		SESSION_EXPIRED;
	}


	/**
	 *  TO SERIALIZATION.
	 */
	public HandlerResultMessage() {
	}

	/**
	 * Create a new result message.
	 * @param status the status.
	 * @param message the message.
	 */
	public HandlerResultMessage(Status status, String message) {
		this.status = status;
		this.message = message;
	}

	/**
	 * Error result.
	 *
	 * @param message the message
	 * @return the handler result message
	 */
	public static HandlerResultMessage errorResult(String message){
		return new HandlerResultMessage(Status.ERROR, message);
	}

	/**
	 * Ok result.
	 *
	 * @param message the message
	 * @return the handler result message
	 */
	public static HandlerResultMessage okResult(String message){
		return new HandlerResultMessage(Status.OK, message);
	}

	/**
	 * Warn result.
	 *
	 * @param message the message
	 * @return the handler result message
	 */
	public static HandlerResultMessage warnResult(String message){
		return new HandlerResultMessage(Status.WARN, message);
	}


	/**
	 * Session expired result.
	 *
	 * @param message the message
	 * @return the handler result message
	 */
	public static HandlerResultMessage sessionExpiredResult(String message){
		return new HandlerResultMessage(Status.SESSION_EXPIRED, message);
	}

	/**
	 * Parses the result.
	 * expected status:message (e.g. OK:Upload aborted)
	 * @param result the result
	 * @return the handler result message
	 */
	public static HandlerResultMessage parseResult(String result){
		//expected status:message
		String statusToken = null;
		String messageToken = null;

		if(result==null || result.isEmpty())
			return errorResult("result is null or empty");

		int index = result.indexOf(':');
		if (index>=0){
			statusToken = result.substring(0,index);
			if (index<result.length()){
				messageToken = result.substring(index+1);
			}
		}

		Status status = Status.UNKNOWN;
		if (statusToken!=null){
			try{
				status = Status.valueOf(statusToken);
			}catch (Exception e) {
				status = Status.UNKNOWN;
			}
		}

		String message = messageToken!=null?messageToken:"";

		return new HandlerResultMessage(status, message);
	}


	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(status);
		builder.append(SEPARATOR);
		builder.append(message);
		return builder.toString();
	}
}
