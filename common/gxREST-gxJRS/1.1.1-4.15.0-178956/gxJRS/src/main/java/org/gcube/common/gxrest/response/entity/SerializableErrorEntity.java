package org.gcube.common.gxrest.response.entity;

import org.gcube.common.gxrest.response.outbound.ErrorCode;

/**
 * An entity that can be serialized in a {@link org.gcube.common.gxrest.response.outbound.WebCodeException}.
 *
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class SerializableErrorEntity {

	protected final static char ENTITY_CHAR_SEPARATOR = '@';
	private int id = -1;
	private String message;
	private String exceptionClass;
	private String encodedTrace = "";

	public SerializableErrorEntity() {}

	/**
	 * @param errorCode the errorc code to serialize
	 */
	public SerializableErrorEntity(ErrorCode errorCode) {
		this.id = errorCode.getId();
		this.message = errorCode.getMessage();
	}

	/**
	 *
	 * @param e the exception to serialize
	 */
	public SerializableErrorEntity(Exception e) {
		this.exceptionClass = e.getClass().getCanonicalName();
		this.message = e.getMessage();
	}

	/**
	 *
	 * @param e the exception to serialize
	 * @param lines the number of lines in the stacktrace to serialize
	 */
	public SerializableErrorEntity(Exception e, int lines) {
		this.exceptionClass = e.getClass().getCanonicalName();
		this.message = e.getMessage();
		this.encodedTrace = StackTraceEncoder.encodeTrace(e.getStackTrace(), lines);
	}

	public int getId() {
		return this.id;
	}


	public String getMessage() {
		return this.message;
	}

	/**
	 * @return the full qualified name of the embedded {@link Exception}
	 */
	public String getExceptionClass() {
		return this.exceptionClass;
	}

	/**
	 * @return the encoded stacktrace
	 */
	public String getEncodedTrace() {
		return encodedTrace;
	}

	/**
	 * Checks if a stacktrace is available.
	 * @return true if a stacktrace is serialized in the entity.
	 */
	public boolean hasStackTrace() {
		return !this.encodedTrace.isEmpty();
	}

	/**
	 * Sets the message.
	 * @param message
	 */
	protected void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Sets the exception class.
	 * @param exceptionClass
	 */
	protected void setExceptionClass(String exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	/**
	 * Sets the enconded trace.
	 * @param encodedTrace
	 */
	protected void setEncodedTrace(String encodedTrace) {
		this.encodedTrace = encodedTrace;
	}

}
