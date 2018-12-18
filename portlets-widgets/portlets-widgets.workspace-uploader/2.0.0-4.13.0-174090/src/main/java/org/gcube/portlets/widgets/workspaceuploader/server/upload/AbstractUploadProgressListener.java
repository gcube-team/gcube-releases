package org.gcube.portlets.widgets.workspaceuploader.server.upload;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.ProgressListener;
import org.gcube.portlets.widgets.workspaceuploader.shared.UploadProgress;
import org.gcube.portlets.widgets.workspaceuploader.shared.event.UploadProgressChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The listener interface for receiving uploadProgress events. The class that is
 * interested in processing a uploadProgress event implements this interface,
 * and the object created with that class is registered with a component using
 * the component's <code>addUploadProgressListener<code> method. When
 * the uploadProgress event occurs, that object's appropriate
 * method is invoked.
 *
 * @see UploadProgressEvent
 */
public abstract class AbstractUploadProgressListener implements ProgressListener, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -785329339005951465L;
	private static final double COMPLETE_PERECENTAGE = 100d;
	private int percentage = -1;

	private UploadProgress uploadProgress;
	private int percentageOffset = 0;
	private double completePercentage = COMPLETE_PERECENTAGE;
	private static Logger logger = LoggerFactory.getLogger(AbstractUploadProgressListener.class);
	protected UploadCanceledException exception = null;
	protected boolean exceptionTrhown = false;
	protected String sessionId;

	/**
	 * Current.
	 *
	 * @param sessionId the session id
	 * @return the abstract upload progress listener
	 */
	public static AbstractUploadProgressListener current(String sessionId) {
		throw new RuntimeException("Implement the static method 'current' in your customized class");
	}

	protected Long bytesRead = 0L;
	protected Long contentLength = 0L;
	private String clientUploadKey;
	// protected static HttpServletRequest request;
	protected String sessionKey;
	private HttpSession session;

	/**
	 * Save itself in session or cache.
	 */
	public abstract void save();

	/**
	 * Remove itself from session or cache.
	 */
	public abstract void remove();

	/**
	 * Instantiates a new upload progress listener.
	 *
	 * @param session the session
	 * @param clientUploadKey the client upload key
	 * @param percentageOffset            the percentage offset
	 * @param completePercentage            the complete percentage
	 */
	public AbstractUploadProgressListener(HttpSession session,
			String clientUploadKey, int percentageOffset,
			double completePercentage) {
		this.sessionId = session.getId();
		this.session = session;
		this.clientUploadKey = clientUploadKey;
		this.sessionKey = sessionId + ":" + clientUploadKey;
		this.uploadProgress = new UploadProgress();
		this.percentageOffset = percentageOffset;
		this.completePercentage = completePercentage;

		logger.debug("Session key: " + sessionKey);
		save();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.fileupload.ProgressListener#update(long, long,
	 * int)
	 */
	@Override
	public void update(final long bytesRead, final long totalBytes, final int items) {

		if (exceptionTrhown) {
			logger.warn("An exception thrown is already true. Is upload cancelled?, returning");
			return;
		}

		// If other request has set an exception, it is thrown so the
		// commons-fileupload's
		// parser stops and the connection is closed.
		if (isCanceled()) {
			String eName = exception.getClass().getName().replaceAll("^.+\\.", "");
			logger.info(AbstractUploadProgressListener.class.getName() + " "
					+ sessionKey + " The upload has been canceled after "
					+ bytesRead + " bytes received, raising an exception ("
					+ eName + ") to close the socket");
			exceptionTrhown = true;
			throw exception;
		}

		this.bytesRead = bytesRead;
		this.contentLength = totalBytes;
		int percentage = percentageOffset+ (int) Math.floor((double) bytesRead / (double) totalBytes* completePercentage);

		if (this.percentage == percentage)
			return;

		this.percentage = percentage;
		UploadProgressChangeEvent event = new UploadProgressChangeEvent();
		event.setReadPercentage(percentage);
		event.setReadTime(System.currentTimeMillis());
		event.setReadBytes(bytesRead);
		event.setTotalBytes(totalBytes);
		// logger.trace("Updating percentage.. "+percentage);
		synchronized (this.uploadProgress) {
			// logger.trace("Adding event: "+event);
			this.uploadProgress.add(event);
//			this.uploadProgress.notifyAll();
		}
	}

	/**
	 * Gets the bytes read.
	 *
	 * @return the bytesRead
	 */
	public Long getBytesRead() {
		return bytesRead;
	}

	/**
	 * Gets the content length.
	 *
	 * @return the contentLength
	 */
	public Long getContentLength() {
		return contentLength;
	}

	/**
	 * Gets the upload progress.
	 *
	 * @return the uploadProgress
	 */
	public UploadProgress getUploadProgress() {
		return uploadProgress;
	}

	/**
	 * Get the exception.
	 *
	 * @return the exception
	 */
	public UploadCanceledException getException() {
		return exception;
	}

	/**
	 * Return true if the process has been canceled due to an error or by the
	 * user.
	 *
	 * @return boolean
	 */
	public boolean isCanceled() {
		return exception != null;
	}

	/**
	 * Set the exception which cancels the upload.
	 *
	 * @param e the new exception
	 */
	public void setException(UploadCanceledException e) {
		logger.info("Set exception to UploadCanceledException to cancel upload");
		exception = e;
		save();
	}

	/**
	 * Gets the percentage.
	 *
	 * @return the percentage
	 */
	public int getPercentage() {
		return percentage;
	}

	/**
	 * Gets the client upload key.
	 *
	 * @return the clientUploadKey
	 */
	public String getClientUploadKey() {
		return clientUploadKey;
	}


	/**
	 * Gets the session.
	 *
	 * @return the session
	 */
	public HttpSession getSession() {
		return session;
	}

	/**
	 * Gets the session key.
	 *
	 * @param httpSessionId
	 *            the http session id
	 * @param clientUploadKey
	 *            the client upload key
	 * @return the session key
	 */
	public static String getSessionKey(String httpSessionId, String clientUploadKey) {
		return httpSessionId + ":" + clientUploadKey;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractUploadProgressListener [percentage=");
		builder.append(percentage);
		builder.append(", percentageOffset=");
		builder.append(percentageOffset);
		builder.append(", completePercentage=");
		builder.append(completePercentage);
		builder.append(", exception=");
		builder.append(exception);
		builder.append(", exceptionTrhown=");
		builder.append(exceptionTrhown);
		builder.append(", sessionId=");
		builder.append(sessionId);
		builder.append(", bytesRead=");
		builder.append(bytesRead);
		builder.append(", contentLength=");
		builder.append(contentLength);
		builder.append(", clientUploadKey=");
		builder.append(clientUploadKey);
		builder.append(", sessionKey=");
		builder.append(sessionKey);
		builder.append("]");
		return builder.toString();
	}


}
