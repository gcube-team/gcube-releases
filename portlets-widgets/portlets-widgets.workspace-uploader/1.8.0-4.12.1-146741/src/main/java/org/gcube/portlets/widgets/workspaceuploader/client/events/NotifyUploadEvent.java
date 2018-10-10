package org.gcube.portlets.widgets.workspaceuploader.client.events;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class NotifyUploadEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 5, 2015
 */
public class NotifyUploadEvent extends GwtEvent<NotifyUploadEventHandler> {
	public static Type<NotifyUploadEventHandler> TYPE = new Type<NotifyUploadEventHandler>();

	/**
	 * The Enum UPLOAD_EVENT_TYPE.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Oct 5, 2015
	 */
	public static enum UPLOAD_EVENT_TYPE {
		UPLOAD_COMPLETED, FAILED, ABORTED, OVERWRITE_COMPLETED
	}

	private UPLOAD_EVENT_TYPE event;
	private String parentId;
	private String itemId;
	private String uploadResultMsg;
	private Throwable exception;


	/**
	 * Gets the exception.
	 *
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Sets the exception.
	 *
	 * @param exception the exception to set
	 */
	public void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * Gets the event.
	 *
	 * @return the event
	 */
	public UPLOAD_EVENT_TYPE getEvent() {
		return event;
	}

	/**
	 * Sets the event.
	 *
	 * @param event the event to set
	 */
	public void setEvent(UPLOAD_EVENT_TYPE event) {
		this.event = event;
	}

	/**
	 * Instantiates a new notify upload event.
	 *
	 * @param event the event
	 * @param parentId the parent id
	 * @param itemId the item id
	 */
	public NotifyUploadEvent(UPLOAD_EVENT_TYPE event, String parentId, String itemId) {
		this.parentId = parentId;
		this.itemId = itemId;
		this.event = event;
	}

	/**
	 * Instantiates a new notify upload event.
	 *
	 * @param event the event
	 * @param parentId the parent id
	 * @param itemId the item id
	 * @param e the e
	 */
	public NotifyUploadEvent(UPLOAD_EVENT_TYPE event, String parentId, String itemId, Throwable e) {
		this.parentId = parentId;
		this.itemId = itemId;
		this.event = event;
		this.exception = e;
	}


	/**
	 * Instantiates a new notify upload event.
	 *
	 * @param event the event
	 * @param parentId the parent id
	 * @param itemId the item id
	 * @param uploadResultMsg the upload result msg
	 * @param e the e
	 */
	public NotifyUploadEvent(UPLOAD_EVENT_TYPE event, String parentId, String itemId, String uploadResultMsg, Throwable e) {
		this.parentId = parentId;
		this.itemId = itemId;
		this.event = event;
		this.uploadResultMsg = uploadResultMsg;
		this.exception = e;
	}

	/**
	 * Gets the parent id.
	 *
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * Gets the item id.
	 *
	 * @return the itemId
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * Sets the parent id.
	 *
	 * @param parentId the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * Sets the item id.
	 *
	 * @param itemId the itemId to set
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}


	/**
	 * @return the uploadResultMsg
	 */
	public String getUploadResultMsg() {

		return uploadResultMsg;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<NotifyUploadEventHandler> getAssociatedType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared
	 * .EventHandler)
	 */
	@Override
	protected void dispatch(NotifyUploadEventHandler handler) {
		handler.onNotifyUpload(this);
	}
}
