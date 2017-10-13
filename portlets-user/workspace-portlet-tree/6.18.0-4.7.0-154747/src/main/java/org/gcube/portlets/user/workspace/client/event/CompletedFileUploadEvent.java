package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WS_UPLOAD_TYPE;
import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class CompletedFileUploadEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Oct 6, 2015
 */
public class CompletedFileUploadEvent extends
		GwtEvent<CompletedFileUploadEventHandler> implements GuiEventInterface {
	public static Type<CompletedFileUploadEventHandler> TYPE = new Type<CompletedFileUploadEventHandler>();

	private String itemIdentifier;
	private String parentId;

	/** The upload type. */
	private WS_UPLOAD_TYPE uploadType;
	private boolean isOverwrite;

	/**
	 * Instantiates a new completed file upload event.
	 *
	 * @param parentId
	 *            the parent id
	 * @param itemIdentifier
	 *            the item identifier
	 * @param uploadType
	 *            the upload type
	 * @param isOverwrite
	 */
	public CompletedFileUploadEvent(String parentId, String itemIdentifier,
			WS_UPLOAD_TYPE uploadType, boolean isOverwrite) {
		this.parentId = parentId;
		this.itemIdentifier = itemIdentifier;
		this.uploadType = uploadType;
		this.isOverwrite = isOverwrite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<CompletedFileUploadEventHandler> getAssociatedType() {
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
	protected void dispatch(CompletedFileUploadEventHandler handler) {
		handler.onCompletedFileUploadEvent(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface
	 * #getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.COMPLETED_FILE_UPLOAD_EVENT;
	}

	/**
	 * Gets the item identifier.
	 *
	 * @return the item identifier
	 */
	public String getItemIdentifier() {
		return itemIdentifier;
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
	 * Sets the item identifier.
	 *
	 * @param itemIdentifier
	 *            the itemIdentifier to set
	 */
	public void setItemIdentifier(String itemIdentifier) {
		this.itemIdentifier = itemIdentifier;
	}

	/**
	 * Sets the parent id.
	 *
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the uploadType
	 */
	public WS_UPLOAD_TYPE getUploadType() {
		return uploadType;
	}

	/**
	 * @return the isOverwrite
	 */
	public boolean isOverwrite() {
		return isOverwrite;
	}

	/**
	 * @param isOverwrite
	 *            the isOverwrite to set
	 */
	public void setOverwrite(boolean isOverwrite) {
		this.isOverwrite = isOverwrite;
	}

}
