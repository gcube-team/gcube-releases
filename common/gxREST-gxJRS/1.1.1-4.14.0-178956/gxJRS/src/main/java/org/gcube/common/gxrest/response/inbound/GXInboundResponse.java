package org.gcube.common.gxrest.response.inbound;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.gxhttp.util.ContentUtils;
import org.gcube.common.gxrest.response.entity.EntityTag;
import org.gcube.common.gxrest.response.entity.SerializableErrorEntity;
import org.gcube.common.gxrest.response.outbound.ErrorCode;
import org.gcube.common.gxrest.response.outbound.GXOutboundErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The response returned from the web application.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class GXInboundResponse {

	private SerializableErrorEntity entity;
	private final int responseCode;
	private String contentType = "";
	private String message = "";
	private String body = "";
	private byte[] streamedContent;
	private Map<String, List<String>> headerFields;
	private static final Logger logger = LoggerFactory.getLogger(GXInboundResponse.class);
	private boolean hasGXError = false;
	private Response source;
	private HttpURLConnection connection;
	// the content cannot be read more than one time from the response or input
	// stream
	private boolean contentAlreadyConsumed = false;
	
	private boolean fromConnection = false;
	private boolean fromResponse = false;

	/**
	 * Builds a new inbound response.
	 * 
	 * @param source
	 *            the original response
	 */
	public GXInboundResponse(Response source) {
		this.fromResponse = true;
		this.source = source;
		this.responseCode = source.getStatusInfo().getStatusCode();
		this.message = source.getStatusInfo().getReasonPhrase();
		this.headerFields = source.getStringHeaders();
		if (Objects.nonNull(source.getMediaType()))
			this.contentType = source.getMediaType().getType();
		try {
			if (Objects.nonNull(source.getEntityTag()) && source.getEntityTag().getValue().equals(EntityTag.gxError)) {
				this.entity = source.readEntity(SerializableErrorEntity.class);
				this.hasGXError = true;
				this.contentAlreadyConsumed = true;
			}
		} catch (ProcessingException | IllegalStateException ie) {
			// if it fails, it's likely message response
			// this.message = (String) source.getEntity();
		}
	}

	/**
	 * @param connection
	 *            the connection from which to parse the information
	 * @throws IOException
	 */
	public GXInboundResponse(HttpURLConnection connection) throws IOException {
		this.fromConnection = true;
		this.connection = connection;
		this.responseCode = connection.getResponseCode();
		this.message = connection.getResponseMessage();
		this.headerFields = connection.getHeaderFields();
		this.contentType = connection.getContentType();
		// header fields are usually wrapped around double quotes
		String eTag = connection.getHeaderField("ETag");
		if (Objects.nonNull(eTag) && eTag.replaceAll("^\"|\"$", "").equals(EntityTag.gxError)) {
			logger.debug("GXErrorResponse detected.");
			this.hasGXError = true;
			try {
				this.streamedContent = ContentUtils.toByteArray(connection.getErrorStream());
				this.contentAlreadyConsumed = true;
				this.body = ContentUtils.toString(streamedContent);
				this.entity = JsonUtils.fromJson(this.body, SerializableErrorEntity.class);
				logger.trace("Response's content: " + this.body);
			} catch (Exception ioe) {
				logger.warn("No data are available in the response.");
			}
		} else {
			try {
				// this.streamedContent =
				// ContentUtils.toByteArray(connection.getInputStream());
				if (this.contentType.equals(MediaType.TEXT_PLAIN)
						|| this.contentType.equals(MediaType.APPLICATION_JSON)) {
					this.body = ContentUtils.toString(ContentUtils.toByteArray(connection.getInputStream()));
					logger.trace("Response's content: " + this.body);
					this.contentAlreadyConsumed = true;
				}
			} catch (Exception ioe) {
				logger.warn("No data are available in the response.", ioe);
			}
		}
	}

	/**
	 * Builds a new inbound response.
	 * 
	 * @param source
	 *            the original response
	 * @param expectedMediaTypes
	 *            the expected media type(s) in the response
	 */
	public GXInboundResponse(Response response, MediaType[] expectedMediaTypes) {
		this(response);
		if (Objects.isNull(expectedMediaTypes) || expectedMediaTypes.length == 0)
			throw new IllegalArgumentException("No expected type was set)");

		// validate the media type
		boolean compatible = false;
		for (MediaType media : expectedMediaTypes) {
			if (Objects.nonNull(response.getMediaType()) && response.getMediaType().isCompatible(media))
				compatible = true;
		}
		if (!compatible)
			throw new IllegalArgumentException("Received MediaType is not compatible with the expected type(s)");
	}

	/**
	 * Checks if there is an {@link Exception} in the entity.
	 * 
	 * @return true if the entity holds an exception, false otherwise
	 */
	public boolean hasException() {
		return Objects.nonNull(this.entity) && Objects.nonNull(this.entity.getExceptionClass());
	}

	/**
	 * Checks if the response is in the range 4xx - 5xx 
	 * .
	 * 
	 * @return true if it is an error response.
	 */
	public boolean isErrorResponse() {
		return this.getHTTPCode() >= 400 && this.getHTTPCode() < 600;
	}
	
	/**
	 * Checks if the response is in the range 2xx 
	 * .
	 * 
	 * @return true if it is a success response.
	 */
	public boolean isSuccessResponse() {
		return this.getHTTPCode() >= 200 && this.getHTTPCode() < 300;
	}
	
	/**
	 * Checks if the response was generated as a {@link GXOutboundErrorResponse}
	 * .
	 * 
	 * @return true if it is an error response generated with GXRest.
	 */
	public boolean hasGXError() {
		return this.hasGXError;
	}

	/**
	 * Gets the {@link Exception} inside the entity.
	 * 
	 * @return the exception or null
	 * @throws ClassNotFoundException
	 *             if the exception's class is not available on the classpath
	 */
	public <E extends Exception> E getException() throws ClassNotFoundException {
		if (Objects.nonNull(this.entity)) {
			E e = ExceptionDeserializer.deserialize(this.entity.getExceptionClass(), this.entity.getMessage());
			if (Objects.nonNull(e)) {
				if (this.entity.hasStackTrace())
					ExceptionDeserializer.addStackTrace(e, this.entity.getEncodedTrace());
				else
					e.setStackTrace(new StackTraceElement[] {});
				return e;
			} else
				throw new ClassNotFoundException(
						"Failed to deserialize: " + this.entity.getExceptionClass() + ". Not on the classpath?");
		} else
			return null;
	}

	/**
	 * Checks if there is an {@link ErrorCode} in the entity.
	 * 
	 * @return true if the entity holds an errorcode, false otherwise
	 */
	public boolean hasErrorCode() {
		if (Objects.nonNull(this.entity))
			return this.entity.getId() != -1;
		else
			return false;
	}

	/**
	 * Gets the {@link ErrorCode} inside the entity.
	 * 
	 * @return the error code or null
	 */
	public ErrorCode getErrorCode() {
		if (Objects.nonNull(this.entity))
			return ErrorCodeDeserializer.deserialize(this.entity.getId(), this.entity.getMessage());
		else
			return null;
	};

	/**
	 * Gets the message in the response
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Gets the streamed content as a string, if possible.
	 * 
	 * @return the content
	 * @throws IOException
	 *             if unable to read the content
	 */
	public String getStreamedContentAsString() throws IOException {
		if (this.body.isEmpty()) {
			this.body = ContentUtils.toString(ContentUtils.toByteArray(getInputStream()));
		}
		return this.body;
	}

	public InputStream getInputStream() throws IOException {
		if(!this.contentAlreadyConsumed) {
			if (this.fromConnection) {
				contentAlreadyConsumed = true;
				return connection.getInputStream();
			} else if (this.fromResponse) {
				contentAlreadyConsumed = true;
				return (InputStream) source.getEntity();
			}
			// This code should be never reached
			return null;
		}
		throw new IOException("Content Already Consumed");
	}
	
	/**
	 * Returns the content of the response as byte array.
	 * 
	 * @return the streamedContent
	 * @throws IOException
	 *             if unable to read the content
	 */
	public byte[] getStreamedContent() throws IOException {
		if (!this.body.isEmpty()) {
			this.streamedContent = this.body.getBytes();
		} else {
			this.streamedContent = ContentUtils.toByteArray(getInputStream());
		}
		return this.streamedContent;
	}

	/**
	 * Tries to convert the content from its Json serialization, if possible.
	 * 
	 * @param <T>
	 *            the type of the desired object
	 * @return an object of type T from the content
	 * @throws Exception
	 *             if the deserialization fails
	 */
	public <T> T tryConvertStreamedContentFromJson(Class<T> raw) throws Exception {
		return JsonUtils.fromJson(this.getStreamedContentAsString(), raw);
	}

	/**
	 * Gets the status code from the HTTP response message.
	 * 
	 * @return the HTTP code
	 */
	public int getHTTPCode() {
		return this.responseCode;
	}

	/**
	 * Checks if the response has a CREATED (201) HTTP status.
	 * 
	 * @return true if CREATED, false otherwise
	 */
	public boolean hasCREATEDCode() {
		return (this.getHTTPCode() == Status.CREATED.getStatusCode());
	}

	/**
	 * Checks if the response has a OK (200) HTTP status.
	 * 
	 * @return true if OK, false otherwise
	 */
	public boolean hasOKCode() {
		return (this.getHTTPCode() == Status.OK.getStatusCode());
	}

	/**
	 * Checks if the response has a NOT_ACCEPTABLE (406) HTTP status.
	 * 
	 * @return true if NOT_ACCEPTABLE, false otherwise
	 */
	public boolean hasNOT_ACCEPTABLECode() {
		return (this.getHTTPCode() == Status.NOT_ACCEPTABLE.getStatusCode());
	}

	/**
	 * Checks if the response has a BAD_REQUEST (400) HTTP status.
	 * 
	 * @return true if BAD_REQUEST, false otherwise
	 */
	public boolean hasBAD_REQUESTCode() {
		return (this.getHTTPCode() == Status.BAD_REQUEST.getStatusCode());
	}

	/**
	 * Returns an unmodifiable Map of the header fields. The Map keys are
	 * Strings that represent the response-header field names. Each Map value is
	 * an unmodifiable List of Strings that represents the corresponding field
	 * values.
	 *
	 * @return a Map of header fields
	 */
	public Map<String, List<String>> getHeaderFields() {
		return this.headerFields;
	}

	/**
	 * @return the source response, if available
	 * @throws UnsupportedOperationException
	 *             if not available
	 */
	public Response getSource() throws UnsupportedOperationException {
		if (Objects.isNull(this.source))
			new UnsupportedOperationException();
		return this.source;
	}

}
